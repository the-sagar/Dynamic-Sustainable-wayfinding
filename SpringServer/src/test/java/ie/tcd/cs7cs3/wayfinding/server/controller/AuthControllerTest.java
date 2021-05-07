package ie.tcd.cs7cs3.wayfinding.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ie.tcd.cs7cs3.wayfinding.server.model.Role;
import ie.tcd.cs7cs3.wayfinding.server.model.User;
import ie.tcd.cs7cs3.wayfinding.server.repository.RoleRepository;
import ie.tcd.cs7cs3.wayfinding.server.repository.UserRepository;
import ie.tcd.cs7cs3.wayfinding.server.requests.RegisterRequest;
import ie.tcd.cs7cs3.wayfinding.server.response.MessageResponse;
import ie.tcd.cs7cs3.wayfinding.server.security.jwt.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    private MockMvc mvc;

    @InjectMocks
    private AuthController controller;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    PasswordEncoder encoder;

    @Mock
    JwtUtils jwtUtils;

    // This object will be magically initialized by the initFields method below.
    private JacksonTester<RegisterRequest> jsonUser;

    private JacksonTester<MessageResponse> jsonResponse;

    @BeforeEach
    public void setup() {
        // We would need this line if we would not use the MockitoExtension
        // MockitoAnnotations.initMocks(this);
        // Here we can't use @AutoConfigureJsonTesters because there isn't a Spring context
        JacksonTester.initFields(this, new ObjectMapper());
        // MockMvc standalone approach
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .build();
    }


    @Test
    public void emailIdAlreadyInUse() throws Exception {
        //Given
        given(userRepository.existsByEmail("aswin@gmail.com"))
                .willReturn(true);
        //When
        RegisterRequest registerRequest = new RegisterRequest("aswin@gmail.com", "aswin123#");
        MockHttpServletResponse response = mvc.perform(
                post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(
                        jsonUser.write(registerRequest).getJson()
                )).andReturn().getResponse();

//        Exception exception = assertThrows(RuntimeException.class, () -> {
//            new RuntimeException("Error: Role is not found.");
//        });
//
//        String expectedMessage = "Error: Role is not found";
//        String actualMessage = exception.getMessage();
//        assertTrue(actualMessage.contains(expectedMessage));
        // then

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).isEqualTo(
                jsonResponse.write(new MessageResponse(-1, "Error: Email is already in use!")).getJson()
        );
    }

    @Test
    public void roleNotFound() throws Exception {
        //Given
        given(userRepository.existsByEmail("aswin@gmail.com"))
                .willReturn(true);
        //When
        RegisterRequest registerRequest = new RegisterRequest("aswin@gmail.com", "aswin123#");
        MockHttpServletResponse response = mvc.perform(
                post("/api/auth/register").contentType(MediaType.APPLICATION_JSON).content(
                        jsonUser.write(registerRequest).getJson()
                )).andReturn().getResponse();

//        Exception exception = assertThrows(RuntimeException.class, () -> {
//            new RuntimeException("Error: Role is not found.");
//        });
//
//        String expectedMessage = "Error: Role is not found";
//        String actualMessage = exception.getMessage();
//        assertTrue(actualMessage.contains(expectedMessage));
        // then

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).isEqualTo(
                jsonResponse.write(new MessageResponse(-1, "Error: Email is already in use!")).getJson()
        );
    }

}
