
# JMTS Architecture

## System Overview
The system follows the MVC (Model-View-Controller) pattern:
- **Model (JPA Entities)** – Defines database tables.
- **View (JSF Pages)** – Provides the frontend UI.
- **Controller (Managed Beans)** – Handles user requests.

## Workflow
1. User submits a job request (JSF form).
2. Managed Bean processes the request.
3. Service layer applies business rules.
4. Data is stored/retrieved via JPA repositories.
5. Response is displayed on JSF pages.

## Deployment
The application is deployed on a Jakarta EE server like Payara or Glassfish.

