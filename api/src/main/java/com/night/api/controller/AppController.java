package com.night.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.sql.SQLException;
import com.night.api.Database;

@Controller
public class AppController {
    private Database db = new Database();

    @ResponseBody
    @RequestMapping("/test")
    public String test() throws SQLException {
        return db.executeQuery("SELECT uid, pwd FROM account", true);
    }
}
