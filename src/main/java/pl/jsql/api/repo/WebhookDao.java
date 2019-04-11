package pl.jsql.api.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.jsql.api.model.payment.Webhook;

@Repository
public interface WebhookDao extends CrudRepository<Webhook, Long> {

}

