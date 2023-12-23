package com.night.api.Account;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.sql.SQLException;
import com.fasterxml.jackson.databind.JsonNode;
import com.night.api.Database;
import com.night.api.Response;

@Controller
public class AccountController {
    @ResponseBody
    @RequestMapping(value="/login", method=RequestMethod.POST)
    public String login(@RequestBody JsonNode request) throws Exception {
        try {
            if (request.has("sessionID"))
                return Authn.validateSession(request.get("sessionID").asText()).toString();

            if (request.has("uid") && request.has("pwd")) {
                return Authn.login(
                    request.get("uid").asText(), request.get("pwd").asText()).toString();
            }

            return new Response(400, "Invalid request body.").toString();
        }
        catch (SQLException e) {
            return new Response(500, "Internal Server Error.").toString();
        }
    }

    @ResponseBody
    @RequestMapping(value="/register", method=RequestMethod.POST)
    public String register(@RequestBody JsonNode request) {
        if (request.has("uid") && request.has("pwd")) {
            final String UID = request.get("uid").asText(), PWD = request.get("pwd").asText();
            
            try {
                if (Database.isUserExisted(UID))
                    return new Response(409, "Existed Username.").toString();

                final String QUERY = "INSERT INTO account (uid, pwd) VALUES (\"%s\", \"%s\")";
                Database.executeQuery(String.format(QUERY, UID, PWD));
                return new Response(201, "Account created.").toString();
            }
            catch (SQLException e) {
                return new Response(500, "Internal Server Error.").toString();
            }
        }
        return new Response(400, "Invalid request body.").toString();
    }
}
