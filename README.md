# Job Management & Tracking System (JMTS)
The JMTS is an enterprise software that facilitates the management and tracking of jobs.

## Things to do
- Add cash payments feature so cashier can add cash payments.
* Add discount type and discount to cash payment form.
* Add fields to cashPayment and database: discount, discountType, 
     paymentTerms, rename JMTSUserId to userId
* Make a payment(first) as desosit and save to jobCostingAndPayment.deposit
* Add PO number to payment panel. 
* Update corresponding fields in jobCostingAndPayment as required.
* Use canEditInvoicingAndPayment where necessary
* Ensure amount due is updated correctly using cash payments.
- Create contact field in the job record to assign contact to current job.
- Implement and include report templates for all reports generated to date.
- Implement prevention of the insertion of incorrect subcontract jobs costing 
amounts which sometimes occur when the date of submission of a parent job is changed
- Modify the permissions scheme to prevent the changing of some fields (such as 
the client field) by unauthorized personnel and track all changes made.  
- Implement "Double View" for the cashier so that the Cashier and Job Costing 
Views can be viewed simultaneously for easy job costing updates.
-- Instead of creating double view, "merge" the columns from "Job Costing" 
view while leaving out unneeded columns.
- Implement BSJ client entry update and automatic contact detail insertion when 
subcontracting jobs.
-- For external jobs, create contact field in job record and store contact from 
a selected client contact.
-- For subcontracts, get or create contact for person doing the subcontract.
- Use primefaces Dialog framework to implement all dialogs so they can be reused
by other apps?

