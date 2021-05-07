package ie.tcd.cs7cs3.wayfinding.server.controller;

import ie.tcd.cs7cs3.wayfinding.server.model.DublinBike;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/DublinBike")
public class DublinBikeController {

@GetMapping(value = "/all")
public ResponseEntity<?> getData(){

    RestTemplate restTemplate = new RestTemplate();
    LocalDateTime df = LocalDateTime.now();
    LocalDateTime dt = LocalDateTime.now();
    df = df.minusMinutes(5);
    DateTimeFormatter df_new = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
    String url = "https://dublinbikes.staging.derilinx.com/api/v1/resources/historical/?dfrom=" + df.format(df_new) + "&dto=" + dt.format(df_new);
        ResponseEntity<DublinBike[]> response
                = restTemplate.getForEntity(url , DublinBike[].class);
        return response;

    }
}
