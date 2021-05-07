package ie.tcd.cs7cs3.wayfinding.server.repository;

import java.util.Optional;



import ie.tcd.cs7cs3.wayfinding.server.model.Role;
import ie.tcd.cs7cs3.wayfinding.server.model.RolesEnum;
import org.springframework.data.repository.CrudRepository;

public interface RoleRepository extends CrudRepository<Role, Long> {
	Optional<Role> findByName(RolesEnum name);
}
