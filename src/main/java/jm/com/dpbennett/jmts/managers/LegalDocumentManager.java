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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import jm.com.dpbennett.business.entity.Classification;
import jm.com.dpbennett.business.entity.DatePeriod;
import jm.com.dpbennett.business.entity.Department;
import jm.com.dpbennett.business.entity.DocumentReport;
import jm.com.dpbennett.business.entity.DocumentSequenceNumber;
import jm.com.dpbennett.business.entity.DocumentType;
import jm.com.dpbennett.business.entity.Employee;
import jm.com.dpbennett.business.entity.JobManagerUser;
import jm.com.dpbennett.business.entity.LegalDocument;
import jm.com.dpbennett.business.entity.utils.BusinessEntityUtils;
import jm.com.dpbennett.jmts.Application;
import jm.com.dpbennett.jmts.utils.MainTabView;
import jm.com.dpbennett.jmts.utils.PrimeFacesUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.hssf.util.HSSFColor;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author Desmond Bennett
 */
@ManagedBean
@SessionScoped
public class LegalDocumentManager implements Serializable {

    @PersistenceUnit(unitName = "JMTSPU")
    private EntityManagerFactory EMF;
    private String dateSearchField;
    private DatePeriod datePeriod;
    private String searchType;
    private String searchText;
    private List<LegalDocument> documentSearchResultList;
    private LegalDocument selectedDocument;
    private LegalDocument currentDocument;
    private DocumentReport documentReport;
    private JobManager jobManager;
    private ClientManager clientManager;
    private SystemManager systemManager;
    private ReportManager reportManager;

    public LegalDocumentManager() {
        init();
    }

    private void init() {
        searchType = "General";
        dateSearchField = "dateReceived";
        datePeriod = new DatePeriod("This month", "month", null, null, null, null, false, false, false);
        datePeriod.initDatePeriod();
    }

    public void openReportsTab() {
        getReportManager().openReportsTab("Legal");
    }

    public void reset() {
        init();
    }

    public List<Classification> completeClassification(String query) {
        EntityManager em = getEntityManager();

        try {

            List<Classification> classifications = Classification.findActiveClassificationsByNameAndCategory(em, query, "Legal");

            return classifications;
        } catch (Exception e) {

            System.out.println(e);
            return new ArrayList<>();
        }
    }

    public Boolean getIsClientNameValid() {
        return BusinessEntityUtils.validateName(getCurrentDocument().getExternalClient().getName());
    }

    public void editExternalClient() {

        getClientManager().setSelectedClient(getCurrentDocument().getExternalClient());
        getClientManager().setIsClientNameAndIdEditable(getUser().getPrivilege().getCanAddClient());

        PrimeFacesUtils.openDialog(null, "/client/clientDialog", true, true, true, 450, 700);
    }

    public void externalClientDialogReturn() {
        if (getClientManager().getSelectedClient().getId() != null) {
            getCurrentDocument().setExternalClient(getClientManager().getSelectedClient());
        }
    }

    public void classificationDialogReturn() {
        if (getSystemManager().getSelectedClassification().getId() != null) {
            getCurrentDocument().setClassification(getSystemManager().getSelectedClassification());
        }
    }

    public void documentTypeDialogReturn() {
        if (getSystemManager().getSelectedDocumentType().getId() != null) {
            getCurrentDocument().setType(getSystemManager().getSelectedDocumentType());
            updateDocument();
        }
    }

    public void documentDialogReturn() {
        if (getCurrentDocument().getIsDirty()) {
            PrimeFacesUtils.addMessage("Document NOT Saved!", "", FacesMessage.SEVERITY_WARN);
        }
    }

    public void createNewExternalClient() {
        getClientManager().createNewClient(true);
        getClientManager().setIsClientNameAndIdEditable(getUser().getPrivilege().getCanAddClient());

        PrimeFacesUtils.openDialog(null, "/client/clientDialog", true, true, true, 450, 700);
    }

    public DatePeriod getDatePeriod() {
        return datePeriod;
    }

    public void setDatePeriod(DatePeriod datePeriod) {
        this.datePeriod = datePeriod;
    }

    public DocumentReport getDocumentReport() {
        if (documentReport == null) {
            documentReport = new DocumentReport();
        }
        return documentReport;
    }

    public void setDocumentReport(DocumentReport documentReport) {
        this.documentReport = documentReport;
    }

    public void handleStartSearchDateSelect(SelectEvent event) {
        doLegalDocumentSearch();
    }

    public void handleEndSearchDateSelect(SelectEvent event) {
        doLegalDocumentSearch();
    }

    public int getNumberOfDocumentsFound() {
        if (documentSearchResultList != null) {
            return documentSearchResultList.size();
        } else {
            return 0;
        }
    }

    public void deleteDocument() {
        EntityManager em = getEntityManager();

        em.getTransaction().begin();
        LegalDocument document = em.find(LegalDocument.class, selectedDocument.getId());
        em.remove(document);
        em.flush();
        em.getTransaction().commit();

        // Do search to update search list.
        doLegalDocumentSearch();
    }

    public void cancelDocumentEdit(ActionEvent actionEvent) {
        PrimeFaces.current().dialog().closeDynamic(null);
    }

    public void editDocument() {
        getCurrentDocument().setIsDirty(false);

        PrimeFacesUtils.openDialog(null, "/legal/legalDocumentDialog", true, true, true, true, 600, 700);
    }

    public void editDocumentType(ActionEvent actionEvent) {

        getSystemManager().setSelectedDocumentType(getCurrentDocument().getType());
        getSystemManager().openDocumentTypeDialog("/admin/documentTypeDialog");
    }

    public void editClassification(ActionEvent actionEvent) {
        getSystemManager().setSelectedClassification(getCurrentDocument().getClassification());

        PrimeFacesUtils.openDialog(null, "/admin/classificationDialog", true, true, true, 325, 600);
    }

    public void createNewClassification(ActionEvent actionEvent) {
        getSystemManager().setSelectedClassification(new Classification());
        getSystemManager().getSelectedClassification().setCategory("Legal");
        getSystemManager().getSelectedClassification().setIsEarning(false);

        PrimeFacesUtils.openDialog(null, "/admin/classificationDialog", true, true, true, 325, 600);
    }

    public void createNewDocumentType(ActionEvent actionEvent) {
        getSystemManager().setSelectedDocumentType(new DocumentType());

        PrimeFacesUtils.openDialog(null, "/admin/documentTypeDialog", true, true, true, 275, 400);
    }

    public void saveCurrentLegalDocument(ActionEvent actionEvent) {

        if (currentDocument.getIsDirty()) {
            EntityManager em = getEntityManager();

            try {
                if (DocumentSequenceNumber.findDocumentSequenceNumber(em,
                        currentDocument.getSequenceNumber(),
                        currentDocument.getYearReceived(),
                        currentDocument.getMonthReceived(),
                        currentDocument.getType().getId()) == null) {

                    currentDocument.setSequenceNumber(DocumentSequenceNumber.findNextDocumentSequenceNumber(em,
                            currentDocument.getYearReceived(),
                            currentDocument.getMonthReceived(),
                            currentDocument.getType().getId()));
                }

                if (currentDocument.getAutoGenerateNumber()) {
                    currentDocument.setNumber(LegalDocument.getLegalDocumentNumber(currentDocument, "ED"));
                }

                // Do save, set clean and dismiss dialog
                currentDocument.setEditedBy(getUser().getEmployee());
                if (currentDocument.save(em).isSuccess()) {
                    currentDocument.setIsDirty(false);
                }

                PrimeFaces.current().dialog().closeDynamic(null);

                // Redo search
                doLegalDocumentSearch();

            } catch (Exception e) {
                System.out.println(e);
            }
        } else {
            PrimeFaces.current().dialog().closeDynamic(null);
        }
    }

    public void updateDocument() {

        if (currentDocument.getAutoGenerateNumber()) {
            currentDocument.setNumber(LegalDocument.getLegalDocumentNumber(currentDocument, "ED"));
        }

        getCurrentDocument().setIsDirty(true);
    }

    public void updateDateReceived() {
        Calendar c = Calendar.getInstance();

        c.setTime(currentDocument.getDateReceived());
        currentDocument.setMonthReceived(c.get(Calendar.MONTH));
        currentDocument.setYearReceived(c.get(Calendar.YEAR));

        updateDocument();
    }

    public void updateDepartmentResponsible() {
        if (currentDocument.getResponsibleDepartment().getId() != null) {
            currentDocument.setResponsibleDepartment(Department.findDepartmentById(getEntityManager(),
                    currentDocument.getResponsibleDepartment().getId()));
            if (currentDocument.getAutoGenerateNumber()) {
                currentDocument.setNumber(LegalDocument.getLegalDocumentNumber(currentDocument, "ED"));
            }
        }
    }

    public void updateDocumentReport() {
        if (documentReport.getId() != null) {
            documentReport = DocumentReport.findDocumentReportById(getEntityManager(), documentReport.getId());
            doLegalDocumentSearch();
        }
    }

    public void createNewLegalDocument(ActionEvent action) {
        currentDocument = createNewLegalDocument(getEntityManager(), getUser());
        editDocument();
    }

    public void openDocumentBrowser() {
        // Add the Job Browser tab is 
        getMainTabView().addTab(getEntityManager(), "Document Browser", true);
        getMainTabView().select("Document Browser");
    }

    public MainTabView getMainTabView() {
        JobManager jm = Application.findBean("jobManager");

        return jm.getMainTabView();
    }

    public JobManagerUser getUser() {
        return getJobManager().getUser();
    }

    public LegalDocument createNewLegalDocument(EntityManager em,
            JobManagerUser user) {

        LegalDocument legalDocument = new LegalDocument();
        legalDocument.setAutoGenerateNumber(Boolean.TRUE);

        if (getUser().getId() != null) {
            if (getUser().getEmployee() != null) {
                legalDocument.setResponsibleOfficer(Employee.findEmployeeById(em, getUser().getEmployee().getId()));
                legalDocument.setResponsibleDepartment(Department.findDepartmentById(em, getUser().getEmployee().getDepartment().getId()));
            }
        } else {
            legalDocument.setResponsibleOfficer(Employee.findDefaultEmployee(getEntityManager(), "--", "--", true));
            legalDocument.setResponsibleDepartment(Department.findDefaultDepartment(em, "--"));
        }

        legalDocument.setRequestingDepartment(Department.findDefaultDepartment(em, "--"));
        legalDocument.setType(DocumentType.findDocumentTypeByName(em, "--"));
        legalDocument.setClassification(Classification.findClassificationByName(em, "--"));
        legalDocument.setDocumentForm("H");
        legalDocument.setNumber(LegalDocument.getLegalDocumentNumber(legalDocument, "ED"));
        legalDocument.setDateReceived(new Date());

        return legalDocument;
    }

    public LegalDocument getCurrentDocument() {
        if (currentDocument == null) {
            currentDocument = createNewLegalDocument(getEntityManager(), getUser());
        }

        return currentDocument;
    }

    public void setTargetDocument(LegalDocument legalDocument) {
        currentDocument = legalDocument;
    }

    public void setCurrentDocument(LegalDocument currentDocument) {
        this.currentDocument = currentDocument;

        this.currentDocument.setVisited(true);
    }

    public LegalDocument getSelectedDocument() {
        return selectedDocument;
    }

    public void setSelectedDocument(LegalDocument selectedDocument) {
        this.selectedDocument = selectedDocument;
    }

    public List<LegalDocument> getDocumentSearchResultList() {
        return documentSearchResultList;
    }

    public List<LegalDocument> getDocumentSearchByTypeResultList() {
        EntityManager em = getEntityManager();

        if (selectedDocument != null) {
            return LegalDocument.findLegalDocumentsByDateSearchField(em,
                    dateSearchField, "By type", selectedDocument.getType().getName(), // tk get from main search
                    getDatePeriod().getStartDate(), getDatePeriod().getEndDate());
        } else {
            return new ArrayList<>();
        }
    }

    public void setDocumentSearchResultList(List<LegalDocument> documentSearchResultList) {
        this.documentSearchResultList = documentSearchResultList;
    }

    public String getDateSearchField() {
        return dateSearchField;
    }

    public void setDateSearchField(String dateSearchField) {
        this.dateSearchField = dateSearchField;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    public void updateSearch() {
        switch (searchType) {
            case "General":
                doLegalDocumentSearch();
                break;
            default:
                doLegalDocumentSearch();
                break;
        }
    }

    public void updateDatePeriodSearch() {
        getDatePeriod().initDatePeriod();

        doLegalDocumentSearch();
    }

    public void doLegalDocumentSearch() {

        EntityManager em = getEntityManager();

        if (searchText != null) {
            documentSearchResultList = LegalDocument.findLegalDocumentsByDateSearchField(em,
                    dateSearchField, searchType, searchText.trim(),
                    getDatePeriod().getStartDate(), getDatePeriod().getEndDate());
        } else { // get all documents based on common test ie "" for now
            documentSearchResultList = LegalDocument.findLegalDocumentsByDateSearchField(em,
                    dateSearchField, searchType, "",
                    getDatePeriod().getStartDate(), getDatePeriod().getEndDate());
        }

        openDocumentBrowser();
    }

    public JobManager getJobManager() {
        if (jobManager == null) {
            jobManager = Application.findBean("jobManager");
        }
        return jobManager;
    }

    public SystemManager getSystemManager() {
        if (systemManager == null) {
            systemManager = Application.findBean("systemManager");
        }
        return systemManager;
    }

    public ClientManager getClientManager() {
        if (clientManager == null) {
            clientManager = Application.findBean("clientManager");
        }

        return clientManager;
    }

    public ReportManager getReportManager() {
        if (reportManager == null) {
            reportManager = Application.findBean("reportManager");
        }

        return reportManager;
    }

    private EntityManager getEntityManager() {
        return EMF.createEntityManager();
    }

    public void formatDocumentTableXLS(Object document, String headerTitle) {
        HSSFWorkbook wb = (HSSFWorkbook) document;
        HSSFSheet sheet = wb.getSheetAt(0);
        // get columns row
        int numCols = sheet.getRow(0).getPhysicalNumberOfCells();
        // create heading row
        sheet.shiftRows(0, sheet.getLastRowNum(), 1);

        HSSFRow header = sheet.getRow(0);
        HSSFFont headerFont = wb.createFont();
        headerFont.setFontHeightInPoints((short) 12);
        headerFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        HSSFCellStyle headerCellStyle = wb.createCellStyle();
        headerCellStyle.setFont(headerFont);
        headerCellStyle.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
        headerCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        headerCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        for (int i = 0; i < numCols; i++) {
            header.createCell(i);
            HSSFCell cell = header.getCell(i);
            cell.setCellStyle(headerCellStyle);
        }
        header.getCell(0).setCellValue(headerTitle);
        // merge header cells
        sheet.addMergedRegion(new CellRangeAddress(
                0, //first row
                (short) 0, //last row
                0, //first column
                (short) (numCols - 1) //last column
        ));

        // Column setup
        // get columns row
        HSSFRow cols = sheet.getRow(1);
        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setFillForegroundColor(HSSFColor.YELLOW.index);
        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        // set columns widths
        for (int i = 0; i < cols.getPhysicalNumberOfCells(); i++) {

            sheet.autoSizeColumn(i);

            if (sheet.getColumnWidth(i) > 15000) {
                sheet.setColumnWidth(i, 15000);
            }

        }
        // set columns cell style
        for (int i = 0; i < cols.getPhysicalNumberOfCells(); i++) {
            HSSFCell cell = cols.getCell(i);
            cell.setCellStyle(cellStyle);
        }
    }

    public void postProcessDocumentTableXLS(Object document) {
        formatDocumentTableXLS(document, "Document by group");
    }

    public void postProcessXLS(Object document) {
        formatDocumentTableXLS(document, documentReport.getName());
    }

    public List<String> completeGoal(String query) {
        // tk put in sys options
        String goals[] = {"# 1", "# 2", "# 3", "# 4", "# 5"};
        List<String> matchedGoals = new ArrayList<>();

        for (String goal : goals) {
            if (goal.contains(query)) {
                matchedGoals.add(goal);
            }
        }

        return matchedGoals;

    }
    
    public List<String> completeStrategicPriority(String query) {
        // tk put in sys options
        String priorities[] = {"1", "2", "3"};
        List<String> matchedPriority = new ArrayList<>();

        for (String priority : priorities) {
            if (priority.contains(query)) {
                matchedPriority.add(priority);
            }
        }

        return matchedPriority;

    }
}
