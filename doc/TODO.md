### General
- In HRM put employee positions tab beside employees tab.
- Call resetManagers() on all managers to init them. 
- Edit SM and JMTS faces config files so they don't include the same info.
- Create job-management-tracking-system-lib and move relevant classes from WAL
  to it.
- Comment out PM code and access to modules from the user menu.
- Move persistence from WAL to JMTS/PM or use existing one.
- Implement PF status monitor for file downloads and remove code the deals with it otherwise.
- Impl default search for all modules.
- Job Costing Privilege Feature Update:
  * Update the job costing privilege feature of the JMTS to prevent unauthorized 
    change of a job costing after the client has been invoiced.
  * Deployment and testing.
- Implement month report feature update in accordance with proforma, report template
  and GA's email.
- Put back the PM search and other features after adding the purchase-management-lib dependency.
- Implement enabling a module for a user from the user menu.
- Check why Finance staff cannot export job costing. This may be applicable to only
  subcontracted jobs. **Done**
- Fix java.lang.NullPointerException
	at jm.com.dpbennett.wal.managers.SystemManager.updateDashboardTabs(SystemManager.java:353)
that is thrown in user menu when a module is selected.
- Remove PM, FM etc menu items and put a master menu from which modules can be selected as is 
done in the preferences dialog. See if there is a checkbox menu item in PF
- Create a lib project for each web application. Eg system-management-lib, 
purchase-management-lib etc.
- Take out the relevant files and put them in their own modules. See the JMTS copy 
  for the files.

### UI Design
- Put "Help" and "About" menu items in the "User" menu.