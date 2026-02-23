// event.ts
import { closeModal } from "./modal.js";
import { closeDropdown } from "./select.js";
export const initGlobalEvents = () => {
    /**
     * ESC 키 감지 이벤트 (모달 닫기)
     */
    document.addEventListener("keydown", (e) => {
        if (e.key !== "Escape")
            return;
        const root = document.getElementById("modal-root");
        // 모달 내용이 있을 때만 닫기 실행
        if (root && root.innerHTML.trim() !== "") {
            closeModal();
        }
    });
    /**
     * modal-form 스크롤 시 Select-ul 닫기
     */
    document.addEventListener('scroll', (e) => {
        const target = e.target;
        if (!(target instanceof HTMLElement)) {
            return;
        }
        if (target.closest('#common-select-ul')) {
            return;
        }
        if (target.closest('.modal-form')) {
            closeDropdown();
        }
    }, true);
    /**
     * modal 밖에 클리스 Select-ul 닫기
     * Theme 변경
     */
    document.addEventListener('click', (e) => {
        const target = e.target;
        const themeBtn = target.closest('[data-theme]');
        if (themeBtn) {
            const theme = themeBtn.dataset.theme;
            if (theme === 'dark') {
                document.documentElement.classList.add('dark');
                localStorage.setItem('theme', 'dark');
            }
            else {
                document.documentElement.classList.remove('dark');
                localStorage.setItem('theme', 'light');
            }
            return;
        }
        const fontSizeBtn = target.closest('[data-font-size]');
        if (fontSizeBtn) {
            const tables = document.querySelectorAll('.table');
            const fontSize = fontSizeBtn.dataset.fontSize;
            if (tables.length > 0 && fontSize) {
                document.documentElement.style.setProperty('--table-font-size', fontSize);
                localStorage.setItem('table-font-size', fontSize);
            }
            const dropDown = target.closest('.btn-dropDown');
            if (dropDown)
                dropDown.classList.add('hidden');
            return;
        }
        const isIgnore = target.closest('.ignore-close') || themeBtn;
        if (!isIgnore) {
            closeDropdown();
        }
    });
};
