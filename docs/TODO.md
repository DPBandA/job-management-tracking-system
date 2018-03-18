# Things to do

## Database update
- Add JMUser fields: 
- Add MODULES_ID to JMUsers and modules table to database. 
- Add Module table and fields

## Inception Report and Misc
- Get procedure documents from QEMS of the BSJ, NCRA and NCBJ
- Provide content and format report based on BABOK if possible.
- Get requirements for Food Dept.

## Testing, Training & Misc
- Do not automatically insert the default contact in job contact for existing jobs.
  * Test this out with existing BSJ jobs.
- Impl creating and editing Job. Look out for "Named" and "ManagedBean" issue.
  * Run financeManager.setEnableOnlyPaymentEditing(); where appropriate.
  * Fix closing job when it is dirty.
  * Fix samples table
  * Fix creating and editing samples
  * Fix edit job costing..Fix dialog.
  * Fix opening job from cashier view table.
  * Check that all components display properly in the job dialog.
- Implement updating the jobs table loading a job from the table.
- :jobDialogForm:jobFormTabView:jobNumber/jobSamples was removed from samples table or related component
  "update" check if it is needed and should be reintroduced.
- Impl add MainView tab when selected in preferences.
- Impl dialog return and do search for all system option dialogs. Fix busy wait.
- Check out the call financeManager.setEnableOnlyPaymentEditing(false/true);
  and see if it is still relevant.
- Remove "Job Detail" tab and fully implement Job Dialog (eg new job and edit job 
  and job costing and all other job views that are generated from the various job.
- Impl "Organization" in JM user so that the user' correct organization can be
  selected when subcontracting a job.
  * Impl departmentPickListDialog. Check why Test Dept2. does not appear in the list.
  * Impl businessConverter/Validator. 
  * Add active/head fields to Business class
- Do report for Edmondson.
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
- Fix "Business office" dropdown menu in employee dialog.
- For existing jobs add the contact/address to the client if it does not already exist in the 
  client's list of contacts/addresses.
- Test "Approve" and "Invoiced" buttons.
- Run through process of entering, updating a job including subcontracts.
- Setup http://bosapp/jmts and link the current app on boshrmapp to it using 
  stealth url forwarding. Use the technique from zoneedit.
- Deploy on test production system.

## Next release
- Consider associating the "Privilege" and "Modules" classes with other business
  classes such as Division and Organization.
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
- Remove Department from user profile and use the employee field to find the 
  the user's department when needed
- Add general preference to hide/show dashboard.
- Add Standards and Certification as separate units in all databases.
- Check if PF('connectionErrorDialog').show(); works wherever it is used.
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
- widen the panels under service contract general services.
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
- Follow the BSJ 6 month proposal and do an assessment of the ISO procedures, 
  work instructions, procedures, audit reports etc. before starting development. 
  This may take at least a month. Study the proposal and layout a plan of action before starting.
- Get rid of use of "--" to determine if a job is sub contracted cause selecting
  it "de-subcontracts" a job and turn it into a parent job.
- Put 1px margin between toolbars and other components.
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
- Put pages in address and contact tables for client dialog.
- Impl preventing job costing and payment from being edited once job is marked as complete.
- Prevent job costing from being edited once it is approved and not when the job is marked completed.
- Some report templates are given in system options while others are given in Report table. 
  Given all of them in Report table and delete the system options over time.
- get(index) is used to get the main phone and fax numbers for a contact. 
  This method may result in the returning of the right number for fax or phone 
  so use the type field to solve this.
- NB: JobManagerUser represents a user profile that has a employee and privileges assigned. 
  Departments also have privileges and along with the user privileges are used 
  to determine the effective privileges of the user.
- Change CRM tab id and title to "Customer Management" and the Clients tab
  in MainTabView to "Customer Management".
- Remove department from user dialog.
- Sort out all that needs to be "locked" once a job's status is marked as complete. 
  Display a warning before the work progress is set to complete.
- Reset all UI when log out including searches results so that if a new user logs 
  in they will not see the searches of the previous user.
- Display message when user logs on without authentication
- Assign git tag to next release.
- Export sample condition(s) to service contract.
- Restrict generation of service contract to an authorized person/department?
  Put client ID# (TRN etc.) into service contract?
- Create LegalTask that extends Task and create other concrete Task classes for CIAO for example. 
- Create Tracking class that can attached to any class to enable tracking of that class. Fields coul include loginDateTime.
- Create process package for strore BPMN files.
- Put button in service contract tab to generate service contract.
- Create dialogs for classification and other objects instead of just adding a row in the table.
- Make "Keep Alive" system.out display a system option that is false by default.
- Impl copy of costing component from components table.
- Implement the max character lengths as system options and set limits on the maximum 
dollar values that can be entered as system options. 
- Consider the maximum double values imposed by Java the possibility that values 
can overflow when added divided or multiplied.
- Prevent changing a job's parent department once it is has subcontracts.
- Put cost component type in job costing analysis to show if costing is fixed variable etc. or put n/a in the rate and hours/unit column when the cost component type is fixed or subcontract.
- Check that the privileges set for user and department are impl properly.
- Implement search other fields such as descriptions for sysadmin objects.
- Look seriously at the maximum integer and double values that can be represented by Java and take these into consideration when working with currency and integer values.
- Disable fields such as client, department etc. once the service contract has been exported or job has been subcontracted. Add a field in the Job class for this and allow only customer service, sysadmin or other designates to change it.
- Change label for department to "Parent department" when a job is subcontracted.
- Get rid of use of "--" for default objects.
- Let users tab filter active users, users that are authenticated and users that have recent activities.
- Create tax table and use it similar to gnucash to apply taxes to costs on a departmental basis.
- Put toolbar in service contract tab to export service contract, uncheck as as exported, email service contract etc.
- In status and tracking add tracking table to show all activities performed on the job. Add section that allows added new action/activity such job completion. See design layouts provide by G. Allen.
- Show the list of privileges a user has in the preference dialog. Copy the UI from sysadmin and make it read only...put them in a separate file and use to bring them in.
- Impl use of camera to take pics of samples etc.
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
