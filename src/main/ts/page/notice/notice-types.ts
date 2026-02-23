export interface NoticeSaveRequest {
    id?: number | null;
    type: string;
    title: string;
    isRealTimeNoticed: boolean;
    isForce: boolean;
    content: string;
    startAt: string | null;
    endAt: string | null;
}