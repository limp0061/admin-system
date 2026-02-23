const form = document.getElementById('login-form');
export const handleLogin = () => {
    const usernameInput = document.getElementById('username');
    const passwordInput = document.getElementById('password');
    if (!usernameInput.value.trim()) {
        usernameInput.focus();
        return;
    }
    if (!passwordInput.value.trim()) {
        passwordInput.focus();
        return;
    }
    form.submit();
};
document.addEventListener('DOMContentLoaded', () => {
    const loginBtn = document.getElementById('login-btn');
    loginBtn?.addEventListener('click', handleLogin);
    const inputs = document.querySelectorAll('input.input-group');
    const errorText = document.getElementById('alert-toast');
    if (errorText) {
        inputs.forEach(input => {
            input.addEventListener('input', () => {
                errorText.style.display = 'none';
            });
        });
        if (window.location.search.includes('error=true')) {
            const cleanUrl = window.location.origin + window.location.pathname;
            window.history.replaceState({}, document.title, cleanUrl);
        }
    }
});
form.addEventListener('keypress', function (e) {
    if (e.key === 'Enter') {
        handleLogin();
    }
});
