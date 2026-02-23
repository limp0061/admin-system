import { showToast } from "../../common/toast.js";
import { closeModal } from "../../common/modal.js";
import { initGlobalEvents } from "../../common/event.js";
import { toggleSelect } from "../../common/select.js";
import { validator } from "../../common/validation.js";
import { sendRequest } from "../../common/request.js";
initGlobalEvents();
document.addEventListener("click", (e) => {
    const target = e.target;
    const btn = target.closest("[data-action]");
    // 1. 버튼 클릭이 아닌 경우 (메뉴 닫기 로직)
    if (!btn) {
        if (!target.closest('.edit-menu')) {
            document.querySelectorAll('.edit-menu').forEach(m => m.classList.add('hidden'));
        }
        return;
    }
    e.stopPropagation();
    const action = btn.dataset.action;
    switch (action) {
        case "expandAll":
            expandAll();
            break;
        case "toggle":
            toggleDept(btn);
            break;
        case "toggleSelect":
            toggleSelect(btn, e);
            break;
        case "openEditMenu":
            openEditMenu(btn, e);
            break;
        case "openAddRootDeptModal":
            openDeptModal("ADD", null);
            break;
        case "openAddChildDeptModal":
            openDeptModal("ADD", btn);
            break;
        case "openEditDeptModal":
            openDeptModal("EDIT", btn);
            break;
        case "openDeleteDeptModal":
            openDeptModal("DEL", btn);
            break;
        case "saveDept":
            saveDepartment();
            break;
        case "deleteDept":
            deleteDepartment(btn);
            break;
    }
});
/**
 * 모든 부서 펼치기
 */
const expandAll = () => {
    document.querySelectorAll('ul[data-parent]')
        .forEach(ul => ul.classList.remove('hidden'));
    document.querySelectorAll('[data-action="toggle"] svg')
        .forEach(svg => svg.classList.add('rotate-90'));
};
/**
 * 개별 부서 토글
 */
const toggleDept = (btn) => {
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
 * 부서 추가/수정/삭제 모달 열기
 */
const openDeptModal = async (mode, btn) => {
    const parent = btn?.closest('[data-id]');
    const id = parent?.dataset.id;
    const requestConfig = {
        ADD: { url: "/depts/modal/add", param: "upperDeptId" },
        EDIT: { url: "/depts/modal/edit", param: "id" },
        DEL: { url: "/depts/modal/delete", param: "id" }
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
/**
 * 데이터 저장 (비동기)
 */
const saveDepartment = async () => {
    const form = document.getElementById('dept-form');
    if (!form)
        return;
    validator.clear(form);
    const formData = new FormData(form);
    const deptData = {
        id: formData.get('id') ? Number(formData.get('id')) : undefined,
        deptCode: formData.get('deptCode'),
        deptName: formData.get('deptName'),
        upperDeptId: formData.get('upperDeptId') ? Number(formData.get('upperDeptId')) : undefined,
        sortOrder: Number(formData.get('sortOrder')) || 0,
        isActive: formData.get('isActive') === 'true',
    };
    const requiredFields = {
        deptName: { label: "부서명" },
        deptCode: { label: "부서 코드" },
    };
    const error = validator.validateRequired(deptData, requiredFields);
    if (error.length > 0) {
        validator.displayErrors(error);
        showToast("입력 형식이 올바르지 않습니다.", "error");
        return;
    }
    try {
        const result = await sendRequest('/api/v1/depts', {
            method: 'POST',
            body: deptData
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
/**
 * 데이터 삭제 (비동기)
 */
const deleteDepartment = async (btn) => {
    const deptId = btn.dataset.key;
    if (!deptId) {
        showToast("삭제할 부서 정보가 없습니다.", "error");
        return;
    }
    try {
        const result = await sendRequest(`/api/v1/depts/${encodeURIComponent(deptId)}`, {
            method: 'DELETE',
        });
        closeModal();
        showToast(result.message, "success");
        setTimeout(() => location.reload(), 1000);
    }
    catch (error) {
        const err = error;
        showToast(err.message || "삭제 중 오류가 발생했습니다.", "error");
    }
};
