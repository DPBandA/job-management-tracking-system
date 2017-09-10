/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.com.dpbennett.jmts;

import java.io.Serializable;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import jm.com.dpbennett.business.entity.JobManagerUser;
import jm.com.dpbennett.business.management.UserManagement;
import org.primefaces.context.RequestContext;

/**
 *
 * @author dbennett
 */
@Named(value = "administration")
@SessionScoped
public class Administration implements Serializable {

    @PersistenceUnit(unitName = "JMTSPU")
    private EntityManagerFactory EMF1;
    private UserManagement userManagement;    
    private Boolean dirty;

    public Administration() {
        userManagement = App.findBean("main");
    }

    private EntityManagerFactory getEMF1() {
        return EMF1;
    }

    public EntityManager getEntityManager1() {
        return getEMF1().createEntityManager();
    }

    public UserManagement getUserManagement() {
        return userManagement;
    }

    public void setUserManagement(UserManagement userManagement) {
        this.userManagement = userManagement;
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
                    if (userManagement.validateAndAssociateUser(em, userManagement.getUsername(), userManagement.getPassword())) {
//                    if (true) {
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

    public String logout() {
        RequestContext context = RequestContext.getCurrentInstance();

        userManagement.setUserLoggedIn(false);
        userManagement.setShowLogin(true);
        userManagement.setPassword("");
        userManagement.setUsername("");

        if (dirty) {
            // save what needs to be saved
        }
        
        userManagement.setLogonMessage("Please provide your login details below:");
        userManagement.setUser(new JobManagerUser());

        context.execute("PrimeFaces.changeTheme('" + userManagement.getUser().getUserInterfaceThemeName() + "');");

        return "logout";
    }

    public void setDirty(Boolean dirty) {
        this.dirty = dirty;
    }

    public void updateJob() {
        setDirty(true);
    }
}
