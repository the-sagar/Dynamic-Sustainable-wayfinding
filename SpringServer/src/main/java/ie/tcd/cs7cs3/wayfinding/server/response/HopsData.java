package ie.tcd.cs7cs3.wayfinding.server.response;


import java.util.Map;

public class HopsData{
    Map<String, String> associatedData;
    String via;
    String from;
    public Map<String, String> getAssociatedData() {
        return associatedData;
    }
    public void setAssociatedData(Map<String, String> associatedData) {
        this.associatedData = associatedData;
    }
    public String getVia() {
        return via;
    }
    public void setVia(String via) {
        this.via = via;
    }
    public String getFrom() {
        return from;
    }
    public void setFrom(String from) {
        this.from = from;
    }
}
