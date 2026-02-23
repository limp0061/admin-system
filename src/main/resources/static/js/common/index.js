// index.ts
import { PaginationUtils } from "./pagination.js";
import { closeModal } from "./modal.js";
import { toggleSelect, btnDropDown } from "./select.js";
import { showToast } from "./toast.js";
import { checkToggle, checkRow } from "./checkbox.js";
import { initGlobalEvents } from "./event.js";
import { notificationToggle, changeTab, initNotificationSSE, closeNotification, handleNotificationClick, readAllNotification, openUserNoticeModal } from "./notification.js";
import { previewImage } from "./upload.js";
const exposeToWindow = () => {
    const w = window;
    // 페이지네이션
    w.goToPage = (page) => PaginationUtils.goToPage(page);
    w.goToFirstPage = () => PaginationUtils.goToFirstPage();
    w.goToLastPage = (total) => PaginationUtils.goToLastPage(total);
    w.goToPrevPage = (current) => PaginationUtils.goToPrevPage(current);
    w.goToNextPage = (current, total) => PaginationUtils.goToNextPage(current, total);
    w.changeSize = (size) => PaginationUtils.changeSize(size);
    w.filterStatus = (status) => PaginationUtils.filterStatus(status);
    // 모달 및 알림
    w.closeModal = closeModal;
    w.showToast = showToast;
    // UI 컴포넌트
    w.toggleSelect = toggleSelect;
    w.btnDropDown = btnDropDown;
    w.checkToggle = checkToggle;
    w.checkRow = checkRow;
    // 알림
    w.notificationToggle = notificationToggle;
    w.changeTab = changeTab;
    w.handleNotificationClick = handleNotificationClick;
    w.readAllNotification = readAllNotification;
    w.openUserNoticeModal = openUserNoticeModal;
    // 업로드
    w.previewImage = previewImage;
};
const initEventListeners = () => {
    initGlobalEvents();
    initNotificationSSE();
    document.addEventListener('click', (event) => {
        const target = event.target;
        const notificationBox = document.getElementById('notificationDropdown');
        const toggleBtn = document.getElementById('notificationBtn');
        if (notificationBox && !notificationBox.contains(target) &&
            toggleBtn && !toggleBtn.contains(target)) {
            closeNotification();
        }
    });
    // 폰트 사이즈 복구
    const savedFontSize = localStorage.getItem('table-font-size');
    if (savedFontSize) {
        document.documentElement.style.setProperty('--table-font-size', savedFontSize);
    }
    // 뒤로가기/앞으로가기 시 페이지 갱신
    window.addEventListener('popstate', () => {
        window.location.reload();
    });
};
exposeToWindow();
initEventListeners();
