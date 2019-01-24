# Tasks
- Put isNameAndIdEditable as transient in Client class and get rid of 
  use of isNameAndIdEditable.
- Add identification and identificationType to Client class.
- Create MarketingCampaign class to capture telemarketing and other types of 
  marketing campaigns. Add Telemarketing (check spelling) as one type of marketing campaign.
- Add menu buttons in client dialog general tab to add/edit most recent address or contact.
- Where does the marketing process start?
- How should knowledge management be incorporated?
- Add dialog return to "Client Management" menu item.

# Customer Relationship Management R&D

## Introduction

The marriage of a set of well-documented, consistently-executed processes, with a
business application that supports, monitors, and reports on them provides the
foundation for an agile organization that learns and evolves.

You have heard the old aphorism “You can’t manage what you can’t measure” – consistent processes and a sophisticated CRM application can provide the platform needed to measure and manage.

Pursuing a data-driven culture means documenting processes, gathering data on their effectiveness, making changes based on the data, and then evaluating the success of the change based on data.

Figure 1-1. Sample Project Communication Plan. Use as the plan for communicating the CRM actvities and project.

Components of CRM Success

Your CRM program will require a number of people from different parts of the organization playing different roles.

## Proposal Notes

Centralizing customer information in a CRM application and building thoughtful integrations to other key applications such as accounting and your web site can facilitate this task. Having all the needed criteria for filtering and targeting marketing touches within CRM allows for more personalized marketing without a complex data manipulation effort to combine data from disparate applications.

For organizations with basic customer service tools, self-service is one of three common focus areas for CRM programs, the other two being the implementation of structured issue tracking and the development of a searchable knowledge base tool. Typically self-service is the third of these areas to be developed; first an organization wants to streamline its internal processes around issue management and seed its knowledge base and only then offer customers self-service options.

## CRM Features

### General
- Search the knowledge base for resolutions to issues;
- Creating, viewing, or updating existing service issues;
- Downloading support materials such as manuals and datasheets;
### Customer Management:
- Customers able to manage their profile information (addresses, phone numbers, and so on)
- Fields to categorize customers in multiple ways - by sales territory, by market segment, or by industry
### Marketing:
- An integrated wiki or other knowledge base tool to foster information sharing across the service department.
- Implement ability to bundle permissions/privileges into “roles” that can be assigned to users or groups to facilitate user management.
### Reporting
- User-focused tools that enable construction of basic reports and dashboards without programming.
- Open access to the data using other reporting tools via standard protocols (for example, ODBC), in a way that respects the
application’s security model. For example, some applications allow data to be exported to Microsoft Excel for reporting and analysis.
### Workflow Automation
- The ability to design multistep workflow processes that can
respond to application events (for example, creation, update,
deletion, reassignment of records) and take a variety of actions (for
example, create new records, reassign records, send e-mails or text
messages, update records). This should not require programming.
- The ability to branch and control the flow of the workflow process.
- The ability to extend workflow processes with custom logic via
code.
### Roadmap
- Create a catalog of the different applications used to manage customer information or customer-related processes.
### Evaluating Software and Consultants
- The traditional CRM “suite” includes features to support three front-office business functions: marketing, sales, and customer service.
- The promised value of the full CRM suite, aside from the value derived within each department from the application’s features, is the information sharing that it can foster across departments. Such sharing is necessary if the organization is going to present a single face to the customer.
### Design
- Application alignment: An item that we will discuss later in the
chapter is the alignment of your application with your
organization’s business processes. One of the largest CRM
implementation gotchas is allowing the technology to drive usage
scenarios and business process. Defining your process up front will
enable you to ensure that your implementation team, including
vendors, are working diligently toward making the application
meet your business processes and not the technology’s process.
- Process updates: Analyzing your processes up front will enable your
team to review those processes and determine whether they are
appropriate for the company moving forward. Spending time to
review how each process might change or be made better will again
allow you to make sure the application will meet those needs.
- Add Prospect entity class as and entity that is being converted to a Client.
