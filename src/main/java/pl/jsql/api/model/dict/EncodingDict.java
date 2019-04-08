package pl.jsql.api.model.dict;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "encoding_dict")
public class  EncodingDict {

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
