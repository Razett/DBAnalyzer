package co.kr.sothis.dbanalyzer.controller;

import co.kr.sothis.dbanalyzer.dto.MariaDbConnInfo;
import co.kr.sothis.dbanalyzer.dto.MessageContainer;
import co.kr.sothis.dbanalyzer.dto.PostgreSqlConnInfo;
import co.kr.sothis.dbanalyzer.util.conn.MariaDbConnector;
import co.kr.sothis.dbanalyzer.util.conn.PostgreSqlConnector;
import com.zaxxer.hikari.pool.HikariPool;
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
    public String connectPostgre(Model model, MessageContainer msg) {
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
                return "redirect:/conn/postgre?title=Error!&msg=Failed to connect to PostgreSQL Server. Please check the connection information and try again.";
            }
        } catch (HikariPool.PoolInitializationException e) {
            return "redirect:/conn/postgre?title=Error!&msg=Failed to initialize pool.";
        }
        catch (Exception e) {
            e.printStackTrace();
            return "redirect:/conn/postgre?title=Error&msg=" + e.getMessage();
        }
    }

    @GetMapping("/mariadb")
    public String connectMariaDB(Model model, MessageContainer msg) {
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
                return "redirect:/conn/mariadb?title=Error!&msg=Failed to connect to MariaDB Server. Please check the connection information and try again.";
            }
        } catch (HikariPool.PoolInitializationException e) {
            return "redirect:/conn/mariadb?title=Error!&msg=Failed to initialize pool.";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/conn/mariadb?msg=" + e.getMessage();
        }
    }

    @GetMapping("/disconn/postgre")
    public String disconnectPostgre(Model model, HttpSession session) {
        session.removeAttribute("postgreInfo");

        return "redirect:/?title=Disconnected!&msg=Disconnected from PostgreSQL Server.";
    }

    @GetMapping("/disconn/mariadb")
    public String disconnectMariaDB(Model model, HttpSession session) {
        session.removeAttribute("mariadbInfo");

        return "redirect:/?title=Disconnected!&msg=Disconnected from MariaDB Server.";
    }

}
