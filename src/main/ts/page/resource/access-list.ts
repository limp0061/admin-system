import {showToast} from "../../common/toast.js";
import {PaginationUtils} from "../../common/pagination.js";
import {btnDropDown, toggleSelect} from "../../common/select.js";
import {checkRow, checkToggle} from "../../common/checkbox.js";
import {initGlobalEvents} from "../../common/event.js";

import {sendRequest} from "../../common/request.js";
import {ErrorResponse, ModalConfig} from "../../common/type";
import {closeModal} from "../../common/modal.js";
import {validator} from "../../common/validation.js";
import {AccessSaveRequest} from "./resource-types";

initGlobalEvents();

const defaultHandler = async (url: string) => {
    const html = await sendRequest(url, {method: 'GET', headers: {'X-Requested-With': 'XMLHttpRequest'}}, 'TEXT');
    document.getElementById('list-wrapper').innerHTML = html;

    window.history.pushState({}, '', url);
};

PaginationUtils.setUpdateHandler(defaultHandler);

document.addEventListener("dblclick", (e) => {
    const target = e.target as HTMLElement;

    const td = target.closest('td');
    if (target.classList.contains('check-box') || (td && td.querySelector('.check-box'))) {
        return;
    }

    const row = target.closest<HTMLElement>("[data-action]");
    if (!row || row.dataset.action !== "clickRow") return;

    const checkbox = row.querySelector<HTMLInputElement>(".check-box");
    if (checkbox) checkbox.checked = true;

    const action = row.dataset.action;
    switch (action) {
        case "clickRow": {
            openResourceModal('EDIT', row);
        }
    }
})

document.addEventListener("click", (e) => {
    const target = e.target as HTMLElement;

    if (target.classList.contains('check-box')) {
        return;
    }

    const btn = target.closest<HTMLElement>("[data-action]");
    if (!btn) {
        return;
    }

    const action = btn.dataset.action;
    switch (action) {
        case "toggleSelect":
            toggleSelect(btn, e);
            break
        case "checkToggle":
            checkToggle(btn);
            break;
        case "clickRow":
            checkRow(btn);
            break;
        case "btnDropDown":
            btnDropDown(btn, e);
            break;
        case "sort":
            handleSort(btn);
            break;
        case "clearInput":
            clearInput(e);
            break;
        case "methodFilter":
            methodFilter(btn);
            break;
        case "openResourceModal":
            openResourceModal('ADD', btn);
            break;
        case "openDeleteModal":
            openDeleteModal();
            break;
        case "saveResource":
            saveResource(false);
            break;
        case "updateResource":
            saveResource(true);
            break;
        case "deleteResource":
            deleteResource(btn);
            break;


    }
});

const handleSort = async (btn: HTMLElement) => {
    const sort = btn.dataset.sort;
    const currentOrder = btn.dataset.order;

    const params = new URLSearchParams(window.location.search);
    const order = currentOrder == 'DESC' ? 'ASC' : 'DESC';

    params.set("sort", `${sort},${order}`);
    params.set("page", '0');

    const targetUrl = `${window.location.pathname}?${params.toString()}`;
    (PaginationUtils as any).onUpdate(targetUrl);
}

const clearInput = async (e: Event) => {
    const target = e.target as HTMLElement;

    const container = target.closest('.search-container');
    const input = container?.querySelector('.search-input') as HTMLInputElement;
    if (!input) return;

    input.value = '';
    const params = new URLSearchParams(window.location.search);
    params.delete('keyword');
    params.set('page', '0');

    const targetUrl = `${window.location.pathname}?${params.toString()}`;
    (PaginationUtils as any).onUpdate(targetUrl);
};

const methodFilter = (li: HTMLElement) => {
    const val = li.dataset.value;
    const text = li.textContent?.trim() || "";

    const commonUl = document.getElementById('common-select-ul') as HTMLElement;
    const ownerId = commonUl.dataset.owner;

    if (!ownerId) return;

    const displayBtn = document.getElementById(ownerId) as HTMLButtonElement;
    if (!displayBtn) return;

    const parentField = displayBtn.parentElement;
    const input = parentField?.querySelector('input[type="hidden"]') as HTMLInputElement;

    if (input) input.value = val || "";
    if (displayBtn) {
        displayBtn.innerText = text;
    }
    document.querySelectorAll('.select-ul').forEach(ul => ul.classList.add('hidden'));

    searchFilter();
}


const searchFilter = (): void => {
    const method = (document.getElementById('method') as HTMLInputElement)?.value || 'ALL';
    const keywordInput = document.querySelector('.search-container input[name="keyword"]') as HTMLInputElement;
    const keyword = keywordInput?.value || '';

    PaginationUtils.applyFilter({
        method: method,
        keyword: keyword
    });
}

const openResourceModal = async (mode: 'ADD' | 'EDIT', btn: HTMLElement | null): Promise<void> => {

    const id = btn.closest<HTMLElement>("[data-id]")?.dataset.id;
    const requestConfig: Record<string, ModalConfig> = {
        ADD: {url: "/resources/access/modal/add"},
        EDIT: {url: "/resources/access/modal/edit", param: "id", value: id},
    };

    const request = requestConfig[mode];
    let url = `${request.url}`;
    if (mode === 'EDIT') {
        if (request.param && request.value) {
            url += `?${request.param}=${encodeURIComponent(String(request.value))}`;
        }
    }
    try {
        const html = await sendRequest(url, {method: 'GET'}, 'TEXT');

        const root = document.getElementById("modal-root");
        if (root) {
            root.innerHTML = html;
        }
    } catch (error) {
        const err = error as ErrorResponse;
        showToast(err.message || "오류가 발생했습니다.", "error");
    }
};


const saveResource = async (isUpdate: boolean): Promise<void> => {
    const form = document.getElementById('resource-form') as HTMLFormElement;

    if (!form) return;

    validator.clear(form);

    const roleElements = document.querySelectorAll('#badge-container .badge-value');
    const roles = Array.from(roleElements)
        .map((el) => {
            const val = (el as HTMLInputElement).value?.trim();
            return val ? Number(val) : NaN;
        })
        .filter(val => !isNaN(val));

    const formData = new FormData(form);
    const resourceData: AccessSaveRequest = {
        id: formData.get("id") ? Number(formData.get("id")) : undefined,
        name: (formData.get("name") as string) || null,
        urlPattern: formData.get("urlPattern") as string,
        method: formData.get("method") as string,
        roleIds: roles,
        description: (formData.get("description") as string) || null,
    };

    const requiredFields: Record<string, { label: string, type?: 'input' | 'select' }> = {
        urlPattern: {label: "url"},
    };

    const error = validator.validateRequired(resourceData, requiredFields);
    if (error.length > 0) {
        validator.displayErrors(error);
        showToast("입력 형식이 올바르지 않습니다.", "error");
        return;
    }

    try {
        const url = isUpdate ? `/api/v1/resources/access/${resourceData.id}/update` : '/api/v1/resources/access';
        const result = await sendRequest(url, {method: 'POST', body: resourceData});

        closeModal();
        showToast(result.message, "success");
        setTimeout(() => location.reload(), 1200);
    } catch (error) {
        const err = error as ErrorResponse;
        showToast(err.message || "저장 중 오류가 발생했습니다.", "error");
    }
};

const openDeleteModal = async (): Promise<void> => {
    const checkedBoxes = document.querySelectorAll<HTMLInputElement>(".check-box:checked");

    let ids = Array.from(checkedBoxes).map(check => Number(check.value));
    if (ids.length <= 0) {
        showToast("리소스를 선택해주세요.", "error")
        return;
    }

    const params = new URLSearchParams();
    ids.forEach(id => params.append("ids", String(id)));
    const url = `/resources/access/modal/delete?${params.toString()}`;
    try {
        const html = await sendRequest(url, {method: 'GET'}, 'TEXT');
        const root = document.getElementById("modal-root");

        if (root) {
            root.innerHTML = html;
        }
    } catch (error) {
        const err = error as ErrorResponse;
        showToast(err.message || "오류가 발생했습니다.", "error");
    }
}

const deleteResource = async (btn: HTMLElement): Promise<void> => {
    const data = btn.dataset.ids;
    const ids = data.split(",").map(id => `ids=${id}`).join("&");
    try {
        const result = await sendRequest(`/api/v1/resources/access?${ids}`, {
            method: "DELETE",
        })
        closeModal();
        showToast(result.message, "success");
        setTimeout(() => location.reload(), 1000);

    } catch (error) {
        const err = error as ErrorResponse;
        showToast(err.message || "오류가 발생했습니다.", "error");
    }
}