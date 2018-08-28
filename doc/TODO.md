# Prepare for Legal Office training

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
- Newly entered document type appears in "Document #" field but not in the
"Document type" field. Fix!
- Hide "For job earnings:" in classification dialog except for "Job" category. 	
- Do update Legal Office report with set and actual turnaround times.
- When doc type is first edited it is not reflected in the doc type form field.
  Replicate this issue and fix!
