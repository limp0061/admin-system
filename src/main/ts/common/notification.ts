import {sendRequest} from "./request.js";
import {formatDiffDate} from "./date.js";
import {NotificationItem} from "../page/notice/notification-types";
import {ErrorResponse} from "./type";
import {showToast} from "./toast.js";

let eventSource: EventSource | null = null;

export const initNotificationSSE = async () => {
    if (eventSource) {
        eventSource.close();
        eventSource = null;
    }

    try {
        eventSource = new EventSource(`/api/v1/notifications/subscribe`);
        eventSource.onopen = () => {
            console.log("SSE 연결이 성공적으로 수립되었습니다.");
        };

        eventSource.addEventListener('CONNECT', (event: MessageEvent) => {
            console.log("SSE CONNECT 수신:", event.data);
            updateBadgeCount(event.data);
        });

        eventSource.addEventListener('NOTICE_CREATED', (event: MessageEvent) => {
            console.log("이벤트 수신 확인!", event.data);
            const data = JSON.parse(event.data);
            updateBadgeCount(data.userCount);

            const dropdown = document.querySelector('.btn-notification');
            if (dropdown && !dropdown.classList.contains('hidden')) {
                loadNotificationTab('ALARM');
            }
        });

        eventSource.onerror = (err) => {
            console.error("SSE 연결 중 에러 발생 (재연결 시도 중...):", err);
            if (eventSource?.readyState === 2) {
                eventSource = null;
            }
        };
    } catch (error) {
        eventSource = null;
    }
};

// 배지 카운트 업데이트 로직
export const updateBadgeCount = (count: number | string) => {
    const badge = document.getElementById('badgeCount');
    if (!badge) return;

    const numericCount = Number(count);
    badge.setAttribute('data-count', String(numericCount));

    if (numericCount > 0) {
        badge.textContent = numericCount > 9 ? "9+" : String(numericCount);
        badge.classList.replace('hidden', 'flex');
    } else {
        badge.classList.replace('flex', 'hidden');
    }
};

export const decrementBadgeCount = () => {
    const badge = document.getElementById('badgeCount');
    if (!badge) return;

    const actualCount = Number(badge.getAttribute('data-count') || 0);

    if (actualCount > 0) {
        updateBadgeCount(actualCount - 1);
    }
};

export const notificationToggle = (btn: HTMLElement) => {
    const dropdown = btn.parentElement?.querySelector('.btn-notification') as HTMLElement;

    if (!dropdown) return;

    const isHidden = dropdown.classList.contains('hidden');
    if (isHidden) {
        dropdown.classList.replace('hidden', 'flex');
        changeTab('ALARM');
    } else {
        dropdown.classList.replace('flex', 'hidden');
    }
}

export const changeTab = (type: 'ALARM' | 'NOTICE') => {
    updateTabUI(type);
    loadNotificationTab(type);
}

export const loadNotificationTab = async (type: 'ALARM' | 'NOTICE') => {
    const container = document.querySelector('.btn-notification .notification-list');
    const header = document.querySelector('.noti-header') as HTMLElement;
    const [readAllBtn, viewAllBtn] = header.querySelectorAll('button');
    if (!container) return;

    switch (type) {
        case 'ALARM':
            readAllBtn.classList.remove('hidden');
            viewAllBtn.classList.add('hidden');
            break;
        case 'NOTICE':
            readAllBtn.classList.add('hidden');
            viewAllBtn.classList.remove('hidden');
            break;
    }

    updateTabUI(type);

    try {
        const url = `/api/v1/notifications?type=${type}`;
        const response = await sendRequest(url, {
            method: 'GET'
        });
        if (Array.isArray(response) && response.length > 0) {
            container.innerHTML = response.map((item: NotificationItem) => `                            
                   <div class="${type == 'ALARM' ? 'notification-row' : ''} flex flex-col gap-1.5 p-4 border-b border-gray-100 transition-all cursor-pointer 
                        ${item.isRead ? 'bg-gray-50/50' : 'bg-white hover:bg-blue-50/30'}"
                            onclick="handleNotificationClick(event, ${item.notificationId}, '${item.url}', ${item.isRead})">
                        <div class="flex justify-between items-center">
                            <span class="notification-date text-[10px] font-medium ${item.isRead ? 'text-gray-300' : 'text-blue-500'}">
                                ${formatDiffDate(item.createdAt)}
                            </span>
                        </div>
                        <div class="notification-title text-[13px] leading-relaxed break-keep ${item.isRead ? 'text-gray-400' : 'text-gray-800 font-semibold'}">
                            ${item.title}
                        </div>
                   </div>`).join('');

        } else {
            container.innerHTML = `<div class="p-4 text-center text-xs text-gray-500">${type === 'ALARM' ? '새로운 알림이 없습니다.' : '최근 공지사항이 없습니다.'}</div>`;
        }
    } catch (e) {
        container.innerHTML = '<div class="p-4 text-center text-xs text-red-400">데이터를 불러오지 못했습니다.</div>';
    }
};

const updateTabUI = (type: 'ALARM' | 'NOTICE') => {
    const tabs = document.querySelectorAll<HTMLButtonElement>('.btn-notification .btn-tab');
    tabs.forEach(tab => {
        const tabType = tab.dataset.tabType;
        if (tabType === type) {
            tab.classList.add('border-b-2', 'border-black', 'font-bold');
            tab.classList.remove('text-gray-600');
        } else {
            tab.classList.remove('border-b-2', 'border-black', 'font-bold');
            tab.classList.add('text-gray-600');
        }
    });
};


export const closeNotification = () => {
    const dropdown = document.getElementById('notificationDropdown');
    if (dropdown && !dropdown.classList.contains('hidden')) {
        dropdown.classList.add('hidden');
    }
};

export const handleNotificationClick = async (e: Event, notificationId: number | null, url: string, isRead: boolean | null): Promise<void> => {
    if (notificationId != null && !isRead) {
        const target = e.currentTarget as HTMLElement;
        readNotification(target);
        decrementBadgeCount();
    }

    try {
        const separator = url.includes('?') ? '&' : '?';
        const requestUrl = notificationId != null ? `${url}${separator}notificationId=${notificationId}` : url;
        const html = await sendRequest(requestUrl, {method: 'GET'}, 'TEXT');
        const root = document.getElementById('modal-root');
        if (root) {
            root.innerHTML = html;

            const count = await sendRequest('/api/v1/notifications/unread-count', {method: 'GET'});
            updateBadgeCount(count);
        }

    } catch (error) {
        const err = error as ErrorResponse;
        showToast(err.message || "오류가 발생했습니다.", "error");
    }
}

export const readAllNotification = async (): Promise<void> => {
    const container = document.querySelector('.btn-notification .notification-list');
    if (!container) return;
    const rows = container.querySelectorAll<HTMLElement>('.notification-row');
    rows.forEach(item => {
        readNotification(item);
    })
    await sendRequest('/api/v1/notifications/readAll', {method: 'POST'});
    updateBadgeCount(0);
}

const readNotification = (target: HTMLElement) => {

    target.classList.remove('bg-white', 'hover:bg-blue-50/30');
    target.classList.add('bg-gray-50/50');

    const date = target.querySelector('.notification-date');
    if (date) {
        date.classList.replace('text-blue-500', 'text-gray-300');
    }

    const title = target.querySelector('.notification-title');
    if (title) {
        title.classList.remove('text-gray-800', 'font-semibold');
        title.classList.add('text-gray-400');
    }
}

export const openUserNoticeModal = async (): Promise<void> => {
    const url = '/notices/modal/view'
    try {
        const html = await sendRequest(url, {method: 'GET'}, 'TEXT');
        const root = document.getElementById('modal-root');
        if (root) {
            root.innerHTML = html;
        }
    } catch (error) {
        const err = error as ErrorResponse;
        showToast(err.message || "오류가 발생했습니다.", "error");
    }
}
