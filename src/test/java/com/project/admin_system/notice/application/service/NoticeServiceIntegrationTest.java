package com.project.admin_system.notice.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.project.admin_system.common.annotation.IntegrationTest;
import com.project.admin_system.common.service.S3StorageManager;
import com.project.admin_system.file.domain.DomainType;
import com.project.admin_system.file.domain.File;
import com.project.admin_system.file.domain.FileRepository;
import com.project.admin_system.file.domain.FileStatus;
import com.project.admin_system.notice.application.dto.NoticeSaveRequest;
import com.project.admin_system.notice.domain.Notice;
import com.project.admin_system.notice.domain.NoticeRepository;
import com.project.admin_system.notice.domain.NoticeType;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@Transactional
class NoticeServiceIntegrationTest {

    @Autowired
    private NoticeService noticeService;
    @MockitoBean
    private S3StorageManager storageManager;
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private EntityManager em;

    private Long tempFileId;

    @BeforeEach
    void init() {
        File temp = File.builder()
                .originalName("테스트 파일")
                .storedFileName("test_file")
                .filePath("temp/test_file.jpg")
                .fileSize(1234L)
                .contentType("image/jpeg")
                .domainType(DomainType.NOTICE)
                .domainId(null)
                .status(FileStatus.TEMP)
                .build();

        fileRepository.save(temp);

        em.flush();
        em.clear();

        tempFileId = temp.getId();
    }

    @Test
    @DisplayName("공지사항 저장 시 파일이동")
    void saveNotice_success() throws Exception {

        //given
        String title = "공지사항 테스트_" + LocalDateTime.now();
        String htmlContent =
                "<p>테스트 입니다.</p><p><img class=\"image_resized\" style=\"aspect-ratio:413/531;width:33.17%;\" src=\"/api/v1/files/viewer/"
                        + tempFileId + "\" width=\"413\" height=\"531\"></p>";
        LocalDateTime now = LocalDateTime.now();
        NoticeSaveRequest request = new NoticeSaveRequest(
                null, NoticeType.NORMAL, title, true,
                false, htmlContent, now, now.plusDays(7)
        );

        //when
        noticeService.saveNotice(request);

        em.flush();
        em.clear();

        //then
        verify(storageManager, times(1)).moveObject(anyString(), anyString());

        Notice savedNotice = noticeRepository.findByTitle(title);
        assertThat(savedNotice.getTitle()).isEqualTo(title);
        assertThat(savedNotice.getContent()).isEqualTo(htmlContent);
        assertThat(savedNotice.getType()).isEqualTo(NoticeType.NORMAL);
    }

    @Test
    @DisplayName("공지사항 수정 : 이미지 파일 교체 시 기존 파일 삭제 처리")
    void updateNotice_success() throws Exception {

        //given
        LocalDateTime now = LocalDateTime.now();
        String htmlContent =
                "<p>테스트 입니다.</p><p><img class=\"image_resized\" style=\"aspect-ratio:413/531;width:33.17%;\" src=\"/api/v1/files/viewer/"
                        + tempFileId + "\" width=\"413\" height=\"531\"></p>";
        Notice notice = Notice.builder()
                .title("공지사항 테스트")
                .content(htmlContent)
                .isForce(false)
                .isRealtimeNotified(false)
                .startAt(now)
                .type(NoticeType.NORMAL)
                .endAt(now.plusDays(7))
                .build();
        noticeRepository.save(notice);

        File file = fileRepository.findById(tempFileId).orElseThrow();
        file.finalizeFile("newPath", notice.getId());

        File temp2 = File.builder()
                .originalName("테스트 파일2")
                .storedFileName("test2_file")
                .filePath("temp/test2_file.jpg")
                .fileSize(1234L)
                .contentType("image/jpeg")
                .domainType(DomainType.NOTICE)
                .domainId(null)
                .status(FileStatus.TEMP)
                .build();

        fileRepository.save(temp2);

        em.flush();
        em.clear();

        String title = "공지사항 테스트_" + now;
        String newHtmlContent =
                "<p>테스트2 입니다.</p><p><img class=\"image_resized\" style=\"aspect-ratio:413/531;width:33.17%;\" src=\"/api/v1/files/viewer/"
                        + temp2.getId() + "\" width=\"413\" height=\"531\"></p>";
        NoticeSaveRequest request = new NoticeSaveRequest(
                notice.getId(), NoticeType.NORMAL, title, false,
                false, newHtmlContent, now, now.plusDays(7)
        );

        //when
        noticeService.saveNotice(request);

        em.flush();
        em.clear();

        //then
        verify(storageManager, times(1)).moveObject(anyString(), anyString());

        Notice updatedNotice = noticeRepository.findById(notice.getId()).orElseThrow();
        assertThat(updatedNotice.getContent()).isEqualTo(newHtmlContent);

        File oldFile = fileRepository.findById(tempFileId).orElseThrow();
        assertThat(oldFile.getStatus()).isEqualTo(FileStatus.DELETED);
        assertThat(oldFile.getDeletedAt()).isNotNull();

        File newFile = fileRepository.findById(temp2.getId()).orElseThrow();
        assertThat(newFile.getStatus()).isEqualTo(FileStatus.STORAGE);
    }


    @Test
    @DisplayName("공지사항 삭제 및 파일 삭제 처리")
    void deleteNotice_success() throws Exception {

        //given
        LocalDateTime now = LocalDateTime.now();
        String htmlContent =
                "<p>테스트 입니다.</p><p><img class=\"image_resized\" style=\"aspect-ratio:413/531;width:33.17%;\" src=\"/api/v1/files/viewer/"
                        + tempFileId + "\" width=\"413\" height=\"531\"></p>";
        Notice notice = Notice.builder()
                .title("공지사항 테스트")
                .content(htmlContent)
                .isForce(false)
                .isRealtimeNotified(false)
                .startAt(now)
                .type(NoticeType.NORMAL)
                .endAt(now.plusDays(7))
                .build();
        noticeRepository.save(notice);

        File file = fileRepository.findById(tempFileId).orElseThrow();
        file.finalizeFile("newPath", notice.getId());

        em.flush();
        em.clear();

        //when
        noticeService.deleteNotice(List.of(notice.getId()));

        em.flush();
        em.clear();

        //then
        File deleteFile = fileRepository.findById(tempFileId).orElseThrow();
        assertThat(noticeRepository.findById(notice.getId())).isEmpty();
        assertThat(deleteFile.getDeletedAt()).isNotNull();
        assertThat(deleteFile.getStatus()).isEqualTo(FileStatus.DELETED);
    }
}