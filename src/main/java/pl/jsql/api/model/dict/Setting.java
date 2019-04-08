package pl.jsql.api.model.dict;

import org.hibernate.annotations.Type;
import pl.jsql.api.enums.SettingEnum;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "setting")
public class Setting {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @Column(unique = true)
    @NotNull
    public String name;

    @Type(type = "org.hibernate.type.TextType")
    @NotNull
    public String value;

    @NotNull
    @Enumerated(EnumType.STRING)
    public SettingEnum type;

}
