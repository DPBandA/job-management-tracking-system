## Accounts Receivable Feature (v3.4.0)
- Add services field to Job class.  
  * Add discounts as given in "BSJ Department Codes and Notes.pdf".
  * Add check boxes for other services such as certification. Look at the codes
    document received from BSJ for other services needed. 
    Complete editing ServiceContract class.
- Add defaultTax field client class.
- Ensure that default tax and discount are applied when a client is selected.
- Impl getDeposits() and do not overwrite the deposit field in JobCostingAndPayment.
  Do similar for "getReceiptNumbers()" and other fields dealing with payment.
- When new cash payment is added the "edited" status is not changed. Fix.
- Impl exporting Excel file using "Invoice" button. The records must be
  must be checked to see if they can be exported first eg the client id is valid.
- Impl  getting dist. code of the from 1310-21-24-21 ie dept code first, subgroup 
  second and div code last.
- Impl searching for job costings that have been approved but not invoiced so
  they can be invoiced.
- Impl invoice export.
  * Call addServices() before doing export to ensure that all relevant services are added.
  * Note each each count item have a dist code. eg discount, gct, testing & cal. etc.
    How to determine the test/cal code eg 1310 for test? Use the service selected in the service contract,
    the department to which the job is assigned etc.
  * Impl fill out of Invoice_Details sheet:
  * Impl fill out of multiple CNTLINE for each CNTITEM.
  * Impl selection of distribution ID. Add tab, new button and dialog for AccountingCode
    in Financial Admin. NB impl finding code by name and description.
  * Export invoices for only selected jobs. Check that each selection can be exported eg the client code is valid. 
  * Add sys option that determines if the accpac invoices spreadsheet is to be
    exported. Add user preference to export invoices when "Invoice" button is pressed?
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
  and contact??
- Take out extra classification dialog navigation out of faces config. Change 
  admin to legal for legal document dialog.
- "Unrender" modules that are not being before next release.
- Check if a job's billing address and contact can be set automatically when a client is selected.
- Automatically get the client's discount and tax and set them when a client is selected.
- Use the debug flag to display Test & Training Version instead of subtitle in jmts.
- Add search for "Approved but not invoiced" jobs.

### Database update, deployment and testing
- Add ISTAXABLE (BIT) to classification, jobcategory and jobsubcategory tables.
- Add DISCOUNT_ID (BIGINT, INDEX), FINANCIALACCOUNT_IDCUST (CHAR(12), INDEX),
  BILLINGADDRESS_ID (BIGINT, INDEX) to client table.
- Add TAX_ID (BIGINT, INDEX), DISCOUNT_ID (BIGINT, INDEX), COSTINGPREPAREDBY_ID (BIGINT, INDEX),
  COSTINGINVOICEDBY_ID (BIGINT, INDEX) to jobcostingandpayment table.
- Add DATECOSTINGINVOICED (DATE) to jobstatusandtracking table.
- Add ACCOUNTINGCODE_ID (BIGINT, INDEX) to service table
- Add SERVICEREQUESTEDFOODINSPECTORATE, SERVICEREQUESTEDLEGALMETROLOGY,
  SERVICEREQUESTEDSALEOFPUBLICATION, SERVICEREQUESTEDSTATIONERYORPHOTOCOPY,
  SERVICEREQUESTEDCERTIFICATION, SERVICEREQUESTEDCERTIFICATIONSTANDARDS,
  SERVICEREQUESTEDDETENTIONREHABINSPECTION, SERVICEREQUESTEDFACILITIESMANAGEMENT
  SERVICEREQUESTEDCEMENTTESTING, SERVICEREQUESTEDPETROLSAMPLING (BIT) to
  servicecontract table.
Data updates:
- Run DiscountAndTaxTest to create Tax table if it is not created automatically.
- Add tax and discount with name "0.0" and value 0.0.
- Copy Job Costing.jxml file to c:\jasperreports and use .jxrml file instead of 
  .jasper to generate job costing.
- Add all GCT taxes.
- Add services including the catch all "Other". Find out the other services 
  that need to be added as check box in ther services tab.
- Run JobTest to create job_service table via BEL.
- Add accounting codes as given in "BSJ Department Codes and Notes.pdf".
- Find out the values of the discount offered.
Testing:
- Test Accpac credit status feature in light of the changes made to AccPacCustomer.

### Release Notes (v3.4.0)
- Tax and discount tables and user interface elements added.
- Corrected the standard financial date periods.
- Updated and deployed the Job Costing and Service Contract forms.