package pl.jsql.api.repo

import org.springframework.data.repository.CrudRepository
import pl.jsql.api.enums.RoleTypeEnum
import pl.jsql.api.model.user.Role

import javax.transaction.Transactional

@Transactional
interface RoleDao extends CrudRepository<Role, Long> {

    Role findByAuthority(RoleTypeEnum authority)
}

