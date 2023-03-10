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
            case "ACCOUNT":
                return "bg-success";

            case "TRANSACTION":
                return "bg-primary";

            case "FRAUD":
                return "bg-danger";

            case "REVERSAL":
                return "bg-warning";
        }

        return "bg-dark";
    }
}
