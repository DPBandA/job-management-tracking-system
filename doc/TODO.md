
# Accpac Invoicing & Credit Status Reporting
- Impl HR module with HumanResourceManager class:
  * Add fields to Division class and update all databases
  * Create divisions and divisional groups tabs.
  * Create labs and units within departments.
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

# Reports & Forms
- Do client report template and upload.
- Impl new service contract and agreement.
- Do report for jobs not approved and incomplete up to 2017.


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

# Impl HR module with HumanResourceManager class
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

# General
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

# Accpac
- Add all depts, subgroups and divisions to jmts live.
- https://smist08.wordpress.com/2013/01/01/the-sage-300-erp-java-api/
   
# HRM
- Look up Java API for PeopleSoft HRM app.
