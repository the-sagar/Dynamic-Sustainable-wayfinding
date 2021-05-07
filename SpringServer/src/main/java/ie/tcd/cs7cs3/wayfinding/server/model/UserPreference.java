package ie.tcd.cs7cs3.wayfinding.server.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "USER_PREFERENCE")
public class UserPreference {

    @Id
    @Column(name = "user_id")
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "firstName")
    private String firstName;

    @Column(name = "lastName")
    private String lastName;

    @Column(name = "birthYear")
    private Integer birthYear;

    @Column(name = "birthMon")
    private Integer birthMon;

    @Column(name = "birthDay")
    private Integer birthDay;

    @Column(name = "objectiveTime")
    private Float objectiveTime;

    @Column(name = "objectiveCost")
    private Float objectiveCost;

    @Column(name = "objectiveSustainable")
    private Float objectiveSustainable;

    @Column(name = "canWalkLong")
    private Boolean canWalkLong;

    @Column(name = "canDrive")
    private Boolean canDrive;

    @Column(name = "canBike")
    private Boolean canBike;

    @Column(name = "canPublicTrans")
    private Boolean canPublicTrans;
}
