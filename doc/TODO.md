
# Purchasing (Financial Administration)
- When PR tab is opened from the Tools menu or when new PR is created it shows 
  all PR in the PRs table instead of PRs from the users department. Fix!
- Iml adding action to the PR actions. Don't add an action more than once.
- Impl email messaging to the originator and other PR stakeholders.
- Complete PR and PO forms and impl auto generation from menu "Forms" menuitems.
- Populate PurchaseOrder with PR. 

# Accpac Invoicing & Credit Status Reporting
- Add CODE, ABBREVIATION to all accountingcode tables and class.
- Impl getting dist. codes for cost components.
- Impl exporting Excel file using "Invoice" button. The records must be
  must be checked to see if they can be exported first eg the client id is valid.
- Fix the columns related to "Client Id" and "Dist'n Ids" in the job costings
  table.
- Impl  getting dist. code of the from 1310-21-24-21 
   for eg. - dept code first, subgroup second and div code last
- Impl searching for job costings that have been approved but not invoiced so
  they can be invoiced.
- Impl invoice export. 
- Note each each count item have a dist code. eg discount, gct, testing & cal. etc.
  NB: How to determine the test/cal code eg 1310 for test.
- In onJobCostingCellEdit save JCP and client only if these were table cells 
    were edited. Also new Accounting codes are being saved. Fix!
- Impl fill out of Invoice_Details sheet
    - Impl fill out of multiple CNTLINE for each CNTITEM.
    - Impl selection of distribution ID. Add tab, new button and dialog for AccountingCode
      in Financial Admin. NB impl finding code by name and description.
- Export invoices for only selected jobs.
   Check that each selection can be exported eg the client code is valid. 
- Flag job as invoiced after export?
  * Make sure to add AccountingCode table to all along with data.
- In job dialog make the tabview dynamic and see what difference it makes.
- Add sys option that determines if the accpac invoices spreadsheet is to be
  exported.
- Put the name of the approver on the job costing.
- NB: Test as various users with various employee positions. 
- Add user preference to export invoioices when "Invoice" button is pressed?

# All Database Update and Deployment
- Add WORKPROGRESS to purchaserequisition table.
- Add canAddAccountingCode to privilege table.
- Add CODE, ABBREVIATION to accountingcode table. 
- Add CANADDSUPPLIER to privileges table.
- Add ACTIVE, INTERNAL, CATEGORY, DESCRIPTION fields to service table.
- Test using the database on BOSAPP from BOSHRMAPP to see the response in
  doing database searches and opening the job form.
- Position names: Team Leader, Divisional Manager, Divisional Director, 
    Finance Manager, Executive Director, Senior Engineer, Analyst, 
    Network Administrator, System Administrator etc.
- Update HRM tables with info from chart of accounts.
- Switch to GF3 before deployment.
- Enter the approval limits in test and production databases.

# Reports & Forms
- Do other client report template and upload.
- Impl new service contract and agreement.
- Do report for jobs not approved and incomplete up to 2017.
- Do not allow export of job costing from until it is approved

# Optional/Future
- Deploy and configure activiti in gf3 on local PC for testing .
- Design and include the purchasing process.
- Pull cost codes from Accpac.
- The "on hand now" field to the PR should link to inventory.
- Link to Accpac and do budget allocation.Canceling a PR/PO should reverse 
  Accpac budget allocation.
- Create report to show Accpac budget allocations.
- Get suppliers from Accpac.
- Impl supplier evaluation.

# Purchasing
- Impl PurchaseRequisition and PurchaseOrder classes.

# Legal Office
- Impl opening the saved document from the database. Refresh the edited document
  in the table and show as visited.
- The "visited" row feature not working for legal document. Fix.
  * It's because re-search is done after saving.
- Hide "For job earnings:" in classification dialog except for "Job" category.
- Do update Legal Office report with set and actual turnaround times. 
  Update "turnaround times" when doc is being saved?
- Switch to LegalOfficeReport2 in system admin.
- Search results do not seem to be sorted. Fix.

# HRM
- Create 
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

# General
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
  calendar component since a calendar component can't be grey out.

# Accpac
- Add all depts, subgroups and divisions to jmts live.
- https://smist08.wordpress.com/2013/01/01/the-sage-300-erp-java-api/

# CRM
- Meet with BSJ re features and requirements
- See docs in downloads folder
- Put isNameAndIdEditable as transient in Client class and get rid of 
  use of isNameAndIdEditable.
   
# HRM
- Fundamentals of Human Resource Management, pg 19
- Look up Java API for PeopleSoft HRM app.


