package pl.jsql.api.repo

import org.springframework.data.repository.CrudRepository
import pl.jsql.api.model.payment.Webhook
import pl.jsql.api.model.user.Avatar
import pl.jsql.api.model.user.User

import javax.transaction.Transactional

@Transactional
interface AvatarDao extends CrudRepository<Avatar, Long> {

    Avatar findByUser(User user)

}

