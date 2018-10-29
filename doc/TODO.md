# Urgent
- Get backup of jmts database on thumb drive.
- Change reference to converter and validators from bel to wal.
- Setup local GF5 for development.
- Delete the relevant resources and overlay the waccar and hrms apps.
- Pull business-entity and make use of new ReturnMessage
- Check that all dialogs display well in the dark hive theme. May have to wrap 
  the dialog content in a <p:panel>
- Division dialog throws table (jmts.division_subgroup) does not exit error. Fix!
  Create all subgroup related tables before creating division related tables.
- Switch around Last month: and Year period: in the monthly report spreadsheet.
- Add this financial year to the list of date periods.
- Impl new service contract and agreement.

# Legal Office
- Impl opening the saved document from the database. Refesh the edited document
  in the table and show as visited.
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
- Impl using only one EMF which is used by all session beans when an entity 
  manager is needed.
- Send error email for only job save related exceptions. Not messages pertaining
  to prvileges. Send emails to desbenn@gmail.com
- Remove major modules such as helpdesk, hrm, crm and stds dev and create as
separate projects using maven overlays.
- Add option to auto generate sample reference as is done with job number.
- Consider using oauth for authentication over the web.

# Accpac
- https://smist08.wordpress.com/2013/01/01/the-sage-300-erp-java-api/
   
