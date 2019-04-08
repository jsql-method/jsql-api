package pl.jsql.api.model.payment;

import com.fasterxml.jackson.annotation.JsonFormat;
import pl.jsql.api.enums.PlansEnum;
import pl.jsql.api.model.user.Company;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;


@Entity
@Table(name = "plans")
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "company_id")
    public Company company;

    @NotNull
    @Enumerated(EnumType.STRING)
    public PlansEnum plan;

    @NotNull
    @JsonFormat(pattern = "dd-MM-yyyy hh:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    public Date activationDate;

    public Integer trialPeriod;

    public Boolean isTrial = false;

    public Boolean active = false;

}
