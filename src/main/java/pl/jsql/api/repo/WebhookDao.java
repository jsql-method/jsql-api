package pl.jsql.api.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.jsql.api.model.payment.Webhook;

@Repository
interface WebhookDao extends CrudRepository<Webhook, Long> {

}

