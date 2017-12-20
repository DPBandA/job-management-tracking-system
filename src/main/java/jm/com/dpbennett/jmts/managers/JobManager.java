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

import jm.com.dpbennett.business.entity.utils.JobDataModel;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.ldap.InitialLdapContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;
import jm.com.dpbennett.business.entity.AccPacCustomer;
import jm.com.dpbennett.business.entity.Address;
import jm.com.dpbennett.business.entity.Alert;
import jm.com.dpbennett.business.entity.BusinessOffice;
import jm.com.dpbennett.business.entity.Classification;
import jm.com.dpbennett.business.entity.Client;
import jm.com.dpbennett.business.entity.Contact;
import jm.com.dpbennett.business.entity.DatePeriod;
import jm.com.dpbennett.business.entity.Department;
import jm.com.dpbennett.business.entity.Employee;
import jm.com.dpbennett.business.entity.Job;
import jm.com.dpbennett.business.entity.JobCategory;
import jm.com.dpbennett.business.entity.JobCostingAndPayment;
import jm.com.dpbennett.business.entity.JobManagerUser;
import jm.com.dpbennett.business.entity.JobSubCategory;
import jm.com.dpbennett.business.entity.Preference;
import jm.com.dpbennett.business.entity.Sector;
import jm.com.dpbennett.business.entity.ServiceContract;
import jm.com.dpbennett.business.entity.ServiceRequest;
import jm.com.dpbennett.business.entity.SystemOption;
import jm.com.dpbennett.business.entity.management.MessageManagement;
import jm.com.dpbennett.business.entity.management.UserManagement;
import jm.com.dpbennett.business.entity.utils.BusinessEntityUtils;
import jm.com.dpbennett.business.entity.utils.SearchParameters;
import static jm.com.dpbennett.jmts.Application.checkForLDAPUser;
import jm.com.dpbennett.jmts.utils.DialogActionHandler;
import jm.com.dpbennett.jmts.utils.Dashboard;
import jm.com.dpbennett.jmts.utils.DashboardTab;
import jm.com.dpbennett.jmts.utils.MainTab;
import jm.com.dpbennett.jmts.utils.MainTabView;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.event.TabCloseEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.StreamedContent;
import jm.com.dpbennett.business.entity.management.BusinessEntityManagement;
import jm.com.dpbennett.business.entity.utils.PrimeFacesUtils;
import jm.com.dpbennett.jmts.Application;
import org.primefaces.component.selectoneradio.SelectOneRadio;

/**
 *
 * @author Desmond Bennett
 */
@Named
@SessionScoped
public class JobManager implements Serializable, BusinessEntityManagement,
        DialogActionHandler, UserManagement, MessageManagement {

    @PersistenceUnit(unitName = "JMTSPU")
    private EntityManagerFactory EMF1;
    @PersistenceUnit(unitName = "AccPacPU")
    private EntityManagerFactory EMF2;
    private Job currentJob;
    private Job selectedJob;
    private Boolean dynamicTabView;
    private Boolean renderSearchComponent;
    private Boolean renderJobDetailTab;
    @ManagedProperty(value = "Jobs")
    private Integer longProcessProgress;
    private final Boolean useAccPacCustomerList;
    private Boolean showJobEntry;
    private List<Job> jobSearchResultList;
    private String userPrivilegeDialogHeader;
    private String userPrivilegeDialogMessage;
    private Integer loginAttempts;
    private SearchParameters currentSearchParameters;
    // Managers
    private final ClientManager clientManager;
    private final SearchManager searchManager;
    private final ReportManager reportManager;
    private final FinanceManager financeManager;
    private final JobSampleManager jobSampleManager;
    private final ContractManager contractManager;
    //////////////////////////////////////////////////
    private SearchParameters reportSearchParameters;
    private String searchText;
    private String dialogActionHandlerId;
    private String jobsTabTitle;
    private Job[] selectedJobs;
    private JobManagerUser user;
    private Boolean userLoggedIn;
    private Boolean showLogin;
    private String username;
    private String password;
    private String logonMessage;
    private Boolean westLayoutUnitCollapsed;
    private String invalidFormFieldMessage;
    // tk rid of dialog* and handler and use growl?
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
    public JobManager() {
        password = null;
        username = null;
        showLogin = true;
        userLoggedIn = false;
        westLayoutUnitCollapsed = true;
        loginAttempts = 0;
        showJobEntry = false;
        longProcessProgress = 0;
        useAccPacCustomerList = false;
        ArrayList searchTypes = new ArrayList();
        ArrayList searchDateFields = new ArrayList();
        searchTypes.add(new SelectItem("General", "General"));
        // Add search fields
        searchDateFields.add(new SelectItem("dateSubmitted", "Date submitted"));
        searchDateFields.add(new SelectItem("dateOfCompletion", "Date completed"));
        searchDateFields.add(new SelectItem("dateAndTimeEntered", "Date entered"));
        reportSearchParameters
                = new SearchParameters(
                        "Report Data Search",
                        null,
                        false,
                        searchTypes,
                        true,
                        "dateSubmitted",
                        true,
                        searchDateFields,
                        "General",
                        new DatePeriod("This month", "month", null, null, false, false, true),
                        "");
        dynamicTabView = true;
        renderSearchComponent = true;
        // Init Managers
        clientManager = Application.findBean("clientManager");
        reportManager = Application.findBean("reportManager");
        searchManager = Application.findBean("searchManager");
        financeManager = Application.findBean("financeManager");
        jobSampleManager = Application.findBean("jobSampleManager");
        contractManager = Application.findBean("contractManager");
        dashboard = new Dashboard(getUser());
        mainTabView = new MainTabView(getUser());
    }

//    public FinanceManager getFinanceManager() {
//        return financeManager;
//    }
    // tk
    public Boolean getCanApplyGCT() {
        return JobCostingAndPayment.getCanApplyGCT(getCurrentJob());
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
                Logger.getLogger(JobManager.class.getName()).log(Level.SEVERE, null, ex);
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
                }

            } else {
                logonMessage = "Login error occured! Please try again or contact the System Administrator";
                username = "";
                password = "";
            }

            em.close();

            updateAllForms(context);
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
        context.update("mainTabViewForm");
        context.update("headerForm");
        context.update("loginForm");
        dashboard.select(0);

    }

    public void logout() {
        RequestContext context = RequestContext.getCurrentInstance();

        userLoggedIn = false;
        showLogin = true;
        password = "";
        username = "";
        logonMessage = "Please provide your login details below:";
        user = new JobManagerUser();

        // Hide search layout unit if initially shown
        if (!westLayoutUnitCollapsed) {
            westLayoutUnitCollapsed = true;
            context.execute("layoutVar.toggle('west');");
        }

        // Unrender all tabs
        dashboard.removeAllTabs();
        dashboard.setRender(false);
        mainTabView.removeAllTabs();
        mainTabView.setRender(false);

        updateAllForms(context);

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
            westLayoutUnitCollapsed = !event.getVisibility().name().equals("VISIBLE");
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

    public void prepareToCloseJobDetailTab() {

        RequestContext requestContext = RequestContext.getCurrentInstance();

        requestContext.addCallbackParam("isDirty", isDirty());
    }

    public void onMainViewTabClose(TabCloseEvent event) {

        // Set render state of tab being closed
        String tabId = ((MainTab) event.getData()).getId();
        mainTabView.renderTab(getEntityManager1(), tabId, false);

        // Close other tabs and take other actions as necessary
        switch (tabId) {
            case "jobDetailTab":
                financeManager.setEnableOnlyPaymentEditing(false);
                if (isDirty()) {
                    PrimeFacesUtils.addMessage("Job Not Saved!", "The current job was edited but not saved", FacesMessage.SEVERITY_WARN);
                }
                break;
            case "jobsTab":
                // Close job detail tab
                mainTabView.renderTab(getEntityManager1(), "jobDetailTab", false);
                financeManager.setEnableOnlyPaymentEditing(false);
                if (isDirty()) {
                    PrimeFacesUtils.addMessage("Job Not Saved!", "The current job was edited but not saved", FacesMessage.SEVERITY_WARN);
                }
                break;
            default:
                if (!tabId.equals("jobDetailTab") && mainTabView.isTabRendered("jobDetailTab")) {
                    financeManager.setEnableOnlyPaymentEditing(false);
                    mainTabView.renderTab(getEntityManager1(), "jobDetailTab", false);
                    if (isDirty()) {
                        PrimeFacesUtils.addMessage("Job Not Saved!", "The recently opened job was edited but not saved", FacesMessage.SEVERITY_WARN);
                        RequestContext.getCurrentInstance().update("headerForm:growl3");
                    }
                }
                break;
        }

    }

    public void onMainViewTabChange(TabChangeEvent event) {
        String tabId = ((MainTab) event.getData()).getId();

        switch (tabId) {
            default:
                if (!tabId.equals("jobDetailTab") && mainTabView.isTabRendered("jobDetailTab")) {
                    financeManager.setEnableOnlyPaymentEditing(false);
                    mainTabView.renderTab(getEntityManager1(), "jobDetailTab", false);
                    if (isDirty()) {
                        PrimeFacesUtils.addMessage("Job Not Saved!", "The recently opened job was edited but not saved", FacesMessage.SEVERITY_WARN);
                        RequestContext.getCurrentInstance().update("headerForm:growl3");
                    }
                }
                break;
        }

    }

    public void closeJobDetailTab() {
        mainTabView.renderTab(getEntityManager1(), "jobDetailTab", false);
    }

    public void closeReportsTab() {
        mainTabView.renderTab(getEntityManager1(), "reportsTab", false);
    }

    public void onDashboardTabChange(TabChangeEvent event) {

        String tabId = ((DashboardTab) event.getData()).getId();
        // Open the corresponding main view tab if necessary
        //mainTabView.renderTab(getEntityManager1(), tabId, true);

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

    public Boolean getRenderJobDetailTab() {
        return renderJobDetailTab;
    }

    public void setRenderJobDetailTab(Boolean renderJobDetailTab) {
        this.renderJobDetailTab = renderJobDetailTab;
    }

    /**
     * Get selected job which is usually displayed in a table.
     *
     * @return
     */
    public Job[] getSelectedJobs() {
        return selectedJobs;
    }

    public void setSelectedJobs(Job[] selectedJobs) {
        this.selectedJobs = selectedJobs;
    }

    public void editPreferences() {
    }

    public void openJobsTab() {
        mainTabView.renderTab(getEntityManager1(), "jobsTab", true);
        mainTabView.renderTab(getEntityManager1(), "cashierTab", false);
        mainTabView.renderTab(getEntityManager1(), "jobCostingsTab", false);
        mainTabView.select("jobsTab");
    }

    public void openCashierTab() {
        mainTabView.renderTab(getEntityManager1(), "cashierTab", true);
        mainTabView.renderTab(getEntityManager1(), "jobsTab", false);
        mainTabView.renderTab(getEntityManager1(), "jobCostingsTab", false);
        mainTabView.select("cashierTab");
    }

    public void openJobCostingsTab() {
        mainTabView.renderTab(getEntityManager1(), "jobCostingsTab", true);
        mainTabView.renderTab(getEntityManager1(), "cashierTab", false);
        mainTabView.renderTab(getEntityManager1(), "jobsTab", false);
        mainTabView.select("jobCostingsTab");
    }

    public void openSystemAdministrationTab() {
        mainTabView.renderTab(getEntityManager1(), "adminTab", true);
        mainTabView.select("adminTab");
    }

    public void openFinancialAdministrationTab() {
        mainTabView.renderTab(getEntityManager1(), "financialAdminTab", true);
        mainTabView.select("financialAdminTab");
    }

    public String getJobsTabTitle() {
        return jobsTabTitle;
    }

    public void setJobsTabTitle(String jobsTabTitle) {
        this.jobsTabTitle = jobsTabTitle;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public SearchParameters getReportSearchParameters() {
        return reportSearchParameters;
    }

    public void setReportSearchParameters(SearchParameters reportSearchParameters) {
        this.reportSearchParameters = reportSearchParameters;
    }

    public void displayUserPrivilegeDialog(RequestContext context,
            String header,
            String message) {

        setUserPrivilegeDialogHeader(header);
        setUserPrivilegeDialogMessage(message);
        context.update("userPrivilegeDialogForm");
        context.execute("userPrivilegeDialog.show();");
    }

    public String getUserPrivilegeDialogHeader() {
        return userPrivilegeDialogHeader;
    }

    public void setUserPrivilegeDialogHeader(String userPrivilegeDialogHeader) {
        this.userPrivilegeDialogHeader = userPrivilegeDialogHeader;
    }

    public String getUserPrivilegeDialogMessage() {
        return userPrivilegeDialogMessage;
    }

    public void setUserPrivilegeDialogMessage(String userPrivilegeDialogMessage) {
        this.userPrivilegeDialogMessage = userPrivilegeDialogMessage;
    }

    public Boolean getShowJobEntry() {
        return showJobEntry;
    }

    public void setShowJobEntry(Boolean showJobEntry) {
        this.showJobEntry = showJobEntry;
    }

    private Boolean isCurrentJobJobAssignedToUser() {
        if (getUser() != null) {
            return currentJob.getAssignedTo().getId().longValue() == getUser().getEmployee().getId().longValue();
        } else {
            return false;
        }
    }

    private Boolean isJobAssignedToUserDepartment() {

        if (getUser() != null) {
            if (currentJob.getDepartment().getId().longValue() == getUser().getEmployee().getDepartment().getId().longValue()) {
                return true;
            } else if (currentJob.getSubContractedDepartment().getId().longValue() == getUser().getEmployee().getDepartment().getId().longValue()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public Boolean getCanEnterJob() {
        if (getUser() != null) {
            return getUser().getPrivilege().getCanEnterJob();
        } else {
            return false;
        }
    }

    /**
     * Can edit job only if the job is assigned to your department or if you
     * have job entry privilege
     *
     * @return
     */
    public Boolean getCanEditDepartmentalJob() {
        if (getCanEnterJob()) {
            return true;
        }

        if (getUser() != null) {
            return getUser().getPrivilege().getCanEditDepartmentJob() && isJobAssignedToUserDepartment();
        } else {
            return false;
        }
    }

    public Boolean getCanEditOwnJob() {
        if (getCanEnterJob()) {
            return true;
        }

        if (getUser() != null) {
            return getUser().getPrivilege().getCanEditOwnJob();
        } else {
            return false;
        }
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

    /**
     * For future implementation if necessary
     *
     * @param query
     * @return
     */
    public List<String> completeSearchText(String query) {
        List<String> suggestions = new ArrayList<>();

        return suggestions;
    }

    public void createNewJob() {
        RequestContext context = RequestContext.getCurrentInstance();
        EntityManager em = getEntityManager1();

        if (checkJobEntryPrivilege(em, context)) {
            createJob(em, false);
            initManagers();
            PrimeFacesUtils.addMessage("New Job Created", "Please enter the required fields for this job", FacesMessage.SEVERITY_INFO);
            mainTabView.renderTab(getEntityManager1(), "jobDetailTab", true);
            context.update("mainTabViewForm:mainTabView:jobFormTabView,headerForm:growl3");
            context.execute("jobFormTabVar.select(0);");
        }

    }

    public StreamedContent getServiceContractFile() {
        StreamedContent serviceContractStreamContent = null;

        try {

            serviceContractStreamContent = contractManager.getServiceContractStreamContent();

            setLongProcessProgress(100);
        } catch (Exception e) {
            System.out.println(e);
            setLongProcessProgress(100);
        }

        return serviceContractStreamContent;
    }

    public String getJobSearchResultsPanelVisibility() {
        if (renderSearchComponent) {
            return "visible";
        } else {
            return "hidden";
        }
    }

    public Boolean getRenderSearchComponent() {
        return renderSearchComponent;
    }

    public void setRenderSearchComponent(Boolean renderSearchComponent) {
        this.renderSearchComponent = renderSearchComponent;
    }

    public void updatePreferedJobTableView(SelectEvent event) {
        doJobViewUpdate((String) event.getObject());
        setDirty(true);
    }

    public Boolean getCurrentJobIsValid() {
        if (getCurrentJob().getId() != null && !getCurrentJob().getIsDirty()) {
            return true;
        }

        return false;
    }

    public List<Preference> getJobTableViewPreferences() {
        EntityManager em = getEntityManager1();

        List<Preference> prefs = Preference.findAllPreferencesByName(em, "jobTableView");

        return prefs;
    }

    public Boolean getDynamicTabView() {
        return dynamicTabView;
    }

    public void setDynamicTabView(Boolean dynamicTabView) {
        this.dynamicTabView = dynamicTabView;
    }

    /**
     * Conditionally disable department entry. Currently not used.
     *
     * @return
     */
    public Boolean getDisableDepartmentEntry() {

        // allow department entry only if business office is null
        if (currentJob != null) {
            if (currentJob.getBusinessOffice() != null) {
                if (currentJob.getBusinessOffice().getCode().trim().equals("")) {
                    return false;
                } else {
                    return true;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public void setJobCompletionDate(Date date) {
        currentJob.getJobStatusAndTracking().setDateOfCompletion(date);
    }

    public Date getJobCompletionDate() {
        if (currentJob != null) {
            return currentJob.getJobStatusAndTracking().getDateOfCompletion();
        } else {
            return null;
        }
    }

    // NB: This and other code that get date is no longer necessary. Clean up!
    public Date getExpectedDateOfCompletion() {
        if (currentJob != null) {
            return currentJob.getJobStatusAndTracking().getExpectedDateOfCompletion();
        } else {
            return null;
        }
    }

    public void setExpectedDateOfCompletion(Date date) {
        currentJob.getJobStatusAndTracking().setExpectedDateOfCompletion(date);
    }

    public Date getDateSamplesCollected() {
        if (currentJob != null) {
            if (currentJob.getJobStatusAndTracking().getDateSamplesCollected() != null) {
                return currentJob.getJobStatusAndTracking().getDateSamplesCollected();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public void setDateSamplesCollected(Date date) {
        currentJob.getJobStatusAndTracking().setDateSamplesCollected(date);
    }

    public Date getDateDocumentCollected() {
        if (currentJob != null) {
            if (currentJob.getJobStatusAndTracking().getDateDocumentCollected() != null) {
                return currentJob.getJobStatusAndTracking().getDateDocumentCollected();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public void setDateDocumentCollected(Date date) {
        currentJob.getJobStatusAndTracking().setDateDocumentCollected(date);
    }

    /**
     *
     * @return
     */
    public Boolean getCompleted() {
        if (currentJob != null) {
            return currentJob.getJobStatusAndTracking().getCompleted();
        } else {
            return false;
        }
    }

    public void setCompleted(Boolean b) {
        currentJob.getJobStatusAndTracking().setCompleted(b);
    }

    public Boolean getJobSaved() {
        return getCurrentJob().getId() != null;
    }

    public Boolean getSamplesCollected() {
        if (currentJob != null) {
            return currentJob.getJobStatusAndTracking().getSamplesCollected();
        } else {
            return false;
        }
    }

    public void setSamplesCollected(Boolean b) {
        currentJob.getJobStatusAndTracking().setSamplesCollected(b);
    }

    // documents collected by
    public Boolean getDocumentCollected() {
        if (currentJob != null) {
            return currentJob.getJobStatusAndTracking().getDocumentCollected();
        } else {
            return false;
        }
    }

    public void setDocumentCollected(Boolean b) {
        currentJob.getJobStatusAndTracking().setDocumentCollected(b);
    }

    public EntityManager getEntityManager2() {
        return EMF2.createEntityManager();
    }

    public void updateJobCategory() {
        setDirty(true);
    }

    public void updateJobSubCategory() {
        setDirty(true);
    }

    public void updateJob() {
        setDirty(true);
    }

    public void updateJobView(AjaxBehaviorEvent event) {

        doJobViewUpdate(user.getJobTableViewPreference());

    }

    public void doJobViewUpdate(String view) {

        switch (view) {
            case "Cashier View":
                getUser().setJobTableViewPreference("Cashier View");
                openCashierTab();
                break;
            case "Job Costings":
                getUser().setJobTableViewPreference("Job Costings");
                openJobCostingsTab();
                break;
            case "Jobs":
                getUser().setJobTableViewPreference("Jobs");
                openJobsTab();
                break;
            default:
                break;
        }
    }

    public void updateJobClassification() {
        EntityManager em = getEntityManager1();

        // Get the clasification saved for use in setting taxes
        JobCostingAndPayment.setJobCostingTaxes(em, currentJob);
        // Update all costs that depend on tax
        if (currentJob.getId() != null) {
            financeManager.updateAllJobCostings();
        }
    }

    public void updateTestsAndCalibration() {
        currentJob.setNoOfTestsOrCalibrations(currentJob.getNoOfTests() + currentJob.getNoOfCalibrations());

        setDirty(true);
    }

    public void update() {
        setDirty(true);
    }

    public void updatePreferences() {
        setDirty(true);
    }

    public void updateJobsTab() {
        dashboard.renderTab(getEntityManager1(), "jobsTab", getUser().getJobManagementAndTrackingUnit());
        if (getUser().getJobManagementAndTrackingUnit()) {
            if (getUser().getIsJobsPreferredJobTableView()) {
                mainTabView.renderTab(getEntityManager1(), "jobsTab", true);
            }
            if (getUser().getIsCashierPreferredJobTableView()) {
                mainTabView.renderTab(getEntityManager1(), "cashierTab", true);
            }
            if (getUser().getIsJobCostingsPreferredJobTableView()) {
                mainTabView.renderTab(getEntityManager1(), "jobCostingsTab", true);
            }

        }
        setDirty(true);
    }

    public void updateAdminTab() {
        dashboard.renderTab(getEntityManager1(), "adminTab", getUser().getAdminUnit());
        if (getUser().getAdminUnit()) {
            mainTabView.renderTab(getEntityManager1(), "adminTab", true);
        }
        setDirty(true);
    }

    public void updateFinancialAdminTab() {
        dashboard.renderTab(getEntityManager1(), "financialAdminTab", getUser().getFinancialAdminUnit());
        if (getUser().getFinancialAdminUnit()) {
            mainTabView.renderTab(getEntityManager1(), "financialAdminTab", true);
        }
        setDirty(true);
    }

    public void updateDocumentsCollectedDate() {
        setDirty(true);
        if (!currentJob.getJobStatusAndTracking().getDocumentCollected()) {
            currentJob.getJobStatusAndTracking().setDocumentCollectedBy("");
            setDateDocumentCollected(null);
        }
    }

    public void updateJobCompleted() {
        if (getCompleted()) {
            currentJob.getJobStatusAndTracking().setWorkProgress("Completed");
            setJobCompletionDate(new Date());
        } else {
            currentJob.getJobStatusAndTracking().setWorkProgress("Not started");
            setJobCompletionDate(null);
        }
        setDirty(true);
    }

    public void updateSamplesCollectedDate() {
        setDirty(true);
        if (!currentJob.getJobStatusAndTracking().getSamplesCollected()) {
            currentJob.getJobStatusAndTracking().setSamplesCollectedBy("");
            setDateSamplesCollected(null);
        }
    }

    public void updateJobReportNumber() {
        setDirty(true);
    }

    public void updateAutoGenerateJobNumber() {

        if (currentJob.getAutoGenerateJobNumber()) {
            currentJob.setJobNumber(getCurrentJobNumber());
        }
        setDirty(true);

    }

    public void updateNewClient() {
        setDirty(true);
    }

    public void updateJobNumber() {
        setDirty(true);
    }

    public void updateSamplesCollected() {
        setDirty(true);
    }

    public Boolean checkWorkProgressReadinessToBeChanged() {
        EntityManager em = getEntityManager1();

        // Find the currently stored job and check it's work status
        if (getCurrentJob().getId() != null) {
            Job job = Job.findJobById(em, getCurrentJob().getId());
            if (job.getJobStatusAndTracking().getWorkProgress().equals("Completed")
                    && !getUser().getPrivilege().getCanBeJMTSAdministrator()
                    && !getUser().isUserDepartmentSupervisor(getCurrentJob(), em)) {

                // Reset current job to its saved work progress
                getCurrentJob().getJobStatusAndTracking().
                        setWorkProgress(job.getJobStatusAndTracking().getWorkProgress());

                displayCommonMessageDialog(null,
                        "This job is marked as completed and cannot be changed. You may contact the department's supervisor.",
                        "Job Work Progress Cannot Be Changed", "info");

                return false;
            } else if (job.getJobStatusAndTracking().getWorkProgress().equals("Completed")
                    && (getUser().getPrivilege().getCanBeJMTSAdministrator()
                    || getUser().isUserDepartmentSupervisor(getCurrentJob(), em))) {

                return true;
            } else if (!job.getJobStatusAndTracking().getWorkProgress().equals("Completed")
                    && getCurrentJob().getJobStatusAndTracking().getWorkProgress().equals("Completed")
                    && !getCurrentJob().getJobCostingAndPayment().getCostingCompleted()) {

                // Reset current job to its saved work progress
                getCurrentJob().getJobStatusAndTracking().
                        setWorkProgress(job.getJobStatusAndTracking().getWorkProgress());

                displayCommonMessageDialog(null,
                        "The job costing needs to be prepared before this job can marked as completed.",
                        "Job Work Progress Cannot Be As Marked Completed", "info");

                return false;

            }
        } else {
            displayCommonMessageDialog(null,
                    "This job cannot be marked as completed because it is not yet saved.",
                    "Job Work Progress Cannot be Changed", "info");
            return false;
        }

        return true;
    }

    public void updateWorkProgress() {
        RequestContext context = RequestContext.getCurrentInstance();

        if (checkWorkProgressReadinessToBeChanged()) {
            if (!currentJob.getJobStatusAndTracking().getWorkProgress().equals("Completed")) {
                currentJob.getJobStatusAndTracking().setCompleted(false);
                currentJob.getJobStatusAndTracking().setSamplesCollected(false);
                currentJob.getJobStatusAndTracking().setDocumentCollected(false);
                // overall job completion
                currentJob.getJobStatusAndTracking().setDateOfCompletion(null);
                // sample collection
                currentJob.getJobStatusAndTracking().setSamplesCollectedBy(null);
                currentJob.getJobStatusAndTracking().setDateSamplesCollected(null);
                // document collection
                currentJob.getJobStatusAndTracking().setDocumentCollectedBy(null);
                currentJob.getJobStatusAndTracking().setDateDocumentCollected(null);

                // Update start date
                if (currentJob.getJobStatusAndTracking().getWorkProgress().equals("Ongoing")
                        && currentJob.getJobStatusAndTracking().getStartDate() == null) {
                    currentJob.getJobStatusAndTracking().setStartDate(new Date());
                } else if (currentJob.getJobStatusAndTracking().getWorkProgress().equals("Not started")) {
                    currentJob.getJobStatusAndTracking().setStartDate(null);
                }

                context.addCallbackParam("jobCompleted", false);
            } else {
                currentJob.getJobStatusAndTracking().setCompleted(true);
                currentJob.getJobStatusAndTracking().setDateOfCompletion(new Date());
                context.addCallbackParam("jobCompleted", true);
            }

            setDirty(true);
        }

    }

    public void resetCurrentJob() {
        EntityManager em = getEntityManager1();

        createJob(em, false);
    }

    public Boolean createJob(EntityManager em, Boolean isSubcontract) {

        RequestContext context = RequestContext.getCurrentInstance();
        Boolean jobCreated;

        try {
            if (isSubcontract) {

                // tk replace this and other use of JS for messaging with 
                // growl messaging
                if (currentJob.getId() == null) {
                    context.addCallbackParam("jobNotSaved", true);
                    return false;
                }

                // Create copy of job and use current sequence number and year.
                Long currentJobSequenceNumber = currentJob.getJobSequenceNumber();
                Integer yearReceived = currentJob.getYearReceived();

                currentJob = Job.copy(em, currentJob, getUser(), true, true);
                currentJob.setClassification(new Classification());
                currentJob.setSubContractedDepartment(new Department());
                currentJob.setIsToBeSubcontracted(isSubcontract);
                currentJob.setYearReceived(yearReceived);
                currentJob.setJobSequenceNumber(currentJobSequenceNumber);

                // Get and use this organization's name as the client
                SystemOption sysOption
                        = SystemOption.findSystemOptionByName(em, "organizationName");
                if (sysOption != null) {
                    currentJob.setClient(Client.findActiveDefaultClient(em, sysOption.getOptionValue(), true));
                } else {
                    currentJob.setClient(Client.findActiveDefaultClient(em, "--", true));
                }

                // Set default billing address
                currentJob.setBillingAddress(currentJob.getClient().getBillingAddress());

                // Get/Set contact of the person creating the subcontract.
                Contact contact = Contact.findClientContactByEmployee(em,
                        getUser().getEmployee(),
                        currentJob.getClient().getId());
                if (contact != null) {
                    currentJob.setContact(contact);
                } else { // Create this contact and add it to the client
                    contact = new Contact(getUser().getEmployee().getFirstName(),
                            getUser().getEmployee().getLastName());
                    contact.save(em);
                    currentJob.getClient().getContacts().add(contact);
                    currentJob.getClient().save(em);
                    currentJob.setContact(contact);
                }

                mainTabView.renderTab(em, "jobDetailTab", true);
                context.update("mainTabViewForm:mainTabView:jobFormTabView");
                mainTabView.select("jobDetailTab");
                context.execute("jobFormTabVar.select(0);");

            } else {
                currentJob = Job.create(em, getUser(), true);
            }
            if (currentJob == null) {
                jobCreated = false;
            } else {
                jobCreated = true;
                if (isSubcontract) {
                    setDirty(true);
                } else {
                    setDirty(false);
                }
            }

            financeManager.setAccPacCustomer(new AccPacCustomer(""));

            // tk replace this and other use of JS for messaging with 
            // growl messaging
            if (context != null) {
                context.addCallbackParam("jobCreated", jobCreated);
            }

        } catch (Exception e) {
            System.out.println(e);
        }

        return true;
    }

    /**
     *
     * @param serviceRequest
     */
    public void createNewJob(ServiceRequest serviceRequest) {
        // handle user privilege and return if the user does not have
        // the privilege to do what they wish
        EntityManager em = getEntityManager1();
        createJob(em, false);

        // fill in fields from service request
        currentJob.setBusinessOffice(serviceRequest.getBusinessOffice());
        currentJob.setJobSequenceNumber(serviceRequest.getServiceRequestSequenceNumber());
        currentJob.getClient().doCopy(serviceRequest.getClient());
        currentJob.setDepartment(serviceRequest.getDepartment());
        currentJob.setAssignedTo(serviceRequest.getAssignedTo());
        if (currentJob.getAutoGenerateJobNumber()) {
            currentJob.setJobNumber(Job.getJobNumber(currentJob, em));
        }
        // set job dirty to ensure it is saved if attempt is made to close it
        //  before saving
        setDirty(true);
    }

    public void subContractJob(ActionEvent actionEvent) {
        EntityManager em = getEntityManager1();
        if (createJob(em, true)) {
            initManagers();
            PrimeFacesUtils.addMessage("Job Copied for Subcontract",
                    "The current job was copied but the copy was not saved. "
                    + "Please enter or change the details for the copied job as required for the subcontract.",
                    FacesMessage.SEVERITY_INFO);
        }
    }

    public void cancelClientEdit(ActionEvent actionEvent) {
        if (currentJob.getClient().getId() == null) {
            currentJob.getClient().setName("");
        }
    }

    public String getSearchResultsTableHeader() {
        return Application.getSearchResultsTableHeader(currentSearchParameters, getJobSearchResultList());
    }

    public void cancelJobEdit(ActionEvent actionEvent) {
        setDirty(false);
        doJobSearch(searchManager.getCurrentSearchParameters());
        setRenderJobDetailTab(false);
    }

    public void closePreferencesDialog2(CloseEvent closeEvent) {
        // Redo search to reloasd stored jobs including
        // the currently edited job.
        closePreferencesDialog1(null);
    }

    public void closePreferencesDialog1(ActionEvent actionEvent) {
        RequestContext context = RequestContext.getCurrentInstance();

        if (isDirty()) {
            // save prefs and update view
            savePreferences();
        }

        dashboard.update("mainTabViewForm:mainTabView");
        dashboard.update("dashboardForm:dashboardAccordion");

        context.update("headerForm");
        context.execute("preferencesDialog.hide();");

        setDirty(false);
    }

    public void savePreferences() {
        // user object holds preferences for now so we save it
        getUser().save(getEntityManager1());
    }

    public void saveAndCloseCurrentJob() {

        setRenderJobDetailTab(false);
        saveCurrentJob();

    }

    public void saveCurrentJob() {
        EntityManager em = getEntityManager1();

        if (isCurrentJobNew() && getUser().getEmployee().getDepartment().getPrivilege().getCanEditJob()) {
            // User can enter/edit any new job...saving
            if (getCurrentJob().prepareAndSave(getEntityManager1(), getUser()).isSuccess()) {
                PrimeFacesUtils.addMessage("Success!", "Job was saved", FacesMessage.SEVERITY_INFO);
            }
        } else if (isCurrentJobNew() && getUser().getEmployee().getDepartment().getPrivilege().getCanEnterJob()) {
            // User can enter any new job...saving
            if (getCurrentJob().prepareAndSave(getEntityManager1(), getUser()).isSuccess()) {
                PrimeFacesUtils.addMessage("Success!", "Job was saved", FacesMessage.SEVERITY_INFO);
            }
        } else if (isCurrentJobNew() && getUser().getPrivilege().getCanEnterJob()) {
            // User can enter any new job...saving
            if (getCurrentJob().prepareAndSave(getEntityManager1(), getUser()).isSuccess()) {
                PrimeFacesUtils.addMessage("Success!", "Job was saved", FacesMessage.SEVERITY_INFO);
            }
        } else if (isCurrentJobNew()
                && getUser().getPrivilege().getCanEnterDepartmentJob()
                && getUser().getEmployee().isMemberOf(Department.findDepartmentAssignedToJob(currentJob, em))) {
            // User can enter new jobs for your department...saving
            if (getCurrentJob().prepareAndSave(getEntityManager1(), getUser()).isSuccess()) {
                PrimeFacesUtils.addMessage("Success!", "Job was saved", FacesMessage.SEVERITY_INFO);
            }
        } else if (isCurrentJobNew()
                && getUser().getPrivilege().getCanEnterOwnJob()
                && isCurrentJobJobAssignedToUser()) {
            // User can enter new jobs for yourself...saving
            if (getCurrentJob().prepareAndSave(getEntityManager1(), getUser()).isSuccess()) {
                PrimeFacesUtils.addMessage("Success!", "Job was saved", FacesMessage.SEVERITY_INFO);
            }
        } else if (isDirty() && !isCurrentJobNew() && getUser().getPrivilege().getCanEditJob()) {
            // User can edit any job...saving
            if (getCurrentJob().prepareAndSave(getEntityManager1(), getUser()).isSuccess()) {
                PrimeFacesUtils.addMessage("Success!", "Job was saved", FacesMessage.SEVERITY_INFO);
            }
        } else if (isDirty() && !isCurrentJobNew()
                && getUser().getPrivilege().getCanEditDepartmentJob()
                && (getUser().getEmployee().isMemberOf(Department.findDepartmentAssignedToJob(currentJob, em))
                || getUser().getEmployee().isMemberOf(currentJob.getDepartment()))) {

            // User can edit jobs for your department...saving
            if (getCurrentJob().prepareAndSave(getEntityManager1(), getUser()).isSuccess()) {
                PrimeFacesUtils.addMessage("Success!", "Job was saved", FacesMessage.SEVERITY_INFO);
            }
        } else if (isDirty() && !isCurrentJobNew()
                && getUser().getPrivilege().getCanEditOwnJob()
                && isCurrentJobJobAssignedToUser()) {
            // User can edit own jobs...saving
            if (getCurrentJob().prepareAndSave(getEntityManager1(), getUser()).isSuccess()) {
                PrimeFacesUtils.addMessage("Success!", "Job was saved", FacesMessage.SEVERITY_INFO);
            }
        } else if (currentJob.getIsToBeCopied()) {
            // Saving cause copy is being created
            if (getCurrentJob().prepareAndSave(getEntityManager1(), getUser()).isSuccess()) {
                PrimeFacesUtils.addMessage("Success!", "Job was saved", FacesMessage.SEVERITY_INFO);
            }
        } else if (currentJob.getIsToBeSubcontracted()) {
            // Saving cause subcontract is being created
            if (getCurrentJob().prepareAndSave(getEntityManager1(), getUser()).isSuccess()) {
                PrimeFacesUtils.addMessage("Success!", "Job was saved", FacesMessage.SEVERITY_INFO);
            }
        } else if (!isDirty()) {
            // Job not dirty so it will not be saved.
            PrimeFacesUtils.addMessage("Already Saved", "Job was not saved because it was not modified or it was recently saved", FacesMessage.SEVERITY_INFO);
        } else {
            PrimeFacesUtils.addMessage("Insufficient Privilege",
                    "You may not have the privilege to enter/save this job. \n"
                    + "Please contact the IT/MIS Department for further assistance.",
                    FacesMessage.SEVERITY_ERROR);
        }

    }

    public Boolean checkJobEntryPrivilege(EntityManager em, RequestContext context) {
        // prompt to save modified job before attempting to create new job
        if (getUser().getPrivilege().getCanEnterJob()
                || getUser().getPrivilege().getCanEnterDepartmentJob()
                || getUser().getDepartment().getPrivilege().getCanEnterJob()
                || getUser().getPrivilege().getCanEnterOwnJob()) {
            return true;
        } else {
            displayUserPrivilegeDialog(context,
                    "Job Entry Privilege",
                    "You do not have job entry privilege.");

            return false;
        }
    }

    public Boolean getIsClientNameValid() {
        return BusinessEntityUtils.validateName(currentJob.getClient().getName());
    }

    public Boolean getIsBillingAddressNameValid() {
        return BusinessEntityUtils.validateText(currentJob.getBillingAddress().getName());
    }

    public String getNewJobEmailMessage(Job job) {
        String message = "";
        DateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");

        message = message + "Dear Colleague,<br><br>";
        message = message + "A job with the following details was assigned to your department via the <a href='http://boshrmapp:8080/jmts'>Job Management & Tracking System (JMTS)</a>:<br><br>";
        message = message + "<span style='font-weight:bold'>Job number: </span>" + job.getJobNumber() + "<br>";
        message = message + "<span style='font-weight:bold'>Client: </span>" + job.getClient().getName() + "<br>";
        if (!job.getSubContractedDepartment().getName().equals("--")) {
            message = message + "<span style='font-weight:bold'>Department: </span>" + job.getSubContractedDepartment().getName() + "<br>";
        } else {
            message = message + "<span style='font-weight:bold'>Department: </span>" + job.getDepartment().getName() + "<br>";
        }
        message = message + "<span style='font-weight:bold'>Date submitted: </span>" + formatter.format(job.getJobStatusAndTracking().getDateSubmitted()) + "<br>";
        message = message + "<span style='font-weight:bold'>Current assignee: </span>" + BusinessEntityUtils.getPersonFullName(job.getAssignedTo(), Boolean.FALSE) + "<br>";
        message = message + "<span style='font-weight:bold'>Entered by: </span>" + BusinessEntityUtils.getPersonFullName(job.getJobStatusAndTracking().getEnteredBy(), Boolean.FALSE) + "<br>";
        message = message + "<span style='font-weight:bold'>Task/Sample descriptions: </span>" + job.getJobSampleDescriptions() + "<br><br>";
        message = message + "If you are the department's supervisor, you should immediately ensure that the job was correctly assigned to your staff member who will see to its completion.<br><br>";
        message = message + "If this job was incorrectly assigned to your department, the department supervisor should contact the person who entered/assigned the job.<br><br>";
        message = message + "This email was automatically generated and sent by the <a href='http://boshrmapp:8080/jmts'>JMTS</a>. Please DO NOT reply.<br><br>";
        message = message + "Signed<br>";
        message = message + "Job Manager";

        return message;
    }

    public String getUpdatedJobEmailMessage(Job job) {
        String message = "";
        DateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");

        message = message + "Dear Colleague,<br><br>";
        message = message + "A job with the following details was updated by someone outside of your department via the <a href='http://boshrmapp:8080/jmts'>Job Management & Tracking System (JMTS)</a>:<br><br>";
        message = message + "<span style='font-weight:bold'>Job number: </span>" + job.getJobNumber() + "<br>";
        message = message + "<span style='font-weight:bold'>Client: </span>" + job.getClient().getName() + "<br>";
        if (!job.getSubContractedDepartment().getName().equals("--")) {
            message = message + "<span style='font-weight:bold'>Department: </span>" + job.getSubContractedDepartment().getName() + "<br>";
        } else {
            message = message + "<span style='font-weight:bold'>Department: </span>" + job.getDepartment().getName() + "<br>";
        }
        message = message + "<span style='font-weight:bold'>Date submitted: </span>" + formatter.format(job.getJobStatusAndTracking().getDateSubmitted()) + "<br>";
        message = message + "<span style='font-weight:bold'>Current assignee: </span>" + BusinessEntityUtils.getPersonFullName(job.getAssignedTo(), Boolean.FALSE) + "<br>";
        message = message + "<span style='font-weight:bold'>Updated by: </span>" + BusinessEntityUtils.getPersonFullName(job.getJobStatusAndTracking().getEditedBy(), Boolean.FALSE) + "<br>";
        message = message + "<span style='font-weight:bold'>Task/Sample descriptions: </span>" + job.getJobSampleDescriptions() + "<br><br>";
        message = message + "You are being informed of this update so that you may take the requisite action.<br><br>";
        message = message + "This email was automatically generated and sent by the <a href='http://boshrmapp:8080/jmts'>JMTS</a>. Please DO NOT reply.<br><br>";
        message = message + "Signed<br>";
        message = message + "Job Manager";

        return message;
    }

    /**
     * Update/create alert for the current job if the job is not completed.
     *
     * @param em
     */
    public void updateAlert(EntityManager em) throws Exception {
        if (getCurrentJob().getJobStatusAndTracking().getCompleted() == null) {
            em.getTransaction().begin();

            Alert alert = Alert.findFirstAlertByOwnerId(em, currentJob.getId());
            if (alert == null) { // This seems to be a new job
                alert = new Alert(currentJob.getId(), new Date(), "Job entered");
                em.persist(alert);
            } else {
                em.refresh(alert);
                alert.setActive(true);
                alert.setDueTime(new Date());
                alert.setStatus("Job saved");
            }

            em.getTransaction().commit();
        } else if (!getCurrentJob().getJobStatusAndTracking().getCompleted()) {
            em.getTransaction().begin();

            Alert alert = Alert.findFirstAlertByOwnerId(em, currentJob.getId());
            if (alert == null) { // This seems to be a new job
                alert = new Alert(currentJob.getId(), new Date(), "Job saved");
                em.persist(alert);
            } else {
                em.refresh(alert);
                alert.setActive(true);
                alert.setDueTime(new Date());
                alert.setStatus("Job saved");
            }

            em.getTransaction().commit();
        }

    }

    public void sendErrorEmail(String subject, String message) {
        try {
            // send error message to developer's email            
            BusinessEntityUtils.postMail(null, null, subject, message);
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    public void editJob() {
        RequestContext context = RequestContext.getCurrentInstance();

        mainTabView.renderTab(getEntityManager1(), "jobDetailTab", true);
        mainTabView.select("jobDetailTab");
        context.execute("jobFormTabVar.select(0);");
    }

    public void editJobCostingAndPayment() {
        RequestContext context = RequestContext.getCurrentInstance();

        mainTabView.renderTab(getEntityManager1(), "jobDetailTab", true);
        mainTabView.select("jobDetailTab");
        context.execute("jobFormTabVar.select(0);");
    }

    public String getJobAssignee() {
        if (currentJob.getAssignedTo() != null) {
            return currentJob.getAssignedTo().getLastName() + ", " + currentJob.getAssignedTo().getFirstName();
        } else {
            return "";
        }
    }

    public String getCurrentJobNumber() {
        return Job.getJobNumber(currentJob, getEntityManager1());
    }

    public Date getJobSubmissionDate() {
        if (currentJob != null) {
            if (currentJob.getJobStatusAndTracking().getDateSubmitted() != null) {
                return currentJob.getJobStatusAndTracking().getDateSubmitted();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public void setJobSubmissionDate(Date date) {
        currentJob.getJobStatusAndTracking().setDateSubmitted(date);
    }

    public void handleDateSubmittedSelect() {

        EntityManager em = getEntityManager1();

        if (currentJob.getAutoGenerateJobNumber()) {
            currentJob.setJobNumber(Job.getJobNumber(currentJob, getEntityManager1()));
        }

        JobCostingAndPayment.setJobCostingTaxes(em, currentJob);
        if (currentJob.getId() != null) {
            financeManager.updateAllJobCostings();
        }
    }

    public void updateDateJobCompleted(SelectEvent event) {
        Date selectedDate = (Date) event.getObject();

        currentJob.getJobStatusAndTracking().setDateOfCompletion(selectedDate);

        setDirty(Boolean.TRUE);
    }

    public void updateDateExpectedCompletionDate(SelectEvent event) {
        Date selectedDate = (Date) event.getObject();

        currentJob.getJobStatusAndTracking().setExpectedDateOfCompletion(selectedDate);

        setDirty(Boolean.TRUE);
    }

    public void updateDateSamplesCollected(SelectEvent event) {
        Date selectedDate = (Date) event.getObject();

        currentJob.getJobStatusAndTracking().setDateSamplesCollected(selectedDate);

        setDirty(Boolean.TRUE);
    }

    public void updateDateDocsCollected(SelectEvent event) {
        Date selectedDate = (Date) event.getObject();

        currentJob.getJobStatusAndTracking().setDateDocumentCollected(selectedDate);

        setDirty(Boolean.TRUE);
    }

    public List<Address> completeClientAddress(String query) {
        List<Address> addresses = new ArrayList<>();

        try {

            for (Address address : getCurrentJob().getClient().getAddresses()) {
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

            for (Contact contact : getCurrentJob().getClient().getContacts()) {
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

    public void doJobSearch(SearchParameters currentSearchParameters) {
        this.currentSearchParameters = currentSearchParameters;
        EntityManager em = null;

        if (getUser().getId() != null) {
            em = getEntityManager1();
            jobSearchResultList = Job.findJobsByDateSearchField(em,
                    getUser(),
                    currentSearchParameters.getDateField(),
                    currentSearchParameters.getJobType(),
                    currentSearchParameters.getSearchType(),
                    currentSearchParameters.getSearchText(),
                    currentSearchParameters.getDatePeriod().getStartDate(),
                    currentSearchParameters.getDatePeriod().getEndDate(), false);

            if (jobSearchResultList.isEmpty()) { // Do search with sample search enabled
                jobSearchResultList = Job.findJobsByDateSearchField(em,
                        getUser(),
                        currentSearchParameters.getDateField(),
                        currentSearchParameters.getJobType(),
                        currentSearchParameters.getSearchType(),
                        currentSearchParameters.getSearchText(),
                        currentSearchParameters.getDatePeriod().getStartDate(),
                        currentSearchParameters.getDatePeriod().getEndDate(), true);
            }
        } else {
            jobSearchResultList = new ArrayList<>();
        }

    }

    /**
     *
     * @return
     */
    public List<Classification> getActiveClassifications() {
        EntityManager em = getEntityManager1();

        List<Classification> classifications = Classification.findAllActiveClassifications(em);

        return classifications;
    }

    public List<Sector> getActiveSectors() {
        EntityManager em = getEntityManager1();

        List<Sector> sectors = Sector.findAllActiveSectors(em);

        return sectors;
    }

    public List<Address> getClientAddresses() {
        EntityManager em = getEntityManager1();

        List<Address> addresses = getCurrentJob().getClient().getAddresses();

        return addresses;
    }

    public List<JobCategory> getActiveJobCategories() {
        EntityManager em = getEntityManager1();

        List<JobCategory> categories = JobCategory.findAllActiveJobCategories(em);

        return categories;
    }

    public List<JobSubCategory> getActiveJobSubCategories() {
        EntityManager em = getEntityManager1();

        List<JobSubCategory> subCategories = JobSubCategory.findAllActiveJobSubCategories(em);

        return subCategories;
    }

    public List<Job> getJobSearchResultList() {
        if (jobSearchResultList == null) {
            jobSearchResultList = new ArrayList<>();
        }
        return jobSearchResultList;
    }

    public Job getCurrentJob() {
        if (currentJob == null) {
            resetCurrentJob();
        }
        return currentJob;
    }

    public void setCurrentJob(Job currentJob) {
        this.currentJob = currentJob;
    }

    private void initManagers() {
        contractManager.setCurrentJob(this.currentJob);
        contractManager.setUser(this.user);
        jobSampleManager.setCurrentJob(currentJob);
        jobSampleManager.setUser(user);
        financeManager.setCurrentJob(currentJob);
        financeManager.setUser(user);
    }

    public void setEditCurrentJob(Job currentJob) {
        this.currentJob = currentJob;
        initManagers();
    }

    public void setEditJobCosting(Job currentJob) {
        this.currentJob = currentJob;
        initManagers();
    }

    public void setEditJobCostingAndPayment(Job currentJob) {
        this.currentJob = currentJob;
        initManagers();
        financeManager.setEnableOnlyPaymentEditing(true);
    }

    @Override
    public void setDirty(Boolean dirty) {
        getCurrentJob().setIsDirty(dirty);
    }

    @Override
    public Boolean isDirty() {
        return getCurrentJob().getIsDirty();
    }

    public void updateBusinessOffice(SelectEvent event) {

        try {
            if (currentJob.getAutoGenerateJobNumber()) {
                currentJob.setJobNumber(getCurrentJobNumber());
            }
            setDirty(true);

        } catch (Exception e) {

            System.out.println(e);
        }
    }

    public void updateSector() {
        setDirty(true);
    }

    public void updateBillingAddress() {
        setDirty(true);
    }

    public void updateDepartment() {

        EntityManager em;

        try {

            em = getEntityManager1();

            if (currentJob.getAutoGenerateJobNumber()) {
                currentJob.setJobNumber(getCurrentJobNumber());
            }

            JobCostingAndPayment.setJobCostingTaxes(em, currentJob);
            if (currentJob.getId() != null) {
                financeManager.updateAllJobCostings();
            }

        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public void updateSubContractedDepartment() {
        EntityManager em;

        try {
            em = getEntityManager1();

            if (currentJob.getAutoGenerateJobNumber()) {
                currentJob.setJobNumber(getCurrentJobNumber());
            }

            JobCostingAndPayment.setJobCostingTaxes(em, currentJob);
            if (currentJob.getId() != null) {
                financeManager.updateAllJobCostings();
            }

        } catch (Exception e) {
            System.out.println(e + ": updateSubContractedDepartment");
        }
    }

    /**
     * Do update for the client field on the General tab on the Job Details form
     */
    public void updateJobEntryTabClient() {

        financeManager.getAccPacCustomer().setCustomerName(currentJob.getClient().getName());
        if (useAccPacCustomerList) {
            financeManager.updateCreditStatus(null);
        }

        // tk
        System.out.println("billing addr: " + currentJob.getClient().getBillingAddress());
        System.out.println("contact: " + currentJob.getClient().getMainContact());

        currentJob.setBillingAddress(currentJob.getClient().getBillingAddress());
        currentJob.setContact(currentJob.getClient().getMainContact());

        setDirty(true);
    }

    public void updateAssignee() {
        setDirty(true);
    }

    public Job getSelectedJob() {
        if (selectedJob == null) {
            selectedJob = new Job();
            selectedJob.setJobNumber("");
        }
        return selectedJob;
    }

    public void setSelectedJob(Job selectedJob) {
        this.selectedJob = selectedJob;
    }

    public void createNewJobClient() {
        clientManager.createNewClient(true);
        clientManager.setUser(getUser());
        clientManager.setClientOwner(getCurrentJob());
        clientManager.setCurrentAddress(new Address());
        clientManager.setCurrentContact(new Contact());
        clientManager.setIsToBeSaved(true);
        clientManager.setIsClientNameAndIdEditable(getUser().getPrivilege().getCanAddClient());

        PrimeFacesUtils.openDialog(null, "clientDialog", true, true, true, 450, 700);
    }

    public void editJobClient() {
        clientManager.setUser(getUser());
        clientManager.setClientOwner(getCurrentJob());
        clientManager.setCurrentClient(getCurrentJob().getClient());
        clientManager.setCurrentAddress(getCurrentJob().getBillingAddress());
        clientManager.setCurrentContact(getCurrentJob().getContact());
        clientManager.setIsToBeSaved(true);
        clientManager.setIsClientNameAndIdEditable(getUser().getPrivilege().getCanAddClient());

        PrimeFacesUtils.openDialog(null, "clientDialog", true, true, true, 450, 700);
    }

    public ServiceRequest createNewServiceRequest(EntityManager em,
            JobManagerUser user,
            Boolean autoGenerateServiceRequestNumber) {

        ServiceRequest sr = new ServiceRequest();
        sr.setClient(new Client("", false));
        sr.setServiceRequestNumber("");
        sr.setJobDescription("");

        sr.setBusinessOffice(BusinessOffice.findDefaultBusinessOffice(em, "Head Office"));

        sr.setClassification(Classification.findClassificationByName(em, "--"));
        sr.setSector(Sector.findSectorByName(em, "--"));
        sr.setJobCategory(JobCategory.findJobCategoryByName(em, "--"));
        sr.setJobSubCategory(JobSubCategory.findJobSubCategoryByName(em, "--"));

        sr.setServiceContract(ServiceContract.create());
        sr.setAutoGenerateServiceRequestNumber(autoGenerateServiceRequestNumber);

        sr.setDateSubmitted(new Date());

        return sr;
    }

    public JobManagerUser createNewUser(EntityManager em) {
        JobManagerUser jmuser = new JobManagerUser();

        jmuser.setEmployee(Employee.findDefaultEmployee(em, "--", "--", true));

        return jmuser;
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

    public Long getJobCountByQuery(EntityManager em, String query) {
        try {
            return (Long) em.createQuery(query).getSingleResult();
        } catch (Exception e) {
            System.out.println(e);
            return 0L;
        }
    }

    public Long saveServiceContract(EntityManager em, ServiceContract serviceContract) {
        return BusinessEntityUtils.saveBusinessEntity(em, serviceContract);
    }

    public void postJobManagerMailToUser(
            Session mailSession,
            JobManagerUser user,
            String subject,
            String message) throws Exception {

        boolean debug = false;
        Message msg;
        EntityManager em = getEntityManager1();

        if (mailSession == null) {
            //Set the host smtp address
            Properties props = new Properties();
            String mailServer = SystemOption.findSystemOptionByName(getEntityManager1(), "mail.smtp.host").getOptionValue();
            props.put("mail.smtp.host", mailServer);

            // create some properties and get the default Session
            Session session = Session.getDefaultInstance(props, null);
            session.setDebug(debug);
            msg = new MimeMessage(session);
        } else {
            msg = new MimeMessage(mailSession);
        }

        // set the from and to address
        String email = SystemOption.findSystemOptionByName(em, "jobManagerEmailAddress").getOptionValue();
        String name = SystemOption.findSystemOptionByName(em, "jobManagerEmailName").getOptionValue();
        InternetAddress addressFrom = new InternetAddress(email, name); // option job manager email addres
        msg.setFrom(addressFrom);

        InternetAddress[] addressTo = new InternetAddress[1];
        if (user != null) {
            addressTo[0] = new InternetAddress(user.getUsername(), user.getEmployee().getFirstName() + " " + user.getEmployee().getLastName());
        } else {
            String email1 = SystemOption.findSystemOptionByName(em, "administratorEmailAddress").getOptionValue();
            String name1 = SystemOption.findSystemOptionByName(em, "administratorEmailName").getOptionValue();
            addressTo[0] = new InternetAddress(email1, name1);
        }

        msg.setRecipients(Message.RecipientType.TO, addressTo);

        // Setting the Subject and Content Type
        msg.setSubject(subject);
        msg.setContent(message, "text/plain");
        Transport.send(msg);
    }

    public void postJobManagerMail(
            Session mailSession,
            String addressedTo,
            String fullNameOfAddressedTo,
            String subject,
            String message) throws Exception {

        boolean debug = false;
        Message msg;
        EntityManager em = getEntityManager1();

        try {
            if (mailSession == null) {
                //Set the host smtp address
                Properties props = new Properties();
                String mailServer = SystemOption.findSystemOptionByName(em, "mail.smtp.host").getOptionValue();
                String trust = SystemOption.findSystemOptionByName(em, "mail.smtp.ssl.trust").getOptionValue();
                props.put("mail.smtp.host", mailServer);
                props.setProperty("mail.smtp.ssl.trust", trust);

                // create some properties and get the default Session
                Session session = Session.getDefaultInstance(props, null);
                session.setDebug(debug);
                msg = new MimeMessage(session);
            } else {
                msg = new MimeMessage(mailSession);
            }

            // set the from and to address
            String email = SystemOption.findSystemOptionByName(em, "jobManagerEmailAddress").getOptionValue();
            String name = SystemOption.findSystemOptionByName(em, "jobManagerEmailName").getOptionValue();
            InternetAddress addressFrom = new InternetAddress(email, name);
            msg.setFrom(addressFrom);

            InternetAddress[] addressTo = new InternetAddress[1];

            addressTo[0] = new InternetAddress(addressedTo, fullNameOfAddressedTo);

            msg.setRecipients(Message.RecipientType.TO, addressTo);

            // Setting the Subject and Content Type
            msg.setSubject(subject);
            msg.setContent(message, "text/html; charset=utf-8");

            Transport.send(msg);
        } catch (UnsupportedEncodingException | MessagingException e) {
            System.out.println(e);
        }
    }

    public void openJobPricingsDialog() {
        Map<String, Object> options = new HashMap<>();
        options.put("modal", true);
        options.put("draggable", true);
        options.put("resizable", true);
        options.put("contentHeight", 600);

        searchText = "";

        RequestContext.getCurrentInstance().openDialog("jobPricings", options, null);
    }

    public void openJobCostingsDialog() {
        Map<String, Object> options = new HashMap<>();
        options.put("modal", true);
        options.put("draggable", true);
        options.put("resizable", true);
        options.put("contentWidth", 800);
        options.put("contentHeight", 600);

        RequestContext.getCurrentInstance().openDialog("jobCostings", options, null);
    }

    @Override
    public void handleDialogOkButtonClick() {
    }

    @Override
    public void handleDialogYesButtonClick() {

        if (dialogActionHandlerId.equals("unitCostDirty")) {
            RequestContext context = RequestContext.getCurrentInstance();
            context.execute("unitCostDialog.hide();");
        }

    }

    @Override
    public void handleDialogNoButtonClick() {

        if (dialogActionHandlerId.equals("unitCostDirty")) {
            RequestContext context = RequestContext.getCurrentInstance();
            setDirty(false);
            context.execute("unitCostDialog.hide();");
        }
    }

    @Override
    public DialogActionHandler initDialogActionHandlerId(String id) {
        dialogActionHandlerId = id;
        return this;
    }

    @Override
    public String getDialogActionHandlerId() {
        return dialogActionHandlerId;
    }

    @Override
    public void handleDialogCancelButtonClick() {
    }

    public JobDataModel getJobsModel() {
        return new JobDataModel(jobSearchResultList);
    }

    public Boolean getDisableDepartment() {
        return getCurrentJob().getIsSubContracted() || getCurrentJob().getIsToBeSubcontracted();
    }

    public Boolean getRenderSubContractingDepartment() {
        return getCurrentJob().getIsToBeSubcontracted() || getCurrentJob().getIsSubContracted();
    }

    /**
     * This is to be implemented further
     *
     * @return
     */
    public Boolean getDisableSubContracting() {
        try {
            if (getCurrentJob().getIsSubContracted() || getCurrentJob().getIsToBeCopied()) {
                return false;
            } else if (getCurrentJob().getId() != null) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            System.out.println(e + ": getDisableSubContracting");
        }

        return false;
    }

    public Boolean isCurrentJobNew() {
        return (getCurrentJob().getId() == null);
    }

    public void generateEmailAlerts() {

        EntityManager em = getEntityManager1();
        // Post job mail to the department if this is a new job and wasn't entered by 
        // by a person assignment 
        try {
            // Do not sent job email if user is a member of the department to which the job was assigned
            if (isCurrentJobNew() && !getUser().getEmployee().isMemberOf(Department.findDepartmentAssignedToJob(currentJob, em))) {
                List<String> emails = Employee.getDepartmentSupervisorsEmailAddresses(Department.findDepartmentAssignedToJob(currentJob, em), em);
                emails.add(Employee.findEmployeeDefaultEmailAdress(currentJob.getAssignedTo(), getEntityManager1()));
                for (String email : emails) {
                    if (!email.equals("")) {
                        postJobManagerMail(null, email, "", "New Job Assignment", getNewJobEmailMessage(currentJob));
                    } else {
                        sendErrorEmail("The department's head email address is not valid!",
                                "Job number: " + currentJob.getJobNumber()
                                + "\nJMTS User: " + getUser().getUsername()
                                + "\nDate/time: " + new Date());
                    }
                }

            } else if (isDirty() && !getUser().getEmployee().isMemberOf(Department.findDepartmentAssignedToJob(currentJob, em))) {
                List<String> emails = Employee.getDepartmentSupervisorsEmailAddresses(Department.findDepartmentAssignedToJob(currentJob, em), em);
                emails.add(Employee.findEmployeeDefaultEmailAdress(currentJob.getAssignedTo(), getEntityManager1()));
                for (String email : emails) {
                    if (!email.equals("")) {
                        postJobManagerMail(null, email, "", "Job Update Alert", getUpdatedJobEmailMessage(currentJob));
                    } else {
                        sendErrorEmail("The department's head email address is not valid!",
                                "Job number: " + currentJob.getJobNumber()
                                + "\nJMTS User: " + getUser().getUsername()
                                + "\nDate/time: " + new Date());
                    }
                }

            }

        } catch (Exception e) {
            System.out.println("Error generating email alert");
            System.out.println(e);
        }

    }

    public void openClientsTab() {
        clientManager.setUser(user);
        clientManager.setIsClientNameAndIdEditable(getUser().getPrivilege().getCanAddClient());

        mainTabView.renderTab(getEntityManager1(), "clientsTab", true);
        mainTabView.select("clientsTab");
    }

    public void openReportsTab() {
        reportManager.setUser(user);
        reportManager.setMainTabView(mainTabView);
        reportManager.setCurrentJob(currentJob);
        mainTabView.renderTab(getEntityManager1(), "reportsTab", true);
        mainTabView.select("reportsTab");
    }

    public void approveSelectedJobCostings() {
        if (selectedJobs.length > 0) {
            EntityManager em = getEntityManager1();

            for (int i = 0; i < selectedJobs.length; i++) {
                Job job = selectedJobs[i];
                if (job.getJobCostingAndPayment().getCostingCompleted()) {
                    em.getTransaction().begin();
                    job.getJobCostingAndPayment().setCostingApproved(true);
                    BusinessEntityUtils.saveBusinessEntity(em, job);
                    em.getTransaction().commit();
                } else {
                    em.getTransaction().begin();
                    job.getJobCostingAndPayment().setCostingApproved(false);
                    BusinessEntityUtils.saveBusinessEntity(em, job);
                    em.getTransaction().commit();
                    displayCommonMessageDialog(null, "Job costing could not be marked as being approved because it was not prepared", "Not Prepared", "alert");
                }
            }
        } else {
            displayCommonMessageDialog(null, "No job costing was selected", "No Selection", "info");
        }

    }

    public void invoiceSelectedJobCostings() {
        if (selectedJobs.length > 0) {
            EntityManager em = getEntityManager1();

            for (Job job : selectedJobs) {
                if (job.getJobCostingAndPayment().getCostingApproved()) {
                    em.getTransaction().begin();
                    job.getJobCostingAndPayment().setInvoiced(true);
                    BusinessEntityUtils.saveBusinessEntity(em, job);
                    em.getTransaction().commit();
                } else {
                    em.getTransaction().begin();
                    job.getJobCostingAndPayment().setInvoiced(false);
                    BusinessEntityUtils.saveBusinessEntity(em, job);
                    em.getTransaction().commit();
                    displayCommonMessageDialog(null, "Job costing could not be marked as being invoiced because it was not approved", "Not Approved", "alert");
                }
            }
        } else {
            displayCommonMessageDialog(null, "No job costing was selected", "No Selection", "info");
        }
    }

}
