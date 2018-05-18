/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compliance;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author dbennett
 */
@Named(value = "clientManager")
@SessionScoped
public class ClientManager extends jm.com.dpbennett.jobmanagementlibrary.ClientManager implements Serializable {

    public ClientManager() {
        super();
    }
}
