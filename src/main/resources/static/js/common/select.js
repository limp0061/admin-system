// select.ts
import { showToast } from "./toast.js";
export const closeDropdown = (ownElement) => {
    // 1. 기존 공통 셀렉트 메뉴 닫기
    const commonUl = document.getElementById('common-select-ul');
    if (commonUl && commonUl != ownElement) {
        commonUl.classList.add('hidden');
        document.querySelectorAll('[data-action="toggleSelect"] svg').forEach(svg => {
            svg.classList.remove('rotate-180');
        });
    }
    // 2. 클래스 기반 드롭다운 메뉴(.btn-dropDown) 모두 닫기
    const statusMenus = document.querySelectorAll('.btn-dropDown');
    statusMenus.forEach(menu => {
        if (!ownElement) {
            menu.classList.replace('flex', 'hidden');
            return;
        }
        if (menu === ownElement || menu.contains(ownElement)) {
            return;
        }
        menu.classList.replace('flex', 'hidden');
        menu.classList.add('hidden');
    });
};
/**
 * Select 메뉴 토글
 */
export const toggleSelect = (btn, e) => {
    e.preventDefault();
    e.stopPropagation();
    const commonUl = document.getElementById('common-select-ul');
    const template = btn.parentElement?.querySelector(".data-template");
    const svg = btn.querySelector('svg');
    if (!commonUl.classList.contains('hidden') && commonUl.dataset.owner === btn.id) {
        commonUl.classList.add('hidden');
        svg?.classList.remove('rotate-180');
        return;
    }
    document.querySelectorAll('[data-action="toggleSelect"] svg').forEach(s => s.classList.remove('rotate-180'));
    commonUl.innerHTML = template.innerHTML;
    commonUl.dataset.owner = btn.id;
    const rect = btn.getBoundingClientRect();
    commonUl.style.top = `${rect.bottom}px`;
    commonUl.style.left = `${rect.left}px`;
    commonUl.style.width = `${rect.width}px`;
    commonUl.classList.remove('hidden');
    svg?.classList.add('rotate-180');
    const isMulti = btn.dataset.multi;
    const badgeContainerId = btn.dataset.target;
    commonUl.onclick = (liEvent) => {
        const li = liEvent.target.closest('.select-li');
        if (!li)
            return;
        if (isMulti) {
            const badgeContainer = document.getElementById(badgeContainerId);
            addBadge(badgeContainer, li);
        }
        else {
            clickOption(li);
        }
    };
};
/**
 * 옵션 클릭 시 값 반영
 */
export const clickOption = (li) => {
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
        const span = displayBtn.querySelector('span');
        const svg = displayBtn.querySelector('svg');
        if (span) {
            span.textContent = text;
        }
        else {
            displayBtn.innerText = text;
        }
        if (svg) {
            svg.classList.remove('rotate-180');
        }
        if (val) {
            displayBtn.removeAttribute('data-is-placeholder');
        }
        else {
            displayBtn.setAttribute('data-is-placeholder', 'true');
        }
    }
    document.querySelectorAll('.select-ul').forEach(ul => ul.classList.add('hidden'));
};
export const addBadge = (container, li) => {
    const val = li.dataset.value;
    const text = li.textContent?.trim() || "";
    const currentRoles = Array.from(container.querySelectorAll('.badge-value')).map(el => el.value);
    if (currentRoles.includes(val)) {
        showToast("이미 등록되어 있습니다.", "error");
        return;
    }
    const html = `
         <div class="flex items-center gap-1.5 w-fit bg-white text-slate-700 px-2.5 py-1.5 rounded-md border border-gray-200 shadow-sm">
            <input type="hidden" class="badge-value" value="${val}">
            <span class="badge-text text-xs font-semibold">${text}</span>
            <button type="button" class="text-gray-400 hover:text-red-500 transition-colors"
                    onclick="this.parentElement.remove()">
                <svg xmlns="http://www.w3.org/2000/svg" class="size-3.5" fill="none"
                     viewBox="0 0 24 24"
                     stroke="currentColor" stroke-width="2.5">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M6 18L18 6M6 6l12 12"/>
                </svg>
            </button>
        </div>
    `;
    container.insertAdjacentHTML('beforeend', html);
};
export const btnDropDown = (btn, e) => {
    e.stopPropagation();
    const dropDown = btn.nextElementSibling;
    if (!dropDown || !dropDown.classList.contains('btn-dropDown'))
        return;
    const isHidden = dropDown.classList.contains('hidden');
    const isNested = btn.closest('.btn-dropDown') !== null;
    closeDropdown(isNested ? dropDown : undefined);
    if (isHidden) {
        closeDropdown(dropDown);
        dropDown.classList.replace('hidden', 'flex');
        dropDown.classList.remove('hidden');
    }
    else {
        dropDown.classList.replace('flex', 'hidden');
        dropDown.classList.add('hidden');
    }
};
