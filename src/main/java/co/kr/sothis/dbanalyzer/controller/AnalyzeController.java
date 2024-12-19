package co.kr.sothis.dbanalyzer.controller;

import co.kr.sothis.dbanalyzer.dto.Column;
import co.kr.sothis.dbanalyzer.dto.PostgreSqlConnInfo;
import co.kr.sothis.dbanalyzer.service.PostgreSqlService;
import co.kr.sothis.dbanalyzer.util.ObjectToCsv;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/analyze")
@Controller
public class AnalyzeController {

    private final PostgreSqlService postgreSqlService;
    private final ObjectToCsv objectToCsv;

    @GetMapping("/postgre")
    public String postgre(HttpSession session, Model model, String result) {
        PostgreSqlConnInfo postgreSqlConnInfo = (PostgreSqlConnInfo) session.getAttribute("postgreInfo");
        if (postgreSqlConnInfo == null) {
            return "redirect:/conn/postgre?msg=First you need to connect to the PostgreSQL server.";
        }
        return "/analyze/postgre";
    }

    @PostMapping("/postgre")
    public String analyzePostgre(HttpSession session, RedirectAttributes redirectAttributes) {
        PostgreSqlConnInfo postgreSqlConnInfo = (PostgreSqlConnInfo) session.getAttribute("postgreInfo");
        if (postgreSqlConnInfo != null) {
            try {
                List<Column> columnList = postgreSqlService.getColumns(postgreSqlConnInfo);
                String result = objectToCsv.columnToCsv(columnList);
                redirectAttributes.addFlashAttribute("result", result);
                return "redirect:/analyze/postgre";
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "redirect:/404";
    }
}
