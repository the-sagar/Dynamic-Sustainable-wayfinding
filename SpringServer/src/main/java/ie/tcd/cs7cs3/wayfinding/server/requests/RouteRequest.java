package ie.tcd.cs7cs3.wayfinding.server.requests;

import java.util.Map;

public class RouteRequest{
    private LocData from;
    private LocData to;
    private Map<String, String> additionalInfo;
    public LocData getFrom() {
        return from;
    }
    public void setFrom(LocData from) {
        this.from = from;
    }
    public LocData getTo() {
        return to;
    }
    public void setTo(LocData to) {
        this.to = to;
    }
    public Map<String, String> getAdditionalInfo() {
        return additionalInfo;
    }
    public void setAdditionalInfo(Map<String, String> additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
}