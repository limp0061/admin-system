import { PaginationUtils } from "../../common/pagination.js";
import { showToast } from "../../common/toast.js";
import { closeModal } from "../../common/modal.js";
import { initGlobalEvents } from "../../common/event.js";
import { checkRow, checkToggle } from "../../common/checkbox.js";
import { btnDropDown, closeDropdown, toggleSelect } from "../../common/select.js";
import { sendRequest } from "../../common/request.js";
initGlobalEvents();
PaginationUtils.setUpdateHandler(async (url) => {
    const html = await sendRequest(url, { method: 'GET', headers: { 'X-Requested-With': 'XMLHttpRequest' } }, 'TEXT');
    document.getElementById('list-wrapper').innerHTML = html;
    window.history.pushState({}, '', url);
});
document.addEventListener("click", (e) => {
    const target = e.target;
    if (target.classList.contains('check-box')) {
        return;
    }
    const btn = target.closest("[data-action]");
    if (!btn) {
        return;
    }
    const action = btn.dataset.action;
    switch (action) {
        case "checkToggle":
            checkToggle(btn);
            break;
        case "status":
            openEditModal(btn);
            break;
        case "sort":
            handleSort(btn);
            break;
        case "clickRow":
            checkRow(btn);
            break;
        case "clearInput":
            clearInput(e);
            break;
        case "changeStatus":
            changeStatus(btn);
            break;
        case "btnDropDown":
            btnDropDown(btn, e);
            break;
        case "toggleSelect":
            toggleSelect(btn, e);
            break;
    }
});
const handleSort = async (btn) => {
    const sort = btn.dataset.sort;
    const currentOrder = btn.dataset.order;
    const params = new URLSearchParams(window.location.search);
    const order = currentOrder == 'DESC' ? 'ASC' : 'DESC';
    params.set("sort", `${sort},${order}`);
    params.set("page", '0');
    const targetUrl = `${window.location.pathname}?${params.toString()}`;
    PaginationUtils.onUpdate(targetUrl);
};
document.addEventListener('keydown', (e) => {
    const target = e.target;
    if (e.key === 'Enter' && target.classList.contains('search-input')) {
        e.preventDefault();
        const input = target;
        const keyword = input.value.trim();
        const params = new URLSearchParams(window.location.search);
        params.set('page', '0');
        if (keyword) {
            params.set('keyword', keyword);
        }
        else {
            params.delete('keyword');
        }
        const targetUrl = `${window.location.pathname}?${params.toString()}`;
        PaginationUtils.onUpdate(targetUrl);
    }
}, true);
document.addEventListener('input', (e) => {
    const input = e.target;
    if (input && input.classList.contains('search-input')) {
        const container = input.closest('.search-container');
        const clearBtn = container?.querySelector('.clear-btn');
        if (input.value.length > 0) {
            clearBtn?.classList.remove('hidden');
        }
        else {
            clearBtn?.classList.add('hidden');
        }
    }
});
const clearInput = async (e) => {
    const target = e.target;
    const container = target.closest('.search-container');
    const input = container?.querySelector('.search-input');
    if (!input)
        return;
    input.value = '';
    const params = new URLSearchParams(window.location.search);
    params.delete('keyword');
    params.set('page', '0');
    const targetUrl = `${window.location.pathname}?${params.toString()}`;
    PaginationUtils.onUpdate(targetUrl);
};
const openEditModal = async (btn) => {
    closeDropdown();
    const checkedBoxes = document.querySelectorAll(".check-box:checked");
    if (checkedBoxes.length == 0) {
        showToast("사용자를 선택해주세요.", "error");
        return;
    }
    let ids = Array.from(checkedBoxes).map(check => Number(check.value));
    const mode = btn.dataset.mode || "";
    const params = new URLSearchParams();
    params.set("mode", mode);
    ids.forEach(id => params.append("ids", String(id)));
    const url = `/users/modal/changeStatus?${params.toString()}`;
    try {
        const html = await sendRequest(url, { method: 'GET' }, 'TEXT');
        const root = document.getElementById("modal-root");
        if (root) {
            root.innerHTML = html;
        }
    }
    catch (error) {
        const err = error;
        showToast(err.message || "오류가 발생했습니다.", "error");
    }
};
const changeStatus = async (btn) => {
    const data = btn.dataset.ids;
    const mode = btn.dataset.mode || "";
    let ids = data.split(",");
    const url = `/api/v1/users/changeStatus`;
    try {
        const result = await sendRequest(url, {
            method: "POST",
            body: {
                ids: ids,
                mode: mode
            }
        });
        closeModal();
        showToast(result.message, "success");
        setTimeout(() => location.reload(), 1200);
    }
    catch (error) {
        const err = error;
        showToast(err.message || "오류가 발생했습니다.", "error");
    }
};
