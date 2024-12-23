package co.kr.sothis.dbanalyzer.controller;

import co.kr.sothis.dbanalyzer.dto.*;
import co.kr.sothis.dbanalyzer.service.PostgreSqlService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

@RequiredArgsConstructor
@Controller
@RequestMapping("/query")
public class QueryController {

    private final PostgreSqlService postgreSqlService;

    @GetMapping("/postgre")
    public String postgreQuery(Model model, HttpSession session) {
        PostgreSqlConnInfo postgreSqlConnInfo = (PostgreSqlConnInfo) session.getAttribute("postgreInfo");
        if (postgreSqlConnInfo == null) {
            return "redirect:/conn/postgre?title=Invalid!&msg=First you need to connect to the PostgreSQL server.";
        }
        try {
            model.addAttribute("tableList", postgreSqlService.getTableList(postgreSqlConnInfo));
        } catch (SQLException e) {
            e.printStackTrace();
            model.addAttribute("msg", MessageContainer.builder().title("Error!").msg("Failed to load schema information.").build());
        }
        return "/query/postgre";
    }

    @ResponseBody
    @PostMapping("/postgre")
    public QueryResult postgreQuery(@RequestBody QueryInput queryInput, Model model, HttpSession session) {
        PostgreSqlConnInfo postgreSqlConnInfo = (PostgreSqlConnInfo) session.getAttribute("postgreInfo");

        System.out.println(queryInput.getQuery());
        if (postgreSqlConnInfo != null) {
            try {
                return postgreSqlService.executeQuery(postgreSqlConnInfo, queryInput);
            } catch (SQLException e) {
                e.printStackTrace();
                return QueryResult.builder().error(e.getMessage()).build();
            }
        }
        return null;
    }

    @ResponseBody
    @PostMapping("/postgre/tableinfo")
    public TableInfo postgreTableInfo(@RequestBody TableInfo tableInfo, Model model, HttpSession session) {
        PostgreSqlConnInfo postgreSqlConnInfo = (PostgreSqlConnInfo) session.getAttribute("postgreInfo");

        if (postgreSqlConnInfo != null) {
            try {
                return postgreSqlService.getTableInfo(postgreSqlConnInfo, tableInfo);
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }
}
