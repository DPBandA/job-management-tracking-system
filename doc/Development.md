
# Purchasing (Financial Administration)
- Complete PR and PO forms and impl auto generation from menu "Forms" menuitems.
  * Add totalCost field to PR class?
  * Provide for entry of the unit field in the PR dialog.
- Populate PurchaseOrder with PR. 
- Impl "Actions" menu for general messages and for the PO to request
  approval from someone who is able to approve a PR.

# Accpac Invoicing & Credit Status Reporting
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

# Database update and deployment
- Add approvalDate to PR class and table for all databases.
- Enter the following employee positions into the all databases: 
  Team Leader, Divisional Manager, Divisional Director, 
  Finance Manager, Executive Director, Senior Engineer, Analyst, 
  Network Administrator, System Administrator.
- Enter the approval limits in the production database using info from the 
  file sent by Donna.