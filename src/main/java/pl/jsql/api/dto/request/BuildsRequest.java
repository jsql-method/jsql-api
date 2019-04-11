package pl.jsql.api.dto.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;
import java.util.List;

public class BuildsRequest {

    @Pattern(regexp = "[0-9]{2}-[0-9]{2}-[0-9]{4}", message = "${validation.message.pattern}")
    public Date dateFrom;

    @Pattern(regexp = "[0-9]{2}-[0-9]{2}-[0-9]{4}", message = "${validation.message.pattern}")
    public Date dateTo;

    @NotNull(message = "${validation.message.notNull}")
    public List<Long> applications;

    @NotNull(message = "${validation.message.notNull}")
    public List<Long> developers;

}
