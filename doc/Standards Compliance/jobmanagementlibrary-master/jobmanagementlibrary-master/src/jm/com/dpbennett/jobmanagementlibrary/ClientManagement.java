/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.com.dpbennett.jobmanagementlibrary;

import javax.persistence.EntityManagerFactory;
import jm.com.dpbennett.entity.BusinessEntityManager;
import jm.com.dpbennett.entity.Client;

/**
 *
 * @author dbennett
 */
public interface ClientManagement {

    public Client getClient();

    public void setClient(Client client);

    public void createNewClient(Client existingClient, Client newClient);

    public String getComponentsToUpdate();

    public void setComponentsToUpdate(String componentsToUpdate);

    public void setEntityManagerFactory(EntityManagerFactory entityManagerFactory);

    public void setSave(Boolean save);

    public void setBusinessEntityManager(BusinessEntityManager businessEntityManager);
}
