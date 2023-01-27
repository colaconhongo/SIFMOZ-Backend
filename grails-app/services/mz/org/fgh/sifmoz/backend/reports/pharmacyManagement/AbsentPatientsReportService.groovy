package mz.org.fgh.sifmoz.backend.reports.pharmacyManagement

import grails.gorm.services.Service
import grails.gorm.transactions.Transactional
import mz.org.fgh.sifmoz.backend.clinic.Clinic
import mz.org.fgh.sifmoz.backend.convertDateUtils.ConvertDateUtils
import mz.org.fgh.sifmoz.backend.episode.Episode
import mz.org.fgh.sifmoz.backend.multithread.ReportSearchParams
import mz.org.fgh.sifmoz.backend.packaging.IPackService
import mz.org.fgh.sifmoz.backend.reports.common.IReportProcessMonitorService
import mz.org.fgh.sifmoz.backend.reports.common.ReportProcessMonitor
import mz.org.fgh.sifmoz.backend.service.ClinicalService
import org.springframework.beans.factory.annotation.Autowired

@Transactional
@Service(AbsentPatientsReport)
abstract class AbsentPatientsReportService implements IAbsentPatientsReportService{
    @Autowired
    IPackService packService
    @Autowired
    IReportProcessMonitorService reportProcessMonitorService

    public static final String PROCESS_STATUS_PROCESSING_FINISHED = "Processamento terminado"

    @Override
    List<AbsentPatientsReport> getReportDataByReportId(String reportId) {
        return AbsentPatientsReport.findAllByReportId(reportId)
    }

    @Override
    void processReportAbsentDispenseRecords(ReportSearchParams searchParams, ReportProcessMonitor processMonitor) {
        Clinic clinic = Clinic.findById(searchParams.clinicId)
        ClinicalService clinicalService = ClinicalService.findById(searchParams.clinicalService)
        List absentReferredPatients = packService.getAbsentPatientsByClinicalServiceAndClinicOnPeriod(clinicalService,clinic,searchParams.startDate,searchParams.endDate)
        double percentageUnit

        if (absentReferredPatients.size() == 0) {
            processMonitor.setProgress(100)
            processMonitor.setMsg(PROCESS_STATUS_PROCESSING_FINISHED)
            reportProcessMonitorService.save(processMonitor)
        }  else{
            percentageUnit = 100/absentReferredPatients.size()
        }
        for (int i = 0; i < absentReferredPatients.size(); i ++) {
            Object item = absentReferredPatients[i]
            //Episode episode = (Episode) item[0]
            AbsentPatientsReport absentPatient = new AbsentPatientsReport()
            absentPatient.setClinic(clinic.clinicName)
            absentPatient.setStartDate(searchParams.startDate)
            absentPatient.setEndDate(searchParams.endDate)
            absentPatient.setClinicalServiceId(clinicalService.code)
            absentPatient.setReportId(searchParams.id)
            absentPatient.setPeriodType(searchParams.periodType)
            absentPatient.setReportId(searchParams.id)
            absentPatient.setNid(item[0])
            absentPatient.setName(String.valueOf(item[1]) +' '+String.valueOf(item[3]))

            absentPatient.setDateMissedPickUp(item[4] as Date)
            Date abandonmentDate = ConvertDateUtils.addDaysDate(absentPatient.dateMissedPickUp,60)
            if(searchParams.endDate.after(abandonmentDate)) {
                absentPatient.setDateIdentifiedAbandonment(abandonmentDate)
            }
            if(item[5] != null){
                absentPatient.setContact(String.valueOf(item[5]))
            }
     //       if(item[3] != null) {
      //          absentPatient.setReturnedPickUp(item[3] as Date)
       //     }
            save(absentPatient)
            processMonitor.setProgress(processMonitor.getProgress() + percentageUnit)
            if (100 == processMonitor.progress.intValue() || 99 == processMonitor.progress.intValue()) {
                processMonitor.setProgress(100)
                processMonitor.setMsg(PROCESS_STATUS_PROCESSING_FINISHED)
            }
            reportProcessMonitorService.save(processMonitor)
        }
    }
}
