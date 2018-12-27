# Things to do (Master)

## Financial Administration
- Create Tax class. Add name, type, threshold, thresholdType, fixed, percentage,fields.
- Create deduction class. Add name, type, threshold, thresholdType, percentage fields.
- Make Purchasing a module Financial Services . Delete the Purchasing app from github for now?
- Implement Payroll class to have a Pay collection field, date, creator, etc. 
  The Pay class should have employee, salary, net salary, date, taxes etc.
- Use phone scanner app to scan front and back cover of books and documents.
  Throw away as many documents as possible.
- Open the purchase req tab by default for now but allow user to choose which tab
  to open by default for financial admin module.
- Add as system option the positions that are allowed to approve a PR. Check 
  that an approver has one of these positions before allowing them to approve.
- Impl search for PR and supplier as is done in fin admin dashboard.
- Do not save supplier or PR if they were not edited.
- Include the updates done to PR in the email.
- Add feature allow sending email from the PR dialog or PRs table to stakeholders. This feature could
  be actvivated for procurement officers only.

### Inventory
- Get more details then start app development.

### Fixed Assets
- Get fixed assets used in tests/calibrations.
- Get more details then start app development.
- Let other assets extend the Asset class.

### Learning Management System (LMS)
- See docs in download folder
- Impl training schedule
- Get more details then start app development.
- Get link to infoserve LMS from ITU.
- Get training evalution forms  (3 levels).

### Inter-billing upload to Accpac
- See email attachment from Simone

## Customer Relationship Management (CRM)
- Get HBS Access database for importation into JMTS.
- Add identification and identificationType to Client class.
- Create MarketingCampaign class to capture telemarketing and other types of 
  marketing campaigns. Add Telemarketing (check spelling) as one type of marketing campaign.
- Add menu buttons in client dialog general tab to add/edit most recent address or contact.
- Where does the marketing process start?
- How should knowledge management be incorporated?
- Add dialog return to "Client Management" menu item.

## Documentation
- Update JMTS user manual

## Reports
- Create parent/child jobs report for Ministry of Finance based on request.
- Remove the jasper-fonts from the hrm server and restart it to make use of the
  jasper fonts deployed with JMTS
- Add default report category to JobManagerUser.
- Including other existing reports where necessary.
- Impl finance report showing jobs that are supposed to have deposits but don't
  "Jobs Requiring Deposits" as the report.
- Deal with the multiple row entries all reports especially those used by 
  Customer Service. See if "DISTINCT" solves the problem.
- Impl finding report by name and description?
- Try again to centre report title.
- Check and fix up font for "Frequently Requested Jobs by Category" and may be 
  other reports.
- Do training doc for Legal Office, list of requested changes and arrange training via email.
  Request additional reports?
- Add test and calibration values to monthly report.
- Reports on recalibration date for equipment.

## HRM
- Take HRM out of system admin.
- Create HRM unit privilege flag. It it is null set its value to the sysadmin 
  flag.
- Create and add Contractor to BEL/HRM? 
- To Organization/Business class add Divisions, Subgroups, Departments, Clients

## Updates based on training, testing and feedback
- The delete payment dialog text does not show in dark hive. Fix!
- Reload entities from database before loading in dialogs.
- Allow creating subcontract from a subcontract.
- Indicate somewhere in the job dialog when a subcontract is being created.
- Fix sammple reference sequencing when a sample is deleted. Test with job 5740.
- Widen credit status dialog and cost component dialog for better display 
  in dark hive theme.
- Display prompt when there are completed subcontracted jobs when the Job Costing dialog
  is opened:
  * Impl "Existing Subcontracts" dialog...copy the "Delete Payment" dialog.
- Fix sample sequencing when a sample is deleted. Note that sample does not 
  change initially when the # of samples is reduced.
  * Do sample sorted by id and not reference taking into account null ids as is
    done for the DatePeriod class. Do not use the string version of the id as is
    done for date period. Compare the value instead. Look for all classes that
    use the string version and change them.
  * The wrong sample seem to get edited after one is deleted. Fix!
- Cordinate the editing of a client and what happens when the dialog returns
  to the job dialog.
- Alerts should be sent to person who entered job, job assignee, representative(s)
  department head where appropriate eg send alert to parent job assignee and the
  person who entered the job when child job is approved.
- Ensure that a job is not marked completed until the child job costing is included?
- Remember to  create labs and units within departments.

## General
- Impl open and close tab methods as is done for MainTabView.
- Remove the busy wait icon from the job dialog tab change and see if this makes 
  any difference on 172.10.0.18/jmts.
- In the "additional services" tab add list in otherRequestedServices field
  in job services contract. Remove "other" checkboxes. For intended market remove 
  checkbox and allow adding countries as other markets.
- In the job dialog change the Service Contract tab to "Services". Create a tab
  called "Service Contract" under the "Services" tab and move the contents of
  "Additional Service" to it.
- Add business type to the organization dialog. Types could include Regulatory,
  Learning Institution etc.
- Create Management classes from Manager classes so that the business logic
  can be used by other classes as is done with ClientManager/ClientManagement classes.
- Fix up the client editing in the job detail dialog. May update or reset the 
  address or contact when the client dialog returns.
- For client credit status check, note client is a credit client if they are "TRADE"
  and the credit limit is not 0.
- Impl "ComplianceSurveyDialogForm" 
- Add option to select the main tab view to open when first logged in.
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
- Check that all growl message have full stop.
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
- Impl system so it is not dependent on "--" department for job number 
  saving subcontracted job etc.
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
- Anybody can mark change the work status of a job even if they don't belong to the department to
  which the job is assigned. Fix!
- Add Tax, Payroll and Deduction classes to BEL.
- To reuse MainTabView and Dashboard put the id of the components in the constructor
  and tab initialization outside of the constructor.
- Doing cell edit in table does not validate the object before saving. Fix!!
- Prevent browser from  saving usernames and password.
- Add cancel button to preferences dialog.

## Job Edit & Synchronization
- Create/maintain list of opened jobs in the Application class.
  * Set opened date when open job and when keepalive runs..
- Add Opened Jobs tab in Sys Admin with search text box and refresh button
- Do sync for all job view tables.
- Ensure access to currentJob is synchronized since it can be accessed by "keepalive"
  other code at the same time.
- Fix up job number generation to do it the way it is done in PR without the use
  of static method.

## Farming
- Create Livestock class to represent fish and other farm animals. Create Broodstock as 
subclass of Livestock.

