package ie.tcd.cs7cs3.wayfinding.server.controller;

import ie.tcd.cs7cs3.wayfinding.server.model.AreaToAvoid;
import ie.tcd.cs7cs3.wayfinding.server.model.UserPreference;
import ie.tcd.cs7cs3.wayfinding.server.repository.AreaToAvoidRepository;
import ie.tcd.cs7cs3.wayfinding.server.repository.PreferenceRepository;
import ie.tcd.cs7cs3.wayfinding.server.service.GRPCClientService;
import io.netty.handler.codec.http.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class UserPreferenceControllerTest {
    @InjectMocks
    private UserPreferenceController userPreferenceController;

    @Mock
    private PreferenceRepository mockUserPreferenceRepository;

    @Mock
    GRPCClientService grpcService;

    private Long correctId=Long.valueOf(1);
    private Long wrongId=Long.valueOf(2);
    UserPreference userPreference=new UserPreference();

    @BeforeEach
    public void setup() {
        userPreference.setBirthDay(10);
        userPreference.setBirthMon(8);
        userPreference.setBirthYear(1998);
        userPreference.setCanBike(true);
        userPreference.setCanDrive(true);
        userPreference.setCanWalkLong(true);
        userPreference.setFirstName("karan");
        userPreference.setLastName("Bhardwaj");
        userPreference.setObjectiveCost(Float.valueOf("0.5"));
        userPreference.setObjectiveSustainable(Float.valueOf("1.4"));
        userPreference.setObjectiveTime(Float.valueOf("3.88"));



    }
    @Test
    public void dataSaved(){
        MockHttpServletRequest httpRequest = new MockHttpServletRequest();
        given((grpcService.getUserPreference( httpRequest)))
                .willReturn(java.util.Optional.of(userPreference));
        assertThat(HttpStatus.OK).isEqualTo(userPreferenceController.saveUserPreferences(httpRequest, userPreference).getStatusCode());
    }

    @Test
    public void foundData(){
        MockHttpServletRequest httpRequest = new MockHttpServletRequest();
        given((grpcService.getUserPreference( httpRequest)))
                .willReturn(java.util.Optional.of(userPreference));
        assertThat(HttpStatus.OK).isEqualTo(userPreferenceController.getUserPreferences(httpRequest).getStatusCode());

    }

    @Test
    public void notfoundData(){
        MockHttpServletRequest httpRequest = new MockHttpServletRequest();
        given((grpcService.getUserPreference( httpRequest)))
                .willReturn(java.util.Optional.empty());
        assertThat(HttpStatus.BAD_REQUEST).isEqualTo(userPreferenceController.getUserPreferences(httpRequest).getStatusCode());

    }
}
