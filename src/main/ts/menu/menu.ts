import {renderMenu, getActiveMenuInfo} from "./menu.render.js";

document.addEventListener("DOMContentLoaded", () => {
    renderMenu("sidebar-menu");

    const menuInfo = getActiveMenuInfo();
    if (!menuInfo) return;

    // header 타이틀
    const headerTitle = document.getElementById("page-title");
    if (headerTitle) {
        headerTitle.textContent = menuInfo;
    }

    // nav 섹션명 (예: "사용자")
    const navSection = document.getElementById("nav-section-title");
    if (navSection) {
        navSection.textContent = menuInfo;
    }
});