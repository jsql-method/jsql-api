package pl.jsql.api.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.jsql.api.model.user.Avatar;
import pl.jsql.api.model.user.User;

import java.util.Optional;

@Repository
public interface AvatarDao extends CrudRepository<Avatar, Long> {

    Avatar findByUser(User user);

}

