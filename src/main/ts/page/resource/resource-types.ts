export interface AccessSaveRequest {
    id?: number | null,
    name: string | null,
    urlPattern: string,
    method: string,
    roleIds: Number[],
    description: string | null
}

export interface RoleSaveRequest {
    id?: number | null,
    roleKey: string,
    roleName: string,
    parentId: number | null,
    isAdmin: boolean
}