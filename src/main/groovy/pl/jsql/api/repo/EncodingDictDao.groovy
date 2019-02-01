package pl.jsql.api.repo

import org.springframework.data.repository.CrudRepository
import pl.jsql.api.model.dict.EncodingDict

import javax.transaction.Transactional

@Transactional
interface EncodingDictDao extends CrudRepository<EncodingDict, Long> {

    EncodingDict findByName(String name)

    EncodingDict findByValue(String value)
}

