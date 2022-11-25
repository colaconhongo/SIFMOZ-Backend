package mz.org.fgh.sifmoz.backend.stockcenter

import mz.org.fgh.sifmoz.backend.base.BaseEntity
import mz.org.fgh.sifmoz.backend.clinic.Clinic

class StockCenter extends BaseEntity {
    String id
    String name
    boolean prefered
    Clinic clinic
    String code

    static mapping = {
        id generator: "assigned"
    }

    static constraints = {
        name(nullable: false, blank: false)
        code(nullable: false, blank: false, unique: true)
    }

    @Override
    String toString() {
        return "StockCenter{" +
                "name='" + name + '\'' +
                ", prefered=" + prefered +
                '}'
    }
}
