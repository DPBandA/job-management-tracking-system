/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.com.dpbennett.jmts;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import jm.com.dpbennett.business.entity.BusinessEntityManager;
import jm.com.dpbennett.business.entity.Category;
import jm.com.dpbennett.business.entity.Client;
import jm.com.dpbennett.business.entity.CompanyRegistration;
import jm.com.dpbennett.business.entity.ComplianceDailyReport;
import jm.com.dpbennett.business.entity.ComplianceSurvey;
import jm.com.dpbennett.business.entity.Contact;
import jm.com.dpbennett.business.entity.Country;
import jm.com.dpbennett.business.entity.Distributor;
import jm.com.dpbennett.business.entity.DocumentInspection;
import jm.com.dpbennett.business.entity.DocumentStandard;
import jm.com.dpbennett.business.entity.Employee;
import jm.com.dpbennett.business.entity.EntryDocumentInspection;
import jm.com.dpbennett.business.entity.Job;
import jm.com.dpbennett.business.entity.JobManagerUser;
import jm.com.dpbennett.business.entity.Manufacturer;
import jm.com.dpbennett.business.entity.ProductInspection;
import jm.com.dpbennett.business.entity.SampleRequest;
import jm.com.dpbennett.business.entity.SequenceNumber;
import jm.com.dpbennett.business.entity.ShippingContainer;
import jm.com.dpbennett.business.entity.SystemOption;
import jm.com.dpbennett.business.utils.BusinessEntityUtils;
import jm.com.dpbennett.business.utils.SearchParameters;
import jm.com.dpbennett.jmts.utils.DialogActionHandler;
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
 * @author dbennett
 */
@Named(value = "complianceManager")
@SessionScoped
public class ComplianceManager implements Serializable, BusinessEntityManager, DialogActionHandler {

    @PersistenceUnit(unitName = "BSJDBPU")
    private EntityManagerFactory EMF1;
    private EntityManager entityManager1;
    private ComplianceSurvey currentComplianceSurvey;
    private ProductInspection currentProductInspection;
    private SampleRequest currentSampleRequest;
    private CompanyRegistration currentCompanyRegistration;
    private Boolean dirty;
    private Boolean isNewProductInspection = false;
    private Boolean isNewComplianceSurvey = false;
    private Boolean isNewDocumentInspection = false;
    private String dialogActionHandlerId;
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
    private Main main;
    private SearchParameters currentSearchParameters;
    private int standardsComplianceMetTabViewActiveIndex;
    private String componentsToUpdate;
    private String shippingContainerTableToUpdate;
    private String complianceSurveyTableToUpdate;
    private Boolean isLoadingForm;
    private Boolean surveysWithProductInspection = true;
    private List<String> selectedStandardNames;
    private List<String> selectedContainerNumbers;

    /**
     * Creates a new instance of ComplianceManager
     */
    public ComplianceManager() {
        selectedStandardNames = new ArrayList<>();
        selectedContainerNumbers = new ArrayList<>();
        complianceSurveys = new ArrayList<>();
        documentInspections = new ArrayList<>();
        dirty = false;
        isLoadingForm = false;
        dateSearchField = "dateFirstReceived";
        searchType = "General";
        dateSearchPeriod = "This month";
        reportPeriod = "This month";
        activeTabTitle = "Compliance Survey";
        currentComplianceDailyReport =
                new ComplianceDailyReport("Report-" + new Date().toString(),
                new Date(), "Berth 11", " "); // tk put into options table.

        main = App.findBean("main");

        // Components to update
        shippingContainerTableToUpdate = ":ComplianceSurveyDialogForm:complianceSurveyTabView:containersTable";
        componentsToUpdate = ":ComplianceSurveyDialogForm";
        complianceSurveyTableToUpdate = "mainTabViewForm:mainTabView:complianceSurveysTable";
        //  setComponentsToUpdate(":mainTabViewForm:mainTabView:complianceSurveysTable,:ComplianceSurveyDialogForm:complianceSurveyToolsMenuButton");
    }

    public List<String> getSelectedStandardNames() {
        return selectedStandardNames;
    }

    public void setSelectedStandardNames(List<String> selectedStandardNames) {
        this.selectedStandardNames = selectedStandardNames;
    }

    public List<String> getSelectedContainerNumbers() {
        return selectedContainerNumbers;
    }

    public void setSelectedContainerNumbers(List<String> selectedContainerNumbers) {
        this.selectedContainerNumbers = selectedContainerNumbers;
    }

    public Boolean getSurveysWithProductInspection() {
        return surveysWithProductInspection;
    }

    public void setSurveysWithProductInspection(Boolean surveysWithProductInspection) {
        this.surveysWithProductInspection = surveysWithProductInspection;
    }

    public String getComplianceSurveyTableToUpdate() {
        return complianceSurveyTableToUpdate;
    }

    public void setComplianceSurveyTableToUpdate(String complianceSurveyTableToUpdate) {
        this.complianceSurveyTableToUpdate = complianceSurveyTableToUpdate;
    }

    public String getShippingContainerTableToUpdate() {
        return shippingContainerTableToUpdate;
    }

    public void setShippingContainerTableToUpdate(String shippingContainerTableToUpdate) {
        this.shippingContainerTableToUpdate = shippingContainerTableToUpdate;
    }

    public String getComponentsToUpdate() {
        return componentsToUpdate;
    }

    public void setComponentsToUpdate(String componentsToUpdate) {
        this.componentsToUpdate = componentsToUpdate;
    }

    public SearchParameters getCurrentSearchParameters() {
        return currentSearchParameters;
    }

    public void setCurrentSearchParameters(SearchParameters currentSearchParameters) {
        this.currentSearchParameters = currentSearchParameters;
    }

    public Main getMain() {
        return main;
    }

    public int getStandardsComplianceMetTabViewActiveIndex() {
        return standardsComplianceMetTabViewActiveIndex;
    }

    public void setStandardsComplianceMetTabViewActiveIndex(int standardsComplianceMetTabViewActiveIndex) {
        this.standardsComplianceMetTabViewActiveIndex = standardsComplianceMetTabViewActiveIndex;
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

    /**
     * Get the user record current stored in the database
     *
     * @return
     */
    private JobManagerUser getCurrentUser() {
        EntityManager em = getEntityManager1();

        JobManagerUser user = JobManagerUser.findJobManagerUserById(em, main.getUser().getId());
        em.refresh(user);

        return user;
    }

    public void updateAuthDetentionRequestPOE() {
        if (getCurrentUser().getPrivilege().getCanAuthDetentionRequest()) {
            currentComplianceSurvey.setAuthSigDateForDetentionRequestPOE(new Date());
            currentComplianceSurvey.setAuthSigForDetentionRequestPOE(main.getUser().getEmployee().getSignature());
            setDirty(true);
        } else {
            getMain().displayCommonMessageDialog(null, "You do not have the privilege to authorize port of entry detentions.", "Not Authorized", "info");
        }

    }
    
    public void updateInspectorSigForSampleRequestPOE() {

        currentComplianceSurvey.setInspectorSigDateForSampleRequestPOE(new Date());
        currentComplianceSurvey.setInspectorSigForSampleRequestPOE(main.getUser().getEmployee().getSignature());

        setDirty(true);
    }

    public void updatePreparedBySigForReleaseRequestPOE() {

        currentComplianceSurvey.setPreparedBySigDateForReleaseRequestPOE(new Date());
        currentComplianceSurvey.setPreparedBySigForReleaseRequestPOE(main.getUser().getEmployee().getSignature());
        currentComplianceSurvey.setPreparedByEmployeeForReleaseRequestPOE(main.getUser().getEmployee());

        setDirty(true);
    }

    public void updateAuthSigForNoticeOfDentionDM() {

        if (getCurrentUser().getPrivilege().getCanAuthDetentionNotice()) {
            currentComplianceSurvey.setAuthSigDateForNoticeOfDentionDM(new Date());
            currentComplianceSurvey.setAuthSigForNoticeOfDentionDM(main.getUser().getEmployee().getSignature());
            setDirty(true);
        } else {
            getMain().displayCommonMessageDialog(null, "You do not have the privilege to authorize detention notices/releases.", "Not Authorized", "info");
        }

    }
    public void updateApprovedBySigForReleaseRequestPOE() {

        if (getCurrentUser().getPrivilege().getCanApprvReleaseRequest()) {
            currentComplianceSurvey.setApprovedBySigDateForReleaseRequestPOE(new Date());
            currentComplianceSurvey.setApprovedBySigForReleaseRequestPOE(main.getUser().getEmployee().getSignature());
            currentComplianceSurvey.setApprovedByEmployeeForReleaseRequestPOE(main.getUser().getEmployee());
            setDirty(true);
        } else {
            getMain().displayCommonMessageDialog(null, "You do not have the privilege to approve release requests.", "Not Authorized", "info");
        }

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

    public EntityManager getEntityManager1() {

        entityManager1 = getEMF1().createEntityManager();
        return entityManager1;
    }

    public void closeEntityManager1() {
        if (entityManager1 != null) {
            if (entityManager1.isOpen()) {
                entityManager1.close();
            }
            entityManager1 = null;
        }
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

    public List<Client> completeClient(String query) {
        try {
            return Client.findActiveClientsByFirstPartOfName(getEntityManager1(), query);

        } catch (Exception e) {
            System.out.println(e);

            return new ArrayList<>();
        }
    }

    public List<String> completeActiveClientName(String query) {
        try {
            return Client.findActiveClientNames(getEntityManager1(), query);
        } catch (Exception e) {
            System.out.println(e);
            return new ArrayList<>();
        }
    }

    public List<String> completeJobNumber(String query) {
        try {
            return Job.findJobNumbers(getEntityManager1(), query);
        } catch (Exception e) {
            System.out.println(e);
            return new ArrayList<>();
        }
    }

    public List<String> completeContainerNumber(String query) {
        List<String> numbers = new ArrayList<>();
        List<String> numberList = getCurrentComplianceSurvey().getEntryDocumentInspection().getContainerNumberList();

        for (String containerNumber : numberList) {
            if (containerNumber.toUpperCase().indexOf(query.toUpperCase()) != -1) {
                numbers.add(containerNumber);
            }
        }

        return numbers;
    }

    public List<String> completeManufacturerName(String query) {
        try {
            EntityManager em = getEntityManager1();

            List<String> names = new ArrayList<>();
            List<Manufacturer> manufacturers = Manufacturer.findManufacturersBySearchPattern(em, query);
            for (Manufacturer manufacturer : manufacturers) {
                names.add(manufacturer.getName());
            }

            return names;

        } catch (Exception e) {
            System.out.println(e);

            return new ArrayList<>();
        }
    }

    public List<String> completeDistributorName(String query) {
        try {
            EntityManager em = getEntityManager1();

            List<String> names = new ArrayList<>();
            List<Distributor> distributors = Distributor.findDistributorsBySearchPattern(em, query);
            for (Distributor distributor : distributors) {
                names.add(distributor.getName());
            }

            return names;

        } catch (Exception e) {
            System.out.println(e);

            return new ArrayList<>();
        }
    }

    public List<Contact> completeRetailOutletRepresentative(String query) {
        ArrayList<Contact> contactsFound = new ArrayList<>();

        for (Contact contact : currentComplianceSurvey.getRetailOutlet().getContacts()) {
            if (contact.getFirstName().toUpperCase().contains(query.trim().toUpperCase())
                    || contact.getLastName().toUpperCase().contains(query.trim().toUpperCase())) {
                contactsFound.add(contact);
            }
        }

        return contactsFound;
    }

    // Consignee update
    public void updateComplianceSurveyConsignee() {

        try {
            EntityManager em = getEntityManager1();

            updateComplianceSurveyConsignee(em);

            closeEntityManager1();
            setDirty(true);
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public void updateDocumentInspectionConsignee() {

        try {
            EntityManager em = getEntityManager1();

            Client client = Client.findClientByName(em, currentDocumentInspection.getConsignee().getName(), true);
            if (client != null) {
                currentDocumentInspection.getConsignee().doCopy(client);
            }

            closeEntityManager1();
            setDirty(true);
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public void updateComplianceSurveyConsignee(EntityManager em) {

        Client client = Client.findClientByName(em, currentComplianceSurvey.getConsignee().getName(), true);
        if (client != null) {
            currentComplianceSurvey.getConsignee().doCopy(client);
            currentComplianceSurvey.getConsignee().setActive(false);
        }
    }

    public void updateComplianceSurveyConsigneeRepresentative() {
        try {
            if (!currentComplianceSurvey.getConsignee().getContacts().isEmpty()) {
                for (Contact contact : currentComplianceSurvey.getConsignee().getContacts()) {
                    if (contact.getName().equals(currentComplianceSurvey.getConsigneeRepresentative().getName().trim())) {
                        currentComplianceSurvey.setConsigneeRepresentative(contact);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public List<String> completeComplianceSurveyConsigneeRepresentativeName(String query) {
        ArrayList<String> contactsFound = new ArrayList<>();

        try {
            for (Contact contact : currentComplianceSurvey.getConsignee().getContacts()) {
                if (contact.getFirstName().toUpperCase().contains(query.trim().toUpperCase())
                        || contact.getLastName().toUpperCase().contains(query.trim().toUpperCase())) {
                    contactsFound.add(contact.getName());
                }
            }

        } catch (Exception e) {
            System.out.println(e);
            return contactsFound;
        }

        return contactsFound;

    }

    public void editComplianceSurveyConsignee() {

        ClientManager clientManager = ComplianceManager.findBean("clientManager");

        clientManager.setClient(currentComplianceSurvey.getConsignee());
        clientManager.setTaxRegistrationNumberRequired(doClientTRNValidation(getEntityManager1()));
        clientManager.setSave(false);
        clientManager.setBusinessEntityManager(this);
        clientManager.setExternalEntityManagerFactory(EMF1);
        clientManager.setComponentsToUpdate(
                ":ComplianceSurveyDialogForm:complianceSurveyTabView:consignee,"
                + ":ComplianceSurveyDialogForm:complianceSurveyTabView:docInspectConsignee,"
                + ":ComplianceSurveyDialogForm:complianceSurveyTabView:consigneeContact,"
                + ":ComplianceSurveyDialogForm:complianceSurveyTabView:consigneeBillingAddress,"
                + ":ComplianceSurveyDialogForm:complianceSurveyTabView:consigneeContact2,"
                + ":ComplianceSurveyDialogForm:complianceSurveyTabView:consigneeBillingAddress2");
    }

    public void editDocumentInspectionConsignee() {

        ClientManager clientManager = ComplianceManager.findBean("clientManager");

        clientManager.setClient(currentDocumentInspection.getConsignee());
        clientManager.setTaxRegistrationNumberRequired(doClientTRNValidation(getEntityManager1()));
        clientManager.setSave(false);
        clientManager.setBusinessEntityManager(this);
        clientManager.setExternalEntityManagerFactory(EMF1);
        clientManager.setComponentsToUpdate(":DocumentInspectionDialogForm:consignee");

    }
    // Consignee update end

    // Broker update 
    public void updateComplianceSurveyBroker() {
        try {
            EntityManager em = getEntityManager1();

            updateComplianceSurveyBroker(em);

            closeEntityManager1();

            setDirty(true);

        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public void updateComplianceSurveyBroker(EntityManager em) {

        Client client = Client.findClientByName(em, currentComplianceSurvey.getBroker().getName(), true);
        if (client != null) {
            currentComplianceSurvey.getBroker().doCopy(client);
            currentComplianceSurvey.getBroker().setActive(false);
        }
    }

    public void updateComplianceSurveyBrokerRepresentative() {
        // Find the selected contact from the client list of contacts for this client
        try {
            if (!currentComplianceSurvey.getBroker().getContacts().isEmpty()) {
                for (Contact contact : currentComplianceSurvey.getBroker().getContacts()) {
                    if (contact.getName().equals(currentComplianceSurvey.getBrokerRepresentative().getName().trim())) {
                        currentComplianceSurvey.setBrokerRepresentative(contact);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public List<String> completeComplianceSurveyBrokerRepresentativeName(String query) {
        ArrayList<String> contactsFound = new ArrayList<>();

        try {
            for (Contact contact : currentComplianceSurvey.getBroker().getContacts()) {
                if (contact.getFirstName().toUpperCase().contains(query.trim().toUpperCase())
                        || contact.getLastName().toUpperCase().contains(query.trim().toUpperCase())) {
                    contactsFound.add(contact.getName());
                }
            }
        } catch (Exception e) {
            System.out.println("Contact complete error!");
            System.out.println(e);
        }

        return contactsFound;

    }

    public void editComplianceSurveyBroker() {

        ClientManager clientManager = ComplianceManager.findBean("clientManager");

        clientManager.setClient(currentComplianceSurvey.getBroker());
        clientManager.setTaxRegistrationNumberRequired(doClientTRNValidation(getEntityManager1()));
        clientManager.setBusinessEntityManager(this);
        clientManager.setExternalEntityManagerFactory(EMF1);
        clientManager.setComponentsToUpdate(
                ":ComplianceSurveyDialogForm:complianceSurveyTabView:broker,"
                + ":ComplianceSurveyDialogForm:complianceSurveyTabView:brokerContact,"
                + ":ComplianceSurveyDialogForm:complianceSurveyTabView:brokerBillingAddress");

    }
    // Broker update end

    // Retail outlet update 
    public void updateComplianceSurveyRetailOutlet() {
        try {
            EntityManager em = getEntityManager1();

            updateComplianceSurveyRetailOutlet(em);

            closeEntityManager1();

            setDirty(true);
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    /**
     * Update the product manufacturer field with one that exists in the
     * database. If the manufacturer does not exist in the database then create
     * a new manufacturer object that will be saved by the
     * saveComplianceSurvey() method.
     */
    public void updateProductManufacturerForProductInsp() {

        EntityManager em = getEntityManager1();

        Manufacturer manufacturer = Manufacturer.findManufacturerByName(em, getCurrentProductInspection().getManufacturer().getName());
        if (manufacturer != null) {
            currentProductInspection.setManufacturer(manufacturer);
        } else {
            if (getCurrentProductInspection().getManufacturer().getName().trim().equals("")) {
                currentProductInspection.setManufacturer(App.getDefaultManufacturer(em, " "));
            } else {
                // create new manufacturer
                currentProductInspection.setManufacturer(new Manufacturer(getCurrentProductInspection().getManufacturer().getName()));
            }
        }

        setDirty(true);
    }

    /**
     * Update the product distributor field with one that exists in the
     * database. If the distributor does not exist in the database then create a
     * new distributor object that will be saved by the saveComplianceSurvey()
     * method.
     */
    public void updateProductDistributorForProductInsp() {

        EntityManager em = getEntityManager1();

        Distributor distributor = Distributor.findDistributorByName(em, getCurrentProductInspection().getDistributor().getName());
        if (distributor != null) {
            currentProductInspection.setDistributor(distributor);
        } else {
            if (getCurrentProductInspection().getDistributor().getName().trim().equals("")) {
                currentProductInspection.setDistributor(App.getDefaultDistributor(em, " "));
            } else {
                // create new distributor
                currentProductInspection.setDistributor(new Distributor(getCurrentProductInspection().getDistributor().getName()));
            }
        }

        setDirty(true);
    }

    public void updateComplianceSurveyRetailOutlet(EntityManager em) {

        Client client = Client.findClientByName(em, currentComplianceSurvey.getRetailOutlet().getName(), true);
        if (client != null) {
            currentComplianceSurvey.getRetailOutlet().doCopy(client);
            currentComplianceSurvey.getRetailOutlet().setActive(false);
        }
    }

    public void updateComplianceSurveyRetailOutletRepresentative() {
        try {
            if (!currentComplianceSurvey.getRetailOutlet().getContacts().isEmpty()) {
                for (Contact contact : currentComplianceSurvey.getRetailOutlet().getContacts()) {
                    if (contact.getName().equals(currentComplianceSurvey.getRetailRepresentative().getName().trim())) {
                        currentComplianceSurvey.setRetailRepresentative(contact);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public List<String> completeComplianceSurveyRetailOutletRepresentativeName(String query) {
        ArrayList<String> contactsFound = new ArrayList<>();

        // we need to get the broker so that the current list of contacts can be obtained.
        try {

            for (Contact contact : currentComplianceSurvey.getRetailOutlet().getContacts()) {
                if (contact.getFirstName().toUpperCase().contains(query.trim().toUpperCase())
                        || contact.getLastName().toUpperCase().contains(query.trim().toUpperCase())) {
                    contactsFound.add(contact.getName());
                }
            }

        } catch (Exception e) {
            System.out.println(e);
        }

        return contactsFound;
    }

    public void editComplianceSurveyRetailOutlet() {

        ClientManager clientManager = ComplianceManager.findBean("clientManager");

        clientManager.setClient(currentComplianceSurvey.getRetailOutlet());
        clientManager.setTaxRegistrationNumberRequired(doClientTRNValidation(getEntityManager1()));
        clientManager.setBusinessEntityManager(this);
        clientManager.setExternalEntityManagerFactory(EMF1);
        clientManager.setComponentsToUpdate(":ComplianceSurveyDialogForm:complianceSurveyTabView:retailOutlet");
    }
    // Retail outlet update end

    @Override
    public void setDirty(Boolean dirty) {
        this.dirty = dirty;
    }

    public void updateJob() {
        setDirty(true);
    }

    public void updateInspectionPoint() {
        if (!currentComplianceSurvey.getInspectionPoint().toUpperCase().equals("OTHER")) {
            currentComplianceSurvey.setOtherInspectionLocation("");
        }

        setDirty(true);
    }

    public void updateSurveyLocationType() {
        getCurrentComplianceSurvey().setTypeOfEstablishment("");
        setDirty(true);
    }

    // tk put this in ComplianceSurvey
    public void updateContainerNumbers() {
        // create/save shipping containers if needed
        // save current list of containers for later use       
        List<ShippingContainer> currentShippingContainers = new ArrayList<>();
        for (ShippingContainer shippingContainer : getCurrentComplianceSurvey().getEntryDocumentInspection().getShippingContainers()) {
            currentShippingContainers.add(shippingContainer);
        }


        // Clear and rebuild list of containers
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

        setDirty(true);
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
        if (currentComplianceSurvey.getRequestForDetentionIssuedForPortOfEntry()) {
            currentComplianceSurvey.setDateOfDetention(currentComplianceSurvey.getDateOfSurvey());
        }

        setDirty(true);
    }

    public void updateDailyReportStartDate() {
        //currentComplianceDailyReport.setEndOfPeriod(currentComplianceDailyReport.getStartOfPeriod());
        //endOfPeriod = startOfPeriod;
        setDirty(true);
    }
    
     public void updateDailyReportEndDate() {
        //currentComplianceDailyReport.setEndOfPeriod(currentComplianceDailyReport.getStartOfPeriod());
        //endOfPeriod = startOfPeriod;
        setDirty(true);
    }

    public void updateCountryOfConsignment() {
        setDirty(true);
    }

    public void updateCompanyTypes() {
        if (!currentComplianceSurvey.getOtherCompanyTypes()) {
            currentComplianceSurvey.setCompanyTypes("");
        }
        setDirty(true);
    }

    public void createNewProductInspection() {
        //EntityManager em = getEntityManager1();

        currentComplianceSurvey.setInspector(main.getUser().getEmployee());
        currentProductInspection = new ProductInspection();
        currentProductInspection.setInspector(main.getUser().getEmployee());
        currentProductInspection.setQuantity(0);
        currentProductInspection.setSampleSize(0);
        isNewProductInspection = true;
        setDirty(true);
    }

    public String getSearchResultsTableHeader() {
        return App.getSearchResultsTableHeader(getComplianceSurveys());
    }

    public ComplianceSurvey getCurrentComplianceSurvey() {
        if (currentComplianceSurvey == null) {
            createNewComplianceSurvey();
        }

        // Load the latest survey from the database
        if (isLoadingForm) {
            if (currentComplianceSurvey.getId() != null) {
                EntityManager em = getEntityManager1();
                currentComplianceSurvey = ComplianceSurvey.findComplianceSurveyById(em, currentComplianceSurvey.getId());
                em.refresh(currentComplianceSurvey);
            }
            isLoadingForm = false;
        }

        return currentComplianceSurvey;
    }

    public void setCurrentComplianceSurvey(ComplianceSurvey currentComplianceSurvey) {
        this.currentComplianceSurvey = currentComplianceSurvey;
    }

    public void createNewComplianceSurvey() {
        RequestContext context = RequestContext.getCurrentInstance();
        EntityManager em = getEntityManager1();

        currentComplianceSurvey = new ComplianceSurvey();
        currentComplianceSurvey.setTypeOfPortOfEntry("Seaport");
        currentComplianceSurvey.setSurveyLocationType("Port of Entry");
        currentComplianceSurvey.setTypeOfEstablishment("");
        currentComplianceSurvey.setPortOfEntry("");
        // consignee and rep
        currentComplianceSurvey.setConsignee(new Client("", false));
        currentComplianceSurvey.setConsigneeRepresentative(new Contact(""));
        // retail outlet and rep
        currentComplianceSurvey.setRetailOutlet(new Client("", false));
        currentComplianceSurvey.setRetailRepresentative(new Contact(""));
        // broker and rep
        currentComplianceSurvey.setBroker(new Client("", false));
        currentComplianceSurvey.setBrokerRepresentative(new Contact(""));

        // Document inspection
        currentComplianceSurvey.setEntryDocumentInspection(new EntryDocumentInspection());

        currentComplianceSurvey.getEntryDocumentInspection().setCountryOfConsignment(" ");
        currentComplianceSurvey.setDateOfSurvey(new Date());
        currentComplianceSurvey.setInspector(Employee.findDefaultEmployee(em, "--", "--", true));
        if (main.getUser() != null) {           
            currentComplianceSurvey.setEditedBy(main.getUser().getEmployee());
            currentComplianceSurvey.setDateEdited(new Date());
        }

        isNewComplianceSurvey = true;
        setDirty(false);

        getMain().openDialog(null, "complianceSurveyDialog", true, true, true, 525, 800);

    }

    public void createNewDocumentInspection() {
        RequestContext context = RequestContext.getCurrentInstance();

        currentDocumentInspection = new DocumentInspection();

        currentDocumentInspection.setName(" ");
        currentComplianceSurvey.setConsignee(new Client("", false));
        currentDocumentInspection.setDateOfInspection(new Date());
        if (main.getUser() != null) {
            currentDocumentInspection.setInspector(main.getUser().getEmployee());
        }

        setDirty(false);

        // Mark that connection is live. this will be check in javascript
        // to determine if we still have connection
        context.addCallbackParam("isConnectionLive", true);
    }

    public void saveComplianceSurvey() {
        saveComplianceSurvey(true);
    }

    private void saveComplianceSurvey(Boolean displayAlert) {
        EntityManager em = getEntityManager1();
        RequestContext context = RequestContext.getCurrentInstance();

        try {
            // Validate fields tk testing without validation
            if (!validateComplianceSurvey(displayAlert)) {
                return;
            }
            // Ensure inspector is not null
            Employee inspector = getEmployeeByName(em, currentComplianceSurvey.getInspector().getName());
            if (inspector != null) {
                currentComplianceSurvey.setInspector(inspector);
            } else {
                currentComplianceSurvey.setInspector(App.getDefaultEmployee(em, "--", "--"));
            }

            // Validate fields required for port of entry detention if one was issued
            // NB: This should be in validation code.
            if (currentComplianceSurvey.getRequestForDetentionIssuedForPortOfEntry()) {
                if (!validatePortOfEntryDetentionData(em)) {
                    return;
                } else {
                }
            }

            if (dirty) {
                Employee employee = Employee.findEmployeeById(em, main.getUser().getEmployee().getId());
                currentComplianceSurvey.setDateEdited(new Date());
                currentComplianceSurvey.setEditedBy(employee);

            }
            em.getTransaction().begin();

            // now save survey            
            Long id = BusinessEntityUtils.saveBusinessEntity(em, currentComplianceSurvey);
            em.getTransaction().commit();

            if (id == null) {
                context.addCallbackParam("entitySaved", false);
            } else if (id == 0L) {
                context.addCallbackParam("entitySaved", false);
            } else {
                context.addCallbackParam("entitySaved", true);
                isNewComplianceSurvey = false;
                setDirty(false);
            }

            // Make sure data is fresh from database by reloading it.
            currentComplianceSurvey = ComplianceSurvey.findComplianceSurveyById(em, currentComplianceSurvey.getId());

            System.out.println("Survey saved!");

        } catch (Exception e) {
            //closeEntityManager1();
            context.addCallbackParam("entitySaved", false);
            System.out.println(e);
        }
    }

    public void closeComplianceSurvey() {
        promptToSaveIfRequired();
    }

    public void closeDocumentInspection() {
        promptToSaveIfRequired();
    }

    public void okProductInspection(ActionEvent actionEvent) {
        RequestContext context = RequestContext.getCurrentInstance();

        try {
            if (activeTabTitle.equals("Compliance Survey")) {
                // validate          
                if (currentProductInspection.getName().trim().equals("")) {
                    context.execute("longProcessDialogVar.hide();requiredValuesAlertDialog.show();");
                    return;
                } else {
                    context.execute("productDialog.hide();longProcessDialogVar.hide();productDialogTabViewVar.select(0);");
                }

                if (isNewProductInspection) {
                    currentComplianceSurvey.getProductInspections().add(currentProductInspection);
                    isNewProductInspection = false;
                }

                // Set or update inspector
                currentProductInspection.setInspector(main.getUser().getEmployee());
                currentComplianceSurvey.setInspector(main.getUser().getEmployee());

                saveComplianceSurvey(false);
            }
        } catch (Exception e) {
            System.out.println(e);
            context.execute("productDialog.hide();longProcessDialogVar.hide();productDialogTabViewVar.select(0);");
        }

    }

    public void removeProductInspection(ActionEvent event) {
        if (activeTabTitle.equals("Compliance Survey")) {
            currentComplianceSurvey.getProductInspections().remove(currentProductInspection);
            currentProductInspection = new ProductInspection();
        }

        setDirty(true);

    }

    public String getLatestAlert() {
        return "*********** TOYS R US BABY STROLLER MODEL # 3213 **********";
    }

    private void promptToSaveIfRequired() {
        if (dirty) {
            getMain().displayCommonConfirmationDialog(initDialogActionHandlerId("surveyDirty"), "This survey was modified. Do you wish to save it?", "Survey Not Saved", "info");
        } else {
//            RequestContext context = RequestContext.getCurrentInstance();
//            context.execute("ComplianceSurveyDialog.hide();");
            RequestContext.getCurrentInstance().closeDialog(null);
        }
    }

    public List<SelectItem> getProductStatus() {

        return App.getStringListAsSelectItems(getEntityManager1(), "productStatusList");

    }

    public List<SelectItem> getShippingContainerDetainPercentages() {

        return App.getStringListAsSelectItems(getEntityManager1(), "shippingContainerPercentageList");

    }

    public List<SelectItem> getCountries() {
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
//
//    // tk used? remove if not
//    public List<SelectItem> getReportNames() {
//        ArrayList names = new ArrayList();
//
//        names.add(new SelectItem("Daily Report", "Daily Report"));
//
//        return names;
//    }

    public List<SelectItem> getTypesOfPortOfEntry() {
        return App.getStringListAsSelectItems(getEntityManager1(), "portOfEntryTypeList");
    }

    public List<SelectItem> getDocumentStamps() {
        ArrayList stamps = new ArrayList();
        stamps.add(new SelectItem("", ""));
        stamps.addAll(App.getStringListAsSelectItems(getEntityManager1(), "portOfEntryDocumentStampList"));

        return stamps;
    }

    public List<SortableSelectItem> getPortsOfEntry() {
        ArrayList ports = new ArrayList();


        switch (getCurrentComplianceSurvey().getTypeOfPortOfEntry()) {
            case "Airport":
                //ports.add(new SortableSelectItem("-- N/A --", "-- N/A --"));
                ports.addAll(App.getStringListAsSortableSelectItems(getEntityManager1(), "airportList"));
                break;
            case "Seaport":
                //ports.add(new SortableSelectItem("-- N/A --", "-- N/A --"));              
                ports.addAll(App.getStringListAsSortableSelectItems(getEntityManager1(), "complianceSurveyMiscellaneousInspectionPointList"));
                ports.addAll(App.getStringListAsSortableSelectItems(getEntityManager1(), "seaportList"));
                ports.addAll(App.getStringListAsSortableSelectItems(getEntityManager1(), "complianceSurveyAirportInspectionPointList"));
                break;
        }


        Collections.sort(ports);
        ports.add(0, new SortableSelectItem("-- N/A --", "-- N/A --"));
        ports.add(0, new SortableSelectItem("", ""));

        return ports;
    }

    public List<SortableSelectItem> getInspectionPoints() {
        ArrayList points = new ArrayList();

        points.add(new SortableSelectItem("Stripping Station", "Stripping Station"));

        points.addAll(App.getStringListAsSortableSelectItems(getEntityManager1(), "complianceSurveyMiscellaneousInspectionPointList"));
        points.addAll(App.getStringListAsSortableSelectItems(getEntityManager1(), "seaportList"));
        points.addAll(App.getStringListAsSortableSelectItems(getEntityManager1(), "complianceSurveyAirportInspectionPointList"));

        Collections.sort(points);
        points.add(0, new SortableSelectItem("", ""));
        points.add(points.size() - 1, new SortableSelectItem("Other", "Other"));

        return points;
    }

    public List<SelectItem> getSurveyTypes() {
        ArrayList types = new ArrayList();

        types.add(new SelectItem(" ", " "));
        types.addAll(App.getStringListAsSelectItems(getEntityManager1(), "complianceSurveyTypes"));

        return types;
    }

    public List<SelectItem> getSurveyLocationTypes() {
        ArrayList types = new ArrayList();

        types.addAll(App.getStringListAsSelectItems(getEntityManager1(), "complianceSurveyLocationTypes"));

        return types;
    }

    public List<SelectItem> getProductCategories() {
        ArrayList types = new ArrayList();

        types.add(new SelectItem("", ""));
        List<Category> categories = Category.findCategoriesByType(getEntityManager1(), "Product");
        for (Category category : categories) {
            types.add(new SelectItem(category.getName(), category.getName()));
        }

        return types;
    }

    public List getTypesOfEstablishment() {
        ArrayList types = new ArrayList();

        types.add(new SelectItem(" ", " "));
        switch (getCurrentComplianceSurvey().getSurveyLocationType()) {
            case "Site":
                types.addAll(App.getStringListAsSelectItems(getEntityManager1(), "siteTypesOfEstablishment"));
                break;
            case "Commercial Marketplace":
                types.addAll(App.getStringListAsSelectItems(getEntityManager1(), "commercialTypesOfEstablishment"));
                break;
        }

        return types;
    }

    public List<SelectItem> getSampleSources() {

        return App.getStringListAsSelectItems(getEntityManager1(), "complianceSampleSources");

    }

    public List<Employee> completeEmployee(String query) {

        try {
            List<Employee> employees = Employee.findEmployeesByName(getEntityManager1(), query);
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

    // tk remove this and use the one in Application?
    private Employee getEmployeeByName(EntityManager em, String name) {
        String names[] = name.split(",");
        if (names.length == 2) {
            return Employee.findEmployeeByName(em,
                    names[1].trim(),
                    names[0].trim());
        } else {
            return null;
        }
    }

    public void updateComplianceSurveyInspector() {
        EntityManager em = getEntityManager1();

        currentComplianceSurvey.setInspector(getEmployeeByName(em, currentComplianceSurvey.getInspector().getName()));

        setDirty(true);
    }

    public void updateDocumentInspectionInspector() {
        EntityManager em = getEntityManager1();

        currentDocumentInspection.setInspector(getEmployeeByName(em, currentDocumentInspection.getInspector().getName()));

        setDirty(true);
    }

    public void onComplianceSurveyTabChange(TabChangeEvent event) {
        if (isDirty()) {
            saveComplianceSurvey(false);
        }
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

    public String logout() {
        RequestContext context = RequestContext.getCurrentInstance();

        main.setUserLoggedIn(false);
        main.setShowLogin(true);
        main.setPassword("");
        main.setUsername("");
        searchText = "";
        reportSearchText = "";
        searchType = "General";

        if (dirty) {
            saveComplianceSurvey();
        }
        main.setLogonMessage("Please provide your login details below:");
        main.setUser(new JobManagerUser());

        context.execute("PrimeFaces.changeTheme('" + main.getUser().getUserInterfaceThemeName() + "');");

        return "logout";
    }

    /**
     * This should automatically log out the user if implemented. This will be
     * made a system option.
     */
    public void autoLogout() {
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
        if (searchText == null) {
            searchText = "";
        }
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

    public void login() {
        EntityManager em = getEntityManager1();
        RequestContext context = RequestContext.getCurrentInstance();

        main.setUserLoggedIn(false);

        try {
            if ((main.getUsername() != null) && (main.getPassword() != null)) {
                if ((main.getUsername().trim().equals("")) || (main.getPassword().trim().equals(""))) {
                    main.setLogonMessage("Invalid username or password! Please try again.");
                    main.setUsername("");
                    main.setPassword("");
                } else {
                    if (main.validateAndAssociateUser(em, main.getUsername(), main.getPassword())) {
//                    if (true) { // used for testing
                        main.setUser(JobManagerUser.findJobManagerUserByUsername(em, main.getUsername()));
                        if (main.getUser() != null) {
                            em.refresh(main.getUser());
                            main.setUserLoggedIn(true);

                            context.execute("PrimeFaces.changeTheme('" + main.getUser().getUserInterfaceThemeName() + "');");
                        } else {
                            main.setLogonMessage("Invalid username or password! Please try again.");
                            main.setUsername("");
                            main.setPassword("");
                        }
                    } else {
                        main.setLogonMessage("Invalid username or password! Please try again.");
                        main.setUsername("");
                        main.setPassword("");
                    }
                }
            } else {
                main.setLogonMessage("Invalid username or password! Please try again.");
                main.setUsername("");
                main.setPassword("");
            }

            // wrap up
            if (main.getUserLoggedIn()) {
                main.setShowLogin(false);
                main.setUsername("");
                main.setPassword("");
                if (context != null) {
                    context.addCallbackParam("userLoggedIn", main.getUserLoggedIn());
                }

            } else {
                main.setLogonMessage("Invalid username or password! Please try again.");
                main.setUsername("");
                main.setPassword("");
            }
        } catch (Exception e) {
            main.setLogonMessage("Login error occured! Please try again or contact the Database Administrator");
            main.setUsername("");
            main.setPassword("");
        }
    }

    public void doEntitySearch(SearchParameters currentSearchParameters) {

        System.out.println("doing compliance search..."); //tk

//        RequestContext context = RequestContext.getCurrentInstance();
//        this.currentSearchParameters = currentSearchParameters;
//
//        if (main.getUserLoggedIn()) {
//            complianceSurveys = ComplianceSurvey.findComplianceSurveysByDateSearchField(getEntityManager1(),
//                    main.getUser(),
//                    currentSearchParameters.getDateField(),
//                    currentSearchParameters.getSearchType(),
//                    currentSearchParameters.getSearchText(),
//                    currentSearchParameters.getDatePeriod().getStartDate(),
//                    currentSearchParameters.getDatePeriod().getEndDate(), true);
//
//        } else {
//            complianceSurveys = new ArrayList<>();
//        }
//
//        context.update("mainTabViewForm:mainTabView:complianceSurveysTable");
//
//        context.addCallbackParam("isConnectionLive", true);
        RequestContext context = RequestContext.getCurrentInstance();

        if (activeTabTitle.equals("Compliance Survey")) {
            if (currentSearchParameters.getSearchText().trim().length() >= 3) {
                if (main.getUserLoggedIn()) {
                    complianceSurveys = ComplianceSurvey.findComplianceSurveysByDateSearchField(
                            getEntityManager1(),
                            main.getUser(),
                            currentSearchParameters.getDateField(),
                            currentSearchParameters.getSearchType(),
                            currentSearchParameters.getSearchText(),
                            null,
                            null,
                            !surveysWithProductInspection);
                    if (complianceSurveys.isEmpty()) {
                        complianceSurveys = ComplianceSurvey.findComplianceSurveysByDateSearchField(
                                getEntityManager1(),
                                main.getUser(),
                                currentSearchParameters.getDateField(),
                                currentSearchParameters.getSearchType(),
                                currentSearchParameters.getSearchText(),
                                null,
                                null,
                                surveysWithProductInspection);
                    }

                } else {
                    complianceSurveys = new ArrayList<>();
                }
            } else {
                getMain().displayCommonMessageDialog(null,
                        "Search text must be a minimum of 3 characters and a maximum of 20 characters.",
                        "Search Text Too Small", "info");
            }

            context.update("mainTabViewForm:mainTabView:complianceSurveysTable");
        }

        context.addCallbackParam("isConnectionLive", true);


    }

    public void doEntitySearch() {

        RequestContext context = RequestContext.getCurrentInstance();

        if (activeTabTitle.equals("Compliance Survey")) {
            if (getSearchText().trim().length() >= 3) {
                if (main.getUserLoggedIn()) {
                    complianceSurveys = ComplianceSurvey.findComplianceSurveysByDateSearchField(
                            getEntityManager1(),
                            main.getUser(),
                            dateSearchField,
                            "General",
                            searchText,
                            null,
                            null,
                            !surveysWithProductInspection);
                    if (complianceSurveys.isEmpty()) {
                        complianceSurveys = ComplianceSurvey.findComplianceSurveysByDateSearchField(
                                getEntityManager1(),
                                main.getUser(),
                                dateSearchField,
                                "General",
                                searchText,
                                null,
                                null,
                                surveysWithProductInspection);
                    }

                } else {
                    complianceSurveys = new ArrayList<>();
                }
            } else {
                getMain().displayCommonMessageDialog(null,
                        "Search text must be a minimum of 3 characters and a maximum of 20 characters.",
                        "Search Text Too Small", "info");
            }

            context.update("mainTabViewForm:mainTabView:complianceSurveysTable");
        }

        context.addCallbackParam("isConnectionLive", true);
    }

    public List<String> completeSearchText(String query) {
        List<String> suggestions = new ArrayList<>();

        // NB: This is only an example implementation
        // based on the current code based, the following code is never called
        if (searchType.equals("?")) {
            // add suggestion strings to the suggestions list
        }

        return suggestions;
    }

    public void handleKeepAlive() {
        EntityManager em = getEntityManager1();

        System.out.println("Handling keep alive session: doing polling for CompDB..." + new Date());
        em.getTransaction().begin();
        main.setUser(JobManagerUser.findJobManagerUserByUsername(em, main.getUser().getUsername()));

        main.getUser().setPollTime(new Timestamp(new Date().getTime()));

        for (JobManagerUser u : JobManagerUser.findAllJobManagerUsers(em)) {
            em.refresh(u);
        }
        BusinessEntityUtils.saveBusinessEntity(em, main.getUser());

        em.getTransaction().commit();
    }

    public String getTablesToUpdateAfterSearch() {

        return ":mainTabViewForm:mainTabView:complianceSurveysTable,:mainTabViewForm:mainTabView:documentInspectionsTable";
    }

    public String getProductTablesToUpdate() {
        return ":ComplianceSurveyDialogForm:complianceSurveyTabView:marketProductsTable";
    }

    public void handleProductPhotoFileUpload(FileUploadEvent event) {
        FileOutputStream fout;
        UploadedFile upLoadedFile = event.getFile();
        String baseURL = SystemOption.findSystemOptionByName(getEntityManager1(), "productImageUploadLocation").getOptionValue();
        String upLoadedFileName = main.getUser().getId() + "_"
                + new Date().getTime() + "_"
                + upLoadedFile.getFileName();

        String imageURL = baseURL + "\\" + upLoadedFileName;

        try {
            fout = new FileOutputStream(imageURL);
            fout.write(upLoadedFile.getContents());
            fout.close();

            getCurrentProductInspection().setImageURL(upLoadedFileName);
            setDirty(true);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void editForm() {
        RequestContext context = RequestContext.getCurrentInstance();

        isLoadingForm = true;

        setComponentsToUpdate(":"
                + getComplianceSurveyTableToUpdate()
                + ",:ComplianceSurveyDialogForm:complianceSurveyToolsMenuButton,:ComplianceSurveyDialogForm:complianceSurveyToolsMenuButton2");

        context.addCallbackParam("isConnectionLive", true);
    }

    public void editComplianceSurvey() {
        getMain().openDialog(null, "complianceSurveyDialog", true, true, true, 525, 800);
    }

    @SuppressWarnings("unchecked")
    public static <T> T findBean(String beanName) {
        FacesContext context = FacesContext.getCurrentInstance();
        return (T) context.getApplication().evaluateExpressionGet(context, "#{" + beanName + "}", Object.class);
    }

    public Boolean getComplianceSurveyIsValid() {
        if (getCurrentComplianceSurvey().getId() != null) {
            return true;
        } else if (dirty) {
            return true;
        }

        return false;
    }

    public Boolean getProductInspectionImageIsValid() {
        if (!getCurrentProductInspection().getImageURL().isEmpty()) {
            return true;
        }

        return false;
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
//    public StreamedContent getCurrentProductInspectionImageDownload() {
//        EntityManager em = getEntityManager1();
//        StreamedContent streamedFile = null;
//
//        // tk test gen report
//        HashMap parameters = new HashMap();
//
//        Connection con = BusinessEntityUtils.establishConnection(
//                SystemOption.findSystemOptionByName(em, "defaultDatabaseDriver").getOptionValue(),
//                SystemOption.findSystemOptionByName(em, "defaultDatabaseURL").getOptionValue(),
//                SystemOption.findSystemOptionByName(em, "defaultDatabaseUsername").getOptionValue(),
//                SystemOption.findSystemOptionByName(em, "defaultDatabasePassword").getOptionValue());  // make sys
//        if (con != null) {
//            System.out.println("connected!");
//            String reportFileURL = "//bosprintsrv/c$/uploads/templates/DetentionRequest.jasper";
//            try {
//                parameters.put("formId", 178153L);
//                JasperPrint print = JasperFillManager.fillReport(reportFileURL, parameters, con);
////                String str = JasperFillManager.fillReportToFile(reportFileURL, parameters, con);
//
////                File file = new File("Detention.pdf");
////                FileOutputStream out = new FileOutputStream(file);
////
////                //JasperExportManager.exportReportToPdfStream(print, out);
////
////                out.close();
//            } catch (JRException ex) {
//                System.out.println(ex);
//            }
////            catch (FileNotFoundException ex1) {
////                System.out.println(ex1);
////            } catch (IOException ex2) {
////                System.out.println(ex2);
////            }
//        } else {
//            System.out.println("not connected!");
//        }
//
//
//        //String baseURL = "\\\\bosprintsrv\\c$\\uploads\\images\11153_1359128328029_print-file.png";
////        String baseURL = "C:\\Projects\\images\\11153_1359128328029_print-file.png"; // \\boshrmapp\c$\glassfishv3\images
//        String baseURL = "C:\\glassfishv3\\images\\11153_1359128328029_print-file.png";
//
//        //InputStream stream = ((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream(baseURL);  
//        try {
//
//            FileInputStream stream = new FileInputStream(baseURL);
//            streamedFile = new DefaultStreamedContent(stream, "image/png", "downloaded.png");
//
//        } catch (Exception ex) {
//            System.out.println(ex);
//        }
//
//        return streamedFile;
//    }
    public static void main(String[] args) {
        String test = "";

        String[] test2 = test.split("[,;:|/]");
        System.out.println("Len: " + test2.length);
        for (int i = 0; i < test2.length; i++) {
            System.out.println(test2[i]);
        }
    }

    public StreamedContent getDetentionRequestFile() {

        EntityManager em = getEntityManager1();
        HashMap parameters = new HashMap();

        updateComplianceSurvey(em);
        // set parameters
        parameters.put("formId", currentComplianceSurvey.getId().longValue());

        Client broker = App.getActiveClientByNameIfAvailable(em, currentComplianceSurvey.getBroker());
        Client consignee = App.getActiveClientByNameIfAvailable(em, currentComplianceSurvey.getConsignee());

        // broker detail
        //Client broker = jm.getClientByName(em, currentComplianceSurvey.getBroker().getName());
        //Contact brokerRep = jm.getContactByName(em, username, username);
        parameters.put("brokerDetail", broker.getName() + "\n"
                + broker.getBillingAddress()
                + BusinessEntityUtils.getContactTelAndFax(broker.getMainContact()));

        //Consignee detail
        //Client consignee = jm.getClientByName(em, currentComplianceSurvey.getConsignee().getName());
        parameters.put("consigneeDetail", consignee.getName() + "\n"
                + consignee.getBillingAddress()
                + BusinessEntityUtils.getContactTelAndFax(consignee.getMainContact()));

        parameters.put("products", getComplianceSurveySampledProductNames());
        parameters.put("quantity", getComplianceSurveySampledProductQuantities());
        parameters.put("numberOfSamplesTaken", getComplianceSurveyProductTotalSampleSize());

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

        updateComplianceSurvey(em);

        Client consignee = App.getActiveClientByNameIfAvailable(em, currentComplianceSurvey.getConsignee());

        // specified release location               
        parameters.put("specifiedReleaseLocation", currentComplianceSurvey.getSpecifiedReleaseLocation().getName() + "\n"
                + currentComplianceSurvey.getSpecifiedReleaseLocation().toString());

        // Consignee   
        parameters.put("consigneeDetail", consignee.getName() + "\n" + consignee.getBillingAddress().toString());

        parameters.put("products", getComplianceSurveySampledProductNames());
        //parameters.put("numberOfSamplesTaken", getComplianceSurveyProductTotalSampleSize());

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

        updateComplianceSurvey(em);

        Client broker = App.getActiveClientByNameIfAvailable(em, currentComplianceSurvey.getBroker());
        Client consignee = App.getActiveClientByNameIfAvailable(em, currentComplianceSurvey.getConsignee());

        // broker detail (address)
        parameters.put("formId", currentComplianceSurvey.getId().longValue());
        parameters.put("brokerDetail", broker.getName() + "\n"
                + broker.getBillingAddress().toString() + "\n"
                + BusinessEntityUtils.getContactTelAndFax(broker.getMainContact()));

        // consignee detail (address)
        parameters.put("consigneeDetail", consignee.getBillingAddress().toString());

        // consignee contact person
        parameters.put("consigneeContactPerson", BusinessEntityUtils.getContactFullName(currentComplianceSurvey.getConsigneeRepresentative()));

        parameters.put("consigneeTelFaxEmail", BusinessEntityUtils.getMainTelFaxEmail(consignee.getMainContact()));
        parameters.put("products", getComplianceSurveyProductNames());
        parameters.put("quantity", getComplianceSurveyProductQuantitiesAndUnits());
        parameters.put("numberOfSamplesTaken", getComplianceSurveyProductTotalSampleSize());

        // sample disposal
        if (currentComplianceSurvey.getSamplesToBeCollected()) {
            parameters.put("samplesToBeCollected", "\u2713"); // \u2713 is unicode for tick
        } else {
            parameters.put("samplesToBeCollected", "");
        }
        if (currentComplianceSurvey.getSamplesToBeDisposed()) {
            parameters.put("samplesToBeDisposed", "\u2713");
        } else {
            parameters.put("samplesToBeDisposed", "");
        }

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

        updateComplianceSurvey(em);

        Client broker = App.getActiveClientByNameIfAvailable(em, currentComplianceSurvey.getBroker());
        Client consignee = App.getActiveClientByNameIfAvailable(em, currentComplianceSurvey.getConsignee());

        // full release
        if (currentComplianceSurvey.getFullRelease()) {
            parameters.put("fullRelease", "\u2713");
        } else {
            parameters.put("fullRelease", "");
        }

        // retailer, distributor, other?
        if (currentComplianceSurvey.getRetailer()) {
            parameters.put("retailer", "\u2713");
        } else {
            parameters.put("retailer", "");
        }
        if (currentComplianceSurvey.getDistributor()) {
            parameters.put("distributor", "\u2713");
        } else {
            parameters.put("distributor", "");
        }
        if (currentComplianceSurvey.getOtherCompanyTypes()) {
            parameters.put("otherCompanyTypes", "\u2713");
            parameters.put("companyTypes", currentComplianceSurvey.getCompanyTypes());
        } else {
            parameters.put("otherCompanyTypes", "");
            currentComplianceSurvey.setCompanyTypes("");
            parameters.put("companyTypes", currentComplianceSurvey.getCompanyTypes());
        }



        // broker
        parameters.put("formId", currentComplianceSurvey.getId().longValue());
        parameters.put("brokerDetail", broker.getName() + "\n"
                + broker.getBillingAddress().toString() + "\n"
                + BusinessEntityUtils.getContactTelAndFax(broker.getMainContact()));

        // consignee
        parameters.put("consigneeDetail", consignee.getBillingAddress().toString());

        // provisional release location 
        parameters.put("specifiedReleaseLocationDomesticMarket", currentComplianceSurvey.getSpecifiedReleaseLocationDomesticMarket().toString());

        // location of detained products locationOfDetainedProduct 
        parameters.put("locationOfDetainedProduct", currentComplianceSurvey.getLocationOfDetainedProductDomesticMarket().toString());

        // consignee contact person
        parameters.put("consigneeContactPerson", BusinessEntityUtils.getContactFullName(currentComplianceSurvey.getConsigneeRepresentative()));

        // consignee tel/fax/email
        parameters.put("consigneeTelFaxEmail", BusinessEntityUtils.getMainTelFaxEmail(consignee.getMainContact()));

        parameters.put("products", getComplianceSurveyProductNames());
        parameters.put("productBrandNames", getComplianceSurveyProductBrandNames());
        parameters.put("productBatchCodes", getComplianceSurveyProductBatchCodes());
        parameters.put("quantity", getComplianceSurveyProductQuantitiesAndUnits());
        parameters.put("numberOfSamplesTaken", getComplianceSurveyProductTotalSampleSize());

        return getComplianceSurveyFormPDFFile(
                em,
                "noticeOfReleaseFromDetentionForm",
                "release_notice.pdf",
                parameters,
                "DOMESTIC_MARKET_DETENTION");
    }

    public StreamedContent getApplicationForRehabilitationFile() {
        EntityManager em = getEntityManager1();
        HashMap parameters = new HashMap();

        updateComplianceSurvey(em);

        Client broker = App.getActiveClientByNameIfAvailable(em, currentComplianceSurvey.getBroker());
        Client consignee = App.getActiveClientByNameIfAvailable(em, currentComplianceSurvey.getConsignee());

        // broker
        parameters.put("formId", currentComplianceSurvey.getId().longValue());
        parameters.put("brokerDetail", broker.getName() + "\n"
                + broker.getBillingAddress().toString() + "\n"
                + BusinessEntityUtils.getContactTelAndFax(broker.getMainContact()));

        // consignee
        parameters.put("consigneeDetail", consignee.getBillingAddress().toString());

        // consignee contact person
        parameters.put("consigneeContactPerson", BusinessEntityUtils.getContactFullName(currentComplianceSurvey.getConsigneeRepresentative()));

        parameters.put("consigneeTelFaxEmail", BusinessEntityUtils.getMainTelFaxEmail(consignee.getMainContact()));
        parameters.put("products", getComplianceSurveyProductNames());
        parameters.put("quantity", getComplianceSurveyProductQuantitiesAndUnits());
        parameters.put("numberOfSamplesTaken", getComplianceSurveyProductTotalSampleSize());

        // sample disposal
        if (currentComplianceSurvey.getSamplesToBeCollected()) {
            parameters.put("samplesToBeCollected", "\u2713"); // \u2713 is unicode for tick
        } else {
            parameters.put("samplesToBeCollected", "");
        }
        if (currentComplianceSurvey.getSamplesToBeDisposed()) {
            parameters.put("samplesToBeDisposed", "\u2713");
        } else {
            parameters.put("samplesToBeDisposed", "");
        }

        return getComplianceSurveyFormPDFFile(
                em,
                "applicationForRehabilitationForm",
                "appliacation_for_rehab.pdf",
                parameters,
                "PORT_OF_ENTRY_DETENTION");
    }

    public String getComplianceSurveyProductNames() {
        String names = "";

        for (ProductInspection productInspection : currentComplianceSurvey.getProductInspections()) {
            if (names.equals("")) {
                names = productInspection.getName();
            } else {
                names = names + ", " + productInspection.getName();
            }
        }

        return names;
    }

    public String getComplianceSurveySampledProductNames() {
        String names = "";

        for (ProductInspection productInspection : currentComplianceSurvey.getProductInspections()) {
            if (productInspection.getSampledForInvestigation()
                    || productInspection.getSampledForLabelAssessment()
                    || productInspection.getSampledForTesting()) {
                if (names.equals("")) {
                    names = productInspection.getName();
                } else {
                    names = names + ", " + productInspection.getName();
                }
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

    public String getComplianceSurveySampledProductQuantities() {
        String quantitiesAndUnits = "";

        for (ProductInspection productInspection : currentComplianceSurvey.getProductInspections()) {
            if (productInspection.getSampledForInvestigation()
                    || productInspection.getSampledForLabelAssessment()
                    || productInspection.getSampledForTesting()) {
                if (quantitiesAndUnits.equals("")) {
                    quantitiesAndUnits = quantitiesAndUnits + productInspection.getContainerSize() + " (" + productInspection.getQuantity() + ")";
                } else {
                    quantitiesAndUnits = quantitiesAndUnits + ", " + productInspection.getContainerSize() + " (" + productInspection.getQuantity() + ")";
                }
            }
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
        if (dirty) {
            saveComplianceSurvey();
        }
        if (currentComplianceSurvey.getId() != null) {
            currentComplianceSurvey = ComplianceSurvey.findComplianceSurveyById(em, currentComplianceSurvey.getId());
        }
    }

    public Boolean doClientTRNValidation(EntityManager em) {
        String value = SystemOption.findSystemOptionByName(em, "validateComplianceSurveyClientTRN").getOptionValue();

        return Boolean.parseBoolean(value);
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
                        SystemOption.findSystemOptionByName(em, "defaultDatabaseDriver").getOptionValue(),
                        SystemOption.findSystemOptionByName(em, "defaultDatabaseURL").getOptionValue(),
                        SystemOption.findSystemOptionByName(em, "defaultDatabaseUsername").getOptionValue(),
                        SystemOption.findSystemOptionByName(em, "defaultDatabasePassword").getOptionValue());
                if (con != null) {
                    StreamedContent streamContent;

                    String reportFileURL = SystemOption.findSystemOptionByName(em, form).getOptionValue();

                    // make sure is parameter is set for all forms
                    parameters.put("formId", currentComplianceSurvey.getId().longValue());
                    // tk remove and do this when compliance survey is being saved
                    // get and set reference number if it is null
                    switch (sequentialNumberName) {
                        case "PORT_OF_ENTRY_DETENTION":
                            if (currentComplianceSurvey.getPortOfEntryDetentionNumber() == null) {
                                em.getTransaction().begin();
                                int year = BusinessEntityUtils.getCurrentYear();
                                currentComplianceSurvey. // tk BSJ-D42- to be made option?
                                        setPortOfEntryDetentionNumber("BSJ-D42-" + year + "-"
                                        + BusinessEntityUtils.getFourDigitString(SequenceNumber.findNextSequentialNumberByNameAndByYear(em, sequentialNumberName, year)));
                                setDirty(dirty);
                                updateComplianceSurvey(em);
                                em.getTransaction().commit();

                            }
                            //parameters.put("referenceNumber", currentComplianceSurvey.getReferenceNumber());
                            break;
                        case "DOMESTIC_MARKET_DETENTION":
                            if (currentComplianceSurvey.getDomesticMarketDetentionNumber() == null) {
                                em.getTransaction().begin();
                                int year = BusinessEntityUtils.getCurrentYear();
                                currentComplianceSurvey. // tk BSJ-D42- to be made option?
                                        setDomesticMarketDetentionNumber("BSJ-DM42-" + year + "-"
                                        + BusinessEntityUtils.getFourDigitString(SequenceNumber.findNextSequentialNumberByNameAndByYear(em, sequentialNumberName, year)));
                                setDirty(dirty);
                                updateComplianceSurvey(em);
                                em.getTransaction().commit();

                            }
                            break;
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
                    SystemOption.findSystemOptionByName(em, "defaultDatabaseDriver").getOptionValue(),
                    SystemOption.findSystemOptionByName(em, "defaultDatabaseURL").getOptionValue(),
                    SystemOption.findSystemOptionByName(em, "defaultDatabaseUsername").getOptionValue(),
                    SystemOption.findSystemOptionByName(em, "defaultDatabasePassword").getOptionValue());  // tk make system option
            if (con != null) {
                StreamedContent streamContent;

                String reportFileURL = SystemOption.findSystemOptionByName(em, "complianceDailyReport").getOptionValue();

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

        updateComplianceSurvey(em);

        Client broker = App.getActiveClientByNameIfAvailable(em, currentComplianceSurvey.getBroker());
        Client consignee = App.getActiveClientByNameIfAvailable(em, currentComplianceSurvey.getConsignee());

        // retailer, distributor, other?
        if (currentComplianceSurvey.getRetailer()) {
            parameters.put("retailer", "\u2713");
        } else {
            parameters.put("retailer", "");
        }
        if (currentComplianceSurvey.getDistributor()) {
            parameters.put("distributor", "\u2713");
        } else {
            parameters.put("distributor", "");
        }
        if (currentComplianceSurvey.getOtherCompanyTypes()) {
            parameters.put("otherCompanyTypes", "\u2713");
            parameters.put("companyTypes", currentComplianceSurvey.getCompanyTypes());
        } else {
            parameters.put("otherCompanyTypes", "");
            currentComplianceSurvey.setCompanyTypes("");
            parameters.put("companyTypes", currentComplianceSurvey.getCompanyTypes());
        }

        // broker
        parameters.put("formId", currentComplianceSurvey.getId().longValue());
        parameters.put("brokerDetail", broker.getName() + "\n"
                + broker.getBillingAddress().toString() + "\n"
                + BusinessEntityUtils.getContactTelAndFax(broker.getMainContact()));

        // consignee
        parameters.put("consigneeDetail", consignee.getBillingAddress().toString());

        // provisional release location 
        parameters.put("specifiedReleaseLocationDomesticMarket", currentComplianceSurvey.getSpecifiedReleaseLocationDomesticMarket().toString());

        // location of detained products locationOfDetainedProduct 
        parameters.put("locationOfDetainedProduct", currentComplianceSurvey.getLocationOfDetainedProductDomesticMarket().toString());

        // consignee tel/fax/email
        parameters.put("consigneeTelFaxEmail", BusinessEntityUtils.getMainTelFaxEmail(consignee.getMainContact()));

        parameters.put("products", getComplianceSurveyProductNames());
        parameters.put("productBrandNames", getComplianceSurveyProductBrandNames());
        parameters.put("productBatchCodes", getComplianceSurveyProductBatchCodes());
        parameters.put("quantity", getComplianceSurveyProductQuantitiesAndUnits());
        parameters.put("numberOfSamplesTaken", getComplianceSurveyProductTotalSampleSize());

        if (samplesTaken()) {
            parameters.put("samplesTaken", "\u2713");
        } else {
            parameters.put("samplesTaken", "");
        }

        return getComplianceSurveyFormPDFFile(
                em,
                "noticeOfDetentionForm",
                "detention_notice.pdf",
                parameters,
                "DOMESTIC_MARKET_DETENTION");
    }

    @Override
    public Boolean isDirty() {
        return dirty;
    }

    public Boolean validatePortOfEntryDetentionData(EntityManager em) {
        if (Job.findJobByJobNumber(em, currentComplianceSurvey.getJobNumber()) == null) {
            getMain().displayCommonMessageDialog(null, "A valid job number is required if a detention request is issued.", "Job Number Required", "info");
            return false;
        }
        if (currentComplianceSurvey.getBroker().getName().trim().equals("")) {
            getMain().displayCommonMessageDialog(null, "The broker name is required if a detention request is issued.", "Broker Required", "info");
            return false;
        }

        if (currentComplianceSurvey.getDateOfDetention() == null) {
            getMain().displayCommonMessageDialog(null, "The date of the detention is required if a detention request is issued.", "Date of Detention Required", "info");
            return false;
        }
        if (currentComplianceSurvey.getReasonForDetention().trim().equals("")) {
            getMain().displayCommonMessageDialog(null, "The reason for the detention is required if a detention request is issued.", "Reason for Detention Required", "info");
            return false;
        }

        return true;
    }

    private Boolean validateComplianceSurvey(Boolean displayAlert) {
        //EntityManager em = getEntityManager1();
        RequestContext context = RequestContext.getCurrentInstance();

        try {

            // Needed for validation purposes
            ClientManager clientManager = ComplianceManager.findBean("clientManager");

            // Validate survey fields based on survey location type
            if (currentComplianceSurvey.getSurveyLocationType().trim().equals("")) {
                if (displayAlert) {
                    context.execute("requiredValuesAlertDialog.show();");
                }
                return false;
            } else if (currentComplianceSurvey.getSurveyLocationType().equals("Port of Entry")
                    || currentComplianceSurvey.getSurveyLocationType().equals("Site")) {
                // Validate port of entry
//                if (currentComplianceSurvey.getPortOfEntry().trim().equals("")) {
//                    if (displayAlert) {
//                        context.execute("requiredValuesAlertDialog.show();");
//                    }
//                    return false;
//                }

                // validate consignee               
                clientManager.setClient(currentComplianceSurvey.getConsignee());
                clientManager.setTaxRegistrationNumberRequired(doClientTRNValidation(getEntityManager1()));
                if (!clientManager.validateClient(displayAlert)) {
                    return false;
                }

            } // Valid consignee if required. 
            else if (currentComplianceSurvey.getSurveyLocationType().equals("Commercial Marketplace")) {
                // Validate type of establishment
                if (currentComplianceSurvey.getTypeOfEstablishment().trim().equals("")) {
                    if (displayAlert) {
                        context.execute("requiredValuesAlertDialog.show();");
                    }
                    return false;
                }
                // Validate retail outlet
                if (currentComplianceSurvey.getRetailOutlet().getName().trim().equals("")) {
                    if (displayAlert) {
                        context.execute("requiredValuesAlertDialog.show();");
                    }
                    return false;
                }
            }

            // Validate date of survey
            if (currentComplianceSurvey.getDateOfSurvey() == null) {
                if (displayAlert) {
                    context.execute("requiredValuesAlertDialog.show();");
                }
                return false;
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        return true;
    }

    @Override
    public void handleDialogOkButtonClick() {
    }

    @Override
    public void handleDialogYesButtonClick() {
        if (dialogActionHandlerId.equals("surveyDirty")) {
            saveComplianceSurvey();
            RequestContext.getCurrentInstance().closeDialog(null);
//            RequestContext context = RequestContext.getCurrentInstance();
//            context.execute("ComplianceSurveyDialog.hide();");
        }
    }

    @Override
    public void handleDialogNoButtonClick() {
        if (dialogActionHandlerId.equals("surveyDirty")) {
            setDirty(false);
            RequestContext.getCurrentInstance().closeDialog(null);
//            RequestContext context = RequestContext.getCurrentInstance();
//            context.execute("ComplianceSurveyDialog.hide();");
        }
    }

    @Override
    public void handleDialogCancelButtonClick() {
    }

    @Override
    public DialogActionHandler initDialogActionHandlerId(String id) {
        dialogActionHandlerId = id;
        return this;
    }

    @Override
    public String getDialogActionHandlerId() {
        return dialogActionHandlerId;
    }

    public List<String> getAllDocumentStandardNames() {
        EntityManager em = getEntityManager1();

        List<String> names = new ArrayList<>();

        List<DocumentStandard> standards = DocumentStandard.findAllDocumentStandards(em);
        for (DocumentStandard documentStandard : standards) {
            names.add(documentStandard.getName());
        }

        return names;
    }

    public List<String> getAllShippingContainers() {
        return getCurrentComplianceSurvey().getEntryDocumentInspection().getContainerNumberList();
    }

    public void updateSpecificationsBreached() {
        for (String name : getSelectedStandardNames()) {
            if (currentComplianceSurvey.getReasonForDetention().indexOf(name) == -1) {
                currentComplianceSurvey.
                        setReasonForDetention(currentComplianceSurvey.getReasonForDetention() + " " + name);
                setDirty(true);
            }
        }
    }

    public void updateProductShippingContainers() {
        for (String containerNumber : getSelectedContainerNumbers()) {
            if (getCurrentProductInspection().getContainerNumber().indexOf(containerNumber) == -1) {
                if (getCurrentProductInspection().getContainerNumber().isEmpty()) {
                    getCurrentProductInspection().setContainerNumber(containerNumber);
                    setDirty(true);
                } else {
                    getCurrentProductInspection().
                            setContainerNumber(getCurrentProductInspection().getContainerNumber().
                            concat(", ").concat(containerNumber));
                    setDirty(true);
                }
            }
        }

        System.out.println("prod cont: " + getCurrentProductInspection().getContainerNumber());
    }
    
     public List getReportNames() {
        ArrayList names = new ArrayList();

        names.add(new SelectItem("Daily Report", "Daily Report"));

        return names;
    }
}
