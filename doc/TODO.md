## Accounts Receivable Feature (v3.4.0)
- Fill out client "Billing Information" tab and test it with the creation of a new client.
  * Set the client's credit limit and name to what is in accpac when the Account ID is selected.
  * Impl find by accpac customer id and use it to where possible to init client class.
- Fix the selection client/accounting ID in the job costing table.
- Add "Billing Information" tab to client dialog with discount, credit limit, 
  tax exempt etc. Get the accpac credit status and limit when the accounting code is
  entered. Accpac customer as field name accountingClient.
  * Create AccPacCustomer converter and use it in billing address autocomplete.
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
- Impl  getting dist. code of the from 1310-21-24-21 ie dept code first, subgroup 
  second and div code last.
- Impl searching for job costings that have been approved but not invoiced so
  they can be invoiced.
- Note each each count item have a dist code. eg discount, gct, testing & cal. etc.
  How to determine the test/cal code eg 1310 for test? Use the service selected in the service contract,
  the department to which the job is assigned etc.
- In onJobCostingCellEdit save JCP and client only if these were table cells 
    were edited. Also new Accounting codes are being saved. Fix!
- Add otherServices and services field for storing services offered for a job. Add
  items to services when a service contract item is selected.
- Use the job subcategory to determine the distribution code by assigning an accounting code to each job subcategory. 
  Remember to add accountingCodes to existing categories and sub categories.
- Associate each service with a job category and sub category. Add the fields to the class.
- Impl fill out of Invoice_Details sheet
    * Impl fill out of multiple CNTLINE for each CNTITEM.
    * Impl selection of distribution ID. Add tab, new button and dialog for AccountingCode
      in Financial Admin. NB impl finding code by name and description.
- Impl invoice export.
- Export invoices for only selected jobs. Check that each selection can be exported eg the client code is valid. 
- Flag job as invoiced after export? Add accounting codes to table.
- Add sys option that determines if the accpac invoices spreadsheet is to be
  exported. Add user preference to export invoices when "Invoice" button is pressed?
- Test Accpac credit status feature in light of the changes made to AccPacCustomer.
- Make credit status dialog about 25 - 50 pixels taller.
- Create FinancialAccount and FinancialTransaction classes for the accounting module of the jmts. Let FinancialTransaction class Implement the  Transaction interface and FinancialAccount class inherit from Account.
- Put "active" field in the JobCostingAndPayment class and use it to manage the visibility of costing templates.
- Put Job costing approval date in Job Costing dialog
- "(edited)" is still shown when Job Costing is edited although costing is already saved.
- Comment out PR and supplier out of search types for finance and JM.
- Impl automatic application of tax based on department or classification of job.
  find out for which and department and classification a tax should apply and
  prompt for a tax if it is 0.0.
- Download new files sent by GA and impl new service contract and agreement. Add menu 
  items to print contract and agreement separately. Check that correct deposit, discount and tax 
  are printed on the contract.
- When new client is being added clear out the existing client, billing address
  and contact.
- Add privilege for changing client billing info or use some other existing criteria?
- Add tax field to JobCategory, JobSubCategory and Classification classes. May have
  to give access to these in fin admin.
- Put costing approved yes/no column in the job costing with invoices and uninvoiced jobs reports.
- Check why job costing can be checked as approved with no approval date.
- Take out extra classification dialog navogation out of faces config. Change 
  admin to legal for legal document dialog.
- "Unrender" modules that are not being before next release.
- Use debug flag to display "Test & Training Version" 
- Set if a job's billing address and contact can be set automatically when a client is selected.
- Automatically get the client's discount and set it the job cost discount when a client is selected.
- Use the debug flag to display Test & Training Version instead of subtitle in jmts.
- Check if job status tracking class is saved in batch or normal approval or invoicing.

### Database update and deployment
- Run DiscountAndTaxTest to create Tax table if it is not created automatically.
- Add ISTAXABLE (BIT) to classification, jobcategory and jobsubcategory tables.
- Add DISCOUNT_ID (BIGINT, INDEX), FINANCIALACCOUNT_IDCUST (CHAR(12), INDEX),
  BILLINGADDRESS_ID (BIGINT, INDEX) to client table.
- Add TAX_ID (BIGINT, INDEX), DISCOUNT_ID (BIGINT, INDEX), COSTINGPREPAREDBY_ID (BIGINT, INDEX),
  COSTINGINVOICEDBY_ID (BIGINT, INDEX) to jobcostingandpayment table.
- Add DATECOSTINGINVOICED (DATE) to jobstatusandtracking table.
- Add tax and discount with name "0.0" and value 0.0.
- Copy Job Costing.jxml file to c:\jasperreports and use .jxrml file instead of 
  .jasper to generate job costing.
- Add all GCT taxes.

### Release Notes (v3.4.0)
- Tax and discount tables and user interface elements added.
- Corrected the standard financial date periods.
- Updated and deployed the Job Costing and Service Contract forms.