// modal.ts
/**
 * 모달 닫기
 */
export const closeModal = (): void => {
    const root = document.getElementById("modal-root");
    if (root) root.innerHTML = "";

    document.body.style.overflow = "auto";
};