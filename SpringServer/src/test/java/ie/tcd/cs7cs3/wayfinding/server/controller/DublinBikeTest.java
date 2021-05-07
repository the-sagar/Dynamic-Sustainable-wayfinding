package ie.tcd.cs7cs3.wayfinding.server.controller;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class DublinBikeTest {
    @InjectMocks
    private DublinBikeController dublinBikeController;

    @Test
    public void dataFound(){
        assertThat(ResponseEntity.status(HttpStatus.OK).build().getStatusCode()).isEqualTo(dublinBikeController.getData().getStatusCode());
    }

}
