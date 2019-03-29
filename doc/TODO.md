## Accounts Receivable Feature (v3.4.0)
- Call addServices() and try to insert to financial account when creating invoices
  Update canInvoiceJobCosting(Job job) for this.
- Export discount with negative value.
- Exclude subcontracts from the "Appr'd & Uninv'd" jobs search.
- Try to get the accpac client id when invoices are being created and update 
  the billing info if successful and save the client.
- Format the invoice number with at least 4 characters for the sequence number as
  is done with the job number.
- Enter the accounting codes and corresponding services based on spreadsheet from RA.
- Deploy and email about deployment.

### Database update, deployment and testing
- Add ACCOUNT (VARCHAR, 255) to accountingcode table.
- Add DEFAULTTAX_ID (BIGINT, INDEX) to classification table.
- Add ISTAXABLE (BIT) to classification, jobcategory and jobsubcategory tables.
- Add DISCOUNT_ID (BIGINT, INDEX), FINANCIALACCOUNT_IDCUST (CHAR(12), INDEX),
  BILLINGADDRESS_ID (BIGINT, INDEX), BILLINGCONTACT_ID (BIGINT, INDEX), DEFAULTTAX_ID (BIGINT, INDEX) to client table.
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
- Add GCT to "earning" classifications and "0" tax to "non-earning".
- Enter the accounting codes and corresponding services based on spreadsheet from RA.

Testing:
- Test Accpac credit status feature in light of the changes made to AccPacCustomer.
- Connect to bsj jmts databases and do testing before deploying.

### Release Notes (v3.4.0)
- Tax and discount tables and user interface elements added.
- Corrected the standard financial date periods.
- Updated and deployed the Job Costing and Service Contract forms.
