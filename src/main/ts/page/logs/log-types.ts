export interface AuditRequest {
    action?: string | null,
    targetEntity?: string | null,
    startAt?: string | null
    endAt?: string | null
}