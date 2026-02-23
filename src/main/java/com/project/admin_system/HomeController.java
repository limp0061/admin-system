package com.project.admin_system;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }

    @GetMapping("/error/{errorCode}")
    public String error(
            @PathVariable(value = "errorCode") String errorCode,
            Model model
    ) {
        if ("403".equals(errorCode)) {
            model.addAttribute("title", "접근 권한이 없습니다.");
            model.addAttribute("message", "상위 관리자에게 권한을 요청하시거나 대시보드로 이동해 주세요.");
        } else if ("404".equals(errorCode)) {
            model.addAttribute("title", "페이지를 찾을 수 없습니다.");
            model.addAttribute("message", "입력하신 주소가 잘못되었거나 삭제된 페이지입니다.");
        } else {
            model.addAttribute("title", "시스템 오류가 발생했습니다.");
            model.addAttribute("message", "잠시 후 다시 시도해 주세요.");
        }
        return "error/error";
    }
}
