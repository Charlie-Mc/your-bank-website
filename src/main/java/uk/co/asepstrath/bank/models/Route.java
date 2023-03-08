package uk.co.asepstrath.bank.models;

public class Route {
    private String route, method, info;

    public Route(String route, String method, String info) {
        this.route = route;
        this.method = method;
        this.info = info;
    }

    public String getRoute() {
        return route;
    }

    public String getMethod() {
        return method;
    }

    public String getInfo() {
        return info;
    }

    public String getColor() {
        switch (method) {
            case "GET":
                return "bg-success";

            case "POST":
                return "bg-primary";

            case "DELETE":
                return "bg-danger";

            case "PATCH":
                return "bg-warning";
        }

        return "bg-dark";
    }
}
