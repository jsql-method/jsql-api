package pl.jsql.api.repo

import org.springframework.data.repository.CrudRepository
import pl.jsql.api.model.user.Session

import javax.transaction.Transactional

@Transactional
interface SessionDao extends CrudRepository<Session, Long> {

    Session findBySessionHash(String sessionHash)

}

