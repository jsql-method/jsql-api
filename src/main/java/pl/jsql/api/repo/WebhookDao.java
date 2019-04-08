package pl.jsql.api.repo

import org.springframework.data.repository.CrudRepository
import pl.jsql.api.enums.RoleTypeEnum
import pl.jsql.api.model.payment.Webhook
import pl.jsql.api.model.user.Role

import javax.transaction.Transactional

@Transactional
interface WebhookDao extends CrudRepository<Webhook, Long> {

}

