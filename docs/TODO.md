# Things to do
## Database update
- Add required fields to JMUser and Privilege tables for all databases.
- Add MODULES_ID to JMUsers and modules table to database. 

## Inception Report and Misc
- Get procedure documents from QEMS of the BSJ, NCRA and NCBJ
- Provide content and format report based on BABOK if possible.
- Get requirements for Food Dept.

## Testing, Training & Misc
- Deal with saving user details when the user is already logged on.
  * Save user immediately when module is selected in pref. dialog??
    Check why it doesn't work otherwise.
  * Retire use of Units in JMUser and create Module class instead. Use the term
    Module and not Unit where applicable.
  * Rename "canAccess?Unit" to "canAccess?Module"
  * Test adding new user.
  * Add "dirty" field to privilege and use when saving user.
  * Load user record from database when opening preference dialog.
  * Add event listener for all privilege fields.
- Preference dialog seems to set the job dirty. Fix that.
- Impl adding/removing dashbaord tab based on preference selection
- Impl dashboard (update live and test databases:
  * Add Industrial Training/Customer Service Unit.
  * If access privilege is unchecked then "turn off" the corresponding unit.
  * Add field (legalOfficeUnit) for Legal Office to class and database.
  * Modify dashboard and user menu to display tabs and menu items based on
    privileges and selection.
  * Move unit privileges to "Modules" tab.
- Financial admin tab needs new button and search text field.
- Change "Jobs" tab title to "Job Browser".
- Look out for the changing of @ManagedBean to @Named where this is needed if
  things do not work as expected.
- Implement updating the jobs table loading a job from the table.
- Redesign MainTabView and MainTab classes to use "p:repeat" and "jobsViewTab"
  with multiple tables.
- Externaldialogs take a long while to load. Check why? 
  * Note first that objects are not being loaded in the dialogs.
  * Test loading a simple dialog that does not access the database  
- Check out the call financeManager.setEnableOnlyPaymentEditing(false/true);
  and see if it is still relevant.
- "Handle keep alive" does not seem to be working. Fix. UseIdleMonitor and not the
   "keepalive" method. Log off user if system is not used after timeout.
- Remove "Job Detail" tab and fully implement Job Dialog (eg new job and edit job 
  and job costing and all other job views that are generated from the various job.
- Impl "Organization" in JM user so that the user' correct organization can be
  selected when subcontracting a job.
  * Impl departmentPickListDialog. Check why Test Dept2. does not appear in the list.
  * Impl businessConverter/Validator. 
  * Add active/head fields to Business class
- Start drafting "inception" report and workplan. See meeting notes.
- Test Edit exiting and create new clients, contacts and addresses.
- Test out creation and use of costing templates.
- Test Export service contract and job costing for old jobs that do not have billing 
  address and contact fields set.
- Test export all reports in live system.
- Take job through completion to see if "Job completed by:" checkbox is actually checked.
- Test that the restriction to create jobs for one's own department actually works.
- Test adding payment with discount.
- Doc requirement in inception report. Job should not be marked completed until
  approved.
- Allow system to allow copying and pasting into text field and record field
  as being edited.

## Misc & Reports
- Add privileges to add/edit contact/address and put client privileges in own tab.
- Add CRM and "Corporate Office" as modules.
- Change context.execute to PrimeFaces.executeScript.
- Add and MySQL 5.1.45 connector/j to glassfish and LEGALOFFICEUNIT to JMUser 
  in all databases.
- Add representatives field to Job class, database and form.
- Add divisions to 
- Add all new unit access privileges to class and database.
- Setup jmts-processes as Activiti explorer for JMTS. Use jmts/jmtstest as the
  databases.
- Add general preference to hide/show dashboard.
- Add Standards and Certification as separate units in all databases.
- Integrate Activiti with BSJ LDAP (AD).
- Look back at saveCurrentJob() to see em.refresh(savedJob); and why it throws
  an exception.
- Allow assigning job to more than one assignee.
- Add "closeable" option to PrimeFacesUtils.openDialog().
- Check if the currently opened job was saved since it was opened or last saved
   by the user and inform and take appropriate action if the user tries to save.
   Test with 2 users logged in with different browsers.
- Get and use activiti for process documentation.
- Don't allow Comments/Description fields to grow in height when text is entered
  as is done in the System Option Detail dialog.
- Show date of activity for user in user profiles table.
- business_department table to be added to database table if it is automatically
  added during deployment.
- Check service contract comes out good on live jmts.
- Impl isDirty in all business entities.
- When an item in a table is edited before it is saved, it reappears in the table
  because its Id is null so it is added again in the list. This happens for 
  cost components, client address and contacts etc. Fix this by checking is the
  item is already in the list before adding it.
- Check that user info in the users table do not blank out when user dialog is
  oked or cancelled.
- Standardize and make all field that are disabled or uneditable have yellow
  background. Use readOnly attribute instead of disabled wherever this is possible.
- The "Active:" label is not linked to the checkbox in the client dialog.
- Prevent changing adding/editing cost components once costing is approved/invoiced.
- Show the "authenticate" flag in the users table.
- Let client search in clients tab search any part of client's name.
- Get all Parish/State/Province to show in client dialog.
- Put "Reports" configuration in Sys Admin Configuration tab.
- Add default fields for department etc. and add field to allow disabling the changing of a field.
- Make relevant fields autocomplete.
- Add description, 2 departments, 3 date periods etc. in class Report.
- Implement and include report templates for all reports generated to date.
- For reports make company specific value parameters such as company name and logo.
- Check for reports/queries on jobs that do not have any samples. Check for example that these jobs are counted despite not have samples.
- Add system option to display "Handling keep alive session..." in debugging mode.
- Remove impl of Converter from all entities.
- Relook at the whole sequence of events when job costing parameters such as
  tax, tolal cost, estimate etc are edited.
- Rename privilege table to privileges if it does not result in data lost.
- Do not save job also when an object in the Job class is edited if this is not
- Create the LegalDepartmentPortal as a git branch of the JMTS.
- Impl real canceling of payment edits as is done with job samples.
- Replace organizationName system option use with "Internal" Business class 
  when this feature is implemented
- Do presentation for BSJ MIS staff to outline the current status of the JMTS 
  and the way forward.
- Follow the BSJ 6 month proposal and do an assessment of the ISO procedures, 
  work instructions, procedures, audit reports etc. before starting development. 
  This may take at least a month. Study the proposal and layout a plan of action before starting.
- Get rid of use of "--" to determine if a job is sub contracted cause selecting
  it "de-subcontracts" a job and turn it into a parent job.
- Note: Units are intrinsic parts of the JMTS such as JM, sys and financial admin. 
  Modules are additional features such legal documents and standard compliance. 
  Delete other units from the system such as legalmetrology??
- Add "identification" and "identificationType" to Client class.
- Impl finding classifications by category and use it when finding job classifications
- Order PhoneNumber, Internet as is done with address and contact.
- When loading table records from System Admin such as user etc 
  load the database record before editing.
- Do not allow changing the department to which a job is saved except by sys admin.
- For all the costing dates, determine what should be the "Costing Date" or the 
  date that the costing is deemed to be completed.
- Check that the getting of a new job sequence number and the saving of a job
  is done in one transaction.
- Remove department from employee dialog and start using department from JM user
  whenever the user's department is needed.
- Add head and active fields to Business class and table.
Impl preventing job costing and payment from being edited once job is marked as complete.
Impl option to use LADP or some other authentication system.
Impl dialogs for all admin configuration eg. classification etc. and put edit button in all tables.
Convert job sample deletion dialog back to a element and in jobSamplesTab.xhtml so that growl messages can be displayed when samples are deleted.
Do not allow the creation of entities such as employee with the same name or warn if this is to be done.
Return costing templates with unique names and don't allow saving templates with same name.
Impl new department button/feature. Use new employee as template code.
Report things such as failed logins and login time and date in the user.activity field.
- Prevent job costing from being edited once it is approved and not when the job is marked completed.
Create Tracking/OperationsManager and use to manage job status and tracking
Put the job number etc in the job detail tab title based on the tabs being shown eg. if it's only job costing and payment being shown then state that.
Implement sub-sectors by adding a collections field in the sector class.
Pass in the job only and not the contact and billing address to the Client Manager?
Impl Job Numbering/Sequencing interface that can be implemented for various organizations including the one used for the BSJ.
Put "Action" menu in client dialog to allow editing or creating new address/contact
Add "Feedback" feature to allow user to report bugs, issues, feature request, contact developer etc.
Impl "Actions" menu in jobs table that has "Edit", "Copy", "Subcontract". The privileges should be checked before doing so.
If a new job is being created and the currently opened job is unclean then prompt to save it.
Impl multiple selection in client tab so that the selected clients can be activated or deactivated fro example.
Create Modules tab in user and department profiles to allow sys admin to control the modules that a user/department can access.
Indicate in the tab title if a form such as the Job Detail form has been edited.
Impl copy and delete sample within the sample dialog.
Use tooltip component to get consistent tooltip across the app.
User an "iterative" JSF component to implement the display of tabs and use collections to initialize the tabs.
Add chat feature. Allow person to add their image to their user profile.
Some report templates are given in system options while others are given in Report table. Given all of them in Report table and delete the system options over time.
Do shallow copy of client, billing address and contact before saving job to ensure that future edits of the client's name, billing address and contact do not affect the job record?
get(index) is used to get the main phone and fax numbers for a contact. This method may result in the returning of the right number for fax or phone so use the type field to solve this.
Consider jmts.cloud, .co, .online as domains.
Create netbeans module for installing the JMTS and adding modules.
Implement searching for users that are not authenticated.
Print service contract for only external client jobs??
Upgrade to Primefaces 6.x to take advantage of responsive design, new features and components.
Use to add favicon when one is designed.
NB: JobManagerUser represents a user profile that has a Business, department, employee and privileges assigned. Departments also have privileges and the user privileges along with the user privileges are used to determine the effective privileges of the user.
Add department to user dialog.
Sort out all that needs to be "locked" once a job's status is marked as complete. Display a warning before the work progress is set to complete.
Make TRN mandatory? Put system option to validate?
Reset all UI when log out including searches results so that if a new user logs in they will not see the searches of the previous user.
Try to put all styles as styleClass and put into index.css?
Look at Garfield's design images on google docs for ideas.
Design a favicon and make it system option.
Display message when user logs on without authentication
- Assign git tag to next release.
Export sample condition(s) to service contract?
Restrict generation of service contract to an authorized person/department?
Put client ID# (TRN etc.) into service contract?
- Implement a system to alert users if they have the same job opened.
- Put button in service contract tab to generate service contract.
- Create autocomplete list of "standard/common" product names using a "Distinct" 
  type of the query on the existing samples.
- Add parentSector to Sector class and add Sub-sector to Groupings tab. 
  Sub-sectors would be those that have a parent sector.
- Add descriptions of grouping items to in "Groupings" tab.
- For jasper reports fill in parameters such as images from configuration values 
  stored in the database.
- Impl job backup as is done with samples and job edit canceling and change 
  "close" button to "Cancel" button.
- Change label Department* to Parent department* when job is subcontracted.
- Check why GCT field shows yellow although it can be changed.
- Add user/department privilege to add payments.
Add search fields and dialogs where they don't exist. Impl find* methods that take "active" as argument and add checkbox to show/hide active objects.
Put Application class in BEL for reuse by managers and other code?
Call JM init() to reset search results etc when user logs out.
Remove access to JobManager from SearchManager and implement access to it to similar to how it's done with ClientManager.
Show message to the user if they login without being authenticated.
Check that if "authenticate" is unchecked it does not allow the login of other users with "authenticate"checked!!
Impl adding new country if from within the respective dialog if the country is not in the list. Only user with privilege should be able to do this. Do not allow adding country if it already exist.
Add feature to activate modules/units for users.
Use a toolbar at the top of search results on all tabs with search text box, buttons and search result table as is done with the jobs tab.
Use parentJob to link contracts with parent jobs and use it to pull in subcontracts costs?
Impl canceling "Saving and Canceling" in all dialogs instead "Saving and Closing". Use the backup and restore method used for samples.
Add Business to JobManagerUser. Add blob to Business for storing company logo.
Put text box in jobsTab to allow quick filter of search results.
Refactor and rename static methods from findEntityName*() to just find*() where possible.
Impl converters/validators to only use active entities when ever possible.
Move complete*() methods where possible from managers to Application.
Limit the maximum characters to be entered into a text field to 50. Put this value in the BEL resource bundle which can be changed. Allow various maximums for different types of fields.
Impl. number validator that accepts a minimum value such as 1. This can be used in the sample dialog.
Replace dialog messages that have only "ok" button with growl messages.
Make all sensitive fields, especially those on the General tab unchangeable except by system admin after a job has been saved. Use field groups with input and output fields as is done with the job number.
Check if new clients can be created even without privilege.
For job search let My Department job be the default.
Create separate login page instead of using login dialog?
Create dialogs for classification instead of just adding a row in the table.
Revisit admin dialogs and fix autocomplete scroll height, converters and validators. Ensure the prevention of creating duplicate objects such as employees with the same name.
Once job is saved, prevent changing of all fields that affect the service contract?
Do copy of billing address, contact and client before saving new job. Do not allow changing these fields except by sysadmin. Implement doShallowCopy() for client that does not copy the list of addresses and contacts.
Make "Keep Alive" display a system option that is false by default.
Use only one toolarbar for reportsTab. Do update of entire tab instead of specific components.
Disable other rendered tabs when the job detail tab is rendered to prevent switching to those tabs??
Impl copy of costing component from components table.
Add privileges for adding/editing contacts and addresses.
Impl the max character lengths as system options an set limits on the maximum dollar values that can be entered as system options too. Consider the maximum double values imposed by Java the possibility that values can overflow when added divided or multiplied.
Prevent changing a job's parent department once it is has subcontracts.
Put cost component type in job costing analysis to show if costing is fixed variable etc. or put n/a in the rate and hours/unit column when the cost component type is fixed or subcontract.
Check that the privileges set for user and department are impl properly.
Implement search other fields such as descriptions for sysadmin objects.
Look serioulsy at the maximum integer and double values that can be represented by Java and take these into consideration when working with currency and integer values.
Disable fields such as client, department etc. once the service contract has been exported or job has been subcontracted. Add a field in the Job class for this and allow only customer service, sysadmin or other designates to change it.
Change label for department to "Parent department" when a job is subcontracted.
Get rid of use of "--" for default objects.
Let users tab filter active users, users that are authenticated and users that have recent activities.
Create tax table and use it similar to gnucash to apply taxes to costs on a departmental basis.
Remove the colons from field labels?
Put toolbar in service contract tab to export service contract, uncheck as as exported, email service contract etc.
In status and tracking add tracking table to show all activities performed on the job. Add section that allows added new action/activity such job completion. See design layouts provide by G. Allen.
Show the list of privileges a user has in the preference dialog. Copy the UI from sysadmin and make it read only...put them in a separate file and use to bring them in.
Impl use of camera to take pics of samples etc.
- Add invoiceDate to JobStatusAndTracking class and database.
- Check if cost component id is null and use it when adding new CC instead of
  using addCostComponent variable.
- Put methods such as approveJobCosting() in their associated class such as JCP 
  so they can be reused by web services developed independently of the JMTS by using
  the BEL.
- See if github api (if it exists) can be used to implement feedback system.
- Make currency symbol a system option.
- Impl real canceling of job costing edits as is done for samples cause when the 
  job costing dialog is opened and not edited and canceled a message says it was edited.
- In CashPayment replace payeeTitle, payeeFirstname and payeeLastname with "Contact payment"
  which would be part of the client associated with the job.
- Add feature to add picture to job sample record by using the camera attached to the system.
- Add system option to hide/show Job tabs and fields eg Samples tab could be hidden for a
  company that doesn't use it.
- See if the opened tab can be selected when the Job Detail lab is automatically
  closed.
- See if system can run on Payara mirco.
- Depreciate the "deposit" and subsequently remove the field from the JCP class
  and database. May have to create a cash payment from the deposit field fist.
- Add: country to Address dialog?
- Set tab to "Job Detail (Costing and Payment) when cashier view is selected). 
  Do similar for other job views.
- Note that TIMESTAMP does not work with the current version of MySQL used by 
  BSJ.
- Use <ui:repeat to display dashboard and maintab tabs? 
- Add TODO to remove auto-boxing/unauto-boxing where it is not necessary.
- Adopt the use of "Release Notes" to announce new JMTS releases.
- Paste the text directly into a CODE_OF_CONDUCT file in your repository. 
  Keep the file in your project’s root directory so it’s easy to find, and link 
  to it from your README.
- Let the subcontracted department be null for parent job and change the job numbering
  system and other business logic to reflect this fact. The use of "--" department
  should then be removed.
- Impl sending alerts to all or specific persons. The messages would be displayed
  when the use just logs on or popup if the user is already logged on.

Legal Documents/Office Module

Fix up entity classes by using ALL annotations for "persistenc commit" in LegalDocument class etc.
Watch out for dateselect and keyup events.
Fix issues where classifications are blank. Create new ones if necessary.
Remove trim from all find* methods

Standards Module

Standards development processs
WTO notification database
JS and ISBN numbers – automate number creation
IEC/ISO standards commenting/tracking
CROSQ – standards databases
Earnings database
Conformity assessment/certification
Registry of companies
Audit schedule
CMP database – companies certified and other status
Registry of all companies registered (see spreadsheets)
QEMS
Technical committees database
Technical regulations/standards list/database/catalogue
Marketing Requirements for Trade

Legal Metrology

Check if all close buttons work in history dialogs.
Impl importing petrol stations from spreadsheet: add pump/nozzle certification to petrol station and included expiry date to certification.
Optionally leave out date period search for petrol stations for example to see how if it affects search speed.
Change LegalMetrologyManager to LegalMetrologyTester in on PetroPumpNozzle test app.
Create LegalMetrologyClient with list of entities such as petrol stations and scales and other products.
Set a one month lead time when for reminders about due date for equipment test/calibration. .
Lpg checking: database to be developed. Details to be obtained later.
Package checking database to be done.
Storage tank database. Tank strapping and volumetric.
Roller weights calibration database. See Calibration Report for roller weights
Option to show/hide search form when logged in and provide basic search if search form (advanced search) is hidden.
Database for high volume meters from petrojam to be done.
Application form for calibration of tankerwagon/scales etc. – put online? Get new form. Form should be validated for required fields before saving/printing.
Client certificates should be automatically generated?
Update seal,sticker,petrolpumpnozzle tables in boshrmapp.
Create new sticker/seal record only if the current number entered is unique.
Implement finding an existing job assigned to the current client in job number dialog. updateJob(SelectEvent event) approach to be looked into to see if it works.
Do job number assignment in gas pump test tab? Do not allow saving test results until job number is assigned or if the assigned job number is for a job that is completed.
Create table for petrol types (diesel etc.)
Allow entering the measure used for a pump/nozzle. Make use of the measures string in nozzles to compute pass or fail and header in the table. Opportune
Change back date recorded to time stamp. Find out how to make the database assign the date and time stamp.
Impl calculating actual petrol usage by bsj using error readings.
Do report on pass/reject in test data table or separate table.
For each job done the value of the job should be subtracted from the deposit made by a gas station.
Change seal and sticker dates to time stamp.
Impl adding pumps as samples to a pump test job.
Impl random testing feature in schedule.
Create new calibration if the date of the last calibration if different from the current date.
Create new calibration for a pump if the date is not the current date or the seal has changed???
Create filtering that shows stations due based on time such as week, 2 weeks, one month etc.
Create sample report:
Setup to do importation of data. Use POI. (i) merge independent with other and enter each station as a separate company (ii)implement random selection of pump/nozzle to be tested (iii) keep history calibration information for each pump. (iv) Set number cells that are blank to 0 before doing importation.
how is random scale testing done:
Check out petrol sampling log and testing. How is it done and where is the report.
Impl adding/editing classification elements (eg sector) in job entry form or department form. Only persons with the required privilege should do this. Do this in SysAdmin??
Create table views for employee, department, user in first release.
Try to put common libs such as primefaces and poi on the application server instead of packaging them as part of the application to reduce its size. Uncheck the lib in the compile-time libraries to do this.
Check for duplicate saving of objects that belong to lists. Start with legalmet.
Impl searching by trn where applicable.
Put dialogs inside of the main forms.
Prepare project database/features list for each database software.
Procedure for adding an entity class as a field to another entity class is: (1) Create and add new entity to the persistence file (2) Add reference to the entity to the parent class (3) Add the entity field to the table of the parent class in the form ENTITYNAME_ID, selecting the index option(4) use ALTER TABLE JOB DROP [FIELDNAME] to drop fields that were moved to new table/class. Other commands found at: http://db.apache.org/derby/docs/dev/ref/rrefsqlj81859.html
Impl save without closing the box for relevant dialog boxes.
Check out ISO/IEC 27001 (information security).
Impl detecting compatibility mode for IE.
Admin: use jasypt lib as a method for password encryption and authentication.
Impl adding stickers to database: Oct 20, 2011 (29601 – 30600) to Jody-Ann Burke.
See notes on inspection form: do regs 1-4, sampling inspection type, tie in sample collection form.
Impl. track registration from time of request/application to completion: see Registration Tracking Sheet (Revised).xlsx: Fields to add to Registration class: assignedInspector, dateAssignedToInspector, assigned etc.
Compliance db: Impl alert 8 weeks before registration due via popup or email to inspector and other relevant personnel eg supervisor.
Standards db: Impl closing using “x” on standard dialog box.
Standards db: Impl “DateTrack?” class that has a type, date, name etc and use it to for recording published, revised dates etc.
Create table or configuration that gives enfo
cement types for a standard eg. Mandatory, Voluntary etc.??

Monthly Report

• # of weighing and measuring devices (what are the devices?) were targeted for 
verifications during the period under review (break out the devices into various 
categories and state the numbers for each category). 
Do not include # of calibrations in the number of verifications (state the difference). 
• # of premises visited for the purposes of testing and verifying weighing and measuring devices. 
• # of weighing and measuring devices that were tested and verified during the period under review. 
• % of those tested and verified that were in compliance with related metrological requirements? 
• # of investigations of suspected inaccurate measurements were conducted? 
(it is assumed that these investigations were as a result of complaints received 
from consumers and businesses who believed that they had received inaccurate measurements) 
• Types of weighing and measuring devices were these investigations conducted 
and the results of the investigations. 
• # of gas pump stickers that were issued during this financial year. 
# issued during the corresponding period last financial year.

Foods Inspectorate

- Itinerary generation (based on operations research).
- Look at the data required to be captured and to design database
- Look at the fields in the FOOD ESTABLISHMENT INSPECTION REPORT to determine 
  requirements.
- See notes on inspection form: do regs 1-4, sampling inspection type, tie in sample collection form.
- Implement track registration from time of request/application to completion: 
see Registration Tracking Sheet (Revised).xlsx: Fields to add to 
Registration class: assignedInspector, dateAssignedToInspector, assigned etc.
- Implement alert 8 weeks before registration due via popup or email to inspector 
and other relevant personnel eg supervisor.
- Implement tracking of application for certificates.
- Lock form upon client signature is affixed to the food factory report.
- System to generate certificates for client.
- Add HACCP recognition programme audit tab in the food inspection form.
- Associate a factory with a client by adding a Client class to the factory class.
- Move pages to web folder under project folder and stop using webpages folder.
- Save inspection form when tab is closed?
- Factory inspection office should default to that of the officer doing the 
inspection if it is not null. Should default to head office if null.
- Find a different solution than using “currentFoodFactory.getAllBusinessEntitiesLists()” 
to save new items added to a list. See how it’s done in ComplianceConnect for example.
- Check if food inspection forms have been merged.
- Develop calendar view for itinerary that shows the planned activity for inspectors.

Status & Tracking

Status note field needs to be locked and a button be used to add updates to it. These updates would be added as new line (i.e. \n). And updates can therefore only be added but not deleted.
Remove dateCostingCompleted, dateCostingApproved, depositDate, dateOfLastPayment and costingDate from where they should not because they are all in jobStatusAndTracking. Make sure they are not being used first.
Steps to be tracked: (i) dispatch date to client (ii) delivery of report/sample to customer service (iii) assignment of job by team leader (iv) date customer was contacted (v) date email sent to client when job is completed. (vi) re-issue of report.

Accpac Integration

Add option to change days to add to invoice date to get invoice due date.
Accpac invoice due date = DUEDATE(DOCDATE) + 15 days (for example). 1 to x days the invoice should be marked as current. PI is code for pre-payment and IN is for invoice.
For credit status show invoice due date and not invoice date?

Standard Compliance Module
General

Fix up reporting.
For Daily Report put the client in the Consignments Inspected table.
Error occurs when product is added, survey dialog is closed and survey dialog reopened. Could be because product is null. Save all products with null id when saving survey?
Add date of survey to compliance connect search in jmts.
Add search params for compliance survey
Ask about saving survey when product is added?
When product deletion is cancelled refresh dialog for compliance survey

Issues

In compliance survey remove job number field and put JOB_ID – ie add a Job class to the survey.
Do not ask for consignee if there are no samples inspected.
Fixup saving survey including editedBy.
Impl report generation – use daily report in conjunction with compliance monthly report for this.
Remove date in/out field entries.
Set inspector to “—“ when no product inspection done.
Fix release notice form.
Implement auto complete for country instead of combo box.
Get the latest remote survey from database before creating a new survey in compliance connect mobile.
Check out adding South Korea/Republic of Korea, Holand, England.
Format numbers as currency where applicable.
Update client name on general tab when it is changed on entry doc tab and vice versa.
DM Notice of/???? tab retailer and other are checked by default. Fix it.
Let client autocomplete show clients from previously entered survey.
In product inspection change gategory to category.
Make sure all generated forms are filled out properly. Start with detention request. When form fields are blank do not show commas: (i) release request: impl authorization.
Print all forms for inspection: (i) check control numbers (ii) consistency of headers (iii) alignment of text etc.
Work on standardizing generated forms fix alignment issues.
Switch to inspection search if no inspection search return 0 records?
Application for rehab customer name and address to be inserted.
Do rehab forms. Redesign them first. Fill in form with data – company name, address.
Get rid of null null wherever it appears.
Remove the color from all pdf forms?
Fix up forms so they print on secure paper where required. See marked up form.
Add all tables/fields to boshrmapp mysql and deploy. Check: ComplianceSurvey, ProductInpsection etc.
Add product type. Find out the standard product types from Inspectorate and ISO.
Add validation to all tabs. Eg if “detention request issued” is checked, make sure job number is valid. In other words make sure that all values required for the generation of forms are entered and valid.
Make sure system options and report records are on the boshrmapp mysql server.
Check to make sure the number of samples is being exported on the relevant forms as required.
Find out how billing is done based on percentage of containers detained.
System outputs: (i) daily report (ii) detention number report (iii) etc.
Fix number of samples taken???
Send email alert from within compliance connect
Adjust POE release request to accommodate header on secure paper.
Clarify and add size of units in sample request form.
Create report of detention and other numbers for audit and other purposes.
Make sure broker detail is included for sample request – port of entry.
Should port of entry reason for detention be separate from domestic market reason?
Add fields for detention and release numbers and use in forms. Do generation of numbers when survey is being saved. Use the format given in the procedure.
When a pdf form is generated, make sure the required *Issued field is set to true and the relevant release/detention number generated.
Detention release number should separate from detention number. Stick to number format as given in procedure.
Should inspector number (eg BSJ-07-22) be used instead of inspector name as is done for notice of detention? Consult on this.
Send email re decisions (such as rotation of inspectors, dry run of system) made in training and recent meetings.
Set release date null when blank.
Add fields for port of entry separate from domestic market detention numbers?
Fix margins so that printed forms fit on secure paper. Get copy of paper.
CHANGE CONNECTION PARAMS FROM JMTSTEST TO JMTS DATABASE!!!. Implement system option for this.
Impl feature to add image to form record. Add text field showing uploaded file and allow file downloads. Fix fact that file upload can’t find destination folder for images. Put picture uploading as separate from pictorial representation.
Check out what is "Size of Units" in "Sample Request - Ports of Entry".
Port of Entry – Release Request. Remove Prepared by and put another Approved by for automatically generated forms. Prepared by would still be required for manually prepare forms.

New Features

Implement associating customer complaints to a job. Lodging of complaints could also be done via the website.
Impl dialog for creating business office.
Link client to training...A feature for ITU of the BSJ. Details to be determined.

Authentication and Authorization

Add privilege to apply discount?
Add canEditInvoicingAndPayment privilege for user and not just dept.
Add privilege to access the modules/units such and "Standard Compliance".
Impl permission to create parent job only if person belongs to certain department/division (now it’s should be regulatory, customer service)
Set user privileges when department privileges are set.
Go through the GUI and enable/disable elements based on who has the privilege to edit field.
Only allow selected persons to export service contract or prevent exportation once it was exported. Add privilege setting for this.
Let constants such as JobManagerUser.CANEDITJOB be used in a method called user.getEffectivePrivilege(JobManagerUser.CANEDITJOB) to determine the overall user privilege based on department and user privilege

Reporting, Queries and Views

Implement deactivation of reports and getting only active reports.
Implement report filtering
Implement report dialog as a tab that can be chosen in "Preferences"
Implement editing report details in "System Admin"
Check on monthly report date periods for duplicates. Note that “This financial year” is not there.
“Jobs in Period” report not working so fix it.
Create interface to upload report files to “jasperrreports” or the folder that is designated as the reports folder in system options.
For report data use left join to get jobs that don't have samples.
Implement "Earning Parent Jobs Only" search type and make in default for the Finance department.
Add date costing prepared as a date search field in Cashier View
Use Dynamic Columns to create views and allow persons to choose any set of columns.
Find way to save/close job costing and maintain selections in cashier/costing views.
Need to be able to use the search area in system admin section to search for partial string in any field, not only the name. I may need to list all users from chemistry for examples, thus, i would need to search for chemistry and all users in chemistry would show up in the list.
Create report that shows workload per staff.
Use RM.generateReport() to test generating and displaying report in web browser.
Create links to report in report tab in job detail dialog.
Implement "Advanced Search" configuration using a database table and not in hard code as is done now. Call table SearchConfigurations or SearchParameters?

General

Get favicon. Green box sample was created as place holder. Real real one to be created.
Implement tip o the day
Generate and send emails in thread NB: Implemented but to be tested directly on BSJ and not thru VPN.

Department Management (Generic ERP)

Ensure that only "activate" depart are retrieved or created to avoid the creation of multiple departments with the same name.
Check if new departments are being created by the LegalDocumentPortal.
Add feature to add/remove employees from departments.

Job Entry & Update
Issues

Validate the sample dialog info before closing it.
When blank business office is selected and job saved the no message comes up. Fix!!
Automatically add the person sub contracting a job as the contact person
Add contact field to job. If not contact is assigned, assign the "main" contact to the job. Update service contract, job costing etc to reflect this.
Add field for sample condition.
User is asked to save job when job dialog is closed even when its saved. Fix.
Prevent job from being marked uncompleted once completed.
"Completed" most be removed by authorized person before a job can be edited.
Implement sample copying inside sample dialog.
Limit the number of samples/products that can be entered into sample/product quantity. Use system option for this.
See how “retest jobs” can be handled without turning off auto job numbering.
Make country of origin autocomplete in job sample dialog.
Find why business office is being marked as in active. Check if done during job entry or update
Look at the code in validateCurrentJob that deals with subcontracted department. Is it necessary to do all that check or just set it to "--" if it is null? May be false should never be returned.
Find way to let cashier do all she needs to do in just the cashier view. Find out details from Cherrian.
Implement assigning job to person only if they are in the department/subcontracted department for the job.
When one attempts to mark a job without a final cost switch back work progress to the stored work progress?
Job Modified dialog takes a while to go away when "no" is pressed and does not show the "busy" icon. When "yes' button is pressed "busy" icon shows and the dialog is dismissed fairly quickly
Add TAT to expected start when job is entered???
Job can be marked "unapproved" while it is still completed. Fix!
Don't make TAT mandatory for non-earning job.
Implement adding multiple sectors to one job.
Implement job copying.
Implement locking of opened job and alert others that it's opened. Implement some form of push notification.
Make sure parent job is not marked completed until child job is complete.
See if entering deactivated departments can be prevented.
Turnaround time given to client should show error if input is not a number. Also, that turnaround time number could be added to submission date upon first save but remains editable just in case.

Process for job entry

Request form is filled out and submitted through the website. This data would go straight to the database.
Notification system designed to signal creation of proforma which would pull data already in the system from the request form.
Database to interact with ACCPAC to signal payment or go ahead for job to be done if credit customer.
Necessary data transferred to the itinerary which can be viewed on the handheld device.
Once at the location, certain fields could automatically be populated on the field report instead of re-entering data already in the system.
The necessary verification checks (capacity and serial numbers for example) would be done and additional data (test results) entered.
Notification to be sent for certificates and job costing to be done which I thought would be as easy as a few clicks of the mouse since all the data would already be in the system/database.

Customer Relation Management

- Allow add/edit of contact or client separately based on privilege and don't make a copy of the client as is current done.
- Implement cancel adding contact and address in dialog.
- Make sure job costing and service contract uses job billing address and the contact assigned to the job and NOT the client main contact.
- Ensure that overseas clients are not charged GCT. Indicate with field in client table. If the main/billing address is changed to something other than "local" the change client to international. Show alert when this is done?
- Implement 'Main' phone number as is done for 'Main' contact.
- Make TRN mandatory for local clients?
- Add country to address/contact details/record.
- Address type for newly added address does not change when selected in table. Fix!
- Enforce entry of certain data if not credit customer. Determine this data.

Human Resource Management

- Check out why employees with blank names are being created. The blank employees are linked to jobs so look for clues in the job creation and update process. Look at all senarios where an
- employee could be created. check if creating a new user causes this.
- Dont let newly created employees be active except when created via system admin.
- Delete blank internet and business office created linked to blank employees? Ensure that only "activate" employees are retrieved or created
- Implement assigning a person to more than one department.

Accounting/Financial Services

Learn GnuCash inside out including python bindings and how to integrate the JMTS with it.
Allow anyone in department to mark costing as prepared/completed.
Prevent creating invoices for child jobs.
Implement cash payments and use of "get total payments received" and not getDeposit for "Total paid".
Put close button on upper right of costing dialog.
At the bottom of the summary section in costing show the names of who approved and who invoiced.
Add feature to show/hide child jobs from showing in tables specifically for finance. Make it a per user preference.
Change jobCostingAndPayment.deposit to jobCostingAndPayment.paid and the corresponding database filed? Same for deposit date?
Job Details on costing needs to pull info from instructions (i.e. special instructions section). Export instructions and not special instructions to contract. Done – instructions are joined with special instructions for now. I suggest that the be properly done when the new form is implemented.
Check job is marked dirty under all circumstances when payment info is edited.
Do this to figure out why payment and may be costing info is not being saved.
GCT to be added only to parent jobs. update all relevant fields when costing is updated.
Set date costing prepared to cost date in the report for now.
Allow marking a non-earning job as completed even with $0.00 final cost.
Receipt, discount and invoice number are editable by non-finance staff? Change if so.
Apply discount to estimated and final cost. Allow applying fixed and percentage cost.
Implement option to turn off the charging of tax such as GCT for specific departments.
Disable invoicing if job is "not earning" and/or user is not from finance.
Remove the costing date when costing is "disapproved".
Job costings for subcontracted jobs are being pulled into the costing of other jobs because the year the job was submitted is being changed. Could be solved by preventing the job number and date submitted from being changed manually.
Append credit terms to payment terms? Get confirmation from Finance.
Implement setting currency and exchange rates for costing payments.
Put isdirty in job costing and payment and only save when it is dirty. Only Finance personnel or other designated persons should be able to change invoicing and payment fields by implementing cash payments.
Add option to remove or include child job costings.
Dept 52 (standards) should charge GCT of 2% by default. Add "Default GCT" as a setting
in the department detail...add a tab called "Adanced Options" with a field called "Default GCT" and move "Apply taxes to job costing" privilege to that tab.
Dynamically add 16.5% gct to labels. Check that job costing shows the correct percentage that is applied.
Prevent job costing approval for jobs without a report number?
Disable "approve checkbox" if person is not dept supervisor or person authorized to approve., invoiced.
Allow setting min. deposit as percentage or fixed cost. #15
NB (Put in Wiki): The job costing template is : c:\jasperreports\Job Costing.jasper

Database management

Implement quick and advanced search for data lists.
Remove excess classification entries for "Non-Earning - Internal Client Request" and "--". NB Check what is causing them to be created.
Find all entities that references the current entity before deleting. try with employee and client for example. create interface for this.
Remove fields from database that are not used: jobcostingandpayment: depositDate, jobcostingandpayment: dateOfLastPayment

Notes:

Consider getting code for iReport and maintain it via github.
