declare const flatpickr: any;

export const defaultFlatpickrOptions = {

    locale: (window as any).flatpickr.l10ns.ko,
    enableTime: true,
    time_24hr: true,
    // 서버 전송용 포맷 (LocalDateTime이 인식하기 가장 좋은 ISO 유사 포맷)
    dateFormat: "Y-m-dTH:i:S",
    // 사용자에게 보여줄 포맷 (화면에는 예쁘게 보임)
    altInput: true,
    altFormat: "Y-m-d H:i",
    disableMobile: true,
    minDate: "today",
    monthSelectorType: 'static',
};

export const initDateTimePicker = (selector: string, options: any = {}) => {
    return flatpickr(selector, {
        ...defaultFlatpickrOptions,
        ...options, // 호출하는 곳에서 mode를 결정할 수 있게 함
    });
};