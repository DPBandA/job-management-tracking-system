/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.com.dpbennett.jmts;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.naming.ldap.InitialLdapContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import jm.com.dpbennett.business.entity.JobManagerUser;
import jm.com.dpbennett.business.entity.management.MessageManagement;
import jm.com.dpbennett.business.entity.management.UserManagement;
import jm.com.dpbennett.business.entity.utils.BusinessEntityUtils;
import static jm.com.dpbennett.jmts.Application.checkForLDAPUser;
import jm.com.dpbennett.jmts.utils.DialogActionHandler;
import org.primefaces.component.tabview.Tab;
import org.primefaces.context.RequestContext;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.event.ToggleEvent;

/**
 *
 * @author dbennett
 */
@Named
@SessionScoped
public class Main implements UserManagement, MessageManagement, Serializable {

    @PersistenceUnit(unitName = "JMTSPU")
    private EntityManagerFactory EMF1;
    //private JobManagement jm;
    private JobManagerUser user;
    private Boolean userLoggedIn = false;
    private Boolean showLogin = true;
    private String username = "";
    private String password = "";
    private String logonMessage;
    private Integer loginAttempts = 0;
    private String tabTitle = "Jobs";
    private Boolean searchLayoutUnitCollapsed;
    private String invalidFormFieldMessage;
    private String dialogMessage;
    private String dialogMessageHeader;
    private String dialogMessageSeverity;
    private Boolean dialogRenderOkButton;
    private Boolean dialogRenderYesButton;
    private Boolean dialogRenderNoButton;
    private Boolean dialogRenderCancelButton;
    private DialogActionHandler dialogActionHandler;

    /**
     * Creates a new instance of Main
     */
    public Main() {
        showLogin = true;
        logonMessage = "Please provide your login details below:";
        searchLayoutUnitCollapsed = true;
    }

    /**
     * Creates a new instance of Main
     * @param persistenceUnitName
     */
    public Main(String persistenceUnitName) {
        showLogin = true;
        searchLayoutUnitCollapsed = true;
        logonMessage = "Please provide your login details below:";

        EMF1 = BusinessEntityUtils.getEntityManagerFactory(persistenceUnitName);
    }

    public void openDialog(Object entity,
            String outcome,
            Boolean modal,
            Boolean draggable,
            Boolean resizable,
            Integer contentHeight,
            Integer contentWidth) {

        Map<String, Object> options = new HashMap<>();
        options.put("modal", modal);
        options.put("draggable", draggable);
        options.put("resizable", resizable);
        options.put("contentHeight", contentHeight);
        options.put("contentWidth", contentWidth);

        RequestContext.getCurrentInstance().openDialog(outcome, options, null);
    }

    public Boolean getDialogRenderCancelButton() {
        return dialogRenderCancelButton;
    }

    public void setDialogRenderCancelButton(Boolean dialogRenderCancelButton) {
        this.dialogRenderCancelButton = dialogRenderCancelButton;
    }

    public void setDialogActionHandler(DialogActionHandler dialogActionHandler) {
        this.dialogActionHandler = dialogActionHandler;
    }

    public Boolean getDialogRenderOkButton() {
        return dialogRenderOkButton;
    }

    public void setDialogRenderOkButton(Boolean dialogRenderOkButton) {
        this.dialogRenderOkButton = dialogRenderOkButton;
    }

    public Boolean getDialogRenderYesButton() {
        return dialogRenderYesButton;
    }

    public void setDialogRenderYesButton(Boolean dialogRenderYesButton) {
        this.dialogRenderYesButton = dialogRenderYesButton;
    }

    public Boolean getDialogRenderNoButton() {
        return dialogRenderNoButton;
    }

    public void setDialogRenderNoButton(Boolean dialogRenderNoButton) {
        this.dialogRenderNoButton = dialogRenderNoButton;
    }

    public Integer getLoginAttempts() {
        return loginAttempts;
    }

    public void setLoginAttempts(Integer loginAttempts) {
        this.loginAttempts = loginAttempts;
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

    public void onMainTabChange(TabChangeEvent event) {
        RequestContext context = RequestContext.getCurrentInstance();

        Tab activeTab = event.getTab();

        if (activeTab != null) {
            setTabTitle(activeTab.getTitle());

            SearchManager sm = Application.findBean("searchManager");
            switch (getTabTitle()) {
                case "Service Requests":
                    sm.setCurrentSearchParameterKey("Service Request Search");
                    break;                
                case "System Administration":
                    sm.setCurrentSearchParameterKey("Admin Search");
                    break;
                default:
                    sm.setCurrentSearchParameterKey("Job Search");
                    break;
            }
        }

        context.update("searchForm");
    }

    public void init() {
        System.out.println("Initializing...");
        // init job manager 
        JobManager jm2 = Application.findBean("jobManager");
        if (jm2 != null) {
            jm2.setDirty(false);
            //   jm2.setIsJobToBeCopied(false);
        }
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

    /**
     * Get user as currently stored in the database
     *
     * @return
     */
    public JobManagerUser getUser(EntityManager em) {
        if (user == null) {
            return new JobManagerUser();
        } else {
            try {
                if (user.getId() != null) {
                    JobManagerUser foundUser = em.find(JobManagerUser.class, user.getId());
                    if (foundUser != null) {
                        em.refresh(foundUser);
                        user = foundUser;
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
                return new JobManagerUser();
            }
        }

        return user;
    }

    public void checkLoginAttemps(RequestContext context) {

        ++loginAttempts;
        if (loginAttempts == 2) {
            context.execute("loginAttemptsDialog.show();");
            try {
                // send email to system administrator
                BusinessEntityUtils.postMail(null, null, "Failed user login", "Username: " + username + "\nDate/Time: " + new Date());
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
                // Find user and determin if authentication is required for this user
                JobManagerUser jobManagerUser = JobManagerUser.findJobManagerUserByUsername(em, getUsername());
                if (jobManagerUser != null) {
                    em.refresh(jobManagerUser);
                    if (!jobManagerUser.getAuthenticate()) {  // NB: for testing...remove before deploy.
                        System.out.println("User will NOT be authen.");
                        setUser(jobManagerUser);
                        //if (getUser() != null) {
//                        em.refresh(getUser());
                        setUserLoggedIn(true);
                        //} else {
                        //    checkLoginAttemps(context);
                        //    logonMessage = "Invalid username or password! Please try again.";
//                    } else if (Application.validateAndAssociateUser(em, getUsername(), getPassword())) {
                    } else if (validateAndAssociateUser(em, getUsername(), getPassword())) {
                        System.out.println("User will be authen.");
                        setUser(jobManagerUser);
                        //if (getUser() != null) {
//                        em.refresh(getUser());
                        setUserLoggedIn(true);
                        //} else {
                        //    checkLoginAttemps(context);
                        //    logonMessage = "Invalid username or password! Please try again.";
                        //}
                    } else {
                        checkLoginAttemps(context);
                        logonMessage = "Invalid username or password! Please try again.";
                    }
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

                    em.getTransaction().begin();
                    BusinessEntityUtils.saveBusinessEntity(em, user);
                    em.getTransaction().commit();

                    updateAllForms(context);

                    // show search layout unit if initially collapsed
//                    if (getUser().getJobTableViewPreference().equals("Jobs")) {
                    if (searchLayoutUnitCollapsed) {
                        searchLayoutUnitCollapsed = false;
                        context.execute("layoutVar.toggle('west');");
                    }


                    context.execute("loginDialog.hide();PrimeFaces.changeTheme('"
                            + getUser().getUserInterfaceThemeName() + "');mainTabViewVar.select(0);");

                }


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
        //getUser().setPollTime(new Timestamp(new Date().getTime()));
        getUser().setPollTime(new Date()); // tk
        // NB: Time is based on the time zone set in the application server
        System.out.println("Handling keep alive session: doing polling for JMTS..." + getUser().getPollTime());
        saveUser();
    }

    public void saveUser() {
        EntityManager em = getEntityManager1();

        if (getUser().getId() != null) {
            JobManagerUser.save(em, getUser());
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

    @Override
    public String getInvalidFormFieldMessage() {
        return invalidFormFieldMessage;
    }

    @Override
    public void setInvalidFormFieldMessage(String invalidFormFieldMessage) {
        this.invalidFormFieldMessage = invalidFormFieldMessage;
    }

    public void displayCommonMessageDialog(DialogActionHandler dialogActionHandler, String dialogMessage,
            String dialogMessageHeader,
            String dialoMessageSeverity) {
        RequestContext context = RequestContext.getCurrentInstance();

        setDialogActionHandler(dialogActionHandler);

        setDialogRenderOkButton(true);
        setDialogRenderYesButton(false);
        setDialogRenderNoButton(false);

        setDialogMessage(dialogMessage);
        setDialogMessageHeader(dialogMessageHeader);
        setDialogMessageSeverity(dialoMessageSeverity);

        context.update("commonMessageDialogForm");
        context.execute("commonMessageDialog.show();");
    }

    public void displayCommonConfirmationDialog(DialogActionHandler dialogActionHandler,
            String dialogMessage,
            String dialogMessageHeader,
            String dialoMessageSeverity) {
        RequestContext context = RequestContext.getCurrentInstance();

        setDialogActionHandler(dialogActionHandler);

        setDialogRenderOkButton(false);
        setDialogRenderYesButton(true);
        setDialogRenderNoButton(true);
        setDialogRenderCancelButton(true);

        setDialogMessage(dialogMessage);
        setDialogMessageHeader(dialogMessageHeader);
        setDialogMessageSeverity(dialoMessageSeverity);

        context.update("commonMessageDialogForm");
        context.execute("commonMessageDialog.show();");
    }

    public void handleDialogOkButtonPressed() {
        if (dialogActionHandler != null) {
            dialogActionHandler.handleDialogOkButtonClick();
        }
    }

    public void handleDialogYesButtonPressed() {
        if (dialogActionHandler != null) {
            dialogActionHandler.handleDialogYesButtonClick();
        }
    }

    public void handleDialogNoButtonPressed() {
        if (dialogActionHandler != null) {
            dialogActionHandler.handleDialogNoButtonClick();
        }
    }

    public void handleDialogCancelButtonPressed() {
        if (dialogActionHandler != null) {
            dialogActionHandler.handleDialogCancelButtonClick();
        }
    }

    /**
     * Validate a user and associate the user with an employee if possible.
     *
     * @param em
     * @param username
     * @param password
     * @return
     */
    @Override
     public Boolean validateAndAssociateUser(EntityManager em, String username, String password) {
        Boolean userValidated = false;
        InitialLdapContext ctx;
        //Employee employee;

        // tk for testing purposes
        if (username.equals("admin") && password.equals("password")) { // password to be encrypted
            return true;
        }

        try {
            List<jm.com.dpbennett.business.entity.LdapContext> ctxs = jm.com.dpbennett.business.entity.LdapContext.findAllLdapContexts(em);

            for (jm.com.dpbennett.business.entity.LdapContext ldapContext : ctxs) {
                ctx = ldapContext.getInitialLDAPContext(username, password);

                if (checkForLDAPUser(em, username, ctx)) {
                    // user exists in LDAP                    
                    userValidated = true;
                    break;
                }
            }

            // get the user if one exists
            // JobManagerUser jmtsUser = JobManagerUser.findJobManagerUserByUsername(em, username);

            if (userValidated) {
               
                System.out.println("User validated.");

                return true;
            
            } else {
                System.out.println("User NOT validated!");
                return false;
            }

        } catch (Exception e) {
            System.err.println("Problem connecting to directory: " + e);
        }


        return false;
    }
//    public Boolean validateAndAssociateUser(EntityManager em, String username, String password) {
//        Boolean userValidated = false;
//        InitialLdapContext ctx = null;
//        Employee employee;
//
//        // tk for testing purposes
//        if (username.equals("admin") && password.equals("password")) { // password to be encrypted
//            return true;
//        }
//
//        try {
//            List<jm.com.dpbennett.entity.LdapContext> ctxs = jm.com.dpbennett.entity.LdapContext.findAllLdapContexts(em);
//
//            for (jm.com.dpbennett.entity.LdapContext ldapContext : ctxs) {
//                ctx = ldapContext.getInitialLDAPContext(username, password);
//
//                if (checkForLDAPUser(em, username, ctx)) {
//                    // user exists in LDAP                    
//                    userValidated = true;
//                    break;
//                }
//            }
//
//            // get employee that corresponds to this username
//            if (userValidated) {
//                employee = Employee.findEmployeeByUsername(em, username, ctx);
//            } else {
//                return false;
//            }
//
//            // get the user if one exists
//            JobManagerUser jmtsUser = JobManagerUser.findJobManagerUserByUsername(em, username);
//
//            if ((jmtsUser == null) && (employee != null)) {
//                // create and associate the user with the employee
//                jmtsUser = createNewUser(em);
//                jmtsUser.setUsername(username);
//                jmtsUser.setEmployee(employee);
//                jmtsUser.setUserFirstname(employee.getFirstName());
//                jmtsUser.setUserLastname(employee.getLastName());
//                em.getTransaction().begin();
//                BusinessEntityUtils.saveBusinessEntity(em, jmtsUser);
//                em.getTransaction().commit();
//
//                System.out.println("User validated and associated with employee.");
//
//                return true;
//            } else if ((jmtsUser != null) && (employee != null)) {
//                System.out.println("User validated.");
//                return true;
//            } else if ((jmtsUser != null) && (employee == null)) {
//                if (jmtsUser.getEmployee() != null) {
//                    System.out.println("User validated.");
//                    return true;
//                } else {
//                    System.out.println("User NOT validated!");
//                    return false;
//                }
//
//            } else {
//                System.out.println("User NOT validated!");
//                return false;
//            }
//
//        } catch (Exception e) {
//            System.err.println("Problem connecting to directory: " + e);
//        }
//
//
//        return false;
//    }
}
