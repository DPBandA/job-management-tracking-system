Impl HR module with HumanResourceManager clas
- Create active* convertters and check that system only finds and uses only active employees, department, 
  divisions, subgroup etc. when necessary.
- Put code column in division and subgroup tables.
- Add subgroups tab to division.
- Create labs and units within departments.
- In departments tab in division dialog, list the departments that are in subgroups
  in addition to those that fall directly under the division.

- When error occurs while saving job occurs and email is sent, a job number is 
still generated. This happened for job 6153 which was later saved to 6154. 
A valid job number should not be generated when this occurs.
