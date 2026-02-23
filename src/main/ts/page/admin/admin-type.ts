export interface AdminRoleRequest {
    id?: number | null
    roleId: number,
    ips: string[]
}