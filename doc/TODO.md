# Urgent
- Add STRATEGICPRIORITY to legaldocument table in all databases and 
  * StrategicPriority1-3 and StrategicPriorities system option list.
- Check how and why persons can close subcontracted jobs not assigned to their department.
- The "Earnings by Category" report is not showing the end date of date period 3 correctly.

# Legal Office
- The "visited" row feature not working for legal document. Fix.
  * It's because re-search is done after saving.
- Change strategic goal to priority in report
- Newly entered document type appears in "Document #" field but not in the
"Document type" field. Fix!
- When doc type is first edited it is not reflected in the doc type form field.
  Replicate this issue and fix! Solve by impl "dialogReturn" for the Document Type
  dialog and set the document type.
- Hide "For job earnings:" in classification dialog except for "Job" category.
- Do update Legal Office report with set and actual turnaround times. 
  Update "turnaround times" when doc is being saved?
- Switch to LegalOfficeReport2 in system admin.

# Impl HR module with HumanResourceManager clas
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
