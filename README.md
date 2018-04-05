# Job Management & Tracking System (JMTS)
## The JMTS is an enterprise software that facilitates business management including the management and tracking of jobs and their associated activities.
### Proposed or Existing Modules/Services include:
- Document Management
- Customer Relationship Management
- Human Resource Management
- Financial Management
- Inventory Management
- Job Management
- Project Management
- Inventory Management
- Compliance
- Certification and Accreditation
- Metrology Management
- Product Management
- Factory Management
- Messaging & Chat

### Installation
#### MySQL
- In my.cnf add the following for mysql 5.7 >= to remove ONLY_FULL_GROUP_BY : [mysqld] sql_mode = STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION
- Setup glassfish/payara as Windows service. 
  * See http://www.luv2code.com/2013/11/13/install-glassfish-4-as-a-windows-service/
