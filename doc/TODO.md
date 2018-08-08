## Things to do

### Reports
- Impl report exportation:
  * The output file name does not change when new report is selected. Fix
  * Impl exporting to excel.
  * Date period fields not updated when new period is selected. Fix.
  * Add dropdown for selecting date field and hide/show them when they are in the 
    collection field. 
  * Update analyticalServicesReportFileInputStream() to use selected report parameters.
    Use the selected date seach field in the method that does the search.
  * Impl selecting report parameters. Use single item selection components for
    selecting parameters such as employee and department. Continue with reporting
    department 1.
  * Impl getReportStreamedContent(Report currentReport) and test with 
    "Job entered by employee" report. Impl "Job entered by employee" report
     Test with employeeId1 = 192, datePeriod1 = "This month"
  * Put the functionality for getting the report file in the Report class?
  * Export "blank" report when "--" report is selected enable disable "Export"
    buttons as as before.
- Impl finance report showing jobs that are supposed to have deposits but don't
  "Jobs Requiring Deposits" as the report.
- Impl and add Legal reports. 
- Add report template for legal.
- Deactivate accpac invoice features and deploy.
- Note the required order of the dates for the monthly report when deploying

### Next Status Update Notes
- Add users, update database, etc. under sys admin
- Mention work on BPM 
- Mention development and testing of infrastructure modules of the JMTS.
- Get copy of ISO/IEC 42010 for Software Architecture.

### Updates based on training and feedback
- Display prompt when there are completed subcontracted jobs when the Job Costing dialog
  is opened:
  * Impl "Existing Subcontracts" dialog...copy the "Delete Payment" dialog.
- Fix sample sequencing when a sample is deleted.

### Job Edit Synchronization
- Create/maintain list of opened jobs in the Application class.
  * Set opened date when open job and when keepalive runs..
- Add Opened Jobs tab in Sys Admin with search text box and refresh button
- Do sync for all job view tables.
- Ensure access to currentJob is synchronized since it can be accessed by "keepalive"
  other code at the same time

### Accpac Invoicing etc.
- Impl invoice export.  
  * Note code is of the from 1310-21-24-21 for eg. - dept code first and div code last
  * Note each each count item have a dist code. eg discount, gct, testing & cal. etc.
  * In onJobCostingCellEdit save JCP and client only if these were table cells 
    were edited. Also new Accounting codes are being saved. Fix!
  * Change "Service Contract" to "Services" and make "Service Contract" a "sub tab"
  * Impl fill out of Invoice_Details sheet
    - Impl fill out of multiple CNTLINE for each CNTITEM.
    - Impl selection of distribution ID. Add tab, new button and dialog for AccountingCode
      in Financial Admin. NB impl finding code by name and description.
  * Export invoices for only selected jobs?
  * Flag job as invoiced after export?
  * Make sure to add AccountingCode table to all along with data.  
- Fix up the client editing in the job detail dialog. May update or reset the 
  address or contact when the client dialog returns.
- For client credit status check, note client is a credit client if they are "TRADE"
  and the credit limit is not 0.
- Impl "ComplianceSurveyDialogForm"  
- Impl "dirty" and save() for each "subclass" and save only when dirty. 
- Replace update*() with single update**() method where possible.
- Impl "new compiance survey" and other functions in dashboard and maintab view.  
- Impl "complianceSurveyToolsMenuButton" menu button
- Fix up "compliance" toolbars so they look like the rest of JMTS.
- Update survey table when dialog is closed.
- Impl all existing reports from "package".
- In "Jobs entered by employee" change "sample" column to "product".
- Add description, 2 departments, 3 date periods etc. in class Report.
- For reports make company specific value parameters such as company name and logo.
- Check for reports/queries on jobs that do not have any samples. Check for example 
  that these jobs are counted despite not have samples. 
- Do report on showing incomplete jobs for a given period by department for which a cost 
  estimate was given and full payment not made.
- When job is job/costing is closed without saving, set "dirty" to false.
- Use the "Jamaican Catalogue DBS" Excel workbook as basis for standards database.
  * Add DocumentTracking to DocumentStandard class.
- Do Pre-shipment inspection form based on excel sheets in compliance folder.
  * Create a table called PreshipmentInspection.
- Follow up with Garfield re service contract.
- Impl. preventing change once job is marked completed. This may already exist
  so verify if it works if so.
- Add "Parent earning jobs" and change "Parent jobs only" to "Parent jobs".
- Find way to allow department heads to use "General" and other authorized 
  search types.
- Implement adding new employee in user dialog.
- Revamp the privilege system. If a person only have the privilege to enter/edit own
- Paste the text directly into a CODE_OF_CONDUCT file in your repository. 
  NB: Keep the file in your project’s root directory so it’s easy to find, and link 
  to it from your README.
- Add a search types that find jobs that are: 
(i) earning parent jobs 
(ii) earning jobs
(iii) non-earning jobs
- Allow printing of approved or invoiced jobs.
- Send email notification when invoice is approved.
- If a job has samples with the same search text return only one instance of the 
  job in the search results. Add search on the following fields: Product common name,
  country, sampledBy, Additional details.
  * Implement search that deals with case when important fields are null such as
    jobSamples.
- Add divisions to Business/Organizations
- Add "+Client" to the dashboard JM "New" menu.
- NOTE: postMail() hard-coded values to be made system options.
- Find way to deactivate old costing templates...Add "active" field to "JobCostingAndPayment"
  and provide interface to deactivate the old templates.
- Price list for all of documents/jobs. Send RA email reminder.
- Remove Department from user profile and use employee field to find the 
  the user's department when needed.
- Sync opened job: Check if the currently opened job was saved since it was opened or last saved
   by the user and inform and take appropriate action if the user tries to save.
   Test with 2 users logged in with different browsers.
- Show date of activity for user in user profiles table: Attach "tracking" feature 
  to the user profile for this.
- Put methods such as approveJobCosting() in their associated class such as JCP 
  so they can be reused by web services developed independently of the JMTS by using
  the BEL.
- Add invoiceDate to JobStatusAndTracking class and database.
- Some report templates are given in system options while others are given in Report table. 
  Given all of them in Report table and delete the system options over time.
- For all the "costing dates", determine what should be the true "Costing Date" or the 
  date that the costing is deemed to be completed.
- Add "+Client" to JM dashboard "New" menu.
- Merge the "edit/delete" buttons in contacts and address table in an "Actions"
  menu as is done for samples table. Impl paging for ech table.
- Replace organizationName system option use with "Internal" Business class 
  when this feature is implemented.
- Impl sending alerts to all or specific persons. The messages would be displayed
  when the use just logs on or popup if the user is already logged on.
- Prevent changing adding/editing cost components once costing is approved/invoiced.
  * Programme so only the approver can edit after approval?
- Display message if an object's dialog is closed but not saved.
- Lock job costing after it is approved. Allow assigned department to edit?
- Costing templates takes long to appear when dropdown is first selected. Fix!
- Use BusinessEntities.postMail and retire use of "postMail" methods in JM
- Change category names from all caps especially for system configuration.
- Amount due still showing for old jobs with deposit. Fix!
- Cancelling a cost component edit still shows the edited component in the table
  Fix!
- Representatives should see jobs when they select “My  jobs” in “Job Search”.
- Add multiple pages for addresses, contacts and samples.
- As discussed in our meeting is it possible to have the option to enter samples
as A1, A2, A3…etc? Currently, the system only allows us to enter them as A, B, C…etc. 
This option will assist in calculating the number of samples for our Commercial Sterility jobs. 
As it is, we are required to count them manually which is extremely time consuming
- In the analytical report:
1.	On the jobs report tab could you at the base of the sheet count “ # of late jobs” = 
And “# of on-time jobs” = 
“Average TAT” (date completed – date submitted) in days = 
2.	On the employee report tab could an additional column be inserted for avg TAT for each line with the staff?
- The current release allows the “deputy head” to have limited privileges. 
A future release will allow directors, managers, team leaders and acting team leaders 
to have the privileges you mentioned and more. The system will also allow other 
persons in a department to be temporarily granted these privileges. 
This release will be around early July.
- Please let the GCT default to 16.5% except for activities done by NCRA. 
  Therefore if NCRA subcontracts a job to BSJ, the GCT would carried only for the BSJ job.
- Update contact and billing address when client is edited to address the invalid address issue
  experienced. Use the ids to find the address/contact for doing the update.
- Do draft proposals re 360/Elastic search, business analysis, items on previous contract,
  etc. for Orville and next contract.
- The name and description fields do not fit on form in dark hive.
- Add buttons to remoove/edit divisions/departments from an organization.
- Check that maxDaysPassInvoiceDate is set to Integer before testing jmts live 
  nd deploying.
- Impl creating parent job along with subcontract in 1 go.
- Impl option to import all the cost components from a subcontract into a parent
  job.
- Put back feature to create subcontract from a subcontract?
- Remove edit buttons from client dialog general tab.
- Impl Status or JobStatus class and use 

### Updates based on training, testing and feedback
- Allow creating subcontract from a subcontract.
- Indicate somewhere in the job dialog when a subcontract is being created.
- Fix sammple reference sequencing when a sample is deleted. Test with job 5740.
