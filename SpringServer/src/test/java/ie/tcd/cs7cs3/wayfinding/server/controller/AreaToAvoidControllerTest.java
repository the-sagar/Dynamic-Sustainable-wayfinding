package ie.tcd.cs7cs3.wayfinding.server.controller;

import ie.tcd.cs7cs3.wayfinding.server.model.AreaToAvoid;
import ie.tcd.cs7cs3.wayfinding.server.repository.AreaToAvoidRepository;
import ie.tcd.cs7cs3.wayfinding.server.requests.AreaToAvoidRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class AreaToAvoidControllerTest {
    @InjectMocks
    private AreaToAvoidController areaToAvoidController;

    @Mock
    private AreaToAvoidRepository mockAreaToAvoidRepository;

    private Long correctId=Long.valueOf(1);
    private Long wrongId=Long.valueOf(2);
    AreaToAvoid areaToAvoid = new AreaToAvoid();

    @BeforeEach
    public void setup() {

        areaToAvoid.setExpireTime(Long.valueOf(100));
        areaToAvoid.setReason("Test");
        areaToAvoid.setBoundB(Float.valueOf(1));
        areaToAvoid.setBoundT(Float.valueOf(2));
        areaToAvoid.setBoundL(Float.valueOf(3));
        areaToAvoid.setBoundR(Float.valueOf(4));
    }

    @Test
    public void dataFound(){
        given(mockAreaToAvoidRepository.findById(correctId))
                .willReturn(java.util.Optional.of(areaToAvoid));
        assertThat(ResponseEntity.ok(areaToAvoid)).isEqualTo(areaToAvoidController.getData("1"));
    }
    @Test
    public void dataNotFound(){
        given(mockAreaToAvoidRepository.findById(wrongId))
                .willReturn((java.util.Optional.empty()));
        assertThat(ResponseEntity.badRequest().build()).isEqualTo(areaToAvoidController.getData("2"));
    }
    @Test
    public void allDataFound(){
        List<AreaToAvoid> areas= new ArrayList<>();
        areas.add(areaToAvoid);
        given(mockAreaToAvoidRepository.findAll())
                .willReturn(areas);
        assertThat(ResponseEntity.ok(areas)).isEqualTo(areaToAvoidController.getAllData());
    }
    @Test
    public void allDataNotFound(){
        List<AreaToAvoid> areas= new ArrayList<>();
        given(mockAreaToAvoidRepository.findAll())
                .willReturn(areas);
        assertThat(ResponseEntity.ok(areas)).isEqualTo(areaToAvoidController.getAllData());
    }
    @Test
    public void dataDeleted(){
        given(mockAreaToAvoidRepository.findById(correctId))
                .willReturn(java.util.Optional.of(areaToAvoid));
        assertThat(ResponseEntity.ok(areaToAvoid)).isEqualTo(areaToAvoidController.deleteData("1"));
    }
    @Test
    public void dataNotDeleted(){
        given(mockAreaToAvoidRepository.findById(wrongId))
                .willReturn(java.util.Optional.empty());
        assertThat(ResponseEntity.badRequest().build()).isEqualTo(areaToAvoidController.deleteData("2"));
    }
    @Test
    public void dataUpdated(){
        given(mockAreaToAvoidRepository.findById(correctId))
                .willReturn(java.util.Optional.of(areaToAvoid));
        AreaToAvoidRequest areaToAvoidRequest=new
                AreaToAvoidRequest(Long.valueOf(100),"udate",Float.valueOf(10),Float.valueOf(20),Float.valueOf(30),Float.valueOf(40));
        AreaToAvoid updatedAreaToAvoid=new AreaToAvoid();
        updatedAreaToAvoid.setExpireTime(Long.valueOf(100));
        updatedAreaToAvoid.setReason("udate");
        updatedAreaToAvoid.setBoundB(Float.valueOf(10));
        updatedAreaToAvoid.setBoundT(Float.valueOf(20));
        updatedAreaToAvoid.setBoundL(Float.valueOf(30));
        updatedAreaToAvoid.setBoundR(Float.valueOf(40));

        assertThat(ResponseEntity.status(HttpStatus.OK).build().getStatusCode()).isEqualTo(areaToAvoidController.updateData("1",areaToAvoidRequest).getStatusCode());


    }

    @Test
    public void dataNotUpdated(){
        given(mockAreaToAvoidRepository.findById(wrongId))
                .willReturn(java.util.Optional.empty());
        AreaToAvoidRequest areaToAvoidRequest=new
                AreaToAvoidRequest(Long.valueOf(100),"udate",Float.valueOf(10),Float.valueOf(20),Float.valueOf(30),Float.valueOf(40));
        AreaToAvoid updatedAreaToAvoid=new AreaToAvoid();
        updatedAreaToAvoid.setExpireTime(Long.valueOf(100));
        updatedAreaToAvoid.setReason("udate");
        updatedAreaToAvoid.setBoundB(Float.valueOf(10));
        updatedAreaToAvoid.setBoundT(Float.valueOf(20));
        updatedAreaToAvoid.setBoundL(Float.valueOf(30));
        updatedAreaToAvoid.setBoundR(Float.valueOf(40));

        assertThat(ResponseEntity.status(HttpStatus.BAD_REQUEST).build().getStatusCode()).isEqualTo(areaToAvoidController.updateData("2",areaToAvoidRequest).getStatusCode());


    }



}
