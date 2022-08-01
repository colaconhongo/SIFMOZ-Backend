package mz.org.fgh.sifmoz.backend.migration.stage

import grails.rest.RestfulController
import grails.validation.ValidationException
import mz.org.fgh.sifmoz.backend.healthInformationSystem.ISystemConfigsService
import mz.org.fgh.sifmoz.backend.healthInformationSystem.SystemConfigs
import mz.org.fgh.sifmoz.backend.migration.base.engine.MigrationEngineImpl
import mz.org.fgh.sifmoz.backend.migration.entity.patient.PatientMigrationRecord
import mz.org.fgh.sifmoz.backend.migration.entity.stock.StockCenterMigrationRecord
import mz.org.fgh.sifmoz.backend.migration.entity.stock.StockMigrationRecord
import mz.org.fgh.sifmoz.backend.migration.params.PatientMigrationSearchParams
import mz.org.fgh.sifmoz.backend.migration.params.stock.StockCenterMigrationSearchParams
import mz.org.fgh.sifmoz.backend.migration.params.stock.StockMigrationSearchParams
import mz.org.fgh.sifmoz.backend.multithread.ExecutorThreadProvider

import java.util.concurrent.ExecutorService

import static org.springframework.http.HttpStatus.CREATED
import static org.springframework.http.HttpStatus.NOT_FOUND
import static org.springframework.http.HttpStatus.NO_CONTENT
import static org.springframework.http.HttpStatus.OK

import grails.gorm.transactions.Transactional

class MigrationController extends RestfulController{

    MigrationStageService migrationStageService
    MigrationEngineImpl migrationEngine
    ISystemConfigsService systemConfigsService
    private static ExecutorService executor;
    private List<SystemConfigs> systemConfigs

    static responseFormats = ['json', 'xml']
    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    MigrationController() {
        super(MigrationStage.class)
        systemConfigs = SystemConfigs.list()
        executor = ExecutorThreadProvider.getInstance().getExecutorService();
    }

    def index(Integer max) {
        initStockCenterMigrationEngine()
        //initPatientMigrationEngine()
        //params.max = Math.min(max ?: 10, 100)
        //respond migrationStageService.list(params), model:[migrationStageCount: migrationStageService.count()]
        //initStockMigrationEngine()
        render status: NOT_FOUND
    }

    def show(String id) {
        respond migrationStageService.get(id)
    }

    def startMigration() {

    }

    private void initPatientMigrationEngine() {
        PatientMigrationSearchParams params = new PatientMigrationSearchParams()
        MigrationEngineImpl<PatientMigrationRecord> patientMigrationEngine = new MigrationEngineImpl<>(params, MigrationEngineImpl.PATIENT_MIGRATION_ENGINE)
        executor.execute(patientMigrationEngine)
    }

    private static void initStockMigrationEngine() {
        StockMigrationSearchParams params = new StockMigrationSearchParams()
        MigrationEngineImpl<StockMigrationRecord> stockMigrationEngine = new MigrationEngineImpl<>(params, MigrationEngineImpl.PATIENT_MIGRATION_ENGINE)
        executor.execute(stockMigrationEngine)
    }

    private static void initStockCenterMigrationEngine() {
        StockCenterMigrationSearchParams params = new StockCenterMigrationSearchParams()
        MigrationEngineImpl<StockCenterMigrationRecord> stockCenterMigrationEngine = new MigrationEngineImpl<>(params, MigrationEngineImpl.PATIENT_MIGRATION_ENGINE)
        executor.execute(stockCenterMigrationEngine)
    }


    @Transactional
    def save(MigrationStage migrationStage) {
        if (migrationStage == null) {
            render status: NOT_FOUND
            return
        }
        if (migrationStage.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond migrationStage.errors
            return
        }

        try {
            migrationStageService.save(migrationStage)
        } catch (ValidationException e) {
            respond migrationStage.errors
            return
        }

        respond migrationStage, [status: CREATED, view:"show"]
    }

    @Transactional
    def update(MigrationStage migrationStage) {
        if (migrationStage == null) {
            render status: NOT_FOUND
            return
        }
        if (migrationStage.hasErrors()) {
            transactionStatus.setRollbackOnly()
            respond migrationStage.errors
            return
        }

        try {
            migrationStageService.save(migrationStage)
        } catch (ValidationException e) {
            respond migrationStage.errors
            return
        }

        respond migrationStage, [status: OK, view:"show"]
    }

    @Transactional
    def delete(Long id) {
        if (id == null || migrationStageService.delete(id) == null) {
            render status: NOT_FOUND
            return
        }

        render status: NO_CONTENT
    }
}