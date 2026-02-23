export interface DeptSaveRequest {
    id?: number | null;
    deptCode: string;
    deptName: string;
    upperDeptId: number | null;
    sortOrder: number;
    isActive: boolean;
    mode?: string;
}