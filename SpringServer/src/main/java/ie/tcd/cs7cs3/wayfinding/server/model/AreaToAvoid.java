package ie.tcd.cs7cs3.wayfinding.server.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "areaToAvoid")
public class AreaToAvoid {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Long expireTime;

    private String reason;

    private Float boundT;
    private Float boundB;
    private Float boundL;
    private Float boundR;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Float getBoundT() {
        return boundT;
    }

    public void setBoundT(Float boundT) {
        this.boundT = boundT;
    }

    public Float getBoundB() {
        return boundB;
    }

    public void setBoundB(Float boundB) {
        this.boundB = boundB;
    }

    public Float getBoundL() {
        return boundL;
    }

    public void setBoundL(Float boundL) {
        this.boundL = boundL;
    }

    public Float getBoundR() {
        return boundR;
    }

    public void setBoundR(Float boundR) {
        this.boundR = boundR;
    }
}
