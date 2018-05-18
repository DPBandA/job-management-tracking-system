/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compliance;

import java.io.Serializable;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;

/**
 *
 * @author dbennett
 */
@Named(value = "complianceManager")
@SessionScoped
public class ComplianceManager extends jm.com.dpbennett.jobmanagementlibrary.ComplianceManager implements Serializable {
      /**
     * Creates a new instance of ComplianceManager
     */
    public ComplianceManager() {
       super();
    } 
    
}
