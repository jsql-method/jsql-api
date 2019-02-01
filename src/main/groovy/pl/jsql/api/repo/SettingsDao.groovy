package pl.jsql.api.repo

import org.springframework.data.repository.CrudRepository
import pl.jsql.api.enums.SettingEnum
import pl.jsql.api.model.dict.Settings

import javax.transaction.Transactional

@Transactional
interface SettingsDao extends CrudRepository<Settings, Long> {
    Settings findByType(SettingEnum type)
}

