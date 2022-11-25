package mz.org.fgh.sifmoz.backend.stockentrance

import mz.org.fgh.sifmoz.backend.base.BaseEntity
import mz.org.fgh.sifmoz.backend.clinic.Clinic
import mz.org.fgh.sifmoz.backend.stock.Stock

class StockEntrance extends BaseEntity {
    String id
    String orderNumber
    Date dateReceived
    Clinic clinic
    static hasMany = [stocks: Stock]

    static mapping = {
        id generator: "assigned"
    }

    static constraints = {
        orderNumber(nullable: false, blank: false, unique: true)
        dateReceived(nullable: false, blank: false, validator: { dateReceived, urc ->
            return ((dateReceived <= new Date()))
        })
    }

    @Override
    String toString() {
        return "StockEntrance{" +
                "stocks=" + stocks +
                ", id='" + id + '\'' +
                ", orderNumber='" + orderNumber + '\'' +
                ", dateReceived=" + dateReceived +
                ", clinic=" + clinic +
                '}'
    }
}
