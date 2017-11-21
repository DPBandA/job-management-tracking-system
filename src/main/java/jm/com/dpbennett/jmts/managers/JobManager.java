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
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
import jm.com.dpbennett.business.entity.AccPacDocument;
import jm.com.dpbennett.business.entity.Address;
import jm.com.dpbennett.business.entity.Alert;
import jm.com.dpbennett.business.entity.BusinessEntity;
import jm.com.dpbennett.business.entity.BusinessOffice;
import jm.com.dpbennett.business.entity.CashPayment;
import jm.com.dpbennett.business.entity.Classification;
import jm.com.dpbennett.business.entity.Client;
import jm.com.dpbennett.business.entity.Contact;
import jm.com.dpbennett.business.entity.CostCode;
import jm.com.dpbennett.business.entity.CostComponent;
import jm.com.dpbennett.business.entity.Country;
import jm.com.dpbennett.business.entity.DatePeriod;
import jm.com.dpbennett.business.entity.Department;
import jm.com.dpbennett.business.entity.DepartmentReport;
import jm.com.dpbennett.business.entity.DepartmentUnit;
import jm.com.dpbennett.business.entity.DocumentStandard;
import jm.com.dpbennett.business.entity.DocumentType;
import jm.com.dpbennett.business.entity.Employee;
import jm.com.dpbennett.business.entity.Job;
import jm.com.dpbennett.business.entity.JobCategory;
import jm.com.dpbennett.business.entity.JobCosting;
import jm.com.dpbennett.business.entity.JobCostingAndPayment;
import jm.com.dpbennett.business.entity.JobManagerUser;
import jm.com.dpbennett.business.entity.JobReportItem;
import jm.com.dpbennett.business.entity.JobSample;
import jm.com.dpbennett.business.entity.JobSequenceNumber;
import jm.com.dpbennett.business.entity.JobSubCategory;
import jm.com.dpbennett.business.entity.Laboratory;
import jm.com.dpbennett.business.entity.Manufacturer;
import jm.com.dpbennett.business.entity.Preference;
import jm.com.dpbennett.business.entity.Sector;
import jm.com.dpbennett.business.entity.ServiceContract;
import jm.com.dpbennett.business.entity.ServiceRequest;
import jm.com.dpbennett.business.entity.SystemOption;
import jm.com.dpbennett.business.entity.UnitCost;
import jm.com.dpbennett.business.entity.management.MessageManagement;
import jm.com.dpbennett.business.entity.management.UserManagement;
import jm.com.dpbennett.business.entity.utils.BusinessEntityUtils;
import jm.com.dpbennett.business.entity.utils.DatePeriodJobReport;
import jm.com.dpbennett.business.entity.utils.DatePeriodJobReportColumnData;
import jm.com.dpbennett.business.entity.utils.SearchParameters;
import static jm.com.dpbennett.jmts.Application.checkForLDAPUser;
import jm.com.dpbennett.jmts.utils.DialogActionHandler;
import jm.com.dpbennett.jmts.utils.Dashboard;
import jm.com.dpbennett.jmts.utils.DashboardTab;
import jm.com.dpbennett.jmts.utils.MainTab;
import jm.com.dpbennett.jmts.utils.MainTabView;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.event.TabCloseEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import jm.com.dpbennett.business.entity.management.BusinessEntityManagement;
import jm.com.dpbennett.business.entity.utils.PrimeFacesUtils;
import jm.com.dpbennett.jmts.Application;

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
    private Long selectedJobId;
    private Long currentJobId;
    private Job selectedJob;
    private JobSample selectedJobSample;
    private JobSample backupSelectedJobSample;
    private CashPayment selectedCashPayment;
    private Boolean addJobSample;
    private Boolean addCostComponent;
    private Boolean addCashPayment;
    private Boolean dynamicTabView;
    private StreamedContent jobCostingFile;
    private Boolean renderSearchComponent;
    private Boolean renderJobDetailTab;
    @ManagedProperty(value = "Jobs")
    private Integer longProcessProgress;
    private AccPacCustomer accPacCustomer;
    private List<AccPacDocument> filteredAccPacCustomerDocuments;
    private Boolean useAccPacCustomerList;
    private CostComponent selectedCostComponent;
    private Long receivedById; // tk Can del/move? NB: seem to be used by samples dialog. Remove and use autocomplete with employee converter.
    private String inputTextStyle; // tk Can del/move?
    private String outcome;
    private String defaultOutcome;
    private Integer jobSampleDialogTabViewActiveIndex;
    private Boolean showJobEntry;
    private List<Job> jobSearchResultList;
    private String userPrivilegeDialogHeader;
    private String userPrivilegeDialogMessage;
    private Integer loginAttempts;
    private String selectedJobCostingTemplate;
    private SearchParameters currentSearchParameters;
    private final ClientManager clientManager;
    private final SearchManager searchManager;
    private final ReportManager reportManager;
    private SearchParameters reportSearchParameters;
    private Department unitCostDepartment;
    private UnitCost currentUnitCost;
    private String searchText;
    private List<UnitCost> unitCosts;
    private String dialogActionHandlerId;
    private List<Job> jobsWithCostings;
    private Job currentJobWithCosting;
    private Department jobCostDepartment;
    private String jobsTabTitle;
    private Job[] selectedJobs;
    private Boolean sendJobCostingCompletedEmail;
    private Boolean sendJobCostingApprovedEmail;
    private Boolean showPrepayments;
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
    public JobManager() {
        this.password = null;
        this.username = null;
        this.showLogin = true;
        this.userLoggedIn = false;
        this.westLayoutUnitCollapsed = true;
        this.loginAttempts = 0;
        this.showJobEntry = false;
        this.longProcessProgress = 0;
        accPacCustomer = new AccPacCustomer(null);
        filteredAccPacCustomerDocuments = new ArrayList<>();
        useAccPacCustomerList = false;
        addJobSample = false;
        addCashPayment = false;
        addCostComponent = false;
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
        inputTextStyle = "font-weight: bold;width: 85%";
        defaultOutcome = "jmtlogin";
        outcome = defaultOutcome;
        selectedJobSample = new JobSample();
        jobSampleDialogTabViewActiveIndex = 0;
        clientManager = Application.findBean("clientManager");
        reportManager = Application.findBean("reportManager");
        searchManager = Application.findBean("searchManager");
        sendJobCostingCompletedEmail = false;
        sendJobCostingApprovedEmail = false;
        addCostComponent = false;
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

    public void addMessage(String summary, String detail, Severity severity) {
        FacesMessage message = new FacesMessage(severity, summary, detail);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void prepareToCloseJobDetailTab() {

        RequestContext requestContext = RequestContext.getCurrentInstance();

        requestContext.addCallbackParam("isDirty", isDirty());
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

    public void closeJobDetailTab() {
        mainTabView.renderTab(getEntityManager1(), "jobDetailTab", false);
    }

    public void closeReportsTab() {
        mainTabView.renderTab(getEntityManager1(), "reportsTab", false);
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

    public Boolean getRenderJobDetailTab() {
        return renderJobDetailTab;
    }

    public void setRenderJobDetailTab(Boolean renderJobDetailTab) {
        this.renderJobDetailTab = renderJobDetailTab;
    }

    public List<AccPacDocument> getFilteredAccPacCustomerDocuments() {
        return filteredAccPacCustomerDocuments;
    }

    public void setFilteredAccPacCustomerDocuments(List<AccPacDocument> filteredAccPacCustomerDocuments) {
        this.filteredAccPacCustomerDocuments = filteredAccPacCustomerDocuments;
    }

    public Boolean getShowPrepayments() {
        if (showPrepayments == null) {
            showPrepayments = false;
        }
        return showPrepayments;
    }

    public void setShowPrepayments(Boolean showPrepayments) {
        this.showPrepayments = showPrepayments;
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
        dashboard.renderTab(getEntityManager1(), "jobsTab", true);
        mainTabView.renderTab(getEntityManager1(), "jobsTab", true);
    }

    public void openSystemAdministrationTab() {
        dashboard.renderTab(getEntityManager1(), "adminTab", true);
        mainTabView.renderTab(getEntityManager1(), "adminTab", true);
    }

    public void openFinancialAdministrationTab() {
        dashboard.renderTab(getEntityManager1(), "financialAdminTab", true);
        mainTabView.renderTab(getEntityManager1(), "financialAdminTab", true);
    }

    public String getJobsTabTitle() {
        return jobsTabTitle;
    }

    public void setJobsTabTitle(String jobsTabTitle) {
        this.jobsTabTitle = jobsTabTitle;
    }

    public Department getJobCostDepartment() {
        if (jobCostDepartment == null) {
            jobCostDepartment = new Department("");
        }
        return jobCostDepartment;
    }

    public void setJobCostDepartment(Department jobCostDepartment) {
        this.jobCostDepartment = jobCostDepartment;
    }

    public Job getCurrentJobWithCosting() {
        if (currentJobWithCosting == null) {
            currentJobWithCosting = new Job();
        }
        return currentJobWithCosting;
    }

    public void setCurrentJobWithCosting(Job currentJobWithCosting) {
        this.currentJobWithCosting = currentJobWithCosting;
    }

    public List<Job> getJobsWithCostings() {
        if (jobsWithCostings == null) {
            jobsWithCostings = new ArrayList<>();
        }
        return jobsWithCostings;
    }

    public List<UnitCost> getUnitCosts() {
        if (unitCosts == null) {
            unitCosts = new ArrayList<>();
        }

        return unitCosts;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public UnitCost getCurrentUnitCost() {
        if (currentUnitCost == null) {
            currentUnitCost = new UnitCost();
        }
        return currentUnitCost;
    }

    public void setCurrentUnitCost(UnitCost currentUnitCost) {
        this.currentUnitCost = currentUnitCost;
    }

    public Department getUnitCostDepartment() {
        if (unitCostDepartment == null) {
            unitCostDepartment = new Department("");
        }
        return unitCostDepartment;
    }

    public void setUnitCostDepartment(Department unitCostDepartment) {
        this.unitCostDepartment = unitCostDepartment;
    }

    public SearchParameters getReportSearchParameters() {
        return reportSearchParameters;
    }

    public void setReportSearchParameters(SearchParameters reportSearchParameters) {
        this.reportSearchParameters = reportSearchParameters;
    }

    public Boolean getCanEditJobCosting() {
        // Can edit if user belongs to the department to which the job was assigned
        if ((getCurrentJob().getDepartment().getId().longValue() == getUser().getEmployee().getDepartment().getId().longValue())
                || (getCurrentJob().getSubContractedDepartment().getId().longValue() == getUser().getEmployee().getDepartment().getId().longValue())) {
            return true;
        }

        return false;
    }

    public String getSelectedJobCostingTemplate() {
        return selectedJobCostingTemplate;
    }

    public void setSelectedJobCostingTemplate(String selectedJobCostingTemplate) {
        this.selectedJobCostingTemplate = selectedJobCostingTemplate;
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
            if (currentJob.getAssignedTo().getId().longValue() == getUser().getEmployee().getId().longValue()) {
                return true;
            } else {
                return false;
            }
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

    // tk remove when autcomplete is done
    public Long getSampledById() {
        return selectedJobSample.getSampledBy().getId();
    }

    // tk remove when autcomplete is done
    public Long getReceivedById() {
        return selectedJobSample.getReceivedBy().getId();
    }

    // tk remove when autcomplete is done
    public void setReceivedById(Long receivedById) {
        this.receivedById = receivedById;
    }

    public Integer getJobSampleDialogTabViewActiveIndex() {
        return jobSampleDialogTabViewActiveIndex;
    }

    public void setJobSampleDialogTabViewActiveIndex(Integer jobSampleDialogTabViewActiveIndex) {
        this.jobSampleDialogTabViewActiveIndex = jobSampleDialogTabViewActiveIndex;
    }

    public Long getSelectedAssigneeId() {
        return currentJob.getAssignedTo().getId();
    }

    public Long getSelectedSubcontractedDepartmentId() {
        return currentJob.getSubContractedDepartment().getId();
    }

    public Long getClassificationId() {
        return currentJob.getClassification().getId();
    }

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

    public Long getSelectedBusinessOfficeId() {
        return currentJob.getBusinessOffice().getId();
    }

    public void onCostComponentSelect(SelectEvent event) {
        selectedCostComponent = (CostComponent) event.getObject();
    }

    public CostComponent getSelectedCostComponent() {
        return selectedCostComponent;
    }

    public void setSelectedCostComponent(CostComponent selectedCostComponent) {
        this.selectedCostComponent = selectedCostComponent;
    }

    public Boolean getUseAccPacCustomerList() {
        return useAccPacCustomerList;
    }

    public void setUseAccPacCustomerList(Boolean useAccPacCustomerList) {
        this.useAccPacCustomerList = useAccPacCustomerList;
    }

    public Integer getNumberOfDocumentsPassDocDate(Integer days) {
        Integer count = 0;

        for (AccPacDocument doc : filteredAccPacCustomerDocuments) {
            if (doc.getDaysOverDocumentDate() >= days) {
                ++count;
            }
        }

        return count;
    }

    public String getAccountStatus() {
        if (getTotalInvoicesAmountOverMaxInvDays().doubleValue() > 0.0
                && getTotalInvoicesAmount().doubleValue() > 0.0) {
            return "hold";
        } else {
            return "active";
        }
    }

    public BigDecimal getTotalInvoicesAmountOverMaxInvDays() {
        BigDecimal total = new BigDecimal(0.0);

        for (AccPacDocument doc : filteredAccPacCustomerDocuments) {
            if (doc.getDaysOverdue() > getMaxDaysPassInvoiceDate()) {
                total = total.add(doc.getCustCurrencyAmountDue());
            }
        }

        return total;
    }

    public BigDecimal getTotalInvoicesAmount() {
        BigDecimal total = new BigDecimal(0.0);

        for (AccPacDocument doc : filteredAccPacCustomerDocuments) {
            total = total.add(doc.getCustCurrencyAmountDue());
        }

        return total;
    }

    public Integer getMaxDaysPassInvoiceDate() {

        EntityManager em = getEntityManager1();

        int days = Integer.parseInt(SystemOption.findSystemOptionByName(em, "maxDaysPassInvoiceDate").getOptionValue());

        return days;
    }

    public List getWorkProgressList() {
        ArrayList list = new ArrayList();
        EntityManager em = getEntityManager1();

        String listAsString = SystemOption.findSystemOptionByName(em, "workProgressList").getOptionValue();
        String progressName[] = listAsString.split(";");

        for (String name : progressName) {
            list.add(new SelectItem(name, name));
        }

        return list;

    }

    /**
     * Get status based on the total amount on documents pass the max allowed
     * days pass the invoice date
     *
     * @return
     */
    public String getAccPacCustomerAccountStatus() {

        if (getAccountStatus().equals("hold")) {
            return "HOLD";
        } else {
            return "ACTIVE";
        }
    }

    public Integer getNumDocumentsPassMaxInvDate() {
        return getNumberOfDocumentsPassDocDate(getMaxDaysPassInvoiceDate());
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

    public StreamedContent getJobCostingAnalysisFile(EntityManager em) {

        HashMap parameters = new HashMap();

        try {
            parameters.put("jobId", getCurrentJob().getId().longValue());

            Client client = Application.getActiveClientByNameIfAvailable(em, getCurrentJob().getClient());

            parameters.put("contactPersonName", BusinessEntityUtils.getContactFullName(client.getMainContact()));
            parameters.put("customerAddress", client.getBillingAddress().toString());
            parameters.put("contactNumbers", client.getStringListOfContactPhoneNumbers());
            parameters.put("jobDescription", getCurrentJob().getJobDescription());

            parameters.put("totalCost", getCurrentJob().getJobCostingAndPayment().getTotalJobCostingsAmount());
            parameters.put("depositReceiptNumbers", getCurrentJob().getJobCostingAndPayment().getReceiptNumber());
            parameters.put("discount", getCurrentJob().getJobCostingAndPayment().getDiscount());
            parameters.put("discountType", getCurrentJob().getJobCostingAndPayment().getDiscountType());
            parameters.put("deposit", getCurrentJob().getJobCostingAndPayment().getDeposit());
            parameters.put("amountDue", getCurrentJob().getJobCostingAndPayment().getAmountDue());
            parameters.put("totalTax", getCurrentJob().getJobCostingAndPayment().getTotalTax());
            parameters.put("totalTaxLabel", getCurrentJob().getJobCostingAndPayment().getTotalTaxLabel());
            parameters.put("grandTotalCostLabel", getCurrentJob().getJobCostingAndPayment().getTotalCostWithTaxLabel().toUpperCase().trim());
            parameters.put("grandTotalCost", getCurrentJob().getJobCostingAndPayment().getTotalCost());

            Connection con = BusinessEntityUtils.establishConnection(
                    SystemOption.findSystemOptionByName(em, "defaultDatabaseDriver").getOptionValue(),
                    SystemOption.findSystemOptionByName(em, "defaultDatabaseURL").getOptionValue(),
                    SystemOption.findSystemOptionByName(em, "defaultDatabaseUsername").getOptionValue(),
                    SystemOption.findSystemOptionByName(em, "defaultDatabasePassword").getOptionValue());

            if (con != null) {
                try {
                    StreamedContent streamContent;
                    // generate report
                    JasperPrint print = JasperFillManager.fillReport(SystemOption.findSystemOptionByName(em, "jobCosting").getOptionValue(), parameters, con);

                    byte[] fileBytes = JasperExportManager.exportReportToPdf(print);

                    streamContent = new DefaultStreamedContent(new ByteArrayInputStream(fileBytes), "application/pdf", "Job Costing - " + currentJob.getJobNumber() + ".pdf");

                    setLongProcessProgress(100);

                    return streamContent;
                } catch (JRException ex) {
                    System.out.println(ex);
                    return null;
                }
            }

            return null;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }

    }

    public StreamedContent getJobCostingFile() {
        EntityManager em = null;

        try {
            em = getEntityManager1();

            prepareAndSaveCurrentJob(em);

            jobCostingFile = getJobCostingAnalysisFile(em);

            setLongProcessProgress(100);

        } catch (Exception e) {
            System.out.println(e);
            setLongProcessProgress(100);
        }

        return jobCostingFile;
    }

    public void createNewJob() {
        RequestContext context = RequestContext.getCurrentInstance();
        EntityManager em = getEntityManager1();

        if (checkJobEntryPrivilege(em, context)) {
            createJob(em, false);
            mainTabView.renderTab(getEntityManager1(), "jobDetailTab", true);
            context.update("mainTabViewForm:mainTabView:jobFormTabView");
            context.execute("jobFormTabVar.select(0);");
        }

    }

    public StreamedContent getServiceContractStreamContent() {
        EntityManager em = null;

        try {

            em = getEntityManager1();

            String filePath = SystemOption.findSystemOptionByName(em, "serviceContract").getOptionValue();
            FileInputStream stream = createServiceContractExcelFileInputStream(em, getUser(), currentJob.getId(), filePath);

            DefaultStreamedContent dsc = new DefaultStreamedContent(stream, "application/xls", "servicecontract.xls");

            return dsc;

        } catch (Exception e) {
            System.out.println(e);
        }

        return null;
    }

    public StreamedContent getServiceContractFile() {
        StreamedContent serviceContractStreamContent = null;
      
        try {

            serviceContractStreamContent = getServiceContractStreamContent();

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

    public void updatePreferedJobTableView() {
        setDirty(true);
    }

    public Boolean getCurrentJobIsValid() {
        if (getCurrentJob().getId() != null && !getCurrentJob().getIsDirty()) {
            return true;
        }

        return false;
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

    public Boolean getCanExportJobCosting() {
        if (getCurrentJob().getJobCostingAndPayment().getCostingApproved()
                && getCurrentJob().getJobCostingAndPayment().getCostingCompleted()) {
            return false;
        }
        return true;
    }

    public void invoiceJobCosting() {
        if (getUser().getEmployee().isMemberOf(getDepartmentBySystemOptionDeptId("invoicingDepartmentId"))) {
            if (!getCurrentJob().getJobCostingAndPayment().getCostingApproved()
                    && !getCurrentJob().getJobCostingAndPayment().getCostingCompleted()) {
                getCurrentJob().getJobCostingAndPayment().setInvoiced(false);
                displayCommonMessageDialog(null, "This job costing cannot be marked as being invoiced because it is not prepared/approved", "Not prepared/Approved", "alert");
            } else {
                setDirty(true);
            }
        } else {
            displayCommonMessageDialog(null, "You do not have permission to change the invoiced status of this job costing.", "Permission Denied", "alert");
            getCurrentJob().getJobCostingAndPayment().setInvoiced(!getCurrentJob().getJobCostingAndPayment().getInvoiced());
        }
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
     * Determine if the current user can mark the current job costing as being
     * completed. This is done by determining if the job was assigned to the
     * user.
     *
     * @param job
     * @return
     */
    public Boolean canUserCompleteJobCosting(Job job) {
        return isJobAssignedToUserDepartment();
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

    // service requested - calibration
    public Boolean getServiceRequestedCalibration() {
        if (currentJob != null) {
            return currentJob.getServiceContract().getServiceRequestedCalibration();
        } else {
            return false;
        }
    }

    public void setServiceRequestedCalibration(Boolean b) {
        currentJob.getServiceContract().setServiceRequestedCalibration(b);
    }

    // service requested - label evaluation
    public Boolean getServiceRequestedLabelEvaluation() {
        if (currentJob != null) {
            return currentJob.getServiceContract().getServiceRequestedLabelEvaluation();
        } else {
            return false;
        }
    }

    public void setServiceRequestedLabelEvaluation(Boolean b) {
        currentJob.getServiceContract().setServiceRequestedLabelEvaluation(b);
    }

    // service requested - inspection
    public Boolean getServiceRequestedInspection() {
        if (currentJob != null) {
            return currentJob.getServiceContract().getServiceRequestedInspection();
        } else {
            return false;
        }
    }

    public void setServiceRequestedInspection(Boolean b) {
        currentJob.getServiceContract().setServiceRequestedInspection(b);
    }

    // service requested - consultancy
    public Boolean getServiceRequestedConsultancy() {
        if (currentJob != null) {
            return currentJob.getServiceContract().getServiceRequestedConsultancy();
        } else {
            return false;
        }
    }

    public void setServiceRequestedConsultancy(Boolean b) {
        currentJob.getServiceContract().setServiceRequestedConsultancy(b);
    }

    // service requested - training
    public Boolean getServiceRequestedTraining() {
        if (currentJob != null) {
            return currentJob.getServiceContract().getServiceRequestedTraining();
        } else {
            return false;
        }
    }

    public void setServiceRequestedTraining(Boolean b) {
        currentJob.getServiceContract().setServiceRequestedTraining(b);
    }

    // service requested - other
    public Boolean getServiceRequestedOther() {
        if (currentJob != null) {
            return currentJob.getServiceContract().getServiceRequestedOther();
        } else {
            return false;
        }
    }

    public void setServiceRequestedOther(Boolean b) {
        currentJob.getServiceContract().setServiceRequestedOther(b);
        if (!b) {
            currentJob.getServiceContract().setServiceRequestedOtherText(null);
        }
    }

    //Intended Market
    //intended martket - local
    public Boolean getIntendedMarketLocal() {
        if (currentJob != null) {
            return currentJob.getServiceContract().getIntendedMarketLocal();
        } else {
            return false;
        }
    }

    public void setIntendedMarketLocal(Boolean b) {
        currentJob.getServiceContract().setIntendedMarketLocal(b);
    }

    // intended martket - caricom
    public Boolean getIntendedMarketCaricom() {
        if (currentJob != null) {
            return currentJob.getServiceContract().getIntendedMarketCaricom();
        } else {
            return false;
        }
    }

    public void setIntendedMarketCaricom(Boolean b) {
        currentJob.getServiceContract().setIntendedMarketCaricom(b);
    }

    // intended martket - UK
    public Boolean getIntendedMarketUK() {
        if (currentJob != null) {
            return currentJob.getServiceContract().getIntendedMarketUK();
        } else {
            return false;
        }
    }

    public void setIntendedMarketUK(Boolean b) {
        currentJob.getServiceContract().setIntendedMarketUK(b);
    }

    // intended martket - USA
    public Boolean getIntendedMarketUSA() {
        if (currentJob != null) {
            return currentJob.getServiceContract().getIntendedMarketUSA();
        } else {
            return false;
        }
    }

    public void setIntendedMarketUSA(Boolean b) {
        currentJob.getServiceContract().setIntendedMarketUSA(b);
    }
    // intended martket - Canada

    public Boolean getIntendedMarketCanada() {
        if (currentJob != null) {
            return currentJob.getServiceContract().getIntendedMarketCanada();
        } else {
            return false;
        }
    }

    public void setIntendedMarketCanada(Boolean b) {
        currentJob.getServiceContract().setIntendedMarketCanada(b);
    }

    // intended martket - Canada
    public Boolean getIntendedMarketOther() {
        if (currentJob != null) {
            return currentJob.getServiceContract().getIntendedMarketOther();
        } else {
            return false;
        }
    }

    public void setIntendedMarketOther(Boolean b) {
        currentJob.getServiceContract().setIntendedMarketOther(b);
        if (!b) {
            currentJob.getServiceContract().setIntendedMarketOtherText(null);
        }
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

    public CashPayment getSelectedCashPayment() {
        return selectedCashPayment;
    }

    public void setSelectedCashPayment(CashPayment selectedCashPayment) {
        this.selectedCashPayment = selectedCashPayment;
    }

    public EntityManager getEntityManager2() {
        return getEMF2().createEntityManager();
    }

    public void updateJobCategory() {
        setDirty(true);
    }

    public void updateJobSubCategory() {
        setDirty(true);
    }

    public void updateCashPayment(AjaxBehaviorEvent event) {

        System.out.println("Fields for: " + CashPayment.class.getSimpleName());
        Field[] fields = CashPayment.class.getDeclaredFields();
        for (Field field : fields) {
            System.out.println("Name: " + field.getName() + "\n");
        }
    }

    public void updateJob() {
        setDirty(true);
    }

    public void updateAllJobCostings() {
        // Update all costs that depend on tax
        updateJobCosting();
        updateJobCostingEstimate();

        setDirty(true);
    }

    public void updateJobClassification() {
        EntityManager em = getEntityManager1();

        // Get the clasification saved for use in setting taxes
        JobCostingAndPayment.setJobCostingTaxes(em, currentJob);
        // Update all costs that depend on tax
        if (currentJob.getId() != null) {
            updateAllJobCostings();
        }
    }

    public void updateJobCostingEstimate() {
        EntityManager em = getEntityManager1();

        // Update estmated cost and min. deposit  
        if (currentJob.getJobCostingAndPayment().getEstimatedCost() != null) {
            Double estimatedCostWithTaxes = BusinessEntityUtils.roundTo2DecimalPlaces(currentJob.getJobCostingAndPayment().getEstimatedCost()
                    + currentJob.getJobCostingAndPayment().getEstimatedCost()
                    * currentJob.getJobCostingAndPayment().getPercentageGCT() / 100.0);
            currentJob.getJobCostingAndPayment().setEstimatedCostIncludingTaxes(estimatedCostWithTaxes);
            setDirty(true);
        }

        if (currentJob.getJobCostingAndPayment().getMinDeposit() != null) {
            Double minDepositWithTaxes = BusinessEntityUtils.roundTo2DecimalPlaces(currentJob.getJobCostingAndPayment().getMinDeposit()
                    + currentJob.getJobCostingAndPayment().getMinDeposit()
                    * currentJob.getJobCostingAndPayment().getPercentageGCT() / 100.0);
            currentJob.getJobCostingAndPayment().setMinDepositIncludingTaxes(minDepositWithTaxes);
            setDirty(true);
        }

    }

    public void updateTotalDeposit() {
        EntityManager em = getEntityManager1();

        Employee employee = Employee.findEmployeeById(em, getUser().getEmployee().getId());
        if (employee != null) {
            getCurrentJob().getJobCostingAndPayment().setLastPaymentEnteredBy(employee);
        }
        updateAmountDue();
        setDirty(true);
    }

    public void updateTestsAndCalibration() {
        currentJob.setNoOfTestsOrCalibrations(currentJob.getNoOfTests() + currentJob.getNoOfCalibrations());

        setDirty(true);
    }

    public void update() {
        setDirty(true);
    }

    public void updateJobCostingValidity() {
        if (!validateCurrentJobCosting() && getCurrentJob().getJobCostingAndPayment().getCostingCompleted()) {
            getCurrentJob().getJobCostingAndPayment().setCostingCompleted(false);
            getCurrentJob().getJobCostingAndPayment().setCostingApproved(false);
            displayCommonMessageDialog(null, "Removing the content of a required field has invalidated this job costing", "Invalid Job Costing", "info");
        } else {
            setDirty(true);
        }
    }

    public void completeJobCosting() {
        EntityManager em = getEntityManager1();

        // Check for completed subcontracts if applicable
        if (!getCurrentJob().getIsSubContracted() && getCurrentJob().getJobCostingAndPayment().getCostingCompleted()) {
            if (!Job.findIncompleteSubcontracts(em, currentJob).isEmpty()) {
                getCurrentJob().getJobCostingAndPayment().setCostingCompleted(false);
                displayCommonMessageDialog(null,
                        "This job costing cannot be marked prepared before all subcontracted jobs are completed", "Incomplete Subcontracts", "info");
            }
        } else if (getCurrentJob().getJobCostingAndPayment().getCostingApproved()) {
            getCurrentJob().getJobCostingAndPayment().setCostingCompleted(!getCurrentJob().getJobCostingAndPayment().getCostingCompleted());
            getCurrentJob().getJobCostingAndPayment().setCostingCompleted(false);
            displayCommonMessageDialog(null,
                    "The job costing preparation status cannot be changed because it was already approved.",
                    "Job Costing Already Approved", "info");
        } else if (!validateCurrentJobCosting()
                && getCurrentJob().getJobCostingAndPayment().getCostingCompleted()) {
            getCurrentJob().getJobStatusAndTracking().setDateCostingCompleted(null);
            getCurrentJob().getJobCostingAndPayment().setCostingCompleted(false);
            getCurrentJob().getJobCostingAndPayment().setCostingApproved(false);
            sendJobCostingCompletedEmail = false;
            displayCommonMessageDialog(null, "Please enter all required (*) fields before checking this job costing as being prepared.", "Required (*) Fields Missing", "info");
        } else if (getCurrentJob().getJobCostingAndPayment().getCostingCompleted()) {
            getCurrentJob().getJobStatusAndTracking().setDateCostingCompleted(new Date());
            sendJobCostingCompletedEmail = true;
            setDirty(true);
        }
    }

    /**
     * Determine if the current user is the department's supervisor. This is
     * done by determining if the user is the head/active acting head of the
     * department to which the job was assigned.
     *
     * @param job
     * @return
     */
    public Boolean isUserDepartmentSupervisor(Job job) {
        EntityManager em = getEntityManager1();

        Job foundJob = Job.findJobById(em, job.getId());

        if (getDepartmentAssignedToJob(foundJob, em).getHead().getId().longValue() == getUser().getEmployee().getId().longValue()) {
            return true;
        } else if ((getDepartmentAssignedToJob(foundJob, em).getActingHead().getId().longValue() == getUser().getEmployee().getId().longValue())
                && getDepartmentAssignedToJob(foundJob, em).getActingHeadActive()) {
            return true;
        } else {
            return false;
        }
    }

    public void approveJobCosting() {

        if (isUserDepartmentSupervisor(getCurrentJob())
                || (isJobAssignedToUserDepartment()
                && getUser().getPrivilege().getCanApproveJobCosting())) {
            if (!getCurrentJob().getJobCostingAndPayment().getCostingCompleted()) {
                getCurrentJob().getJobStatusAndTracking().setDateCostingApproved(null);
                setJobCostingDate(null);
                getCurrentJob().getJobCostingAndPayment().setCostingApproved(false);
                sendJobCostingApprovedEmail = false;
                displayCommonMessageDialog(null, "This job costing cannot be approved before it is prepared", "Cannot Approve", "info");
            } else {
                Date date = new Date();
                setJobCostingDate(date);
                getCurrentJob().getJobStatusAndTracking().setDateCostingApproved(date);
                sendJobCostingApprovedEmail = true;
                setDirty(true);
            }
        } else {
            setJobCostingDate(null);
            getCurrentJob().getJobCostingAndPayment().setCostingApproved(!getCurrentJob().getJobCostingAndPayment().getCostingApproved());
            displayCommonMessageDialog(null, "You do not have the permission to approve job costings.", "No Permission", "alert");
        }
    }

    public void updatePreferences() {
        setDirty(true);
    }

    public void updateJobsTab() {
        dashboard.renderTab(getEntityManager1(), "jobsTab", getUser().getJobManagementAndTrackingUnit());
        setDirty(true);
    }

    public void updateAdminTab() {
        dashboard.renderTab(getEntityManager1(), "adminTab", getUser().getAdminUnit());
        setDirty(true);
    }

    public void updateFinancialAdminTab() {
        dashboard.renderTab(getEntityManager1(), "financialAdminTab", getUser().getFinancialAdminUnit());
        setDirty(true);
    }

    public void updateDocumentsCollectedDate() {
        setDateDocumentCollected(null);
        setDirty(true);
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
        setDateSamplesCollected(null);
    }

    public void updateCostCode() {
        EntityManager em = getEntityManager1();
        CostCode costCode = CostCode.findCostCodeByCode(em, selectedCostComponent.getCode());

        if (costCode.getRate() != null) {
            selectedCostComponent.setRate(costCode.getRate());
        }

        setDirty(true);

    }

    public Boolean getAllowCostEdit() {
        if (selectedCostComponent != null) {
            if (selectedCostComponent.getCode() == null) {
                return true;
            } else if (selectedCostComponent.getCode().equals("--")) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public String getInputTextStyle() {
        if (getAllowCostEdit()) {
            inputTextStyle = "width: 85%";
        } else {
            // May be made a system option
            inputTextStyle = "width: 85%;background-color: lightgrey;background-image: none;color: blue;";
        }

        return inputTextStyle;
    }

    public void setInputTextStyle(String inputTextStyle) {
        this.inputTextStyle = inputTextStyle;
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

    public void updateIsCostComponentHeading() {

    }

    public void updateIsCostComponentFixedCost() {

        if (getSelectedCostComponent().getIsFixedCost()) {

        }
    }

    public void updateNewClient() {
        setDirty(true);
    }

    public void updateDepartmentReport() {
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
                    && !isUserDepartmentSupervisor(getCurrentJob())) {

                // Reset current job to its saved work progress
                getCurrentJob().getJobStatusAndTracking().
                        setWorkProgress(job.getJobStatusAndTracking().getWorkProgress());

                displayCommonMessageDialog(null,
                        "This job is marked as completed and cannot be changed. You may contact the department's supervisor.",
                        "Job Work Progress Cannot Be Changed", "info");

                return false;
            } else if (job.getJobStatusAndTracking().getWorkProgress().equals("Completed")
                    && (getUser().getPrivilege().getCanBeJMTSAdministrator()
                    || isUserDepartmentSupervisor(getCurrentJob()))) {

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

            accPacCustomer = new AccPacCustomer("");
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
            addMessage("Job Copied for Subcontract",
                    "The current job was copied but the copy was not saved. "
                    + "Please enter or change the details for the copied job as required for the subcontract.",
                    FacesMessage.SEVERITY_INFO);
        }
    }

    public void createNewJobWithoutSavingCurrent(ActionEvent action) {
        EntityManager em = getEntityManager1();
        setDirty(false);
        createJob(em, false);
    }

    public void loadOtherJobWithoutSavingCurrent(ActionEvent action) {
        setDirty(false);
        loadJob();
    }

    public void cancelClientEdit(ActionEvent actionEvent) {
        if (currentJob.getClient().getId() == null) {
            currentJob.getClient().setName("");
        }
    }

    public String getSearchResultsTableHeader() {
        return Application.getSearchResultsTableHeader(currentSearchParameters, getJobSearchResultList());
    }

    public void closeJobCostingDialog() {
        // Redo search to reloasd stored jobs including
        // prompt to save modified job before attempting to create new job
        if (isDirty()) {
            RequestContext context = RequestContext.getCurrentInstance();

            context.update("jobCostingSaveConfirmDialogForm");
            context.execute("jobCostingSaveConfirm.show();");
        }

    }

    public void closeUnitCostDialog() {
        RequestContext context = RequestContext.getCurrentInstance();

        // prompt to save modified job before attempting to create new job
        if (isDirty()) {
            // ask to save         
            displayCommonConfirmationDialog(initDialogActionHandlerId("unitCostDirty"), "This unit cost was modified. Do you wish to save it?", "Unit Cost Not Saved", "info");
        } else {
            context.execute("unitCostDialog.hide();");
        }

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

    public void cancelJobCostingAndPayment(ActionEvent actionEvent) {
        EntityManager em = getEntityManager1();

        // refetch costing data from database
        if (currentJob != null) {
            if (currentJob.getId() != null) {
                Job job = Job.findJobById(em, currentJob.getId());
                currentJob.setJobCostingAndPayment(job.getJobCostingAndPayment());
            }
        }

        setDirty(false);
    }

    public void validateJobCostingAndSaveJob(ActionEvent actionEvent) {

        RequestContext context = RequestContext.getCurrentInstance();
        EntityManager em = getEntityManager1();

        try {

            currentJob.getJobCostingAndPayment().calculateAmountDue();

            if (getUser().getEmployee() != null) {
                currentJob.getJobCostingAndPayment().setFinalCostDoneBy(getUser().getEmployee().getName());
            }

            prepareAndSaveCurrentJob(em);

            // Refresh to make sure job costings ids are not null to
            // avoid resaving newly created costing components
            currentJob.setJobCostingAndPayment(em.find(JobCostingAndPayment.class, currentJob.getJobCostingAndPayment().getId()));

        } catch (Exception e) {
            System.out.println(e);
        }

        context.addCallbackParam("jobCostingAndPaymentSaved", true);
    }

    public Boolean validateCurrentJobCosting() {

        EntityManager em = getEntityManager1();

        try {
            // check for valid job
            if (getCurrentJob().getId() == null) {
                return false;
            }

            // check for job report # and description
            if ((currentJob.getReportNumber() == null) || (currentJob.getReportNumber().trim().equals(""))) {
                return false;
            }

            if (currentJob.getJobDescription().trim().equals("")) {
                return false;
            }

        } catch (Exception e) {
            System.out.println(e);
        }

        return true;

    }

    public void saveAndCloseCurrentJob() {

        setRenderJobDetailTab(false);
        saveCurrentJob();

    }

    public void saveCurrentJob() {
        EntityManager em = getEntityManager1();

        if (isCurrentJobNew() && getUser().getEmployee().getDepartment().getPrivilege().getCanEditJob()) {
            System.out.println("You can enter/edit any new job...saving");
            prepareAndSaveCurrentJob(em);
        } else if (isCurrentJobNew() && getUser().getEmployee().getDepartment().getPrivilege().getCanEnterJob()) {
            System.out.println("You can enter any new job...saving");
            prepareAndSaveCurrentJob(em);
        } else if (isCurrentJobNew() && getUser().getPrivilege().getCanEnterJob()) {
            System.out.println("You can enter any new job...saving");
            prepareAndSaveCurrentJob(em);
        } else if (isCurrentJobNew()
                && getUser().getPrivilege().getCanEnterDepartmentJob()
                && getUser().getEmployee().isMemberOf(getDepartmentAssignedToJob(currentJob, em))) {
            System.out.println("You can enter new jobs for your department...saving");
            prepareAndSaveCurrentJob(em);
        } else if (isCurrentJobNew()
                && getUser().getPrivilege().getCanEnterOwnJob()
                && isCurrentJobJobAssignedToUser()) {
            System.out.println("You can enter new jobs for yourself...saving");
            prepareAndSaveCurrentJob(em);
        } else if (isDirty() && !isCurrentJobNew() && getUser().getPrivilege().getCanEditJob()) {
            System.out.println("You can edit any job...saving");
            prepareAndSaveCurrentJob(em);
        } else if (isDirty() && !isCurrentJobNew()
                && getUser().getPrivilege().getCanEditDepartmentJob()
                && (getUser().getEmployee().isMemberOf(getDepartmentAssignedToJob(currentJob, em))
                || getUser().getEmployee().isMemberOf(currentJob.getDepartment()))) {

            System.out.println("You can edit jobs for your department...saving");
            prepareAndSaveCurrentJob(em);
        } else if (isDirty() && !isCurrentJobNew()
                && getUser().getPrivilege().getCanEditOwnJob()
                && isCurrentJobJobAssignedToUser()) {
            System.out.println("You can edit own jobs...saving");
            prepareAndSaveCurrentJob(em);
        } else if (currentJob.getIsToBeCopied()) {
            System.out.println("Saving cause copy is being created");
            prepareAndSaveCurrentJob(em);
        } else if (currentJob.getIsToBeSubcontracted()) {
            System.out.println("Saving cause subcontract is being created");
            prepareAndSaveCurrentJob(em);
        } else if (!isDirty()) {
            System.out.println("Job not dirty so it will not be saved.");
        } else {
            addMessage("Insufficient Privilege",
                    "You may not have the privilege to enter/save this job. \n"
                    + "Please contact the IT/MIS Department for further assistance.",
                    FacesMessage.SEVERITY_ERROR);
        }

    }

    public void saveUnitCost() {
        EntityManager em = getEntityManager1();
        RequestContext context = RequestContext.getCurrentInstance();

        try {

            // Validate and save objects
            // Department
            Department department = Department.findDepartmentByName(em, getCurrentUnitCost().getDepartment().getName());
            if (department == null) {
                setInvalidFormFieldMessage("This unit cost cannot be saved because a valid department was not entered.");

                context.update("invalidFieldDialogForm");
                context.execute("invalidFieldDialog.show();");
                return;
            } else {
                getCurrentUnitCost().setDepartment(department);
            }

            // Department unit
            DepartmentUnit departmentUnit = DepartmentUnit.findDepartmentUnitByName(em, getCurrentUnitCost().getDepartmentUnit().getName());
            if (departmentUnit == null) {
                getCurrentUnitCost().setDepartmentUnit(DepartmentUnit.getDefaultDepartmentUnit(em, "--"));
            } else {
                getCurrentUnitCost().setDepartmentUnit(departmentUnit);
            }

            // Laboratory unit
            Laboratory laboratory = Laboratory.findLaboratoryByName(em, getCurrentUnitCost().getLaboratory().getName());
            if (laboratory == null) {
                getCurrentUnitCost().setLaboratory(Laboratory.getDefaultLaboratory(em, "--"));
            } else {
                getCurrentUnitCost().setLaboratory(laboratory);
            }

            // Service
            if (getCurrentUnitCost().getService().trim().equals("")) {
                setInvalidFormFieldMessage("This unit cost cannot be saved because a valid service was not entered.");
                context.update("invalidFieldDialogForm");
                context.execute("invalidFieldDialog.show();");
                return;
            }

            // Cost
            if (getCurrentUnitCost().getCost() <= 0.0) {
                setInvalidFormFieldMessage("This unit cost cannot be saved because a valid cost was not entered.");
                context.update("invalidFieldDialogForm");
                context.execute("invalidFieldDialog.show();");
                return;
            }

            // Effective date
            if (getCurrentUnitCost().getEffectiveDate() == null) {
                setInvalidFormFieldMessage("This unit cost cannot be saved because a valid effective date was not entered.");
                context.update("invalidFieldDialogForm");
                context.execute("invalidFieldDialog.show();");
                return;
            }

            // save job to database and check for errors
            em.getTransaction().begin();

            Long id = BusinessEntityUtils.saveBusinessEntity(em, currentUnitCost);
            if (id == null) {
                context.execute("undefinedErrorDialog.show();");
                sendErrorEmail("An error occured while saving this unit cost",
                        "Unit cost save error occured");
                return;
            }

            em.getTransaction().commit();
            setDirty(false);

        } catch (Exception e) {
            context.execute("undefinedErrorDialog.show();");
            System.out.println(e);
            // send error message to developer's email
            sendErrorEmail("An exception occurred while saving a unit cost!",
                    "\nJMTS User: " + getUser().getUsername()
                    + "\nDate/time: " + new Date()
                    + "\nException detail: " + e);
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

    public Boolean prepareAndSaveCurrentJob(EntityManager em) {

        Date now = new Date();
        JobSequenceNumber nextJobSequenceNumber = null;
        boolean jobEmailAlertsActivated = Boolean.parseBoolean(SystemOption.
                findSystemOptionByName(em,
                        "jobEmailAlertsActivated").getOptionValue());

        try {
            // Get employee for later use
            Employee employee = Employee.findEmployeeById(em, getUser().getEmployee().getId());

            // Use the client's default billing address and main contact if the
            // Job's billing address and contact are not valid.
            // Validate and save address if required
            if (!BusinessEntityUtils.validateName(getCurrentJob().getBillingAddress().getAddressLine1())) {
                getCurrentJob().setBillingAddress(getCurrentJob().getClient().getBillingAddress());
            }
            if (getCurrentJob().getBillingAddress().getId() == null) {
                getCurrentJob().getBillingAddress().save(em);
            }

            // Validate and save contact if required
            if (!BusinessEntityUtils.validateName(getCurrentJob().getContact().getName())) {
                getCurrentJob().setContact(getCurrentJob().getClient().getMainContact());
            }
            if (getCurrentJob().getContact().getId() == null) {
                getCurrentJob().getContact().save(em);
            }

            // Do not save changed job if it's already marked as completed in the database
            if (getCurrentJob().getId() != null) {
                Job job = Job.findJobById(em, getCurrentJob().getId());
                if (job.getJobStatusAndTracking().getWorkProgress().equals("Completed")
                        && !getUser().getEmployee().isMemberOf(getDepartmentBySystemOptionDeptId("invoicingDepartmentId"))
                        && !getUser().getPrivilege().getCanBeJMTSAdministrator()
                        && !isUserDepartmentSupervisor(getCurrentJob())) {
                    setDirty(false);
                    doJobSearch(searchManager.getCurrentSearchParameters());
                    addMessage("Job Cannot Be Saved",
                            "This job is marked as completed so changes cannot be saved. You may contact your department's supervisor or a system administrator.",
                            FacesMessage.SEVERITY_ERROR);

                    return false;
                }
            }

            em.getTransaction().begin();

            // Set date entered
            if (currentJob.getJobStatusAndTracking().getDateAndTimeEntered() == null) {
                currentJob.getJobStatusAndTracking().setDateAndTimeEntered(now);
            }

            if (employee != null) {
                if (currentJob.getJobStatusAndTracking().getEnteredBy().getId() == null) {
                    // This means this this is a new job so set user and person who entered the job
                    currentJob.getJobStatusAndTracking().setEnteredBy(employee);
                    currentJob.getJobStatusAndTracking().setEditedBy(employee);
                }
            }

            // Update re the person who last edited/entered the job etc.
            if (isDirty()) {
                currentJob.getJobStatusAndTracking().setDateStatusEdited(now);
                currentJob.getJobStatusAndTracking().setEditedBy(employee);
            }

            // Modify job number with sequence number if required
            if (currentJob.getAutoGenerateJobNumber()) {
                if ((currentJob.getId() == null) && (currentJob.getJobSequenceNumber() == null)) {
                    nextJobSequenceNumber = JobSequenceNumber.findNextJobSequenceNumber(em, currentJob.getYearReceived());
                    currentJob.setJobSequenceNumber(nextJobSequenceNumber.getSequentialNumber());
                    currentJob.setJobNumber(Job.getJobNumber(currentJob, em));
                } else {
                    currentJob.setJobNumber(Job.getJobNumber(currentJob, em));
                }
            }

            // Update the sampledby field of the job samples
            if (currentJob.getJobSamples().size() > 0) {
                for (JobSample jobSample : currentJob.getJobSamples()) {

                    if (jobSample.getSampledBy().getId() != null) {
                        Employee e = Employee.findEmployeeByName(em, jobSample.getSampledBy().getName());
                        if (e != null) {
                            jobSample.setSampledBy(e);
                        }
                    }

                    /// Save newly entered samples 
                    if (jobSample.getId() == null) {
                        BusinessEntityUtils.saveBusinessEntity(em, jobSample);
                    }
                }
            } else {
                // NB: This ensures that the job has at least one sample.
                // This may be removed in the future as it may not be required or
                // be necessary
                createNewJobSample(null);
                selectedJobSample.setDescription("--");
                BusinessEntityUtils.saveBusinessEntity(em, selectedJobSample);
            }

            // Do actual save here and check for errors
            Long id = BusinessEntityUtils.saveBusinessEntity(em, currentJob);

            if (id == null) {
                if (currentJob.getAutoGenerateJobNumber()) {
                    currentJob.setJobNumber(Job.getJobNumber(currentJob, em));
                }

                addMessage("Job save error occured",
                        "An error occured while saving job (Null ID)" + currentJob.getJobNumber(),
                        FacesMessage.SEVERITY_ERROR);

                if (jobEmailAlertsActivated) {
                    sendErrorEmail("An error occured while saving job (Null ID)" + currentJob.getJobNumber(),
                            "Job save error occured");
                }

                return false;

            } else if (id == 0L) {
                if (currentJob.getAutoGenerateJobNumber()) {
                    currentJob.setJobNumber(Job.getJobNumber(currentJob, em));
                }
                addMessage("Job save error occured",
                        "An error occured while saving job (0L ID)" + currentJob.getJobNumber(),
                        FacesMessage.SEVERITY_ERROR);
                if (jobEmailAlertsActivated) {
                    sendErrorEmail("An error occured while saving job (0L ID)" + currentJob.getJobNumber(),
                            "Job save error occured");
                }

                return false;
            } else {
                // Job was saved so save id for furture use
                currentJobId = id;

                // Save job sequence number
                if (nextJobSequenceNumber != null) {
                    BusinessEntityUtils.saveBusinessEntity(em, nextJobSequenceNumber);
                }

                // Send job email alerts if the option is activated
                try {
                    // Send email alerts if any was flagged to be sent                
                    if (jobEmailAlertsActivated) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println("Generating and sending emails...");
                                generateEmailAlerts();
                            }
                        }).start();

                    } else {
                        System.out.println("Email alerts will not be generated!");
                    }
                } catch (Exception e) {
                    System.out.println(e);
                    addMessage("Email Error!", "An error occurred while attempting to send an alert email.", FacesMessage.SEVERITY_ERROR);
                }

                addMessage("Success!", "This job was saved.", FacesMessage.SEVERITY_INFO);

            }

            em.getTransaction().commit();

            currentJob.clean();

        } catch (Exception e) {
            if (currentJob.getAutoGenerateJobNumber()) {
                currentJob.setJobNumber(Job.getJobNumber(currentJob, em));
            }
            addMessage("Undefined Error!", "An undefined error occurred while saving this job. "
                    + "Please contact the System Administrator", FacesMessage.SEVERITY_ERROR);

            if (jobEmailAlertsActivated) {
                try {
                    sendErrorEmail("An exception occurred while saving a job!",
                            "Job number: " + currentJob.getJobNumber()
                            + "\nJMTS User: " + getUser().getUsername()
                            + "\nDate/time: " + new Date()
                            + "\nException detail: " + e);
                } catch (Exception e2) {
                    addMessage("Email Error!", "An error occurred while attempting to send an alert email.", FacesMessage.SEVERITY_ERROR);
                    System.out.println(e2);
                }

            }

            System.out.println(e);

            return false;
        }

        return true;

    }

    public String getCompletedJobCostingEmailMessage(Job job) {
        String message = "";
        DateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");

        message = message + "Dear Colleague,<br><br>";
        message = message + "The costing for a job with the following details was completed via the <a href='http://boshrmapp:8080/jmts'>Job Management & Tracking System (JMTS)</a>:<br><br>";
        message = message + "<span style='font-weight:bold'>Job number: </span>" + job.getJobNumber() + "<br>";
        message = message + "<span style='font-weight:bold'>Client: </span>" + job.getClient().getName() + "<br>";
        if (!job.getSubContractedDepartment().getName().equals("--")) {
            message = message + "<span style='font-weight:bold'>Department: </span>" + job.getSubContractedDepartment().getName() + "<br>";
        } else {
            message = message + "<span style='font-weight:bold'>Department: </span>" + job.getDepartment().getName() + "<br>";
        }
        message = message + "<span style='font-weight:bold'>Date submitted: </span>" + formatter.format(job.getJobStatusAndTracking().getDateSubmitted()) + "<br>";
        message = message + "<span style='font-weight:bold'>Current assignee: </span>" + BusinessEntityUtils.getPersonFullName(job.getAssignedTo(), Boolean.FALSE) + "<br>";
        message = message + "<span style='font-weight:bold'>Task/Sample descriptions: </span>" + job.getJobSampleDescriptions() + "<br><br>";
        message = message + "As the department's supervisor/head, you are required to review and approve this costing. An email will be automatically sent to the Finance department after approval.<br><br>";
        message = message + "If this job was incorrectly assigned to your department, the department supervisor should contact the person who entered/assigned the job.<br><br>";
        message = message + "This email was automatically generated and sent by the <a href='http://boshrmapp:8080/jmts'>JMTS</a>. Please DO NOT reply.<br><br>";
        message = message + "Signed<br>";
        message = message + "Job Manager";

        return message;
    }

    public String getApprovedJobCostingEmailMessage(Job job) {
        String message = "";
        DateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
        EntityManager em = getEntityManager1();

        message = message + "Dear Colleague,<br><br>";
        message = message + "The costing for a job with the following details was approved via the <a href='http://boshrmapp:8080/jmts'>Job Management & Tracking System (JMTS)</a>:<br><br>";
        message = message + "<span style='font-weight:bold'>Job number: </span>" + job.getJobNumber() + "<br>";
        message = message + "<span style='font-weight:bold'>Client: </span>" + job.getClient().getName() + "<br>";
        if (!job.getSubContractedDepartment().getName().equals("--")) {
            message = message + "<span style='font-weight:bold'>Department: </span>" + job.getSubContractedDepartment().getName() + "<br>";
        } else {
            message = message + "<span style='font-weight:bold'>Department: </span>" + job.getDepartment().getName() + "<br>";
        }
        message = message + "<span style='font-weight:bold'>Date submitted: </span>" + formatter.format(job.getJobStatusAndTracking().getDateSubmitted()) + "<br>";
        message = message + "<span style='font-weight:bold'>Department/Unit Head: </span>" + BusinessEntityUtils.getPersonFullName(getDepartmentAssignedToJob(job, em).getHead(), false) + "<br>";
        message = message + "<span style='font-weight:bold'>Task/Sample descriptions: </span>" + job.getJobSampleDescriptions() + "<br><br>";
        message = message + "If this email was sent to you in error, please contact the department referred to above.<br><br>";
        message = message + "This email was automatically generated and sent by the <a href='http://boshrmapp:8080/jmts'>JMTS</a>. Please DO NOT reply.<br><br>";
        message = message + "Signed<br>";
        message = message + "Job Manager";

        return message;
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

    /**
     * To be applied when sample if saved
     */
    public void updateJobSampleReference() {
        // update reference while ensuring number of samples is not less than 1
        // or greater than 700 (for now but to be made system option)
        if (selectedJobSample.getSampleQuantity() != null) {
            if (selectedJobSample.getSampleQuantity() == 1) {
                selectedJobSample.setReference(BusinessEntityUtils.getAlphaCode(selectedJobSample.getReferenceIndex()));
            } else {
                selectedJobSample.setReference(BusinessEntityUtils.getAlphaCode(selectedJobSample.getReferenceIndex()) + "-"
                        + BusinessEntityUtils.getAlphaCode(selectedJobSample.getReferenceIndex()
                                + selectedJobSample.getSampleQuantity() - 1));
            }
        }
        setDirty(true);
    }

    public void updateProductQuantity() {
        setDirty(true);
    }

    public void deleteJobSample() {
        EntityManager em = getEntityManager1();

        // update number of samples
        if ((currentJob.getNumberOfSamples() - selectedJobSample.getSampleQuantity()) > 0) {
            currentJob.setNumberOfSamples(currentJob.getNumberOfSamples() - selectedJobSample.getSampleQuantity());
        } else {
            currentJob.setNumberOfSamples(0L);
        }

        List<JobSample> samples = currentJob.getJobSamples();
        int index = 0;
        for (JobSample sample : samples) {
            if (sample.getReference().equals(selectedJobSample.getReference())) {
                // removed sample record
                samples.remove(index);
                break;
            }
            ++index;
        }

        updateSampleReferences();

        if (currentJob.getAutoGenerateJobNumber()) {
            currentJob.setJobNumber(getCurrentJobNumber());
        }
        selectedJobSample = new JobSample();

        setDirty(Boolean.TRUE);
    }

    public void updateDepartments(Job job, EntityManager em) {

        if (!job.getDepartment().getName().equals("")) {
            Department department = Department.findDepartmentByName(em, job.getDepartment().getName());
            if (department != null) {
                job.setDepartment(department);
            }
        }
        // Update subcontracted department
        if (!job.getSubContractedDepartment().getName().equals("")) {
            Department subContractedDepartment = Department.findDepartmentByName(em, job.getSubContractedDepartment().getName());
            job.setSubContractedDepartment(subContractedDepartment);
        }

    }

    public void deleteCostComponent() {
        deleteCostComponentByName(selectedCostComponent.getName());
    }

    // Remove this and other code out of JobManager? Put in JobCostingAndPayment or Job?
    public void deleteCostComponentByName(String componentName) {

        List<CostComponent> components = currentJob.getJobCostingAndPayment().getAllSortedCostComponents();
        int index = 0;
        for (CostComponent costComponent : components) {
            if (costComponent.getName().equals(componentName)) {
                components.remove(index);
                setDirty(Boolean.TRUE);

                break;
            }
            ++index;
        }

        updateFinalCost();
        updateAmountDue();
    }

    public void editJobSample(ActionEvent event) {
        jobSampleDialogTabViewActiveIndex = 0;

        addJobSample = false;
    }

    public void editCashPayment(ActionEvent event) {
        System.out.println("Edit cash payment to be impl.");
    }

    public void copyJobSample() {

        addJobSample = true;
        selectedJobSample = new JobSample(selectedJobSample);
        selectedJobSample.setReferenceIndex(getCurrentNumberOfJobSamples());
        // Init sample    
        if (selectedJobSample.getSampleQuantity() == 1L) {
            selectedJobSample.setReference(BusinessEntityUtils.getAlphaCode(getCurrentNumberOfJobSamples()));
        } else {
            selectedJobSample.setReference(BusinessEntityUtils.getAlphaCode(getCurrentNumberOfJobSamples()) + "-"
                    + BusinessEntityUtils.getAlphaCode(getCurrentNumberOfJobSamples()
                            + selectedJobSample.getSampleQuantity() - 1));
        }

        jobSampleDialogTabViewActiveIndex = 0;
    }

    public void editCostComponent(ActionEvent event) {
    }

    public void createNewCashPayment(ActionEvent event) {
        addCashPayment = true;
        selectedCashPayment = new CashPayment();
    }

    public void createNewJobSample(ActionEvent event) {

        if (getCurrentJob().hasOnlyDefaultJobSample()) {
            addJobSample = false;
            selectedJobSample = getCurrentJob().getJobSamples().get(0);
            selectedJobSample.setDescription("");
        } else {
            addJobSample = true;
            selectedJobSample = new JobSample();
            // Init sample
            selectedJobSample.setJobId(currentJob.getId());
            selectedJobSample.setSampleQuantity(1L);
            selectedJobSample.setQuantity(1L);

            selectedJobSample.setReferenceIndex(getCurrentNumberOfJobSamples());

            if (selectedJobSample.getSampleQuantity() == 1L) {
                selectedJobSample.setReference(BusinessEntityUtils.getAlphaCode(selectedJobSample.getReferenceIndex()));
            } else {
                selectedJobSample.setReference(BusinessEntityUtils.getAlphaCode(selectedJobSample.getReferenceIndex()) + "-"
                        + BusinessEntityUtils.getAlphaCode(selectedJobSample.getReferenceIndex()
                                + selectedJobSample.getSampleQuantity() - 1));
            }
        }

        selectedJobSample.setDateSampled(new Date());
        jobSampleDialogTabViewActiveIndex = 0;
    }

    public void createNewCostComponent(ActionEvent event) {
        // init sample
        addCostComponent = true;
        selectedCostComponent = new CostComponent();
    }

    public void cancelCashPaymentEdit() {

    }

    public void cancelJobSampleEdit() {
        addJobSample = false;
        //find and revert the edit of job sample
        for (JobSample jobSample : currentJob.getJobSamples()) {
            if (jobSample.getReference().equals(selectedJobSample.getReference())) {
                jobSample.setId(backupSelectedJobSample.getId());
                jobSample.setJobId(backupSelectedJobSample.getJobId());
                jobSample.setReference(backupSelectedJobSample.getReference());
                jobSample.setReferenceIndex(backupSelectedJobSample.getReferenceIndex());
                jobSample.setSampleQuantity(backupSelectedJobSample.getSampleQuantity());
                jobSample.setQuantity(backupSelectedJobSample.getQuantity());
                jobSample.setUnitOfMeasure(backupSelectedJobSample.getUnitOfMeasure());
                jobSample.setDescription(backupSelectedJobSample.getDescription());
                jobSample.setDateReceived(backupSelectedJobSample.getDateReceived());
                jobSample.setMethodOfDisposal(backupSelectedJobSample.getMethodOfDisposal());
                backupSelectedJobSample = null;
                setDirty(false);
                return;
            }
        }
        // at this point the edited sample was not found in the current list of samples
        selectedJobSample = new JobSample();
        backupSelectedJobSample = null;
        setDirty(false);
    }

    private Long getCurrentNumberOfJobSamples() {
        if (currentJob.getNumberOfSamples() == null) {
            currentJob.setNumberOfSamples(0L);
            return currentJob.getNumberOfSamples();
        } else {
            return currentJob.getNumberOfSamples();
        }
    }

    /**
     * Checks maximum allowed samples and groups. Currently not used
     */
    public void checkNumberOfJobSamplesAndGroups() {
        EntityManager em = getEntityManager1();

        // setup context for client response
        RequestContext context = RequestContext.getCurrentInstance();
        // check for max sample
        int maxSamples = Integer.parseInt(SystemOption.findSystemOptionByName(em, "maximumJobSamples").getOptionValue());
        if (getCurrentNumberOfJobSamples() == maxSamples) {
            context.addCallbackParam("maxJobSamplesReached", true);
        }
        // check for max sample groups
        int maxGropus = Integer.parseInt(SystemOption.findSystemOptionByName(em, "maximumJobSampleGroups").getOptionValue());
        if (currentJob.getJobSamples().size() == maxGropus) {
            context.addCallbackParam("maxJobSampleGroupsReached", true);
        }
    }

    private void setNumberOfSamples() {
        currentJob.setNumberOfSamples(0L);
        for (int i = 0; i < currentJob.getJobSamples().size(); i++) { // find total
            if (currentJob.getJobSamples().get(i).getSampleQuantity() == null) {
                currentJob.getJobSamples().get(i).setSampleQuantity(1L);
            }
            currentJob.setNumberOfSamples(currentJob.getNumberOfSamples()
                    + currentJob.getJobSamples().get(i).getSampleQuantity());
        }
    }

    private void updateSampleReferences() {
        Long refIndex = 0L;

        ArrayList<JobSample> samplesCopy = new ArrayList<>(currentJob.getJobSamples());
        currentJob.getJobSamples().clear();

        for (JobSample jobSample : samplesCopy) {

            jobSample.setReferenceIndex(refIndex);
            if (jobSample.getSampleQuantity() == 1) {
                jobSample.setReference(BusinessEntityUtils.getAlphaCode(refIndex));
            } else {
                jobSample.setReference(BusinessEntityUtils.getAlphaCode(refIndex) + "-"
                        + BusinessEntityUtils.getAlphaCode(refIndex + jobSample.getSampleQuantity() - 1));
            }

            currentJob.getJobSamples().add(jobSample);

            refIndex = refIndex + jobSample.getSampleQuantity();

        }
    }

    public void updateCostingComponent() {
        if (addCostComponent) {
            addCostComponent = false;
            currentJob.getJobCostingAndPayment().getCostComponents().add(selectedCostComponent);
        }
        updateFinalCost();
        updateAmountDue();
    }

    public void updateFinalCost() {
        currentJob.getJobCostingAndPayment().setFinalCost(currentJob.getJobCostingAndPayment().getTotalJobCostingsAmount());
        setDirty(true);
    }

    public void updateAmountDue() {
        currentJob.getJobCostingAndPayment().setAmountDue(currentJob.getJobCostingAndPayment().calculateAmountDue());
        setDirty(true);
    }

    public Boolean getCanApplyGCT() {
        return JobCostingAndPayment.getCanApplyGCT(getCurrentJob());
    }

    public void editJob() {
        RequestContext context = RequestContext.getCurrentInstance();

        mainTabView.renderTab(getEntityManager1(), "jobDetailTab", true);
        context.execute("jobFormTabVar.select(0);");
    }

    public void editJobCosting() {
        // Nothing to do yet
        System.out.println("Editing job costing...");
    }

    public void updateJobCosting() {

        EntityManager em = getEntityManager1();

        Department dept = getDepartmentAssignedToJob(currentJob, em);
        if (dept != null) {
            if (getDepartmentAssignedToJob(currentJob, em).getJobCostingType().equals("Sample-based")) {
                createSampleBasedJobCostings(currentJob.getJobCostingAndPayment());
            } else {
                createDefaultJobCostings(currentJob.getJobCostingAndPayment());
            }

            // Add sub-contract oostings if any
            List<Job> jobs = Job.findJobsByYearReceivedAndJobSequenceNumber(em,
                    currentJob.getYearReceived(),
                    currentJob.getJobSequenceNumber());
            if (jobs != null) {
                for (Job job : jobs) {
                    if (job.getIsSubContracted() && !currentJob.getIsSubContracted()
                            && (job.getJobStatusAndTracking().getWorkProgress().equals("Completed"))) {
                        String ccName = "Subcontract to " + job.getSubContractedDepartment().getName() + " (" + job.getJobNumber() + ")";
                        // Check that this cost component does not already exist.
                        // The assumption is that only one component will be found if any
                        if (!CostComponent.findCostComponentsByName(ccName,
                                currentJob.getJobCostingAndPayment().getCostComponents()).isEmpty()) {
                            deleteCostComponentByName(ccName);
                        }
                        CostComponent cc
                                = new CostComponent(
                                        ccName,
                                        job.getJobCostingAndPayment().getFinalCost(),
                                        true, false);

                        currentJob.getJobCostingAndPayment().getCostComponents().add(cc);
                        setDirty(true);
                    }
                }
            }
        }
        // NB: Ensure that amount due is recalc. in case something affects
        // taxes was changed
        currentJob.getJobCostingAndPayment().calculateAmountDue();
    }

    public void okCashPayment() {
        // tk
        // update jobCostingAndPayment.receiptNumber...append receipt number.
        // exec. jobManager.updateTotalDeposit(), jobManager.updateAmountDue() is exec by jobManager.updateTotalDeposit()?
        System.out.println("Ok cashpayment");

    }

    public void okJobSample() {
        RequestContext context = RequestContext.getCurrentInstance();
        EntityManager em = getEntityManager1();

        // get sampled by
        selectedJobSample.setSampledBy(Employee.findEmployeeByName(em, selectedJobSample.getSampledBy().getName()));

        // validate form entries
        if (selectedJobSample.getDateSampled() == null) {
            context.execute("jobSampleDialog.show();");
            context.execute("jobSampleRequiredFieldMessageDialog.show();");
            return;
        } else if (selectedJobSample.getSampleQuantity() == null) {
            context.execute("jobSampleDialog.show();");
            context.execute("jobSampleRequiredFieldMessageDialog.show();");
            return;
        } else if (selectedJobSample.getName().trim().isEmpty()) {
            context.execute("jobSampleDialog.show();");
            context.execute("jobSampleRequiredFieldMessageDialog.show();");
            return;
        } else if (selectedJobSample.getQuantity() == null) {
            context.execute("jobSampleDialog.show();");
            context.execute("jobSampleRequiredFieldMessageDialog.show();");
            return;
        } else {
            context.execute("jobSampleDialog.hide();");
        }

        updateJobSampleReference();
        updateProductQuantity();
        if (addJobSample) {
            currentJob.getJobSamples().add(selectedJobSample);
            addJobSample = false;
        }
        setNumberOfSamples();

        updateSampleReferences();

        // Update department
        if (!currentJob.getDepartment().getName().equals("")) {
            Department department = Department.findDepartmentByName(em, currentJob.getDepartment().getName());
            if (department != null) {
                currentJob.setDepartment(department);
            }
        }
        // Update subcontracted department
        if (!currentJob.getSubContractedDepartment().getName().equals("")) {
            Department subContractedDepartment = Department.findDepartmentByName(em, currentJob.getSubContractedDepartment().getName());
            currentJob.setSubContractedDepartment(subContractedDepartment);
        }

        if (currentJob.getAutoGenerateJobNumber()) {
            currentJob.setJobNumber(getCurrentJobNumber());
        }
        jobSampleDialogTabViewActiveIndex = 0;

    }

    public JobSample getSelectedJobSample() {
        return selectedJobSample;
    }

    public void setSelectedJobSample(JobSample selectedJobSample) {
        this.selectedJobSample = selectedJobSample;
        if (selectedJobSample != null) {
            backupSelectedJobSample = new JobSample();
            backupSelectedJobSample.setId(selectedJobSample.getId());
            backupSelectedJobSample.setJobId(selectedJobSample.getJobId());
            backupSelectedJobSample.setReference(selectedJobSample.getReference());
            backupSelectedJobSample.setReferenceIndex(selectedJobSample.getReferenceIndex());
            backupSelectedJobSample.setSampleQuantity(selectedJobSample.getSampleQuantity());
            backupSelectedJobSample.setQuantity(selectedJobSample.getQuantity());
            backupSelectedJobSample.setUnitOfMeasure(selectedJobSample.getUnitOfMeasure());
            backupSelectedJobSample.setDescription(selectedJobSample.getDescription());
            backupSelectedJobSample.setDateReceived(selectedJobSample.getDateReceived());
            backupSelectedJobSample.setMethodOfDisposal(selectedJobSample.getMethodOfDisposal());
        }
    }

    public String getJobAssignee() {
        if (currentJob.getAssignedTo() != null) {
            return currentJob.getAssignedTo().getLastName() + ", " + currentJob.getAssignedTo().getFirstName();
        } else {
            return "";
        }
    }

    public void loadJob() {

        EntityManager em = getEntityManager1();
        RequestContext context = RequestContext.getCurrentInstance();

        // prompt to save modified job before attempting to create new job
        if (isDirty()) {
            context.addCallbackParam("jobDirty", true);
            return;
        }

        if ((selectedJobId != 0L) && (selectedJobId != null)) {
            currentJob = Job.findJobById(em, selectedJobId);
            // create job costings if it does not exist
            currentJobId = currentJob.getId();
            // set accpac custmomer name for later use
            accPacCustomer = new AccPacCustomer();
            accPacCustomer.setCustomerName(currentJob.getClient().getName());
            // update status if we are using accpac customer list and database
            if (useAccPacCustomerList) {
                updateCreditStatusSearch();
            }
        } else {
            createJob(em, false);
        }

        setDirty(false);

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

    public Date getJobCostingDate() {
        return currentJob.getJobStatusAndTracking().getCostingDate();
    }

    public void setJobCostingDate(Date date) {
        currentJob.getJobStatusAndTracking().setCostingDate(date);
    }

    public void handleJobCostingDateSelect(SelectEvent event) {
        Date selectedDate = (Date) event.getObject();

        setJobCostingDate(selectedDate);

        setDirty(true);
    }

    public Date getJobSampleReceivalDate() {
        if (selectedJobSample != null) {
            if (selectedJobSample.getDateReceived() != null) {
                return selectedJobSample.getDateReceived();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public void setJobSampleReceivalDate(Date date) {
        selectedJobSample.setDateReceived(date);
    }

    public Long getSelectedJobId() {

        return selectedJobId;
    }

    public void setSelectedJobId(Long selectedJobId) {
        this.selectedJobId = selectedJobId;
    }

    public void handleDateSubmittedSelect() {

        EntityManager em = getEntityManager1();

        if (currentJob.getAutoGenerateJobNumber()) {
            currentJob.setJobNumber(Job.getJobNumber(currentJob, getEntityManager1()));
        }

        JobCostingAndPayment.setJobCostingTaxes(em, currentJob);
        if (currentJob.getId() != null) {
            updateAllJobCostings();
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

    public List<JobCostingAndPayment> completeJobCostingAndPaymentName(String query) {
        EntityManager em = null;

        try {
            em = getEntityManager1();

            List<JobCostingAndPayment> results = JobCostingAndPayment.findAllJobCostingAndPaymentsByDepartmentAndName(em,
                    getUser().getEmployee().getDepartment().getName(), query);

            return results;

        } catch (Exception e) {
            System.out.println(e);

            return new ArrayList<>();
        }
    }

    public List<BusinessOffice> completeBusinessOffice(String query) {
        EntityManager em = null;

        try {
            em = getEntityManager1();

            List<BusinessOffice> offices = BusinessOffice.findActiveBusinessOfficesByName(em, query);

            return offices;
        } catch (Exception e) {

            System.out.println(e);
            return new ArrayList<>();
        }
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

    public ArrayList<String> completeCountry(String query) {
        EntityManager em = null;

        try {
            em = getEntityManager1();

            ArrayList<Country> countries = new ArrayList<>(Country.findCountriesByName(em, query));
            ArrayList<String> countriesList = (ArrayList<String>) (ArrayList<?>) countries;

            return countriesList;
        } catch (Exception e) {
            System.out.println(e);
            return new ArrayList<>();
        }
    }

    public List<Classification> completeClassification(String query) {
        EntityManager em = null;

        try {
            em = getEntityManager1();

            List<Classification> classifications = Classification.findActiveClassificationsByName(em, query);

            return classifications;
        } catch (Exception e) {

            System.out.println(e);
            return new ArrayList<>();
        }
    }

    public List<Department> completeDepartment(String query) {
        EntityManager em = null;

        try {
            em = getEntityManager1();

            List<Department> departments = Department.findActiveDepartmentsByName(em, query);

            return departments;

        } catch (Exception e) {
            System.out.println(e + ": completeDepartment");
            return new ArrayList<>();
        }
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

    public List<DepartmentUnit> completeDepartmentUnit(String query) {
        EntityManager em = null;

        try {
            em = getEntityManager1();

            List<DepartmentUnit> departmentUnits = DepartmentUnit.findDepartmentUnitsByName(em, query);

            return departmentUnits;

        } catch (Exception e) {
            System.out.println(e);

            return new ArrayList<>();
        }
    }

    public List<Laboratory> completeLaboratory(String query) {
        EntityManager em = null;

        try {
            em = getEntityManager1();

            List<Laboratory> laboratories = Laboratory.findLaboratoriesByName(em, query);

            return laboratories;

        } catch (Exception e) {
            System.out.println(e);

            return new ArrayList<>();
        }
    }

    public List<Employee> completeEmployee(String query) {
        EntityManager em = null;

        try {

            em = getEntityManager1();
            List<Employee> employees = Employee.findActiveEmployeesByName(em, query);

            if (employees != null) {
                return employees;
            } else {
                return new ArrayList<>();
            }
        } catch (Exception e) {
            System.out.println(e);
            return new ArrayList<>();
        }
    }

    public List<String> completeAccPacClientName(String query) {
        EntityManager em2 = null;

        try {

            em2 = getEntityManager2();
            List<AccPacCustomer> clients = AccPacCustomer.findAccPacCustomersByName(em2, query);
            List<String> suggestions = new ArrayList<>();

            for (AccPacCustomer client : clients) {
                suggestions.add(client.getCustomerName());
            }

            return suggestions;
        } catch (Exception e) {

            System.out.println(e);

            return new ArrayList<>();
        }
    }

    public List<AccPacCustomer> completeAccPacClient(String query) {
        EntityManager em2 = null;

        try {
            em2 = getEntityManager2();

            return AccPacCustomer.findAccPacCustomersByName(em2, query);
        } catch (Exception e) {

            System.out.println(e);
            return new ArrayList<>();
        }
    }

    /**
     * This method is used for test purposes. The content of which is to be
     * commented out when not in use. This is code is typically run after doing
     * a job search.
     *
     * @param em
     */
    public void runTestCode(EntityManager em) {
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
            if (jobSearchResultList.isEmpty()) { // Do search with sampple search enabled
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

        // Run test code after completing search.
        // This is used to do bactch updates or just test out new code.
        // NB: The code in this method should be commented out or deleted 
        // when no longer needed.
        runTestCode(em);

        // Reset select selected job id to allow new selection
        selectedJobId = 0L;
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

    /**
     * NB: query parameter currently not used to filter sectors.
     *
     * @param query
     * @return
     */
    public List<Sector> completeActiveSectors(String query) {
        EntityManager em = getEntityManager1();

        List<Sector> sectors = Sector.findAllActiveSectors(em);

        return sectors;
    }

    public List<Address> getClientAddresses() {
        EntityManager em = getEntityManager1();

        List<Address> addresses = getCurrentJob().getClient().getAddresses();

        return addresses;
    }

    /**
     * NB: query not used to filter
     *
     * @param query
     * @return
     */
    public List<JobCategory> completeActiveJobCategories(String query) {
        EntityManager em = getEntityManager1();

        List<JobCategory> categories = JobCategory.findAllActiveJobCategories(em);

        return categories;
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

    public List<JobSubCategory> completeActiveJobSubCategories(String query) {
        EntityManager em = getEntityManager1();

        List<JobSubCategory> subCategories = JobSubCategory.findAllActiveJobSubCategories(em);

        return subCategories;
    }

    public List<CostCode> getAllCostCodes() {
        EntityManager em = getEntityManager1();

        List<CostCode> codes = CostCode.findAllCostCodes(em);

        return codes;
    }

    private EntityManagerFactory getEMF1() {
        return EMF1;
    }

    private EntityManagerFactory getEMF2() {
        return EMF2;
    }

    public List<Job> getJobSearchResultList() {
        if (jobSearchResultList == null) {
            jobSearchResultList = new ArrayList<>();
        }
        return jobSearchResultList;
    }

    public int getNumberOfJobsFound() {
        if (jobSearchResultList != null) {
            return jobSearchResultList.size();
        }

        return 0;
    }

    public Boolean getRenderJobSearchResultsList() {
        if (jobSearchResultList.isEmpty()) {
            return false;
        } else {
            return true;
        }
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

    @Override
    public void setDirty(Boolean dirty) {
        getCurrentJob().setIsDirty(dirty);
    }

    @Override
    public Boolean isDirty() {
        return getCurrentJob().getIsDirty();
    }

    public void handlePoll() {
        System.out.println("Handling poll: " + new Date());
    }

    public String getSystemInfo() {
        String clientName;
        String dirtyStatus;

        EntityManager em = null;

        try {
            if (getUser() != null) {
                em = getEntityManager1();

                if (currentJob == null) {
                    currentJob = Job.create(em, getUser(), true);
                    currentJobId = currentJob.getId();
                }
                // get client name
                if (currentJob.getClient() == null) {
                    clientName = "";
                } else {
                    clientName = " (" + currentJob.getClient().getName() + ")";
                }
                // get dirty status of job
                if (isDirty()) {
                    dirtyStatus = " - MODIFIED";
                } else {
                    dirtyStatus = "";
                }

                // build and return system info string
                return "Current job: " + currentJob.getJobNumber() + clientName + dirtyStatus;
            } else {
                return "";
            }
        } catch (Exception e) {

            System.out.println(e);
        }

        return "";
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
                updateAllJobCostings();
            }

        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public void updateCurrentUnitCostDepartment() {
        EntityManager em = null;

        try {
            em = getEntityManager1();
            if (currentUnitCost.getDepartment().getName() != null) {
                Department department = Department.findDepartmentByName(em, currentUnitCost.getDepartment().getName());
                if (department != null) {
                    currentUnitCost.setDepartment(department);
                    setDirty(true);
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public void updateCurrentUnitCostDepartmentUnit() {
        EntityManager em = null;

        try {
            em = getEntityManager1();
            if (currentUnitCost.getDepartmentUnit().getName() != null) {
                DepartmentUnit departmentUnit = DepartmentUnit.findDepartmentUnitByName(em, currentUnitCost.getDepartmentUnit().getName());
                if (departmentUnit != null) {
                    currentUnitCost.setDepartmentUnit(departmentUnit);
                    setDirty(true);
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public void updateCurrentUnitCostDepartmentLab() {
        EntityManager em = null;

        try {
            em = getEntityManager1();
            if (currentUnitCost.getLaboratory().getName() != null) {
                Laboratory laboratory = Laboratory.findLaboratoryByName(em, currentUnitCost.getLaboratory().getName());

                if (laboratory != null) {
                    currentUnitCost.setLaboratory(laboratory);
                    setDirty(true);
                }
            }

        } catch (Exception e) {
            System.out.println(e);
        }

    }

    public void updateUnitCostDepartment() {
        EntityManager em = null;

        try {
            em = getEntityManager1();
            if (unitCostDepartment.getName() != null) {
                Department department = Department.findDepartmentByName(em, unitCostDepartment.getName());
                if (department != null) {
                    unitCostDepartment = department;
                    doUnitCostSearch();
                }
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void updateJobCostDepartment() {
        EntityManager em = null;

        try {
            em = getEntityManager1();
            if (jobCostDepartment.getName() != null) {
                Department department = Department.findDepartmentByName(em, jobCostDepartment.getName());
                if (department != null) {
                    jobCostDepartment = department;
                    doUnitCostSearch();
                }
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
                updateAllJobCostings();
            }

        } catch (Exception e) {
            System.out.println(e + ": updateSubContractedDepartment");
        }
    }

    public void updateAccPacClient() {

        setShowPrepayments(false);

        filteredAccPacCustomerDocuments = new ArrayList<>();

        try {
            if (currentJob != null) {
                if (!currentJob.getClient().getName().equals("")) {
                    accPacCustomer.setCustomerName(currentJob.getClient().getName());
                } else {
                    accPacCustomer.setCustomerName("?");
                }
                updateCreditStatus(null);
            }
        } catch (Exception e) {
            System.out.println(e);
        }

    }

    /**
     * Do update for the client field on the General tab on the Job Details form
     */
    public void updateJobEntryTabClient() {

        accPacCustomer.setCustomerName(currentJob.getClient().getName());
        if (useAccPacCustomerList) {
            updateCreditStatus(null);
        }

        currentJob.setBillingAddress(currentJob.getClient().getBillingAddress());
        currentJob.setContact(currentJob.getClient().getMainContact());

        setDirty(true);
    }

    public void updateAccPacCustomer(SelectEvent event) {
        if (accPacCustomer != null) {
            try {
                EntityManager em = getEntityManager2();

                accPacCustomer = AccPacCustomer.findByName(em, accPacCustomer.getCustomerName());
                if (accPacCustomer == null) {
                    accPacCustomer = new AccPacCustomer();
                    accPacCustomer.setCustomerName("");
                }

                // set the found client name to the present job client
                if (accPacCustomer.getCustomerName() != null) {
                    updateCreditStatus(null);
                }
            } catch (Exception e) {
                System.out.println(e);
                accPacCustomer = new AccPacCustomer();
                accPacCustomer.setCustomerName("");
            }
        }
    }

    public void updateClientInformation() {
    }

    public Integer getNumberOfFilteredAccPacCustomerDocuments() {
        if (filteredAccPacCustomerDocuments != null) {
            return filteredAccPacCustomerDocuments.size();
        }

        return 0;
    }

    public Boolean getFilteredDocumentAvailable() {
        if (filteredAccPacCustomerDocuments != null) {
            if (filteredAccPacCustomerDocuments.isEmpty()) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    public void updateCreditStatusSearch() {
        accPacCustomer.setCustomerName(currentJob.getClient().getName());
        updateCreditStatus(null);
    }

    public void updateCreditStatus(SelectEvent event) {
        EntityManager em = getEntityManager2();

        accPacCustomer = AccPacCustomer.findByName(em, accPacCustomer.getCustomerName().trim());

        if (accPacCustomer != null) {
            if (getShowPrepayments()) {
                filteredAccPacCustomerDocuments = AccPacDocument.findAccPacInvoicesDueByCustomerId(em, accPacCustomer.getId(), true);
            } else {
                filteredAccPacCustomerDocuments = AccPacDocument.findAccPacInvoicesDueByCustomerId(em, accPacCustomer.getId(), false);
            }
        } else {
            createNewAccPacCustomer();
        }
    }

    public void updateCreditStatus() {
        updateCreditStatus(null);
    }

    public void createNewAccPacCustomer() {
        if (accPacCustomer != null) {
            accPacCustomer = new AccPacCustomer(accPacCustomer.getCustomerName());
        } else {
            accPacCustomer = new AccPacCustomer(null);
        }
        accPacCustomer.setId(null);
        filteredAccPacCustomerDocuments = new ArrayList<>();
    }

    public String getAccPacCustomerID() {
        if (accPacCustomer.getId() == null) {
            return "";
        } else {
            return accPacCustomer.getId();
        }
    }

    public String getAccPacCustomerName() {
        if (accPacCustomer.getCustomerName() == null) {
            return "{Not found}";
        } else {
            return accPacCustomer.getCustomerName();
        }
    }

    public String getCustomerType() {
        if (accPacCustomer.getIDACCTSET().equals("TRADE")) {
            return "CREDIT";
        } else {
            return "REGULAR";
        }
    }

    public AccPacCustomer getAccPacCustomer() {
        return accPacCustomer;
    }

    public void setAccPacCustomer(AccPacCustomer accPacCustomer) {
        this.accPacCustomer = accPacCustomer;
    }

    public void updateAssignee() {
        setDirty(true);
    }

    public void updateCostingComponents() {
        if (selectedJobCostingTemplate != null) {
            EntityManager em = getEntityManager1();
            JobCostingAndPayment jcp
                    = JobCostingAndPayment.findJobCostingAndPaymentByDepartmentAndName(em,
                            getUser().getEmployee().getDepartment().getName(),
                            selectedJobCostingTemplate);
            if (jcp != null) {
                currentJob.getJobCostingAndPayment().getCostComponents().clear();
                currentJob.getJobCostingAndPayment().setCostComponents(copyCostComponents(jcp.getCostComponents()));
                currentJob.getJobCostingAndPayment().calculateAmountDue();
                setDirty(true);
            } else {
                // Nothing yet
            }

            selectedJobCostingTemplate = null;

        }
    }

    public void removeCurrentJobCostingComponents(EntityManager em) {

        if (!getCurrentJob().getJobCostingAndPayment().getCostComponents().isEmpty()) {
            em.getTransaction().begin();
            for (Iterator<CostComponent> iter = getCurrentJob().getJobCostingAndPayment().getCostComponents().iterator(); iter.hasNext();) {
                CostComponent costComponent = iter.next();

                if (costComponent.getId() != null) {
                    costComponent = em.find(CostComponent.class, costComponent.getId());
                    em.remove(costComponent);
                }
            }

            getCurrentJob().getJobCostingAndPayment().getCostComponents().clear();
            BusinessEntityUtils.saveBusinessEntity(em, getCurrentJob().getJobCostingAndPayment());
            em.getTransaction().commit();
        }

    }

    public void updateSampledBy() {
        EntityManager em = getEntityManager1();
        selectedJobSample.setSampledBy(Employee.findEmployeeByName(em, selectedJobSample.getSampledBy().getName()));
    }

    public void updateReceivedBy() {

        EntityManager em = null;
        try {
            em = getEntityManager1();

            if (receivedById != null) {
                Employee receivedBy = Employee.findEmployeeById(em, receivedById);
                if (receivedBy != null) {
                    selectedJobSample.setReceivedBy(receivedBy);
                    setDirty(true);
                } else {
                    selectedJobSample.setReceivedBy(Employee.findDefaultEmployee(em, "--", "--", true));
                }
            }

        } catch (Exception e) {
            System.out.println(e);
        }
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

    public String getLastJobNumber() {
        return "?";
    }

    public void saveManufacturer(EntityManager em, Manufacturer manufacturer) {
        if (manufacturer.getName() != null) {

            Manufacturer currentManufacturer = Manufacturer.findManufacturerByName(em, manufacturer.getName());

            if (currentManufacturer == null) {
                currentManufacturer = new Manufacturer();
                currentManufacturer.setName(manufacturer.getName());

            } else {
                manufacturer.setId(currentManufacturer.getId());
            }

            BusinessEntityUtils.saveBusinessEntity(em, manufacturer);
        }
    }

    public void createNewJobClient() {
        clientManager.createNewClient(true);
        clientManager.setUser(getUser());
        clientManager.setClientOwner(getCurrentJob());
        clientManager.setCurrentAddress(new Address());
        clientManager.setCurrentContact(new Contact());
        clientManager.setIsToBeSaved(true);
        clientManager.setIsClientNameAndIdEditable(getUser().getPrivilege().getCanAddClient());

        PrimeFacesUtils.openDialog(null, "clientDialog", true, true, true, 420, 700);
    }

    public void editJobClient() {
        clientManager.setUser(getUser());
        clientManager.setClientOwner(getCurrentJob());
        clientManager.setCurrentClient(getCurrentJob().getClient());
        clientManager.setCurrentAddress(getCurrentJob().getBillingAddress());
        clientManager.setCurrentContact(getCurrentJob().getContact());
        clientManager.setIsToBeSaved(true);
        clientManager.setIsClientNameAndIdEditable(getUser().getPrivilege().getCanAddClient());

        PrimeFacesUtils.openDialog(null, "clientDialog", true, true, true, 420, 700);
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

    public DocumentStandard createNewDocumentStandard(EntityManager em,
            JobManagerUser user) {

        DocumentStandard documentStandard = new DocumentStandard();
        documentStandard.setNumber("");
        documentStandard.setType(DocumentType.findDocumentTypeByName(em, "Standard"));
        documentStandard.setClassification(Classification.findClassificationByName(em, "--"));
        documentStandard.setEnforcement("Mandatory");

        documentStandard.setAutoGenerateNumber(Boolean.FALSE);

        return documentStandard;
    }

    /*
     * Takes a list of job costings enties an set their ids and component ids
     * to null which will result in new job costings being created when
     * the job costins are commited to the database
     */
    public List<JobCosting> copyJobCostings(List<JobCosting> srcCostings) {
        ArrayList<JobCosting> newJobCostings = new ArrayList<>();

        for (JobCosting jobCosting : srcCostings) {
            JobCosting newJobCosting = new JobCosting(jobCosting);
            for (CostComponent costComponent : jobCosting.getCostComponents()) {
                CostComponent newCostComponent = new CostComponent(costComponent);
                newJobCosting.getCostComponents().add(newCostComponent);
            }
            newJobCostings.add(newJobCosting);
        }

        return newJobCostings;
    }

    public List<CostComponent> copyCostComponents(List<CostComponent> srcCostComponents) {
        ArrayList<CostComponent> newCostComponents = new ArrayList<>();

        for (CostComponent costComponent : srcCostComponents) {
            CostComponent newCostComponent = new CostComponent(costComponent);
            newCostComponents.add(newCostComponent);
        }

        return newCostComponents;
    }

    public void createSampleBasedJobCostings(JobCostingAndPayment jobCostingAndPayment) {
        if (currentJob.getJobCostingAndPayment().getAllSortedCostComponents().isEmpty()) {
            // Add all existing samples as cost oomponents            
            for (JobSample jobSample : currentJob.getJobSamples()) {
                jobCostingAndPayment.getAllSortedCostComponents().add(new CostComponent(jobSample.getDescription()));
            }
        } else if (currentJob.getJobSamples().size() > currentJob.getJobCostingAndPayment().getAllSortedCostComponents().size()) {
        }
    }

    public void createDefaultJobCostings(JobCostingAndPayment jobCostingAndPayment) {

        if (currentJob.getJobCostingAndPayment().getCostComponents().isEmpty()) {
            jobCostingAndPayment.getCostComponents().add(new CostComponent("List of Assessments", Boolean.TRUE));
            jobCostingAndPayment.getCostComponents().add(new CostComponent(""));
        }

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

    public String getReportsLocation() {
        return "."; // to be extracted from database
    }

    public Long saveDepartmentReport(EntityManager em, DepartmentReport departmentReport) {

        try {
            if (departmentReport.getId() != null) {
                em.merge(departmentReport);
            } else {
                em.persist(departmentReport);
            }
        } catch (Exception e) {
            return null;
        }

        return departmentReport.getId();
    }

    public String getMonthShortFormat(Date date) {
        String month = "";
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        switch (c.get(Calendar.MONTH)) {
            case 0:
                month = "Jan";
                break;
            case 1:
                month = "Feb";
                break;
            case 2:
                month = "Mar";
                break;
            case 3:
                month = "Apr";
                break;
            case 4:
                month = "May";
                break;
            case 5:
                month = "Jun";
                break;
            case 6:
                month = "Jul";
                break;
            case 7:
                month = "Aug";
                break;
            case 8:
                month = "Sep";
                break;
            case 9:
                month = "Oct";
                break;
            case 10:
                month = "Nov";
                break;
            case 11:
                month = "Dec";
                break;
            default:
                month = "";
                break;
        }
        return month;
    }

    public String getYearShortFormat(Date date, int digits) {
        String yearString = "";
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        int year = c.get(Calendar.YEAR);
        yearString = yearString + year;

        // get last x digits of year
        yearString = yearString.substring(yearString.length() - digits, yearString.length());

        return yearString;
    }

    public Long saveJobSample(EntityManager em, JobSample jobSample) {
        return BusinessEntityUtils.saveBusinessEntity(em, jobSample);
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

    public Long saveCashPayment(EntityManager em, CashPayment cashPayment) {
        return BusinessEntityUtils.saveBusinessEntity(em, cashPayment);
    }

    public List<CashPayment> getCashPaymentsByJobId(EntityManager em, Long jobId) {
        try {
            List<CashPayment> cashPayments
                    = em.createQuery("SELECT c FROM CashPayment c "
                            + "WHERE c.jobId "
                            + "= '" + jobId + "'", CashPayment.class).getResultList();
            return cashPayments;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public Boolean deleteCashPayment(EntityManager em, Long Id) {
        CashPayment cashPayment;

        cashPayment = em.find(CashPayment.class, Id);
        return BusinessEntityUtils.deleteEntity(em, cashPayment);
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

    HSSFCellStyle getDefaultCellStyle(HSSFWorkbook wb) {
        HSSFCellStyle cellStyle = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setFontName("Arial");
        cellStyle.setFont(font);
        cellStyle.setFillForegroundColor(HSSFColor.WHITE.index);
        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        return cellStyle;
    }

    Font getWingdingsFont(HSSFWorkbook wb) {
        Font font = wb.createFont();
        font.setFontHeightInPoints((short) 14);
        font.setFontName("Wingdings");

        return font;
    }

    Font getFont(HSSFWorkbook wb, String fontName, short fontsize) {
        Font font = wb.createFont();
        font.setFontHeightInPoints(fontsize);
        font.setFontName(fontName);

        return font;
    }

    public FileInputStream createServiceContractExcelFileInputStream(
            EntityManager em,
            JobManagerUser user,
            Long jobId,
            String filePath) {
        try {

            Job job = Job.findJobById(em, jobId);
            job.getJobCostingAndPayment().calculateAmountDue();

            Client ativeClient = Application.getActiveClientByNameIfAvailable(em, job.getClient());

            File file = new File(filePath);

            FileInputStream inp = new FileInputStream(file);

            // Create workbook from input file
            POIFSFileSystem fileSystem = new POIFSFileSystem((FileInputStream) inp);

            HSSFWorkbook wb = new HSSFWorkbook(fileSystem);

            // Fonts
            Font defaultFont = getFont(wb, "Arial", (short) 10);
            Font samplesFont = getFont(wb, "Arial", (short) 9);
            Font samplesRefFont = getFont(wb, "Arial", (short) 9);
            samplesRefFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            Font jobNumberFont = getFont(wb, "Arial Black", (short) 14);

            // Cell style
            HSSFCellStyle dataCellStyle;

            // create temp file for output
            FileOutputStream out = new FileOutputStream("ServiceContract-" + user.getId() + ".xls");

            // get service contract sheet
            HSSFSheet serviceContractSheet = wb.getSheet("ServiceContract");
            serviceContractSheet.setActive(true);
            serviceContractSheet.setForceFormulaRecalculation(true);

            // Fill in job data
            // Job number
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderLeft((short) 2);
            dataCellStyle.setFont(jobNumberFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "A6", // A = 0 , 6 = 5
                    currentJob.getJobNumber(),
                    "java.lang.String", dataCellStyle);

            // Contracting business office       
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderLeft((short) 1);
            dataCellStyle.setBorderBottom((short) 1);
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "K6",
                    currentJob.getBusinessOffice().getName(),
                    "java.lang.String", dataCellStyle);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "K7",
                    "",
                    "java.lang.String", dataCellStyle);

            // Job entry agent:
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderBottom((short) 1);
            dataCellStyle.setBorderTop((short) 1);
            dataCellStyle.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            dataCellStyle.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "F9",
                    currentJob.getJobStatusAndTracking().getEnteredBy().getName(),
                    "java.lang.String", dataCellStyle);

            // Date agent prepared contract:   
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderBottom((short) 1);
            dataCellStyle.setBorderTop((short) 1);
            dataCellStyle.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            dataCellStyle.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "F10",
                    currentJob.getJobStatusAndTracking().getDateAndTimeEntered(),
                    "java.util.Date", dataCellStyle);

            // Department in charge of job (Parent department):   
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderBottom((short) 1);
            dataCellStyle.setBorderTop((short) 1);
            dataCellStyle.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            dataCellStyle.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "F11",
                    currentJob.getDepartment().getName(),
                    "java.lang.String", dataCellStyle);

            // Estimated turn around time:
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderBottom((short) 1);
            dataCellStyle.setBorderTop((short) 1);
            dataCellStyle.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            dataCellStyle.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "F12",
                    currentJob.getEstimatedTurnAroundTimeInDays(),
                    "java.lang.String", dataCellStyle);

            // Estimated Sub Total (J$):
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderBottom((short) 1);
            dataCellStyle.setBorderTop((short) 1);
            dataCellStyle.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            dataCellStyle.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "F13",
                    currentJob.getJobCostingAndPayment().getEstimatedCost(),
                    "Currency", dataCellStyle);

            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderBottom((short) 1);
            dataCellStyle.setBorderTop((short) 1);
            dataCellStyle.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            dataCellStyle.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "F14",
                    currentJob.getJobCostingAndPayment().getEstimatedCost()
                    * currentJob.getJobCostingAndPayment().getPercentageGCT() / 100,
                    "Currency", dataCellStyle);

            // Estimated Total Cost (J$):
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderBottom((short) 1);
            dataCellStyle.setBorderTop((short) 1);
            dataCellStyle.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            dataCellStyle.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "F15",
                    currentJob.getJobCostingAndPayment().getEstimatedCostIncludingTaxes(),
                    "Currency", dataCellStyle);

            // Minimum First Deposit (J$):
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderBottom((short) 1);
            dataCellStyle.setBorderTop((short) 1);
            dataCellStyle.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "F16",
                    currentJob.getJobCostingAndPayment().getMinDepositIncludingTaxes(),
                    "Currency", dataCellStyle);

            // RECEIPT #
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderTop((short) 1);
            dataCellStyle.setBorderBottom((short) 1);
            dataCellStyle.setBorderLeft((short) 1);
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            dataCellStyle.setWrapText(true);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "R9",
                    currentJob.getJobCostingAndPayment().getReceiptNumber(),
                    "java.lang.String", dataCellStyle);

            // TOTAL PAID (J$)
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderTop((short) 1);
            dataCellStyle.setBorderBottom((short) 1);
            dataCellStyle.setBorderLeft((short) 1);
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            dataCellStyle.setWrapText(true);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "V9",
                    currentJob.getJobCostingAndPayment().getDeposit(),
                    "Currency", dataCellStyle);

            // PAYMENT BREAKDOWN
            // Calculate GCT and Job Cost
            Double jobCost = (100.0 * currentJob.getJobCostingAndPayment().getDeposit())
                    / (currentJob.getJobCostingAndPayment().getPercentageGCT() + 100.0);
            Double jobGCT = currentJob.getJobCostingAndPayment().getDeposit() - jobCost;
            // GCT
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderTop((short) 1);
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "AC9",
                    jobGCT,
                    "Currency", dataCellStyle);
            // Job
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderBottom((short) 1);
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "AC10",
                    jobCost,
                    "Currency", dataCellStyle);

            // DATE PAID (date of last payment)
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderBottom((short) 1);
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "AH9",
                    currentJob.getJobStatusAndTracking().getDepositDate(),
                    "java.util.Date", dataCellStyle);

            // BALANCE (amount due) 
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            if (currentJob.getJobCostingAndPayment().getFinalCost() > 0.0) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, "AL9",
                        "exactly",
                        "java.lang.String", dataCellStyle);
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, "AL10",
                        currentJob.getJobCostingAndPayment().getAmountDue(),
                        "Currency", dataCellStyle);
            } else {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, "AL9",
                        "approximately",
                        "java.lang.String", dataCellStyle);
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, "AL10",
                        currentJob.getJobCostingAndPayment().getEstimatedCostIncludingTaxes()
                        - currentJob.getJobCostingAndPayment().getDeposit(),
                        "Currency", dataCellStyle);
            }

            // PAYMENT TERMS/INFORMATION
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderTop((short) 1);
            dataCellStyle.setBorderBottom((short) 1);
            dataCellStyle.setBorderLeft((short) 1);
            dataCellStyle.setBorderRight((short) 1);
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);
            if (!currentJob.getJobCostingAndPayment().getPaymentTerms().trim().equals("")) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, "R12",
                        currentJob.getJobCostingAndPayment().getPaymentTerms(),
                        "java.lang.String", dataCellStyle);
            } else {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, "R12",
                        "Not applicable",
                        "java.lang.String", dataCellStyle);
            }

            // THE INFORMATION IN SECTION 3
            // AGENT/CASHIER
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderTop((short) 1);
            dataCellStyle.setBorderBottom((short) 1);
            dataCellStyle.setBorderLeft((short) 1);
            dataCellStyle.setBorderRight((short) 1);
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "AL12",
                    currentJob.getJobCostingAndPayment().getLastPaymentEnteredBy().getName(),
                    "java.lang.String", dataCellStyle);

            // CLIENT NAME & BILLING ADDRESS
            // Name
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderLeft((short) 1);
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "A20",
                    ativeClient.getName(),
                    "java.lang.String", dataCellStyle);

            // Billing address    
            Address billingAddress;
            if (currentJob.getBillingAddress() == null) {
                billingAddress = ativeClient.getBillingAddress();
            } else {
                billingAddress = currentJob.getBillingAddress();
            }
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "A22",
                    billingAddress.getAddressLine1(),
                    "java.lang.String", dataCellStyle);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "A23",
                    billingAddress.getAddressLine2(),
                    "java.lang.String", dataCellStyle);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "A24",
                    billingAddress.getStateOrProvince(),
                    "java.lang.String", dataCellStyle);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "A25",
                    billingAddress.getCity(),
                    "java.lang.String", dataCellStyle);

            // Contact person
            // Name
            Contact contactPerson;
            if (currentJob.getContact() == null) {
                contactPerson = ativeClient.getMainContact();
            } else {
                contactPerson = currentJob.getContact();
            }
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderLeft((short) 1);
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "M20",
                    contactPerson,
                    "java.lang.String", dataCellStyle);

            // Email
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderTop((short) 1);
            dataCellStyle.setBorderLeft((short) 1);
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "M22",
                    contactPerson.getInternet().getEmail1(),
                    "java.lang.String", dataCellStyle);

            // Phone
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderTop((short) 1);
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "Z20",
                    contactPerson.getMainPhoneNumber(),
                    "java.lang.String", dataCellStyle);

            // Fax
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderBottom((short) 1);
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_BOTTOM);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "Z22",
                    contactPerson.getMainFaxNumber(),
                    "java.lang.String", dataCellStyle);

            // TYPE OF SERVICE(S) NEEDED
            // Gather services
            String services = "";
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderLeft((short) 1);
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);
            dataCellStyle.setWrapText(true);
            if (job.getServiceContract().getServiceRequestedTesting()) {
                services = services + "Testing";
            }
            if (job.getServiceContract().getServiceRequestedCalibration()) {
                services = services + "; Calibration";
            }
            if (job.getServiceContract().getServiceRequestedLabelEvaluation()) {
                services = services + "; Label Evaluation";
            }
            if (job.getServiceContract().getServiceRequestedInspection()) {
                services = services + "; Inspection";
            }
            if (job.getServiceContract().getServiceRequestedConsultancy()) {
                services = services + "; Consultancy";
            }
            if (job.getServiceContract().getServiceRequestedTraining()) {
                services = services + "; Training";
            }
            if (job.getServiceContract().getServiceRequestedOther()) {
                if ((job.getServiceContract().getServiceRequestedOtherText() != null)
                        && (!job.getServiceContract().getServiceRequestedOtherText().isEmpty())) {
                    services = services + "; " + job.getServiceContract().getServiceRequestedOtherText();
                }
            }
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "AD21",
                    services,
                    "java.lang.String", dataCellStyle);

            // Fax/Email report?:
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderRight((short) 1);
            dataCellStyle.setBorderBottom((short) 1);
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_BOTTOM);
            if (job.getServiceContract().getAdditionalServiceFaxResults()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, "AN21",
                        "Yes",
                        "java.lang.String", dataCellStyle);
            } else {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, "AN21",
                        "No",
                        "java.lang.String", dataCellStyle);
            }

            // Expedite?
            if (job.getServiceContract().getAdditionalServiceUrgent()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, "AN23",
                        "Yes",
                        "java.lang.String", dataCellStyle);
            } else {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, "AN23",
                        "No",
                        "java.lang.String", dataCellStyle);
            }

            // Purchase Order:        
            if (currentJob.getJobCostingAndPayment().getPurchaseOrderNumber().equals("")) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, "AN25",
                        "Not applicable",
                        "java.lang.String", dataCellStyle);
            } else {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, "AN25",
                        currentJob.getJobCostingAndPayment().getPurchaseOrderNumber(),
                        "java.lang.String", dataCellStyle);
            }

            // CLIENT INSTRUCTION/DETAILS FOR JOB
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderTop((short) 1);
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "M24",
                    currentJob.getInstructions(),
                    "java.lang.String", dataCellStyle);

            // DESCRIPTION OF SUBMITTED SAMPLE(S)
            int samplesStartngRow = 32;
            if (job.getJobSamples().size() > 0) {
                for (JobSample jobSample : job.getJobSamples()) {
                    dataCellStyle = getDefaultCellStyle(wb);
                    dataCellStyle.setBorderLeft((short) 1);
                    dataCellStyle.setFont(samplesRefFont);
                    dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
                    dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
                    dataCellStyle.setWrapText(true);
                    BusinessEntityUtils.setExcelCellValue(
                            wb, serviceContractSheet, "A" + samplesStartngRow,
                            jobSample.getReference(),
                            "java.lang.String", dataCellStyle);
                    dataCellStyle = getDefaultCellStyle(wb);
                    dataCellStyle.setBorderLeft((short) 1);
                    dataCellStyle.setFont(samplesFont);
                    dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
                    dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
                    dataCellStyle.setWrapText(true);
                    BusinessEntityUtils.setExcelCellValue(
                            wb, serviceContractSheet, "B" + samplesStartngRow,
                            jobSample.getName(),
                            "java.lang.String", dataCellStyle);
                    BusinessEntityUtils.setExcelCellValue(
                            wb, serviceContractSheet, "H" + samplesStartngRow,
                            jobSample.getProductBrand(),
                            "java.lang.String", dataCellStyle);
                    BusinessEntityUtils.setExcelCellValue(
                            wb, serviceContractSheet, "O" + samplesStartngRow,
                            jobSample.getProductModel(),
                            "java.lang.String", dataCellStyle);

                    String productSerialAndCode = jobSample.getProductSerialNumber();
                    if (!jobSample.getProductCode().equals("")) {
                        productSerialAndCode = productSerialAndCode + "/" + jobSample.getProductCode();
                    }
                    BusinessEntityUtils.setExcelCellValue(
                            wb, serviceContractSheet, "W" + samplesStartngRow,
                            productSerialAndCode,
                            "java.lang.String", dataCellStyle);
                    BusinessEntityUtils.setExcelCellValue(
                            wb, serviceContractSheet, "AG" + samplesStartngRow,
                            jobSample.getSampleQuantity(),
                            "java.lang.String", dataCellStyle);
                    BusinessEntityUtils.setExcelCellValue(
                            wb, serviceContractSheet, "AI" + samplesStartngRow,
                            jobSample.getQuantity(),
                            "java.lang.String", dataCellStyle);
                    BusinessEntityUtils.setExcelCellValue(
                            wb, serviceContractSheet, "AI" + samplesStartngRow,
                            jobSample.getQuantity() + " (" + jobSample.getUnitOfMeasure() + ")",
                            "java.lang.String", dataCellStyle);
                    // Disposal
                    if (jobSample.getMethodOfDisposal() == 2) {
                        BusinessEntityUtils.setExcelCellValue(
                                wb, serviceContractSheet, "AO" + samplesStartngRow,
                                "Yes",
                                "java.lang.String", dataCellStyle);
                    } else {
                        BusinessEntityUtils.setExcelCellValue(
                                wb, serviceContractSheet, "AO" + samplesStartngRow,
                                "No",
                                "java.lang.String", dataCellStyle);
                    }

                    samplesStartngRow++;
                }
            } else {
                dataCellStyle = getDefaultCellStyle(wb);
                dataCellStyle.setBorderLeft((short) 1);
                dataCellStyle.setFont(samplesRefFont);
                dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
                dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
                dataCellStyle.setWrapText(true);
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, "A" + samplesStartngRow,
                        "",
                        "java.lang.String", dataCellStyle);
                dataCellStyle = getDefaultCellStyle(wb);
                dataCellStyle.setBorderLeft((short) 1);
                dataCellStyle.setFont(samplesFont);
                dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
                dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
                dataCellStyle.setWrapText(true);
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, "B" + samplesStartngRow,
                        "Not applicable",
                        "java.lang.String", dataCellStyle);
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, "H" + samplesStartngRow,
                        "Not applicable",
                        "java.lang.String", dataCellStyle);
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, "O" + samplesStartngRow,
                        "Not applicable",
                        "java.lang.String", dataCellStyle);
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, "W" + samplesStartngRow,
                        "Not applicable",
                        "java.lang.String", dataCellStyle);
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, "AG" + samplesStartngRow,
                        "",
                        "java.lang.String", dataCellStyle);
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, "AI" + samplesStartngRow,
                        "",
                        "java.lang.String", dataCellStyle);
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, "AI" + samplesStartngRow,
                        "",
                        "java.lang.String", dataCellStyle);
                // Disposal                   
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, "AO" + samplesStartngRow,
                        "",
                        "java.lang.String", dataCellStyle);
            }

            // ADDITIONAL DETAILS FOR SAMPLE(S) 
            String details = "";
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderLeft((short) 1);
            dataCellStyle.setFont(defaultFont);
            dataCellStyle.setAlignment(HSSFCellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            if (job.getJobSamples().isEmpty()) {
                details = "Not applicable";
            } else {
                for (JobSample jobSample : job.getJobSamples()) {
                    if (!jobSample.getDescription().isEmpty()) {
                        details = details + " (" + jobSample.getReference() + ") "
                                + jobSample.getDescription();
                    }
                }
            }
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, "A27",
                    details,
                    "java.lang.String", dataCellStyle);

            // write and save file for later use
            wb.write(out);
            out.close();

            return new FileInputStream("ServiceContract-" + user.getId() + ".xls");
        } catch (Exception e) {
            System.out.println(e);
        }

        return null;
    }

    public Boolean checkForSampleDisposalMethod(List<JobSample> samples, Integer method) {
        for (JobSample jobSample : samples) {
            if (jobSample.getMethodOfDisposal().compareTo(method) == 0) {
                return true;
            }
        }
        return false;
    }

    public void associateDepartmentWithJobSubCategory(
            EntityManager em,
            Department department,
            JobSubCategory jobSubCategory) {

        em.getTransaction().begin();
        jobSubCategory.getDepartments().add(department);
        BusinessEntityUtils.saveBusinessEntity(em, jobSubCategory);
        em.getTransaction().commit();
    }

    public void associateDepartmentWithSector(
            EntityManager em,
            Department department,
            Sector sector) {

        em.getTransaction().begin();
        sector.getDepartments().add(department);
        BusinessEntityUtils.saveBusinessEntity(em, sector);
        em.getTransaction().commit();
    }

    public void associateDepartmentWithJobReportItem(
            EntityManager em,
            Department department,
            JobReportItem jobReportItem) {

        em.getTransaction().begin();
        jobReportItem.getDepartments().add(department);
        BusinessEntityUtils.saveBusinessEntity(em, jobReportItem);
        em.getTransaction().commit();
    }

    public void updateAllJobs(EntityManager em) {
        long jobsUpdated = 0;

        try {
            List<Job> jobs = Job.findAllJobs(em);
            em.getTransaction().begin();
            for (Job job : jobs) {
                if (job.getJobStatusAndTracking() != null) {
                    if (job.getJobStatusAndTracking().getAlertDate() == null) {
                        System.out.println("Updating alert date for: " + job);
                        job.getJobStatusAndTracking().setAlertDate(new Date());
                        BusinessEntityUtils.saveBusinessEntity(em, job);
                        ++jobsUpdated;
                    }
                }
            }
            em.getTransaction().commit();
            System.out.println("Jobs updated: " + jobsUpdated);

        } catch (Exception ex) {
            System.out.println(ex);
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

    public void doUnitCostSearch() {
        RequestContext context = RequestContext.getCurrentInstance();

        unitCosts = UnitCost.findUnitCosts(getEntityManager1(), getUnitCostDepartment().getName(), getSearchText());
        context.update("unitCostsTableForm");
    }

    public void doJobCostSearch() {
        RequestContext context = RequestContext.getCurrentInstance();

        jobsWithCostings = Job.findJobsWithJobCosting(getEntityManager1(), getUnitCostDepartment().getName(), getSearchText());
        context.update("jobCostsTableForm");
    }

    public void createNewUnitCost() {
        RequestContext context = RequestContext.getCurrentInstance();

        currentUnitCost = new UnitCost();

        context.update("unitCostForm");
        context.execute("unitCostDialog.show();");
    }

    public void editUnitCost() {
    }

    @Override
    public void handleDialogOkButtonClick() {
    }

    @Override
    public void handleDialogYesButtonClick() {

        if (dialogActionHandlerId.equals("unitCostDirty")) {
            RequestContext context = RequestContext.getCurrentInstance();
            saveUnitCost();
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

    public void onJobCostingsTableCellEdit(CellEditEvent event) {
        System.out.println("Job number of costing: " + getJobsWithCostings().get(event.getRowIndex()).getJobNumber());
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

    public List<String> getDepartmentSupervisorsEmailAddresses(Department department) {
        List<String> emails = new ArrayList<>();

        emails.add(getEmployeeDefaultEmailAdress(department.getHead()));
        // Get the email of the acting head of he/she is currently acting
        if (department.getActingHeadActive()) {
            emails.add(getEmployeeDefaultEmailAdress(department.getActingHead()));
        }

        return emails;
    }

    public String getEmployeeDefaultEmailAdress(Employee employee) {
        String address = "";

        // Get email1 which is treated as the employee's company email address
        if (!employee.getInternet().getEmail1().trim().equals("")) {
            address = employee.getInternet().getEmail1();
        } else {
            // Get and set default email using company domain
            EntityManager em = getEntityManager1();

            String listAsString = SystemOption.findSystemOptionByName(em, "domainNames").getOptionValue();
            String domainNames[] = listAsString.split(";");

            JobManagerUser user = JobManagerUser.findJobManagerUserByEmployeeId(em, employee.getId());

            // Build email address
            if (user != null) {
                address = user.getUsername();
                if (domainNames.length > 0) {
                    address = address + "@" + domainNames[0];
                }
            }

        }

        return address;
    }

    public Department getDepartmentAssignedToJob(Job job, EntityManager em) {

        Department dept;

        if (job.getSubContractedDepartment().getName().equals("--")
                || job.getSubContractedDepartment().getName().equals("")) {
            // This is not a subcontracted job see return to parent department            
            dept = Department.findDepartmentByName(em, job.getDepartment().getName());
            if (dept != null) {
                em.refresh(dept);
            }

            return dept;
        } else {
            dept = Department.findDepartmentByName(em, job.getSubContractedDepartment().getName());
            em.refresh(dept);

            return dept;
        }
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
            if (isCurrentJobNew() && !getUser().getEmployee().isMemberOf(getDepartmentAssignedToJob(currentJob, em))) {
                List<String> emails = getDepartmentSupervisorsEmailAddresses(getDepartmentAssignedToJob(currentJob, em));
                emails.add(getEmployeeDefaultEmailAdress(currentJob.getAssignedTo()));
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

            } else if (isDirty() && !getUser().getEmployee().isMemberOf(getDepartmentAssignedToJob(currentJob, em))) {
                List<String> emails = getDepartmentSupervisorsEmailAddresses(getDepartmentAssignedToJob(currentJob, em));
                emails.add(getEmployeeDefaultEmailAdress(currentJob.getAssignedTo()));
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

            // Emails related to job costing            
            if (sendJobCostingCompletedEmail) {
                sendJobCostingCompletedEmail = false;
                try {
                    List<String> emails = getDepartmentSupervisorsEmailAddresses(getDepartmentAssignedToJob(currentJob, em));
                    for (String email : emails) {
                        if (!email.equals("")) {
                            postJobManagerMail(null, email,
                                    ""/*BusinessEntityUtils.getPersonFullName(getDepartmentAssignedToJob(currentJob).getHead(), false)*/,
                                    "Job Costing Completed", getCompletedJobCostingEmailMessage(currentJob));
                        } else {
                            sendErrorEmail("The department's head email address is not valid!",
                                    "Job number: " + currentJob.getJobNumber()
                                    + "\nJMTS User: " + getUser().getUsername()
                                    + "\nDate/time: " + new Date());
                        }
                    }

                } catch (Exception e) {
                    System.out.println("Error sending Costing Completed Email");
                    System.out.println(e);
                }
            }

            if (sendJobCostingApprovedEmail) {
                sendJobCostingApprovedEmail = false;
                try {
                    // Send email(s) to approved job costing email recipient(s) 
                    String listAsString = SystemOption.findSystemOptionByName(em, "approvedJobCostingEmailRecipients").getOptionValue();
                    String emails[] = listAsString.split(";");
                    for (String email : emails) {
                        if (!email.equals("")) {
                            postJobManagerMail(null, email,
                                    ""/*BusinessEntityUtils.getPersonFullName(getDepartmentAssignedToJob(currentJob, em).getHead(), false)*/,
                                    "Job Costing Approved", getApprovedJobCostingEmailMessage(currentJob));
                        } else {
                            sendErrorEmail("The approved job costing email recipient is not valid!",
                                    "Job number: " + currentJob.getJobNumber()
                                    + "\nJMTS User: " + getUser().getUsername()
                                    + "\nDate/time: " + new Date());
                        }
                    }

                } catch (Exception e) {
                    System.out.println("Error sending Costing Approved Email");
                    System.out.println(e);
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
    }

    public void openReportsTab() {
        reportManager.setUser(user);
        reportManager.setMainTabView(mainTabView);
        reportManager.setCurrentJob(currentJob);
        mainTabView.renderTab(getEntityManager1(), "reportsTab", true);
    }

    public Department getDepartmentBySystemOptionDeptId(String option) {
        EntityManager em = getEntityManager1();

        Long id = Long.parseLong(SystemOption.findSystemOptionByName(em, option).getOptionValue());

        Department department = Department.findDepartmentById(em, id);
        em.refresh(department);

        if (department != null) {
            return department;
        } else {
            return new Department("");
        }
    }

    public Boolean getIsMemberOfAccountsDept() {
        if (getUser().getEmployee().isMemberOf(getDepartmentBySystemOptionDeptId("accountsDepartmentId"))) {
            return true;
        }
        return false;
    }

    public Boolean getIsMemberOfCustomerServiceDept() {
        if (getUser().getEmployee().isMemberOf(getDepartmentBySystemOptionDeptId("customerServiceDeptId"))) {
            return true;
        }
        return false;
    }

    public Boolean getIsAuthorizedToModifyCostings() {
        // For now
        return true;
    }

    public List<SelectItem> getGCTPercentages() { // tk put in a costing entity
        ArrayList percentages = new ArrayList();

        percentages.addAll(Application.getStringListAsSelectItems(getEntityManager1(), "GCTPercentageList"));

        return percentages;
    }

}
