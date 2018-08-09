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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;
import jm.com.dpbennett.business.entity.DatePeriod;
import jm.com.dpbennett.business.entity.Department;
import jm.com.dpbennett.business.entity.Employee;
import jm.com.dpbennett.business.entity.Job;
import jm.com.dpbennett.business.entity.JobManagerUser;
import jm.com.dpbennett.business.entity.JobReportItem;
import jm.com.dpbennett.business.entity.JobSample;
import jm.com.dpbennett.business.entity.JobSubCategory;
import jm.com.dpbennett.business.entity.Report;
import jm.com.dpbennett.business.entity.Sector;
import jm.com.dpbennett.business.entity.SystemOption;
import jm.com.dpbennett.business.entity.utils.BusinessEntityUtils;
import jm.com.dpbennett.business.entity.utils.DatePeriodJobReport;
import jm.com.dpbennett.business.entity.utils.DatePeriodJobReportColumnData;
import jm.com.dpbennett.jmts.Application;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import jm.com.dpbennett.jmts.utils.PrimeFacesUtils;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.primefaces.PrimeFaces;

/**
 *
 * @author Desmond Bennett
 */
@Named
@SessionScoped
public class ReportManager implements Serializable {

    @PersistenceUnit(unitName = "JMTSPU")
    private EntityManagerFactory EMF1;
    private String columnsToExclude;
    private Integer longProcessProgress;
    private String reportSearchText;
    private List<Report> foundReports;
    private DatePeriodJobReport jobSubCategoryReport; // tk may be retired
    private DatePeriodJobReport sectorReport; // tk may be retired
    private DatePeriodJobReport jobQuantitiesAndServicesReport; // tk may be retired   
    private DatePeriod defaultDatePeriod;
    private JobManager jobManager;
    private Report selectedReport;
    private Report currentReport;
    private Boolean isActiveReportsOnly;
    private String reportCategory;
    private DatePeriod selectedDatePeriod;
    private Boolean edit;

    /**
     * Creates a new instance of JobManagerBean
     */
    public ReportManager() {
        init();
    }

    private EntityManager getLocalEntityManager() {
        Map props = new HashMap();
        props.put("javax.persistence.jdbc.driver",
                (String) SystemOption.getOptionValueObject(getEntityManager1(),
                        "defaultDatabaseDriver"));
        props.put("javax.persistence.jdbc.url",
                (String) SystemOption.getOptionValueObject(getEntityManager1(),
                        "defaultDatabaseURL"));
        props.put("javax.persistence.jdbc.user",
                (String) SystemOption.getOptionValueObject(getEntityManager1(),
                        "defaultDatabaseUsername"));
        props.put("javax.persistence.jdbc.password",
                (String) SystemOption.getOptionValueObject(getEntityManager1(),
                        "defaultDatabasePassword"));

        EntityManagerFactory emf = Persistence
                .createEntityManagerFactory("PU", props);

        return emf.createEntityManager();

    }

    public List<SelectItem> getDatePeriods() {
        ArrayList<SelectItem> datePeriods = new ArrayList<>();

        for (String name : DatePeriod.getDatePeriodNames()) {
            datePeriods.add(new SelectItem(name, name));
        }

        return datePeriods;
    }

    public DatePeriod getReportingDatePeriod1() {
        if (selectedReport.getDatePeriods().isEmpty()) {
            selectedReport.getDatePeriods().add(
                    new DatePeriod("This month", "month", null, null, null,
                            null, false, false, true));
        }

        // Ensure that no date period if null
        if (selectedReport.getDatePeriods().get(0).getStartDate() == null) {
            selectedReport.getDatePeriods().get(0).setStartDate(new Date());
        }
        if (selectedReport.getDatePeriods().get(0).getEndDate() == null) {
            selectedReport.getDatePeriods().get(0).setEndDate(new Date());
        }

        return selectedReport.getDatePeriods().get(0);
    }

    // Special method to be removed later when the current method of generating
    // monthly reports is done away with.
    public DatePeriod getMonthlyReportDataDatePeriod() {
        if (getReportingDatePeriod1().getEndDate() == null) {
            getReportingDatePeriod1().setEndDate(new Date());
        }

        return getReportingDatePeriod1();
    }

    public void setReportingDatePeriod1(DatePeriod reportingDatePeriod1) {
        selectedReport.getDatePeriods().set(0, reportingDatePeriod1);
    }

    public DatePeriod getReportingDatePeriod2() {

        if (selectedReport.getDatePeriods().isEmpty()) {
            selectedReport.getDatePeriods().add(
                    new DatePeriod("This month", "month", null, null, null,
                            null, false, false, true));
            selectedReport.getDatePeriods().add(
                    new DatePeriod("This month", "month", null, null, null,
                            null, false, false, true));

            selectedReport.getDatePeriods().get(1).setShow(false);

        } else if (selectedReport.getDatePeriods().size() == 1) {

            selectedReport.getDatePeriods().add(
                    new DatePeriod("This month", "month", null, null, null,
                            null, false, false, true));

            selectedReport.getDatePeriods().get(1).setShow(false);

        }

        // Ensure that no date period if null
        if (selectedReport.getDatePeriods().get(1).getStartDate() == null) {
            selectedReport.getDatePeriods().get(1).setStartDate(new Date());
        }
        if (selectedReport.getDatePeriods().get(1).getEndDate() == null) {
            selectedReport.getDatePeriods().get(1).setEndDate(new Date());
        }

        return selectedReport.getDatePeriods().get(1);
    }

    public void setReportingDatePeriod2(DatePeriod reportingDatePeriod2) {
        selectedReport.getDatePeriods().set(1, reportingDatePeriod2);
    }

    public DatePeriod getReportingDatePeriod3() {

        if (selectedReport.getDatePeriods().isEmpty()) {
            selectedReport.getDatePeriods().add(
                    new DatePeriod("This month", "month", null, null, null,
                            null, false, false, true));
            selectedReport.getDatePeriods().add(
                    new DatePeriod("This month", "month", null, null, null,
                            null, false, false, true));
            selectedReport.getDatePeriods().add(
                    new DatePeriod("This month", "month", null, null, null,
                            null, false, false, true));

            selectedReport.getDatePeriods().get(2).setShow(false);

        } else if (selectedReport.getDatePeriods().size() == 1) {

            selectedReport.getDatePeriods().add(
                    new DatePeriod("This month", "month", null, null, null,
                            null, false, false, true));

            selectedReport.getDatePeriods().add(
                    new DatePeriod("This month", "month", null, null, null,
                            null, false, false, true));

            selectedReport.getDatePeriods().get(2).setShow(false);

        } else if (selectedReport.getDatePeriods().size() == 2) {

            selectedReport.getDatePeriods().add(
                    new DatePeriod("This month", "month", null, null, null,
                            null, false, false, true));

            selectedReport.getDatePeriods().get(2).setShow(false);

        }

        // Ensure that no date period if null
        if (selectedReport.getDatePeriods().get(2).getStartDate() == null) {
            selectedReport.getDatePeriods().get(2).setStartDate(new Date());
        }
        if (selectedReport.getDatePeriods().get(2).getEndDate() == null) {
            selectedReport.getDatePeriods().get(2).setEndDate(new Date());
        }

        return selectedReport.getDatePeriods().get(2);
    }

    public void setReportingDatePeriod3(DatePeriod reportingDatePeriod3) {
        selectedReport.getDatePeriods().set(2, reportingDatePeriod3);
    }

    public Boolean getIsInvalidReport() {
        return (getSelectedReport().getId() == null);
    }

    public Boolean getEdit() {
        return edit;
    }

    public void setEdit(Boolean edit) {
        this.edit = edit;
    }

    public DatePeriod getSelectedDatePeriod() {
        return selectedDatePeriod;
    }

    public void setSelectedDatePeriod(DatePeriod selectedDatePeriod) {
        this.selectedDatePeriod = selectedDatePeriod;
    }

    public void setDatePeriodToDelete(DatePeriod selectedDatePeriod) {
        this.selectedDatePeriod = selectedDatePeriod;

        deleteDatePeriod();
    }

    public void openReportsTab(String category) {
        setReportCategory(category);
        getJobManager().getMainTabView().addTab(getEntityManager1(), "Reports", true);
        getJobManager().getMainTabView().select("Reports");
    }

    public List<Report> completeReport(String query) {
        EntityManager em;

        try {
            em = getEntityManager1();

            List<Report> reports = Report.findActiveReportsByName(em, query);

            return reports;

        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<Report> completeReportByCategory(String query) {
        EntityManager em;

        try {
            em = getEntityManager1();

            List<Report> reports = Report.findActiveReportsByCategoryAndName(em,
                    getReportCategory(), query);

            return reports;

        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public String getReportCategory() {
        return reportCategory;
    }

    public void setReportCategory(String reportCategory) {
        this.reportCategory = reportCategory;
    }

    public JobManager getJobManager() {
        if (jobManager == null) {
            jobManager = Application.findBean("jobManager");
        }
        return jobManager;
    }

    public List getReportCategories() {
        return Report.getCategories();
    }

    public List getReportMimeTypes() {
        return Report.getMimeTypes();
    }

    public Boolean getIsActiveReportsOnly() {
        if (isActiveReportsOnly == null) {
            isActiveReportsOnly = true;
        }
        return isActiveReportsOnly;
    }

    public void setIsActiveReportsOnly(Boolean isActiveReportsOnly) {
        this.isActiveReportsOnly = isActiveReportsOnly;
    }

    public List<Report> getFoundReports() {
        if (foundReports == null) {
            foundReports = Report.findAllActiveReports(getEntityManager1());
        }

        return foundReports;
    }

    public String getReportSearchText() {
        return reportSearchText;
    }

    public void setReportSearchText(String reportSearchText) {
        this.reportSearchText = reportSearchText;
    }

    public void doReportSearch() {

        if (getIsActiveReportsOnly()) {
            foundReports = Report.findActiveReports(getEntityManager1(), getReportSearchText());
        } else {
            foundReports = Report.findReports(getEntityManager1(), getReportSearchText());
        }

    }

    public void editReport() {
        PrimeFacesUtils.openDialog(null, "reportTemplateDialog", true, true, true, 600, 750);
    }

    public Report getSelectedReport() {
        if (selectedReport == null) {
            selectedReport = new Report();
        }

        return selectedReport;
    }

    public void setSelectedReport(Report selectedReport) {
        this.selectedReport = selectedReport;
    }

    public Report getCurrentReport() {
        if (currentReport == null) {
            currentReport = new Report();
        }

        return currentReport;
    }

    public void setCurrentReport(Report currentReport) {
        this.currentReport = currentReport;
    }

    public void saveCurrentReport() {

        currentReport.save(getEntityManager1());

        PrimeFaces.current().dialog().closeDynamic(null);
    }

    public void okSelectedDatePeriod(ActionEvent actionEvent) {
        getSelectedDatePeriod().setIsDirty(true);

        if (getIsNewDatePeriod()) {
            selectedReport.getDatePeriods().add(selectedDatePeriod);
        }

        closeDialog(actionEvent);
    }

    public void cancelSelectedDatePeriod(ActionEvent actionEvent) {
        getSelectedDatePeriod().setIsDirty(false);

        closeDialog(actionEvent);
    }

    public void closeDialog(ActionEvent actionEvent) {
        PrimeFaces.current().dialog().closeDynamic(null);
    }

    public void createNewReport() {

        selectedReport = new Report();

        editReport();
    }

    public void createNewDatePeriod() {

        selectedDatePeriod = new DatePeriod("This month", "month",
                "", null, null, null, false, false, false);
        selectedDatePeriod.initDatePeriod();

        setEdit(false);

        PrimeFacesUtils.openDialog(null, "reportDatePeriodDialog", true, true, true, 0, 400);
    }

    public Boolean getIsNewDatePeriod() {
        return getSelectedDatePeriod().getId() == null && !getEdit();
    }

    public void editDatePeriod() {

        setEdit(true);

        PrimeFacesUtils.openDialog(null, "reportDatePeriodDialog", true, true, true, 0, 400);

    }

    public void deleteDatePeriod() {
        EntityManager em = getEntityManager1();

        if (selectedDatePeriod.getId() != null) {
            DatePeriod datePeriod = DatePeriod.findById(em, selectedDatePeriod.getId());
            if (datePeriod != null) {
                currentReport.getDatePeriods().remove(selectedDatePeriod);
                currentReport.save(em);
                em.getTransaction().begin();
                em.remove(datePeriod);
                em.getTransaction().commit();
            }
        } else {
            currentReport.getDatePeriods().remove(selectedDatePeriod);
        }

    }

    private void init() {
        this.reportSearchText = "";
        this.longProcessProgress = 0;
        this.columnsToExclude = "";
        this.reportCategory = "Job";
        defaultDatePeriod = new DatePeriod("This month", "month", null, null, null, null, false, false, true);
    }

    public void reset() {
        init();
    }

    public void closeReportsTab() {
        getJobManager().getMainTabView().addTab(getEntityManager1(), "Reports", false);
    }

    public JobManagerUser getUser() {
        return getJobManager().getUser();
    }

    public EntityManager getEntityManager1() {
        return EMF1.createEntityManager();
    }

    public Employee getReportEmployee1() {
        if (selectedReport.getEmployees().isEmpty()) {
            selectedReport.getEmployees().add(getUser().getEmployee());
        }
        return selectedReport.getEmployees().get(0);
    }

    public void setReportEmployee1(Employee reportEmployee1) {
        selectedReport.getEmployees().set(0, reportEmployee1);
    }

    public Department getReportingDepartment1() {
        if (selectedReport.getDepartments().isEmpty()) {
            selectedReport.getDepartments().add(getUser().getEmployee().getDepartment());
        }
        return selectedReport.getDepartments().get(0);
    }

    public void setReportingDepartment1(Department reportingDepartment1) {
        selectedReport.getDepartments().set(0, reportingDepartment1);
    }

    public JasperPrint getJasperPrint(Connection con,
            HashMap parameters) {
        JasperPrint jasperPrint = null;
        FileInputStream fis;

        switch (selectedReport.getReportOutputFileMimeType()) {
            case "application/jasper":
                //                    print = JasperFillManager.fillReport(
//                            selectedReport.getReportFileTemplate(),
//                            parameters,
//                            con);
                
                break;

            case "application/jrxml":
                try {
                    fis = new FileInputStream(getClass().getClassLoader().
                            getResource("/reports/" + selectedReport.getReportFileTemplate()).getFile());

//                        print = JasperFillManager.fillReport(
//                                fis,
//                                parameters,
//                                con);
                    JasperReport jasperReport = JasperCompileManager
                            .compileReport(fis);

                    jasperPrint = JasperFillManager.fillReport(
                            jasperReport,
                            parameters,
                            con);
                } catch (FileNotFoundException | JRException e) {
                    System.out.println(e);
                }
                break;

            default:
                break;
        }

        return jasperPrint;
    }

    public StreamedContent getReportStreamedContent() {

        EntityManager em = getEntityManager1();
        HashMap parameters = new HashMap();
        Connection con;

        try {

            con = BusinessEntityUtils.establishConnection(
                    (String) SystemOption.getOptionValueObject(em, "defaultDatabaseDriver"),
                    (String) SystemOption.getOptionValueObject(em, "defaultDatabaseURL"),
                    (String) SystemOption.getOptionValueObject(em, "defaultDatabaseUsername"),
                    (String) SystemOption.getOptionValueObject(em, "defaultDatabasePassword"));

            if (con != null) {
                StreamedContent streamContent;
                byte[] fileBytes;
                JasperPrint print = null;

                // Provide date parameters if required
                if (selectedReport.getDatePeriodRequired()) {
                    for (int i = 0; i < selectedReport.getDatePeriods().size(); i++) {
                        parameters.put("startOfPeriod" + (i + 1),
                                selectedReport.getDatePeriods().get(i).initDatePeriod().getStartDate());
                        parameters.put("endOfPeriod" + (i + 1),
                                selectedReport.getDatePeriods().get(i).initDatePeriod().getEndDate());
                    }
                }
                // Provide employee parameters if required
                if (selectedReport.getEmployeeRequired()) {
                    for (int i = 0; i < selectedReport.getEmployees().size(); i++) {
                        parameters.put("employeeId" + (i + 1),
                                selectedReport.getEmployees().get(i).getId());
                    }
                }
                // Provide department parameters if required
                if (selectedReport.getDepartmentRequired()) {
                    for (int i = 0; i < selectedReport.getDepartments().size(); i++) {
                        parameters.put("departmentId" + (i + 1),
                                selectedReport.getDepartments().get(i).getId());
                        parameters.put("departmentName" + (i + 1),
                                selectedReport.getDepartments().get(i).getName());
                    }
                }

                // Generate report
                if (getSelectedReport().getUsePackagedReportFileTemplate()) {
                    try {
                        FileInputStream fis = new FileInputStream(getClass().getClassLoader().
                                getResource("/reports/" + selectedReport.getReportFileTemplate()).getFile());

                        JasperReport jasperReport = JasperCompileManager
                                .compileReport(fis);

                        print = JasperFillManager.fillReport(
                                jasperReport,
                                parameters,
                                con);

                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(ReportManager.class.getName()).log(Level.SEVERE, null, ex);
                    }

                } else {
                    JasperReport jasperReport = JasperCompileManager
                            .compileReport(selectedReport.getReportFileTemplate());


                    print = JasperFillManager.fillReport(
                            jasperReport,
                            parameters,
                            con);
                }

                switch (selectedReport.getReportOutputFileMimeType()) {
                    case "application/pdf":

                        fileBytes = JasperExportManager.exportReportToPdf(print);

                        streamContent = new DefaultStreamedContent(new ByteArrayInputStream(fileBytes),
                                selectedReport.getReportOutputFileMimeType(),
                                selectedReport.getReportFile());

                        break;

                    case "application/xlsx":
                    case "application/xls":

                        JRXlsExporter exporterXLS = new JRXlsExporter();
                        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                        exporterXLS.setParameter(JRXlsExporterParameter.JASPER_PRINT, print);
                        exporterXLS.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, outStream);
                        exporterXLS.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.FALSE);
                        exporterXLS.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
                        exporterXLS.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
                        exporterXLS.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
                        exporterXLS.exportReport();

                        streamContent = new DefaultStreamedContent(new ByteArrayInputStream(outStream.toByteArray()),
                                selectedReport.getReportOutputFileMimeType(),
                                selectedReport.getReportFile());

                        break;

                    default:
                        fileBytes = JasperExportManager.exportReportToPdf(print);

                        streamContent = new DefaultStreamedContent(new ByteArrayInputStream(fileBytes),
                                selectedReport.getReportOutputFileMimeType(),
                                selectedReport.getReportFile());
                        break;

                }

                setLongProcessProgress(100);

                return streamContent;

            } else {
                return null;
            }

        } catch (JRException e) {
            System.out.println(e);
            setLongProcessProgress(100);

            return null;
        }

    }

    public StreamedContent getReportFile() {

        StreamedContent reportFile = null;

        try {

            switch (getSelectedReport().getReportFileMimeType()) {
                case "application/jasper":
                case "application/jrxml":
                    reportFile = getReportStreamedContent();
                    break;
                case "application/xlsx":
                    if (getSelectedReport().getName().equals("Analytical Services Report")) {
                        reportFile = getAnalyticalServicesReport(getLocalEntityManager());
                    }
                    break;
                case "application/xls":
                    if (getSelectedReport().getName().equals("Monthly report")) {
                        reportFile = getMonthlyReport(getLocalEntityManager());
                    }
                    break;
                default:
                    break;
            }

            setLongProcessProgress(100);

        } catch (Exception e) {
            System.out.println(e);
            setLongProcessProgress(100);
        }

        return reportFile;
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

    public StreamedContent getMonthlyReport(EntityManager em) {

        try {
            // Get byte stream for report file
            ByteArrayInputStream stream = createExcelMonthlyReportFileInputStream2(
                    em, new File(getSelectedReport().getReportFileTemplate()),
                    getReportingDepartment1().getId());

            return new DefaultStreamedContent(stream, getSelectedReport().getReportFileMimeType(), getSelectedReport().getReportFile());

        } catch (Exception ex) {
            System.out.println(ex);
        }

        return null;
    }

    public StreamedContent getAnalyticalServicesReport(EntityManager em) {

        try {
            ByteArrayInputStream stream;

            if (getSelectedReport().getUsePackagedReportFileTemplate()) {
                stream = analyticalServicesReportFileInputStream(em, new File(getClass().getClassLoader().
                        getResource("/reports/" + getSelectedReport().getReportFileTemplate()).getFile()),
                        getReportingDepartment1().getId());
            } else {
                stream = analyticalServicesReportFileInputStream(em, new File(getSelectedReport().getReportFileTemplate()),
                        getReportingDepartment1().getId());
            }

            return new DefaultStreamedContent(stream, getSelectedReport().getReportFileMimeType(), getSelectedReport().getReportFile());

        } catch (Exception ex) {
            System.out.println(ex);
        }

        return null;
    }

    // tk check if can be del.
    public void updateServiceContract() {
    }

    public List<Report> getJobReports() {
        EntityManager em = getEntityManager1();

        List<Report> reports = Report.findAllReports(em);

        return reports;
    }

    public String getColumnsToExclude() {
        return columnsToExclude;
    }

    public void setColumnsToExclude(String columnsToExclude) {
        this.columnsToExclude = columnsToExclude;
    }

    public void updateDepartmentReport() {
    }

    public void updateReportCategory() {
        setSelectedReport(new Report(""));
    }

    public void updateReport() {

    }

    public List<DatePeriodJobReportColumnData> jobSubCategogyGroupReportByDatePeriod(
            EntityManager em,
            String dateSearchField,
            String searchText,
            Date startDate,
            Date endDate) {

        List<DatePeriodJobReportColumnData> data = null;

        String searchQuery
                = "SELECT NEW jm.com.dpbennett.utils.DatePeriodJobReportColumnData"
                + "("
                + "job.jobSubCategory,"
                + "SUM(jobCostingAndPayment.finalCost),"
                + "SUM(job.noOfTestsOrCalibrations)"
                + ")"
                + " FROM Job job"
                + " JOIN job.jobStatusAndTracking jobStatusAndTracking"
                + " JOIN job.jobSubCategory jobSubCategory"
                + " JOIN job.department department"
                + " JOIN job.subContractedDepartment subContractedDepartment"
                + " JOIN job.jobCostingAndPayment jobCostingAndPayment"
                + " WHERE ((jobStatusAndTracking." + dateSearchField + " >= " + BusinessEntityUtils.getDateString(startDate, "'", "YMD", "-")
                + " AND jobStatusAndTracking." + dateSearchField + " <= " + BusinessEntityUtils.getDateString(endDate, "'", "YMD", "-") + "))"
                + " AND ( UPPER(department.name) = '" + searchText.toUpperCase() + "'"
                + " OR UPPER(subContractedDepartment.name) = '" + searchText.toUpperCase() + "')"
                + " AND UPPER(jobStatusAndTracking.workProgress) <> 'CANCELLED'"
                + " AND UPPER(jobStatusAndTracking.workProgress) <> 'WITHDRAWN BY CLIENT'"
                + " GROUP BY job.jobSubCategory"
                + " ORDER BY job.jobSubCategory.subCategory ASC";
        // now do search
        try {
            data = em.createQuery(searchQuery, DatePeriodJobReportColumnData.class).getResultList();
            if (data == null) {
                data = new ArrayList<>();
            }
            // sort by earnings and non-earnings
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }

        return data;
    }

    public List<DatePeriodJobReportColumnData> sectorReportByDatePeriod(
            EntityManager em,
            String dateSearchField,
            String searchText, // filled in with department's name
            Date startDate,
            Date endDate) {

        List<DatePeriodJobReportColumnData> data;
        String searchQuery;

        searchQuery
                = "SELECT NEW jm.com.dpbennett.utils.DatePeriodJobReportColumnData"
                + "("
                + "job.sector,"
                + "SUM(job.noOfTestsOrCalibrations)"
                + ")"
                + " FROM Job job"
                + " JOIN job.jobStatusAndTracking jobStatusAndTracking"
                + " JOIN job.sector sector"
                + " JOIN job.department department"
                + " JOIN job.subContractedDepartment subContractedDepartment"
                + " WHERE ((jobStatusAndTracking." + dateSearchField + " >= " + BusinessEntityUtils.getDateString(startDate, "'", "YMD", "-")
                + " AND jobStatusAndTracking." + dateSearchField + " <= " + BusinessEntityUtils.getDateString(endDate, "'", "YMD", "-") + "))"
                + " AND ( UPPER(department.name) = '" + searchText.toUpperCase() + "'"
                + " OR UPPER(subContractedDepartment.name) = '" + searchText.toUpperCase() + "')"
                + " AND UPPER(jobStatusAndTracking.workProgress) <> 'CANCELLED'"
                + " AND UPPER(jobStatusAndTracking.workProgress) <> 'WITHDRAWN BY CLIENT'"
                + " GROUP BY job.sector"
                + " ORDER BY job.sector.name ASC";
        // now do search

        try {
            data = em.createQuery(searchQuery, DatePeriodJobReportColumnData.class).getResultList();
            if (data
                    == null) {
                data = new ArrayList<>();
            }
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }

        return data;
    }

    public List<DatePeriodJobReportColumnData> jobReportByDatePeriod(
            EntityManager em,
            String searchText, // filled in with department's name
            Date startDate,
            Date endDate) {

        List<DatePeriodJobReportColumnData> data;

        String searchQuery
                = "SELECT NEW jm.com.dpbennett.utils.DatePeriodJobReportColumnData"
                + "("
                + "job"
                + ")"
                + " FROM Job job"
                + " JOIN job.jobStatusAndTracking jobStatusAndTracking"
                + " JOIN job.department department"
                + " JOIN job.subContractedDepartment subContractedDepartment"
                + " WHERE ((jobStatusAndTracking.dateOfCompletion >= " + BusinessEntityUtils.getDateString(startDate, "'", "YMD", "-")
                + " AND jobStatusAndTracking.dateOfCompletion <= " + BusinessEntityUtils.getDateString(endDate, "'", "YMD", "-") + ")"
                + " OR"
                + " (jobStatusAndTracking.dateSubmitted >= " + BusinessEntityUtils.getDateString(startDate, "'", "YMD", "-")
                + " AND jobStatusAndTracking.dateSubmitted <= " + BusinessEntityUtils.getDateString(endDate, "'", "YMD", "-") + ")"
                + " OR"
                + " (jobStatusAndTracking.expectedDateOfCompletion >= " + BusinessEntityUtils.getDateString(startDate, "'", "YMD", "-")
                + " AND jobStatusAndTracking.expectedDateOfCompletion <= " + BusinessEntityUtils.getDateString(endDate, "'", "YMD", "-") + "))"
                + " AND ( UPPER(department.name) = '" + searchText.toUpperCase() + "'"
                + " OR UPPER(subContractedDepartment.name) = '" + searchText.toUpperCase() + "')"
                + " AND UPPER(jobStatusAndTracking.workProgress) <> 'CANCELLED'"
                + " AND UPPER(jobStatusAndTracking.workProgress) <> 'WITHDRAWN BY CLIENT'"
                + " ORDER BY job.sector.name ASC";
        // now do search

        try {
            data = em.createQuery(searchQuery, DatePeriodJobReportColumnData.class).getResultList();
            if (data
                    == null) {
                data = new ArrayList<>();
            }
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }

        return data;
    }

    public FileInputStream createExcelJobReportFileInputStream(
            URL FileUrl,
            JobManagerUser user,
            Department reportingDepartment,
            DatePeriodJobReport jobSubCategoryReport,
            DatePeriodJobReport sectorReport,
            DatePeriodJobReport jobQuantitiesAndServicesReport) throws URISyntaxException {

        try {
            File file = new File(FileUrl.toURI());
            FileInputStream inp = new FileInputStream(file);
            int row = 0;

            // create workbook from input file
            POIFSFileSystem fileSystem = new POIFSFileSystem((FileInputStream) inp);
            HSSFWorkbook wb = new HSSFWorkbook(fileSystem);
            HSSFCellStyle dataCellStyle = wb.createCellStyle();
            HSSFCellStyle headerCellStyle = wb.createCellStyle();
            headerCellStyle.setFont(BusinessEntityUtils.createBoldFont(wb, (short) 14, HSSFColor.BLUE.index));
            HSSFCellStyle columnHeaderCellStyle = wb.createCellStyle();
            columnHeaderCellStyle.setFont(BusinessEntityUtils.createBoldFont(wb, (short) 12, HSSFColor.BLUE.index));

            // create temp file for output
            FileOutputStream out = new FileOutputStream("MonthlyReport" + user.getId() + ".xls");

            HSSFSheet jobSheet = wb.getSheet("Statistics");
            if (jobSheet == null) {
                jobSheet = wb.createSheet("Statistics");
            }

            BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row++, 0,
                    "Job Statistics",
                    "java.lang.String", headerCellStyle);

            BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row++, 0,
                    reportingDepartment.getName(),
                    "java.lang.String", headerCellStyle);

            BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row++, 0,
                    BusinessEntityUtils.getDateInMediumDateFormat(new Date()),
                    "java.lang.String", headerCellStyle);

            BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row++, 0,
                    jobSubCategoryReport.getDatePeriod(0).getPeriodString(),
                    "java.lang.String", headerCellStyle);

            row++;
            // subcategory report
            if (jobSubCategoryReport != null) {
                BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row++, 0,
                        "EARNINGS",
                        "java.lang.String", headerCellStyle);

                row++;
                for (int i = 0; i < jobSubCategoryReport.getDatePeriods().length; i++) {
                    List<DatePeriodJobReportColumnData> reportColumnData = jobSubCategoryReport.getReportColumnData(jobSubCategoryReport.getDatePeriod(i).getName());
                    // insert table headings
                    BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row++, 0,
                            jobSubCategoryReport.getDatePeriod(i).toString(), "java.lang.String", columnHeaderCellStyle);
                    BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row, 0,
                            "Job subcategory", "java.lang.String", columnHeaderCellStyle);
                    BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row, 1,
                            "Earnings", "java.lang.String", columnHeaderCellStyle);
                    BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row++, 2,
                            "Calibrations/Tests", "java.lang.String", columnHeaderCellStyle);

                    // SUMMARY - earnings
                    for (DatePeriodJobReportColumnData data : reportColumnData) {
                        // subcategory
                        BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row, 0,
                                data.getJobSubCategory().getName(), "java.lang.String", dataCellStyle);
                        // cost
                        BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row, 1,
                                data.getJobCostingAndPayment().getFinalCost(), "java.lang.Double", dataCellStyle);
                        // test/cals
                        BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row++, 2,
                                data.getNoOfTestsOrCalibrations(), "java.lang.Integer", dataCellStyle);
                    }
                    ++row;
                }
            }

            if (sectorReport != null) {
                BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row++, 0,
                        "SECTORS SERVED",
                        "java.lang.String", headerCellStyle);

                row++;
                for (int i = 0; i < sectorReport.getDatePeriods().length; i++) {
                    List<DatePeriodJobReportColumnData> reportColumnData = sectorReport.getReportColumnData(sectorReport.getDatePeriod(i).getName());
                    // sector table headings
                    BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row++, 0,
                            sectorReport.getDatePeriod(i).toString(), "java.lang.String", columnHeaderCellStyle);
                    BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row, 0,
                            "Sector", "java.lang.String", columnHeaderCellStyle);

                    BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row++, 2,
                            "Calibrations/Tests", "java.lang.String", columnHeaderCellStyle);

                    // SECTOR - tests/cals
                    for (DatePeriodJobReportColumnData data : reportColumnData) {
                        // sector
                        BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row, 0,
                                data.getSector().getName(), "java.lang.String", dataCellStyle);
                        // test/cals
                        BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row++, 2,
                                data.getNoOfTestsOrCalibrations(), "java.lang.Integer", dataCellStyle);
                    }
                    ++row;
                }
            }

            if (jobQuantitiesAndServicesReport != null) {
                BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row++, 0,
                        "JOB QUANTITIES AND SERVICES",
                        "java.lang.String", headerCellStyle);

                row++;
                for (int i = 0; i < jobQuantitiesAndServicesReport.getDatePeriods().length; i++) {
                    List<DatePeriodJobReportColumnData> reportColumnData = jobQuantitiesAndServicesReport.getReportColumnData(jobQuantitiesAndServicesReport.getDatePeriod(i).getName());

                    BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row++, 0,
                            sectorReport.getDatePeriod(i).toString(), "java.lang.String", headerCellStyle);
                    // JobQuantities And Services table headings
                    List<JobReportItem> jobReportItems = jobQuantitiesAndServicesReport.getJobReportItems();
                    for (JobReportItem jobReportItem : jobReportItems) {
                        BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row, 0,
                                jobReportItem.getName(),
                                "java.lang.String", dataCellStyle);

                        BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row++, 1,
                                (Double) jobQuantitiesAndServicesReport.getReportItemValue(jobReportItem, jobQuantitiesAndServicesReport.getDatePeriod(i), reportColumnData),
                                "java.lang.Double", dataCellStyle);
                    }
                    row++;
                }
            }

            // write and save file for later use
            wb.write(out);
            out.close();

            return new FileInputStream("MonthlyReport" + user.getId() + ".xls");

        } catch (IOException ex) {
            System.out.println(ex);
        }

        return null;
    }

    public ByteArrayInputStream analyticalServicesReportFileInputStream(
            EntityManager em,
            File reportFile,
            Long departmentId) {

        try {
            FileInputStream inp = new FileInputStream(reportFile);
            int row = 1;
            int col = 0;
            int cell = 0;

            XSSFWorkbook wb = new XSSFWorkbook(inp);
            XSSFCellStyle stringCellStyle = wb.createCellStyle();
            XSSFCellStyle longCellStyle = wb.createCellStyle();
            XSSFCellStyle integerCellStyle = wb.createCellStyle();
            XSSFCellStyle doubleCellStyle = wb.createCellStyle();
            XSSFCellStyle dateCellStyle = wb.createCellStyle();
            CreationHelper createHelper = wb.getCreationHelper();
            dateCellStyle.setDataFormat(
                    createHelper.createDataFormat().getFormat("m/d/yyyy"));

            // Output stream for modified Excel file
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            // Get sheets          
            XSSFSheet rawData = wb.getSheet("Raw Data");
            XSSFSheet jobReportSheet = wb.getSheet("Jobs Report");
            XSSFSheet employeeReportSheet = wb.getSheet("Employee Report");
            XSSFSheet sectorReportSheet = wb.getSheet("Sector Report");

            // Get report data
            List<Object[]> reportData = Job.getJobRecordsByTrackingDate(
                    em,
                    getReportingDatePeriod1().getDateField(),
                    BusinessEntityUtils.getDateString(getReportingDatePeriod1().getStartDate(), "'", "YMD", "-"),
                    BusinessEntityUtils.getDateString(getReportingDatePeriod1().getEndDate(), "'", "YMD", "-"),
                    departmentId);

            // Fill in report data            
            for (Object[] rowData : reportData) {
                col = 0;
                //  Employee/Assignee
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, col++,
                        (String) rowData[7],
                        "java.lang.String", stringCellStyle);
                // No. samples
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, col++,
                        (Long) rowData[9],
                        "java.lang.Long", longCellStyle);
                // No. tests/calibrations
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, col++,
                        (Integer) rowData[10],
                        "java.lang.Integer", integerCellStyle);
                // No. tests
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, col++,
                        (Integer) rowData[11],
                        "java.lang.Integer", integerCellStyle);
                // No. calibrations
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, col++,
                        (Integer) rowData[12],
                        "java.lang.Integer", integerCellStyle);
                // Total cost
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, col++,
                        (Double) rowData[8],
                        "java.lang.Double", doubleCellStyle);
                //  Completion date
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, col++,
                        (Date) rowData[6],
                        "java.util.Date", dateCellStyle);
                //  Expected completion date
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, col++,
                        (Date) rowData[13],
                        "java.util.Date", dateCellStyle);
                // Job numbers
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, col++,
                        (String) rowData[14],
                        "java.lang.String", stringCellStyle);
                // Sample description
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, col++,
                        (String) rowData[0],
                        "java.lang.String", stringCellStyle);
                // Client/Source
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, col++,
                        (String) rowData[15],
                        "java.lang.String", stringCellStyle);
                //  Date submitted
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, col++,
                        (Date) rowData[16],
                        "java.util.Date", dateCellStyle);
                // Sector
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, col++,
                        (String) rowData[17],
                        "java.lang.String", stringCellStyle);
                // Turnaround time status
                if ((rowData[6] != null) && (rowData[13] != null)) {
                    BusinessEntityUtils.setExcelCellValue(wb, rawData, row, col++,
                            ((Date) rowData[6]).getTime() > ((Date) rowData[13]).getTime()
                            ? "late" : "on-time",
                            "java.lang.String", stringCellStyle);
                } else {
                    BusinessEntityUtils.setExcelCellValue(wb, rawData, row, col++,
                            "",
                            "java.lang.String", stringCellStyle);
                }
                // Classification
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, col++,
                        (String) rowData[18],
                        "java.lang.String", stringCellStyle);
                // Category
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, col++,
                        (String) rowData[19],
                        "java.lang.String", stringCellStyle);
                // Subcategory
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, col++,
                        (String) rowData[20],
                        "java.lang.String", stringCellStyle);

                row++;

            }

            // Set department name and report period
            // Dept. name
            BusinessEntityUtils.setExcelCellValue(wb, jobReportSheet, 0, 0,
                    getSelectedReport().getDepartments().get(0).getName(),
                    "java.lang.String", null);
            BusinessEntityUtils.setExcelCellValue(wb, employeeReportSheet, 0, 0,
                    getSelectedReport().getDepartments().get(0).getName(),
                    "java.lang.String", null);
            BusinessEntityUtils.setExcelCellValue(wb, sectorReportSheet, 0, 0,
                    getSelectedReport().getDepartments().get(0).getName(),
                    "java.lang.String", null);
            // Period
            BusinessEntityUtils.setExcelCellValue(wb, jobReportSheet, 2, 0,
                    BusinessEntityUtils.getDateInMediumDateFormat(getReportingDatePeriod1().getStartDate())
                    + " - "
                    + BusinessEntityUtils.getDateInMediumDateFormat(getReportingDatePeriod1().getEndDate()),
                    "java.lang.String", null);
            BusinessEntityUtils.setExcelCellValue(wb, employeeReportSheet, 2, 0,
                    BusinessEntityUtils.getDateInMediumDateFormat(getReportingDatePeriod1().getStartDate())
                    + " - "
                    + BusinessEntityUtils.getDateInMediumDateFormat(getReportingDatePeriod1().getEndDate()),
                    "java.lang.String", null);
            BusinessEntityUtils.setExcelCellValue(wb, sectorReportSheet, 2, 0,
                    BusinessEntityUtils.getDateInMediumDateFormat(getReportingDatePeriod1().getStartDate())
                    + " - "
                    + BusinessEntityUtils.getDateInMediumDateFormat(getReportingDatePeriod1().getEndDate()),
                    "java.lang.String", null);

            // Write modified Excel file and return it
            wb.write(out);

            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException ex) {
            System.out.println(ex);
        }

        return null;
    }

    public ByteArrayInputStream createExcelMonthlyReportFileInputStream2(
            EntityManager em,
            File reportFile,
            Long departmentId) {

        try {

            // create workbook from input file
            POIFSFileSystem fileSystem = new POIFSFileSystem(new FileInputStream(reportFile));
            HSSFWorkbook wb = new HSSFWorkbook(fileSystem);

            // ensure that crucial sheets are updated automatically
            int row = 2;

            HSSFCellStyle stringCellStyle = wb.createCellStyle();
            HSSFCellStyle longCellStyle = wb.createCellStyle();
            HSSFCellStyle integerCellStyle = wb.createCellStyle();
            HSSFCellStyle doubleCellStyle = wb.createCellStyle();
            HSSFCellStyle dateCellStyle = wb.createCellStyle();

            // Output stream for modified Excel file
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            // Get sheets
            HSSFSheet graphsAndCharts = wb.getSheet("Graphs & Charts");
            graphsAndCharts.setForceFormulaRecalculation(true);

            HSSFSheet valuations = wb.getSheet("Valuations");
            valuations.setForceFormulaRecalculation(true);

            HSSFSheet talliesAndRatesA = wb.getSheet("Tallies & Rates (A)");
            talliesAndRatesA.setForceFormulaRecalculation(true);

            HSSFSheet talliesAndRatesB = wb.getSheet("Tallies & Rates (B)");
            talliesAndRatesB.setForceFormulaRecalculation(true);

            HSSFSheet rawData = wb.getSheet("Raw Data");

            // Get report data
            // Set date to now first
            getReportingDatePeriod1().setEndDate(new Date());
            List<Object[]> reportData = Job.getJobReportRecords(
                    em,
                    BusinessEntityUtils.getDateString(getReportingDatePeriod3().getStartDate(), "'", "YMD", "-"),
                    BusinessEntityUtils.getDateString(getReportingDatePeriod3().getEndDate(), "'", "YMD", "-"),
                    departmentId);

            // Fill in report data   
            for (Object[] rowData : reportData) {
                // Job numbers
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 0,
                        (String) rowData[6],
                        "java.lang.String", stringCellStyle);
                // Client
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 1,
                        (String) rowData[8],
                        "java.lang.String", stringCellStyle);
                // Business office
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 2,
                        (String) rowData[11],
                        "java.lang.String", stringCellStyle);
                // Work progress
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 3,
                        (String) rowData[12],
                        "java.lang.String", stringCellStyle);
                // Classification
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 7,
                        (String) rowData[13],
                        "java.lang.String", stringCellStyle);
                // Category
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 8,
                        (String) rowData[14],
                        "java.lang.String", stringCellStyle);
                // Section (Subcategory
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 9,
                        (String) rowData[15],
                        "java.lang.String", stringCellStyle);
                // Section (Subcategory
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 10,
                        (String) rowData[16],
                        "java.lang.String", stringCellStyle);
                // Assigned department
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 14,
                        (String) rowData[9],
                        "java.lang.String", stringCellStyle);
                // Assigned department
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 15,
                        (String) rowData[10],
                        "java.lang.String", stringCellStyle);
                // No. samples
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 16,
                        (Long) rowData[5],
                        "java.lang.Long", longCellStyle);
                // No. test
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 18,
                        (Integer) rowData[4],
                        "java.lang.Integer", integerCellStyle);
                // Total deposit
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 31,
                        (Double) rowData[26],
                        "java.lang.Double", doubleCellStyle);
                // Amount due
                if ((rowData[27] != null) && (rowData[26] != null)) {
                    BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 32,
                            (Double) rowData[27] - (Double) rowData[26],
                            "java.lang.Double", doubleCellStyle);
                }
                // Estimated cost
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 33,
                        (Double) rowData[28],
                        "java.lang.Double", doubleCellStyle);
                // Final cost
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 34,
                        (Double) rowData[27],
                        "java.lang.Double", doubleCellStyle);
                //  Job entry date
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 36,
                        (Date) rowData[20],
                        "java.util.Date", dateCellStyle);
                //  Submission date 
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 37,
                        (Date) rowData[29],
                        "java.util.Date", dateCellStyle);
                //  Expected date completion
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 38,
                        (Date) rowData[17],
                        "java.util.Date", dateCellStyle);
                //  Completion date
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 39,
                        (Date) rowData[19],
                        "java.util.Date", dateCellStyle);
                //  Assignee
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 41,
                        (String) rowData[21] + " " + (String) rowData[22],
                        "java.lang.String", stringCellStyle);
                //  Entered by firstname
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 42,
                        (String) rowData[24],
                        "java.lang.String", stringCellStyle);
                //  Entered by lastname
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 43,
                        (String) rowData[25],
                        "java.lang.String", stringCellStyle);
                //  List of samples
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 44,
                        (String) rowData[0],
                        "java.lang.String", stringCellStyle);
                //  List of sample brands
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 45,
                        (String) rowData[1],
                        "java.lang.String", stringCellStyle);
                //  List of sample brands
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 46,
                        (String) rowData[2],
                        "java.lang.String", stringCellStyle);
                //  List of sample brands
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, 47,
                        (String) rowData[30],
                        "java.lang.String", stringCellStyle);
                row++;

            }

            // Insert data at top of sheet
            //  Department name
            Department department = Department.findDepartmentById(em, departmentId);
            BusinessEntityUtils.setExcelCellValue(wb, rawData, 0, 1,
                    department.getName(),
                    "java.lang.String", stringCellStyle);
            //  Data starts at:
            BusinessEntityUtils.setExcelCellValue(wb, rawData, 0, 4,
                    getMonthlyReportDataDatePeriod().getStartDate(),
                    "java.util.Date", dateCellStyle);
            //  Data ends at:
            BusinessEntityUtils.setExcelCellValue(wb, rawData, 0, 6,
                    getMonthlyReportDataDatePeriod().getEndDate(),
                    "java.util.Date", dateCellStyle);
            //  Month starts at:
            BusinessEntityUtils.setExcelCellValue(wb, rawData, 0, 8,
                    getReportingDatePeriod1().getStartDate(),
                    "java.util.Date", dateCellStyle);
            //  Month ends at:
            BusinessEntityUtils.setExcelCellValue(wb, rawData, 0, 10,
                    getReportingDatePeriod1().getEndDate(),
                    "java.util.Date", dateCellStyle);
            // Year type
            BusinessEntityUtils.setExcelCellValue(wb, rawData, 0, 12,
                    getReportingDatePeriod2().getName(),
                    "java.lang.String", stringCellStyle);
            //  Year starts at:
            BusinessEntityUtils.setExcelCellValue(wb, rawData, 0, 15,
                    getReportingDatePeriod2().getStartDate(),
                    "java.util.Date", dateCellStyle);
            //  Year ends at:
            BusinessEntityUtils.setExcelCellValue(wb, rawData, 0, 17,
                    getReportingDatePeriod2().getEndDate(),
                    "java.util.Date", dateCellStyle);

            wb.write(out);

            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException ex) {
            System.out.println(ex);
        }

        return null;
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

    public Boolean checkForSampleDisposalMethod(List<JobSample> samples, Integer method) {
        for (JobSample jobSample : samples) {
            if (jobSample.getMethodOfDisposal().compareTo(method) == 0) {
                return true;
            }
        }
        return false;
    }

    public void associateDepartmentWithJobSubCategory(
            EntityManager em,
            Department department,
            JobSubCategory jobSubCategory) {

        em.getTransaction().begin();
        jobSubCategory.getDepartments().add(department);
        BusinessEntityUtils.saveBusinessEntity(em, jobSubCategory);
        em.getTransaction().commit();
    }

    public void associateDepartmentWithSector(
            EntityManager em,
            Department department,
            Sector sector) {

        em.getTransaction().begin();
        sector.getDepartments().add(department);
        BusinessEntityUtils.saveBusinessEntity(em, sector);
        em.getTransaction().commit();
    }

    public void associateDepartmentWithJobReportItem(
            EntityManager em,
            Department department,
            JobReportItem jobReportItem) {

        em.getTransaction().begin();
        jobReportItem.getDepartments().add(department);
        BusinessEntityUtils.saveBusinessEntity(em, jobReportItem);
        em.getTransaction().commit();
    }

    public ArrayList getDateSearchFields() {
        return DatePeriod.getDateSearchFields();
    }
}
