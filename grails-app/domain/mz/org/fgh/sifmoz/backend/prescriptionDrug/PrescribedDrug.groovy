package mz.org.fgh.sifmoz.backend.prescriptionDrug

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonManagedReference
import mz.org.fgh.sifmoz.backend.base.BaseEntity
import mz.org.fgh.sifmoz.backend.drug.Drug
import mz.org.fgh.sifmoz.backend.prescription.Prescription

class PrescribedDrug extends BaseEntity {
    String id
    double amtPerTime
    int timesPerDay
    int qtyPrescribed
    String form
    boolean modified
    @JsonManagedReference
    Drug drug
    @JsonBackReference
    Prescription prescription
    static belongsTo = [Prescription]

    static mapping = {
        id generator: "uuid"
    }
    static constraints = {
        timesPerDay(min: 1)
    }
}
