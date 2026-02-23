export interface UserSaveRequest {
    id?: number | null,
    name: string,
    emailId: string,
    password: string | null,
    deptId: number | null,
    userCode: string | null,
    position: string | null,
    gender: string,
    deptCode: string | null,
    userStatus: string,
    roleId: number
}

export interface UserSearchResponse {
    id?: number | null,
    name: string,
    emailId: string,
    deptName: string | null
}