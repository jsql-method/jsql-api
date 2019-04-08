package pl.jsql.api.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import pl.jsql.api.controller.generic.ValidateController
import pl.jsql.api.dto.response.BasicResponse
import pl.jsql.api.enums.RoleTypeEnum
import pl.jsql.api.security.annotation.Security
import pl.jsql.api.service.PaymentService

@CrossOrigin
@RestController
@RequestMapping("/api/plan")
public class  PlanController extends ValidateController {

    @Autowired
    PaymentService paymentService

    @Security(roles = [RoleTypeEnum.ADMIN, RoleTypeEnum.COMPANY_ADMIN, RoleTypeEnum.APP_ADMIN])
    @GetMapping
    BasicResponse get() {
        def response = paymentService.getPlan()
        return new BasicResponse(status: 200, data: response)
    }

}
