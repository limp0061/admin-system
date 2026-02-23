import {showToast} from "../../common/toast.js";
import {PaginationUtils} from "../../common/pagination.js";
import {btnDropDown, toggleSelect} from "../../common/select.js";
import {checkRow, checkToggle} from "../../common/checkbox.js";
import {initGlobalEvents} from "../../common/event.js";

import {sendRequest} from "../../common/request.js";
import {ErrorResponse, ModalConfig} from "../../common/type";
import {UserSearchResponse} from "../user/user-types";
import {AdminRoleRequest} from "./admin-type";
import {closeModal} from "../../common/modal.js";

initGlobalEvents();

PaginationUtils.setUpdateHandler(async (url) => {

    const html = await sendRequest(url, {method: 'GET', headers: {'X-Requested-With': 'XMLHttpRequest'}}, 'TEXT');
    document.getElementById('list-wrapper').innerHTML = html;

    window.history.pushState({}, '', url);
});

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
            openAdminModal('EDIT', row);
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
        case "openAdminModal":
            openAdminModal('ADD', btn);
            break;
        case "toggleSelect":
            toggleSelect(btn, e);
            break
        case "saveAdmin":
            saveAdmin();
            break;
        case "updateAdmin":
            updateAdmin();
            break;
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
        case "selectAutoComplete":
            selectAutoComplete(btn)
            break;
        case "addIps":
            addIps();
            break;
        case "openDeleteModal":
            openDeleteModal();
            break;
        case "deleteAdmin":
            deleteAdmin(btn)
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

document.addEventListener('keydown', (e: KeyboardEvent) => {
    const target = e.target as HTMLElement;

    const isModalInput = target.tagName === 'INPUT' && target.closest('#admin-form');
    if (isModalInput) {
        if (e.key === 'Escape') {
            e.preventDefault();
            const commonUl = document.getElementById('common-select-ul');
            if (commonUl) commonUl.classList.add('hidden');
        }

        if (e.key === 'Enter') {
            e.preventDefault();
            const firstItem = document.querySelector('#common-select-ul .select-li') as HTMLElement;
            if (firstItem && !firstItem.textContent?.includes('결과가 없습니다')) {
                firstItem.click();
            }
        }
    }
});

let timer: ReturnType<typeof setTimeout>;
document.addEventListener('input', (e: Event) => {
    const input = e.target as HTMLInputElement;

    if (input && input.classList.contains('search-input')) {
        const container = input.closest('.search-container');
        const clearBtn = container?.querySelector('.clear-btn');
        if (input.value.length > 0) {
            clearBtn?.classList.remove('hidden');
        } else {
            clearBtn?.classList.add('hidden');
        }
        clearTimeout(timer);
        timer = setTimeout(() => {
            const keyword = input.value.trim();
            if (keyword.length < 2) return;
            searchInput(input, keyword);
        }, 300)
    }
})

const searchInput = async (input: HTMLElement, keyword: string): Promise<void> => {
    const users = await sendRequest(`/api/v1/users/search?keyword=${encodeURIComponent(keyword)}`, {method: 'GET'}) as UserSearchResponse[];
    const commonUl = document.getElementById('common-select-ul') as HTMLElement;

    let html;
    if (!users || users.length === 0) {
        html = `<li class="select-li p-3 hover:bg-slate-50 cursor-pointer border-b last:border-0">
                    검색된 결과가 없습니다.
                </li>`
    } else {
        html = users.map((user: UserSearchResponse) => `
        <li class="select-li p-3 hover:bg-slate-50 cursor-pointer border-b last:border-0" 
            data-action="selectAutoComplete"
            data-id="${user.id}"
            data-name="${user.name}"
            data-dept="${user.deptName}"
            data-email="${user.emailId}">
            <div class="text-sm font-bold text-slate-800">${user.name}</div>
            <div class="text-xs text-gray-500">${user.deptName} | ${user.emailId}</div>
        </li>
    `).join('');
    }

    commonUl.innerHTML = html;
    commonUl.dataset.owner = input.id;

    // 2. 위치 잡기 (검색창 바로 아래)
    const rect = input.getBoundingClientRect();
    commonUl.style.top = `${rect.bottom + window.scrollY}px`; // 스크롤 대비
    commonUl.style.left = `${rect.left + window.scrollX}px`;
    commonUl.style.width = `${rect.width}px`;

    commonUl.classList.remove('hidden');
}

const selectAutoComplete = (li: HTMLElement) => {
    const selectedContainer = document.getElementById('selected-user-card');
    const id = li.dataset.id;
    const email = li.dataset.email;
    const name = li.dataset.name;
    const dept = li.dataset.dept;
    const html = `
            <input type="hidden" name="id" value="${id}">
            <div class="flex items-baseline gap-2">
                 <span class="text-base font-bold text-slate-900">${name}</span>
                 <span class="text-xs font-medium text-slate-500">${dept}</span>
            </div>
            <span class="text-sm text-gray-500">${email}</span>
                <div class="absolute top-3 right-3 text-slate-800">
                    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" class="size-5">
                        <path fill-rule="evenodd" d="M2.25 12c0-5.385 4.365-9.75 9.75-9.75s9.75 4.365 9.75 9.75-4.365 9.75-9.75 9.75S2.25 17.385 2.25 12Zm13.36-1.814a.75.75 0 1 0-1.22-.872l-3.236 4.53L9.53 12.22a.75.75 0 0 0-1.06 1.06l2.25 2.25a.75.75 0 0 0 1.14-.094l3.75-5.25Z"
                        clip-rule="evenodd"/>
                    </svg>
                </div>
            `;
    selectedContainer.innerHTML = html;
    const secondContainer = document.getElementById('second-step');
    secondContainer.classList.remove('hidden');
    secondContainer.classList.add('flex');
}

const clearInput = async (e: Event) => {
    e.stopPropagation();

    const target = e.target as HTMLElement;

    const container = target.closest('.search-container');
    const input = container?.querySelector('.search-input') as HTMLInputElement;
    if (!input) return;

    input.value = '';
};

const openAdminModal = async (mode: 'ADD' | 'EDIT', btn: HTMLElement | null): Promise<void> => {

    const id = btn.closest<HTMLElement>("[data-id]")?.dataset.id;
    const requestConfig: Record<string, ModalConfig> = {
        ADD: {url: "/admins/modal/add"},
        EDIT: {url: "/admins/modal/edit", param: "id", value: id},
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

const addIps = () => {
    const ipInput = document.getElementById('ip-input') as HTMLInputElement;
    const ipListContainer = document.getElementById('ip-list');

    const ip = ipInput.value.trim();

    if (!ip) {
        showToast("IP를 입력해주세요.", "error");
        return;
    }

    const currentIps = Array.from(ipListContainer.querySelectorAll('.ip')).map(el => el.textContent);
    if (currentIps.includes(ip)) {
        showToast("이미 등록된 IP입니다.", "error");
        return;
    }
    const html = `
         <div class="flex items-center gap-1.5 w-fit bg-white text-slate-700 px-2.5 py-1.5 rounded-md border border-gray-200 shadow-sm">
            <span class="ip text-xs font-semibold">${ip}</span>
            <button type="button" class="text-gray-400 hover:text-red-500 transition-colors"
                    onclick="this.parentElement.remove()">
                <svg xmlns="http://www.w3.org/2000/svg" class="size-3.5" fill="none"
                     viewBox="0 0 24 24"
                     stroke="currentColor" stroke-width="2.5">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12"/>
                </svg>
            </button>
        </div>
    `
    ipListContainer.insertAdjacentHTML('beforeend', html);
    ipInput.value = '';
    ipInput.focus();
}

const saveAdmin = async (): Promise<void> => {
    const form = document.getElementById('admin-form') as HTMLFormElement;

    const userId = form.querySelector<HTMLInputElement>('input[name="id"]')?.value;

    const roleId = (document.getElementById('role') as HTMLInputElement)?.value;

    const ipElements = document.querySelectorAll('#ip-list .ip');
    const ips = Array.from(ipElements).map(el => el.textContent?.trim() || "");

    if (!userId) return showToast("관리자로 등록할 유저를 선택해주세요.", "error");
    if (!roleId) return showToast("권한을 선택해주세요.", "error");

    const requestData: AdminRoleRequest = {
        id: Number(userId),
        roleId: Number(roleId),
        ips: ips
    };

    try {
        const result = await sendRequest('/api/v1/admins', {
            method: 'POST',
            body: requestData
        });

        closeModal();
        showToast(result.message, "success");
        setTimeout(() => location.reload(), 1200);
    } catch (error) {
        const err = error as ErrorResponse;
        showToast(err.message || "저장 중 오류가 발생했습니다.", "error");
    }
};

const updateAdmin = async (): Promise<void> => {
    const form = document.getElementById('admin-form') as HTMLFormElement;

    const userId = form.querySelector<HTMLInputElement>('input[name="id"]')?.value;

    const roleId = (document.getElementById('role') as HTMLInputElement)?.value;

    const ipElements = document.querySelectorAll('#ip-list .ip');
    const ips = Array.from(ipElements).map(el => el.textContent?.trim() || "");

    if (!userId) return showToast("관리자로 등록할 유저를 선택해주세요.", "error");
    if (!roleId) return showToast("권한을 선택해주세요.", "error");

    const requestData: AdminRoleRequest = {
        id: Number(userId),
        roleId: Number(roleId),
        ips: ips
    };

    try {
        const result = await sendRequest(`/api/v1/admins/${userId}/update`, {
            method: 'POST',
            body: requestData
        });

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
        showToast("관리자를 선택해주세요.", "error")
        return;
    }

    const params = new URLSearchParams();
    ids.forEach(id => params.append("ids", String(id)));
    const url = `/admins/modal/delete?${params.toString()}`;
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

const deleteAdmin = async (btn: HTMLElement): Promise<void> => {
    const data = btn.dataset.ids;
    const ids = data.split(",").map(id => `ids=${id}`).join("&");
    try {
        const result = await sendRequest(`/api/v1/admins?${ids}`, {
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