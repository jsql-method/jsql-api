package pl.jsql.api.repo

import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import pl.jsql.api.model.user.Session
import pl.jsql.api.model.user.User

import javax.transaction.Transactional

@Transactional
interface SessionDao extends CrudRepository<Session, Long> {
    Session findBySessionHash(String sessionHash)

    @Modifying
    @Query("delete from Session s where s.user = :user")
    void removeAllByUser(@Param('user') User user)
}

