# Things to do

Modules for next release: Job Management, Legal Office, Client Management

## Next release (April 24-25, 2018)
- Address "Legal/Task Module/Management" issue: 
  * "Merge" reporting tab with the general reporting feature. Impl the 
    "Report Templates" tab in sys admin and add the "Legal" category field for
    legal office reports.
  * Store "Agreed Turnaround Time" and "Actual Turnaround Time" in datebase for
    for display in display in report.
  * Impl selecting strategic goal from drop down list.
- Deploy latest update. Add latest mysql driver and restart app server.
- Do presentation for OJ.
  * Note reports on areas of earnings as discussed with RA
  * Note sample tracking as discussed with RA and Engineering.
  * Improved search based on search types.
  * Mention customization for each organization. ie logo, firewall etc.
- Send email re changes and offer training where necessary. Send special email to
  select persons in Finance. Look for screen shots on home PC.
- Do JMTS Audit Data for OJ.

## Next release (May 24-25, 2018)
- Send email to NCRA re feedback on email sent re docs.
- Add "Parent earning jobs" and change "Parent jobs only" to "Parent jobs".
- Find way to allow department heads to use "General" and other authorized 
  search types.
- Update the service contract template with the new control number (MKTG_F_01/04) 
  and other footer information. 
  * Let Yuval do the update and create 3 templates one for each organization.
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
- Add Business class to Job class?
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
- Lock job costing after it is approved. Allow assigned department to edit?
- In "Jobs entered by employee" change "sample" column to "product".

