/*
Job Management & Tracking System (JMTS) 
Copyright (C) 2017  D P Bennett & Associates Limited

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.

Email: info@dpbennett.com.jm
 */
package jm.com.dpbennett.jmts.managers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import javax.faces.event.AjaxBehaviorEvent;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import jm.com.dpbennett.business.entity.Address;
import jm.com.dpbennett.business.entity.Client;
import jm.com.dpbennett.business.entity.Contact;
import jm.com.dpbennett.business.entity.Department;
import jm.com.dpbennett.business.entity.Job;
import jm.com.dpbennett.business.entity.JobManagerUser;
import jm.com.dpbennett.business.entity.JobSample;
import jm.com.dpbennett.business.entity.Service;
import jm.com.dpbennett.business.entity.SystemOption;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import jm.com.dpbennett.business.entity.management.BusinessEntityManagement;
import jm.com.dpbennett.business.entity.utils.BusinessEntityUtils;
import jm.com.dpbennett.wal.utils.BeanUtils;
import jm.com.dpbennett.wal.utils.ReportUtils;

/**
 *
 * @author Desmond Bennett
 */
public class JobContractManager implements Serializable, BusinessEntityManagement {

    @PersistenceUnit(unitName = "JMTSPU")
    private EntityManagerFactory EMF1;
    private Integer longProcessProgress;
    private JobManager jobManager;

    /**
     * Creates a new instance of JobManagerBean
     */
    public JobContractManager() {
        init();
    }

    public JobManager getJobManager() {
        if (jobManager == null) {
            jobManager = BeanUtils.findBean("jobManager");
        }
        return jobManager;
    }

    public Job getCurrentJob() {
        return getJobManager().getCurrentJob();
    }

    private void init() {
    }

    public void reset() {
        init();
    }

    public JobManagerUser getUser() {
        return getJobManager().getUser();
    }

    public EntityManager getEntityManager1() {
        return EMF1.createEntityManager();
    }

    public StreamedContent getServiceContractStreamContent() {
        EntityManager em;

        try {

            em = getEntityManager1();

            String filePath = (String) SystemOption.getOptionValueObject(em, "serviceContract");
            FileInputStream stream = createServiceContractExcelFileInputStream(em, getUser(), getCurrentJob().getId(), filePath);

            // tk remove?
            DefaultStreamedContent dsc = new DefaultStreamedContent(stream, "application/xls", "servicecontract.xls");

            return dsc;

        } catch (Exception e) {
            System.out.println(e);
        }

        return null;
    }

    public Integer getLongProcessProgress() {
        if (longProcessProgress == null) {
            longProcessProgress = 0;
        } else {
            if (longProcessProgress < 10) {
                // this is to ensure that this method does not make the progress
                // complete as this is done elsewhere.
                longProcessProgress = longProcessProgress + 1;
            }
        }

        return longProcessProgress;
    }

    public void onLongProcessComplete() {
        longProcessProgress = 0;
    }

    public void setLongProcessProgress(Integer longProcessProgress) {
        this.longProcessProgress = longProcessProgress;
    }

    public StreamedContent getServiceContractFile() {
        StreamedContent serviceContractStreamContent = null;

        try {

            serviceContractStreamContent = getServiceContractStreamContent();

            setLongProcessProgress(100);
        } catch (Exception e) {
            System.out.println(e);
            setLongProcessProgress(100);
        }

        return serviceContractStreamContent;
    }

    // service requested - calibration
    public Boolean getServiceRequestedCalibration() {
        if (getCurrentJob() != null) {
            return getCurrentJob().getServiceContract().getServiceRequestedCalibration();
        } else {
            return false;
        }
    }

    public void setServiceRequestedCalibration(Boolean b) {
        getCurrentJob().getServiceContract().setServiceRequestedCalibration(b);
    }

    // service requested - label evaluation
    public Boolean getServiceRequestedLabelEvaluation() {
        if (getCurrentJob() != null) {
            return getCurrentJob().getServiceContract().getServiceRequestedLabelEvaluation();
        } else {
            return false;
        }
    }

    public void setServiceRequestedLabelEvaluation(Boolean b) {
        getCurrentJob().getServiceContract().setServiceRequestedLabelEvaluation(b);
    }

    // service requested - inspection
    public Boolean getServiceRequestedInspection() {
        if (getCurrentJob() != null) {
            return getCurrentJob().getServiceContract().getServiceRequestedInspection();
        } else {
            return false;
        }
    }

    public void setServiceRequestedInspection(Boolean b) {
        getCurrentJob().getServiceContract().setServiceRequestedInspection(b);
    }

    // service requested - consultancy
    public Boolean getServiceRequestedConsultancy() {
        if (getCurrentJob() != null) {
            return getCurrentJob().getServiceContract().getServiceRequestedConsultancy();
        } else {
            return false;
        }
    }

    public void setServiceRequestedConsultancy(Boolean b) {
        getCurrentJob().getServiceContract().setServiceRequestedConsultancy(b);
    }

    // service requested - training
    public Boolean getServiceRequestedTraining() {
        if (getCurrentJob() != null) {
            return getCurrentJob().getServiceContract().getServiceRequestedTraining();
        } else {
            return false;
        }
    }

    public void setServiceRequestedTraining(Boolean b) {
        getCurrentJob().getServiceContract().setServiceRequestedTraining(b);
    }

    // service requested - other
    public Boolean getServiceRequestedOther() {
        if (getCurrentJob() != null) {
            return getCurrentJob().getServiceContract().getServiceRequestedOther();
        } else {
            return false;
        }
    }

    public void setServiceRequestedOther(Boolean b) {
        getCurrentJob().getServiceContract().setServiceRequestedOther(b);
        if (!b) {
            getCurrentJob().getServiceContract().setServiceRequestedOtherText(null);
        }
    }

    //Intended Market
    //intended martket - local
    public Boolean getIntendedMarketLocal() {
        if (getCurrentJob() != null) {
            return getCurrentJob().getServiceContract().getIntendedMarketLocal();
        } else {
            return false;
        }
    }

    public void setIntendedMarketLocal(Boolean b) {
        getCurrentJob().getServiceContract().setIntendedMarketLocal(b);
    }

    // intended martket - caricom
    public Boolean getIntendedMarketCaricom() {
        if (getCurrentJob() != null) {
            return getCurrentJob().getServiceContract().getIntendedMarketCaricom();
        } else {
            return false;
        }
    }

    public void setIntendedMarketCaricom(Boolean b) {
        getCurrentJob().getServiceContract().setIntendedMarketCaricom(b);
    }

    // intended martket - UK
    public Boolean getIntendedMarketUK() {
        if (getCurrentJob() != null) {
            return getCurrentJob().getServiceContract().getIntendedMarketUK();
        } else {
            return false;
        }
    }

    public void setIntendedMarketUK(Boolean b) {
        getCurrentJob().getServiceContract().setIntendedMarketUK(b);
    }

    // intended martket - USA
    public Boolean getIntendedMarketUSA() {
        if (getCurrentJob() != null) {
            return getCurrentJob().getServiceContract().getIntendedMarketUSA();
        } else {
            return false;
        }
    }

    public void setIntendedMarketUSA(Boolean b) {
        getCurrentJob().getServiceContract().setIntendedMarketUSA(b);
    }
    // intended martket - Canada

    public Boolean getIntendedMarketCanada() {
        if (getCurrentJob() != null) {
            return getCurrentJob().getServiceContract().getIntendedMarketCanada();
        } else {
            return false;
        }
    }

    public void setIntendedMarketCanada(Boolean b) {
        getCurrentJob().getServiceContract().setIntendedMarketCanada(b);
    }

    // intended martket - Canada
    public Boolean getIntendedMarketOther() {
        if (getCurrentJob() != null) {
            return getCurrentJob().getServiceContract().getIntendedMarketOther();
        } else {
            return false;
        }
    }

    public void setIntendedMarketOther(Boolean b) {
        getCurrentJob().getServiceContract().setIntendedMarketOther(b);
        if (!b) {
            getCurrentJob().getServiceContract().setIntendedMarketOtherText(null);
        }
    }

    /**
     *
     * @return
     */
    public Boolean getCompleted() {
        if (getCurrentJob() != null) {
            return getCurrentJob().getJobStatusAndTracking().getCompleted();
        } else {
            return false;
        }
    }

    public void setCompleted(Boolean b) {
        getCurrentJob().getJobStatusAndTracking().setCompleted(b);
    }

    public Boolean getJobSaved() {
        return getCurrentJob().getId() != null;
    }

    public Boolean getSamplesCollected() {
        if (getCurrentJob() != null) {
            return getCurrentJob().getJobStatusAndTracking().getSamplesCollected();
        } else {
            return false;
        }
    }

    public void setSamplesCollected(Boolean b) {
        getCurrentJob().getJobStatusAndTracking().setSamplesCollected(b);
    }

    // documents collected by
    public Boolean getDocumentCollected() {
        if (getCurrentJob() != null) {
            return getCurrentJob().getJobStatusAndTracking().getDocumentCollected();
        } else {
            return false;
        }
    }

    public void setDocumentCollected(Boolean b) {
        getCurrentJob().getJobStatusAndTracking().setDocumentCollected(b);
    }

    public void updateJob() {
        setIsDirty(true);
    }

    private void addService(String name) {
        Service service = Service.findByName(getEntityManager1(), name);
        if (service != null) {
            // Attempt to remove the service to ensure that it's not already added
            removeService(name);
            
            getCurrentJob().getServices().add(service);
        }
    }

    private void removeService(String name) {
        for (int i = 0; i < getCurrentJob().getServices().size(); i++) {
            if (getCurrentJob().getServices().get(i).getName().equals(name)) {
                getCurrentJob().getServices().remove(i);
            }

        }
    }

    /**
     * Add or remove a service when a service check box is clicked.
     * 
     * @param event 
     */
    public void updateServices(AjaxBehaviorEvent event) {

        switch (event.getComponent().getId()) {
            case "testingService":
                if (getCurrentJob().getServiceContract().getServiceRequestedTesting()) {
                    addService("Testing");
                } else {
                    removeService("Testing");
                }
                break;
            case "calibrationService":
                if (getCurrentJob().getServiceContract().getServiceRequestedCalibration()) {
                    addService("Calibration");
                } else {
                    removeService("Calibration");
                }
                break;
            case "labelEvaluationService":
                if (getCurrentJob().getServiceContract().getServiceRequestedLabelEvaluation()) {
                    addService("Label Evaluation");
                } else {
                    removeService("Label Evaluation");
                }
                break;
            case "inspectionService":
                if (getCurrentJob().getServiceContract().getServiceRequestedInspection()) {
                    addService("Inspection");
                } else {
                    removeService("Inspection");
                }
                break;
            case "consultancyService":
                if (getCurrentJob().getServiceContract().getServiceRequestedConsultancy()) {
                    addService("Consultancy");
                } else {
                    removeService("Consultancy");
                }
                break;
            case "trainingService":
                if (getCurrentJob().getServiceContract().getServiceRequestedTraining()) {
                    addService("Training");
                } else {
                    removeService("Training");
                }
                break;
            case "serviceRequestedFoodInspectorate":
                if (getCurrentJob().getServiceContract().getServiceRequestedFoodInspectorate()) {
                    addService("Food Inspectorate");
                } else {
                    removeService("Food Inspectorate");
                }
                break;    
            case "serviceRequestedLegalMetrology":
                if (getCurrentJob().getServiceContract().getServiceRequestedLegalMetrology()) {
                    addService("Legal Metrology");
                } else {
                    removeService("Legal Metrology");
                }
                break;  
            case "serviceRequestedSaleOfPublication":
                if (getCurrentJob().getServiceContract().getServiceRequestedSaleOfPublication()) {
                    addService("Sale of Publication");
                } else {
                    removeService("Sale of Publication");
                }
                break;  
            case "serviceRequestedStationeryOrPhotocopy":
                if (getCurrentJob().getServiceContract().getServiceRequestedStationeryOrPhotocopy()) {
                    addService("Stationery or Photocopy");
                } else {
                    removeService("Stationery or Photocopy");
                }
                break;    
            case "serviceRequestedCertification":
                if (getCurrentJob().getServiceContract().getServiceRequestedCertification()) {
                    addService("Certification");
                } else {
                    removeService("Certification");
                }
                break;    
            case "serviceRequestedCertificationStandards":
                if (getCurrentJob().getServiceContract().getServiceRequestedCertificationStandards()) {
                    addService("Certification Standards");
                } else {
                    removeService("Certification Standards");
                }
                break;   
            case "serviceRequestedDetentionRehabInspection":
                if (getCurrentJob().getServiceContract().getServiceRequestedDetentionRehabInspection()) {
                    addService("Detention, Rehabilitation & Inspection");
                } else {
                    removeService("Detention, Rehabilitation & Inspection");
                }
                break;    
            case "serviceRequestedFacilitiesManagement":
                if (getCurrentJob().getServiceContract().getServiceRequestedFacilitiesManagement()) {
                    addService("Facilities Management");
                } else {
                    removeService("Facilities Management");
                }
                break;  
            case "serviceRequestedCementTesting":
                if (getCurrentJob().getServiceContract().getServiceRequestedCementTesting()) {
                    addService("Cement Testing");
                } else {
                    removeService("Cement Testing");
                }
                break;    
            case "serviceRequestedPetrolSampling":
                if (getCurrentJob().getServiceContract().getServiceRequestedPetrolSampling()) {
                    addService("Petrol Sampling");
                } else {
                    removeService("Petrol Sampling");
                }
                break;    
            case "otherService":
                if (getCurrentJob().getServiceContract().getServiceRequestedOther()) {
                    addService("Other");
                } else {
                    removeService("Other");
                }
                break;
        }

        setIsDirty(true);
    }

    public void addServices() {
        
        if (getCurrentJob().getServiceContract().getServiceRequestedTesting()) {
            addService("Testing");
        } 

        if (getCurrentJob().getServiceContract().getServiceRequestedCalibration()) {
            addService("Calibration");
        } 

        if (getCurrentJob().getServiceContract().getServiceRequestedLabelEvaluation()) {
            addService("Label Evaluation");
        } 

        if (getCurrentJob().getServiceContract().getServiceRequestedInspection()) {
            addService("Inspection");
        } 

        if (getCurrentJob().getServiceContract().getServiceRequestedConsultancy()) {
            addService("Consultancy");
        } 

        if (getCurrentJob().getServiceContract().getServiceRequestedTraining()) {
            addService("Training");
        } 
        
        if (getCurrentJob().getServiceContract().getServiceRequestedFoodInspectorate()) {
            addService("Food Inspectorate");
        } 
        
        if (getCurrentJob().getServiceContract().getServiceRequestedLegalMetrology()) {
            addService("Legal Metrology");
        }
        
        if (getCurrentJob().getServiceContract().getServiceRequestedSaleOfPublication()) {
            addService("Sale of Publication");
        }
        
        if (getCurrentJob().getServiceContract().getServiceRequestedStationeryOrPhotocopy()) {
            addService("Stationery or Photocopy");
        }
        
        if (getCurrentJob().getServiceContract().getServiceRequestedCertification()) {
            addService("Certification");
        }
        
        if (getCurrentJob().getServiceContract().getServiceRequestedCertificationStandards()) {
            addService("Certification Standards");
        }
        
        if (getCurrentJob().getServiceContract().getServiceRequestedDetentionRehabInspection()) {
            addService("Detention, Rehabilitation & Inspection");
        }
        
        if (getCurrentJob().getServiceContract().getServiceRequestedFacilitiesManagement()) {
            addService("Facilities Management");
        }
        
        if (getCurrentJob().getServiceContract().getServiceRequestedCementTesting()) {
            addService("Cement Testing");
        }
        
        if (getCurrentJob().getServiceContract().getServiceRequestedPetrolSampling()) {
            addService("Petrol Sampling");
        }

        if (getCurrentJob().getServiceContract().getServiceRequestedOther()) {
            addService("Other");
        } 

    }

    @Override
    public void setIsDirty(Boolean dirty) {
        getCurrentJob().setIsDirty(dirty);

        if (dirty) {
            getCurrentJob().getJobStatusAndTracking().setEditStatus("(edited)");
        } else {
            getCurrentJob().getJobStatusAndTracking().setEditStatus("");
        }
    }

    @Override
    public Boolean getIsDirty() {
        return getCurrentJob().getIsDirty();
    }

    /**
     * Determine if the current user is the department's supervisor. This is
     * done by determining if the user is the head/active acting head of the
     * department to which the job was assigned.
     *
     * @param job
     * @return
     */
    // tk del. Move to JobManagerUser and make static method
    public Boolean isUserDepartmentSupervisor(Job job) {
        EntityManager em = getEntityManager1();

        Job foundJob = Job.findJobById(em, job.getId());

        if (Department.findDepartmentAssignedToJob(foundJob, em).getHead().getId().longValue() == getUser().getEmployee().getId().longValue()) {
            return true;
        } else if ((Department.findDepartmentAssignedToJob(foundJob, em).getActingHead().getId().longValue() == getUser().getEmployee().getId().longValue())
                && Department.findDepartmentAssignedToJob(foundJob, em).getActingHeadActive()) {
            return true;
        } else {
            return false;
        }
    }

    public void updateAssignee() {
        setIsDirty(true);
    }

    HSSFCellStyle getDefaultCellStyle(HSSFWorkbook wb) {
        HSSFCellStyle cellStyle = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setFontName("Arial");
        cellStyle.setFont(font);
        cellStyle.setFillForegroundColor(HSSFColor.WHITE.index);
        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        return cellStyle;
    }

    Font getWingdingsFont(HSSFWorkbook wb) {
        Font font = wb.createFont();
        font.setFontHeightInPoints((short) 14);
        font.setFontName("Wingdings");

        return font;
    }

    Font getFont(HSSFWorkbook wb, String fontName, short fontsize) {
        Font font = wb.createFont();
        font.setFontHeightInPoints(fontsize);
        font.setFontName(fontName);

        return font;
    }

    public FileInputStream createServiceContractExcelFileInputStream(
            EntityManager em,
            JobManagerUser user,
            Long jobId,
            String filePath) {
        try {

            Job job = Job.findJobById(em, jobId);
            //job.getJobCostingAndPayment().calculateAmountDue();

            Client client = job.getClient();

            File file = new File(filePath);

            FileInputStream inp = new FileInputStream(file);

            // Create workbook from input file
            POIFSFileSystem fileSystem = new POIFSFileSystem((FileInputStream) inp);

            HSSFWorkbook wb = new HSSFWorkbook(fileSystem);

            // Fonts
            Font defaultFont = getFont(wb, "Arial", (short) 10);
            Font samplesFont = getFont(wb, "Arial", (short) 9);
            Font samplesRefFont = getFont(wb, "Arial", (short) 9);
            samplesRefFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            Font jobNumberFont = getFont(wb, "Arial Black", (short) 14);

            // Cell style
            HSSFCellStyle dataCellStyle;

            // create temp file for output
            FileOutputStream out = new FileOutputStream("ServiceContract-" + user.getId() + ".xls");

            // get service contract sheet
            HSSFSheet serviceContractSheet = wb.getSheet("ServiceContract");
            serviceContractSheet.setActive(true);
            serviceContractSheet.setForceFormulaRecalculation(true);

            // Fill in job data
            // Job number
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderLeft((short) 2);
            dataCellStyle.setFont(jobNumberFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            ReportUtils.setExcelCellValue(
                    wb, serviceContractSheet, "A6", // A = 0 , 6 = 5
                    getCurrentJob().getJobNumber(),
                    "java.lang.String", dataCellStyle);

            // Contracting business office       
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderLeft((short) 1);
            dataCellStyle.setBorderBottom((short) 1);
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            ReportUtils.setExcelCellValue(
                    wb, serviceContractSheet, "K6",
                    getCurrentJob().getBusinessOffice().getName(),
                    "java.lang.String", dataCellStyle);
            ReportUtils.setExcelCellValue(
                    wb, serviceContractSheet, "K7",
                    "",
                    "java.lang.String", dataCellStyle);

            // Job entry agent:
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderBottom((short) 1);
            dataCellStyle.setBorderTop((short) 1);
            dataCellStyle.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            dataCellStyle.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            ReportUtils.setExcelCellValue(
                    wb, serviceContractSheet, "F9",
                    getCurrentJob().getJobStatusAndTracking().getEnteredBy().getName(),
                    "java.lang.String", dataCellStyle);

            // Date agent prepared contract:   
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderBottom((short) 1);
            dataCellStyle.setBorderTop((short) 1);
            dataCellStyle.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            dataCellStyle.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            ReportUtils.setExcelCellValue(
                    wb, serviceContractSheet, "F10",
                    getCurrentJob().getJobStatusAndTracking().getDateAndTimeEntered(),
                    "java.util.Date", dataCellStyle);

            // Department in charge of job (Parent department):   
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderBottom((short) 1);
            dataCellStyle.setBorderTop((short) 1);
            dataCellStyle.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            dataCellStyle.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            ReportUtils.setExcelCellValue(
                    wb, serviceContractSheet, "F11",
                    getCurrentJob().getDepartment().getName(),
                    "java.lang.String", dataCellStyle);

            // Estimated turn around time:
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderBottom((short) 1);
            dataCellStyle.setBorderTop((short) 1);
            dataCellStyle.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            dataCellStyle.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            ReportUtils.setExcelCellValue(
                    wb, serviceContractSheet, "F12",
                    getCurrentJob().getEstimatedTurnAroundTimeInDays(),
                    "java.lang.String", dataCellStyle);

            // Estimated Sub Total (J$):
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderBottom((short) 1);
            dataCellStyle.setBorderTop((short) 1);
            dataCellStyle.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            dataCellStyle.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            ReportUtils.setExcelCellValue(
                    wb, serviceContractSheet, "F13",
                    getCurrentJob().getJobCostingAndPayment().getEstimatedCost(),
                    "Currency", dataCellStyle);

            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderBottom((short) 1);
            dataCellStyle.setBorderTop((short) 1);
            dataCellStyle.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            dataCellStyle.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            ReportUtils.setExcelCellValue(
                    wb, serviceContractSheet, "F14",
                    getCurrentJob().getJobCostingAndPayment().getEstimatedCost()
                    * getCurrentJob().getJobCostingAndPayment().getTax().getValue(),
                    "Currency", dataCellStyle);

            // Estimated Total Cost (J$):
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderBottom((short) 1);
            dataCellStyle.setBorderTop((short) 1);
            dataCellStyle.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            dataCellStyle.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            ReportUtils.setExcelCellValue(
                    wb, serviceContractSheet, "F15",
                    getCurrentJob().getJobCostingAndPayment().getEstimatedCostIncludingTaxes(),
                    "Currency", dataCellStyle);

            // Minimum First Deposit (J$):
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderBottom((short) 1);
            dataCellStyle.setBorderTop((short) 1);
            dataCellStyle.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            ReportUtils.setExcelCellValue(
                    wb, serviceContractSheet, "F16",
                    getCurrentJob().getJobCostingAndPayment().getMinDepositIncludingTaxes(),
                    "Currency", dataCellStyle);

            // RECEIPT #
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderTop((short) 1);
            dataCellStyle.setBorderBottom((short) 1);
            dataCellStyle.setBorderLeft((short) 1);
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            dataCellStyle.setWrapText(true);
            ReportUtils.setExcelCellValue(
                    wb, serviceContractSheet, "R9",
                    getCurrentJob().getJobCostingAndPayment().getReceiptNumbers(),
                    "java.lang.String", dataCellStyle);

            // TOTAL PAID (J$)
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderTop((short) 1);
            dataCellStyle.setBorderBottom((short) 1);
            dataCellStyle.setBorderLeft((short) 1);
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            dataCellStyle.setWrapText(true);
            ReportUtils.setExcelCellValue(
                    wb, serviceContractSheet, "V9",
                    getCurrentJob().getJobCostingAndPayment().getTotalPayment(),
                    "Currency", dataCellStyle);

            // PAYMENT BREAKDOWN
            // Calculate tax from payment
            Double payment = (100.0 * getCurrentJob().getJobCostingAndPayment().getTotalPayment())
                    / (getCurrentJob().getJobCostingAndPayment().getTax().getTaxValue() + 100.0);
            Double tax = getCurrentJob().getJobCostingAndPayment().getTotalPayment() - payment;
            // Tax
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderTop((short) 1);
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            ReportUtils.setExcelCellValue(
                    wb, serviceContractSheet, "AC9",
                    tax,
                    "Currency", dataCellStyle);
            // Payment
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderBottom((short) 1);
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            ReportUtils.setExcelCellValue(
                    wb, serviceContractSheet, "AC10",
                    payment,
                    "Currency", dataCellStyle);

            // DATE PAID (date of last payment)
            if (getCurrentJob().getJobCostingAndPayment().getLastPaymentDate() != null) {
                dataCellStyle = getDefaultCellStyle(wb);
                dataCellStyle.setBorderBottom((short) 1);
                dataCellStyle.setFont(defaultFont);
                dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
                dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
                ReportUtils.setExcelCellValue(
                        wb, serviceContractSheet, "AH9",
                        getCurrentJob().getJobCostingAndPayment().getLastPaymentDate(),
                        "java.util.Date", dataCellStyle);
            }

            // BALANCE (amount due) 
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            if (getCurrentJob().getJobCostingAndPayment().getFinalCost() > 0.0) {
                ReportUtils.setExcelCellValue(
                        wb, serviceContractSheet, "AL9",
                        "exactly",
                        "java.lang.String", dataCellStyle);
                ReportUtils.setExcelCellValue(
                        wb, serviceContractSheet, "AL10",
                        getCurrentJob().getJobCostingAndPayment().getAmountDue(),
                        "Currency", dataCellStyle);
            } else {
                ReportUtils.setExcelCellValue(
                        wb, serviceContractSheet, "AL9",
                        "approximately",
                        "java.lang.String", dataCellStyle);
                ReportUtils.setExcelCellValue(
                        wb, serviceContractSheet, "AL10",
                        BusinessEntityUtils.roundTo2DecimalPlaces(getCurrentJob().getJobCostingAndPayment().getEstimatedCostIncludingTaxes())
                        - BusinessEntityUtils.roundTo2DecimalPlaces(getCurrentJob().getJobCostingAndPayment().getTotalPayment()),
                        "Currency", dataCellStyle);
            }

            // PAYMENT TERMS/INFORMATION
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderTop((short) 1);
            dataCellStyle.setBorderBottom((short) 1);
            dataCellStyle.setBorderLeft((short) 1);
            dataCellStyle.setBorderRight((short) 1);
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);
            if (!getCurrentJob().getJobCostingAndPayment().getAllPaymentTerms().trim().equals("")) {
                ReportUtils.setExcelCellValue(
                        wb, serviceContractSheet, "R12",
                        getCurrentJob().getJobCostingAndPayment().getAllPaymentTerms(),
                        "java.lang.String", dataCellStyle);
            } else {
                ReportUtils.setExcelCellValue(
                        wb, serviceContractSheet, "R12",
                        "Not applicable",
                        "java.lang.String", dataCellStyle);
            }

            // THE INFORMATION IN SECTION 3
            // AGENT/CASHIER
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderTop((short) 1);
            dataCellStyle.setBorderBottom((short) 1);
            dataCellStyle.setBorderLeft((short) 1);
            dataCellStyle.setBorderRight((short) 1);
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            ReportUtils.setExcelCellValue(
                    wb, serviceContractSheet, "AL12",
                    getCurrentJob().getJobCostingAndPayment().getLastPaymentEnteredBy().getName(),
                    "java.lang.String", dataCellStyle);

            // CLIENT NAME & BILLING ADDRESS
            // Name
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderLeft((short) 1);
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            ReportUtils.setExcelCellValue(
                    wb, serviceContractSheet, "A20",
                    client.getName(),
                    "java.lang.String", dataCellStyle);

            // Billing address    
            Address billingAddress = getCurrentJob().getBillingAddress();
            ReportUtils.setExcelCellValue(
                    wb, serviceContractSheet, "A22",
                    billingAddress.getAddressLine1(),
                    "java.lang.String", dataCellStyle);
            ReportUtils.setExcelCellValue(
                    wb, serviceContractSheet, "A23",
                    billingAddress.getAddressLine2(),
                    "java.lang.String", dataCellStyle);
            ReportUtils.setExcelCellValue(
                    wb, serviceContractSheet, "A24",
                    billingAddress.getStateOrProvince(),
                    "java.lang.String", dataCellStyle);
            ReportUtils.setExcelCellValue(
                    wb, serviceContractSheet, "A25",
                    billingAddress.getCity(),
                    "java.lang.String", dataCellStyle);

            // Contact person
            // Name
            Contact contactPerson = getCurrentJob().getContact();
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderLeft((short) 1);
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            ReportUtils.setExcelCellValue(
                    wb, serviceContractSheet, "M20",
                    contactPerson,
                    "java.lang.String", dataCellStyle);

            // Email
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderTop((short) 1);
            dataCellStyle.setBorderLeft((short) 1);
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            ReportUtils.setExcelCellValue(
                    wb, serviceContractSheet, "M22",
                    contactPerson.getInternet().getEmail1(),
                    "java.lang.String", dataCellStyle);

            // Phone
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderTop((short) 1);
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            ReportUtils.setExcelCellValue(
                    wb, serviceContractSheet, "Z20",
                    contactPerson.getMainPhoneNumber(),
                    "java.lang.String", dataCellStyle);

            // Fax
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderBottom((short) 1);
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_BOTTOM);
            ReportUtils.setExcelCellValue(
                    wb, serviceContractSheet, "Z22",
                    contactPerson.getMainFaxNumber(),
                    "java.lang.String", dataCellStyle);

            // TYPE OF SERVICE(S) NEEDED
            // Gather services
            String services = "";
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderLeft((short) 1);
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);
            dataCellStyle.setWrapText(true);
            if (job.getServiceContract().getServiceRequestedTesting()) {
                services = services + "Testing";
            }
            if (job.getServiceContract().getServiceRequestedCalibration()) {
                services = services + "; Calibration";
            }
            if (job.getServiceContract().getServiceRequestedLabelEvaluation()) {
                services = services + "; Label Evaluation";
            }
            if (job.getServiceContract().getServiceRequestedInspection()) {
                services = services + "; Inspection";
            }
            if (job.getServiceContract().getServiceRequestedConsultancy()) {
                services = services + "; Consultancy";
            }
            if (job.getServiceContract().getServiceRequestedTraining()) {
                services = services + "; Training";
            }
            if (job.getServiceContract().getServiceRequestedOther()) {
                if ((job.getServiceContract().getServiceRequestedOtherText() != null)
                        && (!job.getServiceContract().getServiceRequestedOtherText().isEmpty())) {
                    services = services + "; " + job.getServiceContract().getServiceRequestedOtherText();
                }
            }
            ReportUtils.setExcelCellValue(
                    wb, serviceContractSheet, "AD21",
                    services,
                    "java.lang.String", dataCellStyle);

            // Fax/Email report?:
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderRight((short) 1);
            dataCellStyle.setBorderBottom((short) 1);
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_BOTTOM);
            if (job.getServiceContract().getAdditionalServiceFaxResults()) {
                ReportUtils.setExcelCellValue(
                        wb, serviceContractSheet, "AN21",
                        "Yes",
                        "java.lang.String", dataCellStyle);
            } else {
                ReportUtils.setExcelCellValue(
                        wb, serviceContractSheet, "AN21",
                        "No",
                        "java.lang.String", dataCellStyle);
            }

            // Expedite?
            if (job.getServiceContract().getAdditionalServiceUrgent()) {
                ReportUtils.setExcelCellValue(
                        wb, serviceContractSheet, "AN23",
                        "Yes",
                        "java.lang.String", dataCellStyle);
            } else {
                ReportUtils.setExcelCellValue(
                        wb, serviceContractSheet, "AN23",
                        "No",
                        "java.lang.String", dataCellStyle);
            }

            // Purchase Order:        
            if (getCurrentJob().getJobCostingAndPayment().getPurchaseOrderNumber().equals("")) {
                ReportUtils.setExcelCellValue(
                        wb, serviceContractSheet, "AN25",
                        "Not applicable",
                        "java.lang.String", dataCellStyle);
            } else {
                ReportUtils.setExcelCellValue(
                        wb, serviceContractSheet, "AN25",
                        getCurrentJob().getJobCostingAndPayment().getPurchaseOrderNumber(),
                        "java.lang.String", dataCellStyle);
            }

            // CLIENT INSTRUCTION/DETAILS FOR JOB
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderTop((short) 1);
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);
            ReportUtils.setExcelCellValue(
                    wb, serviceContractSheet, "M24",
                    getCurrentJob().getInstructions(),
                    "java.lang.String", dataCellStyle);

            // DESCRIPTION OF SUBMITTED SAMPLE(S)
            int samplesStartngRow = 32;
            if (job.getJobSamples().size() > 0) {
                for (JobSample jobSample : job.getJobSamples()) {
                    dataCellStyle = getDefaultCellStyle(wb);
                    dataCellStyle.setBorderLeft((short) 1);
                    dataCellStyle.setFont(samplesRefFont);
                    dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
                    dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
                    dataCellStyle.setWrapText(true);
                    ReportUtils.setExcelCellValue(
                            wb, serviceContractSheet, "A" + samplesStartngRow,
                            jobSample.getReference(),
                            "java.lang.String", dataCellStyle);
                    dataCellStyle = getDefaultCellStyle(wb);
                    dataCellStyle.setBorderLeft((short) 1);
                    dataCellStyle.setFont(samplesFont);
                    dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
                    dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
                    dataCellStyle.setWrapText(true);
                    ReportUtils.setExcelCellValue(
                            wb, serviceContractSheet, "B" + samplesStartngRow,
                            jobSample.getName(),
                            "java.lang.String", dataCellStyle);
                    ReportUtils.setExcelCellValue(
                            wb, serviceContractSheet, "H" + samplesStartngRow,
                            jobSample.getProductBrand(),
                            "java.lang.String", dataCellStyle);
                    ReportUtils.setExcelCellValue(
                            wb, serviceContractSheet, "O" + samplesStartngRow,
                            jobSample.getProductModel(),
                            "java.lang.String", dataCellStyle);

                    String productSerialAndCode = jobSample.getProductSerialNumber();
                    if (!jobSample.getProductCode().equals("")) {
                        productSerialAndCode = productSerialAndCode + "/" + jobSample.getProductCode();
                    }
                    ReportUtils.setExcelCellValue(
                            wb, serviceContractSheet, "W" + samplesStartngRow,
                            productSerialAndCode,
                            "java.lang.String", dataCellStyle);
                    ReportUtils.setExcelCellValue(
                            wb, serviceContractSheet, "AG" + samplesStartngRow,
                            jobSample.getSampleQuantity(),
                            "java.lang.String", dataCellStyle);
                    ReportUtils.setExcelCellValue(
                            wb, serviceContractSheet, "AI" + samplesStartngRow,
                            jobSample.getQuantity(),
                            "java.lang.String", dataCellStyle);
                    ReportUtils.setExcelCellValue(
                            wb, serviceContractSheet, "AI" + samplesStartngRow,
                            jobSample.getQuantity() + " (" + jobSample.getUnitOfMeasure() + ")",
                            "java.lang.String", dataCellStyle);
                    // Disposal
                    if (jobSample.getMethodOfDisposal() == 2) {
                        ReportUtils.setExcelCellValue(
                                wb, serviceContractSheet, "AO" + samplesStartngRow,
                                "Yes",
                                "java.lang.String", dataCellStyle);
                    } else {
                        ReportUtils.setExcelCellValue(
                                wb, serviceContractSheet, "AO" + samplesStartngRow,
                                "No",
                                "java.lang.String", dataCellStyle);
                    }

                    samplesStartngRow++;
                }
            } else {
                dataCellStyle = getDefaultCellStyle(wb);
                dataCellStyle.setBorderLeft((short) 1);
                dataCellStyle.setFont(samplesRefFont);
                dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
                dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
                dataCellStyle.setWrapText(true);
                ReportUtils.setExcelCellValue(
                        wb, serviceContractSheet, "A" + samplesStartngRow,
                        "",
                        "java.lang.String", dataCellStyle);
                dataCellStyle = getDefaultCellStyle(wb);
                dataCellStyle.setBorderLeft((short) 1);
                dataCellStyle.setFont(samplesFont);
                dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
                dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
                dataCellStyle.setWrapText(true);
                ReportUtils.setExcelCellValue(
                        wb, serviceContractSheet, "B" + samplesStartngRow,
                        "Not applicable",
                        "java.lang.String", dataCellStyle);
                ReportUtils.setExcelCellValue(
                        wb, serviceContractSheet, "H" + samplesStartngRow,
                        "Not applicable",
                        "java.lang.String", dataCellStyle);
                ReportUtils.setExcelCellValue(
                        wb, serviceContractSheet, "O" + samplesStartngRow,
                        "Not applicable",
                        "java.lang.String", dataCellStyle);
                ReportUtils.setExcelCellValue(
                        wb, serviceContractSheet, "W" + samplesStartngRow,
                        "Not applicable",
                        "java.lang.String", dataCellStyle);
                ReportUtils.setExcelCellValue(
                        wb, serviceContractSheet, "AG" + samplesStartngRow,
                        "",
                        "java.lang.String", dataCellStyle);
                ReportUtils.setExcelCellValue(
                        wb, serviceContractSheet, "AI" + samplesStartngRow,
                        "",
                        "java.lang.String", dataCellStyle);
                ReportUtils.setExcelCellValue(
                        wb, serviceContractSheet, "AI" + samplesStartngRow,
                        "",
                        "java.lang.String", dataCellStyle);
                // Disposal                   
                ReportUtils.setExcelCellValue(
                        wb, serviceContractSheet, "AO" + samplesStartngRow,
                        "",
                        "java.lang.String", dataCellStyle);
            }

            // ADDITIONAL DETAILS FOR SAMPLE(S) 
            String details = "";
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderLeft((short) 1);
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            if (job.getJobSamples().isEmpty()) {
                details = "Not applicable";
            } else {
                for (JobSample jobSample : job.getJobSamples()) {
                    if (!jobSample.getDescription().isEmpty()) {
                        details = details + " (" + jobSample.getReference() + ") "
                                + jobSample.getDescription();
                    }
                }
            }
            ReportUtils.setExcelCellValue(
                    wb, serviceContractSheet, "A27",
                    details,
                    "java.lang.String", dataCellStyle);

            // write and save file for later use
            wb.write(out);
            out.close();

            return new FileInputStream("ServiceContract-" + user.getId() + ".xls");
        } catch (Exception e) {
            System.out.println(e);
        }

        return null;
    }

}
