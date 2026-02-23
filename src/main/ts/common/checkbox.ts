// checkbox.ts

/**
 * 전체 체크박스 선택/해제
 */
export const checkToggle = (chk: HTMLElement): void => {
    const input = chk as HTMLInputElement;
    const table = input.closest('table');
    if (!table) return;

    const checkboxes = table.querySelectorAll<HTMLInputElement>(".check-box");

    if (!checkboxes.length) return;

    checkboxes.forEach(check => {
        check.checked = input.checked;
    });
}

export const checkRow = (row: HTMLElement): void => {
    if (row.closest('.check-box')) {
        return;
    }

    const checkbox = row.querySelector<HTMLInputElement>(".check-box");
    if (checkbox) {
        checkbox.checked = !checkbox.checked;
    }
}
