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
import java.io.InputStream;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import jm.com.dpbennett.business.entity.DatePeriod;
import jm.com.dpbennett.business.entity.Department;
import jm.com.dpbennett.business.entity.DepartmentReport;
import jm.com.dpbennett.business.entity.Employee;
import jm.com.dpbennett.business.entity.Job;
import jm.com.dpbennett.business.entity.JobManagerUser;
import jm.com.dpbennett.business.entity.JobReportItem;
import jm.com.dpbennett.business.entity.JobSample;
import jm.com.dpbennett.business.entity.JobSubCategory;
import jm.com.dpbennett.business.entity.Preference;
import jm.com.dpbennett.business.entity.Report;
import jm.com.dpbennett.business.entity.ReportTableColumn;
import jm.com.dpbennett.business.entity.Sector;
import jm.com.dpbennett.business.entity.SystemOption;
import jm.com.dpbennett.business.entity.utils.BusinessEntityUtils;
import jm.com.dpbennett.business.entity.utils.DatePeriodJobReport;
import jm.com.dpbennett.business.entity.utils.DatePeriodJobReportColumnData;
import jm.com.dpbennett.business.entity.utils.SearchParameters;
import jm.com.dpbennett.jmts.Application;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import jm.com.dpbennett.jmts.utils.PrimeFacesUtils;
import net.sf.jasperreports.engine.JasperRunManager;
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
    private StreamedContent reportFile;
    private Integer longProcessProgress;
    private String reportSearchText;
    private List<Job> currentPeriodJobReportSearchResultList; // tk may be retired
    private List<Report> foundReports;
    private DatePeriod previousDatePeriod; // tk may be retired
    private DatePeriodJobReport jobSubCategoryReport; // tk may be retired
    private DatePeriodJobReport sectorReport; // tk may be retired
    private DatePeriodJobReport jobQuantitiesAndServicesReport; // tk may be retired
    //private SearchParameters reportSearchParameters; // tk may be retired  
    private DatePeriod monthlyReportDatePeriod; // tk may be retired
    private DatePeriod monthlyReportDataDatePeriod; // tk may be retired
    private DatePeriod monthlyReportYearDatePeriod; // tk may be retired
    private JobManager jobManager;
    private Report selectedReport;
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

    public DatePeriod getReportingDatePeriod1() {
        if (selectedReport.getDatePeriods().isEmpty()) {
            selectedReport.getDatePeriods().add(
                    new DatePeriod("This month", "month", null, null, null,
                            null, false, false, true));
        }
        return selectedReport.getDatePeriods().get(0);
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
        } else if (selectedReport.getDatePeriods().size() == 1) {

            selectedReport.getDatePeriods().add(
                    new DatePeriod("This month", "month", null, null, null,
                            null, false, false, true));

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
        } else if (selectedReport.getDatePeriods().size() == 1) {

            selectedReport.getDatePeriods().add(
                    new DatePeriod("This month", "month", null, null, null,
                            null, false, false, true));

            selectedReport.getDatePeriods().add(
                    new DatePeriod("This month", "month", null, null, null,
                            null, false, false, true));

        } else if (selectedReport.getDatePeriods().size() == 2) {

            selectedReport.getDatePeriods().add(
                    new DatePeriod("This month", "month", null, null, null,
                            null, false, false, true));


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

    public void saveSelectedReport() {

        selectedReport.save(getEntityManager1());

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
                selectedReport.getDatePeriods().remove(selectedDatePeriod);
                selectedReport.save(em);
                em.getTransaction().begin();
                em.remove(datePeriod);
                em.getTransaction().commit();
            }
        } else {
            selectedReport.getDatePeriods().remove(selectedDatePeriod);
        }

    }

    private void init() {
        this.reportSearchText = "";
        this.longProcessProgress = 0;
        this.columnsToExclude = "";
        this.reportCategory = "Job";
        //report = new Report();
        // reporting vars init
        ArrayList searchTypes = new ArrayList();
        ArrayList searchDateFields = new ArrayList();
        // Add search types
        searchTypes.add(new SelectItem("General", "General"));
        // Add search fields
        searchDateFields.add(new SelectItem("dateSubmitted", "Date submitted"));
        searchDateFields.add(new SelectItem("dateOfCompletion", "Date completed"));
        searchDateFields.add(new SelectItem("dateAndTimeEntered", "Date entered"));
        previousDatePeriod = new DatePeriod("Last month", "month", null, null, null, null, false, false, true);
//        reportSearchParameters
//                = new SearchParameters(
//                        "Report Data Search",
//                        null,
//                        false,
//                        searchTypes,
//                        true,
//                        "dateSubmitted",
//                        true,
//                        searchDateFields,
//                        "General",
//                        new DatePeriod("This month", "month", null, null, null, null, false, false, true),
//                        "");

        // Monthly report date periods
        monthlyReportDatePeriod = new DatePeriod("Last month", "month", null, null, null, null, false, false, true);
        monthlyReportDataDatePeriod = new DatePeriod(
                "Custom",
                "any",
                null,
                null,
                BusinessEntityUtils.createDate(2012, 3, 1), // tk make option
                new Date(), false, false, true);
        monthlyReportYearDatePeriod = new DatePeriod(
                "This financial year to date",
                "year",
                null,
                null,
                null,
                null, false, false, true);
    }

    public void reset() {
        init();
    }

    // tk - test generating report for display in web browser
    public void generateReport(ActionEvent actionEvent)
            throws ClassNotFoundException, SQLException, IOException,
            JRException {
        Connection connection;
        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
        InputStream reportStream = facesContext.getExternalContext()
                .getResourceAsStream("/reports/DbReport.jasper");
        ServletOutputStream servletOutputStream = response
                .getOutputStream();
        Class.forName("com.mysql.jdbc.Driver");
        connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/jmts?"
                + "user=user&password=secret");

        facesContext.responseComplete();
        response.setContentType("application/pdf");

        JasperRunManager.runReportToPdfStream(reportStream,
                servletOutputStream, new HashMap(), connection);
        connection.close();
        servletOutputStream.flush();
        servletOutputStream.close();
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

    public DatePeriod getMonthlyReportDatePeriod() {
        return monthlyReportDatePeriod;
    }

    public void setMonthlyReportDatePeriod(DatePeriod monthlyReportDatePeriod) {
        this.monthlyReportDatePeriod = monthlyReportDatePeriod;
    }

    public DatePeriod getMonthlyReportDataDatePeriod() {
        return monthlyReportDataDatePeriod;
    }

    public void setMonthlyReportDataDatePeriod(DatePeriod monthlyReportDataDatePeriod) {
        this.monthlyReportDataDatePeriod = monthlyReportDataDatePeriod;
    }

    public DatePeriod getMonthlyReportYearDatePeriod() {
        return monthlyReportYearDatePeriod;
    }

    public void setMonthlyReportYearDatePeriod(DatePeriod monthlyReportYearDatePeriod) {
        this.monthlyReportYearDatePeriod = monthlyReportYearDatePeriod;
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

//    public SearchParameters getReportSearchParameters() {
//        return reportSearchParameters;
//    }

    public int getNumberOfCurrentPeriodJobsFound() {
        if (currentPeriodJobReportSearchResultList != null) {
            return currentPeriodJobReportSearchResultList.size();
        }

        return 0;
    }

    public StreamedContent getReportStreamedContent() {

        EntityManager em = getEntityManager1();
        HashMap parameters = new HashMap();

        try {

            Connection con = BusinessEntityUtils.establishConnection(
                    (String) SystemOption.getOptionValueObject(em, "defaultDatabaseDriver"),
                    (String) SystemOption.getOptionValueObject(em, "defaultDatabaseURL"),
                    (String) SystemOption.getOptionValueObject(em, "defaultDatabaseUsername"),
                    (String) SystemOption.getOptionValueObject(em, "defaultDatabasePassword"));

            if (con != null) {
                StreamedContent streamContent = null;
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
                    }
                }

                // Generate report
                if (getSelectedReport().getUsePackagedReportFileTemplate()) {
                    try {
                        FileInputStream fis = new FileInputStream(getClass().getClassLoader().
                                getResource("/reports/" + getSelectedReport().getReportFileTemplate()).getFile());
                        print = JasperFillManager.fillReport(
                                fis,
                                parameters,
                                con);
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(ReportManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    print = JasperFillManager.fillReport(
                            selectedReport.getReportFileTemplate(),
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

                        break;
                    case "application/xls":
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

        EntityManager em = getEntityManager1();

        try {

            switch (getSelectedReport().getReportFileMimeType()) {
                case "application/jasper":
                    if (getSelectedReport().getName().equals("Jobs entered by employee")) {
                        reportFile = getReportStreamedContent();
                    }
                    if (getSelectedReport().getName().equals("Jobs entered by department")) {
                        reportFile = getJobEnteredByDepartmentReportPDFFile(); // tk replace with getReportStreamedContent()
                    }
                    if (getSelectedReport().getName().equals("Jobs assigned to department")) {
                        reportFile = getJobAssignedToDepartmentReportXLSFile(); // tk replace with getReportStreamedContent()
                    }
                    break;
                case "application/xlsx":
                    if (getSelectedReport().getName().equals("Analytical Services Report")) {
                        reportFile = getAnalyticalServicesReport(em);
                    }
                    break;
                case "application/xls":
                    if (getSelectedReport().getName().equals("Monthly report")) {
                        reportFile = getMonthlyReport3(em);
                    } else {
                        reportFile = getExcelReportStreamContent();
                    }
                    break;
                default:
                    break;
            }

            setLongProcessProgress(100);

        } catch (URISyntaxException e) {
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
            DatePeriod datePeriods[] = BusinessEntityUtils.getMonthlyReportDatePeriods(reportSearchParameters.getDatePeriod());

            List<JobSubCategory> subCategories = JobSubCategory.findAllJobSubCategoriesGroupedByEarningsByDepartment(em, getReportingDepartment1());
            List<Sector> sectors = Sector.findAllSectorsByDeparment(em, getReportingDepartment1());
            List<JobReportItem> jobReportItems = JobReportItem.findAllJobReportItemsByDeparment(em, getReportingDepartment1());

            // reports
            jobSubCategoryReport = new DatePeriodJobReport(getReportingDepartment1(), subCategories, null, null, datePeriods);
            sectorReport = new DatePeriodJobReport(getReportingDepartment1(), null, sectors, null, datePeriods);
            jobQuantitiesAndServicesReport = new DatePeriodJobReport(getReportingDepartment1(), null, null, jobReportItems, datePeriods);

            // populate SubCategoryReport/Sector/job report
            for (int i = 0; i < datePeriods.length; i++) {
                // job subcat report
                List<DatePeriodJobReportColumnData> data = jobSubCategogyGroupReportByDatePeriod(em,
                        "dateOfCompletion",
                        jobSubCategoryReport.getReportingDepartment().getName(),
                        datePeriods[i].getStartDate(),
                        datePeriods[i].getEndDate());
                jobSubCategoryReport.updateSubCategoriesReportColumnData(datePeriods[i].getName(), data);
            }

            for (int i = 0; i < datePeriods.length; i++) {
                // sector report
                List<DatePeriodJobReportColumnData> data = sectorReportByDatePeriod(em,
                        "dateOfCompletion",
                        sectorReport.getReportingDepartment().getName(),
                        datePeriods[i].getStartDate(),
                        datePeriods[i].getEndDate());
                sectorReport.updateSectorsReportColumnData(datePeriods[i].getName(), data);
            }

            for (int i = 0; i < datePeriods.length; i++) {
                // job report
                List<DatePeriodJobReportColumnData> data = jobReportByDatePeriod(em,
                        jobQuantitiesAndServicesReport.getReportingDepartment().getName(),
                        datePeriods[i].getStartDate(),
                        datePeriods[i].getEndDate());
                jobQuantitiesAndServicesReport.setReportColumnData(datePeriods[i].getName(), data);
            }

            // generate report
            FileInputStream stream = createExcelJobReportFileInputStream(this.getClass().getResource("MonthlyReport.xls"),
                    getUser(), getReportingDepartment1(), jobSubCategoryReport, sectorReport, jobQuantitiesAndServicesReport);

            return new DefaultStreamedContent(stream, getSelectedReport().getReportFileMimeType(), getSelectedReport().getReportFile());

        } catch (Exception ex) {
            System.out.println(ex);
        }

        return null;
    }

    public StreamedContent getMonthlyReport2(EntityManager em) {

        try {
            // Get byte stream for report file
            ByteArrayInputStream stream = createExcelMonthlyReportFileInputStream(new File(getSelectedReport().getReportFileTemplate()),
                    getReportingDepartment1().getId());

            return new DefaultStreamedContent(stream, getSelectedReport().getReportFileMimeType(), getSelectedReport().getReportFile());

        } catch (Exception ex) {
            System.out.println(ex);
        }

        return null;
    }

    public StreamedContent getMonthlyReport3(EntityManager em) {

        try {
            // Get byte stream for report file
            ByteArrayInputStream stream = createExcelMonthlyReportFileInputStream2(new File(getSelectedReport().getReportFileTemplate()),
                    getReportingDepartment1().getId());

            return new DefaultStreamedContent(stream, getSelectedReport().getReportFileMimeType(), getSelectedReport().getReportFile());

        } catch (Exception ex) {
            System.out.println(ex);
        }

        return null;
    }

    public StreamedContent getCompletedByDepartmentReport(EntityManager em) {

        try {
            // Get byte stream for report file
            ByteArrayInputStream stream = jobsCompletedByDepartmentFileInputStream(new File(getSelectedReport().getReportFileTemplate()),
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
                stream = analyticalServicesReportFileInputStream(new File(getClass().getClassLoader().
                        getResource("/reports/" + getSelectedReport().getReportFileTemplate()).getFile()),
                        getReportingDepartment1().getId());
            } else {
                stream = analyticalServicesReportFileInputStream(new File(getSelectedReport().getReportFileTemplate()),
                        getReportingDepartment1().getId());
            }

            return new DefaultStreamedContent(stream, getSelectedReport().getReportFileMimeType(), getSelectedReport().getReportFile());

        } catch (Exception ex) {
            System.out.println(ex);
        }

        return null;
    }

    public StreamedContent getExcelReportStreamContent() throws URISyntaxException {

        try {

            URL url = this.getClass().getResource(getSelectedReport().getReportFileTemplate());
            File file = new File(url.toURI());
            FileInputStream inp = new FileInputStream(file);
            short startingRow;

            // create workbook from input file
            POIFSFileSystem fileSystem = new POIFSFileSystem((FileInputStream) inp);
            HSSFWorkbook wb = new HSSFWorkbook(fileSystem);

            // ensure that crucial sheets are updated automatically
            HSSFSheet reportSheet = wb.getSheet("Report");
            reportSheet.setActive(true);

            // create temp file for output
            FileOutputStream out = new FileOutputStream(getSelectedReport().getReportFile() + getUser().getId());

            HSSFSheet jobSheet = wb.getSheet("Statistics");
            if (jobSheet == null) {
                jobSheet = wb.createSheet("Statistics");
            }

            wb.setSheetOrder("Statistics", 0);
            wb.setActiveSheet(0);

            // set starting for inserting headers and data
            startingRow = (short) (jobSheet.getLastRowNum());

            // Create header font
            HSSFFont headerFont = wb.createFont();
            headerFont.setFontHeightInPoints((short) 14);

            headerFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

            if (!getSelectedReport().getReportColumns().isEmpty()) {
                HSSFRow headerRow = jobSheet.createRow(startingRow++);
                // Setup header style
                HSSFCellStyle headerCellStyle = wb.createCellStyle();
                headerCellStyle.setFont(headerFont);
                headerCellStyle.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
                headerCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
                headerCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
                for (int i = 0; i < getSelectedReport().getReportColumns().size(); i++) {
                    headerRow.createCell(i);
                    // Set header style
                    headerRow.getCell(i).setCellStyle(headerCellStyle);
                    // Set as first sheet
                }
                // merge header cells
                jobSheet.addMergedRegion(new CellRangeAddress(
                        jobSheet.getLastRowNum(), //first row
                        jobSheet.getLastRowNum(), //last row
                        0, //first column
                        getSelectedReport().getReportColumns().size() - 1 //last column
                ));
                // Set header title
                headerRow.getCell(0).setCellValue(new HSSFRichTextString(getSelectedReport().getName()));

                // Setup column headers
                // Create column header font
                HSSFFont columnHeaderFont = wb.createFont();
                columnHeaderFont.setFontHeightInPoints((short) 12);
                columnHeaderFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
                HSSFRow columnHeaderRow = jobSheet.createRow(startingRow++);
                // Setup header style
                HSSFCellStyle headerColumnCellStyle = wb.createCellStyle();
                headerColumnCellStyle.setFont(columnHeaderFont);
                headerColumnCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
                for (int i = 0; i < getSelectedReport().getReportColumns().size(); i++) {
                    columnHeaderRow.createCell(i).setCellValue(new HSSFRichTextString(getSelectedReport().getReportColumns().get(i).getName().trim()));
                    columnHeaderRow.getCell(i).setCellStyle(headerColumnCellStyle);
                }

                // Insert jobs data
                // Data font
                HSSFFont dataFont = wb.createFont();
                dataFont.setFontHeightInPoints((short) 12);
                short row = startingRow;
                HSSFCellStyle dataCellStyle = wb.createCellStyle();
                dataCellStyle.setFont(dataFont);
                headerColumnCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
                for (Job job : currentPeriodJobReportSearchResultList) {
                    HSSFRow dataRow = jobSheet.createRow(row);
                    for (int i = 0; i < getSelectedReport().getReportColumns().size(); i++) {

                        try {
                            ReportTableColumn col = getSelectedReport().getReportColumns().get(i);
                            BusinessEntityUtils.setExcelCellValue(wb, dataRow.createCell(i), job, col.getEntityClassMethodName());
                        } catch (Exception ex) {
                            System.out.println(ex);
                        }

                        jobSheet.autoSizeColumn((short) i);
                    }
                    ++row;
                }
            }

            // write and save file for later use
            wb.write(out);
            out.close();

            FileInputStream stream = new FileInputStream(getSelectedReport().getReportFile() + getUser().getId());
            return new DefaultStreamedContent(stream, getSelectedReport().getReportFileMimeType(), getSelectedReport().getReportFile());

        } catch (IOException ex) {
            System.out.println(ex);
        }

        return null;
    }

    public void refreshJobReport() {
        //updateReport();        
    }

    public void updateServiceContract() {

    }

    public List<Report> getJobReports() {
        EntityManager em = getEntityManager1();

        List<Report> reports = Report.findAllReports(em);

        return reports;
    }

    public List<Preference> getJobTableViewPreferences() {
        EntityManager em = getEntityManager1();

        List<Preference> prefs = Preference.findAllPreferencesByName(em, "jobTableView");

        return prefs;
    }

    public String getColumnsToExclude() {
        return columnsToExclude;
    }

    public void setColumnsToExclude(String columnsToExclude) {
        this.columnsToExclude = columnsToExclude;
    }

    public void postProcessXLS(Object document) {
        DateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");

        HSSFWorkbook wb = (HSSFWorkbook) document;
        HSSFSheet sheet = wb.getSheetAt(0);
        // Create a new font and alter it.
        HSSFFont headerFont = wb.createFont();
        headerFont.setFontHeightInPoints((short) 12);
        headerFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

        HSSFFont dataFont = wb.createFont();

        sheet.shiftRows(0, currentPeriodJobReportSearchResultList.size(), 1);
        // set columns widths
        for (short i = 0; i < 8; i++) {
            if (i != (short) 3) { // services column
                sheet.autoSizeColumn(i);
            } else {
                sheet.setColumnWidth(i, 15000);
            }
        }
        // set cell borders
        if (currentPeriodJobReportSearchResultList.size() > 0) {
            for (int k = 0; k < sheet.getPhysicalNumberOfRows(); k++) {
                HSSFRow firstInfoRow = sheet.getRow(k);
                for (int i = 0; i < firstInfoRow.getPhysicalNumberOfCells(); i++) {
                    HSSFCell cell = firstInfoRow.getCell(i);
                    // Style the cell with borders all around.
                    if (cell != null) {
                        // Create a new font and alter it.
                        HSSFCellStyle style = wb.createCellStyle();

                        if (k == 1) { // data header
                            dataFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
                        } else {
                            dataFont.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
                        }
                        style.setFont(dataFont);
                        style.setWrapText(true);
                        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
                        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
                        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
                        style.setBorderTop(HSSFCellStyle.BORDER_THIN);

                        // Change cell type/style from string to date
                        if ((i == 6) || (i == 7)) {
                            if (k != 1) {
                                try {
                                    String dateStr = cell.getRichStringCellValue().getString();

                                    if (!dateStr.equals("")) {
                                        Date date = (Date) formatter.parse(dateStr);
                                        cell.setCellValue(date);
                                        cell.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
                                        style.setDataFormat(wb.createDataFormat().getFormat("MMM dd, yyyy"));
                                    }
                                    cell.setCellStyle(style);
                                } catch (ParseException ex) {
                                    System.out.println(ex);
                                }
                            } else {
                                cell.setCellStyle(style);
                            }
                        } else {
                            cell.setCellStyle(style);
                        }
                    }
                }

                // insert sheet header row
                HSSFRow header = sheet.getRow(0);

                header.createCell(0).setCellValue(new HSSFRichTextString(getSelectedReport().getName()));
                // merge header cells
                sheet.addMergedRegion(new CellRangeAddress(
                        0, //first row
                        0, //last row
                        0, //first column
                        7 //last column
                ));
                // style the header
                HSSFCellStyle cellStyle = wb.createCellStyle();
                cellStyle.setFont(headerFont);
                cellStyle.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
                cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
                cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
                for (int i = 0; i < header.getPhysicalNumberOfCells(); i++) {
                    HSSFCell cell = header.getCell(i);
                    cell.setCellStyle(cellStyle);
                }
            }
        }

    }

    public void updateDepartmentReport() {
    }

    private EntityManagerFactory getEMF1() {
        return EMF1;
    }

    public List<Job> getCurrentPeriodJobReportSearchResultList() {
        if (currentPeriodJobReportSearchResultList == null) {
            currentPeriodJobReportSearchResultList = new ArrayList<>();
        }
        return currentPeriodJobReportSearchResultList;
    }

    public void updateReportCategory() {
        setSelectedReport(new Report(""));
    }

    public Long saveDepartmentReport(EntityManager em, DepartmentReport departmentReport) {

        try {
            if (departmentReport.getId() != null) {
                em.merge(departmentReport);
            } else {
                em.persist(departmentReport);
            }
        } catch (Exception e) {
            return null;
        }

        return departmentReport.getId();
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

    public ByteArrayInputStream createExcelMonthlyReportFileInputStream(
            File reportFile,
            Long departmentId) {

        try {
            FileInputStream inp = new FileInputStream(reportFile);
            int row = 2;

            XSSFWorkbook wb = new XSSFWorkbook(inp);

            XSSFCellStyle stringCellStyle = wb.createCellStyle();
            XSSFCellStyle longCellStyle = wb.createCellStyle();
            XSSFCellStyle integerCellStyle = wb.createCellStyle();
            XSSFCellStyle doubleCellStyle = wb.createCellStyle();
            XSSFCellStyle dateCellStyle = wb.createCellStyle();

            // Output stream for modified Excel file
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            // Get sheets
            XSSFSheet graphsAndCharts = wb.getSheet("Graphs & Charts");
            XSSFSheet valuations = wb.getSheet("Valuations");
            XSSFSheet talliesAndRatesA = wb.getSheet("Tallies & Rates (A)");
            XSSFSheet talliesAndRatesB = wb.getSheet("Tallies & Rates (B)");
            XSSFSheet rawData = wb.getSheet("Raw Data");

            // Get report data
            List<Object[]> reportData = Job.getJobReportRecords(
                    getEntityManager1(),
                    BusinessEntityUtils.getDateString(monthlyReportDataDatePeriod.getStartDate(), "'", "YMD", "-"),
                    BusinessEntityUtils.getDateString(monthlyReportDataDatePeriod.getEndDate(), "'", "YMD", "-"),
                    departmentId);

            // Fill in report data            
            System.out.println("rowdata: " + reportData.size());
            for (Object[] rowData : reportData) {
                System.out.println("Inserting row: " + row);
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
            Department department = Department.findDepartmentById(getEntityManager1(), departmentId);
            BusinessEntityUtils.setExcelCellValue(wb, rawData, 0, 1,
                    department.getName(),
                    "java.lang.String", stringCellStyle);
            //  Data starts at:
            BusinessEntityUtils.setExcelCellValue(wb, rawData, 0, 4,
                    monthlyReportDataDatePeriod.getStartDate(),
                    "java.util.Date", dateCellStyle);
            //  Data ends at:
            BusinessEntityUtils.setExcelCellValue(wb, rawData, 0, 6,
                    monthlyReportDataDatePeriod.getEndDate(),
                    "java.util.Date", dateCellStyle);
            //  Month starts at:
            BusinessEntityUtils.setExcelCellValue(wb, rawData, 0, 8,
                    monthlyReportDatePeriod.getStartDate(),
                    "java.util.Date", dateCellStyle);
            //  Month ends at:
            BusinessEntityUtils.setExcelCellValue(wb, rawData, 0, 10,
                    monthlyReportDatePeriod.getEndDate(),
                    "java.util.Date", dateCellStyle);
            // Year type
            BusinessEntityUtils.setExcelCellValue(wb, rawData, 0, 12,
                    monthlyReportYearDatePeriod.getName(),
                    "java.lang.String", stringCellStyle);
            //  Year starts at:
            BusinessEntityUtils.setExcelCellValue(wb, rawData, 0, 15,
                    monthlyReportYearDatePeriod.getStartDate(),
                    "java.util.Date", dateCellStyle);
            //  Year ends at:
            BusinessEntityUtils.setExcelCellValue(wb, rawData, 0, 17,
                    monthlyReportYearDatePeriod.getEndDate(),
                    "java.util.Date", dateCellStyle);

            // Update calculations in relevant sheets
            FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();

            for (Row r : talliesAndRatesA) {
                for (Cell c : r) {
                    if (c.getCellType() == Cell.CELL_TYPE_FORMULA) {
                        evaluator.evaluateFormulaCell(c);
                    }
                }
            }

            for (Row r : valuations) {
                for (Cell c : r) {
                    if (c.getCellType() == Cell.CELL_TYPE_FORMULA) {
                        evaluator.evaluateFormulaCell(c);
                    }
                }
            }

            // Write modified Excel file and return it
            wb.write(out);

            return new ByteArrayInputStream(out.toByteArray());

        } catch (Exception ex) {
            System.out.println(ex);
        }

        return null;
    }

    public ByteArrayInputStream jobsCompletedByDepartmentFileInputStream(
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

            // Output stream for modified Excel file
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            // Get sheets          
            XSSFSheet rawData = wb.getSheet("Raw Data");

            // Get report data            
            List<Object[]> reportData = Job.getJobRecordsByTrackingDate(
                    getEntityManager1(),
                    getSelectedDatePeriod().getDateField(),
                    BusinessEntityUtils.getDateString(reportSearchParameters.getDatePeriod().getStartDate(), "'", "YMD", "-"),
                    BusinessEntityUtils.getDateString(reportSearchParameters.getDatePeriod().getEndDate(), "'", "YMD", "-"),
                    departmentId);

            // Fill in report data            
            for (Object[] rowData : reportData) {
                col = 0;
                //  Assignee
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, col++,
                        (String) rowData[19],
                        "java.lang.String", stringCellStyle);
                // No. samples
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, col++,
                        (Long) rowData[25],
                        "java.lang.Long", longCellStyle);
                // No. tests/calibrations
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, col++,
                        (Integer) rowData[26],
                        "java.lang.Integer", integerCellStyle);
                // No. tests
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, col++,
                        (Integer) rowData[27],
                        "java.lang.Integer", integerCellStyle);
                // No. calibrations
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, col++,
                        (Integer) rowData[28],
                        "java.lang.Integer", integerCellStyle);
                // Total cost
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, col++,
                        (Double) rowData[21],
                        "java.lang.Double", doubleCellStyle);
                //  Completion date
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, col++,
                        (Date) rowData[15],
                        "java.util.Date", dateCellStyle);
                //  Expected completion date
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, col++,
                        (Date) rowData[29],
                        "java.util.Date", dateCellStyle);
                // Job numbers
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, col++,
                        (String) rowData[30],
                        "java.lang.String", stringCellStyle);

                row++;

            }

            wb.write(out);

            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException ex) {
            System.out.println(ex);
        }

        return null;
    }

    public ByteArrayInputStream analyticalServicesReportFileInputStream(
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
                    getEntityManager1(),
                    getSelectedDatePeriod().getDateField(),
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
                // subcategory
                BusinessEntityUtils.setExcelCellValue(wb, rawData, row, col++,
                        (String) rowData[20],
                        "java.lang.String", stringCellStyle);

                row++;

            }

            // Set department name and report period
            // Dept. name
            BusinessEntityUtils.setExcelCellValue(wb, jobReportSheet, 0, 0,
                    //getReportingDepartment().getName(),
                    getSelectedReport().getDepartments().get(0).getName(),
                    "java.lang.String", null);
            BusinessEntityUtils.setExcelCellValue(wb, employeeReportSheet, 0, 0,
                    //getReportingDepartment().getName(),
                    getSelectedReport().getDepartments().get(0).getName(),
                    "java.lang.String", null);
            BusinessEntityUtils.setExcelCellValue(wb, sectorReportSheet, 0, 0,
                    //getReportingDepartment().getName(),
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
            List<Object[]> reportData = Job.getJobReportRecords(
                    getEntityManager1(),
                    BusinessEntityUtils.getDateString(monthlyReportDataDatePeriod.getStartDate(), "'", "YMD", "-"),
                    BusinessEntityUtils.getDateString(monthlyReportDataDatePeriod.getEndDate(), "'", "YMD", "-"),
                    departmentId);

            // Fill in report data            
            System.out.println("rowdata: " + reportData.size());
            for (Object[] rowData : reportData) {
                System.out.println("Inserting row: " + row);
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
            Department department = Department.findDepartmentById(getEntityManager1(), departmentId);
            BusinessEntityUtils.setExcelCellValue(wb, rawData, 0, 1,
                    department.getName(),
                    "java.lang.String", stringCellStyle);
            //  Data starts at:
            BusinessEntityUtils.setExcelCellValue(wb, rawData, 0, 4,
                    monthlyReportDataDatePeriod.getStartDate(),
                    "java.util.Date", dateCellStyle);
            //  Data ends at:
            BusinessEntityUtils.setExcelCellValue(wb, rawData, 0, 6,
                    monthlyReportDataDatePeriod.getEndDate(),
                    "java.util.Date", dateCellStyle);
            //  Month starts at:
            BusinessEntityUtils.setExcelCellValue(wb, rawData, 0, 8,
                    monthlyReportDatePeriod.getStartDate(),
                    "java.util.Date", dateCellStyle);
            //  Month ends at:
            BusinessEntityUtils.setExcelCellValue(wb, rawData, 0, 10,
                    monthlyReportDatePeriod.getEndDate(),
                    "java.util.Date", dateCellStyle);
            // Year type
            BusinessEntityUtils.setExcelCellValue(wb, rawData, 0, 12,
                    monthlyReportYearDatePeriod.getName(),
                    "java.lang.String", stringCellStyle);
            //  Year starts at:
            BusinessEntityUtils.setExcelCellValue(wb, rawData, 0, 15,
                    monthlyReportYearDatePeriod.getStartDate(),
                    "java.util.Date", dateCellStyle);
            //  Year ends at:
            BusinessEntityUtils.setExcelCellValue(wb, rawData, 0, 17,
                    monthlyReportYearDatePeriod.getEndDate(),
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

    /**
     * Get report using a JasperReport compiled report
     *
     * @param databaseDriverClass
     * @param databaseURL
     * @param username
     * @param password
     * @param jasperReportFileName
     * @param exportedReportType
     * @param parameters
     * @return
     */
    public FileInputStream getJasperReportFileInputStream(
            String databaseDriverClass,
            String databaseURL,
            String username,
            String password,
            String jasperReportFileName,
            String exportedReportType,
            HashMap parameters) {

        try {
            // Declare init default reporter
            JRExporter exporter = new JRPdfExporter();

            // Load the required JDBC driver and create the connection
            Class.forName(databaseDriverClass);
            Connection con = DriverManager.getConnection(databaseURL,
                    username,
                    password);

            // Fill report and export it
            JasperPrint print = JasperFillManager.fillReport(jasperReportFileName, parameters, con);

            // Get reporter
            if (exportedReportType.equalsIgnoreCase("HTML")) {
                exporter = new JRHtmlExporter();
            }

            // Configure the exporter (set output file name and print object)
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, "file.pdf");
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
            exporter.exportReport();
            // prepare exported file for transmission to the client
            return new FileInputStream("file.pdf");

        } catch (ClassNotFoundException | SQLException | JRException | FileNotFoundException e) {
            System.out.println(e);
        }

        return null;
    }

    public Report getLatestJobReport(EntityManager em) {
        Report report = Report.findReportById(em, getSelectedReport().getId());
        em.refresh(report);

        return report;
    }

    public StreamedContent getJobEnteredByReportPDFFile() {

        EntityManager em = getEntityManager1();
        HashMap parameters = new HashMap();

        try {

            //reportEmployee1 = Employee.findEmployeeByName(em, getReportEmployee1().getName());
            if (getReportEmployee1().getId() != null) {

                //report = getLatestJobReport(em);
                String reportFileURL = getSelectedReport().getReportFile();

                Connection con = BusinessEntityUtils.establishConnection(
                        (String) SystemOption.getOptionValueObject(em, "defaultDatabaseDriver"),
                        (String) SystemOption.getOptionValueObject(em, "defaultDatabaseURL"),
                        (String) SystemOption.getOptionValueObject(em, "defaultDatabaseUsername"),
                        (String) SystemOption.getOptionValueObject(em, "defaultDatabasePassword"));

                if (con != null) {
                    StreamedContent streamContent;

                    parameters.put("startOFPeriod", reportSearchParameters.getDatePeriod().getStartDate());
                    parameters.put("endOFPeriod", reportSearchParameters.getDatePeriod().getEndDate());
                    parameters.put("inspectorID", getReportEmployee1().getId());

                    // generate report
                    JasperPrint print = JasperFillManager.fillReport(reportFileURL, parameters, con);

                    byte[] fileBytes = JasperExportManager.exportReportToPdf(print);

                    streamContent = new DefaultStreamedContent(new ByteArrayInputStream(fileBytes), "application/pdf", "employee_job_entry.pdf");
                    setLongProcessProgress(100);

                    return streamContent;
                } else {
                    return null;
                }
            } else {
                // tk replace message dialog with growl message
                //displayCommonMessageDialog(null, "The name of employee is required for this report", "Employee Required", "info");
                return null;
            }

        } catch (JRException e) {
            System.out.println(e);
            setLongProcessProgress(100);

            return null;
        }

    }

    // tk to be replaced by getReportStreamedContent()
    public StreamedContent getJobEnteredByDepartmentReportPDFFile() {

        EntityManager em = getEntityManager1();
        HashMap parameters = new HashMap();

        try {

//            reportingDepartment1 = Department.findDepartmentByName(em,
//                    getSelectedReport().getDepartments().get(0).getName()
////            /*getReportingDepartment().getName()*/);
            if (getSelectedReport().getDepartments().get(0).getId() != null) {

                //report = getLatestJobReport(em);
                String reportFileURL = getSelectedReport().getReportFile();

                Connection con = BusinessEntityUtils.establishConnection(
                        (String) SystemOption.getOptionValueObject(em, "defaultDatabaseDriver"),
                        (String) SystemOption.getOptionValueObject(em, "defaultDatabaseURL"),
                        (String) SystemOption.getOptionValueObject(em, "defaultDatabaseUsername"),
                        (String) SystemOption.getOptionValueObject(em, "defaultDatabasePassword"));

                if (con != null) {
                    StreamedContent streamContent;

                    // tk use method used for 
                    parameters.put("startOFPeriod", reportSearchParameters.getDatePeriod().getStartDate());
                    parameters.put("endOFPeriod", reportSearchParameters.getDatePeriod().getEndDate());

                    parameters.put("departmentID",
                            getSelectedReport().getDepartments().get(0).getId());

                    // generate report
                    JasperPrint print = JasperFillManager.fillReport(reportFileURL, parameters, con);

                    byte[] fileBytes = JasperExportManager.exportReportToPdf(print);

                    streamContent = new DefaultStreamedContent(new ByteArrayInputStream(fileBytes), "application/pdf", "department_jobs_entered.pdf");
                    setLongProcessProgress(100);

                    return streamContent;
                } else {
                    return null;
                }
            } else {
                // tk replace message dialog with growl message
                //displayCommonMessageDialog(null, "The name of a department is required for this report", "Department Required", "info");
                return null;
            }

        } catch (JRException e) {
            System.out.println(e);
            setLongProcessProgress(100);

            return null;
        }

    }

    // tk to be replaced by getReportStreamedContent()
    // also to be replaced with jasper version
    public StreamedContent getJobAssignedToDepartmentReportXLSFile() {

        EntityManager em = getEntityManager1();
        HashMap parameters = new HashMap();

        try {

//            reportingDepartment1 = Department.findDepartmentByName(em, 
//                    getSelectedReport().getDepartments().get(0).getName());
            // Use user's department if none found
//            if (getSelectedReport().getDepartments().get(0).getId() == null) {
//                reportingDepartment1 = getUser().getEmployee().getDepartment();
//            }
            //report = getLatestJobReport(em);
            String reportFileURL = getSelectedReport().getReportFile();

            Connection con = BusinessEntityUtils.establishConnection(
                    (String) SystemOption.getOptionValueObject(em, "defaultDatabaseDriver"),
                    (String) SystemOption.getOptionValueObject(em, "defaultDatabaseURL"),
                    (String) SystemOption.getOptionValueObject(em, "defaultDatabaseUsername"),
                    (String) SystemOption.getOptionValueObject(em, "defaultDatabasePassword"));

            if (con != null) {
                StreamedContent streamContent;

                parameters.put("startOFPeriod", reportSearchParameters.getDatePeriod().getStartDate());
                parameters.put("endOFPeriod", reportSearchParameters.getDatePeriod().getEndDate());
                parameters.put("departmentID", getSelectedReport().getDepartments().get(0).getId());
                parameters.put("departmentName", getSelectedReport().getDepartments().get(0).getName());
                // generate report
                JasperPrint print = JasperFillManager.fillReport(reportFileURL, parameters, con);

                JRXlsExporter exporterXLS = new JRXlsExporter();
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                exporterXLS.setParameter(JRXlsExporterParameter.JASPER_PRINT, print);
                exporterXLS.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, outStream);
                exporterXLS.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.FALSE);
                exporterXLS.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
                exporterXLS.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
                exporterXLS.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
                exporterXLS.exportReport();

                streamContent = new DefaultStreamedContent(new ByteArrayInputStream(outStream.toByteArray()), "application/xls", "department_jobs_assigned.xls");
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
    
    public ArrayList getDateSearchFields() {      
        return DatePeriod.getDateSearchFields();
    }
}
