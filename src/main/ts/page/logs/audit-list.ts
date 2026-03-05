import {PaginationUtils} from "../../common/pagination.js";
import {initGlobalEvents} from "../../common/event.js";
import {sendRequest} from "../../common/request.js";
import {initDateTimePicker} from "../../common/datepicker.js";
import {btnDropDown} from "../../common/select.js";
import {AuditRequest} from "./log-types";
import {ErrorResponse} from "../../common/type";
import {showToast} from "../../common/toast.js";

initGlobalEvents()

const defaultHandler = async (url: string) => {
    try {
        const html = await sendRequest(url, {method: 'GET', headers: {'X-Requested-With': 'XMLHttpRequest'}}, 'TEXT');
        document.getElementById('list-wrapper').innerHTML = html;

        window.history.pushState({}, '', url);

        initSearchPickers();
    } catch (error) {
        const err = error as ErrorResponse;
        showToast(err.message || "오류가 발생했습니다.", "error");
    }
};

PaginationUtils.setUpdateHandler(defaultHandler);

document.addEventListener("dblclick", (e) => {
    const target = e.target as HTMLElement;

    const row = target.closest<HTMLElement>("[data-action]");
    if (!row) return;

    const action = row.dataset.action;
    switch (action) {
        case "clickRow": {
            openDetailModal(row);
            break;
        }
    }
})

document.addEventListener("click", (e) => {
    const target = e.target as HTMLElement;

    const btn = target.closest<HTMLElement>("[data-action]");
    if (!btn) {
        return;
    }

    const action = btn.dataset.action;
    switch (action) {
        case "btnDropDown":
            btnDropDown(btn, e);
            break;
        case "selectData":
            selectData(btn);
            break;
        case "clear":
            clear();
            break;
        case "search":
            search();
            break;
    }
});

const initSearchPickers = () => {
    const startInput = document.getElementById("startAt") as HTMLInputElement;
    const endInput = document.getElementById("endAt") as HTMLInputElement;
    if (!startInput || !endInput) return;

    initDateTimePicker("#startAt", {
        enableTime: false,
        dateFormat: 'Y-m-d',
        altFormat: "Y-m-d",
        altInput: false,
        minDate: null,
    });

    initDateTimePicker("#endAt", {
        enableTime: false,
        dateFormat: 'Y-m-d',
        altFormat: "Y-m-d",
        altInput: false,
        minDate: null,
    });
};
document.addEventListener("DOMContentLoaded", initSearchPickers);

const selectData = (btn: HTMLElement) => {
    const container = btn.closest('.relative');
    if (!container) return;

    const input = container.querySelector('input[type="hidden"]') as HTMLInputElement;
    const mode = btn.dataset.mode;
    if (input) {
        input.value = mode;
    }

    const btnDropDown = container.querySelector('button[data-action="btnDropDown"] span');
    if (btnDropDown) {
        btnDropDown.textContent = (btn.textContent || '').trim();
    }

    const dropDownMenu = container.querySelector('.btn-dropDown');
    if (dropDownMenu) {
        dropDownMenu.classList.add('hidden');
    }
}

const clear = () => {
    defaultHandler(window.location.pathname);
}


const search = async (): Promise<void> => {
    const auditRequest: AuditRequest = {
        action: (document.getElementById('action') as HTMLInputElement).value || null,
        targetEntity: (document.getElementById('targetEntity') as HTMLInputElement).value || null,
        startAt: (document.getElementById('startAt') as HTMLInputElement).value || null,
        endAt: (document.getElementById('endAt') as HTMLInputElement).value || null,
    }
    PaginationUtils.applyFilter(auditRequest as Record<string, string | null>);
}

const openDetailModal = async (btn: HTMLElement) => {
    const id = btn.dataset.id;

    try {
        const html = await sendRequest(`/logs/audit/modal/detail?id=${id}`, {method: 'GET'}, 'TEXT');

        const root = document.getElementById("modal-root");
        if (root) {
            root.innerHTML = html;
        }
    } catch (error) {
        const err = error as ErrorResponse;
        showToast(err.message || "오류가 발생했습니다.", "error");
    }
}
