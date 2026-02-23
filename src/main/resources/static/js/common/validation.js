export const validator = {
    clear: (form) => {
        form.querySelectorAll(".error-msg").forEach(span => {
            span.innerHTML = "";
            span.classList.add("hidden");
        });
    },
    displayErrors: (fieldErrors) => {
        if (!fieldErrors)
            return;
        fieldErrors.forEach((err) => {
            const errorSpan = document.getElementById(`error-${err.field}`);
            if (errorSpan) {
                errorSpan.textContent = err.reason;
                errorSpan.classList.remove('hidden');
            }
        });
    },
    validateRequired: (data, requiredFields) => {
        const errors = [];
        Object.keys(requiredFields).forEach(field => {
            const value = data[field];
            const { label, type } = requiredFields[field];
            if (value === null || value === undefined || (typeof value === "string" && value.trim() === "")) {
                const action = type === 'select' ? '선택해주세요' : '필수 정보입니다.';
                errors.push({
                    field: field,
                    reason: `${label}: ${action}`
                });
            }
        });
        return errors;
    }
};
