package co.kr.sothis.dbanalyzer.controller;

import co.kr.sothis.dbanalyzer.dto.MariaDbConnInfo;
import co.kr.sothis.dbanalyzer.dto.PostgreSqlConnInfo;
import co.kr.sothis.dbanalyzer.util.conn.MariaDbConnector;
import co.kr.sothis.dbanalyzer.util.conn.PostgreSqlConnector;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/conn")
public class DbConnenctController {

    private final PostgreSqlConnector postgreSqlConnector;
    private final MariaDbConnector mariaDbConnector;

    @GetMapping("/postgre")
    public String connectPostgre(Model model, String msg) {
        model.addAttribute("msg", msg);
        return "/conn/postgre_form";
    }

    @PostMapping("/postgre")
    public String connectPostgre(PostgreSqlConnInfo info, Model model, HttpSession session) {
        try {
            if (postgreSqlConnector.getConnection(info) != null) {
                session.removeAttribute("postgreInfo");
                session.setAttribute("postgreInfo", info);
                return "redirect:/conn/mariadb";
            } else {
                return "redirect:/conn/postgre?msg=연결실패";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/conn/postgre?msg=" + e.getMessage();
        }
    }

    @GetMapping("/mariadb")
    public String connectMariaDB(Model model, String msg) {
        model.addAttribute("msg", msg);
        return "/conn/mariadb_form";
    }

    @PostMapping("/mariadb")
    public String connectMariaDB(MariaDbConnInfo info, Model model, HttpSession session) {
        try {
            if (mariaDbConnector.getConnection(info) != null) {
                session.removeAttribute("mariadbInfo");
                session.setAttribute("mariadbInfo", info);
                return "redirect:/";
            } else {
                return "redirect:/conn/mariadb?msg=연결실패";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/conn/mariadb?msg=" + e.getMessage();
        }
    }
}
