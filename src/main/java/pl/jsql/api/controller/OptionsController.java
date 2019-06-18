package pl.jsql.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.jsql.api.controller.generic.ValidateController;
import pl.jsql.api.dto.request.OptionsRequest;
import pl.jsql.api.dto.request.ProductionToggleRequest;
import pl.jsql.api.dto.response.BasicResponse;
import pl.jsql.api.dto.response.MessageResponse;
import pl.jsql.api.dto.response.OptionsResponse;
import pl.jsql.api.dto.response.OptionsValuesResponse;
import pl.jsql.api.enums.RoleTypeEnum;
import pl.jsql.api.security.annotation.Security;
import pl.jsql.api.service.OptionsService;

import javax.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/api/options")
public class OptionsController extends ValidateController {

    @Autowired
    private OptionsService optionsService;

    @Security(roles = {RoleTypeEnum.ADMIN, RoleTypeEnum.COMPANY_ADMIN, RoleTypeEnum.APP_ADMIN})
    @GetMapping("/{id}")
    public BasicResponse<OptionsResponse> get(@PathVariable("id") Long id) {
        OptionsResponse response = optionsService.getByApplicationId(id);
        return new BasicResponse<>(200, response);
    }

    @Security(roles = {RoleTypeEnum.ADMIN, RoleTypeEnum.COMPANY_ADMIN, RoleTypeEnum.APP_ADMIN})
    @PatchMapping("/{id}")
    public BasicResponse<MessageResponse> update(@PathVariable("id") Long id, @RequestBody @Valid OptionsRequest optionsRequest) {
        MessageResponse response = optionsService.update(id, optionsRequest);
        return new BasicResponse<>(200, response);
    }

    @Security(roles = {RoleTypeEnum.ADMIN, RoleTypeEnum.COMPANY_ADMIN, RoleTypeEnum.APP_ADMIN})
    @PatchMapping("/toggle-production/{id}")
    public BasicResponse<MessageResponse> update(@PathVariable("id") Long id, @RequestBody @Valid ProductionToggleRequest productionToggleRequest) {
        MessageResponse response = optionsService.toggleProduction(id, productionToggleRequest);
        return new BasicResponse<>(200, response);
    }

    @Security(roles = {RoleTypeEnum.ADMIN, RoleTypeEnum.COMPANY_ADMIN, RoleTypeEnum.APP_ADMIN})
    @PatchMapping("/purge-queries/{id}")
    public BasicResponse<MessageResponse> purgeQueries(@PathVariable("id") Long id) {
        MessageResponse response = optionsService.purgeQueries(id);
        return new BasicResponse<>(200, response);
    }

    @Security(roles = {RoleTypeEnum.ADMIN, RoleTypeEnum.COMPANY_ADMIN, RoleTypeEnum.APP_ADMIN})
    @PatchMapping("/purge-options/{id}")
    public BasicResponse<MessageResponse> purgeOptions(@PathVariable("id") Long id) {
        MessageResponse response = optionsService.purgeOptions(id);
        return new BasicResponse<>(200, response);
    }

    @Security(roles = {RoleTypeEnum.ADMIN, RoleTypeEnum.COMPANY_ADMIN, RoleTypeEnum.APP_ADMIN})
    @GetMapping("/values")
    public BasicResponse<OptionsValuesResponse> getOptionsValue() {
        OptionsValuesResponse response = optionsService.getValues();
        return new BasicResponse<>(200, response);
    }

}
