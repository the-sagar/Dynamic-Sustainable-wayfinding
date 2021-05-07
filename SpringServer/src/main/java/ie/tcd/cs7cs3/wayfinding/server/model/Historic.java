package ie.tcd.cs7cs3.wayfinding.server.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Historic {
    Integer available_bike_stands;
    Integer available_bikes;
    Integer bike_stands;
    String status;
    LocalDateTime time;
}
