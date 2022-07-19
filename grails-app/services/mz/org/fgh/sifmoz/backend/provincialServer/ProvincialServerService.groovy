package mz.org.fgh.sifmoz.backend.provincialServer

import grails.gorm.services.Service

@Service(ProvincialServer)
interface ProvincialServerService {

    ProvincialServer get(Serializable id)

    List<ProvincialServer> list(Map args)

    Long count()

    ProvincialServer delete(Serializable id)

    ProvincialServer save(ProvincialServer provincialServer)

}
