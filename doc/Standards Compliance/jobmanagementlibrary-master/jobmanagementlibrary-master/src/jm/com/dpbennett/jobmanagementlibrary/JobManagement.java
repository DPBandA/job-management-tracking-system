/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jm.com.dpbennett.jobmanagementlibrary;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import jm.com.dpbennett.utils.BusinessEntityUtils;
import jm.com.dpbennett.entity.Address;
import jm.com.dpbennett.entity.BusinessEntity;
import jm.com.dpbennett.entity.BusinessOffice;
import jm.com.dpbennett.entity.CashPayment;
import jm.com.dpbennett.entity.Classification;
import jm.com.dpbennett.entity.Client;
import jm.com.dpbennett.entity.ComplianceSurvey;
import jm.com.dpbennett.entity.Contact;
import jm.com.dpbennett.entity.CostComponent;
import jm.com.dpbennett.entity.DatePeriod;
import jm.com.dpbennett.utils.DatePeriodJobReportColumnData;
import jm.com.dpbennett.entity.Department;
import jm.com.dpbennett.entity.DepartmentReport;
import jm.com.dpbennett.entity.Distributor;
import jm.com.dpbennett.entity.DocumentSequenceNumber;
import jm.com.dpbennett.entity.DocumentStandard;
import jm.com.dpbennett.entity.DocumentType;
import jm.com.dpbennett.entity.Employee;
import jm.com.dpbennett.entity.FactoryInspection;
import jm.com.dpbennett.entity.FactoryInspectionComponent;
import jm.com.dpbennett.entity.Job;
import jm.com.dpbennett.entity.JobCategory;
import jm.com.dpbennett.entity.JobCosting;
import jm.com.dpbennett.entity.JobCostingAndPayment;
import jm.com.dpbennett.entity.JobManagerUser;
import jm.com.dpbennett.entity.JobReportItem;
import jm.com.dpbennett.entity.JobSample;
import jm.com.dpbennett.entity.JobStatusAndTracking;
import jm.com.dpbennett.entity.JobSubCategory;
import jm.com.dpbennett.entity.LegalDocument;
import jm.com.dpbennett.entity.Manufacturer;
import jm.com.dpbennett.entity.PetrolCompany;
import jm.com.dpbennett.entity.PetrolPump;
import jm.com.dpbennett.entity.PetrolPumpNozzle;
import jm.com.dpbennett.entity.PetrolStation;
import jm.com.dpbennett.entity.Seal;
import jm.com.dpbennett.entity.Sector;
import jm.com.dpbennett.entity.ServiceContract;
import jm.com.dpbennett.entity.ServiceRequest;
import jm.com.dpbennett.entity.Sticker;
import jm.com.dpbennett.entity.TestMeasure;
import jm.com.dpbennett.utils.DatePeriodJobReport;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;

/**
 *
 * @author Desmond Bennett
 */
public class JobManagement implements Serializable {

    private EntityManager EM;
    private static EntityManagerFactory EMF;

    public JobManagement() {
    }

    public JobManagement(EntityManager EM) {
        this.EM = EM;
    }

    public ServiceRequest createNewServiceRequest(EntityManager em,
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

    /**
     * Validate a user and associate the user with an employee if possible.
     *
     * @param em
     * @param username
     * @param password
     * @return
     */
    public Boolean validateAndAssociateUser(EntityManager em, String username, String password) {
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
                ctx = ldapContext.getInitialLDAPContext(username, password);
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
    public Boolean checkForLDAPUser(EntityManager em, String username, LdapContext ctx) {

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

    /**
     * Get LDAP attributes
     *
     * @param username
     * @param ctx
     * @return
     */
    public String getUserBasicAttributes(String username, LdapContext ctx) {
        String userdetails = null;
        try {

            SearchControls constraints = new SearchControls();
            constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
            String[] attrIDs = {
                "distinguishedName",
                "sn",
                "givenname",
                "mail",
                "telephonenumber",
                "displayName",
                "uid"};
            constraints.setReturningAttributes(attrIDs);
            //First input parameter is search bas, it can be "CN=Users,DC=YourDomain,DC=com"
            //Second Attribute can be uid=username
            NamingEnumeration answer = ctx.search("DC=bos,DC=local", "SAMAccountName="
                    + username, constraints);
            if (answer.hasMore()) {
                Attributes attrs = ((SearchResult) answer.next()).getAttributes();
                System.out.println("distinguishedName " + attrs.get("distinguishedName"));
                System.out.println("givenname " + attrs.get("givenname"));
                System.out.println("sn " + attrs.get("sn"));
                System.out.println("mail " + attrs.get("mail"));
                System.out.println("telephonenumber " + attrs.get("telephonenumber"));
                System.out.println("displayName " + attrs.get("displayName"));
                System.out.println("uid " + attrs.get("uid"));
            } else {
                throw new Exception("Invalid User");
            }

        } catch (Exception ex) {
            System.out.println(ex);
        }
        return userdetails;
    }

    public Boolean validateUser(String username, String password) {
        Hashtable env = new Hashtable();
        String principal = username + "@bos.local"; // tk option
        String ldapURL = "ldap://admain:389"; // tk option
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, principal);
        env.put(Context.SECURITY_CREDENTIALS, password);
        env.put(Context.PROVIDER_URL, ldapURL);

        try {
            //Create the initial directory context
            LdapContext ctx = new InitialLdapContext(env, null);
            return true;
        } catch (NamingException e) {
            System.err.println("Problem connecting to directory: " + e);
        }

        return false;
    }

    public JobManagerUser createNewUser(EntityManager em) {
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

//    public LegalDocument createNewLegalDocument(EntityManager em,
//            JobManagerUser user) {
//
//        LegalDocument legalDocument = new LegalDocument();
//        legalDocument.setAutoGenerateNumber(Boolean.TRUE);
//        // department, employee & business office
//        if (user != null) {
//            if (user.getEmployee() != null) {
//                legalDocument.setResponsibleOfficer(Employee.findEmployeeById(em, user.getEmployee().getId()));
//                legalDocument.setResponsibleDepartment(Department.findDepartmentById(em, user.getEmployee().getDepartment().getId()));
//            }
//        }
//        // externla client
//        //legalDocument.setExternalClient(getDefaultClient(em, "--"));
//        // default requesting department
//        legalDocument.setRequestingDepartment(getDefaultDepartment(em, "--"));
//        // submitted by
//        //legalDocument.setSubmittedBy(getEmployeeByName(em, "--", "--"));
//        // doc type
//        legalDocument.setType(DocumentType.findDocumentTypeByName(em, "--"));
//        // doc classification
//        legalDocument.setClassification(Classification.findClassificationByName(em, "--"));
//        // doc for
//        legalDocument.setDocumentForm("H"); //
//        // get number
//        legalDocument.setNumber(LegalDocument.getLegalDocumentNumber(legalDocument, "ED"));
//
//        return legalDocument;
//    }

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

    public void createFactoryInspectionComponents(FactoryInspection inspection) {
        // tk impl getting this from a file
        // ESTABLISHMENT ENVIRONS category
        FactoryInspectionComponent component = new FactoryInspectionComponent("ESTABLISHMENT ENVIRONS", "Location", false);
        inspection.getInspectionComponents().add(component);
        component = new FactoryInspectionComponent("ESTABLISHMENT ENVIRONS", "Waste Disposal", false);
        inspection.getInspectionComponents().add(component);
        component = new FactoryInspectionComponent("ESTABLISHMENT ENVIRONS", "Building Exterior", false);
        inspection.getInspectionComponents().add(component);
        component = new FactoryInspectionComponent("ESTABLISHMENT ENVIRONS", "Drainage", false);
        inspection.getInspectionComponents().add(component);
        component = new FactoryInspectionComponent("ESTABLISHMENT ENVIRONS", "Yard Storage", false);
        inspection.getInspectionComponents().add(component);
        component = new FactoryInspectionComponent("ESTABLISHMENT ENVIRONS", "Tank", false);
        inspection.getInspectionComponents().add(component);
        component = new FactoryInspectionComponent("ESTABLISHMENT ENVIRONS", "Other Buildings", false);
        inspection.getInspectionComponents().add(component);
        // BUILDING INTERIOR
        component = new FactoryInspectionComponent("BUILDING INTERIOR", "Design & Contruction", false);
        inspection.getInspectionComponents().add(component);
        component = new FactoryInspectionComponent("BUILDING INTERIOR", "Pipes/Conduits/Beams", false);
        inspection.getInspectionComponents().add(component);
        component = new FactoryInspectionComponent("BUILDING INTERIOR", "Other Overhead Structures", false);
        inspection.getInspectionComponents().add(component);
        component = new FactoryInspectionComponent("BUILDING INTERIOR", "Lighting/Lighting Intensity", false);
        inspection.getInspectionComponents().add(component);
        component = new FactoryInspectionComponent("BUILDING INTERIOR", "Ventilation", false);
        inspection.getInspectionComponents().add(component);
        component = new FactoryInspectionComponent("BUILDING INTERIOR", "Drainage Systems", false);
        inspection.getInspectionComponents().add(component);
        component = new FactoryInspectionComponent("BUILDING INTERIOR", "Maintenance of walls", false);
        inspection.getInspectionComponents().add(component);
        component = new FactoryInspectionComponent("BUILDING INTERIOR", "Maintenance of floors", false);
        inspection.getInspectionComponents().add(component);
        component = new FactoryInspectionComponent("BUILDING INTERIOR", "Maintenance of ceilings", false);
        inspection.getInspectionComponents().add(component);
        component = new FactoryInspectionComponent("BUILDING INTERIOR", "Gutters/drains", false);
        inspection.getInspectionComponents().add(component);
        component = new FactoryInspectionComponent("BUILDING INTERIOR", "Screens/Blowers/Insecticutor", false);
        inspection.getInspectionComponents().add(component);
        // RECEIVING
        component = new FactoryInspectionComponent("RECEIVING", "Raw Material Infestation", false);
        inspection.getInspectionComponents().add(component);
        component = new FactoryInspectionComponent("RECEIVING", "Raw Material Infestation", false);
        inspection.getInspectionComponents().add(component);
        component = new FactoryInspectionComponent("RECEIVING", "Raw Material Infestation", false);
        inspection.getInspectionComponents().add(component);
        component = new FactoryInspectionComponent("RECEIVING", "Raw Material Infestation", false);
        inspection.getInspectionComponents().add(component);
        component = new FactoryInspectionComponent("RECEIVING", "Raw Material Infestation", false);
        inspection.getInspectionComponents().add(component);
    }

    /*
     * Takes a list of job costings enties an set their ids and component ids
     * to null which will result in new job costings being created when
     * the job costins are commited to the database
     */
    public List<JobCosting> copyJobCostings(List<JobCosting> srcCostings) {
        ArrayList<JobCosting> newJobCostings = new ArrayList<JobCosting>();

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

    // tk to be created from template file or database records
    public void createJobCostings(JobCostingAndPayment jobCostingAndPayment) {

        JobCosting jobCosting = new JobCosting("1.0 TEST/CALIBRATION");
        jobCosting.getCostComponents().add(new CostComponent(jobCosting.getName(), Boolean.TRUE));
        jobCosting.getCostComponents().add(new CostComponent("1.1 Preparation"));
        jobCosting.getCostComponents().add(new CostComponent("1.2 Work"));
        jobCostingAndPayment.getJobCostings().add(jobCosting);

        jobCosting = new JobCosting("2.0 LABEL EVALUATION/INSPECTION");
        jobCosting.getCostComponents().add(new CostComponent(jobCosting.getName(), Boolean.TRUE));
        jobCosting.getCostComponents().add(new CostComponent("2.1"));
        jobCosting.getCostComponents().add(new CostComponent("2.2"));
        jobCostingAndPayment.getJobCostings().add(jobCosting);

        jobCosting = new JobCosting("3.0 CONSULTANCY/TRAINING");
        jobCosting.getCostComponents().add(new CostComponent(jobCosting.getName(), Boolean.TRUE));
        jobCosting.getCostComponents().add(new CostComponent("3.1"));
        jobCosting.getCostComponents().add(new CostComponent("3.2"));
        jobCostingAndPayment.getJobCostings().add(jobCosting);

        jobCosting = new JobCosting("4.0 REPORT WRITING");
        jobCosting.getCostComponents().add(new CostComponent(jobCosting.getName(), Boolean.TRUE));
        jobCosting.getCostComponents().add(new CostComponent("4.1 Analysis of data"));
        jobCosting.getCostComponents().add(new CostComponent("4.2 Documentation of report"));
        jobCosting.getCostComponents().add(new CostComponent("4.3 Supervisor's review/approval"));
        jobCostingAndPayment.getJobCostings().add(jobCosting);

        jobCosting = new JobCosting("5.0 ENERGY/MATERIAL COST");
        jobCosting.getCostComponents().add(new CostComponent(jobCosting.getName(), Boolean.TRUE));
        jobCosting.getCostComponents().add(new CostComponent("5.1"));
        jobCosting.getCostComponents().add(new CostComponent("5.2"));
        jobCostingAndPayment.getJobCostings().add(jobCosting);

        jobCosting = new JobCosting("6.0 TRAVELLING");
        jobCosting.getCostComponents().add(new CostComponent(jobCosting.getName(), Boolean.TRUE));
        jobCosting.getCostComponents().add(new CostComponent("6.1 Driver"));
        jobCosting.getCostComponents().add(new CostComponent("6.2 Passenger"));
        jobCostingAndPayment.getJobCostings().add(jobCosting);

        jobCosting = new JobCosting("7.0 OTHER CHARGES");
        jobCosting.getCostComponents().add(new CostComponent(jobCosting.getName(), Boolean.TRUE));
        jobCosting.getCostComponents().add(new CostComponent("7.1"));
        jobCosting.getCostComponents().add(new CostComponent("7.2"));
        jobCostingAndPayment.getJobCostings().add(jobCosting);

    }

    public JobCostingAndPayment createJobCostingAndPayment() {
        JobCostingAndPayment jobCostingAndPayment = new JobCostingAndPayment();

        jobCostingAndPayment.setCostingDate(new Date());
        jobCostingAndPayment.setPurchaseOrderNumber("");
        jobCostingAndPayment.setReceiptNumber("");

        // create costings from template
        createJobCostings(jobCostingAndPayment);

        return jobCostingAndPayment;
    }

    

    public Job copyJob(EntityManager em,
            Job currentJob,
            JobManagerUser user,
            Boolean autoGenerateJobNumber,
            Boolean copySamples) {
        Job job = new Job();

        // client
        job.setClient(currentJob.getClient());

        job.setReportNumber("");
        job.setJobDescription("");

        // business office
        job.setBusinessOffice(currentJob.getBusinessOffice());
        job.setDepartment(currentJob.getDepartment());

        job.setSubContractedDepartment(getDefaultDepartment(em, "--"));
        // reporting fields
        job.setClassification(Classification.findClassificationByName(em, "--"));
        job.setSector(Sector.findSectorByName(em, "--"));
        job.setJobCategory(JobCategory.findJobCategoryByName(em, "--"));
        job.setJobSubCategory(JobSubCategory.findJobSubCategoryByName(em, "--"));
        // service contract
        job.setServiceContract(createServiceContract());
        // set default values
        job.setAutoGenerateJobNumber(autoGenerateJobNumber);
        job.setIsEarningJob(Boolean.TRUE);
        job.setYearReceived(Calendar.getInstance().get(Calendar.YEAR));
        // job status and tracking
        job.setJobStatusAndTracking(new JobStatusAndTracking());
        job.getJobStatusAndTracking().setDateSubmitted(new Date());
        job.getJobStatusAndTracking().setAlertDate(null);
        job.getJobStatusAndTracking().setDateJobEmailWasSent(null);
        // job costing and payment
        // tk init costing...to be done with a template later
        job.setJobCostingAndPayment(createJobCostingAndPayment());
        // this is done here because job number is dependent on business office, department/subcontracted department

        // copy samples
        if (copySamples) {
//            job.setJobSamples(currentJob.getJobSamples());
            List<JobSample> samples = currentJob.getJobSamples();
            job.setNumberOfSamples(currentJob.getNumberOfSamples());
            for (Iterator<JobSample> it = samples.iterator(); it.hasNext();) {
                JobSample jobSample = it.next();
//                JobSample newJobSample = new JobSample(jobSample);
//                newJobSample.setId(new Long(job.getJobSamples().size() + 1));
                job.getJobSamples().add(new JobSample(jobSample));
//                job.getJobSamples().add(jobSample);
            }
        }


        // sequence number
        job.setJobSequenceNumber(currentJob.getJobSequenceNumber());

        // job number
        if (job.getAutoGenerateJobNumber()) {
            job.setJobNumber(getJobNumber(job));
        }

        return job;
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

//    public Long saveBusinessEntity(EntityManager em, BusinessEntity businessEntity) {
//
//        if (businessEntity.getId() != null) {
//            em.merge(businessEntity);
//        } else {
//            em.persist(businessEntity);
//        }
//
//        return businessEntity.getId();
//    }
    public Boolean testDatabaseConnection() {
        return EMF.isOpen();
    }

    public HashMap<String, String> getConnectionProperties(
            String url,
            String driver,
            String username,
            String password) {

        // setup new database connection properties
        HashMap<String, String> prop = new HashMap<String, String>();
        prop.put("javax.persistence.jdbc.user", username);
        prop.put("javax.persistence.jdbc.password", password);
        prop.put("javax.persistence.jdbc.url", url);
        prop.put("javax.persistence.jdbc.driver", driver);

        return prop;
    }

    public Boolean validateJobNumber(String jobNumber, Boolean auto) {
        Integer departmentCode = 0;
        Integer year = 0;
        Long sequenceNumber = 0L;

        String parts[] = jobNumber.split("/");
        if (parts != null) {
            // check for correct number of parts
            if ((parts.length >= 3)
                    && (parts.length <= 5)) {
                // are subgroup code, year and sequence number valid integers/long?
                try {
                    if (auto && parts[0].equals("?")) {
                        // This means the complete job number has not yet
                        // been generate. Ignore for now.
                    } else {
                        departmentCode = Integer.parseInt(parts[0]);
                    }
                    year = Integer.parseInt(parts[1]);
                    if (auto && parts[2].equals("?")) {
                        // This means the complete job number has not yet
                        // been generate. Ignore for now.
                    } else {
                        sequenceNumber = Long.parseLong(parts[2]);
                    }
                } catch (Exception e) {
                    System.out.println(e);
                    return false;
                }
                // subgroup code, year and deparment code have valid ranges?
                if (auto && parts[0].equals("?")) {
                    // This means the complete job number has not yet
                    // been generate. Ignore for now.
                } else if (departmentCode < 0) {
                    return false;
                }
                if (year < 1970) {
                    return false;
                }
                if (auto && parts[2].equals("?")) {
                    // This means the complete job number has not yet
                    // been generate. Ignore for now.
                } else if (sequenceNumber < 1L) {
                    return false;
                }
                // validate 4th part that can be an integer for a department
                // code or a sample reference(s)
                if (parts.length > 3) {
                    try {
                        departmentCode = Integer.parseInt(parts[3]);
                        if (departmentCode < 0) {
                            return false;
                        }
                    } catch (Exception e) {
                        // this means 4th part is not a department code
                        // and that's ok for now.
                        System.out.println("Job number validation error: This means 4th part is not a department code.");
                    }
                }
                // all is well here
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Delete a generic entity
     *
     * @param entity
     * @return
     */
    private Boolean deleteEntity(EntityManager em, Object entity) {

        try {
            if (entity != null) {
                em.remove(entity);
            } else {
                return false;
            }
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }

        return true;
    }

    public Date getCurrentDate() {
        return new Date();
    }

    // tk may lomger needed or used. Check out
    public Long saveClient(EntityManager em, Client client) {
        if (client.getBillingAddress() != null) {
            BusinessEntityUtils.saveBusinessEntity(em, client.getBillingAddress());
        }
        return BusinessEntityUtils.saveBusinessEntity(em, client);
    }

    public Seal getDefaultSeal(EntityManager em,
            String number) {
        Seal seal = Seal.findSealByNumber(em, number);

        if (seal == null) {
            seal = new Seal();

            em.getTransaction().begin();
            seal.setNumber(number);
            BusinessEntityUtils.saveBusinessEntity(em, seal);
            em.getTransaction().commit();
        }

        return seal;
    }

    public Sticker getDefaultSticker(EntityManager em,
            String number) {
        Sticker sticker = Sticker.findStickerByNumber(em, number);

        if (sticker == null) {
            sticker = new Sticker();

            //em.getTransaction().begin();
            sticker.setNumber(number);
            BusinessEntityUtils.saveBusinessEntity(em, sticker);
            //em.getTransaction().commit();
        }

        return sticker;
    }

    public Long saveBillingAddress(EntityManager em, Address address) {
        return BusinessEntityUtils.saveBusinessEntity(em, address);
    }

    /**
     * getFourDigitNumberString Pad number with leading zeros if the number is
     * less than four digits
     *
     * @param number long
     * @return String
     */
    public String getFourDigitString(long number) {
        String fourDigitString = "";

        if ((number >= 0L) && (number <= 9L)) {
            fourDigitString = "000" + number;
        }
        if ((number >= 10L) && (number <= 99L)) {
            fourDigitString = "00" + number;
        }
        if ((number >= 100L) && (number <= 999L)) {
            fourDigitString = "0" + number;
        }
        if (number >= 1000L) {
            fourDigitString = "" + number;
        }
        return fourDigitString;
    }

    public String getJobNumber(
            String departmentCode,
            Integer year,
            Long jobSequenceNumber,
            String subContractedDepartmentCode,
            ArrayList<JobSample> jobSamples) {

        String jobNumber = "";
        String jobSequenceNumberStr = "?";
        Integer numberOfJobSamples = null;

        if (jobSamples != null) {
            numberOfJobSamples = jobSamples.size();
        }

        // include the department code of the deparment id if valid
        if (departmentCode == null) {
            departmentCode = "?";
        }
        // include the sequence number if it is valid
        if (jobSequenceNumber != 0) {
            //sequenceNumber = job.getJobSequenceNumber().toString();
            jobSequenceNumberStr = getFourDigitString(jobSequenceNumber);
        }

        // set base job number
        jobNumber = "" + departmentCode + "/" + year + "/" + jobSequenceNumberStr;
        // append subcontracted code if valid
        if (subContractedDepartmentCode != null) {
            jobNumber = jobNumber + "/" + subContractedDepartmentCode;
        }
        // append samples code if any
        if ((numberOfJobSamples != null) && (numberOfJobSamples > 0)) {
            if ((subContractedDepartmentCode == null) && (numberOfJobSamples > 1)) {
                jobNumber = jobNumber + "/"
                        + BusinessEntityUtils.getAlphaCode(0) + "-"
                        + BusinessEntityUtils.getAlphaCode(numberOfJobSamples - 1);
            } else if ((subContractedDepartmentCode == null) && (numberOfJobSamples <= 1)) {
                // job number remains as is for now
            } else if (subContractedDepartmentCode != null) { // subcontract?
                int index = 0;
                // update subcontract samples
                for (JobSample sample : jobSamples) {
                    if (index == 0) {
                        jobNumber = jobNumber + "/" + sample.getReference();
                    } else {
                        jobNumber = jobNumber + "," + sample.getReference();
                    }
                    index++;
                }
            }
        }

        return jobNumber;
    }

    public String getContactPerson(
            Client client,
            Integer index) {
        String firstname = "";
        String lastname = "";

        try {
            if (client.getContacts().get(index).getFirstName() != null) {
                firstname = client.getContacts().get(index).getFirstName();
            }
            if (client.getContacts().get(index).getLastName() != null) {
                lastname = client.getContacts().get(index).getLastName();
            }
            return firstname + " " + lastname;
        } catch (Exception e) {
            return null;
        }
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

    public ServiceContract createServiceContract() {
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

//    public LegalDocument saveLegalDocument(EntityManager em, LegalDocument legalDocument) {
//
//        // check if the current sequence number exist and assign a new sequence number if needed
//        if (DocumentSequenceNumber.findDocumentSequenceNumber(em,
//                legalDocument.getSequenceNumber(),
//                legalDocument.getYearReceived(),
//                legalDocument.getMonthReceived(),
//                legalDocument.getType().getId()) == null) {
//            legalDocument.setSequenceNumber(DocumentSequenceNumber.findNextDocumentSequenceNumber(em,
//                    legalDocument.getYearReceived(),
//                    legalDocument.getMonthReceived(),
//                    legalDocument.getType().getId()));
//        }
//        if (legalDocument.getAutoGenerateNumber()) {
//            legalDocument.setNumber(LegalDocument.getLegalDocumentNumber(legalDocument, "ED"));
//        }
//        BusinessEntityUtils.saveBusinessEntity(em, legalDocument);
//
//        return legalDocument;
//    }

//    public String getMonthShortFormat(Date date) {
//        String month = "";
//        Calendar c = Calendar.getInstance();
//        c.setTime(date);
//
//        switch (c.get(Calendar.MONTH)) {
//            case 0:
//                month = "Jan";
//                break;
//            case 1:
//                month = "Feb";
//                break;
//            case 2:
//                month = "Mar";
//                break;
//            case 3:
//                month = "Apr";
//                break;
//            case 4:
//                month = "May";
//                break;
//            case 5:
//                month = "Jun";
//                break;
//            case 6:
//                month = "Jul";
//                break;
//            case 7:
//                month = "Aug";
//                break;
//            case 8:
//                month = "Sep";
//                break;
//            case 9:
//                month = "Oct";
//                break;
//            case 10:
//                month = "Nov";
//                break;
//            case 11:
//                month = "Dec";
//                break;
//            default:
//                month = "";
//                break;
//        }
//        return month;
//    }

//    public String getYearShortFormat(Date date, int digits) {
//        String yearString = "";
//        Calendar c = Calendar.getInstance();
//        c.setTime(date);
//
//        int year = c.get(Calendar.YEAR);
//        yearString = yearString + year;
//
//        // get last x digits of year
//        yearString = yearString.substring(yearString.length() - digits, yearString.length());
//
//        return yearString;
//    }

    // tk move to doc manager
//    public String getLegalDocumentNumber(LegalDocument legalDocument, String prefix) {
//        String number = prefix;
//
//        // append department code
//        if (legalDocument.getResponsibleDepartment().getSubGroupCode() != null) {
//            number = number + legalDocument.getResponsibleDepartment().getSubGroupCode();
//        } else {
//            number = number + "?";
//        }
//        // append doc type
//        if (legalDocument.getType() != null) {
//            number = number + "_" + legalDocument.getType().getCode();
//        }
//        // append doc form
//        if (legalDocument.getDocumentForm() != null) {
//            number = number + "/" + legalDocument.getDocumentForm();
//        }
//        // append doc seq
//        if (legalDocument.getSequenceNumber() != null) {
//            NumberFormat formatter = DecimalFormat.getIntegerInstance();
//            formatter.setMinimumIntegerDigits(2);
//            number = number + "_" + formatter.format(legalDocument.getSequenceNumber());
//        } else {
//            number = number + "_?";
//        }
//        // append month in the form (MMM) and year in the form (YY).
//        if (legalDocument.getDateReceived() != null) {
//            number = number + "/" + getMonthShortFormat(legalDocument.getDateReceived())
//                    + getYearShortFormat(legalDocument.getDateReceived(), 2);
//        }
//
//        return number;
//    }

    public String getJobNumber(Job job) {
        Calendar c = Calendar.getInstance();
        String departmentOrCompanyCode = "?";
        String year = "?";
        String sequenceNumber = "?";
        String subContractedDepartmenyOrCompanyCode = null;

        if ((job.getAutoGenerateJobNumber() != null) && (job.getAutoGenerateJobNumber() != false)) {
            // include the department code based on parent or subcontract
            if ((job.getSubContractedDepartment() == null) && // is regional office job
                    (job.getBusinessOffice().getCode() != null)) {

                departmentOrCompanyCode = job.getBusinessOffice().getCode();
                subContractedDepartmenyOrCompanyCode = null;
            } else if ((job.getSubContractedDepartment() == null)
                    && (job.getDepartment() != null)) { // not a subcontract
                // get the department code based on its id
                if (job.getDepartment().getSubGroupCode() != null) {
                    departmentOrCompanyCode = job.getDepartment().getSubGroupCode();
                }
                subContractedDepartmenyOrCompanyCode = null;
            } else if ((job.getSubContractedDepartment() != null) && // is subcontract
                    ((job.getDepartment() != null)
                    || (job.getBusinessOffice().getCode() != null))) {
                if (job.getBusinessOffice().getCode() != null) {
                    departmentOrCompanyCode = job.getBusinessOffice().getCode();
                } else if (job.getDepartment().getSubGroupCode() != null) {
                    departmentOrCompanyCode = job.getDepartment().getSubGroupCode();
                }
                subContractedDepartmenyOrCompanyCode = job.getSubContractedDepartment().getSubGroupCode();
            }
            // use the date submitted to get the year if it is valid
            // and only if this is not a subcontracted job
            if ((job.getJobStatusAndTracking().getDateSubmitted() != null) && (job.getSubContractedDepartment() == null)) {
                c.setTime(job.getJobStatusAndTracking().getDateSubmitted());
                year = "" + c.get(Calendar.YEAR);
            } else if (job.getYearReceived() != null) {
                year = job.getYearReceived().toString();
            }
            // include the sequence number if it is valid
            if (job.getJobSequenceNumber() != null) {
                //sequenceNumber = job.getJobSequenceNumber().toString();
                sequenceNumber = getFourDigitString(job.getJobSequenceNumber());
            } else {
                sequenceNumber = "?";
            }
            // set base job number
            job.setJobNumber(departmentOrCompanyCode + "/" + year + "/" + sequenceNumber);
            // append subcontracted code if valid
            if (subContractedDepartmenyOrCompanyCode != null) {
                job.setJobNumber(job.getJobNumber() + "/" + subContractedDepartmenyOrCompanyCode);
            }
            // append samples code if any
            if ((job.getNumberOfSamples() != null) && (job.getNumberOfSamples() > 0)) {
                if ((subContractedDepartmenyOrCompanyCode == null) && (job.getNumberOfSamples() > 1)) {
                    job.setJobNumber(job.getJobNumber() + "/"
                            + BusinessEntityUtils.getAlphaCode(0) + "-"
                            + BusinessEntityUtils.getAlphaCode(job.getNumberOfSamples() - 1));
                } else if ((subContractedDepartmenyOrCompanyCode == null) && (job.getNumberOfSamples() <= 1)) {
                    // job number remains as is for now
                } else if (subContractedDepartmenyOrCompanyCode != null) { // subcontract?
                    int index = 0;
                    for (JobSample sample : job.getJobSamples()) {
                        if (index == 0) {
                            job.setJobNumber(job.getJobNumber() + "/" + sample.getReference());
                        } else {
                            job.setJobNumber(job.getJobNumber() + "," + sample.getReference());
                        }
                        index++;
                    }
                }
            }
        }

        return job.getJobNumber();
    }

    public Long saveJobSample(EntityManager em, JobSample jobSample) {
        return BusinessEntityUtils.saveBusinessEntity(em, jobSample);
    }

    public Boolean deleteJob(EntityManager em, Long Id) {

        Job job = null;

        job = em.find(Job.class, Id);
        return deleteEntity(em, job);
    }

    public Boolean deleteJobSample(EntityManager em, Long Id) {

        JobSample jobSample = null;

        jobSample = em.find(JobSample.class, Id);
        return deleteEntity(em, jobSample);
    }

    public Long insertJobCategory(EntityManager em, String category,
            String subCategory) {
        JobCategory jobCategory = new JobCategory();
        JobSubCategory jobSubCategory = new JobSubCategory();

        jobCategory.setCategory(category);
        if (subCategory != null) {
            jobSubCategory.setCategoryId(jobCategory.getId());
            jobSubCategory.setSubCategory(subCategory);
        }

        // save category and/or subcategory
        em.persist(jobCategory);
        if (jobCategory.getId() != null) {
            if (subCategory != null) {
                em.persist(jobSubCategory);
                if (jobSubCategory.getId() != null) {
                    return jobCategory.getId();
                }
            }
            return jobCategory.getId();
        }

        return 0L;
    }

    public Long insertJobSubCategory(EntityManager em, Long categoryId,
            String subCategory) {
        JobSubCategory jobSubCategory = new JobSubCategory();
        jobSubCategory.setIsEarning(Boolean.TRUE);

        jobSubCategory.setCategoryId(categoryId);
        jobSubCategory.setSubCategory(subCategory);

        // save subcategory
        em.persist(jobSubCategory);
        if (jobSubCategory.getId() != null) {
            return jobSubCategory.getId();
        } else {
            return 0L;
        }
    }

    public List<DatePeriodJobReportColumnData> jobSubCategogyGroupReportByDatePeriod(
            EntityManager em,
            String dateSearchField,
            String searchText, // filled in with department's name
            Date startDate,
            Date endDate) {

        List<DatePeriodJobReportColumnData> data = null;
        List<DatePeriodJobReportColumnData> dataSortedByEarningType = new ArrayList<DatePeriodJobReportColumnData>();

        String searchQuery = null;

        searchQuery =
                "SELECT NEW jm.com.dpbennett.utils.DatePeriodJobReportColumnData"
                + "("
                + "job.jobSubCategory,"
                + "SUM(jobCostingAndPayment.finalCost),"
                + "SUM(job.noOfTestsOrCalibrations)"
                + ")"
                + " FROM Job job"
                + " JOIN job.jobStatusAndTracking jobStatusAndTracking"
                + " JOIN job.jobSubCategory jobSubCategory"
                + " JOIN job.department department"
                + " JOIN job.subContractedDepartment subContractedDepartment"
                + " JOIN job.jobCostingAndPayment jobCostingAndPayment"
                + " WHERE ((jobStatusAndTracking." + dateSearchField + " >= " + BusinessEntityUtils.getDateString(startDate, "'", "YMD", "-")
                + " AND jobStatusAndTracking." + dateSearchField + " <= " + BusinessEntityUtils.getDateString(endDate, "'", "YMD", "-") + "))"
                + " AND ( UPPER(department.name) = '" + searchText.toUpperCase() + "'"
                + " OR UPPER(subContractedDepartment.name) = '" + searchText.toUpperCase() + "')"
                + " AND UPPER(jobStatusAndTracking.workProgress) <> 'CANCELLED'"
                + " AND UPPER(jobStatusAndTracking.workProgress) <> 'WITHDRAWN BY CLIENT'"
                + " GROUP BY job.jobSubCategory"
                + " ORDER BY job.jobSubCategory.subCategory ASC";
        // now do search
        try {
            data = em.createQuery(searchQuery, DatePeriodJobReportColumnData.class).getResultList();
            if (data == null) {
                data = new ArrayList<DatePeriodJobReportColumnData>();
            }

            // sort by earnings and non-earnings
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }

        return data;
    }

    public List<DatePeriodJobReportColumnData> sectorReportByDatePeriod(
            EntityManager em,
            String dateSearchField,
            String searchText, // filled in with department's name
            Date startDate,
            Date endDate) {

        List<DatePeriodJobReportColumnData> data = null;
        String searchQuery = null;

        searchQuery =
                "SELECT NEW jm.com.dpbennett.utils.DatePeriodJobReportColumnData"
                + "("
                + "job.sector,"
                + "SUM(job.noOfTestsOrCalibrations)"
                + ")"
                + " FROM Job job"
                + " JOIN job.jobStatusAndTracking jobStatusAndTracking"
                + " JOIN job.sector sector"
                + " JOIN job.department department"
                + " JOIN job.subContractedDepartment subContractedDepartment"
                + " WHERE ((jobStatusAndTracking." + dateSearchField + " >= " + BusinessEntityUtils.getDateString(startDate, "'", "YMD", "-")
                + " AND jobStatusAndTracking." + dateSearchField + " <= " + BusinessEntityUtils.getDateString(endDate, "'", "YMD", "-") + "))"
                + " AND ( UPPER(department.name) = '" + searchText.toUpperCase() + "'"
                + " OR UPPER(subContractedDepartment.name) = '" + searchText.toUpperCase() + "')"
                + " AND UPPER(jobStatusAndTracking.workProgress) <> 'CANCELLED'"
                + " AND UPPER(jobStatusAndTracking.workProgress) <> 'WITHDRAWN BY CLIENT'"
                + " GROUP BY job.sector"
                + " ORDER BY job.sector.name ASC";
        // now do search
        try {
            data = em.createQuery(searchQuery, DatePeriodJobReportColumnData.class).getResultList();
            if (data == null) {
                data = new ArrayList<DatePeriodJobReportColumnData>();
            }
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }

        return data;
    }

    public List<DatePeriodJobReportColumnData> jobReportByDatePeriod(
            EntityManager em,
            String searchText, // filled in with department's name
            Date startDate,
            Date endDate) {

        List<DatePeriodJobReportColumnData> data = null;
        String searchQuery = null;
        searchQuery =
                //                "SELECT NEW jm.com.dpbennett.utils.DatePeriodJobReportColumnData"
                //                + "("
                //                + "job"
                //                + ")"
                //                + " FROM Job job"
                //                + " JOIN job.jobStatusAndTracking jobStatusAndTracking"
                //                + " JOIN job.department department"
                //                + " JOIN job.subContractedDepartment subContractedDepartment"
                //                + " WHERE "
                //                + " (jobStatusAndTracking.dateSubmitted >= " + BusinessEntityUtils.getDateString(startDate, "'", "YMD", "-")
                //                + " AND jobStatusAndTracking.dateSubmitted <= " + BusinessEntityUtils.getDateString(endDate, "'", "YMD", "-") + ")"
                //                + " AND ( UPPER(department.name) = '" + searchText.toUpperCase() + "'"
                //                + " OR UPPER(subContractedDepartment.name) = '" + searchText.toUpperCase() + "')"
                //                + " AND jobStatusAndTracking.workProgress <> 'Cancelled'"
                //                + " AND jobStatusAndTracking.workProgress <> 'Withdrawn by client'"
                //                + " ORDER BY job.sector.name ASC";

                searchQuery =
                "SELECT NEW jm.com.dpbennett.utils.DatePeriodJobReportColumnData"
                + "("
                + "job"
                + ")"
                + " FROM Job job"
                + " JOIN job.jobStatusAndTracking jobStatusAndTracking"
                + " JOIN job.department department"
                + " JOIN job.subContractedDepartment subContractedDepartment"
                + " WHERE ((jobStatusAndTracking.dateOfCompletion >= " + BusinessEntityUtils.getDateString(startDate, "'", "YMD", "-")
                + " AND jobStatusAndTracking.dateOfCompletion <= " + BusinessEntityUtils.getDateString(endDate, "'", "YMD", "-") + ")"
                + " OR"
                + " (jobStatusAndTracking.dateSubmitted >= " + BusinessEntityUtils.getDateString(startDate, "'", "YMD", "-")
                + " AND jobStatusAndTracking.dateSubmitted <= " + BusinessEntityUtils.getDateString(endDate, "'", "YMD", "-") + ")"
                + " OR"
                + " (jobStatusAndTracking.expectedDateOfCompletion >= " + BusinessEntityUtils.getDateString(startDate, "'", "YMD", "-")
                + " AND jobStatusAndTracking.expectedDateOfCompletion <= " + BusinessEntityUtils.getDateString(endDate, "'", "YMD", "-") + "))"
                + " AND ( UPPER(department.name) = '" + searchText.toUpperCase() + "'"
                + " OR UPPER(subContractedDepartment.name) = '" + searchText.toUpperCase() + "')"
                + " AND UPPER(jobStatusAndTracking.workProgress) <> 'CANCELLED'"
                + " AND UPPER(jobStatusAndTracking.workProgress) <> 'WITHDRAWN BY CLIENT'"
                + " ORDER BY job.sector.name ASC";
        // now do search
        try {
            data = em.createQuery(searchQuery, DatePeriodJobReportColumnData.class).getResultList();
            if (data == null) {
                data = new ArrayList<DatePeriodJobReportColumnData>();
            }
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }

        return data;
    }

//    public List<Job> getJobsByPositionAndPageSize(
//            EntityManager em,
//            String query,
//            String searchText,
//            int startPos,
//            int pageSize) {
//
//        List<Job> filteredJobs;
//        List<Job> foundJobs = new ArrayList<Job>();
//        try {
//            filteredJobs = em.createQuery(query, Job.class).setFirstResult(startPos).setMaxResults(pageSize).getResultList();
//            if ((filteredJobs != null) && (!filteredJobs.isEmpty())) {
//                for (Job job : filteredJobs) {
//                    if (getJobState(job).toUpperCase().
//                            contains(searchText.trim().toUpperCase())) {
//                        foundJobs.add(job);
//                    }
//                }
//                return foundJobs;
//            }
//        } catch (Exception e) {
//            return null;
//        }
//
//        return null;
//    }
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
            List<CashPayment> cashPayments =
                    em.createQuery("SELECT c FROM CashPayment c "
                    + "WHERE c.jobId "
                    + "= '" + jobId + "'", CashPayment.class).getResultList();
            return cashPayments;
        } catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }

    public Boolean deleteCashPayment(EntityManager em, Long Id) {
        CashPayment cashPayment = null;

        cashPayment = em.find(CashPayment.class, Id);
        return deleteEntity(em, cashPayment);

    }

    public Long saveDepartment(EntityManager em, Department department) {
        return BusinessEntityUtils.saveBusinessEntity(em, department);
    }

    public Long saveEmployee(EntityManager em, Employee employee) {
        return BusinessEntityUtils.saveBusinessEntity(em, employee);
    }

    public EntityManager getEntityManager() {
        return EMF.createEntityManager();
    }

    public BusinessOffice getDefaultBusinessOffice(EntityManager em, String name) {
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

    public Employee getDefaultEmployee(EntityManager em, String firstName, String lastName) {
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

    public Department getDefaultDepartment(EntityManager em,
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

    public Manufacturer getDefaultManufacturer(EntityManager em,
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
    public Distributor getDefaultDistributor(EntityManager em,
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

    public PetrolCompany getDefaultPetrolCompany(EntityManager em,
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

    public Contact getDefaultContact(EntityManager em, String firstName, String lastName) {

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

    public void postJobManagerMail(
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

    public Manufacturer getValidManufacturer(EntityManager em, String name) {

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

    public void buildGasStationDatabase(
            EntityManager em,
            int sheetNumber,
            String company,
            String station,
            ArrayList<TestMeasure> measures) { // when set to null the each row on sheet is used as company branch. Otherwise each row is company.
        // cell indices
        final int NO = 0;
        final int PARISH = 1;
        final int STATION_NAME = 2;
        final int ADDRESS = 3;
        final int NUMBER_OF_PUMPS = 4;
        final int REJECTED_OR_NOT_WORKING = 5;
        final int CERTIFIED_ON = 6;
        final int EXPIRY_DATE = 7;
        final int STATUS = 8;
        final int RATE = 9;
        final int FINAL_AMOUNT = 10;
        final int NOZZLES_PER_PUMP = 2;
        int rowCount = 0;
        int cellCount = 0;
        PetrolCompany petrolCompany = new PetrolCompany();
        //
        try {
            POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream("GAS STATION DATABASE 2009.xls"));
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            em.getTransaction().begin();
            for (int i = 0; i < wb.getNumberOfSheets(); i++) {
                if (i == sheetNumber) {
                    HSSFSheet sheet = wb.getSheetAt(i);
                    petrolCompany.setName(company);
                    for (rowCount = 0; rowCount < sheet.getPhysicalNumberOfRows(); rowCount++) {
//                        for (HSSFRow row : sheet.g) {
                        if (rowCount > 0) { // make sure this is not the header
                            HSSFRow row = sheet.getRow(rowCount);
                            cellCount = 0;
                            // create new station
                            PetrolStation petrolStation = new PetrolStation();
                            if (row != null) {
                                for (cellCount = 0; cellCount < row.getPhysicalNumberOfCells(); cellCount++) {
                                    // populate petrol company and stations with data
                                    HSSFCell cell = row.getCell(cellCount);
                                    if (cell != null) {
                                        switch (cellCount) {
                                            case NO:
                                                break;
                                            case PARISH:
                                                if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
                                                    petrolStation.getBillingAddress().setStateOrProvince(cell.getStringCellValue());
                                                }
                                                break;
                                            case STATION_NAME: // add station only if name if valid
                                                if ((cell.getCellType() == HSSFCell.CELL_TYPE_STRING)
                                                        && (!cell.getRichStringCellValue().getString().trim().equals(""))) {
                                                    System.out.println("Importing: " + cell.getRichStringCellValue().getString());
                                                    petrolCompany.getPetrolStations().add(petrolStation);
                                                    petrolStation.setName(cell.getRichStringCellValue().getString());
                                                }
                                                break;
                                            case ADDRESS:
                                                if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
                                                    petrolStation.getBillingAddress().setAddressLine1(cell.getStringCellValue());
                                                }
                                                break;
                                            case NUMBER_OF_PUMPS:
                                                if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                                                    int numberOfNozzles = (int) cell.getNumericCellValue();
                                                    // create petrol pumps and nozzles. use max 2 nozzles per pump.
                                                    int numberOfPumps = (int) (numberOfNozzles / NOZZLES_PER_PUMP);
                                                    int oddNozzles = numberOfNozzles % NOZZLES_PER_PUMP;

                                                    if (numberOfPumps > 0) {
                                                        for (int j = 0; j < numberOfPumps; j++) {
                                                            PetrolPump petrolPump = new PetrolPump();
                                                            petrolStation.getPetrolPumps().add(petrolPump);
                                                            // create NOZZLES_PER_PUMP nozzles
                                                            for (int k = 0; k < NOZZLES_PER_PUMP; k++) {
                                                                PetrolPumpNozzle nozzle = new PetrolPumpNozzle(measures,
                                                                        getDefaultSeal(em, "--"),
                                                                        getDefaultSticker(em, "--"));
                                                                petrolPump.getNozzles().add(nozzle);
                                                            }
                                                        }
                                                        // create a pump with odd no. nozzles if any
                                                        if (oddNozzles > 0) {
                                                            PetrolPump petrolPump = new PetrolPump();
                                                            petrolStation.getPetrolPumps().add(petrolPump);
                                                            // create NOZZLES_PER_PUMP nozzles
                                                            petrolPump.setNozzles(new ArrayList<PetrolPumpNozzle>());
                                                            for (int k = 0; k < oddNozzles; k++) {
                                                                PetrolPumpNozzle nozzle = new PetrolPumpNozzle(measures,
                                                                        getDefaultSeal(em, "--"),
                                                                        getDefaultSticker(em, "--"));
                                                                petrolPump.getNozzles().add(nozzle);
                                                            }
                                                        }
                                                    } else if (oddNozzles > 0) {
                                                        PetrolPump petrolPump = new PetrolPump();
                                                        petrolStation.getPetrolPumps().add(petrolPump);
                                                        // create NOZZLES_PER_PUMP nozzles
                                                        petrolPump.setNozzles(new ArrayList<PetrolPumpNozzle>());
                                                        for (int k = 0; k < oddNozzles; k++) {
                                                            PetrolPumpNozzle nozzle = new PetrolPumpNozzle(measures,
                                                                    getDefaultSeal(em, "--"),
                                                                    getDefaultSticker(em, "--"));
                                                            petrolPump.getNozzles().add(nozzle);
                                                        }
                                                    } else {
                                                        System.out.println("Station has no pump!!");
                                                    }
                                                    System.out.println("No. of 2 nozzle pumps: " + numberOfPumps + ", Odd nozzles: " + oddNozzles);
                                                }
                                                break;
                                            case REJECTED_OR_NOT_WORKING:
                                                if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                                                    int nozzlesRejected = (int) cell.getNumericCellValue();
                                                    if (nozzlesRejected > 0) {
                                                        int pumpsToUpdate = (int) (nozzlesRejected / NOZZLES_PER_PUMP);
                                                        int oddNozzlesToUpdate = nozzlesRejected % NOZZLES_PER_PUMP;
                                                        // fill out all pump nozzles here
                                                        if (pumpsToUpdate > 0) {
                                                            for (int j = 0; j < pumpsToUpdate; j++) {
                                                                PetrolPump pertrolPump = petrolStation.getPetrolPumps().get(j);
                                                                for (PetrolPumpNozzle nozzle : pertrolPump.getNozzles()) {
                                                                    nozzle.setComments("Rejected/Not working");
                                                                }
                                                            }
                                                            // fill out the odd number pump nozzles
                                                            if (oddNozzlesToUpdate > 0) {
                                                                System.out.println("Bad nozzles: " + oddNozzlesToUpdate);
                                                                PetrolPump pertrolPump = petrolStation.getPetrolPumps().get(pumpsToUpdate);
                                                                for (int j = 0; j < oddNozzlesToUpdate; j++) {
                                                                    pertrolPump.getNozzles().get(j).setComments("Rejected/Not working");
                                                                }
                                                            }
                                                        } else if (oddNozzlesToUpdate > 0) {
                                                            System.out.println("Bad nozzles: " + oddNozzlesToUpdate);
                                                            PetrolPump pertrolPump = petrolStation.getPetrolPumps().get(0);
                                                            if (!pertrolPump.getNozzles().isEmpty()) {
                                                                for (int j = 0; j < oddNozzlesToUpdate; j++) {
                                                                    pertrolPump.getNozzles().get(j).setComments("Rejected/Not working");
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                                break;
                                            case CERTIFIED_ON:
                                                // set the date in all pumps for now
                                                if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                                                    Date certifiedOn = cell.getDateCellValue();
                                                    if (certifiedOn != null) {
                                                        int numberOfPumps = petrolStation.getPetrolPumps().size();
                                                        for (int j = 0; j < numberOfPumps; j++) {
                                                            PetrolPump petrolPump = petrolStation.getPetrolPumps().get(j);
                                                            petrolPump.getCertification().setDateIssued(certifiedOn);
                                                        }
                                                    }
                                                }
                                                break;
                                            case EXPIRY_DATE:
                                                // set the date in all pumps for now
                                                if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                                                    Date expiryDate = cell.getDateCellValue();
                                                    if (expiryDate != null) {
                                                        int numberOfPumps = petrolStation.getPetrolPumps().size();
                                                        for (int j = 0; j < numberOfPumps; j++) {
                                                            PetrolPump petrolPump = petrolStation.getPetrolPumps().get(j);
                                                            petrolPump.getCertification().setExpiryDate(expiryDate);
                                                        }
                                                    }
                                                }
                                                break;
                                            case STATUS:
                                                break;
                                            case RATE:
                                                break;
                                            case FINAL_AMOUNT:
                                                break;
                                            default:
                                                break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    // save the petrol company
                    System.out.println("Saving Petrol company: " + company);
                    BusinessEntityUtils.saveBusinessEntity(em, petrolCompany);
                    em.getTransaction().commit();
                    // stat
                    System.out.println("Number of stations imported: " + petrolCompany.getPetrolStations().size());

                    return;
                }
            }
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    public void insertStickers(EntityManager em, Date dateAssigned, Integer startNumber, Integer endNumber) {
        em.getTransaction().begin();
        for (int i = startNumber; i < endNumber + 1; i++) {
            System.out.println("Inserting sticker number: " + i);
            Sticker sticker = new Sticker(Integer.toString(i), dateAssigned);
            BusinessEntityUtils.saveBusinessEntity(em, sticker);
        }
        em.getTransaction().commit();
    }

    public FileInputStream createExcelJobReportFileInputStream(
            URL FileUrl,
            JobManagerUser user,
            Department reportingDepartment,
            DatePeriodJobReport jobSubCategoryReport,
            DatePeriodJobReport sectorReport,
            DatePeriodJobReport jobQuantitiesAndServicesReport) throws URISyntaxException {

        try {
            File file = new File(FileUrl.toURI());
            FileInputStream inp = new FileInputStream(file);
            int row = 0;

            // create workbook from input file
            POIFSFileSystem fileSystem = new POIFSFileSystem((FileInputStream) inp);
            HSSFWorkbook wb = new HSSFWorkbook(fileSystem);
            HSSFCellStyle dataCellStyle = wb.createCellStyle();
            HSSFCellStyle headerCellStyle = wb.createCellStyle();
            headerCellStyle.setFont(BusinessEntityUtils.createBoldFont(wb, (short) 14, HSSFColor.BLUE.index));
            HSSFCellStyle columnHeaderCellStyle = wb.createCellStyle();
            columnHeaderCellStyle.setFont(BusinessEntityUtils.createBoldFont(wb, (short) 12, HSSFColor.BLUE.index));

            // create temp file for output
            FileOutputStream out = new FileOutputStream("MonthlyReport" + user.getId() + ".xls");

            HSSFSheet costingSheet = wb.getSheet("Report");
            costingSheet.setActive(true);
            costingSheet.setForceFormulaRecalculation(true);

            HSSFSheet jobSheet = wb.getSheet("Statistics");
            if (jobSheet == null) {
                jobSheet = wb.createSheet("Statistics");
            }

            BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row++, 0,
                    "Job Statistics",
                    "java.lang.String", headerCellStyle);

            BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row++, 0,
                    reportingDepartment.getName(),
                    "java.lang.String", headerCellStyle);

            BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row++, 0,
                    BusinessEntityUtils.getDateInMediumDateFormat(new Date()),
                    "java.lang.String", headerCellStyle);

            BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row++, 0,
                    jobSubCategoryReport.getDatePeriod(0).getPeriodString(),
                    "java.lang.String", headerCellStyle);

            row++;
            // subcategory report
            if (jobSubCategoryReport != null) {
                BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row++, 0,
                        "EARNINGS",
                        "java.lang.String", headerCellStyle);

                row++;
                for (int i = 0; i < jobSubCategoryReport.getDatePeriods().length; i++) {
                    List<DatePeriodJobReportColumnData> reportColumnData = jobSubCategoryReport.getReportColumnData(jobSubCategoryReport.getDatePeriod(i).getName());
                    // insert table headings
                    BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row++, 0,
                            jobSubCategoryReport.getDatePeriod(i).toString(), "java.lang.String", columnHeaderCellStyle);
                    BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row, 0,
                            "Job subcategory", "java.lang.String", columnHeaderCellStyle);
                    BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row, 1,
                            "Earnings", "java.lang.String", columnHeaderCellStyle);
                    BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row++, 2,
                            "Calibrations/Tests", "java.lang.String", columnHeaderCellStyle);

                    // SUMMARY - earnings
                    for (DatePeriodJobReportColumnData data : reportColumnData) {
                        // subcategory
                        BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row, 0,
                                data.getJobSubCategory().getName(), "java.lang.String", dataCellStyle);
                        // cost
                        BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row, 1,
                                data.getJobCostingAndPayment().getFinalCost(), "java.lang.Double", dataCellStyle);
                        // test/cals
                        BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row++, 2,
                                data.getNoOfTestsOrCalibrations(), "java.lang.Integer", dataCellStyle);
                    }
                    ++row;
                }
            }

            if (sectorReport != null) {
                BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row++, 0,
                        "SECTORS SERVED",
                        "java.lang.String", headerCellStyle);

                row++;
                for (int i = 0; i < sectorReport.getDatePeriods().length; i++) {
                    List<DatePeriodJobReportColumnData> reportColumnData = sectorReport.getReportColumnData(sectorReport.getDatePeriod(i).getName());
                    // sector table headings
                    BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row++, 0,
                            sectorReport.getDatePeriod(i).toString(), "java.lang.String", columnHeaderCellStyle);
                    BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row, 0,
                            "Sector", "java.lang.String", columnHeaderCellStyle);

                    BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row++, 2,
                            "Calibrations/Tests", "java.lang.String", columnHeaderCellStyle);

                    // SECTOR - tests/cals
                    for (DatePeriodJobReportColumnData data : reportColumnData) {
                        // sector
                        BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row, 0,
                                data.getSector().getName(), "java.lang.String", dataCellStyle);
                        // test/cals
                        BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row++, 2,
                                data.getNoOfTestsOrCalibrations(), "java.lang.Integer", dataCellStyle);
                    }
                    ++row;
                }
            }

            if (jobQuantitiesAndServicesReport != null) {
                BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row++, 0,
                        "JOB QUANTITIES AND SERVICES",
                        "java.lang.String", headerCellStyle);

                row++;
                for (int i = 0; i < jobQuantitiesAndServicesReport.getDatePeriods().length; i++) {
                    List<DatePeriodJobReportColumnData> reportColumnData = jobQuantitiesAndServicesReport.getReportColumnData(jobQuantitiesAndServicesReport.getDatePeriod(i).getName());

                    BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row++, 0,
                            sectorReport.getDatePeriod(i).toString(), "java.lang.String", headerCellStyle);
                    // JobQuantities And Services table headings
                    List<JobReportItem> jobReportItems = jobQuantitiesAndServicesReport.getJobReportItems();
                    //System.out.println(jobReportItems);
                    for (JobReportItem jobReportItem : jobReportItems) {
                        BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row, 0,
                                jobReportItem.getName(),
                                "java.lang.String", dataCellStyle);

                        BusinessEntityUtils.setExcelCellValue(wb, jobSheet, row++, 1,
                                (Double) jobQuantitiesAndServicesReport.getReportItemValue(jobReportItem, jobQuantitiesAndServicesReport.getDatePeriod(i), reportColumnData),
                                "java.lang.Double", dataCellStyle);
                    }
                    row++;
                }
            }

            // write and save file for later use
            wb.write(out);
            out.close();

            return new FileInputStream("MonthlyReport" + user.getId() + ".xls");

        } catch (IOException ex) {
            System.out.println(ex);
        }

        return null;
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
        font.setFontHeightInPoints((short) 12);
        font.setFontName("Wingdings");

        return font;
    }

    Font getFont(HSSFWorkbook wb, String fontName) {
        Font font = wb.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setFontName(fontName);

        return font;
    }

    public FileInputStream createServiceContractExcelFileInputStream(
            EntityManager em,
            JobManagerUser user,
            Long jobId,
            URL url) {
        try {

            Job currentJob = Job.findJobById(em, jobId);
            File file = new File(url.toURI());

            FileInputStream inp = new FileInputStream(file);

            // create workbook from input file
            POIFSFileSystem fileSystem = new POIFSFileSystem((FileInputStream) inp);

            HSSFWorkbook wb = new HSSFWorkbook(fileSystem);
            HSSFCellStyle dataCellStyle = getDefaultCellStyle(wb);

            // create temp file for output
            FileOutputStream out = new FileOutputStream("ServiceContract-" + user.getId() + ".xls");

            // get service contract sheet
            HSSFSheet serviceContractSheet = wb.getSheet("ServiceContract");
            serviceContractSheet.setActive(true);
            serviceContractSheet.setForceFormulaRecalculation(true);

            // fill in job data
            // company name and address
            dataCellStyle.setBorderLeft((short) 2);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 4, 0,
                    currentJob.getClient().getName(),
                    "java.lang.String", dataCellStyle);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 5, 0,
                    currentJob.getClient().getBillingAddress().getAddressLine1(),
                    "java.lang.String", dataCellStyle);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 6, 0,
                    currentJob.getClient().getBillingAddress().getAddressLine2(),
                    "java.lang.String", dataCellStyle);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 7, 0,
                    currentJob.getClient().getBillingAddress().getStateOrProvince(),
                    "java.lang.String", dataCellStyle);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 8, 0,
                    currentJob.getClient().getBillingAddress().getCity(),
                    "java.lang.String", dataCellStyle);
            // contracting business office
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 4, 8,
                    currentJob.getBusinessOffice().getName(),
                    "java.lang.String", dataCellStyle);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 5, 8,
                    currentJob.getBusinessOffice().getAddress().getAddressLine1(),
                    "java.lang.String", dataCellStyle);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 6, 8,
                    currentJob.getBusinessOffice().getAddress().getAddressLine2(),
                    "java.lang.String", dataCellStyle);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 7, 8,
                    currentJob.getBusinessOffice().getAddress().getStateOrProvince(),
                    "java.lang.String", dataCellStyle);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 8, 8,
                    currentJob.getBusinessOffice().getAddress().getCity(),
                    "java.lang.String", dataCellStyle);

            // department use only
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
            dataCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            dataCellStyle.setBorderBottom((short) 1);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 4, 16,
                    currentJob.getDepartment().getName(),
                    "java.lang.String", dataCellStyle);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 5, 16,
                    currentJob.getJobNumber(),
                    "java.lang.String", dataCellStyle);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 6, 16,
                    currentJob.getJobStatusAndTracking().getEnteredBy().getFirstName() + " " + currentJob.getJobStatusAndTracking().getEnteredBy().getLastName(),
                    "java.lang.String", dataCellStyle);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 7, 16,
                    BusinessEntityUtils.getDateInMediumDateFormat(currentJob.getJobStatusAndTracking().getDateSubmitted()),
                    "java.lang.String", dataCellStyle);

            // contact person
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderLeft((short) 2);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 11, 0,
                    currentJob.getClient().getMainContact(),
                    "java.lang.String", dataCellStyle);

            // phone number
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 13, 0,
                    currentJob.getClient().getMainContact().getMainPhoneNumber().getLocalNumber(),
                    "java.lang.String", dataCellStyle);

            // fax number
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 13, 4,
                    currentJob.getClient().getMainContact().getMainFaxNumber().getLocalNumber(),
                    "java.lang.String", dataCellStyle);

            // fax number
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 13, 7,
                    currentJob.getClient().getMainContact().getInternet().getEmail1(),
                    "java.lang.String", dataCellStyle);

            // p.o number
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
            dataCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            dataCellStyle.setBorderBottom((short) 2);
            dataCellStyle.setBorderLeft((short) 2);
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 21, 0,
                    currentJob.getJobCostingAndPayment().getPurchaseOrderNumber(),
                    "java.lang.String", dataCellStyle);

            // date submitted
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 21, 3,
                    BusinessEntityUtils.getDateInMediumDateFormat(currentJob.getJobStatusAndTracking().getDateSubmitted()),
                    "java.lang.String", dataCellStyle);

            // estimated turn around time
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 21, 6,
                    currentJob.getEstimatedTurnAroundTimeInDays(),
                    "java.lang.String", dataCellStyle);

            // estimated cost
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 21, 8,
                    "J$" + currentJob.getJobCostingAndPayment().getEstimatedCost(),
                    "java.lang.String", dataCellStyle);

            // deposit
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 21, 11,
                    "J$" + currentJob.getJobCostingAndPayment().getDeposit(),
                    "java.lang.String", dataCellStyle);

            // receipt no(s)
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 21, 14,
                    currentJob.getJobCostingAndPayment().getReceiptNumber(),
                    "java.lang.String", dataCellStyle);

            // date deposit paid
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
            dataCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            dataCellStyle.setBorderBottom((short) 2);

            if (currentJob.getJobCostingAndPayment().getDepositDate() != null) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 21, 17,
                        BusinessEntityUtils.getDateInMediumDateFormat(currentJob.getJobCostingAndPayment().getDepositDate()),
                        "java.lang.String", dataCellStyle);
            }

            // service requested
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setFont(getWingdingsFont(wb));
            dataCellStyle.setAlignment(CellStyle.ALIGN_RIGHT);
            if (currentJob.getServiceContract().getServiceRequestedCalibration()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 23, 2,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }
            if (currentJob.getServiceContract().getServiceRequestedLabelEvaluation()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 23, 4,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }
            if (currentJob.getServiceContract().getServiceRequestedInspection()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 24, 0,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }
            if (currentJob.getServiceContract().getServiceRequestedConsultancy()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 24, 2,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }
            if (currentJob.getServiceContract().getServiceRequestedTraining()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 24, 4,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }
            if (currentJob.getServiceContract().getServiceRequestedOther()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 24, 6,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }

            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setFont(getWingdingsFont(wb));
            dataCellStyle.setAlignment(CellStyle.ALIGN_RIGHT);
            dataCellStyle.setBorderLeft((short) 2);
            if (currentJob.getServiceContract().getServiceRequestedTesting()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 23, 0,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }
            if (currentJob.getServiceContract().getServiceRequestedInspection()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 24, 0,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setAlignment(CellStyle.ALIGN_RIGHT);
            dataCellStyle.setBorderRight((short) 2);
            if ((currentJob.getServiceContract().getServiceRequestedOtherText() != null)
                    && (!currentJob.getServiceContract().getServiceRequestedOtherText().isEmpty())) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 24, 7,
                        "Other (" + currentJob.getServiceContract().getServiceRequestedOtherText() + ")",
                        "java.lang.String", dataCellStyle);
            }

            // details/sampleples
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setBorderBottom((short) 2);
            dataCellStyle.setBorderLeft((short) 2);
            dataCellStyle.setAlignment(CellStyle.ALIGN_LEFT);
            dataCellStyle.setVerticalAlignment(CellStyle.VERTICAL_TOP);
            dataCellStyle.setWrapText(true);
            String samplesDetail = "";
            int count = 0;
            for (JobSample jobSample : currentJob.getJobSamples()) {
                if (count == 0) {
                    samplesDetail = samplesDetail + "(" + jobSample.getReference() + ") " + jobSample.getDescription();
                } else {
                    samplesDetail = samplesDetail + ", (" + jobSample.getReference() + ") " + jobSample.getDescription();
                }
                ++count;
            }
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 26, 0,
                    samplesDetail,
                    "java.lang.String", dataCellStyle);


            // special instructions           
            BusinessEntityUtils.setExcelCellValue(
                    wb, serviceContractSheet, 25, 8,
                    currentJob.getServiceContract().getSpecialInstructions(),
                    "java.lang.String", dataCellStyle);

            // intended market
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setFont(getWingdingsFont(wb));
            dataCellStyle.setAlignment(CellStyle.ALIGN_RIGHT);
            dataCellStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
            dataCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

            if (currentJob.getServiceContract().getIntendedMarketLocal()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 49, 16,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }
            if (currentJob.getServiceContract().getIntendedMarketCaricom()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 49, 18,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }
            if (currentJob.getServiceContract().getIntendedMarketUK()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 49, 20,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }
            if (currentJob.getServiceContract().getIntendedMarketCanada()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 51, 18,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }
            if (currentJob.getServiceContract().getIntendedMarketOther()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 51, 20,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setFont(getWingdingsFont(wb));
            dataCellStyle.setAlignment(CellStyle.ALIGN_RIGHT);
            dataCellStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
            dataCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            dataCellStyle.setBorderLeft((short) 2);
            if (currentJob.getServiceContract().getIntendedMarketUSA()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 51, 16,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }

            // sample disposal
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setFont(getWingdingsFont(wb));
            dataCellStyle.setAlignment(CellStyle.ALIGN_RIGHT);
            dataCellStyle.setBorderLeft((short) 2);
            // Collected by the client within 30 days
            if (checkForSampleDisposalMethod(currentJob.getJobSamples(), 1)) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 49, 0,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }
            // Disposed of by the bsj
            if (checkForSampleDisposalMethod(currentJob.getJobSamples(), 2)) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 50, 0,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }

            // addservice requested
            dataCellStyle = getDefaultCellStyle(wb);
            dataCellStyle.setFont(getWingdingsFont(wb));
            dataCellStyle.setAlignment(CellStyle.ALIGN_RIGHT);
            dataCellStyle.setBorderLeft((short) 2);
            if (currentJob.getServiceContract().getAdditionalServiceUrgent()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 14, 11,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }

            if (currentJob.getServiceContract().getAdditionalServiceFaxResults()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 15, 11,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }

            if (currentJob.getServiceContract().getAdditionalServiceTelephonePresumptiveResults()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 16, 11,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }

            if (currentJob.getServiceContract().getAdditionalServiceSendMoreContractForms()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 17, 11,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }

            if (currentJob.getServiceContract().getAdditionalServiceOther()) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 18, 11,
                        "þ",
                        "java.lang.String", dataCellStyle);
            }

            dataCellStyle = getDefaultCellStyle(wb);
            Font font = getFont(wb, "Arial");
            font.setBoldweight(Font.BOLDWEIGHT_BOLD);
            dataCellStyle.setFont(font);
            if (currentJob.getServiceContract().getAdditionalServiceOtherText() != null) {
                BusinessEntityUtils.setExcelCellValue(
                        wb, serviceContractSheet, 18, 12,
                        "Other: " + currentJob.getServiceContract().getAdditionalServiceOtherText(),
                        "java.lang.String", dataCellStyle);
            }


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

    

    public void generateTestMonthlyReport(String datePeriodName) {

        // Setup reporting month and period
        DatePeriod reportingPeriod = new DatePeriod(datePeriodName, "month", null, null, false, false, true);
        DatePeriod datePeriods[] = BusinessEntityUtils.getMonthlyReportDatePeriods(reportingPeriod);

        //JobManagement jm = new JobManagement();

        try {

            DatePeriodJobReport jobSubCategoryReport;
            DatePeriodJobReport sectorReport;
            DatePeriodJobReport jobQuantitiesAndServicesReport;

            EntityManager em = BusinessEntityUtils.getEntityManager("EntitiesPU");
            JobManagerUser user = JobManagerUser.findJobManagerUserByUsername(em, "dbennett");
            Department reportingDepartment = Department.findDepartmentById(em, 20L); // eletrical dept id = 20
            List<JobSubCategory> subCategories = JobSubCategory.findAllJobSubCategoriesGroupedByEarningsByDepartment(em, reportingDepartment);
            List<Sector> sectors = Sector.findAllSectorsByDeparment(em, reportingDepartment);
            List<JobReportItem> jobReportItems = JobReportItem.findAllJobReportItemsByDeparment(em, reportingDepartment);

            //             reports
            jobSubCategoryReport = new DatePeriodJobReport(reportingDepartment, subCategories, null, null, datePeriods);
            sectorReport = new DatePeriodJobReport(reportingDepartment, null, sectors, null, datePeriods);
            jobQuantitiesAndServicesReport = new DatePeriodJobReport(reportingDepartment, null, null, jobReportItems, datePeriods);

            // populate SubCategoryReport/Sector/report items
            if (em != null) {
                for (int i = 0; i < datePeriods.length; i++) {
                    //                     job subcat report
                    List<DatePeriodJobReportColumnData> data = jobSubCategogyGroupReportByDatePeriod(em,
                            "dateOfCompletion", // dateOfCompletion
                            jobSubCategoryReport.getReportingDepartment().getName(),
                            datePeriods[i].getStartDate(),
                            datePeriods[i].getEndDate());
                    jobSubCategoryReport.updateSubCategoriesReportColumnData(datePeriods[i].getName(), data);
                }

                for (int i = 0; i < datePeriods.length; i++) {
                    // sector report
                    List<DatePeriodJobReportColumnData> data = sectorReportByDatePeriod(em,
                            "dateOfCompletion",
                            sectorReport.getReportingDepartment().getName(),
                            datePeriods[i].getStartDate(),
                            datePeriods[i].getEndDate());
                    sectorReport.updateSectorsReportColumnData(datePeriods[i].getName(), data);
                }

                for (int i = 0; i < datePeriods.length; i++) {
                    // job report
                    List<DatePeriodJobReportColumnData> data = jobReportByDatePeriod(em,
                            jobQuantitiesAndServicesReport.getReportingDepartment().getName(),
                            datePeriods[i].getStartDate(),
                            datePeriods[i].getEndDate());

                    jobQuantitiesAndServicesReport.setReportColumnData(datePeriods[i].getName(), data);
                }

                //   generate report
                createExcelJobReportFileInputStream(
                        getClass().getResource("MonthlyReport.xls"),
                        user, reportingDepartment, jobSubCategoryReport, sectorReport, jobQuantitiesAndServicesReport);
                System.out.println("Report generated!");
            }

        } catch (Exception ex) {
            System.out.println(ex);
        }
    }

    public void updateAllJobs(EntityManager em) {
        long jobsUpdated = 0;

        try {
            System.out.println("Getting all jobs...");
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

    /**
     * Get report based on JasperReport compiled report
     *
     * @return
     */
    public FileInputStream getJasperReportFileInputStream(EntityManager em) {

        FileInputStream stream = null;
        HashMap parameters = new HashMap();

        try {

            // load the required JDBC driver and create the connection
            // tk

            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://boshrmapp/jmts",
                    "root",
                    "bsj0001");

            // set parameters and retrieve report
            parameters.put("jobId", 352L); // $P{jobId}
            JasperPrint print = JasperFillManager.fillReport("C:\\Projects\\JobManagementAndTracking\\JMTSWebAppBetaV2\\reports\\Foods Dept Lab Requisition.jasper", parameters, con);

            JRExporter exporter = new JRPdfExporter();
            // Configure the exporter (set output file name and print object)
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, "Foods Dept Lab Requisition.pdf");
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);

            // Export the PDF file
            exporter.exportReport();
//
//
//            // prepare exported file for transmission to the client
//            stream = new FileInputStream("form.pdf");
            //jobReportFile = new DefaultStreamedContent(stream, mimeType, fileToExportTo);

        } catch (Exception e) {
            System.out.println(e);
            //longProcessProgress = 100;
        }

        return stream;
    }

    /**
     * Get report using a JasperReport compiled report
     *
     * @return
     */
    public FileInputStream getJasperReportFileInputStream(
            String databaseDriverClass,
            String databaseURL,
            String username,
            String password,
            String jasperReportFileName,
            String exportedReportType,
            HashMap parameters) {

        try {
            // Declare init default reporter
            JRExporter exporter = new JRPdfExporter();

            // Load the required JDBC driver and create the connection
            Class.forName(databaseDriverClass);
            Connection con = DriverManager.getConnection(databaseURL,
                    username,
                    password);

            // Fill report and export it
            JasperPrint print = JasperFillManager.fillReport(jasperReportFileName, parameters, con);

            // Get reporter
            if (exportedReportType.equalsIgnoreCase("HTML")) {
                exporter = new JRHtmlExporter();
            }

            // Configure the exporter (set output file name and print object)
            exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, "file.pdf");
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
            exporter.exportReport();
            // prepare exported file for transmission to the client
            return new FileInputStream("file.pdf");


        } catch (Exception e) {
            System.out.println(e);
        }

        return null;
    }

    public void copyAllEntitiesToDatabase(String destPU, List<BusinessEntity> entities) {
        Integer numEntities = 0;

        try {

            EntityManager em = BusinessEntityUtils.getEntityManager(destPU);


            for (BusinessEntity entity : entities) {
                ++numEntities;
                System.out.println("Copying: " + entity + " : " + numEntities);
                em.getTransaction().begin();
                BusinessEntityUtils.saveBusinessEntity(em, entity);
                em.getTransaction().commit();
            }
            System.out.println("Saving/updating " + numEntities + " entities...");

            System.out.println("Done!");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) {
        try {
            //JobManagement jm = new JobManagement();

            // JMTSTestBOSHRMAPPMySQLPU, EntitiesBOSHRMAPPMySQLPU, EntitiesPU
            EntityManager em = BusinessEntityUtils.getEntityManager("JMTSTestBOSHRMAPPMySQLPU");
            if (em != null) {
                //em.getTransaction().begin();                
                ComplianceSurvey e = new ComplianceSurvey();
                em.persist(e);
                //em.getTransaction().commit();

            } else {
                System.out.println("Not connected to database!");
            }


        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
}
