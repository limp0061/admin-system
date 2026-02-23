export const MENU = [
    { name: "대시보드", icon: "dashboard", url: "/dashboard" },
    {
        name: "사용자",
        icon: "user",
        collapsed: true,
        children: [
            { name: "회원 관리", url: "/users" },
            { name: "가입 승인", url: "/users/approvals" },
            { name: "삭제 관리", url: "/users/deleted" }
        ]
    },
    {
        name: "부서",
        icon: "dept",
        collapsed: true,
        children: [
            { name: "구성원 관리", url: "/depts" },
            { name: "부서 트리/관리", url: "/depts/tree" }
        ]
    },
    { name: "관리자", icon: "admin", url: "/admins" },
    {
        name: "리소스",
        icon: "resource",
        collapsed: true,
        children: [
            { name: "접근 제어 관리", url: "/resources/access" },
            { name: "권한 계층 관리", url: "/resources/role" }
        ]
    },
    { name: "공지", icon: "notice", url: "/notices" },
    {
        name: "로그",
        icon: "log",
        collapsed: true,
        children: [
            { name: "감사 로그", url: "/logs/admin" },
            { name: "활동 로그", url: "/logs/user" }
        ]
    },
    {
        name: "통계",
        icon: "statistic",
        collapsed: true,
        children: [
            { name: "로그인 통계", url: "/statistics/login" },
            { name: "시스템 통계", url: "/statistics/system" }
        ]
    }
];
