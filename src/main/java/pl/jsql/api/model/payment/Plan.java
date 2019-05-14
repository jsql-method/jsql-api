package pl.jsql.api.model.payment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import pl.jsql.api.enums.PlansEnum;
import pl.jsql.api.model.user.Company;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;


@Entity
@Table(name = "plan")
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @JsonIgnore
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "company_id")
    public Company company;

    @NotNull
    @Enumerated(EnumType.STRING)
    public PlansEnum plan;

    @NotNull
    @JsonFormat(pattern = "dd-MM-yyyy hh:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    public Date activationDate;

    @JsonFormat(pattern = "dd-MM-yyyy")
    @Temporal(TemporalType.DATE)
    public Date expiryDate;

    @NotNull
    public String pabblySubscriptionId;

    public Boolean active = false;

    public Boolean trial = false;

    public Integer trialDays = 0;

    public boolean hadTrial = false;

    public boolean doubledSubscriptions = false;


}
