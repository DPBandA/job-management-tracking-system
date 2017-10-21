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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.SessionScoped;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import jm.com.dpbennett.business.entity.Address;
import jm.com.dpbennett.business.entity.Client;
import jm.com.dpbennett.business.entity.Contact;
import jm.com.dpbennett.business.entity.Internet;
import jm.com.dpbennett.business.entity.JobManagerUser;
import jm.com.dpbennett.business.entity.utils.BusinessEntityUtils;
import org.primefaces.context.RequestContext;
import jm.com.dpbennett.business.entity.ClientOwner;
import jm.com.dpbennett.business.entity.management.ClientManagement;
import jm.com.dpbennett.business.entity.utils.PrimeFacesUtils;

/**
 *
 * @author dbennett
 */
@Named
@SessionScoped
public class ClientManager implements Serializable, ClientManagement {

    @PersistenceUnit(unitName = "JMTSPU") // tk to be put in resource bundle
    private EntityManagerFactory entityManagerFactory;
    private Boolean isDirty;
    private Boolean isNewClient;
    private Boolean isNewContact;
    private Boolean isNewAddress;
    private Boolean isToBeSaved;
    private Boolean isClientNameAndIdEditable;
    private Boolean isActiveClientsOnly;
    private Client currentClient;
    private Contact currentContact;
    private Address currentAddress;
    private ClientOwner clientOwner;
    private JobManagerUser user;
    private String searchText;
    private List<Client> foundClients;

    /**
     * Creates a new instance of ClientForm
     */
    public ClientManager() {
        isNewContact = false;
        isNewAddress = false;
        isNewClient = false;
        isToBeSaved = true;
        isClientNameAndIdEditable = false;
        foundClients = new ArrayList<>();
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public Boolean getIsNewContact() {
        return isNewContact;
    }

    public void setIsNewContact(Boolean isNewContact) {
        this.isNewContact = isNewContact;
    }

    public Boolean getIsNewAddress() {
        return isNewAddress;
    }

    public void setIsNewAddress(Boolean isNewAddress) {
        this.isNewAddress = isNewAddress;
    }

    public Boolean getIsActiveClientsOnly() {
        if (isActiveClientsOnly == null) {
            isActiveClientsOnly = true;
        }
        return isActiveClientsOnly;
    }

    public void setIsActiveClientsOnly(Boolean isActiveClientsOnly) {
        this.isActiveClientsOnly = isActiveClientsOnly;
    }

    public int getNumClientFound() {
        return getFoundClients().size();
    }

    public void doClientSearch() {
        if (searchText.trim().length() > 1) {
            if (isActiveClientsOnly) {
                foundClients = Client.findActiveClientsByFirstPartOfName(getEntityManager(), searchText);
            } else {
                foundClients = Client.findClientsByFirstPartOfName(getEntityManager(), searchText);
            }
        } else {
            foundClients = new ArrayList<>();
        }
    }

    public List<Client> getFoundClients() {
        return foundClients;
    }

    public void setFoundClients(List<Client> foundClients) {
        this.foundClients = foundClients;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public Boolean getIsNewClient() {
        return isNewClient;
    }

    public void setIsNewClient(Boolean isNewClient) {
        this.isNewClient = isNewClient;
    }

    public List<Address> completeClientAddress(String query) {
        List<Address> addresses = new ArrayList<>();

        try {

            for (Address address : getCurrentClient().getAddresses()) {
                if (address.toString().toUpperCase().contains(query.toUpperCase())) {
                    addresses.add(address);
                }
            }

            return addresses;
        } catch (Exception e) {

            System.out.println(e);
            return new ArrayList<>();
        }
    }

    public List<Contact> completeClientContact(String query) {
        List<Contact> contacts = new ArrayList<>();

        try {

            for (Contact contact : getCurrentClient().getContacts()) {
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

    public List<Address> getAddressesModel() {
        return getCurrentClient().getAddresses();
    }

    public List<Contact> getContactsModel() {
        return getCurrentClient().getContacts();
    }

    public void setClientOwner(ClientOwner clientHandler) {
        this.clientOwner = clientHandler;
    }

    public ClientOwner getClientOwner() {
        return clientOwner;
    }

    public Address getCurrentAddress() {
        if (currentAddress == null) {
            currentAddress = getCurrentClient().getBillingAddress();
        }
        return currentAddress;
    }

    public void setCurrentAddress(Address currentAddress) {
        this.currentAddress = currentAddress;
    }

    public Boolean getIsClientNameAndIdEditable() {
        return isClientNameAndIdEditable;
    }

    public void setIsClientNameAndIdEditable(Boolean isClientNameAndIdEditable) {
        this.isClientNameAndIdEditable = isClientNameAndIdEditable;
    }

    public JobManagerUser getUser() {
        return this.user;
    }

    public void setUser(JobManagerUser user) {
        this.user = user;
    }

    public void editClient() {
        setIsToBeSaved(true);
    }

    public void editSelectedClient() {
        setClientOwner(null);
        setCurrentAddress(null);
        setCurrentContact(null);
        setIsToBeSaved(true);

        PrimeFacesUtils.openDialog(null, "clientDialog", true, true, true, 420, 700);

    }

    public Boolean getIsToBeSaved() {
        return isToBeSaved;
    }

    @Override
    public void setIsToBeSaved(Boolean isToBeSaved) {
        this.isToBeSaved = isToBeSaved;
    }

    public void updateClient() {
        setIsDirty(true);
    }
    
    public void updateClientName(AjaxBehaviorEvent event) {        
        currentClient.setName(currentClient.getName().trim());
        
        setIsDirty(true);
    }

    public void updateClientOwner(Boolean isDirty) {
        if (getClientOwner() != null) {
            getClientOwner().setIsDirty(isDirty);
        }
    }

    public void updateCurrentContact() {
        setIsDirty(true);
    }

    public void updateCurrentAddress() {
        setIsDirty(true);
    }

    @Override
    public void createNewClient(Boolean active) {
        currentClient = new Client("", active);

        currentClient.setDateEntered(new Date());
        if (getUser() != null) {
            currentClient.setEnteredBy(getUser().getEmployee());
        }
        isNewClient = true;
    }

    public void createNewClient() {
        createNewClient(true);
        setClientOwner(null);
        setCurrentAddress(getCurrentClient().getBillingAddress());
        setCurrentContact(getCurrentClient().getMainContact());
        setIsToBeSaved(true);
        setIsClientNameAndIdEditable(getUser().getPrivilege().getCanAddClient());

        PrimeFacesUtils.openDialog(null, "clientDialog", true, true, true, 420, 700);
    }

    public Boolean getIsDirty() {
        return isDirty;
    }

    public void setIsDirty(Boolean isDirty) {
        this.isDirty = isDirty;
        updateClientOwner(isDirty);
    }

    @Override
    public Client getCurrentClient() {
        if (currentClient == null) {
            return new Client("");
        }
        return currentClient;
    }

    @Override
    public void setCurrentClient(Client currentClient) {
        this.currentClient = currentClient;
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

    public void cancelClientEdit(ActionEvent actionEvent) {

        setIsDirty(false);
        isNewClient = false;

        RequestContext.getCurrentInstance().closeDialog(null);
    }

    public EntityManager getEntityManager() {

        return entityManagerFactory.createEntityManager();

    }

    public void okClient() {

        try {

            if (isNewClient) {
                getCurrentClient().setDateFirstReceived(new Date());
            }

            if (isToBeSaved) {
                currentClient.save(getEntityManager());
                isDirty = false;
            }

            // Pass edited object to the currentClient owner
            if (clientOwner != null) {
                clientOwner.setClient(getCurrentClient());
                clientOwner.setBillingAddress(currentAddress);
                clientOwner.setContact(currentContact);
            }

            isNewClient = false;

            RequestContext.getCurrentInstance().closeDialog(null);

        } catch (Exception e) {
            isNewClient = false;
            System.out.println(e);
        }
    }

    public Boolean getIsClientValid() {
        return BusinessEntityUtils.validateText(getCurrentClient().getName());
    }

    public void removeContact() {
        getCurrentClient().getContacts().remove(currentContact);
        setIsDirty(true);
        currentContact = null;
    }

    public void removeAddress() {
        getCurrentClient().getAddresses().remove(currentAddress);
        setIsDirty(true);
        currentAddress = null;
    }

    public Contact getCurrentContact() {
        if (currentContact == null) {
            currentContact = getCurrentClient().getMainContact();
        }
        return currentContact;
    }

    public void setCurrentContact(Contact currentContact) {
        this.currentContact = currentContact;
    }

    public void okCurrentContact() {

        currentContact = currentContact.prepare();

        if (isNewContact) {
            getCurrentClient().getContacts().add(currentContact);
            isNewContact = false;
        }

        RequestContext.getCurrentInstance().execute("contactFormDialog.hide();");

    }

    public void okCurrentAddress() {

        currentAddress = currentAddress.prepare();

        if (isNewAddress) {
            getCurrentClient().getAddresses().add(currentAddress);
            isNewAddress = false;
        }

        RequestContext.getCurrentInstance().execute("addressFormDialog.hide();");

    }

    public void createNewContact() {
        currentContact = null;

        for (Contact contact : getCurrentClient().getContacts()) {
            if (contact.getFirstName().trim().isEmpty()) {
                isNewContact = false;
                currentContact = contact;
                currentContact.setType("Main");
                break;
            }
        }

        if (currentContact == null) {
            isNewContact = true;
            currentContact = new Contact();
            currentContact.setType("Main");
            currentContact.setInternet(new Internet());
        }
        
        setIsDirty(false);
    }

    public void createNewAddress() {
        currentAddress = null;

        // Find an existing invalid or blank address and use it as the neww address
        for (Address address : getCurrentClient().getAddresses()) {
            if (address.getAddressLine1().trim().isEmpty()) {
                isNewAddress = false;
                currentAddress = address;
                currentAddress.setType("Billing");
                break;
            }
        }

        // No existing blank or invalid address found so creating new one.
        if (currentAddress == null) {
            isNewAddress = true;
            currentAddress = new Address();
            currentAddress.setType("Billing");
        }

        setIsDirty(false);
    }

    public List<Client> completeClient(String query) {
        EntityManager em = getEntityManager();

        try {
            List<Client> clients = Client.findActiveClientsByAnyPartOfName(em, query);

            closeEntityManager(em);

            return clients;

        } catch (Exception e) {
            System.out.println(e);
            return new ArrayList<>();
        } finally {
            closeEntityManager(em);
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
