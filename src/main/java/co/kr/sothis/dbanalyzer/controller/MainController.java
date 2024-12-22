package co.kr.sothis.dbanalyzer.controller;

import co.kr.sothis.dbanalyzer.vo.PostgreSqlQuery;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequiredArgsConstructor
@Controller
public class MainController {

    private final PostgreSqlQuery postgreSqlQuery;

    @RequestMapping("/")
    public String index(HttpSession session, Model model) {
        if (session.getAttribute("postgreInfo") == null && session.getAttribute("mariadbInfo") == null) {
            return "redirect:/conn/postgre?title=Error!&msg=You must connect to at least one database server.";
        } else {
            return "index";
        }
    }
}
