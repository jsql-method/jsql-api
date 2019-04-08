package pl.jsql.api.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.jsql.api.enums.RoleTypeEnum;
import pl.jsql.api.model.user.Role;

import java.util.Optional;

@Repository
public interface RoleDao extends CrudRepository<Role, Long> {

    Optional<Role> findByAuthority(RoleTypeEnum authority);

}

