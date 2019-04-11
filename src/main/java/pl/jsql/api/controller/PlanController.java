package pl.jsql.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.jsql.api.controller.generic.ValidateController;
import pl.jsql.api.dto.response.BasicResponse;
import pl.jsql.api.dto.response.PlanResponse;
import pl.jsql.api.enums.RoleTypeEnum;
import pl.jsql.api.security.annotation.Security;
import pl.jsql.api.service.PaymentService;

@CrossOrigin
@RestController
@RequestMapping("/api/plan")
public class PlanController extends ValidateController {

    @Autowired
    private PaymentService paymentService;

    @Security(roles = {RoleTypeEnum.ADMIN, RoleTypeEnum.COMPANY_ADMIN, RoleTypeEnum.APP_ADMIN})
    @GetMapping
    public BasicResponse<PlanResponse> get() {
        PlanResponse response = paymentService.getPlan();
        return new BasicResponse<>(200, response);
    }

}
