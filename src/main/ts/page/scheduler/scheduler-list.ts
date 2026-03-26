import { PaginationUtils } from '../../common/pagination.js';
import { sendRequest } from '../../common/request.js';
import { ErrorResponse } from '../../common/type';
import { showToast } from '../../common/toast.js';
import { checkRow, checkToggle } from '../../common/checkbox.js';
import { closeModal } from '../../common/modal.js';
import { SchedulerUpdateRequest } from './scheduler-types.js';
import { validator } from '../../common/validation.js';

const defaultHandler = async (url: string) => {
  try {
    const html = await sendRequest(
      url,
      { method: 'GET', headers: { 'X-Requested-With': 'XMLHttpRequest' } },
      'TEXT',
    );
    document.getElementById('list-wrapper').innerHTML = html;

    window.history.pushState({}, '', url);
  } catch (error) {
    const err = error as ErrorResponse;
    showToast(err.message || '오류가 발생했습니다.', 'error');
  }
};

PaginationUtils.setUpdateHandler(defaultHandler);

let lastClickTime = 0;
let lastClickTarget: HTMLElement | null = null;
document.addEventListener('click', (e) => {
  const target = e.target as HTMLElement;

  const btn = target.closest<HTMLElement>('[data-action]');
  if (!btn) {
    return;
  }
  const now = Date.now();
  const isDouble = lastClickTarget === btn && now - lastClickTime < 300;
  lastClickTime = now;
  lastClickTarget = btn;

  const action = btn.dataset.action;
  switch (action) {
    case 'checkToggle':
      checkToggle(btn);
      break;
    case 'clickRow': {
      if (isDouble) {
        openDetailModal(btn);
      } else {
        checkRow(btn);
      }
      break;
    }
    case 'openExecuteModal':
      openConfirmModal('EXECUTE');
      break;
    case 'openSuspendModal':
      openConfirmModal('SUSPEND');
      break;
    case 'openResumeModal':
      openConfirmModal('RESUME');
      break;
    case 'execute':
      execute(btn);
      break;
    case 'suspend':
      suspend(btn);
      break;
    case 'resume':
      resume(btn);
      break;
    case 'toggleView':
      toggleView(btn);
      break;
    case 'clickCard':
      checkRow(btn);
      const checkbox = btn.querySelector('.check-box') as HTMLInputElement;
      if (checkbox) {
        btn.classList.toggle('is-selected', checkbox.checked);
      }
      break;
    case 'openMobileDetail':
      e.stopPropagation();
      openDetailModal(btn);
      break;
    case 'updateScheduler':
      updateScheduler();
      break;
  }
});

const openConfirmModal = async (
  mode: 'EXECUTE' | 'SUSPEND' | 'RESUME',
): Promise<void> => {
  const checkedBoxes =
    document.querySelectorAll<HTMLInputElement>('.check-box:checked');

  if (checkedBoxes.length == 0) {
    showToast('스케줄러를 선택해주세요.', 'error');
    return;
  }
  if (mode === 'EXECUTE' && checkedBoxes.length > 1) {
    showToast('스케줄러는 하나만 선택해주세요.', 'error');
    return;
  }

  const params = new URLSearchParams();
  params.set('mode', mode.toLowerCase());

  checkedBoxes.forEach((box) => {
    params.append('ids', box.value);
  });
  const url = `/schedulers/modal/confirm?${params.toString()}`;

  try {
    const html = await sendRequest(url, { method: 'GET' }, 'TEXT');

    const root = document.getElementById('modal-root');
    if (root) {
      root.innerHTML = html;
    }
  } catch (error) {
    const err = error as ErrorResponse;
    showToast(err.message || '오류가 발생했습니다.', 'error');
  }
};

const execute = async (btn: HTMLElement): Promise<void> => {
  const id = btn.dataset.ids;

  if (!id) {
    showToast('대상 스케줄러가 없습니다.', 'error');
    return;
  }

  try {
    btn.setAttribute('disabled', 'true');
    const result = await sendRequest(`/api/v1/schedulers/run`, {
      method: 'POST',
      body: { id: id },
    });

    closeModal();
    showToast(result.message, 'success');
    setTimeout(() => location.reload(), 1000);
  } catch (error) {
    const err = error as ErrorResponse;
    showToast(err.message || '오류가 발생했습니다.', 'error');
  }
};
const suspend = async (btn: HTMLElement): Promise<void> => {
  const data = btn.dataset.ids;
  const ids = data.split(',').map(Number);

  if (!ids) {
    showToast('대상 스케줄러가 없습니다.', 'error');
    return;
  }

  try {
    btn.setAttribute('disabled', 'true');
    const result = await sendRequest(`/api/v1/schedulers/suspend`, {
      method: 'POST',
      body: { ids: ids },
    });

    closeModal();
    showToast(result.message, 'success');
    setTimeout(() => location.reload(), 1000);
  } catch (error) {
    const err = error as ErrorResponse;
    showToast(err.message || '오류가 발생했습니다.', 'error');
  }
};

const resume = async (btn: HTMLElement): Promise<void> => {
  const data = btn.dataset.ids;
  const ids = data.split(',').map(Number);

  if (!ids) {
    showToast('대상 스케줄러가 없습니다.', 'error');
    return;
  }

  try {
    btn.setAttribute('disabled', 'true');
    const result = await sendRequest(`/api/v1/schedulers/resume`, {
      method: 'POST',
      body: { ids: ids },
    });

    closeModal();
    showToast(result.message, 'success');
    setTimeout(() => location.reload(), 1000);
  } catch (error) {
    const err = error as ErrorResponse;
    showToast(err.message || '오류가 발생했습니다.', 'error');
  }
};

const toggleView = (btn: HTMLElement) => {
  const detailView = document.getElementById('detail-view');
  const editForm = document.getElementById('scheduler-edit-form');
  const mode = btn.dataset.mode;
  if (mode === 'edit') {
    detailView.classList.add('hidden');
    editForm.classList.remove('hidden');
    editForm.classList.add('flex');
  } else {
    detailView.classList.remove('hidden');
    editForm.classList.add('hidden');
    editForm.classList.remove('flex');
  }
};

const openDetailModal = async (btn: HTMLElement) => {
  const id = btn.dataset.id;

  try {
    const html = await sendRequest(
      `/schedulers/modal/detail/${id}`,
      { method: 'GET' },
      'TEXT',
    );

    const root = document.getElementById('modal-root');
    if (root) {
      root.innerHTML = html;
    }
  } catch (error) {
    const err = error as ErrorResponse;
    showToast(err.message || '오류가 발생했습니다.', 'error');
  }
};

const updateScheduler = async (): Promise<void> => {
  const form = document.getElementById(
    'scheduler-edit-form',
  ) as HTMLFormElement;

  const formData = new FormData(form);

  const enabled = form.querySelector(
    'input[name="enabled"]',
  ) as HTMLInputElement;
  const schedulerData: SchedulerUpdateRequest = {
    cron: formData.get('cron') as string,
    description: formData.get('description') as string,
    enabled: enabled.checked,
  };

  const id = Number(formData.get('id'));
  try {
    const result = await sendRequest(`/api/v1/schedulers/${id}`, {
      method: 'POST',
      body: schedulerData,
    });
    closeModal();
    showToast(result.message, 'success');

    setTimeout(() => location.reload(), 1200);
  } catch (error) {
    const err = error as ErrorResponse;
    if (err.fieldErrors) {
      validator.displayErrors(err.fieldErrors);
    }
    showToast(err.message || '저장 중 오류가 발생했습니다.', 'error');
  }
};
