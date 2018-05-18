/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.com.dpbennett.jobmanagementlibrary;

import com.lowagie.text.xml.simpleparser.EntitiesToSymbol;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import jm.com.dpbennett.entity.Business;
import jm.com.dpbennett.entity.BusinessEntityManager;
import jm.com.dpbennett.entity.BusinessOffice;
import jm.com.dpbennett.entity.Client;
import jm.com.dpbennett.entity.CompanyRegistration;
import jm.com.dpbennett.entity.ComplianceDailyReport;
import jm.com.dpbennett.entity.ComplianceSurvey;
import jm.com.dpbennett.entity.Contact;
import jm.com.dpbennett.entity.Country;
import jm.com.dpbennett.entity.Distributor;
import jm.com.dpbennett.entity.DocumentInspection;
import jm.com.dpbennett.entity.Employee;
import jm.com.dpbennett.entity.JobManagerUser;
import jm.com.dpbennett.entity.Manufacturer;
import jm.com.dpbennett.entity.ProductInspection;
import jm.com.dpbennett.entity.SampleRequest;
import jm.com.dpbennett.entity.SequenceNumber;
import jm.com.dpbennett.entity.ShippingContainer;
import jm.com.dpbennett.entity.SystemOption;
import jm.com.dpbennett.utils.BusinessEntityUtils;
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
 * @author dbennett
 */
public class ComplianceManager implements Serializable, BusinessEntityManager {

    @PersistenceUnit(unitName = "JMTSWebAppPU")
    private EntityManagerFactory EMF1;
    private EntityManager entityManager1;
    private ComplianceSurvey currentComplianceSurvey;
    private ProductInspection currentProductInspection;
    private SampleRequest currentSampleRequest;
    private CompanyRegistration currentCompanyRegistration;
    private JobManagement jm;
    private UserManagement userManagement;
    private Boolean dirty;
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
//    private String complianceSurveyBrokerName;
//    private String complianceSurveyConsigneeName;
//    private String complianceSurveyRetailOutletName;

    /**
     * Creates a new instance of ComplianceManager
     */
    public ComplianceManager() {
        jm = new JobManagement();
        complianceSurveys = new ArrayList<ComplianceSurvey>();
        documentInspections = new ArrayList<DocumentInspection>();
        dirty = false;
        dateSearchField = "dateFirstReceived";
        searchText = "";
        searchType = "General";
        dateSearchPeriod = "This month";
        reportPeriod = "This month";
        activeTabTitle = "Compliance Survey";
        currentComplianceDailyReport =
                new ComplianceDailyReport("Report-" + new Date().toString(),
                new Date(), "Berth 11", " ");
    }

//    public String getComplianceSurveyBrokerName() {
//        return complianceSurveyBrokerName;
//    }
//
//    public void setComplianceSurveyBrokerName(String complianceSurveyBrokerName) {
//        this.complianceSurveyBrokerName = complianceSurveyBrokerName;
//    }
//
//    public String getComplianceSurveyConsigneeName() {
//        return complianceSurveyConsigneeName;
//    }
//
//    public void setComplianceSurveyConsigneeName(String complianceSurveyConsigneeName) {
//        this.complianceSurveyConsigneeName = complianceSurveyConsigneeName;
//    }
//
//    public String getComplianceSurveyRetailOutletName() {
//        return complianceSurveyRetailOutletName;
//    }
//
//    public void setComplianceSurveyRetailOutletName(String complianceSurveyRetailOutletName) {
//        this.complianceSurveyRetailOutletName = complianceSurveyRetailOutletName;
//    }
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

        setDirty(true);
    }

    public void updateInspectorSigForSampleRequestPOE() {

        if (currentComplianceSurvey.getInspectorSigForSampleRequestPOE().getId() == null) {
            currentComplianceSurvey.setInspectorSigDateForSampleRequestPOE(new Date());
            currentComplianceSurvey.setInspectorSigForSampleRequestPOE(getUserManagement().getUser().getEmployee().getSignature());
        } else {
            currentComplianceSurvey.setInspectorSigDateForSampleRequestPOE(null);
            currentComplianceSurvey.setInspectorSigForSampleRequestPOE(null);
        }

        setDirty(true);
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

        setDirty(true);
    }

    public void updateAuthSigForNoticeOfDentionDM() {

        if (currentComplianceSurvey.getAuthSigForNoticeOfDentionDM().getId() == null) {
            currentComplianceSurvey.setAuthSigDateForNoticeOfDentionDM(new Date());
            currentComplianceSurvey.setAuthSigForNoticeOfDentionDM(getUserManagement().getUser().getEmployee().getSignature());
        } else {
            currentComplianceSurvey.setAuthSigDateForNoticeOfDentionDM(null);
            currentComplianceSurvey.setAuthSigForNoticeOfDentionDM(null);
        }

        setDirty(true);
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

        setDirty(true);
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
//        if (entityManager1 == null) {
        entityManager1 = getEMF1().createEntityManager();
//        } // We 
//        else if (!entityManager1.isOpen()) {
//            entityManager1 = getEMF1().createEntityManager();
//        }

        return entityManager1;
    }

    // tk put in business entity utils?
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
            return Client.findClientsByName(getEntityManager1(), query);

        } catch (Exception e) {
            System.out.println(e);

            return new ArrayList<Client>();
        }
    }

    public List<String> completeActiveClientName(String query) {
        try {
            return Client.findActiveClientNames(getEntityManager1(), query);

        } catch (Exception e) {
            System.out.println(e);

            return new ArrayList<String>();
        }
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

            Client client = Client.findClientByName(em, currentDocumentInspection.getConsignee().getName());
            if (client != null) {
                currentDocumentInspection.getConsignee().doCopy(client);
            }

            closeEntityManager1();
            setDirty(true);
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    // tk do same for brober and retail outlet.
    public void updateComplianceSurveyConsignee(EntityManager em) {

        Client client = Client.findClientByName(em, currentComplianceSurvey.getConsignee().getName());
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
        ArrayList<String> contactsFound = new ArrayList<String>();

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
        clientManager.setTaxRegistrationNumberRequired(true);
        clientManager.setSave(false);
        clientManager.setBusinessEntityManager(this);
        clientManager.setEntityManagerFactory(EMF1);
        clientManager.setComponentsToUpdate(":ComplianceSurveyDialogForm:complianceSurveyTabView:consignee");

    }

    public void editDocumentInspectionConsignee() {

        ClientManager clientManager = ComplianceManager.findBean("clientManager");

        clientManager.setClient(currentDocumentInspection.getConsignee());
        clientManager.setTaxRegistrationNumberRequired(false);
        clientManager.setSave(false);
        clientManager.setBusinessEntityManager(this);
        clientManager.setEntityManagerFactory(EMF1);
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

        Client client = Client.findClientByName(em, currentComplianceSurvey.getBroker().getName());
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
        ArrayList<String> contactsFound = new ArrayList<String>();

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
        clientManager.setTaxRegistrationNumberRequired(true);
        clientManager.setBusinessEntityManager(this);
        clientManager.setEntityManagerFactory(EMF1);
        clientManager.setComponentsToUpdate(":ComplianceSurveyDialogForm:complianceSurveyTabView:broker");

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
                currentProductInspection.setManufacturer(jm.getDefaultManufacturer(em, " "));
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
                currentProductInspection.setDistributor(jm.getDefaultDistributor(em, " "));
            } else {
                // create new distributor
                currentProductInspection.setDistributor(new Distributor(getCurrentProductInspection().getDistributor().getName()));
            }
        }

        setDirty(true);
    }

    public void updateComplianceSurveyRetailOutlet(EntityManager em) {

        Client client = Client.findClientByName(em, currentComplianceSurvey.getRetailOutlet().getName());
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
        ArrayList<String> contactsFound = new ArrayList<String>();

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
        clientManager.setTaxRegistrationNumberRequired(true);
        clientManager.setBusinessEntityManager(this);
        clientManager.setEntityManagerFactory(EMF1);
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

    public void updateContainerNumber() {
        // create/save shipping containers if needed
        // save current list of containers for later use       
        List<ShippingContainer> currentShippingContainers = new ArrayList<ShippingContainer>();
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
        } else {
            currentComplianceSurvey.setDateOfDetention(null);
        }

        setDirty(true);
    }

    public void updateDailyReportStartDate() {
        currentComplianceDailyReport.setEndOfPeriod(currentComplianceDailyReport.getStartOfPeriod());
        //endOfPeriod = startOfPeriod;
        setDirty(true);
    }

    public void updateCountryOfConsignment() {
//        if ((currentComplianceSurvey.getEntryDocumentInspection().getShipmentCountryOfOrigin() == null)
//                || currentComplianceSurvey.getEntryDocumentInspection().getEntryDocumentInspection().getShipmentCountryOfOrigin().equals("--")
//                || currentComplianceSurvey.getEntryDocumentInspection().getShipmentCountryOfOrigin().trim().equals("")) {
//            currentComplianceSurvey.setShipmentCountryOfOrigin(currentComplianceSurvey.getEntryDocumentInspection().getCountryOfConsignment());
//        }
        setDirty(true);
    }

    public void updateCompanyTypes() {
        if (!currentComplianceSurvey.getOtherCompanyTypes()) {
            currentComplianceSurvey.setCompanyTypes("");
        }
        setDirty(true);
    }

    public void createNewProductInspection() {
        currentProductInspection = new ProductInspection();
        currentProductInspection.setQuantity(0);
        currentProductInspection.setSampleSize(0);
        isNewProductInspection = true;
        setDirty(true);
    }

    public ComplianceSurvey getCurrentComplianceSurvey() {
        if (currentComplianceSurvey == null) {
            createNewComplianceSurvey();
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
        currentComplianceSurvey.setSurveyType(" ");
        // consignee and rep
        currentComplianceSurvey.setConsignee(new Client("", false));
        currentComplianceSurvey.setConsigneeRepresentative(new Contact(""));
        // retail outlet and rep
        currentComplianceSurvey.setRetailOutlet(new Client("", false));
        currentComplianceSurvey.setRetailRepresentative(new Contact(""));
        // broker and rep
        currentComplianceSurvey.setBroker(new Client("", false));
        currentComplianceSurvey.setBrokerRepresentative(new Contact(""));

        currentComplianceSurvey.getEntryDocumentInspection().setCountryOfConsignment(" ");
        currentComplianceSurvey.setDateOfSurvey(new Date());
        if (userManagement.getUser() != null) {
            currentComplianceSurvey.setInspector(userManagement.getUser().getEmployee());
        }

        isNewComplianceSurvey = true;
        setDirty(false);

        // tk  mark that connection is live. this will be check in javascript
        // to determine if we still have connection
        context.addCallbackParam("isConnectionLive", true);
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

        setDirty(false);

        // tk  mark that connection is live. this will be check in javascript
        // to determine if we still have connection
        context.addCallbackParam("isConnectionLive", true);
    }

    public void saveComplianceSurvey() {
        EntityManager em = getEntityManager1();
        RequestContext context = RequestContext.getCurrentInstance();

        try {

            // Needed for validation purposes
            ClientManager clientManager = ComplianceManager.findBean("clientManager");

            // validate survey type
            if (currentComplianceSurvey.getSurveyType().trim().equals("")) {
                context.execute("requiredValuesAlertDialog.show();");
                return;
            }

            // validate country of consignment
            if (currentComplianceSurvey.getSurveyType().equals("Product Survey")) {
                if (currentComplianceSurvey.getEntryDocumentInspection().getCountryOfConsignment().equals(" ")
                        || currentComplianceSurvey.getEntryDocumentInspection().getCountryOfConsignment().equals("-- Not displayed --")) {
                    context.execute("requiredValuesAlertDialog.show();");
                    return;
                }
            }

            // Valid consignee if required. 
            if (currentComplianceSurvey.getSurveyType().equals("Product Survey")) {
                clientManager.setClient(currentComplianceSurvey.getConsignee());
                clientManager.setTaxRegistrationNumberRequired(true);
                if (!clientManager.validateClient()) {
                    return;
                }
            }

            //validate date/time checked
            if (currentComplianceSurvey.getDateOfSurvey() == null) {
                context.execute("requiredValuesAlertDialog.show();");
                return;
            }

//             tk consider remove this validate inspector
            Employee inspector = getEmployeeByName(em, currentComplianceSurvey.getInspector().getName());
            if (inspector != null) {
                currentComplianceSurvey.setInspector(inspector);
            } else {
                currentComplianceSurvey.setInspector(jm.getDefaultEmployee(em, "--", "--"));
            }

            // tk save all clients
            clientManager.setClient(currentComplianceSurvey.getConsignee());
            clientManager.saveClient(em);
            clientManager.setClient(currentComplianceSurvey.getBroker());
            clientManager.saveClient(em);
            clientManager.setClient(currentComplianceSurvey.getRetailOutlet());
            clientManager.saveClient(em);

            if (dirty) {
                Employee employee = Employee.findEmployeeById(em, userManagement.getUser().getEmployee().getId());
                currentComplianceSurvey.setDateEdited(new Date());
                currentComplianceSurvey.setEditedBy(employee);
            }

            // tk
//            System.out.println("Saving retail outlet rep: " + currentComplianceSurvey.getRetailRepresentative().getName() + "," + currentComplianceSurvey.getRetailRepresentative().getId());
//            System.out.println("Saving consignee rep: " + currentComplianceSurvey.getConsigneeRepresentative().getName() + "," + currentComplianceSurvey.getConsigneeRepresentative().getId());
//            System.out.println("Saving broker rep: " + currentComplianceSurvey.getBrokerRepresentative().getName() + "," + currentComplianceSurvey.getBrokerRepresentative().getId());
//            System.out.println("Saving broker: " + currentComplianceSurvey.getBroker() + "," + currentComplianceSurvey.getBroker().getId());

            // validate consignee and rep
//            Client client1 = jm.getClientByName(em, currentComplianceSurvey.getConsignee().getName().trim());
//            currentComplianceSurvey.setConsignee(client1);
//            updateComplianceSurveyConsigneeRepresentative();
//            Contact contact1 = getContactByName(em, currentComplianceSurvey.getConsigneeRepresentative().getName().trim());
//            if (contact1 != null) {
//                currentComplianceSurvey.setConsigneeRepresentative(contact1);
//            } else {
//                currentComplianceSurvey.setConsigneeRepresentative(jm.getDefaultContact(em, "--", "--"));
//            }

//            // validate broker and rep
//            Client client2 = jm.getClientByName(em, currentComplianceSurvey.getBroker().getName().trim());
//            currentComplianceSurvey.setBroker(client2);
//            updateComplianceSurveyBrokerRepresentative();
//            Contact contact2 = getContactByName(em, currentComplianceSurvey.getBrokerRepresentative().getName().trim());
//            if (contact2 != null) {
//                currentComplianceSurvey.setBrokerRepresentative(contact2);
//            } else {
//                currentComplianceSurvey.setBrokerRepresentative(jm.getDefaultContact(em, "--", "--"));
//            }

            // validate retail outlet and rep
//            Client client3 = jm.getClientByName(em, currentComplianceSurvey.getRetailOutlet().getName().trim());
//            currentComplianceSurvey.setRetailOutlet(client3);
//            updateComplianceSurveyRetailOutletRepresentative();
//            Contact contact3 = getContactByName(em, currentComplianceSurvey.getRetailRepresentative().getName().trim());
//            if (contact3 != null) {
//                currentComplianceSurvey.setRetailRepresentative(contact3);
//            } else {
//                currentComplianceSurvey.setRetailRepresentative(jm.getDefaultContact(em, "--", "--"));
//            }

            // save specified release location for detention request
//            if (currentComplianceSurvey.getSpecifiedReleaseLocation().getId() == null) {
//                jm.saveBusinessEntity(em, currentComplianceSurvey.getSpecifiedReleaseLocation());
//            }
//
//            // save specified release location for domestic market release from detention
//            if (currentComplianceSurvey.getSpecifiedReleaseLocationDomesticMarket().getId() == null) {
//                jm.saveBusinessEntity(em, currentComplianceSurvey.getSpecifiedReleaseLocationDomesticMarket());
//            }
//
//            // save location of detained product
//            if (currentComplianceSurvey.getSpecifiedReleaseLocationDomesticMarket().getId() == null) {
//                jm.saveBusinessEntity(em, currentComplianceSurvey.getLocationOfDetainedProductDomesticMarket());
//            }          

            // TK NB Validate othe contact

            em.getTransaction().begin();
            // save shipping containers
//            for (ShippingContainer shippingContainer : getCurrentComplianceSurvey().getShippingContainers()) {
//                // tk 
//
//                if (shippingContainer.getId() == null) {
//                    jm.saveBusinessEntity(em, shippingContainer);
//                }
//            }

            // save new products and associated objects.
//            for (ProductInspection productInspection : getCurrentComplianceSurvey().getProducts()) {
//                // save business source
//                if (productInspection.getBusinessSource().getId() == null) {
//                    jm.saveBusinessEntity(em, productInspection.getBusinessSource());
//                } else {
//                    Business b = jm.getBusinessById(em, productInspection.getBusinessSource().getId());
//                    productInspection.setBusinessSource(b);
//                }
//                // save manufacturer
//                if (productInspection.getManufacturer().getId() == null) {
//                    jm.saveBusinessEntity(em, productInspection.getManufacturer());
//                } else {
//                    Manufacturer m = jm.getManufacturerById(em, productInspection.getManufacturer().getId());
//                    productInspection.setManufacturer(m);
//                }
//                // save distributor
//                if (productInspection.getDistributor().getId() == null) {
//                    jm.saveBusinessEntity(em, productInspection.getDistributor());
//                } else {
//                    Distributor d = jm.getDistributorById(em, productInspection.getDistributor().getId());
//                    productInspection.setDistributor(d);
//                }
//                // save product inspection if it's new
//                if (productInspection.getId() == null) {
//                    jm.saveBusinessEntity(em, productInspection);
//                }
//            }

            // now save survey            
            Long id = BusinessEntityUtils.saveBusinessEntity(em, currentComplianceSurvey);
            em.getTransaction().commit();

            if (id == null) {
                context.addCallbackParam("entitySaved", false);
            } else if ((id == null) || (id == 0L)) {
                context.addCallbackParam("entitySaved", false);
            } else {
                context.addCallbackParam("entitySaved", true);
                isNewComplianceSurvey = false;
                setDirty(false);
            }

            //em.refresh(currentComplianceSurvey); // tk make sure data fresh.
            currentComplianceSurvey = ComplianceSurvey.findComplianceSurveyById(em, currentComplianceSurvey.getId());
            closeEntityManager1();

        } catch (Exception e) {
            closeEntityManager1();
            context.addCallbackParam("entitySaved", false);
            System.out.println(e);
        }
    }

    public void saveDocumentInspection() {
        EntityManager em = getEntityManager1();
        RequestContext context = RequestContext.getCurrentInstance();

        try {

            // Needed for validation purposes
            ClientManager clientManager = ComplianceManager.findBean("clientManager");

            // validate date/time inspected
            if (currentDocumentInspection.getDateOfInspection() == null) {
                context.execute("requiredValuesAlertDialog.show();");
                return;
            }

            // validate inspector
            Employee inspector = getEmployeeByName(em, currentDocumentInspection.getInspector().getName());
            if (inspector != null) {
                currentDocumentInspection.setInspector(inspector);
            } else {
                currentDocumentInspection.setInspector(jm.getDefaultEmployee(em, "--", "--"));
            }

            // validate
            clientManager.setClient(currentDocumentInspection.getConsignee());
            clientManager.setTaxRegistrationNumberRequired(false);
            if (!clientManager.validateClient()) {
                return;
            }

            clientManager.saveClient(em);

            em.getTransaction().begin();
            // now save doc. inspection            
            Long id = BusinessEntityUtils.saveBusinessEntity(em, currentDocumentInspection);
            em.getTransaction().commit();

            if (id == null) {
                context.addCallbackParam("entitySaved", false);
            } else if ((id == null) || (id == 0L)) {
                context.addCallbackParam("entitySaved", false);
            } else {
                context.addCallbackParam("entitySaved", true);
                isNewDocumentInspection = false;
                setDirty(false);
            }

        } catch (Exception e) {
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
        RequestContext context = RequestContext.getCurrentInstance();

        if (dirty) {
            setDirty(false);
            setDialogMessage("The form data was modified. Do you wish to save the modified data?");
            setDialogMessageHeader("Form Data Modified");
            setDialogMessageSeverity("info");
            context.update("saveConfirmationDialogForm");
            context.execute("saveConfirmationDialog.show();");
        }
    }

    public void save() {
        if (activeTabTitle.equals("Compliance Survey")) {
            saveComplianceSurvey();
        }
        if (activeTabTitle.equals("Document Inspection")) {
            saveDocumentInspection();
        }
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
        EntityManager em = getEntityManager1();
        ArrayList countriesList = new ArrayList();
        countriesList.add(new SelectItem(" ", " "));
        countriesList.add(new SelectItem("-- Not displayed --", "-- Not displayed --"));

        List<Country> countries = Country.getAllCountries(em);
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

        types.add(new SelectItem(" ", " "));
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

    public List<Employee> completeEmployee(String query) {

        try {
            List<Employee> employees = Employee.findEmployeesByName(getEntityManager1(), query);
            if (employees != null) {
                return employees;
            } else {
                return new ArrayList<Employee>();
            }
        } catch (Exception e) {
            System.out.println(e);
            return new ArrayList<Employee>();
        }
    }

    // tk remove this and use the one in JMTSApp?
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

    private Contact getContactByName(EntityManager em, String name) {
        String names[] = name.split(",");
        if (names.length == 2) {
            return Contact.findContactByName(em,
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

    public String logout() {
        RequestContext context = RequestContext.getCurrentInstance();

        userManagement.setUserLoggedIn(false);
        userManagement.setShowLogin(true);
        userManagement.setPassword("");
        userManagement.setUsername("");
        searchText = "";
        reportSearchText = "";
        searchType = "General";
        // tk save dirty forms before logging out. make personal option?
        if (dirty) {
            saveComplianceSurvey();
        }
        userManagement.setLogonMessage("Please provide your login details below:");
        userManagement.setUser(new JobManagerUser());

        context.execute("PrimeFaces.changeTheme('" + userManagement.getUser().getUserInterfaceThemeName() + "');");

        return "logout";
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

    public void login() {
        EntityManager em = getEntityManager1();
        RequestContext context = RequestContext.getCurrentInstance();

        userManagement.setUserLoggedIn(false);

        try {
            if ((userManagement.getUsername() != null) && (userManagement.getPassword() != null)) {
                if ((userManagement.getUsername().trim().equals("")) || (userManagement.getPassword().trim().equals(""))) {
                    userManagement.setLogonMessage("Invalid username or password! Please try again.");
                    userManagement.setUsername("");
                    userManagement.setPassword("");
                } else {
                    if (jm.validateAndAssociateUser(em, userManagement.getUsername(), userManagement.getPassword())) {
//                        if (true) { // used for testing
                        userManagement.setUser(JobManagerUser.findJobManagerUserByUsername(em, userManagement.getUsername()));
                        if (userManagement.getUser() != null) {
                            em.refresh(userManagement.getUser());
                            userManagement.setUserLoggedIn(true);

                            context.execute("PrimeFaces.changeTheme('" + userManagement.getUser().getUserInterfaceThemeName() + "');");
                        } else {
                            userManagement.setLogonMessage("Invalid username or password! Please try again.");
                            userManagement.setUsername("");
                            userManagement.setPassword("");
                        }
                    } else {
                        userManagement.setLogonMessage("Invalid username or password! Please try again.");
                        userManagement.setUsername("");
                        userManagement.setPassword("");
                    }
                }
            } else {
                userManagement.setLogonMessage("Invalid username or password! Please try again.");
                userManagement.setUsername("");
                userManagement.setPassword("");
            }

            // wrap up
            if (userManagement.getUserLoggedIn()) {
                userManagement.setShowLogin(false);
                userManagement.setUsername("");
                userManagement.setPassword("");
                if (context != null) {
                    context.addCallbackParam("userLoggedIn", userManagement.getUserLoggedIn());
                }

            } else {
                userManagement.setLogonMessage("Invalid username or password! Please try again.");
                userManagement.setUsername("");
                userManagement.setPassword("");
            }
        } catch (Exception e) {
            userManagement.setLogonMessage("Login error occured! Please try again or contact the Database Administrator");
            userManagement.setUsername("");
            userManagement.setPassword("");
        }
    }

    public void doEntitySearch() {

        RequestContext context = RequestContext.getCurrentInstance();

        if (searchText != null) {
            searchText = searchText.trim();
        } else {
            searchText = "";
        }

        if (activeTabTitle.equals("Compliance Survey")) {
            if (!searchText.equals("")) {
                if (userManagement.getUserLoggedIn()) {
                    System.out.println("doing compl survey search.");
                    complianceSurveys = ComplianceSurvey.findComplianceSurveysByDateSearchField(getEntityManager1(),
                            userManagement.getUser(),
                            dateSearchField, "General", searchText,
                            null, null);

                } else {
                    complianceSurveys = new ArrayList<ComplianceSurvey>();
                }
            } else {
                complianceSurveys = new ArrayList<ComplianceSurvey>();
            }

            context.update("mainTabViewForm:mainTabView:complianceSurveysTable");
        }

        if (activeTabTitle.equals("Document Inspection")) {
            if (!searchText.equals("")) {
                if (userManagement.getUserLoggedIn()) {
                    System.out.println("doing document inspection search.");
                    documentInspections = DocumentInspection.findDocumentInspectionsByDateSearchField(getEntityManager1(),
                            userManagement.getUser(),
                            "dateOfInspection", "General", searchText,
                            null, null);

                } else {
                    documentInspections = new ArrayList<DocumentInspection>();
                }
            } else {
                documentInspections = new ArrayList<DocumentInspection>();
            }

            context.update("mainTabViewForm:mainTabView:documentInspectionsTable");
        }

        // update="#{complianceManager.tablesToUpdateAfterSearch}"

        // tk  mark that connection is live. this will be check in javascript
        // to determine if we still have connection
        //closeEntityManager1(); // tk
        context.addCallbackParam("isConnectionLive", true);
    }

    public List<String> completeSearchText(String query) {
        List<String> suggestions = new ArrayList<String>();

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
        if (userManagement.getUser().getId() != null) {
            em.getTransaction().begin();
            userManagement.getUser().setPollTime(new Date());
            BusinessEntityUtils.saveBusinessEntity(em, userManagement.getUser());
            em.getTransaction().commit();
        }
    }

    // tk find way to update only the tables that need to be updated
    public String getTablesToUpdateAfterSearch() {

        return ":mainTabViewForm:mainTabView:complianceSurveysTable,:mainTabViewForm:mainTabView:documentInspectionsTable";
    }

    public String getProductTablesToUpdate() {
        return ":ComplianceSurveyDialogForm:complianceSurveyTabView:marketProductsTable";
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
            setDirty(true);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void editForm() {
        RequestContext context = RequestContext.getCurrentInstance();

//        try {
//            EntityManager em = getEntityManager1();
//            if (activeTabTitle.equals("Compliance Survey")) {
//                currentComplianceSurvey = jm.getComplianceSurveyById(em, currentComplianceSurvey.getId());
//            }
//            if (activeTabTitle.equals("Document Inspection")) {
//                currentDocumentInspection = jm.getDocumentInspectionById(em, currentComplianceSurvey.getId());
//            }
//        } catch (Exception e) {
//            System.out.println(e);
//        }

        context.addCallbackParam("isConnectionLive", true);
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
//                String str = JasperFillManager.fillReportToFile(reportFileURL, parameters, con);

//                File file = new File("Detention.pdf");
//                FileOutputStream out = new FileOutputStream(file);
//
//                //JasperExportManager.exportReportToPdfStream(print, out);
//
//                out.close();
            } catch (JRException ex) {
                System.out.println(ex);
            }
//            catch (FileNotFoundException ex1) {
//                System.out.println(ex1);
//            } catch (IOException ex2) {
//                System.out.println(ex2);
//            }
        } else {
            System.out.println("not connected!");
        }


        //String baseURL = "\\\\bosprintsrv\\c$\\uploads\\images\11153_1359128328029_print-file.png";
//        String baseURL = "C:\\Projects\\images\\11153_1359128328029_print-file.png"; // \\boshrmapp\c$\glassfishv3\images
        String baseURL = "C:\\glassfishv3\\images\\11153_1359128328029_print-file.png";

        //InputStream stream = ((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream(baseURL);  
        try {

            FileInputStream stream = new FileInputStream(baseURL);
            streamedFile = new DefaultStreamedContent(stream, "image/png", "downloaded.png");

        } catch (Exception ex) {
            System.out.println(ex);
        }

        return streamedFile;
    }

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

        // broker detail
        //Client broker = jm.getClientByName(em, currentComplianceSurvey.getBroker().getName());
        //Contact brokerRep = jm.getContactByName(em, username, username);
        parameters.put("brokerDetail", currentComplianceSurvey.getBroker().getName() + "\n"
                + currentComplianceSurvey.getBrokerRepresentative().getFirstName() + " "
                + currentComplianceSurvey.getBrokerRepresentative().getLastName() + "\n"
                + currentComplianceSurvey.getBroker().getBillingAddress().getAddressLine1() + "\n"
                + currentComplianceSurvey.getBroker().getBillingAddress().getAddressLine2() + "\n"
                + currentComplianceSurvey.getBroker().getBillingAddress().getCity() + "\n"
                + currentComplianceSurvey.getBroker().getBillingAddress().getStateOrProvince());

        //Consignee detail
        //Client consignee = jm.getClientByName(em, currentComplianceSurvey.getConsignee().getName());
        parameters.put("consigneeDetail", currentComplianceSurvey.getConsignee().getName() + "\n"
                + currentComplianceSurvey.getConsignee().getBillingAddress().getAddressLine1() + "\n"
                + currentComplianceSurvey.getConsignee().getBillingAddress().getAddressLine2() + "\n"
                + currentComplianceSurvey.getConsignee().getBillingAddress().getCity() + "\n"
                + currentComplianceSurvey.getConsignee().getBillingAddress().getStateOrProvince() + "\n"
                + BusinessEntityUtils.getContactTelAndFax(currentComplianceSurvey.getConsignee().getMainContact()));

        parameters.put("products", getComplianceSurveyProductNames());
        parameters.put("quantity", getComplianceSurveyProductQuantitiesAndUnits());
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

        // specified release location               
        parameters.put("specifiedReleaseLocation", currentComplianceSurvey.getSpecifiedReleaseLocation().getName() + "\n"
                + currentComplianceSurvey.getSpecifiedReleaseLocation().getAddressLine1() + "\n"
                + currentComplianceSurvey.getSpecifiedReleaseLocation().getAddressLine2() + "\n"
                + currentComplianceSurvey.getSpecifiedReleaseLocation().getCity() + "\n"
                + currentComplianceSurvey.getSpecifiedReleaseLocation().getStateOrProvince());

        // consignee
        // Client consignee = jm.getClientByName(em, currentComplianceSurvey.getConsignee().getName());
        parameters.put("consigneeDetail", currentComplianceSurvey.getConsignee().getName() + "\n"
                + currentComplianceSurvey.getConsignee().getBillingAddress().getAddressLine1() + "\n"
                + currentComplianceSurvey.getConsignee().getBillingAddress().getAddressLine2() + "\n"
                + currentComplianceSurvey.getConsignee().getBillingAddress().getCity() + "\n"
                + currentComplianceSurvey.getConsignee().getBillingAddress().getStateOrProvince());

        parameters.put("products", getComplianceSurveySampledProductNamesQuantitiesAndUnits());
        parameters.put("numberOfSamplesTaken", getComplianceSurveyProductTotalSampleSize());

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

        // broker
        parameters.put("formId", currentComplianceSurvey.getId().longValue());
        parameters.put("brokerDetail", currentComplianceSurvey.getBroker().getName() + "\n"
                + currentComplianceSurvey.getBroker().getBillingAddress().getAddressLine1() + "\n"
                + currentComplianceSurvey.getBroker().getBillingAddress().getAddressLine2() + "\n"
                + BusinessEntityUtils.getContactTelAndFax(currentComplianceSurvey.getBroker().getMainContact()));

        // consignee
        parameters.put("consigneeDetail", currentComplianceSurvey.getConsignee().getBillingAddress().getAddressLine1() + ", "
                + currentComplianceSurvey.getConsignee().getBillingAddress().getAddressLine2() + ", "
                + currentComplianceSurvey.getConsignee().getBillingAddress().getCity() + ", "
                + currentComplianceSurvey.getConsignee().getBillingAddress().getStateOrProvince());

        // consignee contact person
        parameters.put("consigneeContactPerson", BusinessEntityUtils.getContactFullName(currentComplianceSurvey.getConsigneeRepresentative()));

        parameters.put("consigneeTelFaxEmail", BusinessEntityUtils.getMainTelFaxEmail(currentComplianceSurvey.getConsignee().getMainContact()));
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
        parameters.put("brokerDetail", currentComplianceSurvey.getBroker().getName() + "\n"
                + currentComplianceSurvey.getBroker().getBillingAddress().getAddressLine1() + "\n"
                + currentComplianceSurvey.getBroker().getBillingAddress().getAddressLine2() + "\n"
                + BusinessEntityUtils.getContactTelAndFax(currentComplianceSurvey.getBroker().getMainContact()));

        // consignee
        parameters.put("consigneeDetail", currentComplianceSurvey.getConsignee().getBillingAddress().getAddressLine1() + ", "
                + currentComplianceSurvey.getConsignee().getBillingAddress().getAddressLine2() + ", "
                + currentComplianceSurvey.getConsignee().getBillingAddress().getCity() + ", "
                + currentComplianceSurvey.getConsignee().getBillingAddress().getStateOrProvince());

        // provisional release location 
        parameters.put("specifiedReleaseLocationDomesticMarket", currentComplianceSurvey.getSpecifiedReleaseLocationDomesticMarket().getAddressLine1() + ", "
                + currentComplianceSurvey.getSpecifiedReleaseLocationDomesticMarket().getAddressLine2() + ", "
                + currentComplianceSurvey.getSpecifiedReleaseLocationDomesticMarket().getCity() + ", "
                + currentComplianceSurvey.getSpecifiedReleaseLocationDomesticMarket().getStateOrProvince());

        // location of detained products locationOfDetainedProduct 
        parameters.put("locationOfDetainedProduct", currentComplianceSurvey.getLocationOfDetainedProductDomesticMarket().getAddressLine1() + ", "
                + currentComplianceSurvey.getLocationOfDetainedProductDomesticMarket().getAddressLine2() + ", "
                + currentComplianceSurvey.getLocationOfDetainedProductDomesticMarket().getCity() + ", "
                + currentComplianceSurvey.getLocationOfDetainedProductDomesticMarket().getStateOrProvince());

        // consignee contact person
        parameters.put("consigneeContactPerson", BusinessEntityUtils.getContactFullName(currentComplianceSurvey.getConsigneeRepresentative()));

        // consignee tel/fax/email
        parameters.put("consigneeTelFaxEmail", BusinessEntityUtils.getMainTelFaxEmail(currentComplianceSurvey.getConsignee().getMainContact()));

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
        if (dirty) {
            save();
        }
        if (currentComplianceSurvey.getId() != null) {
            currentComplianceSurvey = ComplianceSurvey.findComplianceSurveyById(em, currentComplianceSurvey.getId());
        }
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

                    String reportFileURL = SystemOption.findSystemOptionByName(em, form).getOptionValue();

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
                            setDirty(dirty);
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
                            setDirty(dirty);
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
        parameters.put("brokerDetail", currentComplianceSurvey.getBroker().getName() + "\n"
                + currentComplianceSurvey.getBroker().getBillingAddress().getAddressLine1() + "\n"
                + currentComplianceSurvey.getBroker().getBillingAddress().getAddressLine2() + "\n"
                + BusinessEntityUtils.getContactTelAndFax(currentComplianceSurvey.getBroker().getMainContact()));

        // consignee
        parameters.put("consigneeDetail", currentComplianceSurvey.getConsignee().getBillingAddress().getAddressLine1() + ", "
                + currentComplianceSurvey.getConsignee().getBillingAddress().getAddressLine2() + ", "
                + currentComplianceSurvey.getConsignee().getBillingAddress().getCity() + ", "
                + currentComplianceSurvey.getConsignee().getBillingAddress().getStateOrProvince());

        // provisional release location 
        parameters.put("specifiedReleaseLocationDomesticMarket", currentComplianceSurvey.getSpecifiedReleaseLocationDomesticMarket().getAddressLine1() + ", "
                + currentComplianceSurvey.getSpecifiedReleaseLocationDomesticMarket().getAddressLine2() + ", "
                + currentComplianceSurvey.getSpecifiedReleaseLocationDomesticMarket().getCity() + ", "
                + currentComplianceSurvey.getSpecifiedReleaseLocationDomesticMarket().getStateOrProvince());

        // location of detained products locationOfDetainedProduct 
        parameters.put("locationOfDetainedProduct", currentComplianceSurvey.getLocationOfDetainedProductDomesticMarket().getAddressLine1() + ", "
                + currentComplianceSurvey.getLocationOfDetainedProductDomesticMarket().getAddressLine2() + ", "
                + currentComplianceSurvey.getLocationOfDetainedProductDomesticMarket().getCity() + ", "
                + currentComplianceSurvey.getLocationOfDetainedProductDomesticMarket().getStateOrProvince());

        // consignee tel/fax/email
        parameters.put("consigneeTelFaxEmail", BusinessEntityUtils.getMainTelFaxEmail(currentComplianceSurvey.getConsignee().getMainContact()));

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
}
