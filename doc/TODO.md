
# Legal Office
- Impl "show/hide" date search field in report template so that reports can be
  generated based on date search field. Add to all databases.
  * Add SHOWDATEFIELD to all databases.
- Impl opening the saved document from the database.
- The "visited" row feature not working for legal document. Fix.
  * It's because re-search is done after saving.
- Hide "For job earnings:" in classification dialog except for "Job" category.
- Do update Legal Office report with set and actual turnaround times. 
  Update "turnaround times" when doc is being saved?
- Switch to LegalOfficeReport2 in system admin.

# Proposals/Proforma Invoice
- Do proforma for JMTS Migration to Virtual Server. Include issues being experienced
  in the email wrt GF running out of memory
- Do proposal for update of JMTS Standards module to include the standards development
  process involving the TCs website.

# Impl HR module with HumanResourceManager class
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
