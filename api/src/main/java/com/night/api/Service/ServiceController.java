package com.night.api.Service;

import java.sql.SQLException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.night.api.Response;

@Controller
public class ServiceController {

    @ResponseBody
    @RequestMapping(value="/backup", method=RequestMethod.PUT)
    public String backup(@RequestBody JsonNode request) {
        try {
            if (request.has("sessionID") && request.has("sleepData")) {
                return Backup.upload(
                    request.get("sessionID").asText(), request.get("sleepData").asText()
                ).toString();
            }
            return new Response(400, "Invalid request body.").toString();
        }
        catch (SQLException e) {
            return new Response(500, "Internal Server Error.").toString();
        }
    }

    @ResponseBody
    @RequestMapping(value="/backup", method=RequestMethod.GET)
    public String getBackup(@RequestBody JsonNode request) {
        try {
            if (request.has("sessionID"))
                return Backup.get(request.get("sessionID").asText()).toString();
                
            return new Response(400, "Invalid request body.").toString();
        }
        catch (SQLException e) {
            return new Response(500, "Internal Server Error.").toString();
        }
    }

    @ResponseBody
    @RequestMapping(value="/backup", method=RequestMethod.DELETE)
    public String deleteBackup(@RequestBody JsonNode request) {
        try {
            if (request.has("sessionID"))
                return Backup.delete(request.get("sessionID").asText()).toString();

            return new Response(400, "Invalid request body.").toString();
        }
        catch (SQLException e) {
            return new Response(500, "Internal Server Error.").toString();
        }
    }
}
