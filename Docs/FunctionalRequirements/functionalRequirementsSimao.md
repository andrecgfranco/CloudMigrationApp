# Phase 3 – Functional Requirements and Application Architecture

**Project:** European Migration Assistant  
**Student:** Simão Quintas- 58190

---

## 1. Functional Requirements

### FR1 – Filtered Location Search

**A – Filter by metric**  
The system must allow searching for countries or cities based on a specific statistical metric (e.g.: cost of living, air quality, crime rate).

**B – Filter by value range**  
The system must allow the user to define a minimum and a maximum value applicable to the selected metric, in order to filter the search results.

**C – Results summary**  
The system must return a list containing the summarised information (ID, name, type and code) of the locations that meet the searched requirements.

**D – Validation Errors**  
The system must return HTTP 400 if the dataset is not available or if the search parameters (e.g.: minimum/maximum values) are invalid.

---

### FR2 – Historical Evolution of Metrics

**A – History by location**  
The system must allow querying the temporal evolution of statistical indicators associated with a specific location.

**B – Filter by metrics and year range**  
The request must specify one or more datasets of interest, allowing the user to specify the time range (start year and end year).

**C – Validation/Existence Errors**  
The system must return HTTP 400 if an invalid year range is entered or if the metrics list is malformed, and HTTP 404 if the requested location does not exist in the system.

---

### FR3 – Metric Trends

**A – Metric and period selection**  
The system must allow the user to choose a specific metric and define the time range for analysis (e.g.: the last X years).

**B – Trend and top filter**  
The system must allow indicating whether to discover the locations whose metric increased the most or deteriorated the most during the period, limiting the results to a specific number (Top X).

**C – Variation Calculation**  
The system must calculate the difference of the selected metric over the specified years for each location, ordering the results according to the desired trend.

**D – Validation Errors**  
The system must return HTTP 400 if the parameters (metric, years, trend) are not provided or are invalid.


## 2. Application Architecture ##

### 2.1 Overview ##

Note: Initial Architecture Proposal

The application is composed of four "domains" with independent microservices, all exposed through a single API Gateway / Ingress. Each microservice is autonomous - it contains its own internal layers (router, controller, handler, repository) that are started simultaneously. This separation ensures that each service can be developed, tested, deployed and scaled independently of the others.

The services are grouped into two domains: the locations domain, which aggregates geographic and statistical data, and the users domain, which manages profiles and interactions.

The API Gateway is the only component exposed externally, acting as a single entry point and abstracting the internal structure of the application from the client. It is responsible for routing requests to the correct service based on the path, and for load balancing between replicas of the same service. Routing within each service - between its internal endpoints - is the responsibility of that service's own router.

### 2.2 Components ##

| Component | Responsible | Responsibility |
| --- | --- | --- |
| API Gateway / Ingress | Group | External routing to the correct service; load balancing between replicas |
| Locations Service | André Franco | List, detail, compare and rank locations |
| Search Service | Simão Quintas | Filtered search, historical data, comparisons |
| User Service | Pedro Lopes | User profiles, favourites, personalised recommendations |
| Auth/Forum Service | Juliana Duarte | Registration, authentication, forum posts |
| DB | Group | Stores locations, statistical indicators (Eurostat/EEA), Users and Forums in separate tables |

### 2.3 Architecture Diagram ###

```mermaid
flowchart TD
    Client["REST Client"]
    GW["API Gateway / Ingress - Routing and Load Balancing"]

    Client -- "HTTP Request" --> GW
    GW -- "HTTP Response" --> Client

    GW --> LS["Locations Service - André Franco"]
    GW --> SS["Search Service - Simão Quintas"]
    GW --> US["User Service - Pedro Lopes"]
    GW --> AFS["Auth/Forum Service - Juliana Duarte"]

    LS -- "Router → Controller → Handler → Repository" --- UDB[("Unified DataBase")]
    SS -- "Router → Controller → Handler → Repository" --- UDB
    US -- "Router → Controller → Handler → Repository" --- UDB
    AFS -- "Router → Controller → Handler → Repository" --- UDB
```

### 2.4 Legend ###

**REST Client** - External client that interacts with the application through HTTP requests.

**API Gateway / Ingress** - Single entry point for all external requests. Routes each request to the correct service based on the path (e.g.: `/api/v1/locations` → Locations Service), and distributes the load between replicas of the same service when multiple instances are running. Kubernetes Ingress can be used.

**Locations Service** - Service responsible for use cases related to locations: listing, querying indicators, side-by-side comparison and ranking by metric. Internally composed of router, controller, handler and repository, operating exclusively on the locations domain.

**Search Service** - Service responsible for filtered location search and querying historical data.

**User Service** - Service responsible for managing user profiles, favourite destinations, metric importance and personalised recommendation lists.

**Auth/Forum Service** - Service responsible for user registration and authentication, account deletion and forum post management.

**Unified DB** - Relational database that stores locations, respective statistical indicators from the datasets, user accounts, sessions, metric preferences and forum posts.

**Eurostat / EEA** - External data sources. The datasets are ingested into the DB at service startup through a loading script.

**NOTE** - Services are not expected to communicate with each other. If this becomes necessary in the future, changes described at the end of this file may need to be made.
Additionally, there may be overlapping between domains, which may reduce the number of different services developed.

### 2.5 Design Decisions ###

- **Microservices per domain** - one service per group member, deployable and developed independently.
- **Unified database** - due to data overlap between services.
- **REST over HTTP** - all communication between client and services.
- **Single Ingress** - only the gateway is exposed externally; all services and databases are internal.
- **Dataset ingestion** - Eurostat/EEA CSV files are loaded into the database at container startup.

### 2.6 Possible Changes ###

If microservices need to communicate with each other in the future, the "stack" structure becomes quite inefficient and convoluted. As such, a more scalable alternative solution has been developed (but consequently more complex and with a higher initial development cost) that can easily be implemented in the future:

- The stack ceases to exist as a unified entity, and each of its components (Controller, Handler and Data Access) can be initialised individually and without having a One-to-One relationship with the component in the layer above.
- The components, now independent, will need to communicate with each other through an RPC, in order to avoid the complications that HTTP communication between backend processes brings.
- This implementation may help increase scalability, but may introduce greater initial complexity, as well as introducing a possible bottleneck in the form of communication between components (which were previously all initialised together in the same process). It is an option to consider depending on the circumstances we may encounter in the production phase.
- The transition between the current architecture and this alternative would be relatively easy, requiring only connecting the components through RPC and allowing individual deployment, so it would be a low-cost transition.
