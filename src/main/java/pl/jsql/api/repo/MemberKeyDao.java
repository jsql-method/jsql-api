package pl.jsql.api.repo

import org.springframework.data.repository.CrudRepository
import pl.jsql.api.model.hashing.MemberKey
import pl.jsql.api.model.user.User

import javax.transaction.Transactional

@Transactional
interface MemberKeyDao extends CrudRepository<MemberKey, Long> {

    MemberKey findByUser(User user)

    MemberKey findByKey(String key)

    void deleteByUser(User user)

}

