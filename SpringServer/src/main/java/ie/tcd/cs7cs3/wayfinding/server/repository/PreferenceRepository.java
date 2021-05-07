package ie.tcd.cs7cs3.wayfinding.server.repository;

import ie.tcd.cs7cs3.wayfinding.server.model.User;
import ie.tcd.cs7cs3.wayfinding.server.model.UserPreference;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PreferenceRepository  extends CrudRepository<UserPreference, Long> {
}
