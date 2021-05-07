package ie.tcd.cs7cs3.wayfinding.server.repository;


import ie.tcd.cs7cs3.wayfinding.server.model.User;

import java.util.Optional;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
	Optional<User> findByEmail(String email);
	Boolean existsByEmail(String email);
}
