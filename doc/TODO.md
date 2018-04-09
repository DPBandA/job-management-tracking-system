# Things to do

Modules for next release: Job Management, Legal Office, Client Management

## Next release (April 24-25, 2018)
- Send email re Sonia's issue.
- Status update for OJ.
- JMTS presentation with Balwayne. 
- Check account/call @10:00 re payment.
- Make it known that if a person is given the "Can approve job costing" privilege
then they cannot approve a job if it is flagged completed. Review to see if this 
should be changed. Search for this privilege and see how it is handled.
- Hide/show the "Approved/Invoiced" buttons based on privilege.
- Implement assigning "reps" to a job as a means to assigning more than one person to job
  as required by Chemistry/Micro B.
- Color code jobs that are late/time and recently visited.
- Add privileges to add/edit contact/address and put client privileges in own tab.
- Change context.execute to PrimeFaces.executeScript.
- Add representatives field to Job class, database and form.
- Add general preference to hide/show dashboard.
- Add "closeable" option to PrimeFacesUtils.openDialog(). Make job dialog closeable
  and others not.
- Widen the panels under service contract general services.
- When the "active" checkbox for tabs on "List" is checked the entire tabview 
  refreshes. Fix!
- Doing search/new entry in the sys admin "Lists" and "Configuration" tabs cause a "switch"
  from the tab. Fix!
- Prevent changing adding/editing cost components once costing is approved/invoiced.
- Let client search in clients tab search any part of client's name.
- Add system option to display "Handling keep alive session..." in debugging mode.
- Relook at the whole sequence of events when job costing parameters such as
  tax, total cost, estimate etc are edited.
- Rename privilege table to privileges if it does not result in data lost.
- Do not save job also when an object in the Job class is edited if this is not
- Create the LegalDepartmentPortal as a git branch of the JMTS.
- Impl real canceling of payment edits as is done with job samples.
--------------------------------------------------------------------------------
- Impl "Organization" so that the user's correct organization can be
  selected when subcontracting a job.
  * Impl departmentPickListDialog. Check why Test Dept2. does not appear in the list.
  * Impl businessConverter/Validator. 
  * Add active/head fields to Business class
- Job should not be marked completed until approved.
- Update the service contract template with the new control number (MKTG_F_01/04) 
  and other footer information. 
  * Let G.A. do the update and create 3 templates one for each organization.
- Get list of jobs with incorrect clients from Dwight (not Dr Ramdon)
- Impl preventing job costing and payment from being edited once job is marked as complete.
- Prevent job costing from being edited once it is approved and not when the job is marked completed.
- Display message when user logs on without authentication.
- Make "Keep Alive" system.out display a system option that is false by default.
- Impl copy of costing component from components table.
- Implement the max character lengths as system options and set limits on the maximum 
dollar values that can be entered as system options. 
- Consider the maximum double values imposed by Java the possibility that values 
can overflow when added divided or multiplied.
- Prevent changing a job's parent department once it is has subcontracts.
- Put cost component type in job costing analysis to show if costing is fixed variable etc. 
  or put n/a in the rate and hours/unit column when the cost component type is fixed or subcontract.
- Check that the privileges set for user and department are impl properly.
- Implement search other fields such as descriptions for sysadmin objects.
- Disable fields such as client, department etc. once the service contract has been 
  exported or job has been subcontracted. Add a field in the Job class for this 
  and allow only customer service, sysadmin or other designates to change it.
- Change label for department to "Parent department" when a job is subcontracted.
- Get rid of use of "--" for default objects.
- Let users tab filter active users, users that are authenticated and users that have recent activities.
- Create tax table and use it similar to gnucash to apply taxes to costs on a departmental basis.
- In CashPayment replace payeeTitle, payeeFirstname and payeeLastname with "Contact payment"
  which would be part of the client associated with the job.
- Add feature to add picture to job sample record by using the camera attached to the system.
- Add system option to hide/show Job tabs and fields eg Samples tab could be hidden for a
  company that doesn't use it.
- Depreciate the "deposit" and subsequently remove the field from the JCP class
  and database. May have to create a cash payment from the deposit field fist.
- Add: country to Address dialog?
- Adopt the use of "Release Notes" to announce new JMTS releases.
- Let the subcontracted department be null for parent job and change the job numbering
  system and other business logic to reflect this fact. The use of "--" department
  should then be removed.
- Impl sending alerts to all or specific persons. The messages would be displayed
  when the use just logs on or popup if the user is already logged on.
- Impl organizations tab in system admin.
job then they should not be able to assign a job to anybody but themself. Similar
thing applies to department job entry/edit privilege.
- Do report for Edmondson.
- 2/3 "--" departments were created in production database. Check why this occurred
  and ensure that all classes that refer to department use REFRESH cascade type.
- Do report and invoice
- Train Customer Service/Finance/Engineering/Legal Office.

## Next release (May 24-25, 2018)
- Remove the fields from the top of the cash payments table and put them in the
  cash payment dialog where possible. Put invoiceNumber in CashPayment class.
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

