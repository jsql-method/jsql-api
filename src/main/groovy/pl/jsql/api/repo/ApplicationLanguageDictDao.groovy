package pl.jsql.api.repo

import org.springframework.data.repository.CrudRepository
import pl.jsql.api.model.dict.ApplicationLanguageDict

import javax.transaction.Transactional

@Transactional
interface ApplicationLanguageDictDao extends CrudRepository<ApplicationLanguageDict, Long> {

    ApplicationLanguageDict findByName(String name)

    ApplicationLanguageDict findByValue(String value)

}

