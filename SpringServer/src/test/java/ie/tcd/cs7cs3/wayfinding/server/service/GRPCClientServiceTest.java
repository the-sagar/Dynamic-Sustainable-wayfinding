package ie.tcd.cs7cs3.wayfinding.server.service;

import ie.tcd.cs7cs3.wayfinding.server.model.*;
import ie.tcd.cs7cs3.wayfinding.server.repository.AreaToAvoidRepository;
import ie.tcd.cs7cs3.wayfinding.server.repository.PreferenceRepository;
import ie.tcd.cs7cs3.wayfinding.server.repository.UserRepository;
import ie.tcd.cs7cs3.wayfinding.server.requests.LocData;
import ie.tcd.cs7cs3.wayfinding.server.requests.RouteRequest;
import ie.tcd.cs7cs3.wayfinding.server.response.HopsData;
import ie.tcd.cs7cs3.wayfinding.server.response.RoutingDecisionResponse;
import ie.tcd.cs7cs3.wayfinding.server.rpc.RouteServiceGrpc;
import ie.tcd.cs7cs3.wayfinding.server.rpc.RoutingDecisionReq;
import ie.tcd.cs7cs3.wayfinding.server.rpc.RoutingDecisionResp;
import ie.tcd.cs7cs3.wayfinding.server.security.jwt.JwtUtils;
import io.grpc.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class GRPCClientServiceTest {

    @InjectMocks
    GRPCClientService grpcClientService;

    @Mock
    UserRepository userRepository;

    @Mock
    PreferenceRepository userPreferenceRepository;

    @Mock
    AreaToAvoidRepository areaToAvoidRepository;

    @Mock
    JwtUtils jwtUtils;

    @Mock
    RouteServiceGrpc.RouteServiceBlockingStub stub;

    @Mock
    RoutingDecisionReq routingRequest;

    @Mock
    RoutingDecisionResp routingResponse;

    RouteRequest request = new RouteRequest();
    RoutingDecisionResponse response = new RoutingDecisionResponse();
    UserPreference preference = new UserPreference();
    User user = new User();
    List<AreaToAvoid> areasToAvoidList = new ArrayList<>();
    List<Map<String, Float>> areaToAvoidList = new ArrayList<>();

    @BeforeEach
    public void setup() {
        preference.setBirthDay(11);
        preference.setBirthMon(11);
        preference.setBirthYear(11);
        preference.setCanBike(true);
        preference.setCanDrive(true);
        preference.setCanPublicTrans(true);
        preference.setCanWalkLong(true);
        preference.setFirstName("Test");
        preference.setLastName("Test");
        preference.setObjectiveCost(0.33F);
        preference.setObjectiveSustainable(0.33F);
        preference.setObjectiveTime(0.33F);
        preference.setId(1L);
        Set<Role> roles = new HashSet<>();
        Role role = new Role();
        role.setId(1);
        role.setName(RolesEnum.ROLE_ADMIN);
        roles.add(role);

        user.setId(1L);
        user.setEmail("sample@test.com");
        user.setPassword("");
        user.setRoles(roles);

        LocData from = new LocData();
        LocData to = new LocData();
        request.setFrom(from);
        request.setTo(to);
        Map<String, String> additionalInfo = new HashMap<>();
        request.setAdditionalInfo(additionalInfo);
        List<HopsData> hopsList = new ArrayList<>();
        response.setHops(hopsList);

        AreaToAvoid areatoAvoid = new AreaToAvoid();
        areatoAvoid.setBoundB(53.3F);
        areatoAvoid.setBoundL(53.3F);
        areatoAvoid.setBoundR(53.3F);
        areatoAvoid.setBoundR(53.3F);
        areatoAvoid.setId(1);
        areasToAvoidList.add(areatoAvoid);
        Map<String, Float> areaToAvoidMap = new HashMap<>();
        areaToAvoidMap.put("T", areasToAvoidList.get(0).getBoundT());
        areaToAvoidMap.put("B", areasToAvoidList.get(0).getBoundB());
        areaToAvoidMap.put("L", areasToAvoidList.get(0).getBoundL());
        areaToAvoidMap.put("R", areasToAvoidList.get(0).getBoundR());
        areaToAvoidList.add(areaToAvoidMap);

        additionalInfo.put("objective_time", preference.getObjectiveTime().toString());
        additionalInfo.put("objective_cost", preference.getObjectiveCost().toString());
        additionalInfo.put("objective_sustainable", preference.getObjectiveSustainable().toString());
        additionalInfo.put("can_walkLong", preference.getCanWalkLong().toString());
        additionalInfo.put("can_drive", preference.getCanDrive().toString());
        additionalInfo.put("can_bike", preference.getCanBike().toString());
        additionalInfo.put("can_publictrans", preference.getCanPublicTrans().toString());
        additionalInfo.put("area_to_avoid", "");
    }

    @Test
    public void getUserPrefs(){
        MockHttpServletRequest httpRequest = new MockHttpServletRequest();
        httpRequest.addHeader("Authorization", "");
        String token = httpRequest.getHeader("Authorization").substring(7, httpRequest.getHeader("Authorization").length());
        when(jwtUtils.getUserNameFromJwtToken(token)).thenReturn("sample@test.com");
        when(userRepository.findByEmail("sample@test.com")).thenReturn(Optional.of(user));
        when(userPreferenceRepository.findById(1L)).thenReturn(Optional.of(preference));
        when(stub.route(routingRequest)).thenReturn(routingResponse);
        assertEquals(preference, grpcClientService.getUserPreferences(httpRequest));
    }

    @Test
    public void getRoute(){
        MockHttpServletRequest httpRequest = new MockHttpServletRequest();
        httpRequest.addHeader("Authorization", "");
        String token = httpRequest.getHeader("Authorization").substring(7, httpRequest.getHeader("Authorization").length());
        when(areaToAvoidRepository.getActiveAreaToAvoid()).thenReturn(areasToAvoidList);
        when(jwtUtils.getUserNameFromJwtToken(token)).thenReturn("sample@test.com");
        when(userRepository.findByEmail("sample@test.com")).thenReturn(Optional.of(user));
        when(userPreferenceRepository.findById(1L)).thenReturn(Optional.of(preference));
        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () -> {
            grpcClientService.getRoute(request, httpRequest);
        });
        assertTrue(exception.getMessage().contains("UNAVAILABLE"));
    }
}
