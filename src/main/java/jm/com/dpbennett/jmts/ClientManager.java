/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.com.dpbennett.jmts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import jm.com.dpbennett.business.entity.Address;
import jm.com.dpbennett.business.entity.BusinessEntityManager;
import jm.com.dpbennett.business.entity.Client;
import jm.com.dpbennett.business.entity.ClientHandler;
import jm.com.dpbennett.business.entity.Contact;
import jm.com.dpbennett.business.entity.Employee;
import jm.com.dpbennett.business.entity.Internet;
import jm.com.dpbennett.business.entity.JobManagerUser;
import jm.com.dpbennett.business.entity.PhoneNumber;
import jm.com.dpbennett.business.management.ClientManagement;
import jm.com.dpbennett.business.utils.BusinessEntityUtils;
import org.primefaces.context.RequestContext;

/**
 *
 * @author dbennett
 */
@Named(value = "clientManager")
@SessionScoped
public class ClientManager implements Serializable, ClientManagement {

    // private EntityManager entityManager;
    @PersistenceUnit(unitName = "JMTSPU")
    private EntityManagerFactory localEntityManagerFactory;
    // This factory is used by external clients. May be removed in the future
    private EntityManagerFactory externalEntityManagerFactory;
    private Client client;
    private Boolean dirty;
    private String componentsToUpdate;
    private Boolean isNewContact;
    private Boolean isNewAddress;
    private Contact currentContact;
    private Address currentAddress;
    private Boolean taxRegistrationNumberRequired;
    private String dialogMessage;
    private String dialogMessageHeader;
    private String dialogMessageSeverity;
    private Boolean save;
    private Boolean clientNameEditable;
    private BusinessEntityManager businessEntityManager;
    private Client clientBackup;
    private Boolean isNewClient;
    private String clientSearchText;
    private List<Client> foundClients;
    private Main main;
    private ClientHandler clientHandler;
    private String clientBillingAddressString;

    /**
     * Creates a new instance of ClientForm
     */
    public ClientManager() {
        isNewContact = false;
        isNewAddress = false;
        taxRegistrationNumberRequired = false;
        save = true;
        clientNameEditable = true;
        clientSearchText = "";
        foundClients = new ArrayList<>();
    }
    
    public void contactSelected() {
        changeContactType(getCurrentContact().toString());
    }
    
    public void changeContactType(String contactString) {
        // Reset address types 
        for (Contact contact : getClient().getContacts()) {
            if (contact.getType().equals("Main")) {                
                contact.setType("General");
            }
        }
        // Set new main type
        for (Contact contact : getClient().getContacts()) {
            if (contact.toString().trim().equals(contactString.trim())) {                
                contact.setType("Main");
            }
        }
    }
    
    public void addressSelected() {       
        changeBillingAddress(getCurrentAddress().toString());       
    }
    
    public AddressDataModel getAddressesModel() {
        return new AddressDataModel(getClient().getAddresses());
    }
    
     public ContactDataModel getContactsModel() {
        return new ContactDataModel(getClient().getContacts());
    }

    public String getClientBillingAddressString() {
        return clientBillingAddressString;
    }

    public void setClientBillingAddressString(String clientBillingAddressString) {
        this.clientBillingAddressString = clientBillingAddressString;
    }
    
    public List<SelectItem> getClientAddresses() {
        ArrayList<SelectItem> addresses = new ArrayList<>();

        //int count = 0;

        for (Address address : getClient().getAddresses()) {
            //addresses.add(new SelectItem(address.toString(), BusinessEntityUtils.getShortenedString(address.toString(), 30) + "..."));
            addresses.add(new SelectItem(address.toString(), address.toString()));
        }

        return addresses;
    }

    public void setClientHandler(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    public Address getCurrentAddress() {
        if (currentAddress == null) {
            return new Address();
        }
        return currentAddress;
    }

    public void setCurrentAddress(Address currentAddress) {
        this.currentAddress = currentAddress;
    }

    public Boolean getClientNameEditable() {
        return clientNameEditable;
    }

    public void setClientNameEditable(Boolean clientNameEditable) {
        this.clientNameEditable = clientNameEditable;
    }

    public EntityManager getLocalEntityManager() {
        return localEntityManagerFactory.createEntityManager();
    }

    public Main getMain() {
        if (main == null) {
            main = App.findBean("main");
        }
        return main;
    }

    public JobManagerUser getUser() {
        return getMain().getUser();
    }

    // Edit the client via the ClientManager
    public void editClient() {

        //EntityManager em = getEntityManager();

        setSave(true);
        //clientManager.setBusinessEntityManager(this);
        setComponentsToUpdate(":clientsTableForm:clientsTable");

    }

    public List<Client> getFoundClients() {
        return foundClients;
    }

    public void setFoundClients(List<Client> foundClients) {
        this.foundClients = foundClients;
    }

    public void doClientSearch() {
        if (clientSearchText.trim().length() > 0) {
//            foundClients = Client.findClientsByAnyPartOfName(getLocalEntityManager(), clientSearchText);
            foundClients = Client.findActiveClientsByAnyPartOfName(getLocalEntityManager(), clientSearchText);
        } else {
            foundClients = new ArrayList<>();
            getMain().displayCommonMessageDialog(null, "Please provide at least 1 character for the search text.", "Insufficient Characters", "alert");
        }
    }

    public String getClientSearchText() {
        return clientSearchText;
    }

    public void setClientSearchText(String clientSearchText) {
        this.clientSearchText = clientSearchText;
    }

    public Client getClientBackup() {
        return clientBackup;
    }

    public void setClientBackup(Client clientBackup) {
        this.clientBackup = clientBackup;
    }

    public BusinessEntityManager getBusinessEntityManager() {
        return businessEntityManager;
    }

    @Override
    public void setBusinessEntityManager(BusinessEntityManager businessEntityManager) {
        this.businessEntityManager = businessEntityManager;
    }

    public Boolean getSave() {
        return save;
    }

    @Override
    public void setSave(Boolean save) {
        this.save = save;
    }

    public void displayMessageDialog(String dialogMessage,
            String dialogMessageHeader,
            String dialoMessageSeverity) {
        RequestContext context = RequestContext.getCurrentInstance();

        setDialogMessage(dialogMessage);
        setDialogMessageHeader(dialogMessageHeader);
        setDialogMessageSeverity(dialoMessageSeverity);
        context.update("clientManagerMessageDialogForm");
        context.execute("clientManagerMessageDialog.show();");
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

    public Boolean getTaxRegistrationNumberRequired() {
        if (taxRegistrationNumberRequired == null) {
            taxRegistrationNumberRequired = false;
        }
        return taxRegistrationNumberRequired;
    }

    public void setTaxRegistrationNumberRequired(Boolean taxRegistrationNumberRequired) {
        this.taxRegistrationNumberRequired = taxRegistrationNumberRequired;
    }

    @Override
    public void setExternalEntityManagerFactory(EntityManagerFactory externalEntityManagerFactory) {
        this.externalEntityManagerFactory = externalEntityManagerFactory;
    }

    public EntityManager getExternalEntityManager() {
        return externalEntityManagerFactory.createEntityManager();
    }

    public List<Address> completeAddress(String query) {
        // tk
        return client.getAddresses();
    }
    
    public void changeBillingAddress(String addressString) {
        // Reset address types 
        for (Address address : getClient().getAddresses()) {
            if (address.getType().equals("Billing")) {                
                address.setType("General");
            }
        }
        // Set new billing type
        for (Address address : getClient().getAddresses()) {
            if (address.toString().trim().equals(addressString.trim())) {
                address.setType("Billing");
            }
        }
    }

    public void updateBillingAddress() {
        setDirty(true);
        System.out.println("selected val: " + clientBillingAddressString); // tk take action
//        // Reset address types 
//        for (Address address : getClient().getAddresses()) {
//            if (address.getType().equals("Billing")) {
//                System.out.println("billing found and change to general");
//                address.setType("General");
//            }
//        }
//        // Set new billing type
//        for (Address address : getClient().getAddresses()) {
//            if (address.toString().trim().equals(clientBillingAddressString.trim())) {
//                System.out.println("setting to billing");
//                address.setType("Billing");
//            }
//        }
        changeBillingAddress(clientBillingAddressString);
        
        // Remove selection in table
        currentAddress = null;
        clientBillingAddressString = getClient().getBillingAddress().toString(); //tk
    }

    public void updateClient() {
        setDirty(true);
    }

    public void updateCurrentContact() {
        getCurrentContact().setName(getCurrentContact().toString());
        setDirty(true);
    }

    public void updateCurrentAddress() {
        getCurrentAddress().setName(getCurrentAddress().toString());
        setDirty(true);
    }

    @Override
    public void createNewClient(Client existingClient, Client newClient) {
        if (existingClient != null) {
            clientBackup = existingClient;
        }
        client = newClient;
        getClient().setDateEntered(new Date());
        System.out.println("client creator id: " + getUser().getEmployee().getId()); // tk
        getClient().setEnteredBy(getUser().getEmployee());
        isNewClient = true;
    }

    @Override
    public String getComponentsToUpdate() {
        return componentsToUpdate;
    }

    @Override
    public void setComponentsToUpdate(String componentsToUpdate) {
        this.componentsToUpdate = componentsToUpdate;
    }

    public Boolean getDirty() {
        return dirty;
    }

    public void setDirty(Boolean dirty) {
        this.dirty = dirty;
        if (businessEntityManager != null) {
            businessEntityManager.setDirty(dirty);
        }
    }

    @Override
    public Client getClient() {
        if (client == null) {
            return new Client("");
        }
        return client;
    }

    @Override
    public void setClient(Client client) {
        this.client = client;
        setClientBackup(new Client(getClient(), getClient().getActive()));
        clientBillingAddressString = null;
        currentAddress = getClient().getBillingAddress(); //tk
        isNewClient = false;
    }

    public Client getClientById(EntityManager em, Long Id) {

        try {
            return em.find(Client.class, Id);
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    //   + " AND c.active = 1"
    public Client getActiveClientByName(EntityManager em, String clientName) {

        try {
            String newClientName = clientName.trim().replaceAll("'", "''");

            List<Client> clients = em.createQuery("SELECT c FROM Client c "
                    + "WHERE UPPER(c.name) "
                    + "= '" + newClientName.toUpperCase() + "'" + " AND c.active = 1",
                    Client.class).getResultList();

            if (!clients.isEmpty()) {
                return clients.get(0);
            }
            return null;
        } catch (Exception e) {
            System.out.println("Error getting client: possibly null name");
            return null;
        }
    }

    public Client getClientByName(EntityManager em, String clientName) {

        try {
            String newClientName = clientName.trim().replaceAll("'", "''");

            List<Client> clients = em.createQuery("SELECT c FROM Client c "
                    + "WHERE UPPER(c.name) "
                    + "= '" + newClientName.toUpperCase() + "'", Client.class).getResultList();

            if (!clients.isEmpty()) {
                return clients.get(0);
            }
            return null;
        } catch (Exception e) {
            System.out.println("Error getting client: possibly null name");
            return null;
        }
    }

    public void cancelClientEdit(ActionEvent actionEvent) {
        // This is the default component to update which is the 
        // Client Database table
        setComponentsToUpdate("@form");

        setClientNameEditable(true);

        if (clientBackup != null) {
            getClient().doCopy(clientBackup);
        }
        setDirty(false);
        isNewClient = false;

        if (businessEntityManager != null) {
            businessEntityManager.setDirty(false);
        }
    }

    public Boolean validateClient(Boolean displayAlert) {
        if ((getClient().getName() == null) || (getClient().getName().trim().equals(""))) {
            // display message
            if (displayAlert) {
                displayMessageDialog("Please enter a name for the client", "No Client Name", "info");
            }
            return false;
        }
        if (!BusinessEntityUtils.validateName(getClient().getName())) {
            // display message
            if (displayAlert) {
                displayMessageDialog("Please enter a valid client name", "Invalid Client Name", "info");
            }
            return false;
        }


        // Validate TRN if required
        if (getTaxRegistrationNumberRequired()) {
            if ((getClient().getTaxRegistrationNumber() == null) || (getClient().getTaxRegistrationNumber().trim().equals(""))) {
                if (displayAlert) {
                    displayMessageDialog("Please enter a valid TRN for the client", "No Tax Registration Number (TRN)", "info");
                }
                return false;
            }
        }

        return true;
    }

    public void createNewClient() {
        createNewClient(null, new Client("", true));
        setSave(true);
    }

    public EntityManager getEntityManager() {
        if (externalEntityManagerFactory != null) {
            return getExternalEntityManager();
        } else {
            return getLocalEntityManager();
        }
    }

    public void okClient() {

        EntityManager em;

        RequestContext context = RequestContext.getCurrentInstance();

        try {

            em = getEntityManager();

            if (!validateClient(true)) {
                return;
            }

            // If we have reached this far we can hide the client dialog...for now
            context.execute("clientFormDialog.hide();");

            // This is the default component to update which is the 
            // Client Database table
            setComponentsToUpdate("@form");

            setClientNameEditable(true);

            // replace double quotes with two single quotes to avoid query issues
            getClient().setName(getClient().getName().replaceAll("\"", "''"));
            // replace & with &#38; query issues
            // save client and check for save error
            // indicate if this is a new client being created
            // this prevents creating two clients with the same name
            if (isNewClient) {
                // check if the client already exist
                String clientName = getClient().getName();
                Client currentClient = getActiveClientByName(em, clientName);
                if (currentClient != null) {
                    context.addCallbackParam("clientExists", true);
                    displayMessageDialog("This client already exists", "Client Exists", "info");
                    return;
                }
                getClient().setDateFirstReceived(new Date());
            }
            isNewClient = false;

            // Get entered by 
            if (getClient().getEnteredBy().getId() != null) {
                Employee e = Employee.findEmployeeById(em, getClient().getEnteredBy().getId());
                getClient().setEnteredBy(e);
            }

            if (save) {
                saveClient(em, true);
                em.close();
            }

            // Set the client of the class that invoked the Client Manager
            if (clientHandler != null) {
                clientHandler.setClient(getClient());
            }

        } catch (Exception e) {
            isNewClient = false;
            System.out.println(e);
        }
    }

    public void saveClient(EntityManager em, Boolean useTransaction) {

        try {
            if (useTransaction) {
                em.getTransaction().begin();
            }
            // save list of objects first if they are new
            for (Contact contact : getClient().getContacts()) {
                if (contact.getId() == null) {
                    // Save objects in contact first
                    for (PhoneNumber phoneNumber : contact.getPhoneNumbers()) {
                        if (phoneNumber.getId() == null) {
                            BusinessEntityUtils.saveBusinessEntity(em, phoneNumber);
                        }
                    }
                    for (Address address : contact.getAddresses()) {
                        if (address.getId() == null) {
                            BusinessEntityUtils.saveBusinessEntity(em, address);
                        }
                    }
                    BusinessEntityUtils.saveBusinessEntity(em, contact);
                }
            }
            for (Address address : getClient().getAddresses()) {
                if (address.getId() == null) {
                    BusinessEntityUtils.saveBusinessEntity(em, address);
                }
            }
            BusinessEntityUtils.saveBusinessEntity(em, getClient());

            if (useTransaction) {
                em.getTransaction().commit();
            }

            // reload from database to ensure that all entities recently saved
            // do not have null ids.
            setClient(em.find(Client.class, getClient().getId()));

            setDirty(false);

            if (businessEntityManager != null) {
                businessEntityManager.setDirty(false);
            }

        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public void removeContact() {
        getClient().getContacts().remove(currentContact);
        setDirty(true);
        currentContact = null;
    }
    
     public void removeAddress() {
        getClient().getAddresses().remove(currentAddress);
        setDirty(true);
        currentAddress = null;
    }

    public Contact getCurrentContact() {
        if (currentContact == null) {
            return new Contact();
        }
        return currentContact;
    }

    public void setCurrentContact(Contact currentContact) {
        this.currentContact = currentContact;
    }

    public void okCurrentContact() {
        if (isNewContact) {
            getClient().getContacts().add(currentContact);
            isNewContact = false;
        }
    }

    public void okCurrentAddress() {
        if (isNewAddress) {
            getClient().getAddresses().add(currentAddress);
            clientBillingAddressString = null;
            isNewAddress = false;
        }
        if (getCurrentAddress().getType().equals("Billing")) {
            changeBillingAddress(getCurrentAddress().toString());
        }
    }

    public void createNewContact() {
        isNewContact = true;
        currentContact = new Contact();
        currentContact.setInternet(new Internet());
        setDirty(false);
    }

    public void createNewAddress() {
        isNewAddress = true;
        currentAddress = new Address();
        currentAddress.setType("Billing");
        setDirty(false);
    }

    public List getContactTypes() {

        return App.getStringListAsSortableSelectItems(getEntityManager(), "personalContactTypes");

    }

    public List<Client> completeClient(String query) {
        EntityManager em = getEntityManager();

        try {
            List<Client> clients = findActiveClientsByName(em, query);

            closeEntityManager(em);

            return clients;

        } catch (Exception e) {
            System.out.println(e);
            return new ArrayList<>();
        } finally {
            closeEntityManager(em);
        }
    }

    public List<Client> findActiveClientsByName(EntityManager em, String name) {

        try {
            String newName = name.replaceAll("'", "''");

            List<Client> clients =
                    em.createQuery("SELECT c FROM Client c WHERE UPPER(c.name) like '"
                    + newName.toUpperCase().trim() + "%'"
                    + " AND c.active = 1"
                    + " ORDER BY c.name", Client.class).getResultList();
            return clients;
        } catch (Exception e) {
            System.out.println(e);
            return new ArrayList<>();
        }
    }

    public List<String> completeClientName(String query) {
        try {
            return findActiveClientNames(getEntityManager(), query);

        } catch (Exception e) {
            System.out.println(e);

            return new ArrayList<>();
        }
    }

    public List<String> findActiveClientNames(EntityManager em, String name) {

        try {
            String newName = name.replaceAll("'", "''");

            List<String> names =
                    em.createQuery("SELECT c FROM Client c WHERE UPPER(c.name) like '"
                    + newName.toUpperCase().trim() + "%'"
                    + " AND c.active = 1"
                    + " ORDER BY c.name", String.class).getResultList();
            return names;
        } catch (Exception e) {
            System.out.println(e);
            return new ArrayList<>();
        }
    }

    public List<Client> findClientsByTRN(EntityManager em, String trn) {

        try {
            String newTrn = trn.replaceAll("'", "''");

            List<Client> clients =
                    em.createQuery("SELECT c FROM Client c where UPPER(c.taxRegistrationNumber) like '"
                    + newTrn.toUpperCase().trim() + "%' ORDER BY c.taxRegistrationNumber", Client.class).getResultList();
            return clients;
        } catch (Exception e) {
            System.out.println(e);
            return new ArrayList<Client>();
        }
    }

    public List<Client> getAllClients(EntityManager em) {

        try {
            List<Client> clients = em.createNamedQuery("findAllClients", Client.class).getResultList();
            return clients;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public List<Client> findClientsByName(EntityManager em, String name) {

        try {
            String newName = name.replaceAll("'", "''");

            List<Client> clients =
                    em.createQuery("SELECT c FROM Client c where UPPER(c.name) like '"
                    + newName.toUpperCase().trim()
                    + "%' ORDER BY c.name", Client.class).getResultList();
            return clients;
        } catch (Exception e) {
            System.out.println(e);
            return new ArrayList<>();
        }
    }

    public List<String> findClientNames(EntityManager em, String name) {

        try {
            String newName = name.replaceAll("'", "''");

            List<String> names =
                    em.createQuery("SELECT c FROM Client c where UPPER(c.name) like '"
                    + newName.toUpperCase().trim()
                    + "%' ORDER BY c.name", String.class).getResultList();
            return names;
        } catch (Exception e) {
            System.out.println(e);
            return new ArrayList<>();
        }
    }

    public void closeEntityManager(EntityManager em) {
        if (em != null) {
            if (em.isOpen()) {
                em.close();
            }
        }
    }
}
