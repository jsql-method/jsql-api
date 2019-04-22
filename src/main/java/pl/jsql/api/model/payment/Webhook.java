package pl.jsql.api.model.payment;

import org.hibernate.annotations.Type;
import pl.jsql.api.enums.PabblyStatus;

import javax.persistence.*;

@Entity
@Table(name = "pabbly_webhook")
public class Webhook {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @Type(type = "org.hibernate.type.TextType")
    public String requestText;

    @Enumerated(EnumType.STRING)
    public PabblyStatus pabblyStatus;

}
