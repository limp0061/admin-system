export type HttpMethod = "POST" | "GET" | "DELETE" | "PUT" | "PATCH";

export interface requestConfig {
    method: HttpMethod,
    body?: any,
    headers?: Record<string, string>;
}

export const sendRequest = async (url: string, options: requestConfig, responseType: 'JSON' | 'TEXT' = 'JSON') => {
    const response = await fetch(url, {
        method: options.method,
        headers: {
            "Content-Type": "application/json",
            ...options.headers
        },
        body: options.body ? JSON.stringify(options.body) : null,
    })

    if (!response.ok) {
        const errorData = await response.text();
        try {
            throw JSON.parse(errorData);
        } catch {
            throw {message: errorData || "서버 오류가 발생했습니다."};
        }
    }

    const text = await response.text();

    if (responseType === 'TEXT') {
        return text;
    }
    return text ? JSON.parse(text) : null;
}

export const sendMultipartRequest = async (
    url: string,
    options: any,
    responseType: 'JSON' | 'TEXT' = 'JSON'
) => {
    const response = await fetch(url, {
        method: options.method,
        headers: {
            ...options.headers
        },
        body: options.body as FormData,
    });

    if (!response.ok) {
        const errorData = await response.text();
        try {
            throw JSON.parse(errorData);
        } catch {
            throw {message: errorData || "서버 오류가 발생했습니다."};
        }
    }

    const text = await response.text();
    if (responseType === 'TEXT') return text;
    return text ? JSON.parse(text) : null;
}