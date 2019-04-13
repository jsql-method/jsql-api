package pl.jsql.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.jsql.api.dto.request.ApplicationCreateRequest;
import pl.jsql.api.dto.request.UserRequest;
import pl.jsql.api.enums.PlansEnum;
import pl.jsql.api.enums.RoleTypeEnum;
import pl.jsql.api.enums.SettingEnum;
import pl.jsql.api.model.dict.Setting;
import pl.jsql.api.model.user.Role;
import pl.jsql.api.model.user.User;
import pl.jsql.api.repo.*;
import pl.jsql.api.service.ApplicationService;
import pl.jsql.api.service.AuthService;
import pl.jsql.api.service.UserService;

import javax.annotation.PostConstruct;

@Component
public class InitializeData {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @Autowired
    private SettingDao settingDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private UserDao userDao;

    private void initRoles() {

        if (roleDao.count() > 0) {
            return;
        }

        for (RoleTypeEnum roleType : RoleTypeEnum.values()) {
            Role role = new Role();
            role.authority = roleType;
            roleDao.save(role);
        }

    }

    private void initSettings() {

        for (SettingEnum s : SettingEnum.values()) {
            if (settingDao.findByType(s) == null) {
                Setting setting = new Setting();
                setting.type = s;
                setting.value = s.defaultValue;
                settingDao.save(setting);
            }
        }

    }

    private void createFullCompanyAdmin() {

        String email = "dawid.senko@jsql.it";
        authService.register(new UserRequest(email, "test123", "Paweł", "Stachurski", "JSQL Sp.z.o.o.", PlansEnum.LARGE));

        User user = userDao.findByEmail(email);
        userService.activateAccount(user.token);
        user = userDao.findByEmail(email);

        applicationService.create(user, new ApplicationCreateRequest("Test application"));

    }

    @PostConstruct
    public void init() {
        initRoles();
        initSettings();
        createFullCompanyAdmin();
    }

}
