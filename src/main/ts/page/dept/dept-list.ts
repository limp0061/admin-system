import {PaginationUtils} from "../../common/pagination.js";
import {showToast} from "../../common/toast.js";
import {initGlobalEvents} from "../../common/event.js";
import {checkRow, checkToggle} from "../../common/checkbox.js";
import {btnDropDown, toggleSelect} from "../../common/select.js";

import {sendRequest} from "../../common/request.js";
import {ErrorResponse} from "../../common/type";

initGlobalEvents();

const defaultHandler = async (url: string) => {
    const html = await sendRequest(url, {method: 'GET', headers: {'X-Requested-With': 'XMLHttpRequest'}}, 'TEXT');
    document.getElementById('list-wrapper').innerHTML = html;

    window.history.pushState({}, '', url);
};

const modalHandler = async (url: string) => {
    const html = await sendRequest(url, {method: 'GET', headers: {'X-Requested-With': 'XMLHttpRequest'}}, 'TEXT');
    document.getElementById('modal-list-wrapper').innerHTML = html;
};

PaginationUtils.setUpdateHandler(defaultHandler);
document.addEventListener("click", (e) => {
    const target = e.target as HTMLElement;

    if (target.classList.contains('check-box')) {
        return;
    }
    const btn = target.closest<HTMLElement>("[data-action]");

    if (!btn) return;

    const action = btn.dataset.action;
    switch (action) {
        case "expandAll":
            expandAll();
            break;
        case "toggle":
            e.stopPropagation();
            toggleDept(btn);
            break;
        case "selectDept":
            selectDept(btn);
            break;
        case "clearInput":
            clearInput(e);
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
        case "openAddDeptUserModal":
            openDeptUserModal(btn);
            break;
        case "openDeleteDeptUserModal":
            openDeptUserConfirmModal('DEL');
            break;
        case "userChangeDept":
            userChangeDept(btn)
            break;
        case "userRemoveDept":
            userRemoveDept(btn)
            break;
        case "toggleSelect":
            toggleSelect(btn, e);
            break;
        case "selectModalDept":
            selectModalDept(btn);
            break;
        case "closeModal":
            closeModal();
            break;
        case "moveUser":
            moveUser();
            break;
        case "removeUser":
            removeUser();
            break;
        case "removeOne":
            removeOne(btn);
            break;
    }
})

/**
 * 모든 부서 펼치기
 */
const expandAll = (): void => {
    document.querySelectorAll('ul[data-parent]')
        .forEach(ul => ul.classList.remove('hidden'));

    document.querySelectorAll('[data-action="toggle"] svg')
        .forEach(svg => svg.classList.add('rotate-90'));
};

/**
 * 개별 부서 토글
 */
const toggleDept = (btn: HTMLElement): void => {
    const li = btn.closest('li');
    const childUl = li?.querySelector('ul[data-parent]');
    childUl?.classList.toggle('hidden');

    const icon = btn.querySelector('svg');
    icon?.classList.toggle('rotate-90');
};

const selectDept = async (btn: HTMLElement) => {
    const id = btn.dataset.id;
    if (!id) return;

    const params = new URLSearchParams(window.location.search);
    params.delete('keyword');
    params.set('deptId', id)
    params.set('page', '0');

    const targetUrl = `${window.location.pathname}?${params.toString()}`;
    (PaginationUtils as any).onUpdate(targetUrl);
};

document.addEventListener('keydown', (e: KeyboardEvent) => {
    const target = e.target as HTMLElement;

    if (e.key === 'Enter' && target.classList.contains('search-input')) {
        e.preventDefault();

        const input = target as HTMLInputElement;
        const keyword = input.value.trim();

        const modalContainer = document.getElementById("modal-form");
        if (modalContainer) {
            searchModal();
        } else {
            const params = new URLSearchParams(window.location.search);
            params.set('page', '0');

            if (keyword) {
                params.set('keyword', keyword);
            } else {
                params.delete('keyword');
            }

            const targetUrl = `${window.location.pathname}?${params.toString()}`;
            (PaginationUtils as any).onUpdate(targetUrl);
        }
    }
}, true);

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
    }
})

const clearInput = async (e: Event) => {
    const target = e.target as HTMLElement;

    const container = target.closest('.search-container');
    const input = container?.querySelector('.search-input') as HTMLInputElement;
    const clearBtn = container?.querySelector('.clear-btn');
    if (!input) return;

    input.value = '';
    clearBtn.classList.add('hidden');
    const isModal = document.getElementById("modal-form");
    if (isModal) {
        searchModal();
    } else {
        const params = new URLSearchParams(window.location.search);
        params.delete('keyword');
        params.set('page', '0');

        const targetUrl = `${window.location.pathname}?${params.toString()}`;
        (PaginationUtils as any).onUpdate(targetUrl);
    }
};

/**
 * 부서 구성원 변경
 */
const openDeptUserModal = async (btn: HTMLElement): Promise<void> => {
    const checkedBoxes = document.querySelectorAll<HTMLInputElement>(".check-box:checked");
    let ids = Array.from(checkedBoxes).map(check => Number(check.value));
    const params = new URLSearchParams();
    ids.forEach(id => params.append("ids", String(id)));

    const url = `/user-depts/modal/form?${params.toString()}`;
    try {
        const html = await sendRequest(url, {method: 'GET'}, 'TEXT');
        const root = document.getElementById("modal-root");
        if (root) {
            root.innerHTML = html;
            PaginationUtils.setBaseUrl('/user-depts/modal/form/table');
            PaginationUtils.setUpdateHandler(modalHandler);
        }
    } catch (error) {
        const err = error as ErrorResponse;
        showToast(err.message || "오류가 발생했습니다.", "error");
    }
};

/**
 * 부서 구성원 제거
 */

const openDeptUserConfirmModal = async (mode: 'EDIT' | 'DEL'): Promise<void> => {
    const checkedBoxes = document.querySelectorAll<HTMLInputElement>(".check-box:checked");
    if (checkedBoxes.length == 0) return;

    let ids = Array.from(checkedBoxes).map(check => Number(check.value));
    if (ids.length <= 0) {
        showToast("사용자를 선택해주세요.", "error")
        return;
    }
    const params = new URLSearchParams();
    params.set("mode", mode);

    ids.forEach(id => params.append("ids", String(id)));
    const url = `/user-depts/modal/delete?${params.toString()}`;

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

const userChangeDept = async (btn: HTMLElement): Promise<void> => {
    const selectedUsers = document.getElementById('selected-user-list')?.querySelectorAll('li');

    if (!selectedUsers || selectedUsers.length === 0) {
        showToast("이동할 사용자를 선택해주세요.", "error");
        return;
    }

    const targetDeptInput = document.getElementById('targetDeptId') as HTMLInputElement;
    const deptId = targetDeptInput?.value;
    if (!deptId || deptId === "") {
        showToast("이동할 부서를 선택해주세요.", "error");
        return;
    }
    const params = Array.from(selectedUsers).map(el => ({
            userId: el.dataset.userId,
            deptId: el.dataset.deptId
        })
    );
    const mode = btn.dataset.mode;
    try {
        const result = await sendRequest('/api/v1/user-depts/change',
            {
                method: "POST",
                body: {
                    targetDeptId: deptId,
                    userDepts: params,
                    mode: mode
                }
            }
        );
        closeModal();
        showToast(result.message, "success");

        setTimeout(() => location.reload(), 1200);
    } catch (error) {
        const err = error as ErrorResponse;
        showToast(err.message || "오류가 발생했습니다.", "error");
    }
}

const userRemoveDept = async (btn: HTMLElement): Promise<void> => {
    const userInfo = document.querySelectorAll<HTMLElement>('.dept-user-info');
    const params = Array.from(userInfo).map(el => ({
            userId: el.dataset.userId,
            deptId: el.dataset.deptId
        })
    );
    const deptId = btn.dataset.targetDeptId;
    const mode = btn.dataset.mode;
    try {
        const result = await sendRequest('/api/v1/user-depts/change',
            {
                method: "POST",
                body: {
                    targetDeptId: deptId,
                    userDepts: params,
                    mode: mode
                }
            }
        );
        closeModal();
        showToast(result.message, "success");

        setTimeout(() => location.reload(), 1200);
    } catch (error) {
        const err = error as ErrorResponse;
        showToast(err.message || "오류가 발생했습니다.", "error");
    }
}

const selectModalDept = (li: HTMLElement) => {
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

    searchModal();
}

const searchModal = (): void => {
    const modalContainer = document.getElementById("modal-form");
    const deptId = (modalContainer.querySelector('#select-dept-id') as HTMLInputElement)?.value;
    const keyword = (modalContainer.querySelector('input[name="keyword"]') as HTMLInputElement)?.value;

    PaginationUtils.applyFilter({
        deptId: deptId,
        keyword: keyword
    });
}

const closeModal = (): void => {
    const root = document.getElementById("modal-root");
    if (root) root.innerHTML = "";

    document.body.style.overflow = "auto";
    PaginationUtils.setBaseUrl(null);
    PaginationUtils.setUpdateHandler(defaultHandler);
};

const moveUser = () => {
    const wrapper = document.getElementById('modal-list-wrapper');
    if (!wrapper) return;

    const checkedBoxes = wrapper.querySelectorAll<HTMLInputElement>(".check-box:checked");
    if (checkedBoxes.length == 0) return;

    const targetUl = document.getElementById('selected-user-list');
    const emptySpace = document.getElementById("empty-msg");
    emptySpace?.classList.add('hidden');
    targetUl.classList.remove('hidden');
    targetUl.classList.add('flex', 'flex-col', 'gap-1');

    checkedBoxes.forEach(box => {
        const tr = box.closest('tr');
        if (!tr) return;

        const id = box.value;
        const name = tr.cells[1].innerText;
        const userCode = tr.cells[2].innerText;
        const deptId = tr.cells[3].dataset.deptId;

        if (targetUl.querySelector(`li[data-id="${id}"]`)) return;

        const liHtml = `
            <li class="flex items-center justify-between p-2 border-b bg-white text-xs hover:cursor-pointer hover:bg-gray-50" 
            data-user-id="${id}" data-dept-id="${deptId}" data-action="clickRow">
                <div class="flex items-center gap-3">
                    <input type="checkbox" class="check-box size-3" value="${id}">
                    <span>${name} (${userCode})</span>
                </div>
                <button type="button" class="text-gray-400 hover:text-red-500" data-action="removeOne">
                    <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" class="size-3">
                        <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12" />
                    </svg>
                </button>
            </li>
        `;

        targetUl.insertAdjacentHTML('beforeend', liHtml);
    });
    const allBoxes = wrapper.querySelectorAll<HTMLInputElement>('input[type="checkbox"]');
    allBoxes.forEach(cb => {
        cb.checked = false;
    });
}

const removeUser = () => {
    const ul = document.getElementById('selected-user-list');
    const checkedBoxes = ul.querySelectorAll<HTMLInputElement>('.check-box:checked');
    if (checkedBoxes.length == 0) return;

    checkedBoxes.forEach(box => {
        box.closest('li').remove();
    });

    if (ul && ul.children.length === 0) {
        ul.classList.add('hidden');
        document.getElementById('empty-msg')?.classList.remove('hidden');
    }
}

const removeOne = (btn: HTMLElement) => {
    if (btn) {
        btn.closest('li')?.remove();

        const ul = document.getElementById('selected-user-list');
        if (ul && ul.children.length === 0) {
            ul.classList.add('hidden');
            document.getElementById('empty-msg')?.classList.remove('hidden');
        }
    }
}