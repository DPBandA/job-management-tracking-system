## Accounts Receivable Feature (v3.4.0)
- Impl use of Tax and Discount classes in the JobCostingAndPayment class instead of 
  the existing individual fields.
- Impl automatic application of tax based on department.
- Impl Discount class with value, valueType, description, AccountingCode etc 
- Impl new service contract and agreement using new files sent by GA. Add menu 
  items to print contract and agreement separately. Check that correct deposit is 
  printed on the contract. Update job costing form to include approver and receipt #s.
- Add "Billing Information" tab to client dialog with discount, credit limit, 
  tax exempt etc. Get the accpac credit status and limit when the accounting code is
  entered.
- Add Services tab to system admin. Add services field to Job class. Add/remove the 
  respective service when a service is selected/unselected.
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

### Deployment
- Run TaxTest to create Tax table if it is not created automatically.
