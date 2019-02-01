package pl.jsql.api.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import pl.jsql.api.model.user.Session
import pl.jsql.api.repo.SessionDao

import javax.transaction.Transactional

@Transactional
@Service
class SessionService {

    @Autowired
    SessionDao sessionDao

    Boolean isLogged(String sessionHash){

        Session session = sessionDao.findBySessionHash(sessionHash)

        return session == null ? false : session.closedDate == null

    }

    def removeSession(String sessionHash) {

        Session session = sessionDao.findBySessionHash(sessionHash)

        if(session == null){
            return
        }

        session.closedDate = new Date()

        sessionDao.save(session)

    }
}
