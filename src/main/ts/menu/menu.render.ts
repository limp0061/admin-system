import {MENU, MenuItem} from "./menu.config.js";
import {ICON_MAP} from "../icons/icon-map.js";

export function renderMenu(targetId: string) {
    const root = document.getElementById(targetId);
    if (!root) return;

    const activeMenuName = getActiveMenuInfo();

    // 재귀 함수: 메뉴 아이템 생성
    function createMenuItem(item: MenuItem): HTMLLIElement {
        const li = document.createElement("li");
        li.className = "flex flex-col";

        // 상위 Div 생성
        const isActive = activeMenuName === item.name;
        const hasActiveChild = item.children?.some(child => child.name === activeMenuName);

        const topDiv = document.createElement("div");
        const activeStyles = (isActive || hasActiveChild)
            ? "text-white font-bold"
            : "text-gray-50";
        const bgStyles = isActive ? "bg-[#3E6187]" : "hover:bg-[#2C3245]";

        topDiv.className = `flex justify-between items-center px-4 py-3 cursor-pointer transition-colors ${activeStyles} ${bgStyles}`;

        // 아이콘 + 이름
        const leftDiv = document.createElement("div");
        leftDiv.className = "flex items-center gap-3";

        if (item.icon) leftDiv.insertAdjacentHTML("beforeend", ICON_MAP[item.icon]());

        const span = document.createElement("span");
        span.textContent = item.name;
        leftDiv.appendChild(span);
        topDiv.appendChild(leftDiv);

        li.appendChild(topDiv);

        let subUl: HTMLUListElement | null = null;
        let arrowDiv: HTMLDivElement | null = null;

        // children 있는 경우
        if (item.children && item.children.length > 0) {
            // 화살표
            arrowDiv = document.createElement("div");
            arrowDiv.insertAdjacentHTML("beforeend", ICON_MAP["arrow"]());
            arrowDiv.className = "transition-transform duration-200";
            topDiv.appendChild(arrowDiv);

            // 하위 메뉴 ul
            subUl = document.createElement("ul");
            subUl.className = "ml-6 mt-2 flex flex-col hidden";

            item.children.forEach((child: any) => {
                subUl!.appendChild(createMenuItem(child));
            });

            li.appendChild(subUl);
        }

        // 클릭 이벤트 처리
        topDiv.addEventListener("click", () => {
            if (item.children && item.children.length > 0 && subUl && arrowDiv) {
                subUl.classList.toggle("hidden");
                arrowDiv.classList.toggle("rotate-90");
            } else if (item.url) {
                // children 없으면 이동
                window.location.href = item.url!;
            }
        });

        return li;
    }

    // 메뉴 렌더링
    MENU.forEach(item => {
        root.appendChild(createMenuItem(item));
    });
}

export function getActiveMenuInfo() {
    const path = window.location.pathname;

    for (const menu of MENU) {
        if (menu.children) {
            for (const child of menu.children) {
                if (child.url && path === child.url.toLowerCase()) {
                    return child.name;
                }
            }
        }

        if (menu.url && path.startsWith(menu.url)) {
            return menu.name;
        }
    }

    return null;
}