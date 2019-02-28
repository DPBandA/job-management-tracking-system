/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.com.dpbennett.jmts.managers;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import jm.com.dpbennett.business.entity.Client;
import jm.com.dpbennett.business.entity.CompanyRegistration;
import jm.com.dpbennett.business.entity.ComplianceDailyReport;
import jm.com.dpbennett.business.entity.ComplianceSurvey;
import jm.com.dpbennett.business.entity.Contact;
import jm.com.dpbennett.business.entity.Country;
import jm.com.dpbennett.business.entity.DatePeriod;
import jm.com.dpbennett.business.entity.Distributor;
import jm.com.dpbennett.business.entity.DocumentInspection;
import jm.com.dpbennett.business.entity.Manufacturer;
import jm.com.dpbennett.business.entity.ProductInspection;
import jm.com.dpbennett.business.entity.SampleRequest;
import jm.com.dpbennett.business.entity.SequenceNumber;
import jm.com.dpbennett.business.entity.ShippingContainer;
import jm.com.dpbennett.business.entity.SystemOption;
import jm.com.dpbennett.business.entity.management.UserManagement;
import jm.com.dpbennett.business.entity.utils.BusinessEntityUtils;
import jm.com.dpbennett.wal.managers.ReportManager;
import jm.com.dpbennett.wal.utils.BeanUtils;
import jm.com.dpbennett.wal.utils.PrimeFacesUtils;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author Desmond Bennett
 */
@ManagedBean
@SessionScoped
public class ComplianceManager implements Serializable {

    @PersistenceUnit(unitName = "JMTSPU")
    private EntityManagerFactory EMF1;
    private EntityManager entityManager1;
    private ComplianceSurvey currentComplianceSurvey;
    private ProductInspection currentProductInspection;
    private SampleRequest currentSampleRequest;
    private CompanyRegistration currentCompanyRegistration;
    private UserManagement userManagement;
    private Boolean isNewProductInspection = false;
    private Boolean isNewComplianceSurvey = false;
    private Boolean isNewDocumentInspection = false;
    private List<ComplianceSurvey> complianceSurveys;
    private Date reportStartDate;
    private Date reportEndDate;
    private String searchText;
    private String reportSearchText;
    private String dateSearchField;
    private String dateSearchPeriod;
    private String reportPeriod;
    private String searchType;
    private String activeTabTitle;
    private String dialogMessage;
    private String dialogMessageHeader;
    private String dialogMessageSeverity;
    private Integer longProcessProgress = 0;
    private ComplianceDailyReport currentComplianceDailyReport;
    private ShippingContainer currentShippingContainer;
    private DocumentInspection currentDocumentInspection;
    private List<DocumentInspection> documentInspections;
    private DatePeriod datePeriod;
    // Managers
    private ClientManager clientManager;
    //private JobManager jobManager;
    private ReportManager reportManager;

    /**
     * Creates a new instance of ComplianceManager.
     */
    public ComplianceManager() {
        complianceSurveys = new ArrayList<>();
        documentInspections = new ArrayList<>();
        dateSearchField = "dateFirstReceived";
        searchText = "";
        searchType = "General";
        dateSearchPeriod = "This month";
        reportPeriod = "This month";
        activeTabTitle = "Compliance Survey";
        currentComplianceDailyReport
                = new ComplianceDailyReport("Report-" + new Date().toString(),
                        new Date(), "Berth 11", " ");
        datePeriod = new DatePeriod("This month", "month", null, null, null, null, false, false, false);
        datePeriod.initDatePeriod();
    }

    public List<Manufacturer> completeManufacturer(String query) {
        return Manufacturer.findManufacturersBySearchPattern(getEntityManager1(), query);
    }

    public ReportManager getReportManager() {
        if (reportManager == null) {
            reportManager = BeanUtils.findBean("reportManager");
        }

        return reportManager;
    }

    public void openReportsTab() {
        getReportManager().openReportsTab("Job");
    }

    public void updateCconsignee() {
        currentComplianceSurvey.setConsigneeRepresentative(new Contact());
        currentComplianceSurvey.setIsDirty(true);
    }

    public Boolean getIsConsigneeNameValid() {
        return BusinessEntityUtils.validateName(currentComplianceSurvey.getConsignee().getName());
    }

//    public void editConsignee() {
//        getClientManager().setSelectedClient(currentComplianceSurvey.getConsignee());
//        getClientManager().setIsClientNameAndIdEditable(getUser().getPrivilege().getCanAddClient());
//
//        PrimeFacesUtils.openDialog(null, "/client/clientDialog", true, true, true, 450, 700);
//    }

    public void consigneeDialogReturn() {
        if (clientManager.getSelectedClient().getId() != null) {
            currentComplianceSurvey.setConsignee(clientManager.getSelectedClient());
        }
    }

//    public void createNewConsignee() {
//        clientManager.createNewClient(true);
//        clientManager.setIsClientNameAndIdEditable(getUser().getPrivilege().getCanAddClient());
//
//        PrimeFacesUtils.openDialog(null, "/client/clientDialog", true, true, true, 450, 700);
//    }

    public List<Contact> completeConsigneetContact(String query) {
        List<Contact> contacts = new ArrayList<>();

        try {

            for (Contact contact : getCurrentComplianceSurvey().getConsignee().getContacts()) {
                if (contact.toString().toUpperCase().contains(query.toUpperCase())) {
                    contacts.add(contact);
                }
            }

            return contacts;
        } catch (Exception e) {
            System.out.println(e);
            return new ArrayList<>();
        }
    }

    public void openComplianceSurvey() {
        // tk set dirty false here

        PrimeFacesUtils.openDialog(null, "/compliance/surveyDialog", true, true, true, true, 600, 700);
    }

//    public void openSurveyBrowser() {
//        // Add the Job Browser tab is 
//        getJobManager().getMainTabView().addTab(getEntityManager1(), "Survey Browser", true);
//        getJobManager().getMainTabView().select("Survey Browser");
//    }
//
//    public JobManagerUser getUser() {
//        return getJobManager().getUser();
//    }

//    public JobManager getJobManager() {
//        if (jobManager == null) {
//            jobManager = BeanUtils.findBean("jobManager");
//        }
//        return jobManager;
//    }

    public ClientManager getClientManager() {
        if (clientManager == null) {
            clientManager = BeanUtils.findBean("clientManager");
        }
        return clientManager;
    }

    public void surveyDialogReturn() {
//        if (currentJob.getIsDirty()) {
//            PrimeFacesUtils.addMessage("Job was NOT saved", "The recently edited job was not saved", FacesMessage.SEVERITY_WARN);
//            PrimeFaces.current().ajax().update("headerForm:growl3");
//            currentJob.setIsDirty(false);
//        }
    }

    public void updateDatePeriodSearch() {
        getDatePeriod().initDatePeriod();

    }

    public DatePeriod getDatePeriod() {
        return datePeriod;
    }

    public void setDatePeriod(DatePeriod datePeriod) {
        this.datePeriod = datePeriod;
    }

    public List getComplianceSearchTypes() {
        ArrayList searchTypes = new ArrayList();

        searchTypes.add(new SelectItem("General", "General"));

        return searchTypes;
    }

    public List getComplianceDateSearchFields() {
        ArrayList dateFields = new ArrayList();

        // add items
        dateFields.add(new SelectItem("dateOfSurvey", "Date of survey"));

        return dateFields;
    }

    public void updateSearch() {
    }

    public List<DocumentInspection> getDocumentInspections() {
        return documentInspections;
    }

    public DocumentInspection getCurrentDocumentInspection() {
        if (currentDocumentInspection == null) {
            currentDocumentInspection = new DocumentInspection();
        }
        return currentDocumentInspection;
    }

    public void setCurrentDocumentInspection(DocumentInspection currentDocumentInspection) {
        this.currentDocumentInspection = currentDocumentInspection;
    }

    public ShippingContainer getCurrentShippingContainer() {
        if (currentShippingContainer == null) {
            currentShippingContainer = new ShippingContainer();
        }
        return currentShippingContainer;
    }

    public void setCurrentShippingContainer(ShippingContainer currentShippingContainer) {
        this.currentShippingContainer = currentShippingContainer;
    }

    public StreamedContent getAuthSigForDetentionRequestPOE() {
        if (currentComplianceSurvey.getAuthSigForDetentionRequestPOE().getId() != null) {
            if (currentComplianceSurvey.getAuthSigForDetentionRequestPOE().getSignatureImage() != null) {
                return new DefaultStreamedContent(new ByteArrayInputStream(currentComplianceSurvey.getAuthSigForDetentionRequestPOE().getSignatureImage()), "image/png");
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public StreamedContent getInspectorSigForSampleRequestPOE() {
        if (currentComplianceSurvey.getInspectorSigForSampleRequestPOE().getId() != null) {
            if (currentComplianceSurvey.getInspectorSigForSampleRequestPOE().getSignatureImage() != null) {
                return new DefaultStreamedContent(new ByteArrayInputStream(currentComplianceSurvey.getInspectorSigForSampleRequestPOE().getSignatureImage()), "image/png");
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public StreamedContent getPreparedBySigForReleaseRequestPOE() {
        if (currentComplianceSurvey.getPreparedBySigForReleaseRequestPOE().getId() != null) {
            if (currentComplianceSurvey.getPreparedBySigForReleaseRequestPOE().getSignatureImage() != null) {
                return new DefaultStreamedContent(new ByteArrayInputStream(currentComplianceSurvey.getPreparedBySigForReleaseRequestPOE().getSignatureImage()), "image/png");
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public StreamedContent getAuthSigForNoticeOfDentionDM() {
        if (currentComplianceSurvey.getAuthSigForNoticeOfDentionDM().getId() != null) {
            if (currentComplianceSurvey.getAuthSigForNoticeOfDentionDM().getSignatureImage() != null) {
                return new DefaultStreamedContent(new ByteArrayInputStream(currentComplianceSurvey.getAuthSigForNoticeOfDentionDM().getSignatureImage()), "image/png");
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public StreamedContent getApprovedBySigForReleaseRequestPOE() {
        if (currentComplianceSurvey.getApprovedBySigForReleaseRequestPOE().getId() != null) {
            if (currentComplianceSurvey.getApprovedBySigForReleaseRequestPOE().getSignatureImage() != null) {
                return new DefaultStreamedContent(new ByteArrayInputStream(currentComplianceSurvey.getApprovedBySigForReleaseRequestPOE().getSignatureImage()), "image/png");
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public void updateAuthDetentionRequestPOE() {

        if (currentComplianceSurvey.getAuthSigForDetentionRequestPOE().getId() == null) {
            currentComplianceSurvey.setAuthSigDateForDetentionRequestPOE(new Date());
            currentComplianceSurvey.setAuthSigForDetentionRequestPOE(getUserManagement().getUser().getEmployee().getSignature());
        } else {
            currentComplianceSurvey.setAuthSigDateForDetentionRequestPOE(null);
            currentComplianceSurvey.setAuthSigForDetentionRequestPOE(null);
        }

    }

    public void updateInspectorSigForSampleRequestPOE() {

        if (currentComplianceSurvey.getInspectorSigForSampleRequestPOE().getId() == null) {
            currentComplianceSurvey.setInspectorSigDateForSampleRequestPOE(new Date());
            currentComplianceSurvey.setInspectorSigForSampleRequestPOE(getUserManagement().getUser().getEmployee().getSignature());
        } else {
            currentComplianceSurvey.setInspectorSigDateForSampleRequestPOE(null);
            currentComplianceSurvey.setInspectorSigForSampleRequestPOE(null);
        }

    }

    public void updatePreparedBySigForReleaseRequestPOE() {

        if (currentComplianceSurvey.getPreparedBySigForReleaseRequestPOE().getId() == null) {
            currentComplianceSurvey.setPreparedBySigDateForReleaseRequestPOE(new Date());
            currentComplianceSurvey.setPreparedBySigForReleaseRequestPOE(getUserManagement().getUser().getEmployee().getSignature());
            currentComplianceSurvey.setPreparedByEmployeeForReleaseRequestPOE(getUserManagement().getUser().getEmployee());
        } else {
            currentComplianceSurvey.setPreparedBySigDateForReleaseRequestPOE(null);
            currentComplianceSurvey.setPreparedBySigForReleaseRequestPOE(null);
            currentComplianceSurvey.setPreparedByEmployeeForReleaseRequestPOE(null);
        }

    }

    public void updateAuthSigForNoticeOfDentionDM() {

        if (currentComplianceSurvey.getAuthSigForNoticeOfDentionDM().getId() == null) {
            currentComplianceSurvey.setAuthSigDateForNoticeOfDentionDM(new Date());
            currentComplianceSurvey.setAuthSigForNoticeOfDentionDM(getUserManagement().getUser().getEmployee().getSignature());
        } else {
            currentComplianceSurvey.setAuthSigDateForNoticeOfDentionDM(null);
            currentComplianceSurvey.setAuthSigForNoticeOfDentionDM(null);
        }

    }

    public void updateApprovedBySigForReleaseRequestPOE() {

        if (currentComplianceSurvey.getApprovedBySigForReleaseRequestPOE().getId() == null) {
            currentComplianceSurvey.setApprovedBySigDateForReleaseRequestPOE(new Date());
            currentComplianceSurvey.setApprovedBySigForReleaseRequestPOE(getUserManagement().getUser().getEmployee().getSignature());
            currentComplianceSurvey.setApprovedByEmployeeForReleaseRequestPOE(getUserManagement().getUser().getEmployee());
        } else {
            currentComplianceSurvey.setApprovedBySigDateForReleaseRequestPOE(null);
            currentComplianceSurvey.setApprovedBySigForReleaseRequestPOE(null);
            currentComplianceSurvey.setApprovedByEmployeeForReleaseRequestPOE(null);
        }

    }

    public UserManagement getUserManagement() {
        return userManagement;
    }

    public void setUserManagement(UserManagement userManagement) {
        this.userManagement = userManagement;
    }

    public ComplianceDailyReport getCurrentComplianceDailyReport() {
        return currentComplianceDailyReport;
    }

    public void setCurrentComplianceDailyReport(ComplianceDailyReport currentComplianceDailyReport) {
        this.currentComplianceDailyReport = currentComplianceDailyReport;
    }

    public List<ComplianceSurvey> getComplianceSurveys() {
        return complianceSurveys;
    }

    public String getDialogMessage() {
        return dialogMessage;
    }

    public void setDialogMessage(String dialogMessage) {
        this.dialogMessage = dialogMessage;
    }

    public String getDialogMessageHeader() {
        return dialogMessageHeader;
    }

    public void setDialogMessageHeader(String dialogMessageHeader) {
        this.dialogMessageHeader = dialogMessageHeader;
    }

    public String getDialogMessageSeverity() {
        return dialogMessageSeverity;
    }

    public void setDialogMessageSeverity(String dialogMessageSeverity) {
        this.dialogMessageSeverity = dialogMessageSeverity;
    }

    public CompanyRegistration getCurrentCompanyRegistration() {
        if (currentCompanyRegistration == null) {
            currentCompanyRegistration = new CompanyRegistration();
        }
        return currentCompanyRegistration;
    }

    private EntityManagerFactory getEMF1() {
        return EMF1;
    }

    // tk put in business entity utils?
    public EntityManager getEntityManager1() {

        entityManager1 = getEMF1().createEntityManager();

        return entityManager1;
    }

    public ProductInspection getCurrentProductInspection() {
        if (currentProductInspection == null) {
            currentProductInspection = new ProductInspection();
        }
        return currentProductInspection;
    }

    public void setCurrentProductInspection(ProductInspection currentProductInspection) {

        this.currentProductInspection = currentProductInspection;
    }

    public List<String> completeManufacturerName(String query) {
        try {
            EntityManager em = getEntityManager1();

            List<String> names = new ArrayList<String>();
            List<Manufacturer> manufacturers = Manufacturer.findManufacturersBySearchPattern(em, query);
            for (Manufacturer manufacturer : manufacturers) {
                names.add(manufacturer.getName());
            }

            return names;

        } catch (Exception e) {
            System.out.println(e);

            return new ArrayList<String>();
        }
    }

    public List<String> completeDistributorName(String query) {
        try {
            EntityManager em = getEntityManager1();

            List<String> names = new ArrayList<String>();
            List<Distributor> distributors = Distributor.findDistributorsBySearchPattern(em, query);
            for (Distributor distributor : distributors) {
                names.add(distributor.getName());
            }

            return names;

        } catch (Exception e) {
            System.out.println(e);

            return new ArrayList<String>();
        }
    }

    public List<Contact> completeRetailOutletRepresentative(String query) {
        ArrayList<Contact> contactsFound = new ArrayList<Contact>();

        for (Contact contact : currentComplianceSurvey.getRetailOutlet().getContacts()) {
            if (contact.getFirstName().toUpperCase().contains(query.trim().toUpperCase())
                    || contact.getLastName().toUpperCase().contains(query.trim().toUpperCase())) {
                contactsFound.add(contact);
            }
        }

        return contactsFound;
    }

    // Consignee update tk can remove this similar methods like it
    public void updateComplianceSurveyConsignee() {

        try {
            EntityManager em = getEntityManager1();

            updateComplianceSurveyConsignee(em);

        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public void updateDocumentInspectionConsignee() {

    }

    public void updateComplianceSurveyConsignee(EntityManager em) {

    }

    public void updateComplianceSurveyConsigneeRepresentative() {

    }

    public void editComplianceSurveyConsignee() {

    }

    public void editDocumentInspectionConsignee() {
    }

    public void updateComplianceSurveyBroker() {
    }

    public void updateComplianceSurveyBroker(EntityManager em) {
    }

    public void updateComplianceSurveyBrokerRepresentative() {
    }

    public List<String> completeComplianceSurveyBrokerRepresentativeName(String query) {
        ArrayList<String> contactsFound = new ArrayList<String>();

        return contactsFound;

    }

    // tk needed??
    public void createNewMarketProduct() {

    }

    public void editComplianceSurveyBroker() {
    }

    public void updateComplianceSurveyRetailOutlet() {
    }

    public void updateProductManufacturerForProductInsp() {
    }

    public void updateProductDistributorForProductInsp() {
    }

    public void updateComplianceSurveyRetailOutlet(EntityManager em) {
    }

    public void updateComplianceSurveyRetailOutletRepresentative() {
    }

    // tk replace this and others like it with clientManager.completeActiveClient()
    public List<String> completeComplianceSurveyRetailOutletRepresentativeName(String query) {
        ArrayList<String> contactsFound = new ArrayList<>();

        return contactsFound;
    }

    public void editComplianceSurveyRetailOutlet() {
    }

    // tk del if found not necessary
    public void updateJob() {
        //setDirty(true);
    }

    public void updateSurvey() {
        getCurrentComplianceSurvey().setIsDirty(true);
    }

    public void updateEntryDocumentInspection() {
        getCurrentComplianceSurvey().getEntryDocumentInspection().setIsDirty(true);

        updateSurvey();
    }

    public void updateContainerNumber() {
        // create/save shipping containers if needed
        // save current list of containers for later use       
        List<ShippingContainer> currentShippingContainers = new ArrayList<>();
        for (ShippingContainer shippingContainer : getCurrentComplianceSurvey().getEntryDocumentInspection().getShippingContainers()) {
            currentShippingContainers.add(shippingContainer);
        }

        getCurrentComplianceSurvey().getEntryDocumentInspection().getShippingContainers().clear();

        List<String> numList = getCurrentComplianceSurvey().getEntryDocumentInspection().getContainerNumberList();
        for (String containerNum : numList) {
            if (!containerNum.trim().equals("")) {
                ShippingContainer sc = getCurrentShippingContainerByNumber(currentShippingContainers, containerNum);
                if (sc == null) {
                    getCurrentComplianceSurvey().getEntryDocumentInspection().getShippingContainers().add(new ShippingContainer(containerNum));
                } else {
                    getCurrentComplianceSurvey().getEntryDocumentInspection().getShippingContainers().add(sc);
                }
            }
        }

        //setDirty(true);
    }

    public ShippingContainer getCurrentShippingContainerByNumber(List<ShippingContainer> shippingContainers, String number) {
        for (ShippingContainer shippingContainer : shippingContainers) {
            if (shippingContainer.getNumber().trim().equals(number.trim())) {
                return shippingContainer;
            }
        }

        return null;
    }

    public void updateDateOfDetention() {

    }

    public void updateDailyReportStartDate() {
        currentComplianceDailyReport.setEndOfPeriod(currentComplianceDailyReport.getStartOfPeriod());
        //endOfPeriod = startOfPeriod;
        //setDirty(true);
    }

    public void updateCountryOfConsignment() {

    }

    public void updateCompanyTypes() {
        if (!currentComplianceSurvey.getOtherCompanyTypes()) {
            currentComplianceSurvey.setCompanyTypes("");
        }
        //setDirty(true);
    }

    public void createNewProductInspection() {
        currentProductInspection = new ProductInspection();
        currentProductInspection.setQuantity(0);
        currentProductInspection.setSampleSize(0);
        isNewProductInspection = true;
        //setDirty(true);
    }

    public ComplianceSurvey getCurrentComplianceSurvey() {
        //if (currentComplianceSurvey == null) {
        //    createNewComplianceSurvey();
        //}
        return currentComplianceSurvey;
    }

    public void setCurrentComplianceSurvey(ComplianceSurvey currentComplianceSurvey) {
        this.currentComplianceSurvey = currentComplianceSurvey;
    }

    public void createNewComplianceSurvey() {
//        RequestContext context = RequestContext.getCurrentInstance();
//        EntityManager em = getEntityManager1();
//
//        currentComplianceSurvey = new ComplianceSurvey();
//        currentComplianceSurvey.setSurveyType(" ");
//        // consignee and rep
//        currentComplianceSurvey.setConsignee(new Client("", false));
//        currentComplianceSurvey.setConsigneeRepresentative(new Contact(""));
//        // retail outlet and rep
//        currentComplianceSurvey.setRetailOutlet(new Client("", false));
//        currentComplianceSurvey.setRetailRepresentative(new Contact(""));
//        // broker and rep
//        currentComplianceSurvey.setBroker(new Client("", false));
//        currentComplianceSurvey.setBrokerRepresentative(new Contact(""));
//
//        currentComplianceSurvey.getEntryDocumentInspection().setCountryOfConsignment(" ");
//        currentComplianceSurvey.setDateOfSurvey(new Date());
//        if (userManagement.getUser() != null) {
//            currentComplianceSurvey.setInspector(userManagement.getUser().getEmployee());
//        }
//
//        isNewComplianceSurvey = true;
//        setDirty(false);

    }

    public void createNewDocumentInspection() {
        RequestContext context = RequestContext.getCurrentInstance();

        currentDocumentInspection = new DocumentInspection();

        currentDocumentInspection.setName(" ");
        currentComplianceSurvey.setConsignee(new Client("", false));
        currentDocumentInspection.setDateOfInspection(new Date());
        if (userManagement.getUser() != null) {
            currentDocumentInspection.setInspector(userManagement.getUser().getEmployee());
        }

        //setDirty(false);
    }

    public void saveComplianceSurvey(ActionEvent actionEvent) {
        currentComplianceSurvey.save(getEntityManager1());
    }

    public void saveEntryDocumentInspection() {
        // tk impl save
        System.out.println("impl save doc inspection");
    }

    public void closeComplianceSurvey() {
        PrimeFacesUtils.closeDialog(null);
    }

    public void closeDocumentInspection() {
        promptToSaveIfRequired();
    }

    public void okProductInspection(ActionEvent actionEvent) {
        // tk impl save
        System.out.println("impl okProductInspection");
    }

    public void removeProductInspection(ActionEvent event) {
        // tk edit
//        if (activeTabTitle.equals("Compliance Survey")) {
//            currentComplianceSurvey.getProductInspections().remove(currentProductInspection);
//            currentProductInspection = new ProductInspection();
//        }
//
//        setDirty(true);

    }

    public String getLatestAlert() {
        return "*********** TOYS R US BABY STROLLER MODEL # 3213 **********";
    }

    private void promptToSaveIfRequired() {
        // tk impl 
        System.out.println("impl promptToSaveIfRequired");
    }

    public void save() {
        // tk impl 
        System.out.println("impl save()");
    }

    public List getProductStatus() {
        ArrayList types = new ArrayList();

        types.add(new SelectItem("-- select status --", "-- select status --"));
        types.add(new SelectItem("Satisfactory", "Satisfactory"));
        types.add(new SelectItem("Unsatisfactory", "Unsatisfactory"));

        return types;
    }

    public List getShippingContainerDetainPercentages() {
        ArrayList types = new ArrayList();

        types.add(new SelectItem("0", "0"));
        types.add(new SelectItem("25", "25"));
        types.add(new SelectItem("50", "50"));
        types.add(new SelectItem("75", "75"));
        types.add(new SelectItem("100", "100"));

        return types;
    }

    public List getCountries() {

        // tk see how this is impl. elsewhere
        EntityManager em = getEntityManager1();
        ArrayList countriesList = new ArrayList();
        countriesList.add(new SelectItem(" ", " "));
        countriesList.add(new SelectItem("-- Not displayed --", "-- Not displayed --"));

        List<Country> countries = Country.findAllCountries(em);
        for (Country country : countries) {
            countriesList.add(new SelectItem(country.getName(), country.getName()));
        }

        return countriesList;
    }

    // tk put in db
    public List getReportNames() {
        ArrayList names = new ArrayList();

        names.add(new SelectItem("Daily Report", "Daily Report"));

        return names;
    }

    // tk put in db
    public List getPortsOfEntry() {
        ArrayList types = new ArrayList();

        types.add(new SelectItem("--", "--"));
        types.add(new SelectItem("-- N/A --", "-- N/A --"));
        types.add(new SelectItem("Adolph Levy warehouse", "Adolph Levy warehouse"));
        types.add(new SelectItem("Airport", "Airport"));
        types.add(new SelectItem("Berth 1", "Berth 1"));
        types.add(new SelectItem("Berth 2", "Berth 2"));
        types.add(new SelectItem("Berth 3", "Berth 3"));
        types.add(new SelectItem("Berth 4", "Berth 4"));
        types.add(new SelectItem("Berth 5", "Berth 5"));
        types.add(new SelectItem("Berth 6", "Berth 6"));
        types.add(new SelectItem("Berth 7", "Berth 7"));
        types.add(new SelectItem("Berth 8", "Berth 8"));
        types.add(new SelectItem("Berth 9", "Berth 9"));
        types.add(new SelectItem("Berth 10", "Berth 10"));
        types.add(new SelectItem("Berth 11", "Berth 11"));
        types.add(new SelectItem("CKL", "CKL"));
        types.add(new SelectItem("Harbour cold storage", "Harbour cold storage"));
        types.add(new SelectItem("Kingston Logistics Centre", "Kingston Logistics Centre"));
        types.add(new SelectItem("Kingston Wharf", "Kingston Wharf"));
        types.add(new SelectItem("Port handlers", "Port handlers"));
        types.add(new SelectItem("Precision cold storage", "Precision cold storage"));
        types.add(new SelectItem("Seaboard freight", "Seaboard freight"));
        types.add(new SelectItem("Universal Freight Handlers", "Universal Freight Handlers"));
        types.add(new SelectItem("Zero cold storage", "Zero cold storage"));

        return types;
    }

    public List getInspectionPoints() {
        ArrayList points = new ArrayList();

        points.add(new SelectItem("-- N/A --", "-- N/A --"));
        points.add(new SelectItem("One Stop Shop", "One Stop Shop"));
        points.add(new SelectItem("Stripping Station", "Stripping Station"));

        return points;
    }

    public List getDocumentInspectionActions() {
        ArrayList points = new ArrayList();

        points.add(new SelectItem("None", "None"));
        points.add(new SelectItem("Stamped 'To Be Inspected'", "Stamped 'To Be Inspected'"));
        points.add(new SelectItem("Stamped 'Document Seen'", "Stamped 'Document Seen'"));

        return points;
    }

    // tk put in db
    public List getSurveyTypes() {
        ArrayList types = new ArrayList();

        types.add(new SelectItem("", ""));
        types.add(new SelectItem("Market Survey", "Market Survey"));
        types.add(new SelectItem("Product Survey", "Product Survey"));

        return types;
    }

    // tk put in db
    public List getSampleSources() {
        ArrayList types = new ArrayList();

        types.add(new SelectItem("Domestic Market", "Domestic Market"));
        types.add(new SelectItem("Port of Entry", "Port of Entry"));

        return types;
    }

//    public List<Employee> completeEmployee(String query) {
//
//        try {
//            List<Employee> employees = Employee.findEmployeesByName(getEntityManager1(), query);
//            if (employees != null) {
//                return employees;
//            } else {
//                return new ArrayList<>();
//            }
//        } catch (Exception e) {
//            System.out.println(e);
//            return new ArrayList<>();
//        }
//    }
    public void updateComplianceSurveyInspector() {
        // tk impl 
        System.out.println("impl ...");
    }

    public void updateDocumentInspectionInspector() {
        // tk impl 
        System.out.println("impl updateDocumentInspectionInspector");
    }

    public void onMainTabChange(TabChangeEvent event) {
        activeTabTitle = event.getTab().getTitle();
    }

    public String getSurveyFormComponentsToUpdate() {
        return ":marketProductSurveyDialogForm:retailOutlet,"
                + ":marketProductSurveyDialogForm:inspector,"
                + ":marketProductSurveyDialogForm:dateAndTimeChecked,"
                + ":marketProductSurveyDialogForm:comments,"
                + ":marketProductSurveyDialogForm:surveyType,"
                + ":marketProductSurveyDialogForm:portOfEntry,"
                + ":marketProductSurveyDialogForm:container,"
                + ":marketProductSurveyDialogForm:products";
    }

    public String getDateSearchField() {
        return dateSearchField;
    }

    public void setDateSearchField(String dateSearchField) {
        this.dateSearchField = dateSearchField;
    }

    public String getDateSearchPeriod() {
        return dateSearchPeriod;
    }

    public void setDateSearchPeriod(String dateSearchPeriod) {
        this.dateSearchPeriod = dateSearchPeriod;
    }

    public Date getReportEndDate() {
        return reportEndDate;
    }

    public void setReportEndDate(Date reportEndDate) {
        this.reportEndDate = reportEndDate;
    }

    public String getReportPeriod() {
        return reportPeriod;
    }

    public void setReportPeriod(String reportPeriod) {
        this.reportPeriod = reportPeriod;
    }

    public String getReportSearchText() {
        return reportSearchText;
    }

    public void setReportSearchText(String reportSearchText) {
        this.reportSearchText = reportSearchText;
    }

    public Date getReportStartDate() {
        return reportStartDate;
    }

    public void setReportStartDate(Date reportStartDate) {
        this.reportStartDate = reportStartDate;
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

//    public void doSurveySearch() {
//        complianceSurveys = ComplianceSurvey.findComplianceSurveysByDateSearchField(getEntityManager1(),
//                getUser(),
//                dateSearchField,
//                "General",
//                searchText,
//                getDatePeriod().getStartDate(),
//                getDatePeriod().getEndDate(),
//                false);
//
//        openSurveyBrowser();
//    }

    public List<String> completeSearchText(String query) {
        List<String> suggestions = new ArrayList<>();

        // NB: This is only an example implementation
        // based on the current code based, the following code is never called
        if (searchType.equals("?")) {
            // add suggestion strings to the suggestions list
        }

        return suggestions;
    }

    public void handleProductPhotoFileUpload(FileUploadEvent event) {
        FileOutputStream fout;
        UploadedFile upLoadedFile = event.getFile();
        String baseURL = "\\\\bosprintsrv\\c$\\uploads\\images"; // ".\\uploads\\images\\" +  // tk to be made system option
        String upLoadedFileName = userManagement.getUser().getId() + "_"
                + new Date().getTime() + "_"
                + upLoadedFile.getFileName();

        String imageURL = baseURL + "\\" + upLoadedFileName;

        try {
            fout = new FileOutputStream(imageURL);
            fout.write(upLoadedFile.getContents());
            fout.close();

            getCurrentProductInspection().setImageURL(upLoadedFileName);
            //setDirty(true);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void editComplianceSurvey() {
        openComplianceSurvey();
    }

    public Boolean getComplianceSurveyIsValid() {
//        if (getCurrentComplianceSurvey().getId() != null) {
//            return true;
//        } else if (dirty) {
//            return true;
//        }

        return false;
    }

    public Boolean getProductInspectionImageIsValid() {
        return !getCurrentProductInspection().getImageURL().isEmpty();
    }

    // start long process bar related methods
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
    // end long process bar related methods

    // tk to download actual file
    public StreamedContent getCurrentProductInspectionImageDownload() {
        StreamedContent streamedFile = null;

        // tk test gen report
        HashMap parameters = new HashMap();

        Connection con = BusinessEntityUtils.establishConnection("com.mysql.jdbc.Driver",
                "jdbc:mysql://boshrmapp:3306/jmtstest",
                "root", // make system option
                "bsj0001");  // make sys
        if (con != null) {
            System.out.println("connected!");
            String reportFileURL = "//bosprintsrv/c$/uploads/templates/DetentionRequest.jasper";
            try {
                parameters.put("formId", 178153L);
                JasperPrint print = JasperFillManager.fillReport(reportFileURL, parameters, con);

            } catch (JRException ex) {
                System.out.println(ex);
            }

        } else {
            System.out.println("not connected!");
        }

        String baseURL = "C:\\glassfishv3\\images\\11153_1359128328029_print-file.png";

        try {

            FileInputStream stream = new FileInputStream(baseURL);
            streamedFile = new DefaultStreamedContent(stream, "image/png", "downloaded.png");

        } catch (FileNotFoundException ex) {
            System.out.println(ex);
        }

        return streamedFile;
    }

    public StreamedContent getDetentionRequestFile() {

        EntityManager em = getEntityManager1();
        HashMap parameters = new HashMap();
//
//        updateComplianceSurvey(em);
//        // set parameters
//        parameters.put("formId", currentComplianceSurvey.getId().longValue());
//
//        // broker detail
//        //Client broker = jm.getClientByName(em, currentComplianceSurvey.getBroker().getName());
//        //Contact brokerRep = jm.getContactByName(em, username, username);
//        parameters.put("brokerDetail", currentComplianceSurvey.getBroker().getName() + "\n"
//                + currentComplianceSurvey.getBrokerRepresentative().getFirstName() + " "
//                + currentComplianceSurvey.getBrokerRepresentative().getLastName() + "\n"
//                + currentComplianceSurvey.getBroker().getBillingAddress().getAddressLine1() + "\n"
//                + currentComplianceSurvey.getBroker().getBillingAddress().getAddressLine2() + "\n"
//                + currentComplianceSurvey.getBroker().getBillingAddress().getCity() + "\n"
//                + currentComplianceSurvey.getBroker().getBillingAddress().getStateOrProvince());
//
//        //Consignee detail
//        //Client consignee = jm.getClientByName(em, currentComplianceSurvey.getConsignee().getName());
//        parameters.put("consigneeDetail", currentComplianceSurvey.getConsignee().getName() + "\n"
//                + currentComplianceSurvey.getConsignee().getBillingAddress().getAddressLine1() + "\n"
//                + currentComplianceSurvey.getConsignee().getBillingAddress().getAddressLine2() + "\n"
//                + currentComplianceSurvey.getConsignee().getBillingAddress().getCity() + "\n"
//                + currentComplianceSurvey.getConsignee().getBillingAddress().getStateOrProvince() + "\n"
//                + BusinessEntityUtils.getContactTelAndFax(currentComplianceSurvey.getConsignee().getMainContact()));
//
//        parameters.put("products", getComplianceSurveyProductNames());
//        parameters.put("quantity", getComplianceSurveyProductQuantitiesAndUnits());
//        parameters.put("numberOfSamplesTaken", getComplianceSurveyProductTotalSampleSize());

        return getComplianceSurveyFormPDFFile(
                em,
                "portOfEntryDetentionRequestForm",
                "detention_request.pdf",
                parameters,
                "PORT_OF_ENTRY_DETENTION");
    }

    public StreamedContent getReleaseRequestForPortOfEntryFile() {

        EntityManager em = getEntityManager1();
        HashMap parameters = new HashMap();

//        updateComplianceSurvey(em);
//
//        // specified release location               
//        parameters.put("specifiedReleaseLocation", currentComplianceSurvey.getSpecifiedReleaseLocation().getName() + "\n"
//                + currentComplianceSurvey.getSpecifiedReleaseLocation().getAddressLine1() + "\n"
//                + currentComplianceSurvey.getSpecifiedReleaseLocation().getAddressLine2() + "\n"
//                + currentComplianceSurvey.getSpecifiedReleaseLocation().getCity() + "\n"
//                + currentComplianceSurvey.getSpecifiedReleaseLocation().getStateOrProvince());
//
//        // consignee
//        // Client consignee = jm.getClientByName(em, currentComplianceSurvey.getConsignee().getName());
//        parameters.put("consigneeDetail", currentComplianceSurvey.getConsignee().getName() + "\n"
//                + currentComplianceSurvey.getConsignee().getBillingAddress().getAddressLine1() + "\n"
//                + currentComplianceSurvey.getConsignee().getBillingAddress().getAddressLine2() + "\n"
//                + currentComplianceSurvey.getConsignee().getBillingAddress().getCity() + "\n"
//                + currentComplianceSurvey.getConsignee().getBillingAddress().getStateOrProvince());
//
//        parameters.put("products", getComplianceSurveySampledProductNamesQuantitiesAndUnits());
//        parameters.put("numberOfSamplesTaken", getComplianceSurveyProductTotalSampleSize());
        return getComplianceSurveyFormPDFFile(
                em,
                "portOfEntryReleaseRequestForm",
                "release_request.pdf",
                parameters,
                "PORT_OF_ENTRY_DETENTION");

    }

    public StreamedContent getSampleRequestFile() {

        EntityManager em = getEntityManager1();
        HashMap parameters = new HashMap();

//        updateComplianceSurvey(em);
//
//        // broker
//        parameters.put("formId", currentComplianceSurvey.getId().longValue());
//        parameters.put("brokerDetail", currentComplianceSurvey.getBroker().getName() + "\n"
//                + currentComplianceSurvey.getBroker().getBillingAddress().getAddressLine1() + "\n"
//                + currentComplianceSurvey.getBroker().getBillingAddress().getAddressLine2() + "\n"
//                + BusinessEntityUtils.getContactTelAndFax(currentComplianceSurvey.getBroker().getMainContact()));
//
//        // consignee
//        parameters.put("consigneeDetail", currentComplianceSurvey.getConsignee().getBillingAddress().getAddressLine1() + ", "
//                + currentComplianceSurvey.getConsignee().getBillingAddress().getAddressLine2() + ", "
//                + currentComplianceSurvey.getConsignee().getBillingAddress().getCity() + ", "
//                + currentComplianceSurvey.getConsignee().getBillingAddress().getStateOrProvince());
//
//        // consignee contact person
//        parameters.put("consigneeContactPerson", BusinessEntityUtils.getContactFullName(currentComplianceSurvey.getConsigneeRepresentative()));
//
//        parameters.put("consigneeTelFaxEmail", BusinessEntityUtils.getMainTelFaxEmail(currentComplianceSurvey.getConsignee().getMainContact()));
//        parameters.put("products", getComplianceSurveyProductNames());
//        parameters.put("quantity", getComplianceSurveyProductQuantitiesAndUnits());
//        parameters.put("numberOfSamplesTaken", getComplianceSurveyProductTotalSampleSize());
//
//        // sample disposal
//        if (currentComplianceSurvey.getSamplesToBeCollected()) {
//            parameters.put("samplesToBeCollected", "\u2713"); // \u2713 is unicode for tick
//        } else {
//            parameters.put("samplesToBeCollected", "");
//        }
//        if (currentComplianceSurvey.getSamplesToBeDisposed()) {
//            parameters.put("samplesToBeDisposed", "\u2713");
//        } else {
//            parameters.put("samplesToBeDisposed", "");
//        }
        return getComplianceSurveyFormPDFFile(
                em,
                "portOfEntryDetentionSampleRequestForm",
                "sample_request.pdf",
                parameters,
                "PORT_OF_ENTRY_DETENTION");
    }

    public StreamedContent getNoticeOfReleaseFromDetentionFile() {
        EntityManager em = getEntityManager1();
        HashMap parameters = new HashMap();
//
//        updateComplianceSurvey(em);
//
//        // full release
//        if (currentComplianceSurvey.getFullRelease()) {
//            parameters.put("fullRelease", "\u2713");
//        } else {
//            parameters.put("fullRelease", "");
//        }
//
//        // retailer, distributor, other?
//        if (currentComplianceSurvey.getRetailer()) {
//            parameters.put("retailer", "\u2713");
//        } else {
//            parameters.put("retailer", "");
//        }
//        if (currentComplianceSurvey.getDistributor()) {
//            parameters.put("distributor", "\u2713");
//        } else {
//            parameters.put("distributor", "");
//        }
//        if (currentComplianceSurvey.getOtherCompanyTypes()) {
//            parameters.put("otherCompanyTypes", "\u2713");
//            parameters.put("companyTypes", currentComplianceSurvey.getCompanyTypes());
//        } else {
//            parameters.put("otherCompanyTypes", "");
//            currentComplianceSurvey.setCompanyTypes("");
//            parameters.put("companyTypes", currentComplianceSurvey.getCompanyTypes());
//        }
//
//        // broker
//        parameters.put("formId", currentComplianceSurvey.getId().longValue());
//        parameters.put("brokerDetail", currentComplianceSurvey.getBroker().getName() + "\n"
//                + currentComplianceSurvey.getBroker().getBillingAddress().getAddressLine1() + "\n"
//                + currentComplianceSurvey.getBroker().getBillingAddress().getAddressLine2() + "\n"
//                + BusinessEntityUtils.getContactTelAndFax(currentComplianceSurvey.getBroker().getMainContact()));
//
//        // consignee
//        parameters.put("consigneeDetail", currentComplianceSurvey.getConsignee().getBillingAddress().getAddressLine1() + ", "
//                + currentComplianceSurvey.getConsignee().getBillingAddress().getAddressLine2() + ", "
//                + currentComplianceSurvey.getConsignee().getBillingAddress().getCity() + ", "
//                + currentComplianceSurvey.getConsignee().getBillingAddress().getStateOrProvince());
//
//        // provisional release location 
//        parameters.put("specifiedReleaseLocationDomesticMarket", currentComplianceSurvey.getSpecifiedReleaseLocationDomesticMarket().getAddressLine1() + ", "
//                + currentComplianceSurvey.getSpecifiedReleaseLocationDomesticMarket().getAddressLine2() + ", "
//                + currentComplianceSurvey.getSpecifiedReleaseLocationDomesticMarket().getCity() + ", "
//                + currentComplianceSurvey.getSpecifiedReleaseLocationDomesticMarket().getStateOrProvince());
//
//        // location of detained products locationOfDetainedProduct 
//        parameters.put("locationOfDetainedProduct", currentComplianceSurvey.getLocationOfDetainedProductDomesticMarket().getAddressLine1() + ", "
//                + currentComplianceSurvey.getLocationOfDetainedProductDomesticMarket().getAddressLine2() + ", "
//                + currentComplianceSurvey.getLocationOfDetainedProductDomesticMarket().getCity() + ", "
//                + currentComplianceSurvey.getLocationOfDetainedProductDomesticMarket().getStateOrProvince());
//
//        // consignee contact person
//        parameters.put("consigneeContactPerson", BusinessEntityUtils.getContactFullName(currentComplianceSurvey.getConsigneeRepresentative()));
//
//        // consignee tel/fax/email
//        parameters.put("consigneeTelFaxEmail", BusinessEntityUtils.getMainTelFaxEmail(currentComplianceSurvey.getConsignee().getMainContact()));
//
//        parameters.put("products", getComplianceSurveyProductNames());
//        parameters.put("productBrandNames", getComplianceSurveyProductBrandNames());
//        parameters.put("productBatchCodes", getComplianceSurveyProductBatchCodes());
//        parameters.put("quantity", getComplianceSurveyProductQuantitiesAndUnits());
//        parameters.put("numberOfSamplesTaken", getComplianceSurveyProductTotalSampleSize());

        return getComplianceSurveyFormPDFFile(
                em,
                "noticeOfReleaseFromDetentionForm",
                "release_notice.pdf",
                parameters,
                "DOMESTIC_MARKET_DETENTION");
    }

    public String getComplianceSurveyProductNames() {
        String names = "";

        for (ProductInspection product : currentComplianceSurvey.getProductInspections()) {
            if (names.equals("")) {
                names = product.getName();
            } else {
                names = names + ", " + product.getName();
            }
        }

        return names;
    }

    public Boolean samplesTaken() {
        for (ProductInspection product : currentComplianceSurvey.getProductInspections()) {
            if (product.getSampledForLabelAssessment() || product.getSampledForTesting()) {
                return true;
            }
        }

        return false;
    }

    public String getComplianceSurveyProductBrandNames() {
        String brandNames = "";

        for (ProductInspection product : currentComplianceSurvey.getProductInspections()) {
            if (brandNames.equals("")) {
                brandNames = product.getBrand();
            } else {
                brandNames = brandNames + ", " + product.getBrand();
            }
        }

        return brandNames;
    }

    public String getComplianceSurveyProductBatchCodes() {
        String batchCodes = "";

        for (ProductInspection product : currentComplianceSurvey.getProductInspections()) {
            if (batchCodes.equals("")) {
                batchCodes = product.getBatchCode();
            } else {
                batchCodes = batchCodes + ", " + product.getBatchCode();
            }
        }

        return batchCodes;
    }

    public String getComplianceSurveyProductTotalQuantity() {
        Integer totalQuantity = 0;

        for (ProductInspection product : currentComplianceSurvey.getProductInspections()) {
            if (product.getQuantity() != null) {
                totalQuantity = totalQuantity + product.getQuantity();
            }
        }

        return totalQuantity.toString();
    }

    public String getComplianceSurveySampledProductNamesQuantitiesAndUnits() {
        String namesQuantitiesAndUnits = "";

        for (ProductInspection product : currentComplianceSurvey.getProductInspections()) {
            if (product.getSampledForTesting() || product.getSampledForLabelAssessment()) {
                if (namesQuantitiesAndUnits.equals("")) {
                    namesQuantitiesAndUnits = namesQuantitiesAndUnits + product.getContainerSize() + " " + product.getQuantity() + " " + product.getQuantityUnit() + " of " + product.getName();
                } else {
                    namesQuantitiesAndUnits = namesQuantitiesAndUnits + ", " + product.getContainerSize() + " " + product.getQuantity() + " " + product.getQuantityUnit() + " of " + product.getName();
                }
            }
        }

        return namesQuantitiesAndUnits;
    }

    public String getComplianceSurveyProductQuantitiesAndUnits() {
        String quantitiesAndUnits = "";

        for (ProductInspection product : currentComplianceSurvey.getProductInspections()) {
//            if (product.getQuantity() != null) {
            if (quantitiesAndUnits.equals("")) {
                quantitiesAndUnits = quantitiesAndUnits + product.getContainerSize() + " " + product.getQuantity() + " " + product.getQuantityUnit();
            } else {
                quantitiesAndUnits = quantitiesAndUnits + ", " + product.getContainerSize() + " " + product.getQuantity() + " " + product.getQuantityUnit();
            }
//            }
        }

        return quantitiesAndUnits;
    }

    public String getComplianceSurveyProductTotalSampleSize() {
        Integer totalSampleSize = 0;

        for (ProductInspection productInspection : currentComplianceSurvey.getProductInspections()) {
            if (productInspection.getNumProductsSampled() != null) {
                totalSampleSize = totalSampleSize + productInspection.getNumProductsSampled();
            }
        }

        return totalSampleSize.toString();
    }

    // tk use of this may have to be retired.
    public void updateComplianceSurvey(EntityManager em) {
//        if (dirty) {
//            save();
//        }
//        if (currentComplianceSurvey.getId() != null) {
//            currentComplianceSurvey = ComplianceSurvey.findComplianceSurveyById(em, currentComplianceSurvey.getId());
//        }
    }

    public StreamedContent getComplianceSurveyFormPDFFile(
            EntityManager em,
            String form,
            String fileName,
            HashMap parameters,
            String sequentialNumberName) {

        if (currentComplianceSurvey.getId() != null) {
            try {
                Connection con = BusinessEntityUtils.establishConnection(
                        "com.mysql.jdbc.Driver",
                        "jdbc:mysql://boshrmapp:3306/jmtstest", // tk make system option
                        "root", // tk make system option
                        "bsj0001");  // tk make system option
                if (con != null) {
                    StreamedContent streamContent;

                    String reportFileURL = (String) SystemOption.getOptionValueObject(em, form);

                    // make sure is parameter is set for all forms
                    parameters.put("formId", currentComplianceSurvey.getId().longValue());

                    // tk remove and do this when compliance survey is being saved
                    // get and set reference number if it is null
                    if (sequentialNumberName.equals("PORT_OF_ENTRY_DETENTION")) {
                        if (currentComplianceSurvey.getPortOfEntryDetentionNumber() == null) {
                            em.getTransaction().begin();
                            int year = BusinessEntityUtils.getCurrentYear();
                            currentComplianceSurvey. // tk BSJ-D42- to be made option?
                                    setPortOfEntryDetentionNumber("BSJ-D42-" + year + "-"
                                            + BusinessEntityUtils.getFourDigitString(SequenceNumber.findNextSequentialNumberByNameAndByYear(em, sequentialNumberName, year)));
                            //setDirty(dirty);
                            updateComplianceSurvey(em);
                            em.getTransaction().commit();

                        }
                        //parameters.put("referenceNumber", currentComplianceSurvey.getReferenceNumber());
                    } else if (sequentialNumberName.equals("DOMESTIC_MARKET_DETENTION")) {
                        if (currentComplianceSurvey.getDomesticMarketDetentionNumber() == null) {
                            em.getTransaction().begin();
                            int year = BusinessEntityUtils.getCurrentYear();
                            currentComplianceSurvey. // tk BSJ-D42- to be made option?
                                    setDomesticMarketDetentionNumber("BSJ-DM42-" + year + "-"
                                            + BusinessEntityUtils.getFourDigitString(SequenceNumber.findNextSequentialNumberByNameAndByYear(em, sequentialNumberName, year)));
                            //setDirty(dirty);
                            updateComplianceSurvey(em);
                            em.getTransaction().commit();

                        }
                        //parameters.put("referenceNumber", currentComplianceSurvey.getReferenceNumber());
                    }

                    // generate report
                    JasperPrint print = JasperFillManager.fillReport(reportFileURL, parameters, con);

                    byte[] fileBytes = JasperExportManager.exportReportToPdf(print);

                    streamContent = new DefaultStreamedContent(new ByteArrayInputStream(fileBytes), "application/pdf", fileName);
                    setLongProcessProgress(100);

                    return streamContent;
                } else {
                    return null;
                }
            } catch (Exception e) {
                System.out.println(e);
                setLongProcessProgress(100);

                return null;
            }
        } else {
            return null;
        }
    }

    public StreamedContent getComplianceDailyReportPDFFile() {

        EntityManager em = getEntityManager1();
        HashMap parameters = new HashMap();

        try {
            Connection con = BusinessEntityUtils.establishConnection(
                    "com.mysql.jdbc.Driver",
                    "jdbc:mysql://boshrmapp:3306/jmtstest", // tk make system option
                    "root", // tk make system option
                    "bsj0001");  // tk make system option
            if (con != null) {
                StreamedContent streamContent;

                String reportFileURL = (String) SystemOption.getOptionValueObject(em, "complianceDailyReport");

                // make sure is parameter is set for all forms
                parameters.put("team", currentComplianceDailyReport.getTeam());
                parameters.put("startOfPeriod", currentComplianceDailyReport.getStartOfPeriod());
                parameters.put("endOfPeriod", currentComplianceDailyReport.getEndOfPeriod());
                parameters.put("timeOfArrival", currentComplianceDailyReport.getTimeOfArrival());
                parameters.put("timeOfDeparture", currentComplianceDailyReport.getTimeOfDeparture());
                parameters.put("inspectionPoint", "One Stop Shop"); // tk ,make option
                parameters.put("inspectionPoint_2", "Stripping Station"); // tk make option
                parameters.put("location", currentComplianceDailyReport.getLocation());
                parameters.put("teamMembers", currentComplianceDailyReport.getTeamMembers());
                parameters.put("driver", currentComplianceDailyReport.getDriver());
                parameters.put("numOfInspectorsOnVisit", currentComplianceDailyReport.getNumOfInspectorsOnVisit());

                // generate report
                JasperPrint print = JasperFillManager.fillReport(reportFileURL, parameters, con);

                byte[] fileBytes = JasperExportManager.exportReportToPdf(print);

                streamContent = new DefaultStreamedContent(new ByteArrayInputStream(fileBytes), "application/pdf", "daily_report.pdf");
                setLongProcessProgress(100);

                return streamContent;
            } else {
                return null;
            }

        } catch (Exception e) {
            System.out.println(e);
            setLongProcessProgress(100);

            return null;
        }
    }

    public StreamedContent getNoticeOfDetentionFile() {
        EntityManager em = getEntityManager1();
        HashMap parameters = new HashMap();

//        updateComplianceSurvey(em);
//
//        // retailer, distributor, other?
//        if (currentComplianceSurvey.getRetailer()) {
//            parameters.put("retailer", "\u2713");
//        } else {
//            parameters.put("retailer", "");
//        }
//        if (currentComplianceSurvey.getDistributor()) {
//            parameters.put("distributor", "\u2713");
//        } else {
//            parameters.put("distributor", "");
//        }
//        if (currentComplianceSurvey.getOtherCompanyTypes()) {
//            parameters.put("otherCompanyTypes", "\u2713");
//            parameters.put("companyTypes", currentComplianceSurvey.getCompanyTypes());
//        } else {
//            parameters.put("otherCompanyTypes", "");
//            currentComplianceSurvey.setCompanyTypes("");
//            parameters.put("companyTypes", currentComplianceSurvey.getCompanyTypes());
//        }
//
//        // broker
//        parameters.put("formId", currentComplianceSurvey.getId().longValue());
//        parameters.put("brokerDetail", currentComplianceSurvey.getBroker().getName() + "\n"
//                + currentComplianceSurvey.getBroker().getBillingAddress().getAddressLine1() + "\n"
//                + currentComplianceSurvey.getBroker().getBillingAddress().getAddressLine2() + "\n"
//                + BusinessEntityUtils.getContactTelAndFax(currentComplianceSurvey.getBroker().getMainContact()));
//
//        // consignee
//        parameters.put("consigneeDetail", currentComplianceSurvey.getConsignee().getBillingAddress().getAddressLine1() + ", "
//                + currentComplianceSurvey.getConsignee().getBillingAddress().getAddressLine2() + ", "
//                + currentComplianceSurvey.getConsignee().getBillingAddress().getCity() + ", "
//                + currentComplianceSurvey.getConsignee().getBillingAddress().getStateOrProvince());
//
//        // provisional release location 
//        parameters.put("specifiedReleaseLocationDomesticMarket", currentComplianceSurvey.getSpecifiedReleaseLocationDomesticMarket().getAddressLine1() + ", "
//                + currentComplianceSurvey.getSpecifiedReleaseLocationDomesticMarket().getAddressLine2() + ", "
//                + currentComplianceSurvey.getSpecifiedReleaseLocationDomesticMarket().getCity() + ", "
//                + currentComplianceSurvey.getSpecifiedReleaseLocationDomesticMarket().getStateOrProvince());
//
//        // location of detained products locationOfDetainedProduct 
//        parameters.put("locationOfDetainedProduct", currentComplianceSurvey.getLocationOfDetainedProductDomesticMarket().getAddressLine1() + ", "
//                + currentComplianceSurvey.getLocationOfDetainedProductDomesticMarket().getAddressLine2() + ", "
//                + currentComplianceSurvey.getLocationOfDetainedProductDomesticMarket().getCity() + ", "
//                + currentComplianceSurvey.getLocationOfDetainedProductDomesticMarket().getStateOrProvince());
//
//        // consignee tel/fax/email
//        parameters.put("consigneeTelFaxEmail", BusinessEntityUtils.getMainTelFaxEmail(currentComplianceSurvey.getConsignee().getMainContact()));
//
//        parameters.put("products", getComplianceSurveyProductNames());
//        parameters.put("productBrandNames", getComplianceSurveyProductBrandNames());
//        parameters.put("productBatchCodes", getComplianceSurveyProductBatchCodes());
//        parameters.put("quantity", getComplianceSurveyProductQuantitiesAndUnits());
//        parameters.put("numberOfSamplesTaken", getComplianceSurveyProductTotalSampleSize());
//
//        if (samplesTaken()) {
//            parameters.put("samplesTaken", "\u2713");
//        } else {
//            parameters.put("samplesTaken", "");
//        }
        return getComplianceSurveyFormPDFFile(
                em,
                "noticeOfDetentionForm",
                "detention_notice.pdf",
                parameters,
                "DOMESTIC_MARKET_DETENTION");
    }

    public Boolean isDirty() { // tk delete
        return false; //dirty;
    }
}
