package ie.tcd.cs7cs3.wayfinding.server.controller;

import ie.tcd.cs7cs3.wayfinding.server.model.AreaToAvoid;
import ie.tcd.cs7cs3.wayfinding.server.requests.LocData;
import ie.tcd.cs7cs3.wayfinding.server.requests.RouteRequest;
import ie.tcd.cs7cs3.wayfinding.server.response.HopsData;
import ie.tcd.cs7cs3.wayfinding.server.response.RoutingDecisionResponse;
import ie.tcd.cs7cs3.wayfinding.server.service.GRPCClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class GRPCClientControllerTest {

    @InjectMocks
    private GRPCClientController grpcClientController;

    @Mock
    GRPCClientService grpcService;

    RouteRequest request = new RouteRequest();
    RoutingDecisionResponse response = new RoutingDecisionResponse();

    @BeforeEach
    public void setup() {
        LocData from = new LocData();
        LocData to = new LocData();
        request.setFrom(from);
        request.setTo(to);
        Map<String, String> additionalInfo = new HashMap<>();
        request.setAdditionalInfo(additionalInfo);
        List<HopsData> hopsList = new ArrayList<>();
        response.setHops(hopsList);
    }

    @Test
    public void routeFound(){
        MockHttpServletRequest httpRequest = new MockHttpServletRequest();
        given(grpcService.getRoute(request, httpRequest))
                .willReturn(response);
        assertThat(response.getHops().equals(grpcClientController.getRoute(request, httpRequest)));
    }

}
