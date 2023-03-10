package uk.co.asepstrath.bank.models;

public class Route {
    private final String route;
    private final String method;
    private final String info;
    private final String responses;

    public Route(String route, String method, String info, String responses) {
        this.route = route;
        this.method = method;
        this.info = info;
        this.responses = responses;
    }

    public String getRoute() { return route; }

    public String getMethod() { return method; }

    public String getInfo() { return info; }

    public String getResponses() { return responses; }

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
