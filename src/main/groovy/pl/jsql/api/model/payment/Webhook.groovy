package pl.jsql.api.model.payment

import org.hibernate.annotations.Type
import pl.jsql.api.model.user.Company

import javax.persistence.*
import javax.validation.constraints.NotNull


@Entity
@Table(name = "pabbly_webhook")
class Webhook {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id

    @Type(type = "org.hibernate.type.TextType")
    String requestText

}
