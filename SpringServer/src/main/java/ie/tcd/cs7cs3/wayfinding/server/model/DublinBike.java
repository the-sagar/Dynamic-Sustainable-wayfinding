package ie.tcd.cs7cs3.wayfinding.server.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DublinBike {

    String address;
    Boolean banking;
    Integer id;
    Float latitude;
    Float longitude;
    String name;
    Historic historic[];

}
