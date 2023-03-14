package uk.co.asepstrath.bank.models;

import java.util.ArrayList;
import java.util.HashMap;

public class Route {
    private final String route, method, info;
    private ArrayList<Response> responses;

    public Route(String route, String method, String info) {
        this.route = route;
        this.method = method;
        this.info = info;
        this.responses = new ArrayList<>();
    }

    public String getRoute() { return route; }

    public String getMethod() { return method; }

    public String getInfo() { return info; }

    public String getColor() {
        switch (method) {
            case "GET":
                return "bg-primary";

            case "POST":
                return "bg-success";

            case "DELETE":
                return "bg-danger";

            case "PATCH":
                return "bg-warning";
        }

        return "bg-dark";
    }

    public Route addResponse(String code, String description) {
        responses.add(new Response(code, description));
        return this;
    }

    public ArrayList<Response> getResponses() {
        return responses;
    }
}
