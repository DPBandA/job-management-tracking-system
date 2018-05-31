# Things to do

Status Update Notes for May 28 - June 1, 2018
- Add users, update databse, etc. under sys admin
- Attend meeting with Engineering div. (June 1)
- Mention work on BPM 

Month 1 modules: Job Management, Legal Office, Client Management
Month 2 modules: Standards Compliance, Certification (Legal Metrology instead), Foods Inspectorate
Month 3 modules: Legal Metrology, Task Management, Service Request.

## Next release (May 24-25, 2018)
- Make cost component a textarea component.
- Check why seach is automatically done when text pasted while not for other.
  Check if change event or keyup event is being used.
- Comment out search menu items in tools for in system admin.
- Respond to rohan emails.
- Read ED report.
- Complete Legal module:
  * Add report template to
  * Filter list of reports based on category.
- Setup dashboard and main tab view for Compliance module:
  * Impl "ComplianceSurveyDialogForm"  
  * Impl "dirty" and save() for each "subclass" and save only when dirty. 
  * Replace update*() with single update**() method where possible.
  * Track object dirtiness as with other objects in the system eg Address.
  * Impl "new survey" and other functions in dashboard and maintab view.  
  * Impl "complianceSurveyToolsMenuButton" menu button
  * Fix up the toolbars so they look like the rest of JMTS.
  * Update survey table when dialog is closed.
- Setup dashboard and main tab view for LegalMetrology and Certification modules:
- Revamps the Reports Tab:
  * Impl all existing reports from "package".
  * Impl access to module via the dashboard.
  * Impl report dialog
  * All reports except monthly report and analytical reports not working. Fix!    
  * Impl getting reports by category.
  * Add report description.
  * Impl and add Legal reports.
  * Get rid of use of jasperreports folder for now.
  * The "Jobs entered by employee" report does not show the samples. 
    In "Jobs entered by employee" change "sample" column to "product".
  * Add description, 2 departments, 3 date periods etc. in class Report.
  * For reports make company specific value parameters such as company name and logo.
  * Check for reports/queries on jobs that do not have any samples. Check for example 
    that these jobs are counted despite not have samples. 
- When job is job/costing is closed without saving, set "dirty" to false.
- Do Pre-shipment inspection form based on excel sheets in compliance folder.
  * Create a table called PreshipmentInspection.
- Follow up with Garfield re service contract.
- Impl. preventing change once job is marked completed. This may already exist
  so verify if it works if so.
- Add "Parent earning jobs" and change "Parent jobs only" to "Parent jobs".
- Find way to allow department heads to use "General" and other authorized 
  search types.
- Impl reset() in LegalDocumentManager.
- Fix job search slowness:
  * Select the exact fields that will be displayed in the respect "Job View" tables. 
    Do this for others such as the ones used in "completeObjects" methods in the
    autocomplete component.
  * Try what's at https://zeroturnaround.com/rebellabs/how-to-use-jpa-correctly-to-avoid-complaints-of-a-slow-application/
    and https://zeroturnaround.com/rebellabs/three-jpa-2-1-features-that-will-boost-your-applications-performance/
  * Use database pagination with setFirstResult() and setMaxResults()??
- Implement adding new employee in user dialog.
- Revamp the privilege system. If a person only have the privilege to enter/edit own
- Paste the text directly into a CODE_OF_CONDUCT file in your repository. 
  NB: Keep the file in your project’s root directory so it’s easy to find, and link 
  to it from your README.
- Make address and contact dialogs external and implement adding contact/address 
  via the client's action menu.
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
- Implement entering sampling information as required by Micro B. and may be Chemistry.
- Get client credit status showing same info as as shown by accpac.
- Price list for all of documents/jobs. 
- Setup jmts-processes as Activiti explorer for JMTS. Use jmts/jmtstest as the
  databases.
- Integrate with Activiti with BSJ mail server.
- Integrate Activiti with BSJ LDAP (AD).
- Remove Department from user profile and use employee field to find the 
  the user's department when needed.
- Sync opened job: Check if the currently opened job was saved since it was opened or last saved
   by the user and inform and take appropriate action if the user tries to save.
   Test with 2 users logged in with different browsers.
- Show date of activity for user in user profiles table: Attach "tracking" feature 
  to the user profile for this.
- Put "Reports" configuration in Sys Admin Configuration tab. 
- Implement "Templates" tab with "Form", "Letter" and "Report" sub tabs.
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
- Display message if an object's dialog is closed but not saved.
- Lock job costing after it is approved. Allow assigned department to edit?
- Costing templates takes long to appear when dropdown is first selected. Fix!
- Fully integrate the use of Activi to facilitate the "low-code" or "no-code"
  paradigm.
- Use BusinessEntities.postMail and retire use of "postMail" methods in JM
- Change category names from all caps especially for system configuration.
- Amount due still showing for old jobs with deposit.
- Cancelling a cost component edit still shows the edited component in the table
  Fix!
- Representatives should see jobs when they select “My  jobs” in “Job Search”.
- Send email to reps if there are any.
- Add multiple pages for addresses, contacts and samples.
- Meet with S&T re:
- As discussed in our meeting is it possible to have the option to enter samples
as A1, A2, A3…etc? Currently, the system only allows us to enter them as A, B, C…etc. 
This option will assist in calculating the number of samples for our Commercial Sterility jobs. 
As it is, we are required to count them manually which is extremely time consuming
- In the analytical report:
1.	On the jobs report tab could you at the base of the sheet count “ # of late jobs” = 
And “# of on-time jobs” = 
“Average TAT” (date completed – date submitted) in days = 
2.	On the employee report tab could an additional column be inserted for avg TAT for each line with the staff?

  

