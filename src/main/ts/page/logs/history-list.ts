import { PaginationUtils } from '../../common/pagination.js';
import { sendRequest } from '../../common/request.js';
import { initDateTimePicker } from '../../common/datepicker.js';
import { btnDropDown } from '../../common/select.js';
import { ErrorResponse } from '../../common/type';
import { showToast } from '../../common/toast.js';
import { HistoryRequest } from './log-types';
import { checkRow } from '../../common/checkbox.js';

const defaultHandler = async (url: string) => {
  try {
    const html = await sendRequest(
      url,
      { method: 'GET', headers: { 'X-Requested-With': 'XMLHttpRequest' } },
      'TEXT',
    );
    document.getElementById('list-wrapper').innerHTML = html;

    window.history.pushState({}, '', url);

    initSearchPickers();
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
    case 'btnDropDown':
      btnDropDown(btn, e);
      break;
    case 'clear':
      clear();
      break;
    case 'search':
      search();
      break;
    case 'switchTab':
      switchTab(btn);
      break;
    case 'clickRow': {
      if (isDouble) {
        openDetailModal(btn);
      }
      break;
    }
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
  }
});

const initSearchPickers = () => {
  const startInput = document.getElementById('startAt') as HTMLInputElement;
  const endInput = document.getElementById('endAt') as HTMLInputElement;
  if (!startInput || !endInput) return;

  initDateTimePicker('#startAt', {
    enableTime: false,
    dateFormat: 'Y-m-d',
    altFormat: 'Y-m-d',
    altInput: false,
    minDate: null,
  });

  initDateTimePicker('#endAt', {
    enableTime: false,
    dateFormat: 'Y-m-d',
    altFormat: 'Y-m-d',
    altInput: false,
    minDate: null,
  });
};
document.addEventListener('DOMContentLoaded', initSearchPickers);

const clear = () => {
  defaultHandler(window.location.pathname);
};

const search = async (): Promise<void> => {
  const historyRequest: HistoryRequest = {
    startAt:
      (document.getElementById('startAt') as HTMLInputElement).value || null,
    endAt: (document.getElementById('endAt') as HTMLInputElement).value || null,
    emailId:
      (document.getElementById('emailId') as HTMLInputElement).value || null,
  };
  PaginationUtils.applyFilter(historyRequest as Record<string, string | null>);
};

const switchTab = async (btn: HTMLElement): Promise<void> => {
  const url = btn.dataset.url;
  defaultHandler(url);
};

document.addEventListener(
  'keydown',
  (e: KeyboardEvent) => {
    const target = e.target as HTMLElement;

    if (e.key === 'Enter' && target.classList.contains('search-input')) {
      e.preventDefault();

      const input = target as HTMLInputElement;
      const keyword = input.value.trim();

      const params = new URLSearchParams(window.location.search);
      params.set('page', '0');

      if (keyword) {
        params.set('emailId', keyword);
      } else {
        params.delete('emailId');
      }

      const targetUrl = `${window.location.pathname}?${params.toString()}`;
      (PaginationUtils as any).onUpdate(targetUrl);
    }
  },
  true,
);

const openDetailModal = async (btn: HTMLElement) => {
  const id = btn.dataset.id;

  try {
    const html = await sendRequest(
      `/logs/history/modal/detail?id=${id}`,
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
