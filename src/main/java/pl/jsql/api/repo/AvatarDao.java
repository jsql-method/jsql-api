package pl.jsql.api.repo;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.jsql.api.model.user.Avatar;
import pl.jsql.api.model.user.User;

import java.util.Optional;

@Repository
public interface AvatarDao extends CrudRepository<Avatar, Long> {

    Avatar findByUser(User user);

    @Modifying
    @Query("delete from Avatar a where a.user = :user")
    void removeAvatarsByUser(@Param("user") User user);

}

