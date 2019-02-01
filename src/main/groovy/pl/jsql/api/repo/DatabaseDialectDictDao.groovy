package pl.jsql.api.repo

import org.springframework.data.repository.CrudRepository
import pl.jsql.api.model.dict.DatabaseDialectDict

import javax.transaction.Transactional

@Transactional
interface DatabaseDialectDictDao extends CrudRepository<DatabaseDialectDict, Long> {

    DatabaseDialectDict findByName(String name)

    DatabaseDialectDict findByValue(String value)
}

