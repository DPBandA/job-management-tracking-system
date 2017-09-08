/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.com.dpbennett.jmts;

import java.io.Serializable;
import java.util.Date;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import jm.com.dpbennett.business.entity.Client;
import jm.com.dpbennett.business.entity.ComplianceSurvey;
import jm.com.dpbennett.business.entity.Contact;
import jm.com.dpbennett.business.entity.EntryDocumentInspection;
import org.primefaces.context.RequestContext;

/**
 *
 * @author dbennett
 */
@Named(value = "complianceMobile")
@RequestScoped
public class ComplianceMobile implements Serializable {


    private ComplianceSurvey currentComplianceSurvey;

    /**
     * Creates a new instance of ComplianceManager
     */
    public ComplianceMobile() {
       
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
        //EntityManager em = getEntityManager1();

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

        // Documrnt inspection
        currentComplianceSurvey.setEntryDocumentInspection(new EntryDocumentInspection());

        currentComplianceSurvey.getEntryDocumentInspection().setCountryOfConsignment(" "); // moved to EntryDocumenInpection
        currentComplianceSurvey.setDateOfSurvey(new Date());
       
        context.addCallbackParam("isConnectionLive", true);
    }
    
     public String test() {
         System.out.println("Yes!!");    
         return null;
     }
}
