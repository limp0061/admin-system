import { showToast } from "../../common/toast.js";
import { initGlobalEvents } from "../../common/event.js";
import { sendRequest } from "../../common/request.js";
import { toggleSelect } from "../../common/select.js";
import { validator } from "../../common/validation.js";
import { closeModal } from "../../common/modal.js";
initGlobalEvents();
document.addEventListener("click", (e) => {
    const target = e.target;
    const btn = target.closest("[data-action]");
    if (!btn) {
        if (!target.closest('.edit-menu')) {
            document.querySelectorAll('.edit-menu').forEach(m => m.classList.add('hidden'));
        }
        return;
    }
    const action = btn.dataset.action;
    switch (action) {
        case "expandAll":
            expandAll();
            break;
        case "toggle":
            e.stopPropagation();
            toggleRole(btn);
            break;
        case "openEditMenu":
            openEditMenu(btn, e);
            break;
        case "openAddRoleModal":
            openRoleModal('ADD', null);
            break;
        case "openAddChildRoleModal":
            openRoleModal('ADD', btn);
            break;
        case "openEditRoleModal":
            openRoleModal('EDIT', btn);
            break;
        case "openDeleteRoleModal":
            openRoleModal('DEL', btn);
            break;
        case "toggleSelect":
            toggleSelect(btn, e);
            break;
        case "saveRole":
            saveRole();
            break;
        case "deleteRole":
            deleteRole(btn);
            break;
    }
});
/**
 * 모든 권한 펼치기
 */
const expandAll = () => {
    document.querySelectorAll('ul[data-parent]')
        .forEach(ul => ul.classList.remove('hidden'));
    document.querySelectorAll('[data-action="toggle"] svg')
        .forEach(svg => svg.classList.add('rotate-90'));
};
/**
 * 개별 권한 토글
 */
const toggleRole = (btn) => {
    const li = btn.closest('li');
    const childUl = li?.querySelector('ul[data-parent]');
    childUl?.classList.toggle('hidden');
    const icon = btn.querySelector('svg');
    icon?.classList.toggle('rotate-90');
};
/**
 * 편집 메뉴(점 세개) 열기
 */
const openEditMenu = (btn, e) => {
    e.preventDefault();
    e.stopPropagation();
    // 다른 열려있는 메뉴 닫기
    document.querySelectorAll('.edit-menu').forEach(m => {
        if (m !== btn.querySelector('.edit-menu'))
            m.classList.add('hidden');
    });
    const menu = btn.querySelector('.edit-menu');
    menu?.classList.toggle('hidden');
};
/**
 * 권한 추가/수정/삭제 모달 열기
 */
const openRoleModal = async (mode, btn) => {
    const parent = btn?.closest('[data-id]');
    const id = parent?.dataset.id;
    const requestConfig = {
        ADD: { url: "/resources/role/modal/add", param: "parentId" },
        EDIT: { url: "/resources/role/modal/edit", param: "id" },
        DEL: { url: "/resources/role/modal/delete", param: "id" }
    };
    const request = requestConfig[mode];
    if (!request)
        return;
    const params = new URLSearchParams();
    params.set("mode", mode);
    if (id && id !== 'undefined' && request.param) {
        params.set(request.param, id);
    }
    const url = `${request.url}?${params.toString()}`;
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
const saveRole = async () => {
    const form = document.getElementById('role-form');
    const formData = new FormData(form);
    const isAdmin = document.getElementById('isAdmin').checked;
    const roleData = {
        id: formData.get('id') ? Number(formData.get('id')) : undefined,
        roleKey: formData.get('roleKey'),
        roleName: formData.get('roleName'),
        parentId: formData.get('parentId') ? Number(formData.get('parentId')) : undefined,
        isAdmin: isAdmin
    };
    const requiredFields = {
        roleKey: { label: "권한 키" },
        roleName: { label: "권한 명" },
    };
    const error = validator.validateRequired(roleData, requiredFields);
    if (error.length > 0) {
        validator.displayErrors(error);
        showToast("입력 형식이 올바르지 않습니다.", "error");
        return;
    }
    const url = roleData.id
        ? `/api/v1/resources/role/${roleData.id}/update`
        : `/api/v1/resources/role`;
    try {
        const result = await sendRequest(url, {
            method: 'POST',
            body: roleData
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
        showToast(err.message || "저장 중 오류가 발생했습니다.", "error");
    }
};
const deleteRole = async (btn) => {
    const id = btn.dataset.id;
    try {
        const result = await sendRequest(`/api/v1/resources/role?id=${id}`, {
            method: "DELETE",
        });
        closeModal();
        showToast(result.message, "success");
        setTimeout(() => location.reload(), 1000);
    }
    catch (error) {
        const err = error;
        showToast(err.message || "오류가 발생했습니다.", "error");
    }
};
