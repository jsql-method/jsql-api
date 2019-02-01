package pl.jsql.api.service

import org.apache.commons.lang3.RandomStringUtils
import org.apache.commons.net.ntp.TimeStamp
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import pl.jsql.api.dto.UserRequest
import pl.jsql.api.enums.PlansEnum
import pl.jsql.api.enums.RoleTypeEnum
import pl.jsql.api.enums.SettingEnum
import pl.jsql.api.model.payment.Plans
import pl.jsql.api.model.user.User
import pl.jsql.api.repo.*
import pl.jsql.api.security.service.SecurityService

import javax.transaction.Transactional
import java.text.SimpleDateFormat

import static pl.jsql.api.enums.HttpMessageEnum.SUCCESS


@Transactional
@Service
class PaymentService {

    @Autowired
    UserDao userDao

    @Autowired
    AuthService authService

    @Autowired
    PlansDao plansDao

    @Autowired
    SettingsDao settingsDao

    @Autowired
    SecurityService securityService

    @Autowired
    ApplicationDao applicationDao

    @Autowired
    RoleDao roleDao

    private static final String SUBSCRIPTION_CREATE = "subscription_create"
    private static final String SUBSCRIPTION_ACTIVATE = "subscription_activate"
    private static final String PAYMENT_FAILURE = "payment_failure"

    void activeOrUnactivePlan(def request) {

        String eventType = request.event_type

        switch (eventType) {

            case SUBSCRIPTION_ACTIVATE:
            case SUBSCRIPTION_CREATE:

                String userEmail = request.data.email_id
                String planDescription = request.data.plan.plan_code
                int trialPeriod = request.data.plan.trial_period

                User user = userDao.findByEmail(userEmail)

                if (user == null) {

                    UserRequest userRequest = new UserRequest(RandomStringUtils.randomAlphanumeric(8), userEmail, "John", "Doe")
                    userRequest.origin = settingsDao.findByType(SettingEnum.ORIGIN_URL)
                    authService.register(userRequest)
                    user = userDao.findByEmail(userEmail)

                }

                Plans plan = plansDao.findFirstByCompany(user.company)

                switch (planDescription.toLowerCase()) {

                    case PlansEnum.STARTER.name.toLowerCase():
                        plan.active = true
                        plan.plan = PlansEnum.STARTER
                        plan.isTrial = false
                        break

                    case PlansEnum.BUSINESS.name.toLowerCase():
                        plan.active = true
                        plan.plan = PlansEnum.BUSINESS
                        plan.isTrial = eventType == SUBSCRIPTION_CREATE
                        plan.trialPeriod = trialPeriod
                        break

                    case PlansEnum.LARGE.name.toLowerCase():
                        plan.active = true
                        plan.plan = PlansEnum.LARGE
                        plan.isTrial = eventType == SUBSCRIPTION_CREATE
                        plan.trialPeriod = trialPeriod
                        break

                }

                plansDao.save(plan)

                break

            case PAYMENT_FAILURE:

                String userEmail = request.data.transaction.payment_method.email
                User user = userDao.findByEmail(userEmail)

                if (user == null) break

                Plans plan = plansDao.findFirstByCompany(user.company)
                plan.active = false
                plansDao.save(plan)

                break

            default:
                break
        }
    }

    def getPlan() {

        User user = securityService.getCurrentAccount()

        if (user.role.authority != RoleTypeEnum.COMPANY_ADMIN) {

            user = userDao.findByCompanyAndRole(user.company, roleDao.findByAuthority(RoleTypeEnum.COMPANY_ADMIN)).get(0)

        }

        int usersCount = userDao.countByCompany(user.company)
        int activeApplications = applicationDao.countByUser(user)
        Plans plan = plansDao.findFirstByCompany(user.company)
        SimpleDateFormat simplify = new SimpleDateFormat("dd-MM-YYY")
        def data = [
                activationDate    : simplify.format(plan.activationDate),
                trial             : plan.isTrial,
                trialRemainingDays: plan.trialPeriod,
                active            : plan.active,
                maxApps           : plan.plan.appQty,
                usedApps          : activeApplications,
                id                : plan.id,
                name              : plan.plan.name,
                maxUsers          : plan.plan.userQty,
                usedUsers         : usersCount
        ]

        return [code: SUCCESS.getCode(), data: data]
    }
}
