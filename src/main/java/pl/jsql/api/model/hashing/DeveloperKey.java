package pl.jsql.api.model.hashing;

import org.hibernate.annotations.Type;
import pl.jsql.api.model.user.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "developer_key")
public class DeveloperKey {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id")
    public User user;

    @Type(type = "org.hibernate.type.TextType")
    @NotNull
    public String key;

}
