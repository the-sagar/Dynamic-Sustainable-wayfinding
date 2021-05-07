package ie.tcd.cs7cs3.wayfinding.server.repository;

import ie.tcd.cs7cs3.wayfinding.server.model.AreaToAvoid;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AreaToAvoidRepository extends CrudRepository<AreaToAvoid, Long> {
    @Query(value = "SELECT * from wayfinding.area_to_avoid WHERE expire_time < UNIX_TIMESTAMP()",
            nativeQuery = true)
    List<AreaToAvoid> getActiveAreaToAvoid();
}
