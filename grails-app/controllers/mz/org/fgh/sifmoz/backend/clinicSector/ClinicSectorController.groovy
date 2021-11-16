package mz.org.fgh.sifmoz.backend.clinicSector

import grails.converters.JSON
import grails.rest.RestfulController
import grails.validation.ValidationException
import static org.springframework.http.HttpStatus.CREATED
import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.HttpStatus.NO_CONTENT
import static org.springframework.http.HttpStatus.OK

import grails.gorm.transactions.Transactional


class ClinicSectorController extends RestfulController{

    IClinicSectorService clinicSectorService

    static responseFormats = ['json', 'xml']
    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    ClinicSectorController() {
        super(ClinicSector)
    }

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond clinicSectorService.list(params), model:[clinicSectorCount: clinicSectorService.count()]
    }

    def show(Long id) {
        respond clinicSectorService.get(id)
    }

    @Transactional
    def save(ClinicSector clinicSector) {
        if (clinicSector == null) {
            render status: NOT_FOUND
            return
        }
        if (clinicSector.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond clinicSector.errors
            return
        }

        try {
            clinicSectorService.save(clinicSector)
        } catch (ValidationException e) {
            respond clinicSector.errors
            return
        }

        respond clinicSector, [status: CREATED, view:"show"]
    }

    @Transactional
    def update(ClinicSector clinicSector) {
        if (clinicSector == null) {
            render status: NOT_FOUND
            return
        }
        if (clinicSector.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond clinicSector.errors
            return
        }

        try {
            clinicSectorService.save(clinicSector)
        } catch (ValidationException e) {
            respond clinicSector.errors
            return
        }

        respond clinicSector, [status: OK, view:"show"]
    }

    @Transactional
    def delete(Long id) {
        if (id == null || clinicSectorService.delete(id) == null) {
            render status: NOT_FOUND
            return
        }

        render status: NO_CONTENT
    }

    def getAllByClinicId(String clinicId, int offset, int max) {
            render clinicSectorService.getAllByClinicId(clinicId, offset, max) as JSON

    }
}
