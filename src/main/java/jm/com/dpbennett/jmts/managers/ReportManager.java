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
import jm.com.dpbennett.business.entity.Address;
import jm.com.dpbennett.business.entity.Client;
import jm.com.dpbennett.business.entity.Contact;
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
import jm.com.dpbennett.business.entity.utils.BusinessEntityUtils;
import jm.com.dpbennett.business.entity.utils.DatePeriodJobReport;
import jm.com.dpbennett.business.entity.utils.DatePeriodJobReportColumnData;
import jm.com.dpbennett.business.entity.utils.SearchParameters;
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
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import jm.com.dpbennett.jmts.Application;
import jm.com.dpbennett.jmts.utils.MainTabView;
import net.sf.jasperreports.engine.JasperRunManager;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author Desmond Bennett
 */
@Named
@SessionScoped
public class ReportManager implements Serializable {

    @PersistenceUnit(unitName = "JMTSPU")
    private EntityManagerFactory EMF1;
    @PersistenceUnit(unitName = "AccPacPU")
    private EntityManagerFactory EMF2;
    private Job currentJob;
    private String columnsToExclude;
    private Report report;
    private StreamedContent reportFile;
    private Integer longProcessProgress;
    private Department reportingDepartment;
    private String reportSearchText;
    private List<Job> currentPeriodJobReportSearchResultList;
    private List<Job> previousPeriodJobReportSearchResultList;
    private DatePeriod previousDatePeriod;
    private DatePeriodJobReport jobSubCategoryReport;
    private DatePeriodJobReport sectorReport;
    private DatePeriodJobReport jobQuantitiesAndServicesReport;
    private SearchParameters reportSearchParameters;
    private Employee reportEmployee;
    // Monthly report date periods
    private DatePeriod monthlyReportDatePeriod;
    private DatePeriod monthlyReportDataDatePeriod;
    private DatePeriod monthlyReportYearDatePeriod;
    private JobManagerUser user;
    private MainTabView mainTabView;

    /**
     * Creates a new instance of JobManagerBean
     */
    public ReportManager() {
        this.longProcessProgress = 0;
        this.columnsToExclude = "";
        // Accpac fields init        
        report = new Report();
        // reporting vars init
        ArrayList searchTypes = new ArrayList();
        ArrayList searchDateFields = new ArrayList();
        // Add search types
        searchTypes.add(new SelectItem("General", "General"));
        // Add search fields
        searchDateFields.add(new SelectItem("dateSubmitted", "Date submitted"));
        searchDateFields.add(new SelectItem("dateOfCompletion", "Date completed"));
        searchDateFields.add(new SelectItem("dateAndTimeEntered", "Date entered"));
        previousDatePeriod = new DatePeriod("Last month", "month", null, null, false, false, true);
        reportSearchParameters
                = new SearchParameters(
                        "Report Data Search",
                        null,
                        false,
                        searchTypes,
                        true,
                        "dateSubmitted",
                        true,
                        searchDateFields,
                        "General",
                        new DatePeriod("This month", "month", null, null, false, false, true),
                        "");

        // Monthly report date periods
        monthlyReportDatePeriod = new DatePeriod("Last month", "month", null, null, false, false, true);
        monthlyReportDataDatePeriod = new DatePeriod(
                "Custom",
                "any",
                BusinessEntityUtils.createDate(2012, 3, 1), // tk make option
                new Date(), false, false, true);
        monthlyReportYearDatePeriod = new DatePeriod(
                "This financial year to date",
                "year",
                null,
                null, false, false, true);

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

    public MainTabView getMainTabView() {
        return mainTabView;
    }

    public void setMainTabView(MainTabView mainTabView) {
        this.mainTabView = mainTabView;
    }

    public void closeReportsTab() {
        mainTabView.renderTab(getEntityManager1(), "reportsTab", false);
    }

    public JobManagerUser getUser() {
        return user;
    }

    public void setUser(JobManagerUser user) {
        this.user = user;
    }

    public void init() {
        System.out.println("Initializing Report Manager...");
    }

    public final EntityManager getEntityManager1() {
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

    public Employee getReportEmployee() {
        if (reportEmployee == null) {
            reportEmployee = new Employee();
        }
        return reportEmployee;
    }

    public List<Employee> completeEmployee(String query) {
        EntityManager em = null;

        try {

            em = getEntityManager1();
            List<Employee> employees = Employee.findActiveEmployeesByName(em, query);

            if (employees != null) {
                return employees;
            } else {
                return new ArrayList<>();
            }
        } catch (Exception e) {
            System.out.println(e);
            return new ArrayList<>();
        }
    }

    public void setReportEmployee(Employee reportEmployee) {
        this.reportEmployee = reportEmployee;
    }

    public SearchParameters getReportSearchParameters() {
        return reportSearchParameters;
    }

    public List<SelectItem> getDatePeriods() {
        ArrayList<SelectItem> datePeriods = new ArrayList<>();

        // add items
        if (report.getName().equals("Monthly report")) {
            for (String name : DatePeriod.getDatePeriodNames()) {
                // get only month date periods                
                datePeriods.add(new SelectItem(name, name));

            }
        } else {
            for (String name : DatePeriod.getDatePeriodNames()) {
                datePeriods.add(new SelectItem(name, name));
            }
        }

        return datePeriods;
    }

    public int getNumberOfCurrentPeriodJobsFound() {
        if (currentPeriodJobReportSearchResultList != null) {
            return currentPeriodJobReportSearchResultList.size();
        }

        return 0;
    }

    public void handleCurrentPeriodStartDateSelect(SelectEvent event) {
        reportSearchParameters.getDatePeriod().setStartDate((Date) event.getObject());
        updateJobReport();
    }

    public void handleCurrentPeriodEndDateSelect(SelectEvent event) {
        reportSearchParameters.getDatePeriod().setEndDate((Date) event.getObject());
        updateJobReport();
    }

    public void setReportSearchParameters(SearchParameters reportSearchParameters) {
        this.reportSearchParameters = reportSearchParameters;
    }

    public DatePeriod getPreviousDatePeriod() {
        return previousDatePeriod;
    }

    public void setPreviousDatePeriod(DatePeriod previousDatePeriod) {
        this.previousDatePeriod = previousDatePeriod;
    }

    public List<Job> getPreviousPeriodJobReportSearchResultList() {
        return previousPeriodJobReportSearchResultList;
    }

    public void setPreviousPeriodJobReportSearchResultList(List<Job> previousPeriodJobReportSearchResultList) {
        this.previousPeriodJobReportSearchResultList = previousPeriodJobReportSearchResultList;
    }

    public Department getReportingDepartment() {
        if (reportingDepartment == null) {
            reportingDepartment = new Department("");
        }
        return reportingDepartment;
    }

    public void setReportingDepartment(Department reportingDepartment) {
        this.reportingDepartment = reportingDepartment;
    }

    public List<Department> completeDepartment(String query) {
        EntityManager em = null;

        try {
            em = getEntityManager1();

            List<Department> departments = Department.findActiveDepartmentsByName(em, query);

            return departments;

        } catch (Exception e) {
            System.out.println(e + ": completeDepartment");
            return new ArrayList<>();
        }
    }

    public String getReportSearchText() {
        return reportSearchText;
    }

    public void setReportSearchText(String reportSearchText) {
        this.reportSearchText = reportSearchText;
    }

    public StreamedContent getReportFile() {

        EntityManager em = null;

        try {
            em = getEntityManager1();

            report = em.find(Report.class, report.getId());

            if (report.getReportFileMimeType().equals("application/jasper")) {
                if (report.getName().equals("Jobs entered by employee")) {
                    reportFile = getJobEnteredByReportPDFFile();
                }
                if (report.getName().equals("Jobs entered by department")) {
                    reportFile = getJobEnteredByDepartmentReportPDFFile();
                }
                if (report.getName().equals("Jobs assigned to department")) {
                    reportFile = getJobAssignedToDepartmentReportXLSFile();
                }
            } else if (report.getReportFileMimeType().equals("application/xlsx")) {
                if (report.getName().equals("Jobs completed by department")) {
                    reportFile = getCompletedByDepartmentReport(em);
                }

                if (report.getName().equals("Analytical Services Report")) {
                    reportFile = getAnalyticalServicesReport(em);
                }
            } else if (report.getReportFileMimeType().equals("application/xls")) {
                if (report.getName().equals("Monthly report")) {
                    reportFile = getMonthlyReport3(em);
                } else {
                    reportFile = getExcelReportStreamContent();
                }
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
            DatePeriod datePeriods[] = BusinessEntityUtils.getMonthlyReportDatePeriods(reportSearchParameters.getDatePeriod());

            List<JobSubCategory> subCategories = JobSubCategory.findAllJobSubCategoriesGroupedByEarningsByDepartment(em, reportingDepartment);
            List<Sector> sectors = Sector.findAllSectorsByDeparment(em, reportingDepartment);
            List<JobReportItem> jobReportItems = JobReportItem.findAllJobReportItemsByDeparment(em, reportingDepartment);

            // reports
            jobSubCategoryReport = new DatePeriodJobReport(reportingDepartment, subCategories, null, null, datePeriods);
            sectorReport = new DatePeriodJobReport(reportingDepartment, null, sectors, null, datePeriods);
            jobQuantitiesAndServicesReport = new DatePeriodJobReport(reportingDepartment, null, null, jobReportItems, datePeriods);

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
            FileInputStream stream = createExcelJobReportFileInputStream(
                    this.getClass().getResource("MonthlyReport.xls"),
                    getUser(), reportingDepartment, jobSubCategoryReport, sectorReport, jobQuantitiesAndServicesReport);

            return new DefaultStreamedContent(stream, report.getReportFileMimeType(), report.getReportFile());

        } catch (Exception ex) {
            System.out.println(ex);
        }

        return null;
    }

    public StreamedContent getMonthlyReport2(EntityManager em) {

        updateReportingDepartment();

        try {
            // Get byte stream for report file
            ByteArrayInputStream stream = createExcelMonthlyReportFileInputStream(new File(report.getReportFileTemplate()),
                    reportingDepartment.getId());

            return new DefaultStreamedContent(stream, report.getReportFileMimeType(), report.getReportFile());

        } catch (Exception ex) {
            System.out.println(ex);
        }

        return null;
    }

    public StreamedContent getMonthlyReport3(EntityManager em) {

        updateReportingDepartment();

        try {
            // Get byte stream for report file
            ByteArrayInputStream stream = createExcelMonthlyReportFileInputStream2(new File(report.getReportFileTemplate()),
                    reportingDepartment.getId());

            return new DefaultStreamedContent(stream, report.getReportFileMimeType(), report.getReportFile());

        } catch (Exception ex) {
            System.out.println(ex);
        }

        return null;
    }

    public StreamedContent getCompletedByDepartmentReport(EntityManager em) {

        updateReportingDepartment();

        try {
            // Get byte stream for report file
            ByteArrayInputStream stream = jobsCompletedByDepartmentFileInputStream(new File(report.getReportFileTemplate()),
                    reportingDepartment.getId());

            return new DefaultStreamedContent(stream, report.getReportFileMimeType(), report.getReportFile());

        } catch (Exception ex) {
            System.out.println(ex);
        }

        return null;
    }

    public StreamedContent getAnalyticalServicesReport(EntityManager em) {

        updateReportingDepartment(); // tk remove when dept is obtained via converter etc.

        try {
            // Get byte stream for report file
            ByteArrayInputStream stream = analyticalServicesReportFileInputStream(new File(report.getReportFileTemplate()),
                    reportingDepartment.getId());

            return new DefaultStreamedContent(stream, report.getReportFileMimeType(), report.getReportFile());

        } catch (Exception ex) {
            System.out.println(ex);
        }

        return null;
    }

    public StreamedContent getExcelReportStreamContent() throws URISyntaxException {

        try {

            URL url = this.getClass().getResource(report.getReportFileTemplate());
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
            FileOutputStream out = new FileOutputStream(report.getReportFile() + getUser().getId());

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

            if (!report.getReportColumns().isEmpty()) {
                HSSFRow headerRow = jobSheet.createRow(startingRow++);
                // Setup header style
                HSSFCellStyle headerCellStyle = wb.createCellStyle();
                headerCellStyle.setFont(headerFont);
                headerCellStyle.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
                headerCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
                headerCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
                for (int i = 0; i < report.getReportColumns().size(); i++) {
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
                        report.getReportColumns().size() - 1 //last column
                ));
                // Set header title
                headerRow.getCell(0).setCellValue(new HSSFRichTextString(report.getName()));

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
                for (int i = 0; i < report.getReportColumns().size(); i++) {
                    columnHeaderRow.createCell(i).setCellValue(new HSSFRichTextString(report.getReportColumns().get(i).getName().trim()));
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
                    for (int i = 0; i < report.getReportColumns().size(); i++) {

                        try {
                            ReportTableColumn col = report.getReportColumns().get(i);
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

            FileInputStream stream = new FileInputStream(report.getReportFile() + getUser().getId());
            return new DefaultStreamedContent(stream, report.getReportFileMimeType(), report.getReportFile());

        } catch (IOException ex) {
            System.out.println(ex);
        }

        return null;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public Boolean getDisableReportingDepartment() {
        if (report.getName().equals("Jobs entered by department")
                || report.getName().equals("Jobs for my department")) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean getRenderReportingDepartment() {
        if (report.getName().equals("Monthly report")
                || report.getName().equals("Jobs for my department")
                || report.getName().equals("Jobs assigned to department")
                || report.getName().equals("Jobs entered by department")
                || report.getName().equals("Jobs completed by department")
                || report.getName().equals("Analytical Services Report")) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean getRenderJobsReportTableTabView() {
        if (report.getName().equals("Jobs for my department")
                || report.getName().equals("Jobs in period")) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean getIsMonthlyReport() {
        if (report.getName().equals("Monthly report")) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean getRenderEmployee() {
        if (report.getName().equals("Jobs entered by employee")) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean getRenderPreviousPeriod() {
        if (report.getName().equals("Monthly report")) {
            return true;
        } else {
            return false;
        }
    }

    public void refreshJobReport() {
        updateJobReport();
    }

    public void updateServiceContract() {

    }

    public Boolean getCurrentJobIsValid() {
        if (getCurrentJob().getId() != null) {
            return true;
        }

        return false;
    }

    public Boolean getCanExportJobCosting() {
        if (getCurrentJob().getJobCostingAndPayment().getCostingApproved()
                && getCurrentJob().getJobCostingAndPayment().getCostingCompleted()) {
            return false;
        }
        return true;
    }

    /**
     * Search for jobs using the current job report search parameters.
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public List<Job> doJobReportSearch(Date startDate, Date endDate) {
        EntityManager em = getEntityManager1();

        List<Job> jobReportSearchResultList = Job.findJobsByDateSearchField(em,
                getUser(),
                reportSearchParameters.getDateField(),
                reportSearchParameters.getJobType(),
                report.getName(),
                reportSearchText,
                startDate, endDate, false);

        if (jobReportSearchResultList == null) {
            jobReportSearchResultList = new ArrayList<>();
        }

        return jobReportSearchResultList;

    }

    public void updateJobReport() {

        EntityManager em = getEntityManager1();

        if (report.getId() != null) {
            report = getLatestJobReport(em);

            // set search text if require
            if (report.getName().equals("Jobs for my department")) {
                reportSearchText = getUser().getEmployee().getDepartment().getName();
                reportingDepartment = getUser().getEmployee().getDepartment();
                currentPeriodJobReportSearchResultList = doJobReportSearch(reportSearchParameters.getDatePeriod().getStartDate(), reportSearchParameters.getDatePeriod().getEndDate());
            } else if (report.getName().equals("Jobs for my department")) {
                reportSearchText = getUser().getEmployee().getDepartment().getName();
                reportingDepartment = getUser().getEmployee().getDepartment();
                currentPeriodJobReportSearchResultList = doJobReportSearch(reportSearchParameters.getDatePeriod().getStartDate(), reportSearchParameters.getDatePeriod().getEndDate());
            } else if (report.getName().equals("Jobs entered by department")) {
                if (reportingDepartment == null) {
                    reportingDepartment = getUser().getEmployee().getDepartment();
                    reportSearchText = reportingDepartment.getName();
                } else {
                    reportSearchText = reportingDepartment.getName();
                }
                reportSearchParameters.setDateField("dateAndTimeEntered");
            } else if (report.getName().equals("Monthly report")) {
                if (reportingDepartment == null) {
                    reportingDepartment = getUser().getEmployee().getDepartment();
                    reportSearchText = reportingDepartment.getName();
                } else {
                    reportSearchText = reportingDepartment.getName();
                }
                currentPeriodJobReportSearchResultList = new ArrayList<>();
                previousPeriodJobReportSearchResultList = new ArrayList<>();
            } else if (report.getName().equals("Jobs entered by employee")) {
                reportSearchParameters.setDateField("dateAndTimeEntered");
            } else if (report.getName().equals("Jobs entered by department")) {
                reportSearchParameters.setDateField("dateAndTimeEntered");
            } else if (report.getName().equals("Jobs completed by department")
                    || report.getName().equals("Analytical Services Report")) {
                reportSearchParameters.setDateField("dateOfCompletion");
                if (getReportingDepartment().getName().equals("")) {
                    reportingDepartment = getUser().getEmployee().getDepartment();
                }
            } else {
                currentPeriodJobReportSearchResultList = new ArrayList<>();
            }
        }

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

    public Job getCurrentJob() {
        return currentJob;
    }

    public void setCurrentJob(Job currentJob) {
        this.currentJob = currentJob;
    }

    public void postProcessXLS(Object document) {
        Long jobId = null;
        DateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");

        // NB: Save the current job id for later restoration
        // of the current job
        if (currentJob != null) {
            jobId = currentJob.getId();
        }

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

                header.createCell(0).setCellValue(new HSSFRichTextString(report.getName()));
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

        // NB: Data exporter sets the current job to null
        // so this ensures that it is not null before returning
        // to the client.
        EntityManager em = getEntityManager1();
        if (jobId != null) {
            currentJob = em.find(Job.class, jobId);
        } else {
            currentJob = Job.create(em, getUser(), true);
        }
    }

    public void updateDepartmentReport() {
    }

    private EntityManagerFactory getEMF1() {
        return EMF1;
    }

    private EntityManagerFactory getEMF2() {
        return EMF2;
    }

    public List<Job> getCurrentPeriodJobReportSearchResultList() {
        if (currentPeriodJobReportSearchResultList == null) {
            currentPeriodJobReportSearchResultList = new ArrayList<>();
        }
        return currentPeriodJobReportSearchResultList;
    }

    public Boolean getCanExportCurrentPeriodJobReport() {
        if (report.getName().equals("Monthly report")) {
            return true;
        }

        if (report.getName().equals("Jobs entered by employee")) {
            if (Employee.findEmployeeByName(getEntityManager1(), getReportEmployee().getName()) != null) {
                return true;
            } else {
                return false;
            }
        }

        if (report.getName().equals("Jobs entered by department")
                || report.getName().equals("Jobs assigned to department")
                || report.getName().equals("Jobs completed by department")
                || report.getName().equals("Analytical Services Report")) {
            if (Department.findDepartmentByName(getEntityManager1(), getReportingDepartment().getName()) != null) {
                return true;
            } else {
                return false;
            }
        }

        // fix this. should check for current period 
        if (currentPeriodJobReportSearchResultList != null) {
            if (!currentPeriodJobReportSearchResultList.isEmpty()) {
                return true;
            } else {
                return false;
            }
        }

        return false;
    }

    public void updateReportDateField() {
        updateJobReport();
    }

    public void changeCurrentDateSearchPeriod() {
        reportSearchParameters.getDatePeriod().initDatePeriod();
        updateJobReport();
    }

    public void changeMonthlyReportDatePeriods() {
        getMonthlyReportDatePeriod().initDatePeriod();
        getMonthlyReportDataDatePeriod().initDatePeriod();
        getMonthlyReportYearDatePeriod().initDatePeriod();
        updateJobReport();
    }

    public void changePreviousDateSearchPeriod() {
        previousDatePeriod.initDatePeriod();
        updateJobReport();
    }

    public void updateReportingDepartment() {
        EntityManager em = null;

        try {
            em = getEntityManager1();

            Department department = Department.findDepartmentByName(em, getReportingDepartment().getName());
            if (department != null) {
                reportingDepartment = department;
                updateJobReport();
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void updateReportEmployee() {
        EntityManager em = getEntityManager1();

        try {
            reportEmployee = Employee.findEmployeeByName(em, getReportEmployee().getName());

        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public String getReportsLocation() {
        return "."; // to be extracted from database
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
            List<Object[]> reportData = Job.getCompletedJobRecords(
                    getEntityManager1(),
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

        } catch (Exception ex) {
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

            // Output stream for modified Excel file
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            // Get sheets          
            XSSFSheet rawData = wb.getSheet("Raw Data");
            XSSFSheet jobReportSheet = wb.getSheet("Jobs Report");
            XSSFSheet employeeReportSheet = wb.getSheet("Employee Report");
            XSSFSheet sectorReportSheet = wb.getSheet("Sector Report");

            // Get report data
            List<Object[]> reportData = Job.getCompletedJobRecords(
                    getEntityManager1(),
                    BusinessEntityUtils.getDateString(reportSearchParameters.getDatePeriod().getStartDate(), "'", "YMD", "-"),
                    BusinessEntityUtils.getDateString(reportSearchParameters.getDatePeriod().getEndDate(), "'", "YMD", "-"),
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
                    getReportingDepartment().getName(),
                    "java.lang.String", null);
            BusinessEntityUtils.setExcelCellValue(wb, employeeReportSheet, 0, 0,
                    getReportingDepartment().getName(),
                    "java.lang.String", null);
            BusinessEntityUtils.setExcelCellValue(wb, sectorReportSheet, 0, 0,
                    getReportingDepartment().getName(),
                    "java.lang.String", null);
            // Period
            BusinessEntityUtils.setExcelCellValue(wb, jobReportSheet, 2, 0,
                    BusinessEntityUtils.getDateInMediumDateFormat(reportSearchParameters.getDatePeriod().getStartDate())
                    + " - "
                    + BusinessEntityUtils.getDateInMediumDateFormat(reportSearchParameters.getDatePeriod().getEndDate()),
                    "java.lang.String", null);
            BusinessEntityUtils.setExcelCellValue(wb, employeeReportSheet, 2, 0,
                    BusinessEntityUtils.getDateInMediumDateFormat(reportSearchParameters.getDatePeriod().getStartDate())
                    + " - "
                    + BusinessEntityUtils.getDateInMediumDateFormat(reportSearchParameters.getDatePeriod().getEndDate()),
                    "java.lang.String", null);
            BusinessEntityUtils.setExcelCellValue(wb, sectorReportSheet, 2, 0,
                    BusinessEntityUtils.getDateInMediumDateFormat(reportSearchParameters.getDatePeriod().getStartDate())
                    + " - "
                    + BusinessEntityUtils.getDateInMediumDateFormat(reportSearchParameters.getDatePeriod().getEndDate()),
                    "java.lang.String", null);

            // Write modified Excel file and return it
            wb.write(out);

            return new ByteArrayInputStream(out.toByteArray());

        } catch (Exception ex) {
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

        } catch (Exception ex) {
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

    public FileInputStream createServiceContractExcelFileInputStream_bkup(
            EntityManager em,
            JobManagerUser user,
            Long jobId,
            String filePath) {
        try {

            Job job = Job.findJobById(em, jobId);

            Client ativeClient = Application.getActiveClientByNameIfAvailable(em, job.getClient());

            File file = new File(filePath);

            FileInputStream inp = new FileInputStream(file);

            // create workbook from input file
            POIFSFileSystem fileSystem = new POIFSFileSystem((FileInputStream) inp);

            HSSFWorkbook wb = new HSSFWorkbook(fileSystem);
            HSSFCellStyle dataCellStyle = getDefaultCellStyle(wb);
            // Default font
            Font defaultFont = getFont(wb, "Arial", (short) 16);
            Font departmentUseOnlyFont = getFont(wb, "Arial", (short) 12);

            // create temp file for output
            FileOutputStream out = new FileOutputStream("ServiceContract-" + user.getId() + ".xls");

            // get service contract sheet
            HSSFSheet serviceContractSheet = wb.getSheet("ServiceContract");
            serviceContractSheet.setActive(true);
            serviceContractSheet.setForceFormulaRecalculation(true);

            // fill in job data
            // company name and address
            dataCellStyle.setBorderLeft((short) 2);
            dataCellStyle.setFont(defaultFont);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 4, 0,
                    ativeClient.getName(),
                    "java.lang.String", dataCellStyle);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 5, 0,
                    ativeClient.getBillingAddress().getAddressLine1(),
                    "java.lang.String", dataCellStyle);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 6, 0,
                    ativeClient.getBillingAddress().getAddressLine2(),
                    "java.lang.String", dataCellStyle);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 7, 0,
                    ativeClient.getBillingAddress().getStateOrProvince(),
                    "java.lang.String", dataCellStyle);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 8, 0,
                    ativeClient.getBillingAddress().getCity(),
                    "java.lang.String", dataCellStyle);

            // contracting business office
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 4, 8,
                    job.getBusinessOffice().getName(),
                    "java.lang.String", dataCellStyle);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 5, 8,
                    job.getBusinessOffice().getAddress().getAddressLine1(),
                    "java.lang.String", dataCellStyle);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 6, 8,
                    job.getBusinessOffice().getAddress().getAddressLine2(),
                    "java.lang.String", dataCellStyle);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 7, 8,
                    job.getBusinessOffice().getAddress().getStateOrProvince(),
                    "java.lang.String", dataCellStyle);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 8, 8,
                    job.getBusinessOffice().getAddress().getCity(),
                    "java.lang.String", dataCellStyle);

            // Department use only
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
            dataCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            dataCellStyle.setBorderBottom((short) 1);
            dataCellStyle.setFont(departmentUseOnlyFont);
            dataCellStyle.setAlignment(CellStyle.VERTICAL_BOTTOM);
            dataCellStyle.setAlignment(CellStyle.ALIGN_LEFT);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 4, 16,
                    job.getDepartment().getName(),
                    "java.lang.String", dataCellStyle);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 5, 16,
                    job.getJobNumber(),
                    "java.lang.String", dataCellStyle);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 6, 16,
                    job.getJobStatusAndTracking().getEnteredBy().getFirstName() + " " + job.getJobStatusAndTracking().getEnteredBy().getLastName(),
                    "java.lang.String", dataCellStyle);
            // Date and time
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 7, 16,
                    BusinessEntityUtils.getDateInMediumDateAndTimeFormat(new Date()),
                    "java.lang.String", dataCellStyle);

            // contact person
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderLeft((short) 2);
            dataCellStyle.setFont(defaultFont);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 11, 0,
                    ativeClient.getMainContact(),
                    "java.lang.String", dataCellStyle);

            // phone number
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 13, 0,
                    ativeClient.getMainContact().getMainPhoneNumber().getLocalNumber(),
                    "java.lang.String", dataCellStyle);

            // fax number
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 13, 4,
                    ativeClient.getMainContact().getMainFaxNumber().getLocalNumber(),
                    "java.lang.String", dataCellStyle);

            // email address
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 13, 7,
                    ativeClient.getMainContact().getInternet().getEmail1(),
                    "java.lang.String", dataCellStyle);

            // p.o number
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
            dataCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            dataCellStyle.setBorderBottom((short) 2);
            dataCellStyle.setBorderLeft((short) 2);
            dataCellStyle.setFont(defaultFont);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 21, 0,
                    job.getJobCostingAndPayment().getPurchaseOrderNumber(),
                    "java.lang.String", dataCellStyle);

            // date submitted
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 21, 3,
                    BusinessEntityUtils.getDateInMediumDateFormat(job.getJobStatusAndTracking().getDateSubmitted()),
                    "java.lang.String", dataCellStyle);

            // estimated turn around time
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 21, 6,
                    job.getEstimatedTurnAroundTimeInDays() + " day(s)",
                    "java.lang.String", dataCellStyle);

            // estimated cost
            dataCellStyle.setAlignment(CellStyle.ALIGN_LEFT);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 21, 8,
                    job.getJobCostingAndPayment().getEstimatedCost(),
                    "Currency", dataCellStyle);

            // total deposit
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 21, 11,
                    job.getJobCostingAndPayment().getDeposit(),
                    "Currency", dataCellStyle);

            // receipt no(s)        
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 21, 14,
                    job.getJobCostingAndPayment().getReceiptNumber(),
                    "java.lang.String", dataCellStyle);

            // date deposit paid
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
            dataCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            dataCellStyle.setBorderBottom((short) 2);
            dataCellStyle.setFont(defaultFont);

            if (job.getJobStatusAndTracking().getDepositDate() != null) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 21, 17,
                        BusinessEntityUtils.getDateInMediumDateFormat(job.getJobStatusAndTracking().getDepositDate()),
                        "java.lang.String", dataCellStyle);
            }

            // service requested
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setFont(getWingdingsFont(wb));
            dataCellStyle.setAlignment(CellStyle.ALIGN_RIGHT);
            if (job.getServiceContract().getServiceRequestedCalibration()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 23, 2,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }
            if (job.getServiceContract().getServiceRequestedLabelEvaluation()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 23, 4,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }
            if (job.getServiceContract().getServiceRequestedInspection()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 24, 0,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }
            if (job.getServiceContract().getServiceRequestedConsultancy()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 24, 2,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }
            if (job.getServiceContract().getServiceRequestedTraining()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 24, 4,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }
            if (job.getServiceContract().getServiceRequestedOther()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 24, 6,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }

            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setFont(getWingdingsFont(wb));
            dataCellStyle.setBorderLeft((short) 2);
            if (job.getServiceContract().getServiceRequestedTesting()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 23, 0,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderRight((short) 2);
            if ((job.getServiceContract().getServiceRequestedOtherText() != null)
                    && (!job.getServiceContract().getServiceRequestedOtherText().isEmpty())) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 24, 7,
                        "Other (" + job.getServiceContract().getServiceRequestedOtherText() + ")",
                        "java.lang.String", dataCellStyle);
            }

            // details/samples
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderBottom((short) 2);
            dataCellStyle.setBorderLeft((short) 2);
            dataCellStyle.setAlignment(CellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(CellStyle.VERTICAL_TOP);
            dataCellStyle.setWrapText(true);
            dataCellStyle.setFont(defaultFont);
            String samplesDetail = "";

            for (JobSample jobSample : job.getJobSamples()) {
                samplesDetail = samplesDetail + jobSample.getSampleDetail() + "\n";
            }
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 26, 0,
                    samplesDetail,
                    "java.lang.String", dataCellStyle);

            // Special instructions           
            // NB: Instruction and joined with special instructions for now
            // until new service contract is implemented.
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 25, 8,
                    job.getInstructions() + "\n" + job.getServiceContract().getSpecialInstructions(),
                    "java.lang.String", dataCellStyle);

            // intended market
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setFont(getWingdingsFont(wb));
            dataCellStyle.setAlignment(CellStyle.ALIGN_RIGHT);
            dataCellStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
            dataCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

            if (job.getServiceContract().getIntendedMarketLocal()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 47, 16, // org 49, 16
                        "þ",
                        "java.lang.String", dataCellStyle);
            }
            if (job.getServiceContract().getIntendedMarketCaricom()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 47, 18,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }
            if (job.getServiceContract().getIntendedMarketUK()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 47, 20,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }
            if (job.getServiceContract().getIntendedMarketCanada()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 49, 18,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }
            if (job.getServiceContract().getIntendedMarketOther()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 49, 20,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setFont(getWingdingsFont(wb));
            dataCellStyle.setAlignment(CellStyle.ALIGN_RIGHT);
            dataCellStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
            dataCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            dataCellStyle.setBorderLeft((short) 2);
            if (job.getServiceContract().getIntendedMarketUSA()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 49, 16,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }

            // Sample disposal
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setFont(getWingdingsFont(wb));
            dataCellStyle.setAlignment(CellStyle.ALIGN_RIGHT);
            dataCellStyle.setBorderLeft((short) 2);
            // Collected by the client within 30 days
            if (checkForSampleDisposalMethod(currentJob.getJobSamples(), 1)) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 47, 0,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }
            // Disposed of by the company
            if (checkForSampleDisposalMethod(job.getJobSamples(), 2)) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 48, 0,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }

            // addservice requested
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setFont(getWingdingsFont(wb));
            dataCellStyle.setAlignment(CellStyle.ALIGN_RIGHT);
            dataCellStyle.setBorderLeft((short) 2);
            if (job.getServiceContract().getAdditionalServiceUrgent()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 14, 11,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }

            if (job.getServiceContract().getAdditionalServiceFaxResults()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 15, 11,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }

            if (job.getServiceContract().getAdditionalServiceTelephonePresumptiveResults()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 16, 11,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }

            if (job.getServiceContract().getAdditionalServiceSendMoreContractForms()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 17, 11,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }

            if (job.getServiceContract().getAdditionalServiceOther()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 18, 11,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }

            // other additinal service
            dataCellStyle = getDefaultCellStyle(wb);
            Font font = getFont(wb, "Arial", (short) 14);
            font.setBoldweight(Font.BOLDWEIGHT_BOLD);
            dataCellStyle.setFont(font);
            dataCellStyle.setBorderBottom((short) 1);
            if (job.getServiceContract().getAdditionalServiceOtherText() != null) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 18, 13,
                        job.getServiceContract().getAdditionalServiceOtherText(),
                        "java.lang.String", dataCellStyle);
            }

            // write and save file for later use
            wb.write(out);
            out.close();

            return new FileInputStream("ServiceContract-" + user.getId() + ".xls");
        } catch (Exception e) {
            System.out.println(e);
        }

        return null;
    }

    public FileInputStream createServiceContractExcelFileInputStream(
            EntityManager em,
            JobManagerUser user,
            Long jobId,
            String filePath) {
        try {

            Job job = Job.findJobById(em, jobId);
            job.getJobCostingAndPayment().calculateAmountDue();

            Client ativeClient = Application.getActiveClientByNameIfAvailable(em, job.getClient());

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
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "A6", // A = 0 , 6 = 5
                    currentJob.getJobNumber(),
                    "java.lang.String", dataCellStyle);

            // Contracting business office       
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderLeft((short) 1);
            dataCellStyle.setBorderBottom((short) 1);
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "K6",
                    currentJob.getBusinessOffice().getName(),
                    "java.lang.String", dataCellStyle);
            BusinessEntityUtils.setExcelCellValue(
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
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "F9",
                    currentJob.getJobStatusAndTracking().getEnteredBy().getName(),
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
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "F10",
                    currentJob.getJobStatusAndTracking().getDateAndTimeEntered(),
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
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "F11",
                    currentJob.getDepartment().getName(),
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
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "F12",
                    currentJob.getEstimatedTurnAroundTimeInDays(),
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
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "F13",
                    currentJob.getJobCostingAndPayment().getEstimatedCost(),
                    "Currency", dataCellStyle);

            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderBottom((short) 1);
            dataCellStyle.setBorderTop((short) 1);
            dataCellStyle.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            dataCellStyle.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "F14",
                    currentJob.getJobCostingAndPayment().getEstimatedCost()
                    * currentJob.getJobCostingAndPayment().getPercentageGCT() / 100,
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
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "F15",
                    currentJob.getJobCostingAndPayment().getEstimatedCostIncludingTaxes(),
                    "Currency", dataCellStyle);

            // Minimum First Deposit (J$):
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderBottom((short) 1);
            dataCellStyle.setBorderTop((short) 1);
            dataCellStyle.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "F16",
                    currentJob.getJobCostingAndPayment().getMinDepositIncludingTaxes(),
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
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "R9",
                    currentJob.getJobCostingAndPayment().getReceiptNumber(),
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
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "V9",
                    currentJob.getJobCostingAndPayment().getDeposit(),
                    "Currency", dataCellStyle);

            // PAYMENT BREAKDOWN
            // Calculate GCT and Job Cost
            Double jobCost = (100.0 * currentJob.getJobCostingAndPayment().getDeposit())
                    / (currentJob.getJobCostingAndPayment().getPercentageGCT() + 100.0);
            Double jobGCT = currentJob.getJobCostingAndPayment().getDeposit() - jobCost;
            // GCT
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderTop((short) 1);
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "AC9",
                    jobGCT,
                    "Currency", dataCellStyle);
            // Job
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderBottom((short) 1);
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "AC10",
                    jobCost,
                    "Currency", dataCellStyle);

            // DATE PAID (date of last payment)
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderBottom((short) 1);
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "AH9",
                    currentJob.getJobStatusAndTracking().getDepositDate(),
                    "java.util.Date", dataCellStyle);

            // BALANCE (amount due) 
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            if (currentJob.getJobCostingAndPayment().getFinalCost() > 0.0) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, "AL9",
                        "exactly",
                        "java.lang.String", dataCellStyle);
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, "AL10",
                        currentJob.getJobCostingAndPayment().getAmountDue(),
                        "Currency", dataCellStyle);
            } else {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, "AL9",
                        "approximately",
                        "java.lang.String", dataCellStyle);
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, "AL10",
                        currentJob.getJobCostingAndPayment().getEstimatedCostIncludingTaxes()
                        - currentJob.getJobCostingAndPayment().getDeposit(),
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
            if (!currentJob.getJobCostingAndPayment().getPaymentTerms().trim().equals("")) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, "R12",
                        currentJob.getJobCostingAndPayment().getPaymentTerms(),
                        "java.lang.String", dataCellStyle);
            } else {
                BusinessEntityUtils.setExcelCellValue(
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
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "AL12",
                    currentJob.getJobCostingAndPayment().getLastPaymentEnteredBy().getName(),
                    "java.lang.String", dataCellStyle);

            // CLIENT NAME & BILLING ADDRESS
            // Name
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderLeft((short) 1);
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "A20",
                    ativeClient.getName(),
                    "java.lang.String", dataCellStyle);

            // Billing address    
            Address billingAddress;
            if (currentJob.getBillingAddress() == null) {
                billingAddress = ativeClient.getBillingAddress();
            } else {
                billingAddress = currentJob.getBillingAddress();
            }
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "A22",
                    billingAddress.getAddressLine1(),
                    "java.lang.String", dataCellStyle);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "A23",
                    billingAddress.getAddressLine2(),
                    "java.lang.String", dataCellStyle);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "A24",
                    billingAddress.getStateOrProvince(),
                    "java.lang.String", dataCellStyle);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "A25",
                    billingAddress.getCity(),
                    "java.lang.String", dataCellStyle);

            // Contact person
            // Name
            Contact contactPerson;
            if (currentJob.getContact() == null) {
                contactPerson = ativeClient.getMainContact();
            } else {
                contactPerson = currentJob.getContact();
            }
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderLeft((short) 1);
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            BusinessEntityUtils.setExcelCellValue(
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
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "M22",
                    contactPerson.getInternet().getEmail1(),
                    "java.lang.String", dataCellStyle);

            // Phone
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderTop((short) 1);
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "Z20",
                    contactPerson.getMainPhoneNumber(),
                    "java.lang.String", dataCellStyle);

            // Fax
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderBottom((short) 1);
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_BOTTOM);
            BusinessEntityUtils.setExcelCellValue(
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
            BusinessEntityUtils.setExcelCellValue(
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
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, "AN21",
                        "Yes",
                        "java.lang.String", dataCellStyle);
            } else {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, "AN21",
                        "No",
                        "java.lang.String", dataCellStyle);
            }

            // Expedite?
            if (job.getServiceContract().getAdditionalServiceUrgent()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, "AN23",
                        "Yes",
                        "java.lang.String", dataCellStyle);
            } else {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, "AN23",
                        "No",
                        "java.lang.String", dataCellStyle);
            }

            // Purchase Order:        
            if (currentJob.getJobCostingAndPayment().getPurchaseOrderNumber().equals("")) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, "AN25",
                        "Not applicable",
                        "java.lang.String", dataCellStyle);
            } else {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, "AN25",
                        currentJob.getJobCostingAndPayment().getPurchaseOrderNumber(),
                        "java.lang.String", dataCellStyle);
            }

            // CLIENT INSTRUCTION/DETAILS FOR JOB
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderTop((short) 1);
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "M24",
                    currentJob.getInstructions(),
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
                    BusinessEntityUtils.setExcelCellValue(
                            wb, serviceContractSheet, "A" + samplesStartngRow,
                            jobSample.getReference(),
                            "java.lang.String", dataCellStyle);
                    dataCellStyle = getDefaultCellStyle(wb);
                    dataCellStyle.setBorderLeft((short) 1);
                    dataCellStyle.setFont(samplesFont);
                    dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
                    dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
                    dataCellStyle.setWrapText(true);
                    BusinessEntityUtils.setExcelCellValue(
                            wb, serviceContractSheet, "B" + samplesStartngRow,
                            jobSample.getName(),
                            "java.lang.String", dataCellStyle);
                    BusinessEntityUtils.setExcelCellValue(
                            wb, serviceContractSheet, "H" + samplesStartngRow,
                            jobSample.getProductBrand(),
                            "java.lang.String", dataCellStyle);
                    BusinessEntityUtils.setExcelCellValue(
                            wb, serviceContractSheet, "O" + samplesStartngRow,
                            jobSample.getProductModel(),
                            "java.lang.String", dataCellStyle);

                    String productSerialAndCode = jobSample.getProductSerialNumber();
                    if (!jobSample.getProductCode().equals("")) {
                        productSerialAndCode = productSerialAndCode + "/" + jobSample.getProductCode();
                    }
                    BusinessEntityUtils.setExcelCellValue(
                            wb, serviceContractSheet, "W" + samplesStartngRow,
                            productSerialAndCode,
                            "java.lang.String", dataCellStyle);
                    BusinessEntityUtils.setExcelCellValue(
                            wb, serviceContractSheet, "AG" + samplesStartngRow,
                            jobSample.getSampleQuantity(),
                            "java.lang.String", dataCellStyle);
                    BusinessEntityUtils.setExcelCellValue(
                            wb, serviceContractSheet, "AI" + samplesStartngRow,
                            jobSample.getQuantity(),
                            "java.lang.String", dataCellStyle);
                    BusinessEntityUtils.setExcelCellValue(
                            wb, serviceContractSheet, "AI" + samplesStartngRow,
                            jobSample.getQuantity() + " (" + jobSample.getUnitOfMeasure() + ")",
                            "java.lang.String", dataCellStyle);
                    // Disposal
                    if (jobSample.getMethodOfDisposal() == 2) {
                        BusinessEntityUtils.setExcelCellValue(
                                wb, serviceContractSheet, "AO" + samplesStartngRow,
                                "Yes",
                                "java.lang.String", dataCellStyle);
                    } else {
                        BusinessEntityUtils.setExcelCellValue(
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
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, "A" + samplesStartngRow,
                        "",
                        "java.lang.String", dataCellStyle);
                dataCellStyle = getDefaultCellStyle(wb);
                dataCellStyle.setBorderLeft((short) 1);
                dataCellStyle.setFont(samplesFont);
                dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
                dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
                dataCellStyle.setWrapText(true);
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, "B" + samplesStartngRow,
                        "Not applicable",
                        "java.lang.String", dataCellStyle);
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, "H" + samplesStartngRow,
                        "Not applicable",
                        "java.lang.String", dataCellStyle);
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, "O" + samplesStartngRow,
                        "Not applicable",
                        "java.lang.String", dataCellStyle);
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, "W" + samplesStartngRow,
                        "Not applicable",
                        "java.lang.String", dataCellStyle);
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, "AG" + samplesStartngRow,
                        "",
                        "java.lang.String", dataCellStyle);
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, "AI" + samplesStartngRow,
                        "",
                        "java.lang.String", dataCellStyle);
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, "AI" + samplesStartngRow,
                        "",
                        "java.lang.String", dataCellStyle);
                // Disposal                   
                BusinessEntityUtils.setExcelCellValue(
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
            BusinessEntityUtils.setExcelCellValue(
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
        Report report = Report.findReportById(em, getReport().getId());
        em.refresh(report);

        return report;
    }

    public StreamedContent getJobEnteredByReportPDFFile() {

        EntityManager em = getEntityManager1();
        HashMap parameters = new HashMap();

        try {

            reportEmployee = Employee.findEmployeeByName(em, getReportEmployee().getName());

            if (getReportEmployee().getId() != null) {

                report = getLatestJobReport(em);
                String reportFileURL = report.getReportFile();

                Connection con = BusinessEntityUtils.establishConnection(report.getDatabaseDriverClass(),
                        report.getDatabaseURL(),
                        report.getDatabaseUsername(),
                        report.getDatabasePassword());
                if (con != null) {
                    StreamedContent streamContent;

                    parameters.put("startOFPeriod", reportSearchParameters.getDatePeriod().getStartDate());
                    parameters.put("endOFPeriod", reportSearchParameters.getDatePeriod().getEndDate());
                    parameters.put("inspectorID", getReportEmployee().getId());

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

    public StreamedContent getJobEnteredByDepartmentReportPDFFile() {

        EntityManager em = getEntityManager1();
        HashMap parameters = new HashMap();

        try {

            reportingDepartment = Department.findDepartmentByName(em, getReportingDepartment().getName());

            if (getReportingDepartment().getId() != null) {

                report = getLatestJobReport(em);
                String reportFileURL = report.getReportFile();

                Connection con = BusinessEntityUtils.establishConnection(report.getDatabaseDriverClass(),
                        report.getDatabaseURL(),
                        report.getDatabaseUsername(),
                        report.getDatabasePassword());
                if (con != null) {
                    StreamedContent streamContent;

                    parameters.put("startOFPeriod", reportSearchParameters.getDatePeriod().getStartDate());
                    parameters.put("endOFPeriod", reportSearchParameters.getDatePeriod().getEndDate());
                    parameters.put("departmentID", getReportingDepartment().getId());

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

    public StreamedContent getJobAssignedToDepartmentReportXLSFile() {

        EntityManager em = getEntityManager1();
        HashMap parameters = new HashMap();

        try {

            reportingDepartment = Department.findDepartmentByName(em, getReportingDepartment().getName()); //getEmployeeByName(em, getReportEmployee().getName());

            // Use user's department if none found
            if (getReportingDepartment().getId() == null) {
                reportingDepartment = getUser().getEmployee().getDepartment();
            }

            report = getLatestJobReport(em);
            String reportFileURL = report.getReportFile();

            Connection con = BusinessEntityUtils.establishConnection(report.getDatabaseDriverClass(),
                    report.getDatabaseURL(),
                    report.getDatabaseUsername(),
                    report.getDatabasePassword());
            if (con != null) {
                StreamedContent streamContent;

                parameters.put("startOFPeriod", reportSearchParameters.getDatePeriod().getStartDate());
                parameters.put("endOFPeriod", reportSearchParameters.getDatePeriod().getEndDate());
                parameters.put("departmentID", getReportingDepartment().getId());
                parameters.put("departmentName", getReportingDepartment().getName());
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
}
