# Job Management & Tracking System (JMTS)
The JMTS is an enterprise software that facilitates the management and tracking of jobs.
================================================================================
## Things to do
### PENDING LIVE DATABASE ALTERATIONS:
- Add: discount, discountType, paymentTerms to CASHPAYMENT table
- Rename: rename JMTSUserId to userId in CASHPAYMENT??
- Add: BILLINGADDRESS_ID, CONTACT_ID to Job table (for billing address, contact)
- Add: country to Address dialog?
- Add: System Option organizationName, Bureau of Standards Jamaica, String, 
       GENERAL, The name of this organization.
- Update: Deactivate records that have "--" so they do not appear in autocomplete
  and select components.
- Add: System options applicationHeading and applicationSubheading
================================================================================
### Rebuild UI Functionality
- In employee dialog fix up the width of the dropdown or autocomplete as is done
  with client autocomplete.
- Implement creating subcontracts. Create the "organizationName" system option
  and use it to retrieve/create the client. Make sure billing address and contact
  are set. Automaticaly retrieve or create contact person which is the person
  creating the subcontract.
- Implement generating service contact form
- Do shallow copy of client, billing address and contact before saving job to 
  ensure that future edits of the client's name, billing address and contact
  do not affect the job record.
- The select/add billing dialog sometimes reject the selected address. Check why.
- Check that client credit status dialog still works
- Fix client database dialog.
- Make sure buttons at both top and bottom of dialogs are identical.
- Move addMessage("Success", "Job was saved!"); to each "if" statement in 
  saveCurrentJob().
- Use parentJob to link contracts with parent jobs and use it to pull 
  in subcontracts costs?
- Enter long text such as addresline1 and see howthe UI reacts.
- Test with normal user (kmiller)
- Update job search table when client dialog is closed?
- Make TRN mandatory? Put system option to validate?
- Hide billing address display if client is not valid.
- Impl. clients dialog.
### Immediate Issues
- Impl validating the creation of new clients...do not allow creating of client
  that already exist.
- Impl. rendering job detail panel in response to job open and close.
- Impl. job dialog as tab to see if it fixes the issue where the client is 
  not being updated when the client dialog is "oked". Try with simple client form first.
- The client owner should be set to the client manager's client when the client 
  is "oked". 
  Impl. handling of saving and closing client dialog. 
  After adding an address or contact the respective address and contact fields
  are to be updated. 
- <p:autoComplete id="currentClientBillingAddress"> should be validated. Validation
  must coonsider the case when the address is new and does not exist in the database.
- Put use <p:ajax event="dialogReturn"> to update fields and use primefaces 
  dialog framework for the client form/dialog.
- Finish NewClientValidator and use it to validate new client entries. Impl. 
  prevention dialog closing and client save until client is validated like 
  what is done for the system option dialog.
  Try remove <p:dialog> may be that will make it behave like the systemOptionDialogForm.
- Ensure "Entered by" and "Edited by" works.
- Implement "Select/Add contact" 
- Imlement "+ New" to be put beside "Select/Add Contact/Address" in client form.
- Setup UI for entry and display of billing address for new and existing client.
   The current UI is not user friendly.
- Call getBillingAddress(), getMainContact() when client is first selected in 
   job client field. 
- Remove code that changes the billing address type to only one address. ie allow
   more than one address to bear the "billing" type.
- Validate address fields in address dialog.
- Remove type and "set billing addres" from client dialog and fix fact that
   table is not properly update or stateOrProvince not being set.
- Find address using all fields of the address and use autocomplete and validate
  billing address before save. Create a validator that show errors warns about using
  comma in an address field and ensure that all fields are filled out.
- Implement change back to billing address and implement search on all the fields
   of a address for use in the converter.
- Use POJOs where possible on ClientForm.
- Implement billing address field on job dialog. Fill in billing address after
   address is selected in "select billing address combobox.
- Implement validators where necessary.
- Add contact field in "General" tab. Make it autocomplete with drop down and 
   force selection. Add edit button beside field. Allow setting the contacts address
   as the billing address if no billing address is set on the main client. 
- In Job.validate() check for id == null instead where possible instead of fetching the object.
- Implement updateCashPayment() to record updates made to a field and store the updates.
- Make sure service contract, job costing etc. are updated to use the billing address
  and contact found in the job record.
in a list called updates using a method called update() in JobManager. 
Only mark the job as dirty when the dialog is closed or "oked". 
JobManager isDirty should be not be set to true if edits in a dialog have been 
canceled. Implement same feature for the JobDialog and other dialogs.
- Add cash payments feature so cashier can add cash payments.
* Add discount type and discount to cash payment form.
* Add fields to cashPayment and database: discount, discountType, 
paymentTerms, rename JMTSUserId to userId
* Make a payment(first) as desosit and save to jobCostingAndPayment.deposit
* Add PO number to payment panel. 
* Update corresponding fields in jobCostingAndPayment as required.
* Use canEditInvoicingAndPayment where necessary
* Ensure amount due is updated correctly using cash payments.
- Create contact field in the job record to assign contact to current job.
- Implement and include report templates for all reports generated to date.
- Implement prevention of the insertion of incorrect subcontract jobs costing 
amounts which sometimes occur when the date of submission of a parent job is changed
- Modify the permissions scheme to prevent the changing of some fields (such as 
the client field) by unauthorized personnel and track all changes made.  
- Implement "Double View" for the cashier so that the Cashier and Job Costing 
Views can be viewed simultaneously for easy job costing updates.
-- Create views/tabs for job costing and cashier instead of using job tables view. 
    OR
-- Instead of creating double view, "merge" the columns from "Job Costing" 
view while leaving out unneeded columns.
- Implement BSJ client entry update and automatic contact detail insertion when 
subcontracting jobs.
-- For external jobs, create contact field in job record and store contact from 
a selected client contact.
-- For subcontracts, get or create contact for person doing the subcontract.
- Use primefaces Dialog framework to implement all dialogs so they can be reused
by other apps?
- Remove payeeFirst/Lastname and put contact/person.
- Remove search parameters for legal, compliance and foods.
- When subcontracted department is deleted and the job form is closed,
  the subcontracted department field disappears when the form is reopened. Fix!
- This was commented out of ClientManager: 
    //getMain().displayCommonMessageDialog(null, "Please provide at least 1 character for the search text.", "Insufficient Characters", "alert");
    Find way to get something like this back in without using getMain().
- For new clients make sure that the "EnteredBy" field is filled out.
- If the billing address field is not set then set the default billing address
  for the client being edited.
- Do copy of billing address, contact and client before saving new job. Do
  not allow changing these fields except by sysadmin. Implement doShallowCopy()
  for client that does not copy the list of addresses and contacts.
- Prevent changing of all fields that affect the service contract.
- Put sys option that controls the changing auto job number generation. Make it
  unchangeable by default.

### System Design
#### Design
- Refactor and rename static methods from findEntityName*() to just find*() where
  possible.
- Create Reporting backing bean and move relevant code from JM to it.
- Limit the maximum characters to be entered into a text field to 50. Put this
  value in the BEL resource bundle which can be changed. Allow various maximums
  for different types of fields.
- Make all sensitive fields, especially those on the General tab unchangeable
  except by system admin after a job has been saved. Use field groups with 
  input and output fields as is done with the job number. 
- Put reports/queries in own tab and not dialog.
- Check if new clients can be created even without privilege.
- Implement "isdirty" for client, contact, address and do shallow copy when they
  change?
- For job search let My Department job be the defaut.
- Test out creating a new default client. Clean up client search and ensure 
  consistency when doing search with ignoring case and part of client being searched.
- Implement selectTab(index) in javascript that selects a tab based on the 
  the number of tabs in the tabview and the default index position of the tab
  as defined in index.html.
- Modify updateEntity methods to not find the entity by name cause that's already
  done by the respective converters.
- Create separate login page instead of using login dialog?
- Remove client dialog and put as closable tab.
- Show control panel when a tab is open of this is applicable.
- Check what happens if mainTabViewVar.select(0) is called and no tab is visible.
- Implement checkboxes in user menu to allow activating modules and showing windows.
- Create dialogs for classification instead of just adding a row in the table.  
- Revisit admin dialogs and fix autocomplete scroll height, converters and validators.
  Ensure the prevention of creating duplicate objects such as employees with the same name.
- Move all "save and create" methods from JobManager to their respective entities
- Change discount combobox to a menu.
- Look to move fields and methods from JobManager in the entities such as Job.
- Rename "Advanced Search" panel to something more general.
- Clean up development database by removing "blank" records etc.
- Create Home screen showing user's jobs, departmental job etc.
- Display message when user logs on without authentication
- Make country of origin autocomplete.
- Redesign as group RESTFul web services (eg CRM, HRM) that are accessed by light weigh user interfaces
- For job/survey and other search just return the values needed for the table display and not the entire job/survey record. This requires less bandwidth.
- Implement data backup from one database to another ie database replication.
- Get rid of use of "--" as placeholder objects and fields
- Move JobManager and all other beans to BEL?
- Replace names such as BSJ/Bureau of Standards and images with system options.
- Fix up ServiceManager to use ClientManager the way it is used by JobManager
- Separate address and contact dialogs into separate files and include them
  into the clientForm.xml file as required. This is allow the contact and address
  dialogs to be used independently.
- Use validators outside of UI and implement validate() for relevant entities
  such as client.
- Use validators and growl to ensure display validation message consistency.
- Put cancel button on contact and address dialogs.
- Put every form in its on file.
- Impl. the following using primefaces dialog framework: loginDialog, 
  accPacClientInformationDialog, reportingDialog, jobSampleDialogs, jobDialog,
  jobCostingDialogs, preferencesDialog, cashPaymentDialog
- Change the Search combos to p:selectonemenu.
- Upgrade to latest primefaces if possible.
- Redesign layout with dashboard component?
- Merge Main and Application...Use just Application. May be get rid of both.
- Make System Admin tab closable.
- Make jobs tab closable but remember the JS call mainTabViewVar.select(0); that
  may cause problems.
- Implement the Organization class and use its name field instead of "thisOrganizationName"
  system option when creating subcontracts.
- Show a summarized version of a completed job that does not allow editing. 
  Create a separate job detail tab for this?
- Check why <p:remoteCommand name="doJobSampleCopy"> is used and if copy sample
  can be done without using a remote command.
- Do not allow creating client that already exist. Use client validator.
- Implement display of message when converter fails.
- Implement dynamically hide/show "Jobs" tab based on search. Allow closing the
  tab.
- Use the "Advanced Search" panel for other purposes depending on what is selected
  in the "Central" layout unit. Change the heading from "Advanced Search" to 
  reflect its purpose.
- Implement "Quick Search" for data list tables. Collapse "Advanced Search" for
  for tabs that does not use advance search.
- Add button for the creation of new objects such as jobs in the control panel
  based on the active tab.
- Put "Reports/Queries" in separate tab called "Reports" and put in user menu
- Look at Garfield's design images on google docs for ideas.
- Design a favicon and make it system option.
- Try to put all styles as styleClass and put into index.css
- Print service contract for only external client jobs??
- Upgrade to Primefaces 6.x


### Legal Documents/Office Module (GenericERP Module)
- Fix up entity classes by using ALL annotations for "persistenc commit" in
   LegalDocument class etc.
- Watch out for dateselect and keyup events.
- Fix issues where classifications are blank. Create new ones if necessary.
- Remove trim from all find* methods

### Service Contract
- Export sample condition(s) to service contract.
- Ensure that the contact in the job and not the main contact in the client is used where required. The job contact field to be created if necessary.
- Restrict generation of service contract to an authorized person/department?
- If department name/id too long for field, shorten and add ellipses to the end.
- Put client ID# (TRN etc.) into service contract?
- Where applicable put grey and not black borders.
    “The contract is exporting date entered as the date submitted as well” this is from Garfield check it out.
- NB (Put in Wiki): The service contract template is : c:\jasperreports\ServiceContractTemplate.xls

#### Issues
- Change "keyup" to "change" where possible.
- Update system options table after editing an option.
- Remove record id from dialogs.
- Implement setting of logo and title of main page in system admin
- Do not allow the creation of entities such as employee with the same name or warn if this is to be done.
- Change all combobox to autocomplete with dropdown where possible.
- When saving a job let the each "major" object do its own saving e.g JobStatusAndTracking(em).
- Get rid of extra busy wait in login dialog.
- Hide search dialog when no module is selected.
- Checkout using converters such as employeeConverter that gets the actual object from the database. this could possibly prevent creating employees with blank names when saving a job.
- Set lightyellow background for all form fields that are not editable.
- Let findEmployeeByName and similar searches return only one result and not the first from several.
- Implement adding digital or image signature using system admin?
- Make all dialog closable wit "x" in upper right corner.
- Use JSF validation where possible.
- Check that developer email address in sys option is not used when sending emails about errors etc.
- Check that "Search Type" is reset when a user logs off.
- Add "Report Issue" button somewhere in GUI.
- Remove closeEntityManager(em) where it's not necessary.
- Return costing templates with unique names and don't allow saving templates with same name.

### Standards Module (GenericERP)
1) Standards development processs
2) WTO notification database
3) JS and ISBN numbers – automate number creation
4) IEC/ISO standards commenting/tracking
5) CROSQ – standards databases
6) Earnings database
7) Conformity assessment/certification
8) Registry of companies
9) Audit schedule
10) CMP database – companies certified and other status
11) Registry of all companies registered (see spreadsheets)
12) QEMS
13) Technical committees database
14) Technical regulations/standards list/database/catalogue

### Legal Metrology (GenericERP)
1) Check if all close buttons work in history dialogs. 
2) Impl importing petrol stations from spreadsheet: add pump/nozzle certification to petrol station and included expiry date to certification. 
3) Optionally leave out date period search for petrol stations for example to see how if it affects search speed.
4) Change LegalMetrologyManager to LegalMetrologyTester in on PetroPumpNozzle test app.
5) Create LegalMetrologyClient with list of entities such as petrol stations and scales and other products. 
6) Set a one month lead time when for reminders about due date for equipment test/calibration. .
7) Lpg checking: database to be developed. Details to be obtained later.
8) Package checking database to be done.
9) Storage tank database. Tank strapping and volumetric.
10) Roller weights calibration database. See Calibration Report for roller weights
11) Option to show/hide search form when logged in and provide basic search if search form (advanced search) is hidden.
12) Database for high volume meters from petrojam to be done.
13) Application form for calibration of tankerwagon/scales etc. – put online? Get new form. Form should be validated for required fields before saving/printing.
14) Client certificates should be automatically generated?
15) Update seal,sticker,petrolpumpnozzle tables in boshrmapp.
16) Create new sticker/seal record only if the current number entered is unique.
17) Implement finding an existing job assigned to the current client in job number dialog. updateJob(SelectEvent event) approach to be looked into to see if it works.
18) Do job number assignment in gas pump test tab? Do not allow saving test results until job number is assigned or if the assigned job number is for a job that is completed.
19) Create table for petrol types (diesel etc.)
20) Allow entering the measure used for a pump/nozzle. Make use of the measures string in nozzles to compute pass or fail and header in the table. Opportune 
21) Change back date recorded to time stamp. Find out how to make the database assign the date and time stamp.
22) Impl calculating actual petrol usage by bsj using error readings.
23) Do report on pass/reject in test data table or separate table.
24) For each job done the value of the job should be subtracted from the deposit made by a gas station.
25) Change seal and sticker dates to time stamp.
26) Impl adding pumps as samples to a pump test job.
27) Impl random testing feature in schedule.
28) Create new calibration if the date of the last calibration if different from the current date.
29) Create new calibration for a pump if the date is not the current date or the seal has changed???
30) Create filtering that shows stations due based on time such as week, 2 weeks, one month etc.
31) Create sample report: 
32) Setup to do importation of data. Use POI. (i) merge independent with other and enter each station as a separate company (ii)implement random selection of pump/nozzle to be tested (iii) keep history calibration information for each pump. (iv) Set number cells that are blank to 0 before doing importation.
33) how is random scale testing done:
34) Check out petrol sampling log and testing. How is it done and where is the report.
35) Impl adding/editing classification elements (eg sector) in job entry form or department form. Only persons with the required privilege should do this. Do this in SysAdmin??
36) Create table views for employee, department, user in first release.
37) Try to put common libs such as primefaces and poi on the application server instead of packaging them as part of the application to reduce its size. Uncheck the lib in the compile-time libraries to do this.
38) Check for duplicate saving of objects that belong to lists. Start with legalmet.
39) Impl searching by trn where applicable.
40) Put dialogs inside of the main forms.
41) Prepare project database/features list for each database software.
42) Procedure for adding an entity class as a field to another entity class is: (1) Create and add new entity to the persistence file (2) Add reference to the entity to the parent class (3) Add the entity field to the table of the parent class in the form ENTITYNAME_ID, selecting the index option(4) use ALTER TABLE JOB DROP [FIELDNAME] to drop fields that were moved to new table/class. Other commands found at: http://db.apache.org/derby/docs/dev/ref/rrefsqlj81859.html
43) Impl save without closing the box for relevant dialog boxes.
44) Check out ISO/IEC 27001 (information security).
45) Impl detecting compatibility mode for IE.
46) Admin: use jasypt lib as a method for password encryption and authentication.
47) Impl adding stickers to database: Oct 20, 2011 (29601 – 30600) to Jody-Ann Burke.
48) See notes on inspection form: do regs 1-4, sampling inspection type, tie in sample collection form.
49) Impl. track registration from time of request/application to completion: see Registration Tracking Sheet (Revised).xlsx: Fields to add to Registration class: assignedInspector, dateAssignedToInspector,  assigned etc.
50) Compliance db: Impl alert 8 weeks before registration due via popup or email to inspector and other relevant personnel eg supervisor.
51) Standards db: Impl closing using “x” on standard dialog box.
52) Standards db: Impl “DateTrack?” class that has a type, date, name etc and use it to for recording published, revised dates etc.
53) Create table or configuration that gives enfo
54) cement types for a standard eg. Mandatory, Voluntary etc.??
#### Monthly Report
• # of weighing and measuring devices (what are the devices?) were targeted for verifications during the period under review (break out the devices into various categories and state the numbers for each category). Do not include # of calibrations in the number of verifications (state the difference). 
• # of premises visited for the purposes of testing and verifying weighing and measuring devices.
• # of weighing and measuring devices that were tested and verified during the period under review.
• % of those tested and verified that were in compliance with related metrological requirements?
• # of investigations of suspected inaccurate measurements were conducted? (it is assumed that these investigations were as a result of complaints received from consumers and businesses who believed that they had received inaccurate measurements)
• Types of weighing and measuring devices were these investigations conducted and the results of the investigations. 
• # of gas pump stickers that were issued during this financial year. # issued during the corresponding period last financial year. 

### Foods Inspectorate (GenericERP)
1) See notes on inspection form: do regs 1-4, sampling inspection type, tie in sample collection form.
2) Implement track registration from time of request/application to completion: see Registration Tracking Sheet (Revised).xlsx: Fields to add to Registration class: assignedInspector, dateAssignedToInspector,  assigned etc.
3) Implement alert 8 weeks before registration due via popup or email to inspector and other relevant personnel eg supervisor.
4) Implement tracking of application for certificates.
5) Lock form upon client signature is affixed to the food factory report.
6) System to generate certificates for client.
7) Add HACCP recognition programme audit tab in the food inspection form.
8) Associate a factory with a client by adding a Client class to the factory class.
9) Move pages to web folder under project folder and stop using webpages folder.
10) Save inspection form when tab is closed?
11) Factory inspection office should default to that of the officer doing the inspection if it is not null. Should default to head office if null.
12) Find a different solution than using “currentFoodFactory.getAllBusinessEntitiesLists()” to save new items added to a list. See how it’s done in ComplianceConnect for example.
13) Check if food inspection forms have been merged.

### Status & Tracking
- Status note field needs to be locked and a button be used to add updates to it. These updates would be added as new line (i.e. \n). And updates can therefore only be added but not deleted.
- Remove dateCostingCompleted, dateCostingApproved, depositDate, dateOfLastPayment and costingDate from where they should not because they are all in jobStatusAndTracking. Make sure they are not being used first.
- Steps to be tracked: (i) dispatch date to client (ii) delivery of report/sample to customer service (iii) assignment of job by team leader (iv) date customer was contacted (v) date email sent to client when job is completed. (vi) re-issue of report.

### Accpac Integration
- Add option to change days to add to invoice date to get invoice due date.
- Accpac invoice due date = DUEDATE(DOCDATE) + 15 days (for example). 1 to x days the invoice should be marked as current. PI is code for pre-payment and IN is for invoice.
- For credit status show invoice due date and not invoice date?

### Standard Compliance Module (GenericERP)
#### General
- Fix up reporting.
- For Daily Report put the client in the Consignments Inspected table.
- Error occurs when product is added, survey dialog is closed and survey dialog reopened. Could be because product is null. Save all products with null id when saving survey?
- Add date of survey to compliance connect search in jmts.
- Add search params for compliance survey
- Ask about saving survey when product is added?
- When product deletion is cancelled refresh dialog for compliance survey
#### Issues
1) In compliance survey remove job number field and put JOB_ID – ie add a Job class to the survey.
2) Do not ask for consignee if there are no samples inspected.
3) Fixup saving survey including editedBy.
4) Impl report generation – use daily report in conjunction with compliance monthly report for this.
5) Remove date in/out field entries.
6) Set inspector to “—“ when no product inspection done.
7) Fix release notice form.
8) Implement auto complete for country instead of combo box. 
9) Get the latest remote survey from database before creating a new survey in compliance connect mobile.
10) Check out adding South Korea/Republic of Korea, Holand, England.
11) Format numbers as currency where applicable.
12) Update client name on general tab when it is changed on entry doc tab and vice versa.
13) DM Notice of/???? tab retailer and other are checked by default. Fix it.
14) Let client autocomplete show clients from previously entered survey.
15) In product inspection change gategory to category.
16) Make sure all generated forms are filled out properly. Start with detention request. When form fields are blank do not show commas: (i) release request: impl authorization.
17) Print all forms for inspection: (i) check control numbers (ii) consistency of headers (iii) alignment of text etc.
18) Work on standardizing generated forms fix alignment issues.
19) Switch to inspection search if no inspection search return 0 records?
20) Application for rehab customer name and address to be inserted.
21) Do rehab forms. Redesign them first. Fill in form with data – company name, address.
22) Get rid of null null wherever it appears.
23) Remove the color from all pdf forms?
24) Fix up forms so they print on secure paper where required. See marked up form. 
25) Add all tables/fields to boshrmapp mysql and deploy. Check: ComplianceSurvey, ProductInpsection etc. 
26) Add product type. Find out the standard product types from Inspectorate and ISO.
27) Add validation to all tabs. Eg if “detention request issued” is checked, make sure job number is valid. In other words make sure that all values required for the generation of forms are entered and valid.
28) Make sure system options and report records are on the boshrmapp mysql server. 
29) Check to make sure the number of samples is being exported on the relevant forms as required.
30) Find out how billing is done based on percentage of containers detained.
31) System outputs: (i) daily report (ii) detention number report (iii) etc.
32) Fix number of samples taken???
33) Send email alert from within compliance connect
34) Adjust POE release request to accommodate header on secure paper.
35) Clarify and add size of units in sample request form.
36) Create report of detention and other numbers for audit and other purposes.
37) Make sure broker detail is included for sample request – port of entry.
38) Should port of entry reason for detention be separate from domestic market reason?
39) Add fields for detention and release numbers and use in forms. Do generation of numbers when survey is being saved. Use the format given in the procedure.
40) When a pdf form is generated, make sure the required *Issued field is set to true and the relevant release/detention number generated.
41) Detention release number should separate from detention number. Stick to number format as given in procedure.
42) Should inspector number (eg BSJ-07-22) be used instead of inspector name as is done for notice of detention? Consult on this.
43) Send email re decisions (such as rotation of inspectors, dry run of system) made in training and recent meetings.
44) Set release date null when blank.
45) Add fields for port of entry separate from domestic market detention numbers? 
46) Fix margins so that printed forms fit on secure paper. Get copy of paper.
47) CHANGE CONNECTION PARAMS FROM JMTSTEST TO JMTS DATABASE!!!. Implement system option for this.
48) Impl feature to add image to form record. Add text field showing uploaded file and allow file downloads. Fix fact that file upload can’t find destination folder for images. Put picture uploading as separate from pictorial representation.
49) Check out what is "Size of Units" in "Sample Request - Ports of Entry".
50) Port of Entry – Release Request. Remove Prepared by and put another Approved by for automatically generated forms. Prepared by would still be required for manually prepare forms.

### New Features
- Implement associating customer complaints to a job. Lodging of complaints could also be done via the website.
- Impl dialog for creating business office.
- Link client to training...A feature for ITU of the BSJ. Details to be determined.

### Authentication and Authorization
- Add privilege to apply discount?
- Add canEditInvoicingAndPayment privilege for user and not just dept.
- Add privilege to access the modules/units such and "Standard Compliance".
- Impl permission to create parent job only if person belongs to certain department/division (now it’s should be regulatory, customer service)
- Set user privileges when department privileges are set.
- Go through the GUI and enable/disable elements based on who has the privilege to edit field.
- Only allow selected persons to export service contract or prevent exportation once it was exported. Add privilege setting for this.
- Let constants such as JobManagerUser.CANEDITJOB be used in a method called user.getEffectivePrivilege(JobManagerUser.CANEDITJOB) to determine the overall user privilege based on department and user privilege

### Reporting, Queries and Views
- Implement deactivation of reports and getting only active reports.
- Implement report filtering
- Implement report dialog as a tab that can be chosen in "Preferences"
- Implement editing report details in "System Admin"
- Check on monthly report date periods for duplicates. Note that “This financial year” is not there.
- “Jobs in Period” report not working so fix it.
- Create interface to upload report files to “jasperrreports” or the folder that is designated as the reports folder in system options.
- For report data use left join to get jobs that don't have samples.
- Implement "Earning Parent Jobs Only" search type and make in default for the Finance department.
- Add date costing prepared as a date search field in Cashier View
- Use Dynamic Columns to create views and allow persons to choose any set of columns.
- Find way to save/close job costing and maintain selections in cashier/costing views.
- Need to be able to use the search area in system admin section to search for partial string in any field, not only the name. I may need to list all users from chemistry for examples, thus, i would need to search for chemistry and all users in chemistry would show up in the list.
- Create report that shows workload per staff.
- Create links to report in report tab in job detail dialog.
- Implement "Advanced Search" configuration using a database table and not in hard code as is done now. Call table SearchConfigurations or SearchParameters?

### General
- Get favicon. Green box sample was created as place holder. Real real one to be created.
- Implement tip o the day
- Generate and send emails in thread NB: Implemented but to be tested directly on BSJ and not thru VPN.

### Department Management (Generic ERP)
- Ensure that only "activate" depart are retrieved or created to avoid the creation of multiple departments with the same name.
- Check if new departments are being created by the LegalDocumentPortal.
- Add feature to add/remove employees from departments.

### Job Entry & Update
#### Issues
- Validate the sample dialog info before closing it.
- When blank business office is selected and job saved the no message comes up. Fix!!
- Automatically add the person sub contracting a job as the contact person
- Add contact field to job. If not contact is assigned, assign the "main" contact to the job. Update service contract, job costing etc to reflect this.
- Add field for sample condition.
- User is asked to save job when job dialog is closed even when its saved. Fix.
- Prevent job from being marked uncompleted once completed.
- "Completed" most be removed by authorized person before a job can be edited.
- Implement sample copying inside sample dialog.
- Limit the number of samples/products that can be entered into sample/product quantity. Use system option for this.
- See how “retest jobs” can be handled without turning off auto job numbering.
- Make country of origin autocomplete in job sample dialog.
- Find why business office is being marked as in active. Check if done during job entry or update
- Look at the code in validateCurrentJob that deals with subcontracted department. Is it necessary to do all that check or just set it to "--" if it is null? May be false should never be returned.
- Find way to let cashier do all she needs to do in just the cashier view. Find out details from Cherrian.
- Implement assigning job to person only if they are in the department/subcontracted department for the job.
- When one attempts to mark a job without a final cost switch back work progress to the stored work progress?
- Job Modified dialog takes a while to go away when "no" is pressed and does not show the "busy" icon. When "yes' button is pressed "busy" icon shows and the dialog is dismissed fairly quickly
- Add TAT to expected start when job is entered???
- Job can be marked "unapproved" while it is still completed. Fix!
- Don't make TAT mandatory for non-earning job.
- Implement adding multiple sectors to one job.
- Implement job copying.
- Implement locking of opened job and alert others that it's opened. Implement some form of push notification.
- Make sure parent job is not marked completed until child job is complete.
- See if entering deactivated departments can be prevented.
- Turnaround time given to client should show error if input is not a number. Also, that turnaround time number could be added to submission date upon first save but remains editable just in case.
#### Process for job entry
1. Request form is filled out and submitted through the website. This data would go straight to the database.
2. Notification system designed to signal creation of proforma which would pull data already in the system from the request form. 
3. Database to interact with ACCPAC to signal payment or go ahead for job to be done if credit customer.
4. Necessary data transferred to the itinerary which can be viewed on the handheld device.
5. Once at the location, certain fields could automatically be populated on the field report instead of re-entering data already in the system.
6. The necessary verification checks (capacity and serial numbers for example) would be done and additional data (test results) entered.
7. Notification to be sent for certificates and job costing to be done which I thought would be as easy as a few clicks of the mouse since all the data would already be in the system/database.

### Customer Relation Management (GenericERP)
- Allow add/edit of contact or client separately based on privilege and don't make a copy of the client as is current done.
- Implement cancel adding contact and address in dialog.
- Make sure job costing and service contract uses job billing address and the contact assigned to the job and NOT the client main contact.
- Ensure that overseas clients are not charged GCT. Indicate with field in client table. If the main/billing address is changed to something other than "local" the change client to international. Show alert when this is done?
- Implement 'Main' phone number as is done for 'Main' contact.
- Make TRN mandatory for local clients?
- Add country to address/contact details/record.
- Address type for newly added address does not change when selected in table. Fix!
- Enforce entry of certain data if not credit customer. Determine this data.

### Human Resource Management (GenericERP)
- Check out why employees with blank names are being created. The blank employees are linked to jobs so look for clues in the job creation and update process. Look at all senarios where an
- employee could be created. check if creating a new user causes this.
- Dont let newly created employees be active except when created via system admin.
- Delete blank internet and business office created linked to blank employees? Ensure that only "activate" employees are retrieved or created
- Implement assigning a person to more than one department.

### Accounting/Financial Services
- Allow anyone in department to mark costing as prepared/completed.
- Prevent creating invoices for child jobs.
- Implement cash payments and use of "get total payments received" and not getDeposit for "Total paid".
- Put close button on upper right of costing dialog.
- At the bottom of the summary section in costing show the names of who approved and who invoiced.
- Add feature to show/hide child jobs from showing in tables specifically for finance. Make it a per user preference.
- Change jobCostingAndPayment.deposit to jobCostingAndPayment.paid and the corresponding database filed? Same for deposit date?
- Job Details on costing needs to pull info from instructions (i.e. special instructions section). Export instructions and not special instructions to contract. Done – instructions are joined with special instructions for now. I suggest that the be properly done when the new form is implemented.
- Check job is marked dirty under all circumstances when payment info is edited.
- Do this to figure out why payment and may be costing info is not being saved.
- GCT to be added only to parent jobs. update all relevant fields when costing is updated.
- Set date costing prepared to cost date in the report for now.
- Allow marking a non-earning job as completed even with $0.00 final cost.
- Receipt, discount and invoice number are editable by non-finance staff? Change if so.
- Apply discount to estimated and final cost. Allow applying fixed and percentage cost.
- Implement option to turn off the charging of tax such as GCT for specific departments.
- Disable invoicing if job is "not earning" and/or user is not from finance.
- Remove the costing date when costing is "disapproved".
- Job costings for subcontracted jobs are being pulled into the costing of other jobs because the year the job was submitted is being changed. Could be solved by preventing the job number and date submitted from being changed manually.
- Append credit terms to payment terms? Get confirmation from Finance.
- Implement setting currency and exchange rates for costing payments.
- Put isdirty in job costing and payment and only save when it is dirty. Only Finance personnel or other designated persons should be able to change invoicing and payment fields by implementing cash payments.
- Add option to remove or include child job costings.
- Dept 52 (standards) should charge GCT of 2% by default. Add "Default GCT" as a setting
- in the department detail...add a tab called "Adanced Options" with a field called "Default GCT" and move "Apply taxes to job costing" privilege to that tab.
- Dynamically add 16.5% gct to labels. Check that job costing shows the correct percentage that is applied.
- Prevent job costing approval for jobs without a report number?
- Disable "approve checkbox" if person is not dept supervisor or person authorized to approve., invoiced.
- Allow setting min. deposit as percentage or fixed cost. #15
- NB (Put in Wiki): The job costing template is : c:\jasperreports\Job Costing.jasper

### Database management
- Implement quick and advanced search for data lists.
- Remove excess classification entries for "Non-Earning - Internal Client Request" and "--". NB Check what is causing them to be created.
- Find all entities that references the current entity before deleting. try with employee and client for example. create interface for this.
- Remove fields from database that are not used: jobcostingandpayment: depositDate, jobcostingandpayment: dateOfLastPayment

