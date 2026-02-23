package com.project.admin_system.common.service;

import static com.project.admin_system.file.domain.utils.FilePathUtils.IMAGE_VIEWER;

import com.project.admin_system.file.application.dto.FileResponse;
import com.project.admin_system.file.application.service.FileService;
import com.project.admin_system.notice.application.service.NoticeService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
public class ParseService {

    public List<Long> extractFileIdsFromHtml(String html) {
        Document doc = Jsoup.parse(html);
        Elements images = doc.select("img");

        List<Long> fileIds = new ArrayList<>();
        for (Element img : images) {
            String src = img.attr("src");

            if (src.contains(IMAGE_VIEWER)) {
                String fileId = src.substring(src.lastIndexOf("/") + 1);
                fileIds.add(Long.parseLong(fileId));
            }
        }
        return fileIds;
    }
}
