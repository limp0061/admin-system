import {FieldError} from "../common/type.js";

export const validator = {
    clear: (form: HTMLFormElement) => {
        form.querySelectorAll(".error-msg").forEach(span => {
            span.innerHTML = "";
            span.classList.add("hidden");
        })
    },
    displayErrors: (fieldErrors: FieldError[]) => {
        if (!fieldErrors) return;

        fieldErrors.forEach((err: FieldError) => {
            const errorSpan = document.getElementById(`error-${err.field}`);
            if (errorSpan) {
                errorSpan.textContent = err.reason;
                errorSpan.classList.remove('hidden');
            }
        })
    },
    validateRequired: (data: Record<string, any>, requiredFields: Record<string, {
        label: string,
        type?: 'input' | 'select'
    }>): FieldError[] => {
        const errors: FieldError[] = [];

        Object.keys(requiredFields).forEach(field => {
            const value = data[field];
            const {label, type} = requiredFields[field];

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
}
