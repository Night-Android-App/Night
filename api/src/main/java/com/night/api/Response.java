package com.night.api;

import org.json.JSONObject;

public class Response extends JSONObject {

    public Response(int status, Object data, String message) {
        put("status", status);
        put("data", data);
        put("msg", message);
    }

    public Response(int status, String message) {
        this(status, null, message);
    }

    public Response(int status, Object data) {
        this(status, data, null);
    }
}