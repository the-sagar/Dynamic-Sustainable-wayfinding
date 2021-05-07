package ie.tcd.cs7cs3.wayfinding.server.controller;

import ie.tcd.cs7cs3.wayfinding.server.requests.RouteRequest;
import ie.tcd.cs7cs3.wayfinding.server.response.RoutingDecisionResponse;
import ie.tcd.cs7cs3.wayfinding.server.service.GRPCClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
public class GRPCClientController {

    @Autowired
    GRPCClientService grpcService;

    @PostMapping("/route")
    public RoutingDecisionResponse getRoute(@RequestBody RouteRequest request, HttpServletRequest header){
        RoutingDecisionResponse resp = grpcService.getRoute(request, header);
        return resp;
    }
}