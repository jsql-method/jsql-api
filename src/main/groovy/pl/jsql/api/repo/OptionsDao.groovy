package pl.jsql.api.repo

import org.springframework.data.repository.CrudRepository
import pl.jsql.api.model.hashing.Application
import pl.jsql.api.model.hashing.Options

import javax.transaction.Transactional

@Transactional
interface OptionsDao extends CrudRepository<Options, Long> {

    Options findByApplication(Application application)

}

