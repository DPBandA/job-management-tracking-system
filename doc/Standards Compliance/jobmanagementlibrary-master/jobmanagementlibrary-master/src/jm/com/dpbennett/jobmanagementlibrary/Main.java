/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.com.dpbennett.jobmanagementlibrary;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.ldap.InitialLdapContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import jm.com.dpbennett.entity.Employee;
import jm.com.dpbennett.entity.JobManagerUser;
import jm.com.dpbennett.utils.BusinessEntityUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.ToggleEvent;

/**
 *
 * @author dbennett
 */
public class Main implements UserManagement, MessageManagement, Serializable {

     @PersistenceUnit(unitName = "JMTSWebAppPU")
    private EntityManagerFactory EMF1;
    private JobManagement jm;
    private JobManagerUser user;
    private Boolean userLoggedIn = false;
    private Boolean showLogin = true;
    private String username = "";
    private String password = "";
    private String logonMessage;
    private Integer loginAttempts = 0;
    private String tabTitle = "Job Entry";
    private Boolean searchLayoutUnitCollapsed;
    private String invalidFormFieldMessage;

    /**
     * Creates a new instance of Main
     */
    public Main() {
        jm = new JobManagement();
        showLogin = true;
        searchLayoutUnitCollapsed = true;
        logonMessage = "Please provide your login details below:";
//        invalidFormFieldMessage = "";
    }

    /**
     * Creates a new instance of Main
     */
    public Main(String persistenceUnitName) {
        jm = new JobManagement();
        showLogin = true;
        searchLayoutUnitCollapsed = true;
        logonMessage = "Please provide your login details below:";
//        invalidFormFieldMessage = "";
        
        EMF1 = BusinessEntityUtils.getEntityManagerFactory(persistenceUnitName);
    }      

    public String getTabTitle() {
        return tabTitle;
    }

    public void setTabTitle(String tabTitle) {
        this.tabTitle = tabTitle;
    }

    public Boolean getSearchLayoutUnitCollapsed() {
        return searchLayoutUnitCollapsed;
    }

    public void setSearchLayoutUnitCollapsed(Boolean searchLayoutUnitCollapsed) {
        this.searchLayoutUnitCollapsed = searchLayoutUnitCollapsed;
    }

    @Override
    public String getLogonMessage() {
        return logonMessage;
    }

    @Override
    public void setLogonMessage(String logonMessage) {
        this.logonMessage = logonMessage;
    }

   
    @Override
    public Boolean getUserLoggedIn() {
        return userLoggedIn;
    }

    @Override
    public void setUserLoggedIn(Boolean userLoggedIn) {
        this.userLoggedIn = userLoggedIn;
    }

    @Override
    public Boolean getShowLogin() {
        return showLogin;
    }

    @Override
    public void setShowLogin(Boolean showLogin) {
        this.showLogin = showLogin;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    public EntityManager getEntityManager1() {
        return EMF1.createEntityManager();
    }

    @Override
    public void setUser(JobManagerUser user) {
        this.user = user;
    }

    @Override
    public JobManagerUser getUser() {
        if (user == null) {
            return new JobManagerUser();
        }
        return user;
    }

    public void checkLoginAttemps(RequestContext context) {

        ++loginAttempts;
        if (loginAttempts == 2) {
            context.execute("loginAttemptsDialog.show();");
            try {
                // send email to system administrator
                jm.postJobManagerMail(null, null, "Failed user login", "Username: " + username + "\nDate/Time: " + new Date());
            } catch (Exception ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (loginAttempts > 2) {// tk # attempts to be made option
            context.execute("loginAttemptsDialog.show();");
        }

        username = "";
        password = "";
    }

    public void login() {
       
        EntityManager em = getEntityManager1();
        RequestContext context = RequestContext.getCurrentInstance();

        setUserLoggedIn(false);

        try {
            if ((getUsername() != null) && (getPassword() != null)) {
                if (jm.validateAndAssociateUser(em, getUsername(), getPassword())) {
//                if (true) {  // NB: for testing...remove before deploy.
                    setUser(JobManagerUser.findJobManagerUserByUsername(em, getUsername()));
                    if (getUser() != null) {
                        em.refresh(getUser());
                        setUserLoggedIn(true);
                    } else {
                        checkLoginAttemps(context);
                        logonMessage = "Invalid username or password! Please try again.";
                    }
                } else {
                    checkLoginAttemps(context);
                    logonMessage = "Invalid username or password! Please try again.";
                }
            } else {
                logonMessage = "Invalid username or password! Please try again.";
                username = "";
                password = "";
            }
            // wrap up
            if (getUserLoggedIn()) {
                setShowLogin(false);
                username = "";
                password = "";
                loginAttempts = 0;
                if (context != null) {
                    context.addCallbackParam("userLogggedIn", getUserLoggedIn());
                }

                em.getTransaction().begin();
                BusinessEntityUtils.saveBusinessEntity(em, user);
                em.getTransaction().commit();

                updateAllForms(context);

                // show search layout unit if initially collapsed
                if (searchLayoutUnitCollapsed) {
                    searchLayoutUnitCollapsed = false;
                    context.execute("layoutVar.toggle('west');");
                }

                context.execute("loginDialog.hide();mainTabViewVar.select(0);PrimeFaces.changeTheme('" + getUser().getUserInterfaceThemeName() + "');");

            } else {
                logonMessage = "Invalid username or password! Please try again.";
                username = "";
                password = "";
            }

            em.close();
        } catch (Exception e) {
            System.out.println(e);
            logonMessage = "Login error occured! Please try again or contact the Database Administrator";
            checkLoginAttemps(context);
        }
    }

    private void updateAllForms(RequestContext context) {
//        context.update("preferencesDialogForm");
        context.update("searchForm");
        context.update("headerForm");
        context.update("unitDialogForms");
        context.update("mainTabViewForm");
        context.update("loginForm");
        //context.update("menubarForm");
    }

    public void logout() {
        RequestContext context = RequestContext.getCurrentInstance();

        userLoggedIn = false;
        showLogin = true;
        password = "";
        username = "";
        logonMessage = "Please provide your login details below:";
        user = new JobManagerUser();

        updateAllForms(context);

        // hide search layout unit if initially shown
        if (!searchLayoutUnitCollapsed) {
            searchLayoutUnitCollapsed = true;
            context.execute("layoutVar.toggle('west');");
        }

        // oncomplete="longProcessDialogVar.hide();loginDialog.show();"
        context.execute("loginDialog.show();longProcessDialogVar.hide();PrimeFaces.changeTheme('" + getUser().getUserInterfaceThemeName() + "');");

    }

    public void handleKeepAlive() {
        getUser().setPollTime(new Date());
        System.out.println("Handling keep alive session: doing polling for JMTS..." + getUser().getPollTime());
        saveUser();
    }

    public void saveUser() {
        EntityManager em = getEntityManager1();

        if (getUser().getId() != null) {
            em.getTransaction().begin();
            BusinessEntityUtils.saveBusinessEntity(em, getUser());
            em.getTransaction().commit();
        }

        em.close();
    }

    public void updateDatabaseModule() {
        System.out.println("To be impl. or removed...");
    }

    public void handleLayoutUnitToggle(ToggleEvent event) {

        if (event.getComponent().getId().equals("searchLayoutUnit")) {
            if (event.getVisibility().name().equals("VISIBLE")) {
                searchLayoutUnitCollapsed = false;
            } else {
                searchLayoutUnitCollapsed = true;
            }
        }    
    }

    public Boolean renderUserMenu() {
        if (getUser().getId() == null) {
            return false;
        } else {
            return true;
        }
    }

    public void editPreferences() {
    }

      @Override
    public String getInvalidFormFieldMessage() {
        return invalidFormFieldMessage;
    }

    @Override
    public void setInvalidFormFieldMessage(String invalidFormFieldMessage) {
        this.invalidFormFieldMessage = invalidFormFieldMessage;
    }
    
     

}
