package ie.tcd.cs7cs3.wayfinding.server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.UnknownFieldSet;
import ie.tcd.cs7cs3.wayfinding.server.model.AreaToAvoid;
import ie.tcd.cs7cs3.wayfinding.server.model.User;
import ie.tcd.cs7cs3.wayfinding.server.model.UserPreference;
import ie.tcd.cs7cs3.wayfinding.server.repository.AreaToAvoidRepository;
import ie.tcd.cs7cs3.wayfinding.server.repository.PreferenceRepository;
import ie.tcd.cs7cs3.wayfinding.server.repository.UserRepository;
import ie.tcd.cs7cs3.wayfinding.server.requests.RouteRequest;
import ie.tcd.cs7cs3.wayfinding.server.response.HopsData;
import ie.tcd.cs7cs3.wayfinding.server.response.RoutingDecisionResponse;
import ie.tcd.cs7cs3.wayfinding.server.rpc.*;
import ie.tcd.cs7cs3.wayfinding.server.security.jwt.JwtUtils;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

@Service
public class GRPCClientService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PreferenceRepository userPreferenceRepository;

    @Autowired
    AreaToAvoidRepository areaToAvoidRepository;

    @Autowired
    JwtUtils jwtUtils;

    public RoutingDecisionResponse getRoute(RouteRequest request, HttpServletRequest header) {

        UserPreference userPreference = getUserPreferences(header);
        List<AreaToAvoid> areasToAvoidList = areaToAvoidRepository.getActiveAreaToAvoid();
        List<Map<String, Float>> areaToAvoidList = new ArrayList<>();

        for(AreaToAvoid area : areasToAvoidList){
            Map<String, Float> areaToAvoidMap = new HashMap<>();
            areaToAvoidMap.put("T", area.getBoundT());
            areaToAvoidMap.put("B", area.getBoundB());
            areaToAvoidMap.put("L", area.getBoundL());
            areaToAvoidMap.put("R", area.getBoundR());
            areaToAvoidList.add(areaToAvoidMap);
        }
        Map<String, List<Map<String, Float>>> areaToAvoid = new HashMap<>();
        areaToAvoid.put("Items", areaToAvoidList);

        ObjectMapper Obj = new ObjectMapper();
        String jsonStr = "";

        try {
            jsonStr = Obj.writeValueAsString(areaToAvoid);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, String> additionalInfo = new HashMap<>();
        additionalInfo.put("objective_time", userPreference.getObjectiveTime().toString());
        additionalInfo.put("objective_cost", userPreference.getObjectiveCost().toString());
        additionalInfo.put("objective_sustainable", userPreference.getObjectiveSustainable().toString());
        additionalInfo.put("can_walkLong", userPreference.getCanWalkLong().toString());
        additionalInfo.put("can_drive", userPreference.getCanDrive().toString());
        additionalInfo.put("can_bike", userPreference.getCanBike().toString());
        additionalInfo.put("can_publictrans", userPreference.getCanPublicTrans().toString());
        additionalInfo.put("area_to_avoid", jsonStr);

        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9001)
                .usePlaintext()
                .build();

        RouteServiceGrpc.RouteServiceBlockingStub stub = RouteServiceGrpc
                .newBlockingStub(channel);

        RoutingDecisionReqLocation from = RoutingDecisionReqLocation
                .newBuilder()
                .setLat(request.getFrom().getLat())
                .setLon(request.getFrom().getLon())
                .build();

        RoutingDecisionReqLocation to = RoutingDecisionReqLocation
                .newBuilder()
                .setLat(request.getTo().getLat())
                .setLon(request.getTo().getLon())
                .build();

        RoutingDecisionResp response = stub.route(RoutingDecisionReq
                .newBuilder()
                .setFrom(from)
                .setTo(to)
                .putAllAdditionalInfo(additionalInfo)
                .build());

        List<RoutingDecision> hopsList = response.getHopsList();
        RoutingDecisionResponse resp = new RoutingDecisionResponse();
        List<HopsData> hopsListResp = new ArrayList<>();

        for(int i = 0; i < hopsList.size(); i++){
            HopsData data = new HopsData();
            data.setAssociatedData(hopsList.get(i).getAssociatedDataMap());
            data.setFrom(hopsList.get(i).getFrom());
            data.setVia(hopsList.get(i).getVia());
            hopsListResp.add(data);
        }

        resp.setHops(hopsListResp);
        UnknownFieldSet fieldSet = response.getUnknownFields();
        channel.shutdown();
        return resp;
    }

    public UserPreference getUserPreferences(HttpServletRequest header){
        Optional<User> userDetails = getUser(header);
        Optional<UserPreference> userPreference = userPreferenceRepository.findById(userDetails.get().getId());
        return userPreference.get();
    }

    public Optional<UserPreference> getUserPreference(HttpServletRequest header){
        Optional<User> userDetails = getUser(header);
        Optional<UserPreference> userPreference = userPreferenceRepository.findById(userDetails.get().getId());
        return userPreference;
    }

    public Optional<User> getUser(HttpServletRequest header) {
        String headerAuth = header.getHeader("Authorization");
        if (headerAuth!= null && StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            headerAuth =  headerAuth.substring(7, headerAuth.length());
        }
        String emailId = jwtUtils.getUserNameFromJwtToken(headerAuth);
        Optional<User> userDetails = userRepository.findByEmail(emailId);
        return userDetails;
    }
}