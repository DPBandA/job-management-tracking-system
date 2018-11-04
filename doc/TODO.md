# Urgent
- Connection pool com.mysql.jdbc.jdbc2.optional.MysqlDataSource.
- Change from @ManagedBean to @Named and test 
  * Check why beans dont work when changed from @ManagedBean to @Named. 
    Try GF5 and see if it works.
- Add SHOWDATEFIELD to report table.
- Test and use gf3 and javaee 6 then upgrade to gf5 and javee 8 over time.
- Switch around Last month: and Year period: in the monthly report spreadsheet.
- Add this financial year to the list of date periods.
- Impl new service contract and agreement.
- Do client report 
- Impl GeneralManager based on JobManager and let all managers inherit from it.

# Legal Office
- Impl opening the saved document from the database. Refresh the edited document
  in the table and show as visited.
- The "visited" row feature not working for legal document. Fix.
  * It's because re-search is done after saving.
- Hide "For job earnings:" in classification dialog except for "Job" category.
- Do update Legal Office report with set and actual turnaround times. 
  Update "turnaround times" when doc is being saved?
- Switch to LegalOfficeReport2 in system admin.

# Impl HR module with HumanResourceManager class
- Create 
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
- Upgrade to using Java EE 7/8 and GF 5 on boshrmapp and bosapp ASAP.
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
- Check why admain2 is not used when admain is not available.
- Impl login with admin user and encrypted password.
- See if all passwords can be encrypted in system options.

# Accpac
- Add all depts, subgroups and divisions to jmts live.
- https://smist08.wordpress.com/2013/01/01/the-sage-300-erp-java-api/
   
# HRM
- Look up Java API for PeopleSoft HRM app.