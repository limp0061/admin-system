// paginationUtils.ts
export class PaginationUtils {
    static onUpdate = (url) => window.location.href = url;
    static baseUrl = null;
    static setUpdateHandler(handler) {
        this.onUpdate = handler;
    }
    static setBaseUrl(url) {
        this.baseUrl = url;
    }
    /**
     * 특정 페이지로 이동
     */
    static goToPage(page) {
        const base = PaginationUtils.baseUrl || window.location.href;
        const url = new URL(base, window.location.origin);
        url.searchParams.set('page', page.toString());
        PaginationUtils.onUpdate(url.toString());
    }
    /**
     * 첫 페이지로 이동
     */
    static goToFirstPage() {
        PaginationUtils.goToPage(0);
    }
    /**
     * 마지막 페이지로 이동
     */
    static goToLastPage(totalPages) {
        const lastPage = totalPages > 0 ? totalPages - 1 : 0;
        PaginationUtils.goToPage(lastPage);
    }
    /**
     * 이전 페이지로 이동
     */
    static goToPrevPage(currentPage) {
        if (currentPage > 0) {
            PaginationUtils.goToPage(currentPage - 1);
        }
    }
    /**
     * 다음 페이지로 이동
     */
    static goToNextPage(currentPage, totalPages) {
        if (currentPage < totalPages - 1) {
            PaginationUtils.goToPage(currentPage + 1);
        }
    }
    /**
     * 목록 크기 변경
     */
    static changeSize = (sizeValue) => {
        const base = PaginationUtils.baseUrl || window.location.href;
        const url = new URL(base, window.location.origin);
        url.searchParams.set('page', "0");
        url.searchParams.set('size', sizeValue.toString());
        PaginationUtils.onUpdate(url.toString());
    };
    static filterStatus = (status) => {
        const base = PaginationUtils.baseUrl || window.location.href;
        const url = new URL(base, window.location.origin);
        url.searchParams.set('page', "0");
        url.searchParams.set('status', status);
        PaginationUtils.onUpdate(url.toString());
    };
    static applyFilter(params) {
        const base = PaginationUtils.baseUrl || window.location.href;
        const url = new URL(base, window.location.origin);
        url.searchParams.set('page', "0");
        Object.keys(params).forEach(key => {
            const value = params[key];
            if (value !== null && value !== undefined && value !== '') {
                url.searchParams.set(key, value.toString());
            }
            else {
                url.searchParams.delete(key);
            }
        });
        PaginationUtils.onUpdate(url.toString());
    }
}
