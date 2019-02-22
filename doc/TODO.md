# Tasks

## Accpac Invoicing, Credit Status Reporting, Job Costing
- Impl Tax class for storing GCT and other taxes. Add AccountingCode as field 
  called "code" for specifying the distribution code. Impl automatic application 
  of tax based on department. Use another entity class as basis.
- Impl Discount class with value, valueType, description, AccountingCode etc 
- Impl new service contract and agreement using new files sent by GA. Add menu 
    items to print contract and agreement separately. Check that correct deposit is 
    printed on the contract. Update job costing form to include approver and receipt #s.
- Add "Billing Information" tab to client dialog with discount, credit limit, 
    tax exempt etc.
- Impl automtatic pulling of Accpac info into JMTS client class where possible.
- Impl getDeposits() and do not overwrite the deposit field in JobCostingAndPayment.
  Do similar for "getReceiptNumbers()" and other fields dealing with payment.
- See if two values can be combined into 1 column in SQL.
- When new cash payment is added the "edited" status is not changed. Fix.
- Fix printing of receipts on job costing using 25/2018/9506 and ensure job costing approver
  appears on the printed job costing.
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
- Test Accpac credit status feature in light of the changes made to AccPacCustomer.
- Make credit status dialog about 25 - 50 pixels taller.
- Create FinancialAccount and FinancialTransaction classes for the accounting module of the jmts. Let FinancialTransaction class Implement the  Transaction interface and FinancialAccount class inherit from Account.
- Put "active" field in the JobCostingAndPayment class and use it to manage the visibility of costing templates.

## Purchasing Module 
- Adding/Editing supplier in the PR dialog does not seem to work. Fix.
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
- Implement adding links to attachments
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

## General
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
- Do not allow export of job costing from until it is approved?
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

## Database update and deployment
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
    
## Accpac Integration
- See https://smist08.wordpress.com/2013/01/01/the-sage-300-erp-java-api/ for info
  on Java API. Check other Sage/Accpac posts from said blogger.
  * Check the exact version of Sage 300 and the required jar files for the API
- Get more details for Inter-billing upload to Accpac from Finance.

## Inventory
- Get more details then start app development.

## Fixed Assets
- Get fixed assets used in tests/calibrations.
- Get more details then start app development.
- Let other assets extend the Asset class.
