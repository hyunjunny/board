package kr.co.ultari.board.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
public class ImageViewerController {
    @PostMapping("/imgViewer")
    public String imageViewer(Model model, @RequestParam String imgId, final HttpServletRequest request){
        model.addAttribute("img",imgId);
        log.debug(imgId);
        return "/imageViewer";
    }
}
