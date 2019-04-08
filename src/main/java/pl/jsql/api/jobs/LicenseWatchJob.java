package pl.jsql.api.jobs

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import pl.jsql.api.enums.RoleTypeEnum
import pl.jsql.api.model.payment.Plans
import pl.jsql.api.model.user.User
import pl.jsql.api.repo.PlansDao
import pl.jsql.api.repo.RoleDao
import pl.jsql.api.repo.UserDao
import pl.jsql.api.service.UserService

@Component
public class  LicenseWatchJob {

    @Autowired
    UserDao userDao

    @Autowired
    PlansDao plansDao

    @Autowired
    RoleDao roleDao

    @Autowired
    UserService userService


    @Transactional
    @Scheduled(cron = "0 0 0 * * ?")
    def scheduleJob() {

        List<Plans> plans = plansDao.findAll()

        for (Plans plan : plans) {

            if (plan.isTrial) {

                int remainingDays = plan.trialPeriod

                if (remainingDays > 0) {

                    if (remainingDays <= 3) {

                        User user = userDao.findByCompanyAndRole(plan.company, roleDao.findByAuthority(RoleTypeEnum.COMPANY_ADMIN))
                        userService.sendMessageEndLicense(user, remainingDays)

                    }

                    plan.trialPeriod = --remainingDays

                } else if (remainingDays == 0) {
                    plan.active = false
                }
            }


        }
    }

}
