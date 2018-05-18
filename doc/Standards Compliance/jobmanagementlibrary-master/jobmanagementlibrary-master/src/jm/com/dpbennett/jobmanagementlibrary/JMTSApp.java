/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.com.dpbennett.jobmanagementlibrary;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import javax.inject.Named;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.NamingEnumeration;
import javax.naming.directory.SearchControls;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import jm.com.dpbennett.entity.BusinessOffice;
import jm.com.dpbennett.entity.Classification;
import jm.com.dpbennett.entity.Client;
import jm.com.dpbennett.entity.Contact;
import jm.com.dpbennett.entity.Country;
import jm.com.dpbennett.entity.DatePeriod;
import jm.com.dpbennett.entity.Department;
import jm.com.dpbennett.entity.Distributor;
import jm.com.dpbennett.entity.Employee;
import jm.com.dpbennett.entity.JobCategory;
import jm.com.dpbennett.entity.JobManagerUser;
import jm.com.dpbennett.entity.JobSample;
import jm.com.dpbennett.entity.JobSubCategory;
import jm.com.dpbennett.entity.Manufacturer;
import jm.com.dpbennett.entity.PetrolCompany;
import jm.com.dpbennett.entity.Sector;
import jm.com.dpbennett.entity.ServiceContract;
import jm.com.dpbennett.entity.ServiceRequest;
import jm.com.dpbennett.utils.BusinessEntityUtils;
import jm.com.dpbennett.utils.SearchParameters;

/**
 *
 * @author Desmond Bennett
 */
@Named(value = "JMTSApp")
@ApplicationScoped
public class JMTSApp {

    @PersistenceUnit(unitName = "JMTSWebAppPU")
    private EntityManagerFactory EMF1;    
    private List years;
    private ArrayList jaParishes = new ArrayList();
    private Map<String, String> themes = new TreeMap<String, String>();

    /**
     * Creates a new instance of JMTSApp
     */
    public JMTSApp() {
        
        // init themes
        themes.put("Aristo", "aristo");
        themes.put("Black-Tie", "black-tie");
        themes.put("Redmond", "redmond");

        // tk init jamaica parishes to put into database
        // Cornwall County
        jaParishes.add(new SelectItem("Hanover", "Hanover"));
        jaParishes.add(new SelectItem("Saint Elizabeth", "Saint Elizabeth"));
        jaParishes.add(new SelectItem("Saint James", "Saint James"));
        jaParishes.add(new SelectItem("Trelawny", "Trelawny"));
        jaParishes.add(new SelectItem("Westmoreland", "Westmoreland"));
        // Middlesex County
        jaParishes.add(new SelectItem("Clarendon", "Clarendon"));
        jaParishes.add(new SelectItem("Manchester", "Manchester"));
        jaParishes.add(new SelectItem("Saint Ann", "Saint Ann"));
        jaParishes.add(new SelectItem("Saint Catherine", "Saint Catherine"));
        jaParishes.add(new SelectItem("Saint Mary", "Saint Mary"));
        // Surrey County
        jaParishes.add(new SelectItem("Kingston", "Kingston"));
        jaParishes.add(new SelectItem("Portland", "Portland"));
        jaParishes.add(new SelectItem("Saint Andrew", "Saint Andrew"));
        jaParishes.add(new SelectItem("Saint Thomas", "Saint Thomas"));
    }

    public static JobManagerUser getUser() {
        Main main = findBean("main");
        if (main != null) {
            return main.getUser();
        }

        return null;
    }

    public Map<String, String> getThemes() {
        return themes;
    }

    public EntityManager getEntityManager1() {
        return EMF1.createEntityManager();
    }

//    public static EntityManager getEntityManager() {
//        jm.com.dpbennett.jobmanagementlibrary.JMTSApp app = findBean("JMTSApp");
//
//        return getEntityManager1();
//    }

    public List getYears() {
        if (years == null) {
            years = new ArrayList();
            Integer currentYear = Calendar.getInstance().get(Calendar.YEAR);
            for (Integer i = currentYear; i > (currentYear - 10); i--) {
                years.add(new SelectItem(i, i.toString()));
            }

        } else {
            return years;
        }

        return years;
    }

    public List getDateFields() {
        ArrayList dateFields = new ArrayList();

        // add items
        dateFields.add(new SelectItem("dateSubmitted", "Date submitted"));
        dateFields.add(new SelectItem("dateOfCompletion", "Date completed"));
        dateFields.add(new SelectItem("expectedDateOfCompletion", "Exp'ted date of completion"));
        dateFields.add(new SelectItem("dateSamplesCollected", "Date sample(s) collected"));
        dateFields.add(new SelectItem("dateDocumentCollected", "Date document(s) collected"));

        return dateFields;
    }

    public List getContactTypes() {
        ArrayList types = new ArrayList();

        // add items
        types.add(new SelectItem("General", "General"));
        types.add(new SelectItem("Main", "Main"));
        types.add(new SelectItem("Not associated", "Not associated"));


        return types;
    }

    public List getJamaicaParishes() {

        return jaParishes;
    }

    public List getSearchTypes() {
        ArrayList searchTypes = new ArrayList();

        searchTypes.add(new SelectItem("General", "General"));
        searchTypes.add(new SelectItem("My jobs", "My jobs"));
        searchTypes.add(new SelectItem("My department's jobs", "My department's jobs"));

        return searchTypes;
    }

    /*
     * NB: Methods to be put in database and not hard coded
     */
    public List getMethodsOfDisposal() {
        ArrayList dateFields = new ArrayList();

        // add items
        dateFields.add(new SelectItem("1", "Collected by the client within 30 days"));
        dateFields.add(new SelectItem("2", "Disposed of by the Bureau of Standards"));
        dateFields.add(new SelectItem("3", "To be determined"));

        return dateFields;
    }

    /*
     * NB: Methods to be put in database and not hard coded
     */
    public List getJobSampleTypes() {
        ArrayList dateFields = new ArrayList();

        // add items
        dateFields.add(new SelectItem("1", "Food"));
        dateFields.add(new SelectItem("2", "Electrical"));
        dateFields.add(new SelectItem("3", "Mechanical"));

        return dateFields;
    }

    public List<SelectItem> getDatePeriods() {
        ArrayList<SelectItem> datePeriods = new ArrayList<SelectItem>();

        // add items
        for (String name : DatePeriod.getDatePeriodNames()) {
            datePeriods.add(new SelectItem(name, name));
        }

        return datePeriods;
    }

    public List<String> completeDatePeriods(String query) {

        return DatePeriod.getDatePeriodNames();
    }

    public String getLogonUser() {
        String userName = System.getProperty("user.name");

        return userName;
    }

    // tk to be obtained from database
    public List getTestMeasures() {
        List measures = new ArrayList();

        measures.add(new SelectItem("5,20", "5,20")); // tk make option
        measures.add(new SelectItem("20,200", "20,200")); // tk make option
        measures.add(new SelectItem("200", "200")); // tk make option

        return measures;
    }

    public List getEquipmentWorkingStatus() {
        List status = new ArrayList();

        status.add(new SelectItem("--", "--"));
        status.add(new SelectItem("Not working", "Not working"));
        status.add(new SelectItem("Rejected", "Rejected"));
        status.add(new SelectItem("Working", "Working"));


        return status;
    }

    public Boolean findJobSample(List<JobSample> samples, String reference) {
        for (JobSample jobSample : samples) {
            if (jobSample.getReference().equals(reference)) {
                return true;
            }
        }

        return false;
    }

    //tk??
    public static void main(String[] args) {
        String[] fileNameParts = "testreport.jasper".split("\\.");
        System.out.println(fileNameParts[1]);
    }

    @SuppressWarnings("unchecked")
    public static <T> T findBean(String beanName) {
        FacesContext context = FacesContext.getCurrentInstance();
        return (T) context.getApplication().evaluateExpressionGet(context, "#{" + beanName + "}", Object.class);
    }

    public List<BusinessOffice> completeBusinessOffice(String query) {
        try {
            return BusinessOffice.findBusinessOfficesByName(getEntityManager1(), query);
        } catch (Exception e) {
            System.out.println(e);
            return new ArrayList<BusinessOffice>();
        }
    }

    public List<Department> completeDepartment(String query) {
        try {
            return Department.findDepartmentsByName(getEntityManager1(), query);

        } catch (Exception e) {
            System.out.println(e);

            return new ArrayList<Department>();
        }
    }

    public List<Manufacturer> completeManufacturer(String query) {
        return Manufacturer.findManufacturersBySearchPattern(getEntityManager1(), query);
    }

    public List<Employee> completeEmployee(String query) {

        try {
            List<Employee> employees = Employee.findActiveEmployeesByName(getEntityManager1(), query);
            if (employees != null) {
                return employees;
            } else {
                return new ArrayList<Employee>();
            }
        } catch (Exception e) {
            System.out.println(e);
            return new ArrayList<Employee>();
        }
    }

    // remove as it is implemented in ClientManager
    public List<Client> completeClient(String query) {
        try {
            return Client.findActiveClientsByName(getEntityManager1(), query);

        } catch (Exception e) {
            System.out.println(e);

            return new ArrayList<Client>();
        }
    }

    // remove as it is implemented in ClientManager 
    public List<String> completeClientName(String query) {
        try {
            return Client.findActiveClientNames(getEntityManager1(), query);

        } catch (Exception e) {
            System.out.println(e);

            return new ArrayList<String>();
        }
    }

    private Employee getEmployeeByName(EntityManager em, String name) {
        String names[] = name.split(",");
        if (names.length == 2) {
            return Employee.findEmployeeByName(em,
                    names[1].trim(),
                    names[0].trim());
        } else {
            return null;
        }
    }

    public static String getSearchResultsTableHeader(SearchParameters searchParameters, List searchResultsList) {
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");


        if (searchParameters != null) {
            if (searchParameters.getDatePeriod().getStartDate() != null
                    && searchParameters.getDatePeriod().getEndDate() != null) {
                return "Period: " + formatter.format(searchParameters.getDatePeriod().getStartDate()) + " to "
                        + formatter.format(searchParameters.getDatePeriod().getEndDate()) + " (found: "
                        + searchResultsList.size() + ")";
            } else {
                return "Search Results";
            }
        } else {
            return "Search Results";
        }
    }

    public List getCountries() {
        EntityManager em = getEntityManager1();
        ArrayList countriesList = new ArrayList();
        countriesList.add(new SelectItem(" ", " "));
        countriesList.add(new SelectItem("-- Not displayed --", "-- Not displayed --"));

        List<Country> countries = Country.getAllCountries(em);
        for (Country country : countries) {
            countriesList.add(new SelectItem(country.getName(), country.getName()));
        }

        return countriesList;
    }
    
    /**
     * Validate a user and associate the user with an employee if possible.
     *
     * @param em
     * @param username
     * @param password
     * @return
     */
    public static Boolean validateAndAssociateUser(EntityManager em, String username, String password) {
        Boolean userValidated = false;
        InitialLdapContext ctx = null;
        Employee employee;

        // tk for testing purposes
        if (username.equals("admin") && password.equals("password")) { // password to be encrypted
            return true;
        }

        try {
            List<jm.com.dpbennett.entity.LdapContext> ctxs = jm.com.dpbennett.entity.LdapContext.getAllLdapContexts(em);

            for (jm.com.dpbennett.entity.LdapContext ldapContext : ctxs) {
                em.refresh(ldapContext);
                ctx = ldapContext.getInitialLDAPContext(em, username, password);
                if (checkForLDAPUser(em, username, ctx)) {
                    // user does not exist in LDAP                    
                    userValidated = true;
                    break;
                }
            }

            // get employee that corresponds to this username
            if (userValidated) {
                employee = Employee.findEmployeeByUsername(em, username, ctx);
            } else {
                return false;
            }

            // get the user if one exists
            JobManagerUser jmtsUser = JobManagerUser.findJobManagerUserByUsername(em, username);

            if ((jmtsUser == null) && (employee != null)) {
                // create and associate the user with the employee
                jmtsUser = createNewUser(em);
                jmtsUser.setUsername(username);
                jmtsUser.setEmployee(employee);
                jmtsUser.setUserFirstname(employee.getFirstName());
                jmtsUser.setUserLastname(employee.getLastName());
                em.getTransaction().begin();
                BusinessEntityUtils.saveBusinessEntity(em, jmtsUser);
                em.getTransaction().commit();

                System.out.println("User validated and associated with employee.");

                return true;
            } else if ((jmtsUser != null) && (employee != null)) {
                System.out.println("User validated.");
                return true;
            } else if ((jmtsUser != null) && (employee == null)) {
                if (jmtsUser.getEmployee() != null) {
                    System.out.println("User validated.");
                    return true;
                } else {
                    System.out.println("User NOT validated!");
                    return false;
                }

            } else {
                System.out.println("User NOT validated!");
                return false;
            }

        } catch (Exception e) {
            System.err.println("Problem connecting to directory: " + e);
        }


        return false;
    }
    
    /**
     * Get LDAP attributes
     *
     * @param username
     * @param ctx
     * @return
     */
    public static Boolean checkForLDAPUser(EntityManager em, String username, LdapContext ctx) {

        try {
            SearchControls constraints = new SearchControls();
            constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
            String[] attrIDs = {"displayName"};

            constraints.setReturningAttributes(attrIDs);

            NamingEnumeration answer = ctx.search("DC=bos,DC=local", "SAMAccountName=" // tk make DC=? option
                    + username, constraints);
            if (!answer.hasMore()) { // assuming only one match
                System.out.println("LDAP user not found!");
                return Boolean.FALSE;
            }
        } catch (Exception ex) {
            System.out.println(ex);
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }
    
    public static JobManagerUser createNewUser(EntityManager em) {
        JobManagerUser user = new JobManagerUser();

        user.setUsername("");
        // init employee
        user.setEmployee(getDefaultEmployee(em, "--", "--"));

        // name
        user.setUserFirstname("");
        user.setUserLastname("");
        // default privileges
        user.setCanEditOwnJob(Boolean.TRUE);
        user.setCanEditDepartmentalJob(Boolean.TRUE);
        user.setCanEnterJob(Boolean.TRUE);

        return user;
    }
    
    public static Employee getDefaultEmployee(EntityManager em, String firstName, String lastName) {
        Employee employee = Employee.findEmployeeByName(em, firstName, lastName);

        // create employee if it does not exist
        if (employee == null) {
            employee = new Employee();
            employee.setFirstName(firstName);
            employee.setLastName(lastName);
            employee.setBusinessOffice(getDefaultBusinessOffice(em, "--"));
            employee.setDepartment(getDefaultDepartment(em, "--"));
            // save
            em.getTransaction().begin();
            BusinessEntityUtils.saveBusinessEntity(em, employee);
            em.getTransaction().commit();
        }

        return employee;
    }
    
     public static BusinessOffice getDefaultBusinessOffice(EntityManager em, String name) {
        BusinessOffice office = BusinessOffice.findBusinessOfficeByName(em, name);

        if (office == null) {
            em.getTransaction().begin();
            office = new BusinessOffice();
            office.setName(name);
            BusinessEntityUtils.saveBusinessEntity(em, office);
            em.getTransaction().commit();
        }

        return office;
    }
     
     public static Department getDefaultDepartment(EntityManager em,
            String name) {
        Department department = Department.findDepartmentByName(em, name);

        if (department == null) {
            department = new Department();

            em.getTransaction().begin();
            department.setName(name);
            BusinessEntityUtils.saveBusinessEntity(em, department);
            em.getTransaction().commit();
        }

        return department;
    }

    public static Manufacturer getDefaultManufacturer(EntityManager em,
            String name) {
        Manufacturer manufacturer = Manufacturer.findManufacturerByName(em, name);

        if (manufacturer == null) {
            manufacturer = new Manufacturer();
            em.getTransaction().begin();
            manufacturer.setName(name);
            BusinessEntityUtils.saveBusinessEntity(em, manufacturer);
            em.getTransaction().commit();
        }

        return manufacturer;
    }

    /**
     * Get the default distributor by name and creates one if it does not exist.
     *
     * @param em
     * @param name
     * @return
     */
    public static Distributor getDefaultDistributor(EntityManager em,
            String name) {
        Distributor distributor = Distributor.findDistributorByName(em, name);

        if (distributor == null) {
            distributor = new Distributor();
            em.getTransaction().begin();
            distributor.setName(name);
            BusinessEntityUtils.saveBusinessEntity(em, distributor);
            em.getTransaction().commit();
        }

        return distributor;
    }

    public static PetrolCompany getDefaultPetrolCompany(EntityManager em,
            String name) {
        PetrolCompany petrolCompany = PetrolCompany.findPetrolCompanyByName(em, name);

        if (petrolCompany == null) {
            petrolCompany = new PetrolCompany();

            em.getTransaction().begin();
            petrolCompany.setName(name);
            BusinessEntityUtils.saveBusinessEntity(em, petrolCompany);
            em.getTransaction().commit();
        }

        return petrolCompany;
    }

    public static Contact getDefaultContact(EntityManager em, String firstName, String lastName) {

        Contact contact = Contact.findContactByName(em, firstName, lastName);

        // create employee if it does not exist
        if (contact == null) {
            contact = new Contact();
            contact.setFirstName(firstName);
            contact.setLastName(lastName);
            // save
            em.getTransaction().begin();
            BusinessEntityUtils.saveBusinessEntity(em, contact);
            em.getTransaction().commit();
        }

        return contact;
    }
    
    public static void postJobManagerMail(
            Session mailSession,
            JobManagerUser user,
            String subject,
            String message) throws Exception {

        boolean debug = false;
        Message msg;

        if (mailSession == null) {
            //Set the host smtp address
            Properties props = new Properties();
            props.put("mail.smtp.host", "BOSMAIL2.BOS.local");// "172.16.0.3" // tk host to be made an option stored in database

            // create some properties and get the default Session
            Session session = Session.getDefaultInstance(props, null); // def null
            session.setDebug(debug);
            msg = new MimeMessage(session);
        } else {
            msg = new MimeMessage(mailSession);
        }

        // set the from and to address
        InternetAddress addressFrom = new InternetAddress("jobmanager", "Job Manager");
        msg.setFrom(addressFrom);

        InternetAddress[] addressTo = new InternetAddress[1];
        if (user != null) {
            addressTo[0] = new InternetAddress(user.getUsername(), user.getEmployee().getFirstName() + " " + user.getEmployee().getLastName());
        } else {
            // tk send message to developer. username and full name to be obtained from database in future.
            addressTo[0] = new InternetAddress("dbennett", "Desmond Bennett");
        }

        msg.setRecipients(Message.RecipientType.TO, addressTo);

        // Setting the Subject and Content Type
        msg.setSubject(subject);
        msg.setContent(message, "text/plain");
        Transport.send(msg);
    }

    public static Manufacturer getValidManufacturer(EntityManager em, String name) {

        if ((name == null) || (name.equals(""))) {
            return null;
        } else if (!BusinessEntityUtils.validateName(name)) {
            return null;
        } else {
            Manufacturer currentManufacturer = Manufacturer.findManufacturerByName(em, name);
            if (currentManufacturer == null) {
                // create new
                currentManufacturer = new Manufacturer();
                // replace double quotes with two single quotes to avoid query issues
                currentManufacturer.setName(name.replaceAll("\"", "''"));

                return currentManufacturer;
            } else { // create and return new manufacturer
                return currentManufacturer;
            }
        }
    }
    
    public static ServiceRequest createNewServiceRequest(EntityManager em,
            JobManagerUser user,
            Boolean autoGenerateServiceRequestNumber) {

        ServiceRequest sr = new ServiceRequest();
        sr.setClient(new Client(""));
        sr.setServiceRequestNumber("");
        sr.setJobDescription("");

        sr.setBusinessOffice(getDefaultBusinessOffice(em, "Head Office"));
//        sr.setDepartment(getDefaultDepartment(em, "--"));
//        sr.setAssignedTo(getDefaultEmployee(em, "--", "--"));

        sr.setClassification(Classification.findClassificationByName(em, "--"));
        sr.setSector(Sector.findSectorByName(em, "--"));
        sr.setJobCategory(JobCategory.findJobCategoryByName(em, "--"));
        sr.setJobSubCategory(JobSubCategory.findJobSubCategoryByName(em, "--"));

        sr.setServiceContract(createServiceContract());
        sr.setAutoGenerateServiceRequestNumber(autoGenerateServiceRequestNumber);

        // job status and tracking
        sr.setDateSubmitted(new Date());

        return sr;
    }
    
    public static ServiceContract createServiceContract() {
        ServiceContract serviceContract = new ServiceContract();
        // init service contract
        serviceContract.setIntendedMarketLocal(true);
        serviceContract.setAutoAddSampleInformation(true);
        serviceContract.setAdditionalServiceUrgent(false);
        serviceContract.setAdditionalServiceFaxResults(false);
        serviceContract.setAdditionalServiceTelephonePresumptiveResults(false);
        serviceContract.setAdditionalServiceSendMoreContractForms(false);
        serviceContract.setAdditionalServiceOther(false);
        serviceContract.setAdditionalServiceOtherText("");
        serviceContract.setIntendedMarketLocal(false);
        serviceContract.setIntendedMarketCaricom(false);
        serviceContract.setIntendedMarketUK(false);
        serviceContract.setIntendedMarketUSA(false);
        serviceContract.setIntendedMarketCanada(false);
        serviceContract.setIntendedMarketOther(false);
        serviceContract.setIntendedMarketOtherText("");
        serviceContract.setServiceRequestedTesting(false);
        serviceContract.setServiceRequestedCalibration(false);
        serviceContract.setServiceRequestedLabelEvaluation(false);
        serviceContract.setServiceRequestedInspection(false);
        serviceContract.setServiceRequestedConsultancy(false);
        serviceContract.setServiceRequestedTraining(false);
        serviceContract.setServiceRequestedOther(false);
        serviceContract.setServiceRequestedOtherText("");
        serviceContract.setSpecialInstructions("");

        return serviceContract;
    }
}
