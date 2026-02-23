// toast.ts
export const showToast = (message, type) => {
    const container = document.getElementById("toast-container");
    if (!container)
        return;
    let displayMessage = message;
    if (message.trim().startsWith('{')) {
        try {
            const parsed = JSON.parse(message);
            displayMessage = parsed.message || displayMessage;
        }
        catch (e) {
            displayMessage = message;
        }
    }
    const toast = document.createElement("div");
    toast.className = `toast toast-${type}`;
    toast.textContent = displayMessage;
    container.appendChild(toast);
    setTimeout(() => toast.remove(), 3000);
};
