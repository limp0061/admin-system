declare const flatpickr: any;

export const defaultFlatpickrOptions = {
  locale: (window as any).flatpickr.l10ns.ko,
  enableTime: true,
  time_24hr: true,
  // 서버 전송용 포맷 (LocalDateTime이 인식하기 가장 좋은 ISO 유사 포맷)
  dateFormat: 'Y-m-dTH:i:S',
  // 사용자에게 보여줄 포맷 (화면에는 예쁘게 보임)
  altInput: true,
  altFormat: 'Y-m-d H:i',
  disableMobile: true,
  minDate: 'today',
  monthSelectorType: 'static',
  appendTo: document.body,
  onOpen: (selectedDates: any, dateStr: any, instance: any) => {
    const inputEl = (
      instance.altInput || instance.element
    ).getBoundingClientRect();
    const cal = instance.calendarContainer;
    const calHeight = cal.offsetHeight;
    const spaceBelow = window.innerHeight - inputEl.bottom;

    cal.style.position = 'fixed';
    cal.style.left = `${inputEl.left}px`;

    // 아래 공간 부족하면 위로
    if (spaceBelow < calHeight) {
      cal.style.top = `${inputEl.top - calHeight - 5}px`;
    } else {
      cal.style.top = `${inputEl.bottom + 5}px`;
    }
  },
};

export const initDateTimePicker = (selector: string, options: any = {}) => {
  return flatpickr(selector, {
    ...defaultFlatpickrOptions,
    ...options, // 호출하는 곳에서 mode를 결정할 수 있게 함
  });
};
