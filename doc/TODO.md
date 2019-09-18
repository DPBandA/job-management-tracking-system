### General
- Job Costing Privilege Feature Update:
  * Disable editing job costing once job is completed or invoiced.
  * Disable entry into job costing dialog once job is completed or invoiced.
  * Update the job costing privilege feature of the JMTS to prevent unauthorized 
    change of a job costing after the client has been invoiced.
  * Deployment and testing.
- Implement month report feature update in accordance with proforma, report template
  and GA's email:
  * Update Job class to contain the new fields according to the new monthly report format
  * Update the Job dialog to allow entry into the new fields.
  * See GA's email and Github issues.
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
- Add authenticate to c lewis account after testing.

### UI Design
- Put "Help" and "About" menu items in the "User" menu.