package ie.tcd.cs7cs3.wayfinding.server.controller;

import ie.tcd.cs7cs3.wayfinding.server.model.AreaToAvoid;
import ie.tcd.cs7cs3.wayfinding.server.repository.AreaToAvoidRepository;
import ie.tcd.cs7cs3.wayfinding.server.requests.AreaToAvoidRequest;
import ie.tcd.cs7cs3.wayfinding.server.response.MessageResponse;
import lombok.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/areaToAvoid")
public class AreaToAvoidController {
    @Autowired
    AreaToAvoidRepository areaToAvoidRepository;
    @PostMapping()
    public ResponseEntity<?> postData(@RequestBody AreaToAvoidRequest req){
        AreaToAvoid areaToAvoid = new AreaToAvoid();
        areaToAvoid.setExpireTime(req.getExpireTime());
        areaToAvoid.setReason(req.getReason());
        areaToAvoid.setBoundB(req.getLocationB());
        areaToAvoid.setBoundT(req.getLocationT());
        areaToAvoid.setBoundL(req.getLocationL());
        areaToAvoid.setBoundR(req.getLocationR());

        areaToAvoidRepository.save(areaToAvoid);
//        areaToAvoidRepository.flush();

        return ResponseEntity.ok(new MessageResponse(200, "Content Created!"));
    }
    @GetMapping(value = "/All")
    public ResponseEntity<?> getAllData(){
        List<AreaToAvoid> allAreas = (List<AreaToAvoid>) areaToAvoidRepository.findAll();
        return ResponseEntity.ok(allAreas);
    }

    @GetMapping(value = "/{Id}")
    public ResponseEntity<?> getData(@PathVariable("Id") String id){
        Optional<AreaToAvoid> areaToAvoid=areaToAvoidRepository.findById(Long.parseLong(id));
        if(areaToAvoid.isPresent()){
            return ResponseEntity.ok(areaToAvoid.get());
        }
        else{
            return ResponseEntity.badRequest().build();
        }
    }
    @DeleteMapping(value="/{Id}")
    public ResponseEntity<?> deleteData(@PathVariable("Id") String id){
        Optional<AreaToAvoid> areaToAvoid=areaToAvoidRepository.findById(Long.parseLong(id));
        if(areaToAvoid.isPresent()){
            areaToAvoidRepository.delete(areaToAvoid.get());
            return ResponseEntity.ok(areaToAvoid.get());
        }
        else{
            return ResponseEntity.badRequest().build();
        }
    }
    @PutMapping(value = "/{id}")
    public ResponseEntity<?> updateData(@PathVariable("Id") String id,@RequestBody AreaToAvoidRequest req){
        Optional<AreaToAvoid> areaToAvoid=areaToAvoidRepository.findById(Long.parseLong(id));
        if (areaToAvoid.isPresent()){
            areaToAvoidRepository.delete(areaToAvoid.get());
            areaToAvoid.get().setExpireTime(req.getExpireTime());
            areaToAvoid.get().setReason(req.getReason());
            areaToAvoid.get().setBoundB(req.getLocationB());
            areaToAvoid.get().setBoundT(req.getLocationT());
            areaToAvoid.get().setBoundL(req.getLocationL());
            areaToAvoid.get().setBoundR(req.getLocationR());
            areaToAvoidRepository.save(areaToAvoid.get());
//            areaToAvoidRepository.flush();
            return ResponseEntity.ok(areaToAvoid.get());

        }
        else{
            return ResponseEntity.badRequest().build();
        }
    }
}
