## Accounts Receivable Feature (v3.4.0)
- Use "depreciated" javadoc feature to document class and database fields that will be removed in the future eg discount and discountType in the JobCostingAndPayment class.
- Enter taxes, discounts and accounting codes.
- Deal with old jobs that do not have the tax or discount object set.
  * Fixed cost discount does not work. Edit getTotalTax() and getTotalCost()
    to use fixed cost.
  * Check if fixed cost tax can also be applied as is done with discount.
  * Impl and use discountValue and discountType in getDiscount().
  * Impl getting/finding default discount based on name, value and value type. 
    Test with JCP ID = 705759, and job 26/2015/1576/A-B, discount = $2745.0.
  * Impl getTax() and getDiscount() in JobFinanceManager deal with field and primitive 
    objects for these values.
- Impl batch invoice export
  * Check that all selected invoices can be exported. eg a valid client accounting ID is set    
    ~ Impl canInvoiceJobCosting(Job job) by checking all the criteria required for generating and invoice.
  * Create menu button similar to tools button for "Invoiced job costing(s)" and "Export invoice(s)".
    ~ Test out "Export invoice(s)" by exporting incomplete invoice spreadsheet.
  * Call addServices() before doing export to ensure that all relevant services are added.
    Add a default service if none is selected.
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
- Add TAT to "invoices reports".


### Database update, deployment and testing
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

Testing:
- Test Accpac credit status feature in light of the changes made to AccPacCustomer.
- Connect to bsj jmts databases and do testing before deploying.

### Release Notes (v3.4.0)
- Tax and discount tables and user interface elements added.
- Corrected the standard financial date periods.
- Updated and deployed the Job Costing and Service Contract forms.
