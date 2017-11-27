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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.naming.ldap.InitialLdapContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;
import jm.com.dpbennett.business.entity.JobManagerUser;
import jm.com.dpbennett.business.entity.Preference;
import jm.com.dpbennett.business.entity.SystemOption;
import jm.com.dpbennett.business.entity.management.UserManagement;
import jm.com.dpbennett.business.entity.utils.BusinessEntityUtils;
import static jm.com.dpbennett.jmts.Application.checkForLDAPUser;
import jm.com.dpbennett.jmts.utils.DialogActionHandler;
import jm.com.dpbennett.jmts.utils.Dashboard;
import jm.com.dpbennett.jmts.utils.DashboardTab;
import jm.com.dpbennett.jmts.utils.MainTab;
import jm.com.dpbennett.jmts.utils.MainTabView;
import org.primefaces.context.RequestContext;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.event.TabCloseEvent;
import org.primefaces.event.ToggleEvent;
import jm.com.dpbennett.jmts.Application;

/**
 *
 * @author Desmond Bennett
 */
@Named
@SessionScoped
public class GeneralManager implements Serializable, UserManagement {

    @PersistenceUnit(unitName = "JMTSPU")
    private EntityManagerFactory EMF1;
    @PersistenceUnit(unitName = "AccPacPU")
    private EntityManagerFactory EMF2;
    private Boolean dynamicTabView;
    private Integer longProcessProgress;
    private String outcome;
    private String defaultOutcome;
    private Integer loginAttempts;
    // Other Managers
    // Managers
    private final ClientManager clientManager;
    private final SearchManager searchManager;
    private final ReportManager reportManager;
    private final FinanceManager financeManager;
    private JobManagerUser user;
    private Boolean userLoggedIn;
    private Boolean showLogin;
    private String username;
    private String password;
    private String logonMessage;
    private Boolean westLayoutUnitCollapsed;
    private String invalidFormFieldMessage;
    private String dialogMessage;
    private String dialogMessageHeader;
    private String dialogMessageSeverity;
    private Boolean dialogRenderOkButton;
    private Boolean dialogRenderYesButton;
    private Boolean dialogRenderNoButton;
    private Boolean dialogRenderCancelButton;
    private DialogActionHandler dialogActionHandler;
    private Dashboard dashboard;
    private MainTabView mainTabView;

    /**
     * Creates a new instance of JobManagerBean
     */
    public GeneralManager() {
        password = null;
        username = null;
        showLogin = true;
        userLoggedIn = false;
        westLayoutUnitCollapsed = true;
        loginAttempts = 0;
        longProcessProgress = 0;
        dynamicTabView = true;
        outcome = defaultOutcome;
        // Init Other Managers
        clientManager = Application.findBean("clientManager");
        reportManager = Application.findBean("reportManager");
        searchManager = Application.findBean("searchManager");
        financeManager = Application.findBean("financeManager");
        dashboard = new Dashboard(getUser());
        mainTabView = new MainTabView(getUser());
    }

    public MainTabView getMainTabView() {
        return mainTabView;
    }

    public void setMainTabView(MainTabView mainTabView) {
        this.mainTabView = mainTabView;
    }

    public String getApplicationHeader() {
        return SystemOption.findSystemOptionByName(getEntityManager1(),
                "applicationHeader").getOptionValue();
    }

    public String getApplicationSubheader() {
        String subHeader = SystemOption.findSystemOptionByName(
                getEntityManager1(),
                "applicationSubheader").getOptionValue();

        if (subHeader.trim().equals("None")) {
            return getUser().getEmployee().getDepartment().getName();
        }

        return subHeader;
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

    public void init() {
        System.out.println("Initializing Job Manager...");
        showLogin = true;
        logonMessage = "Please provide your login details below:";
        westLayoutUnitCollapsed = true;
    }

    public Boolean getWestLayoutUnitCollapsed() {
        return westLayoutUnitCollapsed;
    }

    public void setWestLayoutUnitCollapsed(Boolean westLayoutUnitCollapsed) {
        this.westLayoutUnitCollapsed = westLayoutUnitCollapsed;
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
        if (username == null) {
            username = SystemOption.findSystemOptionByName(getEntityManager1(),
                    "defaultUsername").getOptionValue();
        }
        return username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        if (password == null) {
            password = SystemOption.findSystemOptionByName(getEntityManager1(),
                    "defaultPassword").getOptionValue();
        }
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    public final EntityManager getEntityManager1() {
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
     * @param em
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
                Logger.getLogger(GeneralManager.class.getName()).log(Level.SEVERE, null, ex);
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
            // Find user and determin if authentication is required for this user
            JobManagerUser jobManagerUser = JobManagerUser.findJobManagerUserByUsername(em, getUsername());
            if (jobManagerUser != null) {
                em.refresh(jobManagerUser);
                if (!jobManagerUser.getAuthenticate()) {
                    System.out.println("User will NOT be authenticated.");
                    setUser(jobManagerUser);
                    setUserLoggedIn(true);
                } else if (validateAndAssociateUser(em, getUsername(), getPassword())) {
                    System.out.println("User will be authenticated.");
                    setUser(jobManagerUser);
                    setUserLoggedIn(true);
                } else {
                    checkLoginAttemps(context);
                    logonMessage = "Please enter a valid username.";
                }
            } else {
                logonMessage = "Please enter a registered username.";
                username = "";
                password = "";
            }
            // wrap up
            if (getUserLoggedIn()) {
                getUser().setPollTime(new Date());
                getUser().save(em);
                setShowLogin(false);
                username = "";
                password = "";
                loginAttempts = 0;
                if (context != null) {
                    context.addCallbackParam("userLogggedIn", getUserLoggedIn());

                    em.getTransaction().begin();
                    BusinessEntityUtils.saveBusinessEntity(em, user);
                    em.getTransaction().commit();

                    // Show search layout unit if initially collapsed
                    if (westLayoutUnitCollapsed) {
                        westLayoutUnitCollapsed = false;
                        context.execute("layoutVar.toggle('west');");
                    }

                    context.execute("loginDialog.hide();PrimeFaces.changeTheme('"
                            + getUser().getUserInterfaceThemeName() + "');");

                    dashboard.reset(user);
                    mainTabView.reset(user);

                    updateAllForms(context);

                }

            } else {
                logonMessage = "Login error occured! Please try again or contact the System Administrator";
                username = "";
                password = "";
            }

            em.close();
        } catch (Exception e) {
            System.out.println(e);
            logonMessage = "Login error occured! Please try again or contact the System Administrator";
            checkLoginAttemps(context);
        }
    }

    public Dashboard getDashboard() {
        return dashboard;
    }

    public void setDashboard(Dashboard dashboard) {
        this.dashboard = dashboard;
    }

    private void updateAllForms(RequestContext context) {
        context.update("dashboardForm");
        context.update("headerForm");
        dashboard.update("mainTabViewForm:mainTabView");
        dashboard.update("dashboardForm:dashboardAccordion");
        dashboard.select(0);
        context.update("loginForm");
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

        // Hide search layout unit if initially shown
        if (!westLayoutUnitCollapsed) {
            westLayoutUnitCollapsed = true;
            context.execute("layoutVar.toggle('west');");
        }

        // Unrender all tabs
        dashboard.removeAllTabs();
        mainTabView.removeAllTabs();

        context.execute("loginDialog.show();longProcessDialogVar.hide();PrimeFaces.changeTheme('" + getUser().getUserInterfaceThemeName() + "');");

    }

    public void handleKeepAlive() {
        getUser().setPollTime(new Date());
        // NB: Time is based on the time zone set in the application server
        System.out.println("Handling keep alive session: doing polling for JMTS..." + getUser().getPollTime());
        if (getUser().getId() != null) {
            getUser().save(getEntityManager1());
        }
    }

    public void handleLayoutUnitToggle(ToggleEvent event) {

        if (event.getComponent().getId().equals("dashboard")) {
            if (event.getVisibility().name().equals("VISIBLE")) {
                westLayoutUnitCollapsed = false;
            } else {
                westLayoutUnitCollapsed = true;
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

    public void addMessage(String summary, String detail, Severity severity) {
        FacesMessage message = new FacesMessage(severity, summary, detail);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void onMainViewTabClose(TabCloseEvent event) {

        String tabId = ((MainTab) event.getData()).getId();
        mainTabView.renderTab(getEntityManager1(), tabId, false);

        // Select the jobs tab if the job detail tab was closed
        if (tabId.equals("jobDetailTab")) {
            MainTab tab = mainTabView.findTab("jobsTab");
            if (tab != null) {
                mainTabView.select("mainTabViewVar", mainTabView.getTabIndex());
            }
        }
    }

    public void onDashboardTabChange(TabChangeEvent event) {

        String tabId = ((DashboardTab) event.getData()).getId();
        mainTabView.renderTab(getEntityManager1(), tabId, true);

    }

    public void updateDashboard(String tabId) {
        RequestContext context = RequestContext.getCurrentInstance();

        SearchManager sm = Application.findBean("searchManager");
        switch (tabId) {
            case "adminTab":
                break;
            case "financialAdminTab":
                break;
            case "jobsTab":
                break;
            case "jobDetailTab":
                break;
            case "clientsTab":
                break;
            default:
                break;
        }

        context.update("dashboardForm");

    }

    public void editPreferences() {
    }

    // tk delete if not needed.
    public String getJmt() {
        // outcome to use when login
        defaultOutcome = "jmtlogin";

        return "jmt";
    }

    public String getDefaultOutcome() {
        return defaultOutcome;
    }

    public void setDefaultOutcome(String defaultOutcome) {
        this.defaultOutcome = defaultOutcome;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public Integer getLongProcessProgress() {
        if (longProcessProgress == null) {
            longProcessProgress = 0;
        } else {
            if (longProcessProgress < 10) {
                // this is to ensure that this method does not make the progress
                // complete as this is done elsewhere.
                longProcessProgress = longProcessProgress + 1;
            }
        }

        return longProcessProgress;
    }

    public void onLongProcessComplete() {
        longProcessProgress = 0;
    }

    public void setLongProcessProgress(Integer longProcessProgress) {
        this.longProcessProgress = longProcessProgress;
    }

    public Boolean getDynamicTabView() {
        return dynamicTabView;
    }

    public void setDynamicTabView(Boolean dynamicTabView) {
        this.dynamicTabView = dynamicTabView;
    }

    public void savePreferences() {
        // user object holds preferences for now so we save it
        getUser().save(getEntityManager1());
    }

    public List<String> completePreferenceValue(String query) {
        EntityManager em = null;

        try {
            em = getEntityManager1();

            List<String> preferenceValues = Preference.findAllPreferenceValues(em, query);

            return preferenceValues;

        } catch (Exception e) {
            System.out.println(e);

            return new ArrayList<>();
        }
    }

    private EntityManagerFactory getEMF1() {
        return EMF1;
    }

    private EntityManagerFactory getEMF2() {
        return EMF2;
    }

    public void handlePoll() {
        System.out.println("Handling poll: " + new Date());
    }

    public EntityManagerFactory setupDatabaseConnection(String PU) {
        try {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(PU);
            if (emf.isOpen()) {
                return emf;
            } else {
                return null;
            }
        } catch (Exception ex) {
            System.out.println(PU + " Connection failed: " + ex);
            return null;
        }
    }

    public HashMap<String, String> getConnectionProperties(
            String url,
            String driver,
            String username,
            String password) {

        // setup new database connection properties
        HashMap<String, String> prop = new HashMap<>();
        prop.put("javax.persistence.jdbc.user", username);
        prop.put("javax.persistence.jdbc.password", password);
        prop.put("javax.persistence.jdbc.url", url);
        prop.put("javax.persistence.jdbc.driver", driver);

        return prop;
    }

    public Date getCurrentDate() {
        return new Date();
    }
   

}
