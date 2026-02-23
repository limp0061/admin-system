export interface FieldError {
    field: string;
    reason: string;
}

export interface ErrorResponse {
    message: string;
    status: number;
    code: string;
    fieldErrors?: FieldError[];
}

export interface ModalConfig {
    url: string;
    param?: string;
    value?: string | number | string[] | number[] | null;
}
