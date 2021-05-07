package ie.tcd.cs7cs3.wayfinding.server.controller;

import ie.tcd.cs7cs3.wayfinding.server.model.User;
import ie.tcd.cs7cs3.wayfinding.server.model.UserPreference;

import ie.tcd.cs7cs3.wayfinding.server.repository.PreferenceRepository;
import ie.tcd.cs7cs3.wayfinding.server.repository.UserRepository;
import ie.tcd.cs7cs3.wayfinding.server.response.MessageResponse;
import ie.tcd.cs7cs3.wayfinding.server.security.jwt.JwtUtils;
import ie.tcd.cs7cs3.wayfinding.server.service.GRPCClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/UserPreference")
public class UserPreferenceController {


    @Autowired
    PreferenceRepository userPreferenceRepository;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserRepository userRepository;

    @Autowired
    GRPCClientService grpcService;

    @PostMapping()
    public ResponseEntity<?> saveUserPreferences(HttpServletRequest header, @RequestBody UserPreference userPreference){
        Optional<User> userDetails = grpcService.getUser(header);
        userPreference.setUser(userDetails.get());
        userPreference.setId(userDetails.get().getId());
        userPreferenceRepository.save(userPreference);
        return ResponseEntity.ok(new MessageResponse(0,"success"));
    }

    @GetMapping()
    public ResponseEntity<?> getUserPreferences(HttpServletRequest header){
        Optional<UserPreference> userPreference=grpcService.getUserPreference(header);
        if(userPreference.isPresent()){
            UserPreference up = userPreference.get();
            up.setUser(null);
            return ResponseEntity.ok(up);
        }
        else{
            return ResponseEntity.badRequest().body(new MessageResponse(-1,"failure"));
        }
    }

}
