package mz.org.fgh.sifmoz.backend.patientVisit

import grails.converters.JSON
import grails.rest.RestfulController
import grails.validation.ValidationException
import mz.org.fgh.sifmoz.backend.packagedDrug.PackagedDrug
import mz.org.fgh.sifmoz.backend.packagedDrug.PackagedDrugStock
import mz.org.fgh.sifmoz.backend.packaging.IPackService
import mz.org.fgh.sifmoz.backend.packaging.Pack
import mz.org.fgh.sifmoz.backend.patient.Patient
import mz.org.fgh.sifmoz.backend.distribuicaoAdministrativa.Localidade
import mz.org.fgh.sifmoz.backend.patientVisitDetails.PatientVisitDetails
import mz.org.fgh.sifmoz.backend.prescription.IPrescriptionService
import mz.org.fgh.sifmoz.backend.screening.AdherenceScreeningService
import mz.org.fgh.sifmoz.backend.screening.PregnancyScreeningService
import mz.org.fgh.sifmoz.backend.screening.RAMScreeningService
import mz.org.fgh.sifmoz.backend.screening.TBScreeningService
import mz.org.fgh.sifmoz.backend.screening.VitalSignsScreeningService
import mz.org.fgh.sifmoz.backend.stock.Stock
import mz.org.fgh.sifmoz.backend.utilities.JSONSerializer
import mz.org.fgh.sifmoz.backend.utilities.Utilities
import org.springframework.beans.factory.annotation.Autowired

import static org.springframework.http.HttpStatus.CREATED
import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.HttpStatus.NO_CONTENT
import static org.springframework.http.HttpStatus.OK

import grails.gorm.transactions.Transactional

class PatientVisitController extends RestfulController{

    IPatientVisitService patientVisitService
    IPrescriptionService prescriptionService
    IPackService packService
    VitalSignsScreeningService vitalSignsScreeningService
    @Autowired
    TBScreeningService tbScreeningService
    AdherenceScreeningService adherenceScreeningService
    @Autowired
    RAMScreeningService ramScreeningService
    PregnancyScreeningService pregnancyScreeningService

    static responseFormats = ['json', 'xml']
    static allowedMethods = [save: "POST", update: "PATCH", delete: "DELETE"]

    PatientVisitController() {
        super(PatientVisit)
    }

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        render JSONSerializer.setObjectListJsonResponse(patientVisitService.list(params)) as JSON
    }

    def show(String id) {
        render JSONSerializer.setJsonObjectResponse(patientVisitService.get(id)) as JSON
    }

    def saveRecord(PatientVisit visit){
        render JSONSerializer.setJsonObjectResponse(visit) as JSON
    }

    @Transactional
    def save() {
        PatientVisit visit = new PatientVisit()
        def objectJSON = request.JSON
        visit = objectJSON as PatientVisit

        visit.beforeInsert()
        visit.validate()

        if(objectJSON.id){
            visit.id = UUID.fromString(objectJSON.id)
            visit.patientVisitDetails.eachWithIndex { item, index ->
                item.id = UUID.fromString(objectJSON.patientVisitDetails[index].id)
                item.prescription.id = UUID.fromString(objectJSON.patientVisitDetails[index].prescription.id)
                item.prescription.prescribedDrugs.eachWithIndex { item2, index2 ->
                    item2.id = UUID.fromString(objectJSON.patientVisitDetails[index].prescription.prescribedDrugs[index2].id)
                }
                item.prescription.prescriptionDetails.eachWithIndex { item3, index3 ->
                    item3.id = UUID.fromString(objectJSON.patientVisitDetails[index].prescription.prescriptionDetails[index3].id)
                }
                item.pack.id = UUID.fromString(objectJSON.patientVisitDetails[index].pack.id)
                item.pack.packagedDrugs.eachWithIndex { item4, index4 ->
                    item4.id = UUID.fromString(objectJSON.patientVisitDetails[index].pack.packagedDrugs[index4].id)
                    item4.drug.stockList = null
                    item4.packagedDrugStocks.eachWithIndex{ item5, index5 ->
                        item5.id = UUID.fromString(objectJSON.patientVisitDetails[index].pack.packagedDrugs[index4].packagedDrugStocks[index5].id)
                    }
                }
            }
            visit.adherenceScreening.eachWithIndex{  item, index ->
                item.id = UUID.fromString(objectJSON.adherenceScreening[index].id)
            }
            visit.vitalSigns.eachWithIndex{  item, index ->
                item.id = UUID.fromString(objectJSON.vitalSigns[index].id)
            }
            visit.pregnancyScreening.eachWithIndex{  item, index ->
                item.id = UUID.fromString(objectJSON.pregnancyScreening[index].id)
            }
            visit.tbScreening.eachWithIndex{  item, index ->
                item.id = UUID.fromString(objectJSON.tbScreening[index].id)
            }
            visit.ramScreening.eachWithIndex{  item, index ->
                item.id = UUID.fromString(objectJSON.ramScreening[index].id)
            }
        }
        if (visit.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond visit.errors
            return
        }

        try {
            PatientVisit existingPatientVisit = PatientVisit.findByVisitDateAndPatient(visit.visitDate, visit.patient)
            if (existingPatientVisit != null) {
                visit.vitalSigns.each {item ->
                    item.visit = existingPatientVisit
                    vitalSignsScreeningService.save(item)
                }

                visit.pregnancyScreening.each {item ->
                    item.visit = existingPatientVisit
                    pregnancyScreeningService.save(item)
                }
                visit.ramScreening.each {item ->
                    item.visit = existingPatientVisit
                    ramScreeningService.save(item)
                }
                visit.adherenceScreening.each {item ->
                    item.visit = existingPatientVisit
                    adherenceScreeningService.save(item)
                }
                visit.tbScreening.each {item ->
                    item.visit = existingPatientVisit
                    tbScreeningService.save(item)
                }
              visit.patientVisitDetails.each {item ->
                  item.patientVisit = existingPatientVisit
                    prescriptionService.save(item.prescription)
                    packService.save(item.pack)
                }
            } else {
                visit.patientVisitDetails.each {item ->
                    prescriptionService.save(item.prescription)
                    packService.save(item.pack)
                }
                patientVisitService.save(visit)
            }
        } catch (ValidationException e) {
            transactionStatus.setRollbackOnly()
            respond visit.errors
            return
        }

        respond visit, [status: CREATED, view:"show"]
    }

    @Transactional
    def update() {
        PatientVisit visit = new PatientVisit()
        def objectJSON = request.JSON
        visit = objectJSON as PatientVisit
        if (visit == null) {
            render status: NOT_FOUND
            return
        }
        visit.id = UUID.fromString(objectJSON.id)
        visit.patientVisitDetails.eachWithIndex { item, index ->
            item.id = UUID.fromString(objectJSON.patientVisitDetails[index].id)
            item.prescription.id = UUID.fromString(objectJSON.patientVisitDetails[index].prescription.id)
            item.prescription.prescribedDrugs.eachWithIndex { item2, index2 ->
                item2.id = UUID.fromString(objectJSON.patientVisitDetails[index].prescription.prescribedDrugs[index2].id)
            }
            item.prescription.prescriptionDetails.eachWithIndex { item3, index3 ->
                item3.id = UUID.fromString(objectJSON.patientVisitDetails[index].prescription.prescriptionDetails[index3].id)
            }
            item.pack.id = UUID.fromString(objectJSON.patientVisitDetails[index].pack.id)
            item.pack.packagedDrugs.eachWithIndex { item4, index4 ->
                item4.id = UUID.fromString(objectJSON.patientVisitDetails[index].pack.packagedDrugs[index4].id)
                item4.drug.stockList = null
                item4.packagedDrugStocks.eachWithIndex{ item5, index5 ->
                    item5.id = UUID.fromString(objectJSON.patientVisitDetails[index].pack.packagedDrugs[index4].packagedDrugStocks[index5].id)
                }
            }
        }
        if (visit.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond visit.errors
            return
        }

        try {
            restoreStock(visit.patientVisitDetails.getAt(0).pack)
            savePackageDrugs(visit.patientVisitDetails.getAt(0).pack)
            patientVisitService.save(visit)
        } catch (ValidationException e) {
            respond visit.errors
            return
        }

        respond visit, [status: OK, view:"show"]
    }

    @Transactional
    def delete(String id) {
        if (id == null || patientVisitService.delete(id) == null) {
            render status: NOT_FOUND
            return
        }

        render status: NO_CONTENT
    }

    def getAllByClinicId(String clinicId, int offset, int max) {
        render JSONSerializer.setObjectListJsonResponse(patientVisitService.getAllByClinicId(clinicId, offset, max)) as JSON
    }

    def getByPatientId(String patientId) {
        render JSONSerializer.setObjectListJsonResponse(patientVisitService.getAllByPatientId(patientId)) as JSON
    }

    def getLastVisitOfPatient(String patientId) {
        render JSONSerializer.setJsonObjectResponse(patientVisitService.getLastVisitOfPatient(patientId)) as JSON
    }

    def getAllLastVisitWithScreeningOfClinic(String clinicId, int offset, int max) {
        render JSONSerializer.setObjectListJsonResponse(patientVisitService.getAllLastWithScreening(clinicId, offset, max)) as JSON
    }

    void savePackageDrugs(Pack pack) {
        // remover todas packageDrugs e respectivas packageDrugsStock antigas
        // inserir as novas packageDrugs e respectivas packageDrugsStock
        // descontar novamente o stock
    }

    void restoreStock(Pack pack) {
        for (PackagedDrug packagedDrug : pack.packagedDrugs) {
            for (PackagedDrugStock packagedDrugStock : packagedDrug.packagedDrugStocks) {
                Stock stock = Stock.findById(packagedDrugStock.stock.id)
                stock.stockMoviment += stock
            }
        }
    }
}
