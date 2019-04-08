package pl.jsql.api.model.dict;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "database_dialect_dict")
public class  DatabaseDialectDict {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @Column(unique = true)
    @NotNull
    public String name;

    @Column(unique = true)
    @NotNull
    public String value;

}
