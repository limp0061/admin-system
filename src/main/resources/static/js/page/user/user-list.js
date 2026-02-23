import { showToast } from "../../common/toast.js";
import { closeModal } from "../../common/modal.js";
import { PaginationUtils } from "../../common/pagination.js";
import { btnDropDown, closeDropdown, toggleSelect } from "../../common/select.js";
import { checkRow, checkToggle } from "../../common/checkbox.js";
import { initGlobalEvents } from "../../common/event.js";
import { sendMultipartRequest, sendRequest } from "../../common/request.js";
import { validator } from "../../common/validation.js";
initGlobalEvents();
function openSetting() {
    console.log("Opening settings.");
}
PaginationUtils.setUpdateHandler(async (url) => {
    const html = await sendRequest(url, { method: 'GET', headers: { 'X-Requested-With': 'XMLHttpRequest' } }, 'TEXT');
    document.getElementById('list-wrapper').innerHTML = html;
    window.history.pushState({}, '', url);
});
document.addEventListener("dblclick", (e) => {
    const target = e.target;
    const td = target.closest('td');
    if (target.classList.contains('check-box') || (td && td.querySelector('.check-box'))) {
        return;
    }
    const row = target.closest("[data-action]");
    if (!row || row.dataset.action !== "clickRow")
        return;
    const checkbox = row.querySelector(".check-box");
    if (checkbox)
        checkbox.checked = true;
    const action = row.dataset.action;
    switch (action) {
        case "clickRow": {
            openUserModal('EDIT', row);
        }
    }
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
        case "add":
            openUserModal('ADD', btn);
            break;
        case "status":
            openEditModal(btn);
            break;
        case "setting":
            openSetting();
            break;
        case "toggleSelect":
            toggleSelect(btn, e);
            break;
        case "saveUser":
            saveUser();
            break;
        case "editUser":
            checkRow(btn);
            editUser();
            break;
        case "changeStatus":
            changeStatus(btn);
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
    }
});
/**
 * 부서 추가/수정 모달 열기
 */
const openUserModal = async (mode, btn) => {
    const id = btn.closest("[data-id]")?.dataset.id;
    const requestConfig = {
        ADD: { url: "/users/modal/add" },
        EDIT: { url: "/users/modal/edit", param: "id", value: id },
    };
    const request = requestConfig[mode];
    let url = `${request.url}`;
    if (mode === 'EDIT') {
        if (request.param && request.value) {
            url += `?${request.param}=${encodeURIComponent(String(request.value))}`;
        }
    }
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
const saveUser = async () => {
    const form = document.getElementById("user-form");
    if (!form)
        return;
    validator.clear(form);
    const formData = new FormData(form);
    const userData = {
        id: formData.get("id") ? Number(formData.get("id")) : undefined,
        name: formData.get("name"),
        emailId: formData.get("emailId"),
        password: formData.get("password"),
        deptId: formData.get("deptId") ? Number(formData.get("deptId")) : undefined,
        userCode: formData.get("userCode") || null,
        position: formData.get("position") || null,
        gender: formData.get("gender"),
        deptCode: formData.get("deptCode") || null,
        userStatus: formData.get("userStatus"),
        roleId: Number(formData.get("role"))
    };
    const requiredFields = {
        name: { label: "이름" },
        password: { label: "비밀번호" },
        emailId: { label: "이메일" },
        gender: { label: "성별", type: 'select' },
        userStatus: { label: "사용자 상태", type: 'select' }
    };
    const error = validator.validateRequired(userData, requiredFields);
    if (error.length > 0) {
        validator.displayErrors(error);
        showToast("입력 형식이 올바르지 않습니다.", "error");
        return;
    }
    const fileInput = document.getElementById('fileInput');
    const hasFile = fileInput?.files && fileInput.files.length > 0;
    try {
        const sendFormData = new FormData();
        sendFormData.append("userData", new Blob([JSON.stringify(userData)], { type: "application/json" }));
        if (hasFile) {
            sendFormData.append("profileImage", fileInput.files[0]);
        }
        const result = await sendMultipartRequest('/api/v1/users', {
            method: "POST",
            body: sendFormData
        });
        closeModal();
        showToast(result.message, "success");
        setTimeout(() => location.reload(), 1200);
    }
    catch (error) {
        const err = error;
        if (err.fieldErrors && err.fieldErrors.length > 0) {
            validator.displayErrors(err.fieldErrors);
        }
        showToast(err.message || "입력 형식이 올바르지 않습니다.", "error");
    }
};
const editUser = async () => {
    const form = document.getElementById("user-form");
    if (!form)
        return;
    validator.clear(form);
    const formData = new FormData(form);
    const userData = {
        id: formData.get("id") ? Number(formData.get("id")) : undefined,
        name: formData.get("name"),
        emailId: formData.get("emailId"),
        password: formData.get("password") || null,
        deptId: formData.get("deptId") ? Number(formData.get("deptId")) : undefined,
        userCode: formData.get("userCode") || null,
        position: formData.get("position") || null,
        gender: formData.get("gender"),
        deptCode: formData.get("deptCode") || null,
        userStatus: formData.get("userStatus"),
        roleId: Number(formData.get("role"))
    };
    const requiredFields = {
        name: { label: "이름" },
        emailId: { label: "이메일" },
        gender: { label: "성별", type: 'select' },
        userStatus: { label: "사용자 상태", type: 'select' }
    };
    const error = validator.validateRequired(userData, requiredFields);
    if (error.length > 0) {
        validator.displayErrors(error);
        showToast("입력 형식이 올바르지 않습니다.", "error");
        return;
    }
    const fileInput = document.getElementById('fileInput');
    const hasFile = fileInput?.files && fileInput.files.length > 0;
    try {
        const sendFormData = new FormData();
        sendFormData.append("userData", new Blob([JSON.stringify(userData)], { type: "application/json" }));
        if (hasFile) {
            sendFormData.append("profileImage", fileInput.files[0]);
        }
        const result = await sendMultipartRequest(`/api/v1/users/${userData.id}/update`, {
            method: "POST",
            body: sendFormData
        });
        closeModal();
        showToast(result.message, "success");
        setTimeout(() => location.reload(), 1200);
    }
    catch (error) {
        const err = error;
        if (err.fieldErrors) {
            validator.displayErrors(err.fieldErrors);
        }
        showToast(err.message || "입력 형식이 올바르지 않습니다.", "error");
    }
};
const openEditModal = async (btn) => {
    closeDropdown();
    const checkedBoxes = document.querySelectorAll(".check-box:checked");
    if (checkedBoxes.length == 0)
        return;
    let ids = Array.from(checkedBoxes).map(check => Number(check.value));
    if (ids.length <= 0) {
        showToast("사용자를 선택해주세요.", "error");
        return;
    }
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
