# Maintenance Tasks

## Reports
- Report on total earnings for a department for a specific period. 
  First see if there is a report that gives this info. 
- Do other client report template and upload.
- Impl new service contract and agreement.
- Do report for jobs not approved and incomplete up to 2017.
- Do not allow export of job costing from until it is approved
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

## Financial Administration
- Create service contract as jasper report and add feature to generate blank form.
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
- Add feature allow sending email from the PR dialog or PRs table to stakeholders. 
  This feature could be activated for procurement officers only.
- Add job costing and purchase requisition forms to the Form templates tab in 
  Business entities tab and stop using system options to get the files. 
- Fix up PR jasper form by removing the overlapping line and put in missing borders.
- Values that are 0 set them n/a in the PR form and change the corresponding 
  parameter types to String.
- Make sure that more than one person with the same position cannot approve PR.
- Take the approvers section out of the page footer of the PR form and put it
  at the end of the report.
- Make the PO box the same width as the Rate and Cost column heading width combined.
- Right justing the total cost in the PR form.
- Centre "Suggested Supplier:" static text with the text field. It is not centred
  when exported to PDF on Linux. See if it is the same on Windows.
- Remove the default values from all parameters.
- Impl feature to send PO to supplier.
- Impl editing cost components in the table in the PR dialog.
- Use PrimeFaces TreeTable to create an accounting system like GnuCash.
  
### Purchasing Module 
- Pull cost codes from Accpac.
- The "on hand now" field to the PR should link to inventory.
- Link to Accpac and do budget allocation.Canceling a PR/PO should reverse 
  Accpac budget allocation.
- Create report to show Accpac budget allocations.
- Get suppliers from Accpac.
- Impl supplier evaluation.
- Only allow one of the set positions to approve otherwise an approval date will
  not be shown. Add system option that sets the positions that can approve PR.
- Allow entry of the urgency of the PR possibly via the priority field. Use an
  autocomplete component with A, B, C to indicate priority for now.
- Indicate the number of approvals in the email template?
- Add the supplier address to the PR and PO form.
- Do proposal to get cost codes, budgets and suppliers from Accpac.

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

### Accpac
- Add all depts, subgroups and divisions to jmts live.
- https://smist08.wordpress.com/2013/01/01/the-sage-300-erp-java-api/

## Customer Relationship Management (CRM)
- Meet with BSJ re features and requirements
- See docs in downloads folder
- Put isNameAndIdEditable as transient in Client class and get rid of 
  use of isNameAndIdEditable.
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
- Document templates variables such as {role} which is used in email templates.

## HRM
- Take HRM out of system admin.
- Create HRM unit privilege flag. It it is null set its value to the sysadmin 
  flag.
- Create and add Contractor to BEL/HRM? 
- To Organization/Business class add Divisions, Subgroups, Departments, Clients
- Reload entities before opening dialogs. Start with employee.
  * Load from database using setSelected* in <f:setPropertyActionListener>
    and redo search in the "dialogReturn" method.
- In departments tab in division dialog, list the departments that are in subgroups
  in addition to those that fall directly under the division.
- Deploy and subgroup and division records.
- Add code to generate "accounting codes" in Job Costing tab.
- When error occurs while saving job occurs and email is sent, a job number is 
still generated. This happened for job 6153 which was later saved to 6154. 
A valid job number should not be generated when this occurs.
- Deal with apparent creation of multiple 
"QEMS-OSH (Quality &amp; Environmental Management Systems - Occupational Health &amp; Safety)"
department which seems to be due to the "&" character or "&amp;". Try to delete the
in active ones.
- Impl searching for department on the code field.
- Add head to division dialog.
- Impl Labs and Department units.
- Instead of Notes put head in Dept, Subgroup and Division tables.
- In the relevant dialogs, layout fields as Code, Name, Head.
- Add payCycle to EmployeePosition class. Should be in Payroll class too?
- Fundamentals of Human Resource Management, pg 19
- Look up Java API for PeopleSoft HRM app.
- Trim names entered via any dialog.
- Do not make the entry of the email address mandatory in the employee dialog. Make
  mandatory in the User dialog. This is so that the HR personnel is not forced 
  know the email address of a new employee.

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
- Set all search results list in all managers to null when logout to prevent the next logged
  user from accessing that list.
- Misc config search results not sorted. Fix!
- Upgrade to using Java EE 7/8 and GF 5 on boshrmapp and bosapp ASAP.
- Set address type to billing for new addresses.
- Check out Server Performance Tuner in GF4/5 to see if this help the speed of 
  searches etc.
- Dr. Ramdonquestion: Can JMTS have more than one person able to untick complete?
  * Allow other persons apart from department "Head" and "Acting Head" and system admin.
- Reminder: include info re "Legal Office" module in JMTS User manual.
- Put Contact Management module in Future.md
- Impl automatic including child job cost based on various criteria such as 
  percentage, cost component type etc. -- Include this as proposal for future 
  work.
- Allow tracking information to added to the job even after it is marked completed
  * Impl tacking feature for this.
- Impl using only one EMF which is used by all session beans when an entity 
  manager is needed.
- Now that department is removed from JMUser, check that no code is broken.
- Send error email for only job save related exceptions. Not messages pertaining
  to privileges. Send emails to desbenn@gmail.com
- Remove major modules such as helpdesk, hrm, crm and stds dev and create as
separate projects using maven overlays.
- Add option to auto generate sample reference as is done with job number.
- Consider using oauth for authentication over the web.
- Check why admain2 is not used when admain is not available.
- Impl login with admin user and encrypted password.
- See if all passwords can be encrypted in system options.
- Receipt # not showing on some job costings. Impl the "Sing User Job Access" for
  to solve this which may be as result of one user overwriting the edits of 
  another.
- Impl UpdateAction as is done in PR class to determine alerts to send.
- In Authentication impl notification when login attempts reaches a certain amount
  eg. 2.
- Get rid of "shadow" when  mouse over menu item in edit client menu and others.
- Get rid of excess "--" employee.
- Display growl when user is not authenticated.
- Use dialogReturn through the system update the respective tables when dialogs
  are closed.
- Backup all affected projects and and change jmuser to user or userprofile.
  Eventually rename the jmuser table to user or userprofile.
- Set all dialog heights to 0 so the actual height is automatically set
  when the dialog is open.
- Remove TRN and add iden. type and iden. to client dialog. If iden type is TRN
  set the TRN field in the Client class.
- Add list of id type as autocomplete to supplier and client dialogs.
- Make Parish/State/Province autocomplete in address dialog.
- Impl searching or any part of client or supplier as is done with job entry.
- Use finance related methods in FinancialApplication and not JMTSApplication.
- Clean up unused or unneeded out of JMTSApplication. Some of this code is already
  in managers/WAL or can be put in them.
- Create Application class and put in WAL. Other application classes such as
  JMTSApplication can be put in it.
- Do Dashboard and MainTab tab init in JobManager and not in the contructors.
- Put the sync methods in the Application class from which all application scope
   classes would inherit. openedJobs would become openedObjects etc.
- Go through each manager and remove code that is not needed in that manager.
- Add a separate tab in user account dialog for sys admin.
- Impl giving privileges to specific tabs in sys admin such as hrm.
- Use uneditable greyed input text to display uneditable date field instead of 
  calendar component sin
- Checkout what causes this error to occure: javax.persistence.RollbackException: Exception [EclipseLink-4002] (Eclipse Persistence Services - 2.3.2.v20111125-r10461): org.eclipse.persistence.exceptions.DatabaseException
Internal Exception: com.mysql.jdbc.MysqlDataTruncation: Data truncation: Data too long for column 'COMMENT' at row 1
Error Code: 1406
Call: UPDATE job SET COMMENT = ?, JOBNUMBER = ?, JOBSEQUENCENUMBER = ? WHERE (ID = ?)
        bind => [4 parameters bound]
Query: UpdateObjectQuery(1514057)
