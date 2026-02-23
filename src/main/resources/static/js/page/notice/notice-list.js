import { showToast } from "../../common/toast.js";
import { closeModal } from "../../common/modal.js";
import { PaginationUtils } from "../../common/pagination.js";
import { btnDropDown, toggleSelect } from "../../common/select.js";
import { checkRow, checkToggle } from "../../common/checkbox.js";
import { initGlobalEvents } from "../../common/event.js";
import { sendRequest } from "../../common/request.js";
import { initDateTimePicker } from "../../common/datepicker.js";
import { validator } from "../../common/validation.js";
import { EditorManager } from "../../common/editor.js";
initGlobalEvents();
const noticeEditor = new EditorManager();
const defaultHandler = async (url) => {
    const html = await sendRequest(url, { method: 'GET', headers: { 'X-Requested-With': 'XMLHttpRequest' } }, 'TEXT');
    document.getElementById('list-wrapper').innerHTML = html;
    window.history.pushState({}, '', url);
};
PaginationUtils.setUpdateHandler(defaultHandler);
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
            openNoticeModal('EDIT', row);
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
        case "toggleSelect":
            toggleSelect(btn, e);
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
        case "clearInput":
            clearInput(e);
            break;
        case "noticeFilter":
            noticeFilter(btn);
            break;
        case "toggleTab":
            toggleTab(btn);
            break;
        case "openNoticeModal":
            openNoticeModal("ADD", btn);
            break;
        case "saveNotice":
            saveNotice();
            break;
        case "clearDateInput":
            clearDateInput(e);
            break;
        case "checkRealTime":
            checkRealTime(e);
            break;
        case "checkForce":
            checkForce(e);
            break;
        case "openDeleteModal":
            openDeleteModal();
            break;
        case "deleteNotice":
            deleteNotice(btn);
            break;
        case "closeEditModal":
            closeEditModal();
            break;
    }
});
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
const noticeFilter = (li) => {
    const val = li.dataset.value;
    const text = li.textContent?.trim() || "";
    const commonUl = document.getElementById('common-select-ul');
    const ownerId = commonUl.dataset.owner;
    if (!ownerId)
        return;
    const displayBtn = document.getElementById(ownerId);
    if (!displayBtn)
        return;
    const parentField = displayBtn.parentElement;
    const input = parentField?.querySelector('input[type="hidden"]');
    if (input)
        input.value = val || "";
    if (displayBtn) {
        displayBtn.innerText = text;
    }
    document.querySelectorAll('.select-ul').forEach(ul => ul.classList.add('hidden'));
    searchFilter();
};
const toggleTab = (btn) => {
    const type = btn.dataset.tab;
    const tabInput = document.getElementById('tabType');
    tabInput.value = type;
    searchFilter();
};
const searchFilter = () => {
    const filter = document.getElementById('noticeType')?.value || 'ALL';
    const type = document.getElementById('tabType')?.value || 'ALL';
    const keywordInput = document.querySelector('.search-container input[name="keyword"]');
    const keyword = keywordInput?.value || '';
    PaginationUtils.applyFilter({
        type: type,
        filter: filter,
        keyword: keyword
    });
};
const openNoticeModal = async (mode, btn) => {
    if (noticeEditor.instance) {
        await noticeEditor.destroy();
    }
    const id = btn.closest("[data-id]")?.dataset.id;
    const requestConfig = {
        ADD: { url: "/notices/modal/add" },
        EDIT: { url: "/notices/modal/edit", param: "id", value: id },
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
            await noticeEditor.init('#editor', '/api/v1/files/editor-upload?type=NOTICE');
            const startVal = document.getElementById("startAt").value;
            const endVal = document.getElementById("endAt").value;
            const startPicker = initDateTimePicker("#startAt", {
                defaultDate: startVal || null,
                onChange: (selectedDates) => {
                    const clearBtn = document.getElementById("clear-startAt");
                    if (selectedDates.length > 0) {
                        endPicker.set("minDate", selectedDates[0]);
                        clearBtn?.classList.remove("hidden");
                    }
                    else {
                        clearBtn?.classList.add("hidden");
                    }
                }
            });
            const endPicker = initDateTimePicker("#endAt", {
                defaultDate: endVal || null,
                onChange: (selectedDates) => {
                    const clearBtn = document.getElementById("clear-endAt");
                    if (selectedDates.length > 0) {
                        startPicker.set("maxDate", selectedDates[0]);
                        clearBtn?.classList.remove("hidden");
                    }
                    else {
                        clearBtn?.classList.add("hidden");
                    }
                }
            });
            const realTimeInput = document.getElementById("isRealTimeNoticed");
            const isRealTimeNoticed = realTimeInput.checked;
            if (isRealTimeNoticed) {
                realTimeInput.disabled = true;
                startPicker.clear();
                startPicker.set("clickOpens", false);
                const startAtInput = document.getElementById("startAt");
                if (startAtInput && startAtInput._flatpickr) {
                    const visualInput = startAtInput._flatpickr.altInput || startAtInput;
                    visualInput.disabled = true;
                    visualInput.classList.add('pointer-events-none', 'opacity-50', 'bg-gray-100');
                }
                document.getElementById("clear-startAt")?.classList.add("hidden");
            }
            if (mode === 'EDIT') {
                if (startVal)
                    endPicker.set("minDate", startVal);
                if (endVal)
                    startPicker.set("maxDate", endVal);
            }
            const forceInput = document.getElementById("isForce");
            if (forceInput && forceInput.checked) {
                realTimeInput.checked = true;
                realTimeInput.disabled = true;
                forceInput.checked = true;
                forceInput.disabled = true;
                const fakeEvent = { target: realTimeInput };
                checkRealTime(fakeEvent);
            }
            const wrapper = document.getElementById("editor-wrapper");
            if (wrapper) {
                wrapper.classList.remove("opacity-0");
                wrapper.classList.add("opacity-100");
            }
        }
    }
    catch (error) {
        const err = error;
        showToast(err.message || "오류가 발생했습니다.", "error");
    }
};
const saveNotice = async () => {
    const form = document.getElementById('noticeForm');
    if (!form)
        return;
    validator.clear(form);
    const realTimeCheckbox = document.getElementById('isRealTimeNoticed');
    const forceCheckbox = document.getElementById('isForce');
    const isRealTimeNoticed = realTimeCheckbox ? realTimeCheckbox.checked : false;
    const isForce = forceCheckbox ? forceCheckbox.checked : false;
    const formData = new FormData(form);
    const noticeData = {
        id: formData.get('id') ? Number(formData.get('id')) : undefined,
        type: (formData.get('type')),
        title: (formData.get('title')),
        isRealTimeNoticed: isRealTimeNoticed,
        isForce: isForce,
        content: noticeEditor.getData(),
        startAt: formData.get('startAt') || null,
        endAt: formData.get('endAt') || null,
    };
    const requiredFields = {
        type: { label: "유형" },
        title: { label: "제목" },
        content: { label: "내용" },
    };
    const error = validator.validateRequired(noticeData, requiredFields);
    if (error.length > 0) {
        validator.displayErrors(error);
        showToast("입력 형식이 올바르지 않습니다.", "error");
        return;
    }
    try {
        const result = await sendRequest('/api/v1/notices', {
            method: 'POST',
            body: noticeData
        });
        closeEditModal();
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
const clearDateInput = (e) => {
    const target = e.target;
    const btn = target.closest('.clear-btn');
    if (btn) {
        const container = btn.parentElement;
        const input = container?.querySelector('input');
        if (input && input._flatpickr) {
            input._flatpickr.clear();
            btn.classList.add('hidden');
        }
    }
};
const checkRealTime = (e) => {
    const checkbox = e.target;
    const startAt = document.getElementById('startAt');
    const isChecked = checkbox.checked;
    if (startAt && startAt._flatpickr) {
        const fp = startAt._flatpickr;
        if (isChecked) {
            fp.clear();
            fp.set('clickOpens', false);
            fp.altInput.disabled = true;
            fp.altInput.classList.add('pointer-events-none', 'opacity-50', 'bg-gray-100');
        }
        else {
            fp.set('clickOpens', true);
            fp.altInput.disabled = false;
            fp.altInput.classList.remove('pointer-events-none', 'opacity-50', 'bg-gray-100');
        }
    }
};
const openDeleteModal = async () => {
    const checkedBoxes = document.querySelectorAll(".check-box:checked");
    let ids = Array.from(checkedBoxes).map(check => Number(check.value));
    if (ids.length <= 0) {
        showToast("공지를 선택해주세요.", "error");
        return;
    }
    const params = new URLSearchParams();
    ids.forEach(id => params.append("ids", String(id)));
    const url = `/notices/modal/delete?${params.toString()}`;
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
const checkForce = (e) => {
    const checkbox = e.target;
    const realTimeInput = document.getElementById("isRealTimeNoticed");
    const isChecked = checkbox.checked;
    if (isChecked) {
        realTimeInput.checked = true;
        realTimeInput.disabled = true;
    }
    else {
        realTimeInput.checked = false;
        realTimeInput.disabled = false;
    }
    const fakeEvent = { target: realTimeInput };
    checkRealTime(fakeEvent);
};
const deleteNotice = async (btn) => {
    const data = btn.dataset.ids;
    const ids = data.split(",").map(id => `ids=${id}`).join("&");
    try {
        const result = await sendRequest(`/api/v1/notices?${ids}`, {
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
const closeEditModal = () => {
    noticeEditor.destroy();
    closeModal();
};
