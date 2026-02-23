export function formatDiffDate(date: string | Date): string {
    const now = new Date();
    const dateTime = new Date(date);
    const diffSeconds = Math.floor((now.getTime() - dateTime.getTime()) / 1000);
    
    if (diffSeconds < 60) return '방금 전';

    const diffMinutes = Math.floor(diffSeconds / 60);
    if (diffMinutes < 60) return `${diffMinutes}분 전`;

    const diffHours = Math.floor(diffMinutes / 60);
    if (diffHours < 24) return `${diffHours}시간 전`;

    const diffDays = Math.floor(diffHours / 24);
    if (diffDays < 7) return `${diffDays}일 전`;

    return dateTime.toLocaleDateString();
}