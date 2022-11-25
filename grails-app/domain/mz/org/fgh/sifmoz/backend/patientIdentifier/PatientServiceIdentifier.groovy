package mz.org.fgh.sifmoz.backend.patientIdentifier

import mz.org.fgh.sifmoz.backend.base.BaseEntity
import mz.org.fgh.sifmoz.backend.clinic.Clinic
import mz.org.fgh.sifmoz.backend.episode.Episode
import mz.org.fgh.sifmoz.backend.identifierType.IdentifierType
import mz.org.fgh.sifmoz.backend.patient.Patient
import mz.org.fgh.sifmoz.backend.service.ClinicalService

class PatientServiceIdentifier extends BaseEntity {
    String id
    Date startDate
    Date endDate
    Date reopenDate
    String value
    String state
    boolean prefered
    IdentifierType identifierType
    ClinicalService service
    Clinic clinic

    Patient patient
    static belongsTo = [Patient]

    static hasMany = [episodes: Episode]

    static mapping = {
        id generator: "assigned"
    }

    static constraints = {
        value(unique: ['patient', 'service'], nullable: true)
        startDate(nullable: false, blank: false, validator: { startDate, urc ->
            return startDate != null ? startDate <= new Date() : null
        })
        endDate nullable: true
        reopenDate nullable: true
    }


//    @Override
//    String toString() {
//        return "PatientServiceIdentifier{" +
//                "patient=" + patient +
//                ", episodes=" + episodes +
//                ", id='" + id + '\'' +
//                ", startDate=" + startDate +
//                ", endDate=" + endDate +
//                ", reopenDate=" + reopenDate +
//                ", value='" + value + '\'' +
//                ", state='" + state + '\'' +
//                ", prefered=" + prefered +
//                ", identifierType=" + identifierType +
//                ", service=" + service +
//                ", clinic=" + clinic +
//                '}';
//    }
}
