package mz.org.fgh.sifmoz.backend.reports.patients

class ActivePatientReport {
    String id
    String reportId
    String periodType
    int year
    Date startDate
    Date endDate

    String firstNames
    String middleNames
    String lastNames
    String gender
    String age
    String cellphone
    String province
    String district
    String clinic // Report Parameter
    String nid
    Date pickupDate
    Date nextPickUpDate
    String therapeuticRegimen
    String therapeuticLine
    String dispenseType
    String patientType

    ActivePatientReport() {
    }

    ActivePatientReport(String reportId, String firstNames, String middleNames, String lastNames, String gender, String cellphone, String therapeuticRegimen, String therapeuticLine,String periodType, String dispenseType, int year, Date startDate, Date endDate) {
        this.reportId = reportId
        this.firstNames = firstNames
        this.middleNames = middleNames
        this.lastNames = lastNames
        this.gender = gender
        this.cellphone = cellphone
        this.therapeuticRegimen = therapeuticRegimen
        this.therapeuticLine = therapeuticLine
        this.periodType = periodType
        this.year = year
        this.startDate = startDate
        this.endDate = endDate
        this.dispenseType = dispenseType
    }
    static constraints = {
        id generator: "uuid"
        clinic nullable: true
        province nullable: true
        district nullable: true
        periodType nullable: false, inList: ['MONTH', 'QUARTER', 'SEMESTER', 'ANNUAL', 'SPECIFIC']
        startDate nullable: true
        endDate nullable: true
        year nullable: true
        dispenseType nullable: false
    }

    static mapping = {
        id generator: "uuid"
    }


    @Override
    String toString() {
        return "ActivePatientReport{" +
                " Id='" + id + '\'' +
                ", reportId='" + reportId + '\'' +
                ", firstNames='" + firstNames + '\'' +
                ", middleNames='" + middleNames + '\'' +
                ", lastNames='" + lastNames + '\'' +
                ", gender='" + gender + '\'' +
                ", cellphone='" + cellphone + '\'' +
                ", provinceId='" + province + '\'' +
                ", districtId='" + district + '\'' +
                ", clinic='" + clinic + '\'' +
                ", nid='" + nid + '\'' +
                ", pickupDate=" + pickupDate +
                ", nextPickUpDate=" + nextPickUpDate +
                ", therapeuticRegimen='" + therapeuticRegimen + '\'' +
                ", dispenseType='" + dispenseType + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", periodType='" + periodType + '\'' +
                '}'
    }
}
