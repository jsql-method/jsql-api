package pl.jsql.api.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.jsql.api.enums.SettingEnum;
import pl.jsql.api.model.dict.Setting;

import java.util.Optional;

@Repository
interface SettingDao extends CrudRepository<Setting, Long> {

    Optional<Setting> findByType(SettingEnum type);

}

