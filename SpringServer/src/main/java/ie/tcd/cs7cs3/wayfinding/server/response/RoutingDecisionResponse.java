package ie.tcd.cs7cs3.wayfinding.server.response;

import ie.tcd.cs7cs3.wayfinding.server.rpc.RoutingDecision;

import java.util.List;
import java.util.Map;

public class RoutingDecisionResponse {
    List<HopsData> hops;
    public List<HopsData> getHops() {
        return hops;
    }
    public void setHops(List<HopsData> hops) {
        this.hops = hops;
    }
}