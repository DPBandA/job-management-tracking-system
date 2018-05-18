/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.com.dpbennett.jobmanagementlibrary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.faces.event.ActionEvent;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import jm.com.dpbennett.entity.Certification;
import jm.com.dpbennett.entity.Client;
import jm.com.dpbennett.entity.Employee;
import jm.com.dpbennett.entity.JobManagerUser;
import jm.com.dpbennett.entity.Manufacturer;
import jm.com.dpbennett.entity.PetrolPump;
import jm.com.dpbennett.entity.PetrolPumpNozzle;
import jm.com.dpbennett.entity.PetrolStation;
import jm.com.dpbennett.entity.Scale;
import jm.com.dpbennett.entity.Sticker;
import jm.com.dpbennett.utils.BusinessEntityUtils;
import jm.com.dpbennett.utils.SearchParameters;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CloseEvent;

/**
 *
 * @author dbennett
 */
public class LegalMetrologyManager implements Serializable {

    @PersistenceUnit(unitName = "JMTSWebAppPU")
    private EntityManagerFactory EMF;
    private List<PetrolStation> petrolStationSearchResultsList;
    private List<Scale> scaleSearchResultsList;
    private PetrolStation currentPetrolStation;
    private PetrolPump currentPetrolPump;
    private PetrolPumpNozzle currentPetrolPumpNozzle;
    private Scale currentScale;
    private SearchParameters currentSearchParameters;
    private JobManagement jm;
    private Boolean dirty;
    private Boolean addPetrolPump;
    private Boolean addPetrolPumpNozzle;
    private MessageManagement messageManagement;
    private ClientManagement clientManagement;

    /**
     * Creates a new instance of LegalMetrologyManager
     */
    public LegalMetrologyManager() {
        dirty = false;
        addPetrolPump = false;
        addPetrolPumpNozzle = false;
        jm = new JobManagement();
    }

    public LegalMetrologyManager(String persistenceUnitName ) {
        dirty = false;
        addPetrolPump = false;
        addPetrolPumpNozzle = false;
        jm = new JobManagement();
        
        EMF = BusinessEntityUtils.getEntityManagerFactory(persistenceUnitName);
    }

    public ClientManagement getClientManagement() {
        return clientManagement;
    }

    public void setClientManagement(ClientManagement clientManagement) {
        this.clientManagement = clientManagement;
    }

    public void setMessageManagement(MessageManagement messageManagement) {
        this.messageManagement = messageManagement;
    }

    public Scale getCurrentScale() {
        if (currentScale == null) {
            currentScale = new Scale();
        }
        return currentScale;
    }

    public void setCurrentScale(Scale currentScale) {
        this.currentScale = currentScale;
    }

    public PetrolPumpNozzle getCurrentPetrolPumpNozzle() {
        if (currentPetrolPumpNozzle == null) {
            currentPetrolPumpNozzle = new PetrolPumpNozzle();
        }
        return currentPetrolPumpNozzle;
    }

    public void setCurrentPetrolPumpNozzle(PetrolPumpNozzle currentPetrolPumpNozzle) {
        this.currentPetrolPumpNozzle = currentPetrolPumpNozzle;
    }

    public PetrolPump getCurrentPetrolPump() {
        if (currentPetrolPump == null) {
            currentPetrolPump = new PetrolPump();
        }
        return currentPetrolPump;
    }

    public void setCurrentPetrolPump(PetrolPump currentPetrolPump) {
        this.currentPetrolPump = currentPetrolPump;
    }

    public PetrolStation getCurrentPetrolStation() {
        if (currentPetrolStation == null) {
            currentPetrolStation = new PetrolStation();
        }
        return currentPetrolStation;
    }

    public void setCurrentPetrolStation(PetrolStation currentPetrolStation) {
        this.currentPetrolStation = currentPetrolStation;
    }

    public String getPetrolStationSearchResultsTableHeader() {
        return JMTSApp.getSearchResultsTableHeader(currentSearchParameters, getPetrolStationSearchResultsList());
    }

    public String getScaleSearchResultsTableHeader() {
        return JMTSApp.getSearchResultsTableHeader(currentSearchParameters, getScaleSearchResultsList());
    }

    public SearchParameters getCurrentSearchParameters() {
        return currentSearchParameters;
    }

    public void setCurrentSearchParameters(SearchParameters currentSearchParameters) {
        this.currentSearchParameters = currentSearchParameters;
    }

    public Boolean getDirty() {
        return dirty;
    }

    public void setDirty(Boolean dirty) {
        this.dirty = dirty;
    }

    public List<PetrolStation> getPetrolStationSearchResultsList() {
        if (petrolStationSearchResultsList == null) {
            petrolStationSearchResultsList = new ArrayList<PetrolStation>();
        }
        return petrolStationSearchResultsList;
    }

    public List<Scale> getScaleSearchResultsList() {
        if (scaleSearchResultsList == null) {
            scaleSearchResultsList = new ArrayList<Scale>();
        }
        return scaleSearchResultsList;
    }

//    public int getSizeOfSearchResults() {
//        // tk choose list based on search
//        return getPetrolStationSearchResultsList().size();
//    }
    public EntityManager getEntityManager() {
        return EMF.createEntityManager();
    }

    public void doPetrolStationSearch(SearchParameters currentSearchParameters) {

        this.currentSearchParameters = currentSearchParameters;

        petrolStationSearchResultsList = PetrolStation.findPetrolStationsByDateSearchField(getEntityManager(),
                null,
                currentSearchParameters.getDateField(),
                currentSearchParameters.getSearchType(),
                currentSearchParameters.getSearchText(),
                currentSearchParameters.getDatePeriod().getStartDate(),
                currentSearchParameters.getDatePeriod().getEndDate(), false);

        if (petrolStationSearchResultsList == null) {
            petrolStationSearchResultsList = new ArrayList<PetrolStation>();
        }
    }

    public void doScaleSearch(SearchParameters currentSearchParameters) {

        this.currentSearchParameters = currentSearchParameters;

        scaleSearchResultsList = Scale.findScalesByDateSearchField(getEntityManager(),
                null,
                currentSearchParameters.getDateField(),
                currentSearchParameters.getSearchType(),
                currentSearchParameters.getSearchText(),
                currentSearchParameters.getDatePeriod().getStartDate(),
                currentSearchParameters.getDatePeriod().getEndDate(), false);

        if (scaleSearchResultsList == null) {
            scaleSearchResultsList = new ArrayList<Scale>();
        }

    }

    public void closePetrolStationDialog2() {
        closePetrolStationDialog1();
    }

    public void closePetrolStationDialog1() {
        // Redo search to reloasd stored jobs including
        RequestContext context = RequestContext.getCurrentInstance();

        // prompt to save modified job before attempting to create new job
        if (getDirty()) {
            context.update("unitDialogForms:petrolStationSaveConfirm");
            context.execute("petrolStationSaveConfirmDialog.show();petrolStationDialog.hide();");

            return;
        } else {
            context.execute("petrolStationDialog.hide();");
        }

        setDirty(false);
        // refresh search if possible
        if (currentSearchParameters != null) {
            doPetrolStationSearch(currentSearchParameters);
        }
    }

    // tk one in JMTSApp to be used
    public void sendErrorEmail(String subject, String message) {
        try {
            // send error message to developer's email
            jm.postJobManagerMail(null, null, subject, message);
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    public void savePetrolStation() {
        EntityManager em = getEntityManager();
        RequestContext context = RequestContext.getCurrentInstance();

        try {

            // Validate client
            if (!BusinessEntityUtils.validateName(currentPetrolStation.getClient().getName())) {
                messageManagement.setInvalidFormFieldMessage("Please enter a valid client name. If the client does not exist in the list, press the 'add/edit' button to enter a new client.");
                context.update("invalidFieldDialogForm");
                context.execute("invalidFieldDialog.show();");
                return;
            } else {
                Client currentClient = Client.findClientByName(em, currentPetrolStation.getClient().getName());
                if (currentClient == null) { // new client?
                    messageManagement.setInvalidFormFieldMessage("Please enter a valid client name. If the client does not exist in the list, press the 'add/edit' button to enter a new client.");
                    context.update("invalidFieldDialogForm");
                    context.execute("invalidFieldDialog.show();");
                    return;
                } else {
                    currentPetrolStation.setClient(currentClient);
                    currentClient.setDateLastAccessed(new Date());
                }
            }

            // validate name
            if ((currentPetrolStation.getName() == null) || (currentPetrolStation.getName().trim().isEmpty())) {
                messageManagement.setInvalidFormFieldMessage("Please enter a valid petrol station name");
                context.update("invalidFieldDialogForm");
                context.execute("invalidFieldDialog.show();");
                return;
            }

            // validate assignee
            Employee assignee = getEmployeeByName(em, currentPetrolStation.getLastAssignee().getName());
            if (assignee != null) {
                currentPetrolStation.setLastAssignee(assignee);
            } else {
                messageManagement.setInvalidFormFieldMessage("Please select an inspector.");
                context.update("invalidFieldDialogForm");
                context.execute("invalidFieldDialog.show();");
                return;
            }


            // save 
            em.getTransaction().begin();
            // save new objects in list first 
            for (PetrolPump pump : currentPetrolStation.getPetrolPumps()) {
                for (PetrolPumpNozzle nozzle : pump.getNozzles()) {
                    for (Certification certification : nozzle.getCertifications()) {
                        if (certification.getId() == null) {
                            BusinessEntityUtils.saveBusinessEntity(em, certification);
                        }
                    }

                    if (nozzle.getId() == null) {
                        BusinessEntityUtils.saveBusinessEntity(em, nozzle);
                    }
                }
                if (pump.getId() == null) {
                    BusinessEntityUtils.saveBusinessEntity(em, pump);
                }
            }

            Long id = BusinessEntityUtils.saveBusinessEntity(em, currentPetrolStation);
            em.getTransaction().commit();
            if (id == null) {
                context.execute("undefinedErrorDialog.show();");
                sendErrorEmail("An error occured while saving petrol station (ID): " + currentPetrolStation.getId(),
                        "Petrol station save error occured");
            }

            em.close();
            setDirty(false);
        } catch (Exception e) {
            System.out.println(e);
            context.execute("undefinedErrorDialog.show();");
            sendErrorEmail("An error occured while saving petrol station (ID): " + currentPetrolStation.getId(),
                    "Petrol station save error occured");
        }
    }

    public void createNewScale() {
        RequestContext context = RequestContext.getCurrentInstance();

        currentScale = new Scale();
        context.update("unitDialogForms:scaleDetailDialog");
        context.execute("scaleDialog.show();");

        setDirty(false);
    }

    public void createNewPetrolStation() {
        // handle user privilege and return if the user does not have
        // the privilege to do what they wish
        RequestContext context = RequestContext.getCurrentInstance();
        EntityManager em = getEntityManager();

        currentPetrolStation = new PetrolStation();
        context.update("unitDialogForms:petrolStationDetailDialog");
        context.execute("petrolStationDialog.show();");

        // add pump and nozzles
        PetrolPump pump = new PetrolPump();
        pump.setNumber("1");

        PetrolPumpNozzle nozzle = new PetrolPumpNozzle();
        nozzle.setNumber("1");
        nozzle.setTestMeasures("5,20");
        pump.getNozzles().add(nozzle);

        nozzle = new PetrolPumpNozzle();
        nozzle.setNumber("2");
        nozzle.setTestMeasures("5,20");
        pump.getNozzles().add(nozzle);

        currentPetrolStation.getPetrolPumps().add(pump);

        // set default cert. dates
        Date certDate = new Date();
        Date expDate = BusinessEntityUtils.getModifiedDate(certDate, Calendar.MONTH, 6); //tk 6 shud be in options

        currentPetrolStation.getCertification().setDateIssued(certDate);
        currentPetrolStation.getCertification().setExpiryDate(expDate);

        em.close();

        setDirty(false);
    }

    public void updatePetrolStationClient() {

        Client client = Client.findClientByName(getEntityManager(), currentPetrolStation.getClient().getName());
        if (client != null) {
            currentPetrolStation.setClient(client);
        }

        setDirty(true);
    }

    public void updateCurrentScaleClient() {

        Client client = Client.findClientByName(getEntityManager(), currentScale.getClient().getName());
        if (client != null) {
            currentScale.setClient(client);
        }

        setDirty(true);
    }

    // Edit the client via the ClientManager
    public void editPetrolStationClient() {

        //ClientManager clientManager = JMTSApp.findBean("clientManager");

        // Try to retrieve the client from the database if it exists
        Client client = Client.findClientByName(getEntityManager(), currentPetrolStation.getClient().getName());
        if (client != null) {
            currentPetrolStation.setClient(client);
            clientManagement.setClient(client);
        } else {
            clientManagement.setClient(currentPetrolStation.getClient());
        }

        clientManagement.setComponentsToUpdate(":unitDialogForms:petrolStationClient");
        setDirty(true);
    }

    // Edit the client via the ClientManager
    public void editScaleClient() {

//        ClientManager clientManager = JMTSApp.findBean("clientManager");

        // Try to retrieve the client from the database if it exists
        Client client = Client.findClientByName(getEntityManager(), currentScale.getClient().getName());
        if (client != null) {
            currentScale.setClient(client);
            clientManagement.setClient(client);
        } else {
            clientManagement.setClient(currentPetrolStation.getClient());
        }

        clientManagement.setComponentsToUpdate(":unitDialogForms:scaleClient");
        setDirty(true);
    }

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

    public void updatePetrolStationLastAssignee() {
        currentPetrolStation.setLastAssignee(getEmployeeByName(getEntityManager(), currentPetrolStation.getLastAssignee().getName()));

        setDirty(true);
    }

    public void cancelPetrolStationEdit() {
        setDirty(false);
        if (currentSearchParameters != null) {
            doPetrolStationSearch(currentSearchParameters);
        }
    }

    public void cancelScaleEdit() {
        setDirty(false);
        if (currentSearchParameters != null) {
            doScaleSearch(currentSearchParameters);
        }
    }

    public void updatePetrolStation() {
        setDirty(true);
    }

    public void updatePetrolPump() {
        setDirty(true);
    }

    /**
     * Check if a nozzle has the certificate of its assoc. pump. Presently the
     * certificate issue and expiry dates are used for this
     *
     * @param pump
     * @return
     */
    private Boolean doesNozzleHavePumpCertificate(PetrolPump pump, PetrolPumpNozzle nozzle) {
        Certification pumpCert = pump.getCertification();

        for (Certification nozzleCert : nozzle.getCertifications()) {
            if (BusinessEntityUtils.areDatesEqual(pumpCert.getDateIssued(), nozzleCert.getDateIssued())
                    && BusinessEntityUtils.areDatesEqual(pumpCert.getExpiryDate(), nozzleCert.getExpiryDate())) {
                return true;
            }
        }

        return false;
    }

    private void updatePetroPumpNozzleCertificates(PetrolPump pump, PetrolPumpNozzle nozzle) {
        if (!doesNozzleHavePumpCertificate(pump, nozzle)) {
            nozzle.getCertifications().add(new Certification(pump.getCertification()));
        }
    }

    public void updatePetrolStationDateCertified() {
        // tk update pump/nozzle certification     
        Date certDate = getCurrentPetrolStation().getCertification().getDateIssued();
        Date expDate = BusinessEntityUtils.getModifiedDate(certDate, Calendar.MONTH, 6); //tk 6 shud be in options

        getCurrentPetrolStation().getCertification().setExpiryDate(expDate);

        for (PetrolPump pump : getCurrentPetrolStation().getPetrolPumps()) {
            pump.getCertification().setDateIssued(certDate); // tk set in nozzles too
            pump.getCertification().setExpiryDate(expDate);

            // tk update nozzle
            for (PetrolPumpNozzle nozzle : pump.getNozzles()) {
                updatePetroPumpNozzleCertificates(pump, nozzle);
            }
        }

        setDirty(true);
    }

    public void updatePetrolStationDateCertificationDue() {
        // tk update pump/nozzle certification          

        Date date = getCurrentPetrolStation().getCertification().getExpiryDate();

        for (PetrolPump pump : getCurrentPetrolStation().getPetrolPumps()) {
            pump.getCertification().setExpiryDate(date); // tk set in nozzles too

            // tk update nozzle
            for (PetrolPumpNozzle nozzle : pump.getNozzles()) {
                updatePetroPumpNozzleCertificates(pump, nozzle);
            }

        }


        setDirty(true);
    }

    public void updatePetrolPumpDateCertified() {
        Date certDate = getCurrentPetrolPump().getCertification().getDateIssued();
        Date expDate = BusinessEntityUtils.getModifiedDate(certDate, Calendar.MONTH, 6); //tk 6 shud be in options

        getCurrentPetrolPump().getCertification().setExpiryDate(expDate);

        // tk update nozzle
        for (PetrolPumpNozzle nozzle : getCurrentPetrolPump().getNozzles()) {
            updatePetroPumpNozzleCertificates(getCurrentPetrolPump(), nozzle);
        }

        // tk add new certiif required for nozzle

        setDirty(true);
    }

    public void updatePetrolPumpDateCertificationDue() {
        // tk update nozzle
        for (PetrolPumpNozzle nozzle : getCurrentPetrolPump().getNozzles()) {
            updatePetroPumpNozzleCertificates(getCurrentPetrolPump(), nozzle);
        }

        setDirty(true);
    }

    public void updatePetrolPumpNozzle() {
        setDirty(true);
    }

    public void editPetrolStation() {
    }

    public void editScale() {
    }

    /**
     * Do not allow deletion if its the only one left
     *
     * @return
     */
    public Boolean getCanDeletePetrolPump() {
        if (getCurrentPetrolStation().getPetrolPumps().size() == 1) {
            return false;
        } else {
            return true;
        }
    }

    public void deletePetrolPump() {

        RequestContext context = RequestContext.getCurrentInstance();

        getCurrentPetrolStation().getPetrolPumps().remove(currentPetrolPump);

        currentPetrolPump = new PetrolPump();
        currentPetrolPump.setNumber("?");

        context.update("unitDialogForms:petrolPumpTable");


        setDirty(true);
    }

    public void deletePetrolPumpNozzle() {

        RequestContext context = RequestContext.getCurrentInstance();

        getCurrentPetrolPump().getNozzles().remove(currentPetrolPumpNozzle);

        currentPetrolPumpNozzle = new PetrolPumpNozzle();
        currentPetrolPumpNozzle.setNumber("?");

        context.update("unitDialogForms:petrolPumpNozzleTable");


        setDirty(true);
    }

    public void createNewPump() {

        // this means the pump will added to the station when oked.
        addPetrolPump = true;

        currentPetrolPump = new PetrolPump();

        PetrolPumpNozzle nozzle = new PetrolPumpNozzle();
        nozzle.setNumber("1");
        nozzle.setTestMeasures("5,20");
        currentPetrolPump.getNozzles().add(nozzle);

        nozzle = new PetrolPumpNozzle();
        nozzle.setNumber("2");
        nozzle.setTestMeasures("5,20");
        currentPetrolPump.getNozzles().add(nozzle);
    }

    public void createNewNozzle() {

        RequestContext context = RequestContext.getCurrentInstance();

        // this means the nozzle will added to the station when oked.
        addPetrolPumpNozzle = true;

        currentPetrolPumpNozzle = new PetrolPumpNozzle();
        currentPetrolPumpNozzle.setTestMeasures("5,20");

        context.update("unitDialogForms:petrolPumpNozzleDetailDialog");
    }

    public void updateCurrentPetrolPumpManufacturer() {

        try {
            EntityManager em = getEntityManager();
//            Manufacturer manufacturer = jm.getManufacturerByName(em, getCurrentPetrolPump().getManufacturer().getName());
//            currentPetrolPump.setManufacturer(manufacturer);

            // get/validate pump manufacturer
            Manufacturer manufacturer = jm.getValidManufacturer(em, getCurrentPetrolPump().getManufacturer().getName());
            if (manufacturer != null) {
                getCurrentPetrolPump().setManufacturer(manufacturer);
            } else {
                getCurrentPetrolPump().setManufacturer(jm.getDefaultManufacturer(em, "--"));
            }
            em.close();

            setDirty(true);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void updatePetrolPumpsList() {
        RequestContext context = RequestContext.getCurrentInstance();

        if (addPetrolPump) {
            currentPetrolStation.getPetrolPumps().add(currentPetrolPump);
            addPetrolPump = false;
            setDirty(true);
        }
    }

    public void updatePetrolPumpNozzlesList() {
        RequestContext context = RequestContext.getCurrentInstance();

        if (addPetrolPumpNozzle) {
            currentPetrolPump.getNozzles().add(currentPetrolPumpNozzle);
            addPetrolPumpNozzle = false;
            setDirty(true);
        }

    }

    public void cancelPetrolPumpEdit() {
        addPetrolPump = false;

        if (currentSearchParameters != null) {
            doPetrolStationSearch(currentSearchParameters);
        }
    }

    public void cancelPetrolPumpNozzleEdit() {
        addPetrolPumpNozzle = false;

        if (currentSearchParameters != null) {
            doPetrolStationSearch(currentSearchParameters);
        }
    }

    /**
     * Do not allow deletion if its the only one left
     *
     * @return
     */
    public Boolean getCanDeletePetrolPumpNozzle() {
        if (getCurrentPetrolPump().getNozzles().size() == 1) {
            return false;
        } else {
            return true;
        }
    }

    public void updatePetrolPumpNozzleManufacturer() {
        try {
            EntityManager em = getEntityManager();
            Manufacturer manufacturer = jm.getValidManufacturer(em, getCurrentPetrolPumpNozzle().getManufacturer().getName());
            if (manufacturer != null) {
                getCurrentPetrolPumpNozzle().setManufacturer(manufacturer);
            } else {
                getCurrentPetrolPumpNozzle().setManufacturer(jm.getDefaultManufacturer(em, "--"));
            }
            em.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void updateCurrentScaleManufacturer() {
        try {
            EntityManager em = getEntityManager();

            Manufacturer manufacturer = jm.getValidManufacturer(em, getCurrentScale().getManufacturer().getName());
            if (manufacturer != null) {
                getCurrentScale().setManufacturer(manufacturer);
            } else {
                getCurrentScale().setManufacturer(jm.getDefaultManufacturer(em, "--"));
            }
            em.close();

            setDirty(true);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void saveCurrentScale() {
        RequestContext context = RequestContext.getCurrentInstance();

        try {
            EntityManager em = getEntityManager();
            // validate client          
            if (!BusinessEntityUtils.validateName(currentScale.getClient().getName())) {
                messageManagement.setInvalidFormFieldMessage("Please enter a valid client name. If the client does not exist in the list, press the 'add/edit' button to enter a new client.");
                context.update("invalidFieldDialogForm");
                context.execute("invalidFieldDialog.show();");
                return;
            } else {
                Client currentClient = Client.findClientByName(em, currentScale.getClient().getName());
                if (currentClient == null) { // new client?
                    messageManagement.setInvalidFormFieldMessage("Please enter a valid client name. If the client does not exist in the list, press the 'add/edit' button to enter a new client.");
                    context.update("invalidFieldDialogForm");
                    context.execute("invalidFieldDialog.show();");
                    return;
                } else {
                    currentScale.setClient(currentClient);
                    currentClient.setDateLastAccessed(new Date());
                }
            }

            // get valid manufacturer
            Manufacturer manufacturer = jm.getValidManufacturer(em, getCurrentScale().getManufacturer().getName());
            if (manufacturer != null) {
                getCurrentScale().setManufacturer(manufacturer);
            } else {
                getCurrentScale().setManufacturer(jm.getDefaultManufacturer(em, "--"));
            }

            em.getTransaction().begin();
            // save list of objects firt
            for (Sticker sticker : currentScale.getStickers()) {
                if (sticker.getId() == null) {
                    BusinessEntityUtils.saveBusinessEntity(em, sticker);
                }
            }
            BusinessEntityUtils.saveBusinessEntity(em, currentScale);
            em.getTransaction().commit();

            em.close();

            setDirty(false);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void closeScaleDialog2(CloseEvent closeEvent) {
        closeScaleDialog1(null);
    }

    public void closeScaleDialog1(ActionEvent actionEvent) {
        RequestContext context = RequestContext.getCurrentInstance();

        // prompt to save modified job before attempting to create new job
        if (getDirty()) {
            context.update("unitDialogForms:scaleSaveConfirm");
            context.execute("scaleSaveConfirmDialog.show();");

            return;
        }

        setDirty(false);
        // refresh search if possible
        if (currentSearchParameters != null) {
            doScaleSearch(currentSearchParameters);
        }
    }

    public void updateScale() {
        setDirty(true);
    }

    public void deleteScale() {
        System.out.println("Not yet impl.");
    }

    public Date getCurrentDate() {
        return new Date();
    }
}
