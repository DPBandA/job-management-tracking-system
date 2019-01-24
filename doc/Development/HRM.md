# Tasks
- Take HRM out of system admin.
- Create HRM unit privilege flag. It it is null set its value to the sysadmin 
  flag.
- Create and add Contractor class based on the employee class to BEL/HRM.
- To Organization/Business class add Divisions, Subgroups, Departments, Clients
- Reload entities before opening dialogs. Start with employee.
  * Load from database using setSelected* in <f:setPropertyActionListener>
    and redo search in the "dialogReturn" method.
- In departments tab in division dialog, list the departments that are in subgroups
  in addition to those that fall directly under the division.
- Deploy and subgroup and division records.
- Add code to generate "accounting codes" in Job Costing tab.
- Deal with apparent creation of multiple 
"QEMS-OSH (Quality &amp; Environmental Management Systems - Occupational Health &amp; Safety)"
department which seems to be due to the "&" character or "&amp;". Try to delete the
in active ones.
- Impl searching for department on the code field.
- Add head to division dialog.
- Impl Labs and Department units.
- Instead of Notes put head in Dept, Subgroup and Division tables.
- In the relevant dialogs, layout fields as Code, Name, Head.
- Add payCycle to EmployeePosition class. Should be in Payroll class too?
- Fundamentals of Human Resource Management, pg 19
- Look up Java API for PeopleSoft HRM app.
- Trim names entered via any dialog.
- Do not make the entry of the email address mandatory in the employee dialog. Make
  mandatory in the User dialog. This is so that the HR personnel is not forced 
  know the email address of a new employee.
