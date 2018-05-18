/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.com.dpbennett.jobmanagementlibrary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import jm.com.dpbennett.entity.Address;
import jm.com.dpbennett.entity.BusinessEntityManager;
import jm.com.dpbennett.entity.Client;
import jm.com.dpbennett.entity.Contact;
import jm.com.dpbennett.entity.Internet;
import jm.com.dpbennett.utils.BusinessEntityUtils;
import org.primefaces.context.RequestContext;

/**
 *
 * @author dbennett
 */
public class ClientManager implements ClientManagement, Serializable {

    //private EntityManager entityManager;
    private EntityManagerFactory entityManagerFactory;
    private Client client;
    private Boolean dirty;
    private String componentsToUpdate;
    private Boolean isNewContact;
    private Contact currentContact;
    private Boolean taxRegistrationNumberRequired;
    private String dialogMessage;
    private String dialogMessageHeader;
    private String dialogMessageSeverity;
    private Boolean save;
    private BusinessEntityManager businessEntityManager;
    private Client clientBackup;
    private Boolean isNewClient;

    public ClientManager() {
        isNewContact = false;
        taxRegistrationNumberRequired = false;
        save = true;
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
        return taxRegistrationNumberRequired;
    }

    public void setTaxRegistrationNumberRequired(Boolean taxRegistrationNumberRequired) {
        this.taxRegistrationNumberRequired = taxRegistrationNumberRequired;
    }

    @Override
    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }

    public void updateClient() {
        setDirty(true);
    }

    public void updateCurrentContact() {
        getCurrentContact().setName(getCurrentContact().toString());
        setDirty(true);
    }

    @Override
    public void createNewClient(Client existingClient, Client newClient) {
        if (existingClient != null) {
            clientBackup = existingClient;
        }
        client = newClient;
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
        //setClient(null);
        if (clientBackup != null) {
            getClient().doCopy(clientBackup);
        }
        setDirty(false);
        isNewClient = false;

        if (businessEntityManager != null) {
            businessEntityManager.setDirty(false);
        }
    }

    public Boolean validateClient() {
        if ((getClient().getName() == null) || (getClient().getName().trim().equals(""))) {
            // display message
            displayMessageDialog("Please enter a name for the client", "No Client Name", "info");
            return false;
        } else if (!BusinessEntityUtils.validateName(getClient().getName())) {
            // display message
            displayMessageDialog("Please enter a valid client name", "Invalid Client Name", "info");
            return false;
        }

        // Validate TRN if required
        if (getTaxRegistrationNumberRequired()) {
            if ((getClient().getTaxRegistrationNumber() == null) || (getClient().getTaxRegistrationNumber().trim().equals(""))) {
                displayMessageDialog("Please enter a valid TRN for the client", "No Tax Registration Number (TRN)", "info");
                return false;
            }
        }

        return true;
    }

    public void okClient() {

        RequestContext context = RequestContext.getCurrentInstance();

        try {

            EntityManager em = getEntityManager();

            if (!validateClient()) {
                return;
            }

            // If we have reached this far we can hide the client dialog...for now
            context.execute("clientFormDialog.hide();");

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

            if (save) {
                saveClient(em);
                em.close();
            }

        } catch (Exception e) {
            isNewClient = false;
            System.out.println(e);
        }
    }

    public void saveClient(EntityManager em) {

        try {
            em.getTransaction().begin();
            // save list of objects first if they are new
            for (Contact contact : getClient().getContacts()) {
                if (contact.getId() == null) {
                    BusinessEntityUtils.saveBusinessEntity(em, contact);
                }
            }
            for (Address address : getClient().getAddresses()) {
                if (address.getId() == null) {
                    BusinessEntityUtils.saveBusinessEntity(em, address);
                }
            }
            BusinessEntityUtils.saveBusinessEntity(em, getClient());
            em.getTransaction().commit();

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
        currentContact = null;
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

    public void createNewContact() {
        isNewContact = true;
        currentContact = new Contact();
        currentContact.setInternet(new Internet());
        setDirty(false);
    }

    public List getContactTypes() {
        ArrayList types = new ArrayList();

        // add items
        types.add(new SelectItem("General", "General"));
        types.add(new SelectItem("Main", "Main"));
        types.add(new SelectItem("Not associated", "Not associated"));

        return types;
    }

    public List<Client> completeClient(String query) {
        EntityManager em = getEntityManager();

        try {
            List<Client> clients = findActiveClientsByName(em, query);

            closeEntityManager(em);

            return clients;

        } catch (Exception e) {
            System.out.println(e);
            return new ArrayList<Client>();
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
            return new ArrayList<Client>();
        }
    }

    public List<String> completeClientName(String query) {
        try {
            return findActiveClientNames(getEntityManager(), query);

        } catch (Exception e) {
            System.out.println(e);

            return new ArrayList<String>();
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
            return new ArrayList<String>();
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
            return new ArrayList<Client>();
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
            return new ArrayList<String>();
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
