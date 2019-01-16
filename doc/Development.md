# Purchasing (Financial Administration)
- Editing supplier in the PR dialog does not seem to work. Fix.
- Complete PO Jasper form:
  * Set all parameters before exporting.
  * Change parameter quotation Number to quotationNumber.
- Add a "Order" tab that allows entry of information for the PO such as 
  terms. Add the required fields to the PR class.
- For PO form get the terms and conditions from the printed form and place after
  the page break in the jasper form.
- Use "Regular" and "Urgent" as priority codes.
- Allow only the PO to cancell a PR once it is saved.
- Add a default of 2 weeks to the date when the PR is fully approved (2 or 3 approvals) 
to arrive at the default "Expected date of completion". Allow changing this default period
in fin options.
- Set the limit in fin otions when a PR needs to go to procurement. 1.5M is the current limit.
show an alert when the total cost exceeds this limit. Put a note in the status note section
stating that the limit was exceeded.
- Send automatic email to persons that can approve the PR based on the criteria given by Rohan Anderson.
  Note only persons in the originator's division should get this email.
- Add the total amount to the PR emails templates.
- Send automatic PR emails to admin assistant of a department/division.
- Implement selecting the currency in the costcomponent. Implement "Currency"/ entity class to faciliate 
conversion between currencies using static methods. Currency class should have the ISO symbol and abbreviation.

# Accpac Invoicing & Credit Status Reporting
- Impl getting dist. codes for cost components.
- Impl exporting Excel file using "Invoice" button. The records must be
  must be checked to see if they can be exported first eg the client id is valid.
- Fix the columns related to "Client Id" and "Dist'n Ids" in the job costings
  table.
- Impl  getting dist. code of the from 1310-21-24-21 
   ie dept code first, subgroup second and div code last
- Impl searching for job costings that have been approved but not invoiced so
  they can be invoiced.
- Impl invoice export. 
- Note each each count item have a dist code. eg discount, gct, testing & cal. etc.
  How to determine the test/cal code eg 1310 for test? Use the service selected in the service contract,
  the department to which the job is assigned etc.
- In onJobCostingCellEdit save JCP and client only if these were table cells 
    were edited. Also new Accounting codes are being saved. Fix!
- Impl fill out of Invoice_Details sheet
    * Impl fill out of multiple CNTLINE for each CNTITEM.
    * Impl selection of distribution ID. Add tab, new button and dialog for AccountingCode
      in Financial Admin. NB impl finding code by name and description.
- Export invoices for only selected jobs. Check that each selection can be exported eg the client code is valid. 
- Flag job as invoiced after export? Add accounting codes to table.
- Add sys option that determines if the accpac invoices spreadsheet is to be
  exported. Add user preference to export invoices when "Invoice" button is pressed?
- Put the name of the approver on the job costing.

# Database update and deployment
- Add "pr-email-template" to system.
- Add approvalDate to PR class and table for all databases.
- Enter the following employee positions into the all databases: 
  Team Leader, Divisional Manager, Divisional Director, 
  Finance Manager, Executive Director, Senior Engineer, Analyst, 
  Network Administrator, System Administrator.
- Enter the approval limits in the production database using info from the 
  file sent by Donna.
- Enter purchaseRequisition system option for the Purchase Requisition.jrxml
  file for now. Later this will be done in "Form templates" tab in Business Entities.
  
