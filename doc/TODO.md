# Things to do

Modules for next release: Job Management, Legal Office, Client Management

## Next release (April 24-25, 2018)
- Color code jobs that are due, soon due, overdue and recently visited (light grey).
  * Do for all job browser tables.
  * For completed check if status "Completed" correspond to the completed flag.
- When the "active" checkbox for tabs on "List" is checked the entire tab view 
  refreshes. Fix!
- Doing search/new entry in the sys admin "Lists" and "Configuration" tabs cause a "switch"
  from the tab. Fix!
- Relook at the whole sequence of events when job costing parameters such as
  tax, total cost, estimate etc are edited.
- Impl real canceling of payment edits as is done with job samples.
- JMTS presentation with Balwayne.
- Impl "Organization" so that the user's correct organization can be
  selected when subcontracting a job.
  * Impl departmentPickListDialog. Check why Test Dept2. does not appear in the list.
  * Impl businessConverter/Validator. 
  * Add active/head fields to Business class
- Job should not be marked completed until approved: 
  * Is it possible to set the job entry to accept completed only when the costing has been approved?
- Update the service contract template with the new control number (MKTG_F_01/04) 
  and other footer information. 
  * Let Yuval do the update and create 3 templates one for each organization.
- Get list of jobs with incorrect clients from Bremmer.
  * See Bremmer's email for the details.
  * Do a "job report with clients" to facilitate extracting the list of jobs with the issue
- Impl preventing job costing and payment from being edited once job is marked as complete.
- Prevent job costing from being edited once it is approved and not when the job is marked completed.
- Remove the fields from the top of the cash payments table and put them in the
  cash payment dialog where possible. Put invoiceNumber in CashPayment class.
- Do report for Edmondson.
- Implement Legal Office module using the Legal Departmental Portal.
- 2/3 "--" departments were created in production database. Check why this occurred
  and ensure that all classes that refer to department use REFRESH cascade type.
- Deploy as jmtstrain for testing and training.
- Do report and invoice
- Train Customer Service/Finance/Engineering/Legal Office.

## Next release (May 24-25, 2018)
- "--" departments are being created delete them and find reason why it is happening.
- Setup http://bosapp/jmts and link the current app on boshrmapp to it using 
  stealth url forwarding. Use the technique from zoneedit.
- Send proposed features to NCRA based on inception report.
- Fix job search slowness:
  * Setup GF3 and test MySQL/JPA slowness
  * Checkout if indices are required
  * Try with the local mysql57 with the 5.1.45 driver.
  * Try what's at https://zeroturnaround.com/rebellabs/how-to-use-jpa-correctly-to-avoid-complaints-of-a-slow-application/
    and https://zeroturnaround.com/rebellabs/three-jpa-2-1-features-that-will-boost-your-applications-performance/
  * Use database pagination with setFirstResult() and setMaxResults()
- Include javascript check of a variable if it is null as a means to determine
  if connection is live in keepAlive code. Use something like context.addCallbackParam("jobCompleted", false);
- Implement adding new employee in user dialog.
- Revamp the privilege system. If a person only have the privilege to enter/edit own
- Paste the text directly into a CODE_OF_CONDUCT file in your repository. 
  Keep the file in your project’s root directory so it’s easy to find, and link 
  to it from your README.
- Make address and contact dialogs external and implement adding contact/address 
  via the client's action menu.
- Add a search types that find jobs that are: 
(i) earning parent jobs 
(ii) earning jobs
(iii) non-earning jobs
- Allow printing of approved or invoiced jobs.
- Send email notification when invoice is approved.
- Assign BusinessOffices to Business class. 
- Add Business class to Job class.
- If a job has samples with the same search text return only one instance of the 
  job in the search results. Add search on the following fields: Product common name,
  country, sampledBy, Additional details.
  * Implement search that deals with case when important fields are null such as
    jobSamples.
- Add divisions to Business/Organizations
- NOTE: postMail() hard-coded values to be made system options.
- Find way to deactivate old costing templates...Add "active" field to "JobCostingAndPayment"
  and provide interface to deactivate the old templates.
- Implement entering sampling information as required by Micro B. and may be Chemistry.
- Get client credit status showing same info as as shown by accpac.
- Price list for all of documents/jobs. 
- Setup jmts-processes as Activiti explorer for JMTS. Use jmts/jmtstest as the
  databases.
- Integrate Activiti with BSJ LDAP (AD).
- Remove Department from user profile and employee field to find the 
  the user's department when needed.
- Sync opened job: Check if the currently opened job was saved since it was opened or last saved
   by the user and inform and take appropriate action if the user tries to save.
   Test with 2 users logged in with different browsers.
- Show date of activity for user in user profiles table: Attach "tracking" feature 
  to the user profile for this.
- Put "Reports" configuration in Sys Admin Configuration tab.
  * Add description, 2 departments, 3 date periods etc. in class Report.
  * Implement and include report templates for all reports generated to date.
  * For reports make company specific value parameters such as company name and logo.
  * Check for reports/queries on jobs that do not have any samples. 
  Check for example that these jobs are counted despite not have samples. 
- Assign git tag to next release.
- Implement "Templates" tab with "Form", "Letter" and "Report" sub tabs.
- Make currency symbol a system option.
- Put methods such as approveJobCosting() in their associated class such as JCP 
  so they can be reused by web services developed independently of the JMTS by using
  the BEL.
- Add invoiceDate to JobStatusAndTracking class and database.
- Some report templates are given in system options while others are given in Report table. 
  Given all of them in Report table and delete the system options over time.
- For all the costing dates, determine what should be the "Costing Date" or the 
  date that the costing is deemed to be completed.
- Add clients tab to system admin main tab.
- Merge the "edit/delete" buttons in contacts and address table in an "Actions"
  menu as is done for samples table.
- Replace organizationName system option use with "Internal" Business class 
  when this feature is implemented.
- Impl sending alerts to all or specific persons. The messages would be displayed
  when the use just logs on or popup if the user is already logged on.
- Prevent changing adding/editing cost components once costing is approved/invoiced.
- The "Jobs entered by employee" report does not show the samples.
- Display message if an object's dialog is closed but not saved.
- Depreciate the "deposit" and subsequently remove the field from the JCP class
  and database. May have to create a cash payment from the deposit field fist.

