package pl.jsql.api.dto.request;

import pl.jsql.api.validator.UniqueApplicationForCurrentCompany;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class ApplicationCreateRequest {

    @NotEmpty(message = "${validation.message.notEmpty}")
    @Size(min = 1, max = 100, message = "${validation.message.size}")
    @UniqueApplicationForCurrentCompany
    public String name;

}
