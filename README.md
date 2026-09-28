# RetailX — Inventory Reorder Alert System

> Real-time Inventory Ledger & Automated Reorder Alert Intelligence Engine.

---

## 1. Project Title & Description

**RetailX Inventory System** is a robust, full-stack enterprise retail inventory management application designed to maintain real-time stock balances across ledger movements (`PURCHASE`, `SALE`, `RETURN`, `DAMAGE`), eliminate stockouts through automated reorder threshold triggers, enforce strict duplicate alert suppression rules, and provide sales velocity ranking reports.

---

## 2. Problem Statement & Real-World Scenario

In fast-paced retail and supermarket environments (e.g., FMCG, groceries, apparel):
- **Stale Stock Counters**: Relying on a single mutable `stock_count` integer causes race conditions, phantom inventory, and sync drift.
- **Alert Flooding**: As stock continually drops below a critical threshold during ongoing sales, naive systems spam managers with dozens of duplicate alerts for the same item.
- **Over-selling**: Selling items without atomic ledger validation leads to negative physical inventory and unfulfillable orders.

**RetailX Solution**:
1. Every inventory change is recorded in an immutable ledger (`StockMovement`).
2. Current stock is calculated dynamically:
   $$\text{Current Stock} = \sum(\text{PURCHASE} + \text{RETURN}) - \sum(\text{SALE} + \text{DAMAGE})$$
3. A single `OPEN` alert is created when stock reaches or drops below threshold. Subsequent sales do **not** create duplicate alerts while an alert is active. Once fulfilled, future drops trigger a clean new alert.

---

## 3. Features List

- **Live Dynamic Stock Calculation**: Query-level dynamic calculation preventing race conditions.
- **Duplicate Alert Suppression**: Enforces at most one active `OPEN` alert per product at any given time.
- **Over-Sale Protection**: `SALE` and `DAMAGE` transactions exceeding current available stock are rejected (`400 Insufficient Stock`).
- **Product Catalog Management**: Full CRUD with unique SKU enforcement (`409 Conflict`).
- **Audit Trail & Movement History**: Chronological, paginated, sortable log of all inventory movements.
- **Fast-Moving Products Analytics**: Ranks products strictly by outbound `SALE` volume over customized date ranges.
- **Modern Responsive Web UI**: Clean, light-themed React 18 dashboard with real-time badges, confirmation dialogs, and top-right auto-dismiss toasts.
- **OpenAPI 3.0 Interactive Docs**: Swagger UI available at `/swagger-ui.html`.

---## 4. Complete System Workflow & Architecture Diagrams

### 1. Frontend User Interaction Flow
``
USER ACTION (Click/Form Submit)
       │
       ▼
React Component State Update
       │
       ▼
Validate Input (Client-side)
       │
       ▼
Call API Service (Axios)
       │
       ▼
HTTP Request (GET/POST/PUT/DELETE)
       │
       ▼
Request Sent to Backend http://localhost:8080/api/...
       │
       ▼
Wait for Response ───► [BACKEND PROCESSES REQUEST]
       │
       ▼
HTTP Response (200, 201, 400, 404, 409, 500)
       │
       ▼
Response Interceptor Catches Response
       │
       ▼
Check Status Code
       ├──► Success (2xx)?
       │      ├── Update Component State
       │      ├── Display Success Toast (Top Right)
       │      ├── Refresh UI / Table
       │      └── Dismiss Loading Indicator
       │
       └──► Error (4xx, 5xx)?
              ├── Extract Error Message
              ├── Display Red Error Toast
              ├── Log to Console
              └── Dismiss Loading Indicator
```

---

### 2. Backend Request Handling Pipeline
```
HTTP Request Arrives at Backend
       │
       ▼
Spring DispatcherServlet Routes Request
       │
       ▼
Find Matching Controller Method (@RequestMapping)
Example: POST /api/stock-movements -> [StockMovementController.recordMovement()]
       │
       ▼
Parse Request Body ──► StockMovementRequest DTO
       │
       ▼
Bean Validation (@Valid annotation)
       ├──► Validation Fails? ──► Return 400 Bad Request (ErrorResponseDTO) ──► Stop Processing
       └──► Validation Passes ──► Continue
              │
              ▼
       Call Service Layer (@Autowired StockMovementService)
              │
              ▼
       [StockMovementService.recordMovement(request)]
              │
              ├──► Service Throws Exception? ──► GlobalExceptionHandler catches
              │                                 └──► Convert to ErrorResponseDTO ──► Return HTTP Error Status
              └──► Business Logic Succeeds
                     │
                     ▼
              Service Returns Response DTO
                     │
                     ▼
              Controller Builds ResponseEntity
                     │
                     ▼
              Set HTTP Status Code (200, 201, etc.)
                     │
                     ▼
              Serialize Response DTO to JSON ──► Send HTTP Response to Frontend Client
```

---

### 3. Database Transaction Flow (Stock Movement + Alert)
```
START TRANSACTION (@Transactional)
       │
       ▼
Insert StockMovement Record
       │
       ▼
Commit StockMovement to Database
       │
       ▼
SELECT from stock_movements to Recalculate Stock
       │
       ▼
Calculate Current Stock = SUM(PURCHASE + RETURN) - SUM(SALE + DAMAGE)
       │
       ▼
Compare Stock with Threshold (Stock <= Threshold?)
       ├──► NO ──► No Alert Needed ──► COMMIT Transaction ──► Return to Controller (reorderAlertCreated=false)
       │
       └──► YES ──► Check for OPEN Alert
              │
              ▼
       SELECT * FROM reorder_alerts WHERE product_id=? AND status='OPEN'
              │
              ├──► OPEN Alert Exists? ──► Do NOT Create Duplicate ──► COMMIT Transaction ──► Return "Alert already exists"
              │
              └──► NO OPEN Alert Found ──► Create New OPEN Alert
                     │
                     ▼
              INSERT INTO reorder_alerts (product_id, current_stock_at_alert, reorder_threshold_at_alert, reorder_quantity, status='OPEN')
                     │
                     ▼
              COMMIT Transaction ──► Return "Alert created" (reorderAlertCreated=true, alertId=...)
```

---

### 4. Stock Movement Recording — Detailed Flow (Most Important)
```
USER Action on Frontend:
  Select Product: Coca-Cola (id=1)
  Select Movement Type: SALE
  Enter Quantity: 20
  Select Date: 2026-09-28
  Click "Record Movement"

FRONTEND:
  POST /api/stock-movements
  { "productId": 1, "movementType": "SALE", "quantity": 20, "movementDate": "2026-09-28" }

BACKEND CONTROLLER (StockMovementController):
  @PostMapping
  public ResponseEntity<CreateStockMovementResponse> recordMovement(
      @Valid @RequestBody CreateStockMovementRequest request) {
    CreateStockMovementResponse response = movementService.recordMovement(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

BACKEND SERVICE (StockMovementService.recordMovement):
  Step 1: Validate Product Exists ─────────► Product product = productRepository.findById(1) -> Found Coca-Cola
  Step 2: Validate Movement Type ──────────► Valid Enum ('SALE')
  Step 3: Validate Quantity ───────────────► 20 > 0
  Step 4: Get Current Stock (Dynamic) ────► InventoryService.getCurrentStock(1) -> 112 units
  Step 5: For SALE, Validate Stock ────────► 112 >= 20 (Sufficient Stock confirmed)
  Step 6: Save Movement to Database ───────► INSERT INTO stock_movements (product_id, movement_type, quantity, movement_date) VALUES (1, 'SALE', 20, '2026-09-28') -> id=50
  Step 7: Recalculate Current Stock ───────► 150 - (20 + 15 + 3 + 20) = 92 units
  Step 8: Check Reorder Threshold ─────────► 92 > 20 (Threshold) -> reorderAlertCreated = false
  Step 9: Build Response ──────────────────► CreateStockMovementResponse(movement, currentStock=92, reorderAlertCreated=false, alertId=null)
  Step 10: Return HTTP 201 Created ────────► Send JSON to Client

FRONTEND Receives Response:
  ✓ Status: 201 Created
  ✓ Display: "Stock movement recorded successfully"
  ✓ Display: "Current Stock: 92 units"
  ✓ Clear form, refresh movements table & dashboard
```

---

### 5. Reorder Alert Creation & Duplicate Prevention Lifecycle

#### Scenario A: Stock Drops Below Threshold (No existing OPEN alert)
```
Current State: Product: Parle-G (id=2), Threshold: 15, Current Stock: 45, OPEN Alerts: None
User Action: Record SALE 30 units
Backend Processing:
  Current Stock = 45 - 30 = 15 <= Threshold 15 -> YES
  Query DB: SELECT * FROM reorder_alerts WHERE product_id=2 AND status='OPEN' -> NONE found
  CREATE NEW OPEN ALERT:
    INSERT INTO reorder_alerts (product_id, current_stock_at_alert, reorder_threshold_at_alert, reorder_quantity, status)
    VALUES (2, 15, 15, 80, 'OPEN') -> Alert #1 created
Frontend Response:
  ✓ Display: "⚠️ Reorder alert created for Parle-G"
  ✓ Dashboard open alerts count = 1
```

#### Scenario B: Stock Drops Further (OPEN alert already exists)
```
Current State: Product: Parle-G (id=2), Threshold: 15, Current Stock: 15, OPEN Alert: id=1
User Action: Record SALE 5 units
Backend Processing:
  Current Stock = 15 - 5 = 10 <= Threshold 15 -> YES
  Query DB: SELECT * FROM reorder_alerts WHERE product_id=2 AND status='OPEN' -> FOUND Alert id=1
  DO NOT CREATE DUPLICATE:
    Return reorderAlertCreated = false, alertId = null
Frontend Response:
  ✓ Display: "ℹ️ Product already has an open reorder alert"
  ✓ Existing alert #1 remains OPEN, no duplicate alert created
```

#### Scenario C: Alert Fulfillment (Status changes to FULFILLED)
```
Current State: Alert id=1: status='OPEN', product_id=2
User Action: Click "Fulfill Alert" in Reorder Alerts page -> PUT /api/reorder-alerts/1/fulfill
Backend Processing:
  Validate alert exists and is OPEN -> UPDATE reorder_alerts SET status='FULFILLED', fulfilled_at=NOW() WHERE id=1
Frontend Response:
  ✓ Status: 200 OK -> Alert status changes to FULFILLED (Green badge)
  ✓ Alert preserved in historical table
```

#### Scenario D: New Alert After Fulfillment (Cycle repeats)
```
Current State: Alert id=1: status='FULFILLED', Current Stock: 10
User Action: Record SALE 1 unit (stock drops to 9 <= 15)
Backend Processing:
  Query for OPEN alert: SELECT * FROM reorder_alerts WHERE product_id=2 AND status='OPEN' -> NONE found (id=1 is FULFILLED)
  CREATE NEW OPEN ALERT: INSERT INTO reorder_alerts (...) -> Alert id=2 created ('OPEN')
Frontend Response:
  ✓ Display: "⚠️ New reorder alert created for Parle-G" (Alert #1 historical, Alert #2 active)
```

---

### 6. Current Stock Calculation — Detailed
```
Example: Coca-Cola Product (id=1)
All Movements in Database:
  (1, PURCHASE, 150, 2026-09-01)
  (1, SALE,      20, 2026-09-02)
  (1, SALE,      15, 2026-09-03)
  (1, DAMAGE,     3, 2026-09-04)

InventoryService Calculation:
  PURCHASE 150  ──► +150  (Running Total = 150)
  SALE 20       ──► - 20  (Running Total = 130)
  SALE 15       ──► - 15  (Running Total = 115)
  DAMAGE 3      ──► -  3  (Running Total = 112)
  ───────────────────────────────────────────
  Final Stock   = 112 units

High-Performance SQL Aggregation:
  SELECT
    COALESCE(SUM(CASE WHEN movement_type IN ('PURCHASE', 'RETURN') THEN quantity ELSE 0 END), 0) -
    COALESCE(SUM(CASE WHEN movement_type IN ('SALE', 'DAMAGE') THEN quantity ELSE 0 END), 0)
  FROM stock_movements
  WHERE product_id = 1;
  Result: 150 - (20 + 15 + 3) = 112
```

---

### 7. Fast-Moving Products Calculation
```
User Request: GET /api/reports/fast-moving-products?startDate=2026-09-01&endDate=2026-09-28

Backend Processing:
  Step 1: Validate Dates (startDate <= endDate)
  Step 2: Query Database (ONLY SALE movements):
          SELECT product_id, SUM(quantity) as total_sold
          FROM stock_movements
          WHERE movement_type = 'SALE'
            AND movement_date BETWEEN '2026-09-01' AND '2026-09-28'
          GROUP BY product_id
          ORDER BY total_sold DESC;

  Step 3: Fetch Product Details (Name, SKU)
  Step 4: Build Ranked Response:
          [
            { "rank": 1, "productId": 5, "name": "Aashirvaad Rice 5kg", "sku": "AARRICE5", "unitsSold": 115 },
            { "rank": 2, "productId": 2, "name": "Parle-G Biscuits",   "sku": "PARLEG100", "unitsSold": 85 },
            { "rank": 3, "productId": 1, "name": "Coca-Cola 500ml",    "sku": "COKE500",  "unitsSold": 55 },
            { "rank": 4, "productId": 4, "name": "Head & Shoulders",   "sku": "HS200",    "unitsSold": 33 },
            { "rank": 5, "productId": 3, "name": "Lux Soap 125g",      "sku": "LUX125",   "unitsSold": 20 }
          ]

Frontend Display:
  ✓ Renders Sales Velocity Ranking table sorted strictly descending by outbound sales volume.
```

---

### 8. Dashboard Summary — All Metrics from Database
```
User navigates to Dashboard: GET /api/dashboard/summary

Backend Aggregations:
  Metric 1 (Total Products):           SELECT COUNT(*) FROM products -> 5
  Metric 2 (Total Stock Units):        SUM of getCurrentStock(p) for all products -> 209
  Metric 3 (Open Reorder Alerts):      SELECT COUNT(*) FROM reorder_alerts WHERE status='OPEN' -> 2
  Metric 4 (Products Low Stock):       Count of products where currentStock <= reorderThreshold -> 2
  Metric 5 (Recent Movements):         SELECT * FROM stock_movements ORDER BY created_at DESC LIMIT 10
  Metric 6 (Open Alerts List):         SELECT * FROM reorder_alerts WHERE status='OPEN'
  Metric 7 (Top Fast-Moving Products): Top 5 sales from last 30 days

Response Payload:
  {
    "totalProducts": 5,
    "totalStockUnits": 209,
    "openReorderAlerts": 2,
    "productsLowStock": 2,
    "recentMovements": [...],
    "openAlerts": [...],
    "topFastMovingProducts": [...]
  }
```

---

### 9. Layered Architecture — Data Flow
```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           REQUEST LAYER (Client)                            │
│  HTTP Request from React Frontend (Axios) -> JSON Payload                   │
└──────────────────────────────────────┬──────────────────────────────────────┘
                                       │
                                       ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                           CONTROLLER LAYER                                  │
│  @RestController receives request -> Parses @PathVariable, @RequestBody      │
│  Validates @Valid annotations -> Returns ResponseEntity with HTTP status    │
└──────────────────────────────────────┬──────────────────────────────────────┘
                                       │
                                       ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                            SERVICE LAYER                                    │
│  @Service business logic -> Calculates stock balances -> Checks thresholds  │
│  Enforces duplicate alert prevention rule -> Throws custom exceptions       │
└──────────────────────────────────────┬──────────────────────────────────────┘
                                       │
                                       ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                           REPOSITORY LAYER                                  │
│  @Repository queries database (Spring Data JPA) -> Custom @Query aggregates  │
│  Performs CRUD operations -> Returns Entities                               │
└──────────────────────────────────────┬──────────────────────────────────────┘
                                       │
                                       ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                            DATABASE LAYER                                   │
│  MySQL / H2 -> Tables: products, stock_movements, reorder_alerts            │
│  Integrity Constraints: UNIQUE SKU, FK REFERENCES, CHECK (quantity > 0)     │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

### 10. Error Handling Flow
```
Exception Thrown in Service Layer
       │
       ▼
Spring Catches Exception
       │
       ▼
Global Exception Handler (@ControllerAdvice) Intercepts
       │
       ▼
Determine Exception Type:
  • ResourceNotFoundException        ──► 404 Not Found
  • DuplicateSKUException            ──► 409 Conflict
  • InsufficientStockException       ──► 400 Bad Request
  • ValidationException              ──► 400 Bad Request
  • MethodArgumentNotValidException  ──► 400 Bad Request
  • Generic Exception                ──► 500 Internal Server Error
       │
       ▼
Build ErrorResponseDTO:
{
  "timestamp": "2026-09-28T10:30:00Z",
  "status": 400,
  "error": "Validation Error",
  "message": "Clear error message",
  "path": "/api/stock-movements"
}
       │
       ▼
Log Exception (Secure Server-Side Logging)
       │
       ▼
Return ResponseEntity with ErrorResponseDTO
       │
       ▼
Frontend Catches Error:
  - Extracts message from error.response.data.message
  - Displays red error toast banner
  - Logs to browser console
```

---

## 5. Technology Stack

### Backend
- **Framework**: Spring Boot 3.3.4 (Java 17+)
- **Data Access**: Spring Data JPA, Hibernate ORM
- **Database**: MySQL 8.0 (Production) / H2 In-Memory (Dev/Testing)
- **Validation**: Jakarta Bean Validation (`hibernate-validator`)
- **API Documentation**: SpringDoc OpenAPI 3.0 (Swagger UI)
- **Testing**: JUnit 5, Spring Boot Starter Test, MockMvc

### Frontend
- **Framework**: React 19 / 18, TypeScript, Vite
- **Styling**: Tailwind CSS, Vanilla CSS Design System
- **HTTP Client**: Axios
- **Routing**: React Router DOM 7
- **Icons**: Lucide React

---

## 6. Database Schema Description

### 1. `products`
| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY AUTO_INCREMENT` | Unique product identifier |
| `name` | `VARCHAR(255)` | `NOT NULL` | Product display name |
| `sku` | `VARCHAR(100)` | `NOT NULL UNIQUE` | Unique SKU code (uppercase) |
| `reorder_threshold`| `INT` | `NOT NULL DEFAULT 0` | Minimum stock boundary |
| `reorder_quantity` | `INT` | `NOT NULL` | Recommended reorder batch size |
| `created_at` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP` | Creation timestamp |
| `updated_at` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP` | Last updated timestamp |

### 2. `stock_movements`
| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY AUTO_INCREMENT` | Unique ledger entry ID |
| `product_id` | `BIGINT` | `FOREIGN KEY REFERENCES products(id)` | Associated product |
| `movement_type` | `VARCHAR(20)`| `NOT NULL` | `PURCHASE`, `SALE`, `RETURN`, `DAMAGE` |
| `quantity` | `INT` | `NOT NULL CHECK (quantity > 0)` | Quantity transacted |
| `movement_date` | `DATE` | `NOT NULL` | Effective date of movement |
| `created_at` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP` | Audit log timestamp |

### 3. `reorder_alerts`
| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | `BIGINT` | `PRIMARY KEY AUTO_INCREMENT` | Alert ID |
| `product_id` | `BIGINT` | `FOREIGN KEY REFERENCES products(id)` | Target product |
| `current_stock_at_alert` | `INT` | `NOT NULL` | Stock level when alert triggered |
| `reorder_threshold_at_alert` | `INT`| `NOT NULL` | Threshold configured at alert time |
| `reorder_quantity` | `INT` | `NOT NULL` | Recommended batch size |
| `status` | `VARCHAR(20)`| `DEFAULT 'OPEN'` | `OPEN` or `FULFILLED` |
| `created_at` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP` | Alert creation timestamp |
| `fulfilled_at` | `TIMESTAMP` | `NULL` | Timestamp when fulfilled |

---

## 7. Important Business Rules

1. **Current Stock Calculation**:
   $$\text{Current Stock} = \sum(\text{PURCHASE} + \text{RETURN}) - \sum(\text{SALE} + \text{DAMAGE})$$
2. **Duplicate Alert Suppression**:
   - Evaluated atomically after every stock movement.
   - If dynamic stock $\le$ `reorderThreshold` and **no** `OPEN` alert exists $\rightarrow$ creates a new `OPEN` alert.
   - If an `OPEN` alert already exists $\rightarrow$ skips creation, preventing alert floods.
   - When an alert is fulfilled via `PUT /api/reorder-alerts/{id}/fulfill`, sets `status='FULFILLED'` and records `fulfilled_at`. If stock drops below threshold again later, a new `OPEN` alert is created cleanly.
3. **Negative Stock Prevention**:
   - `SALE` and `DAMAGE` transactions that request more units than current available stock are rejected immediately with `400 InsufficientStockException`.

---

## 8. Complete API List

### Products (`/api/products`)
| Method | Endpoint | Description | Status Codes |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/products` | Create new product | `201 Created`, `400 Bad Request`, `409 Conflict` |
| `GET` | `/api/products` | List all products (paginated) | `200 OK` |
| `GET` | `/api/products/{id}` | Get product details by ID | `200 OK`, `404 Not Found` |
| `PUT` | `/api/products/{id}` | Update product & parameters | `200 OK`, `400 Bad Request`, `404 Not Found`, `409 Conflict` |
| `DELETE`| `/api/products/{id}` | Delete product | `204 No Content`, `404 Not Found` |
| `GET` | `/api/products/{id}/stock`| Get dynamic stock & status | `200 OK`, `404 Not Found` |

### Stock Movements (`/api/stock-movements`)
| Method | Endpoint | Description | Status Codes |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/stock-movements` | Record movement (PURCHASE/SALE/RETURN/DAMAGE) | `201 Created`, `400 Insufficient Stock / Validation`, `404 Not Found` |
| `GET` | `/api/stock-movements` | List movement history (paginated) | `200 OK` |
| `GET` | `/api/stock-movements/product/{id}` | List movements for specific product | `200 OK`, `404 Not Found` |

### Reorder Alerts (`/api/reorder-alerts`)
| Method | Endpoint | Description | Status Codes |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/reorder-alerts` | List all alerts (optional `status` filter) | `200 OK` |
| `GET` | `/api/reorder-alerts/open`| List only OPEN alerts | `200 OK` |
| `GET` | `/api/reorder-alerts/{id}`| Get single alert by ID | `200 OK`, `404 Not Found` |
| `PUT` | `/api/reorder-alerts/{id}/fulfill` | Fulfill alert and record timestamp | `200 OK`, `400 Bad Request`, `404 Not Found` |

### Dashboard & Analytics (`/api/dashboard`, `/api/reports`)
| Method | Endpoint | Description | Status Codes |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/dashboard/summary` | Get live aggregated metrics & summary tables | `200 OK` |
| `GET` | `/api/reports/fast-moving-products`| Ranked sales velocity report | `200 OK`, `400 Bad Request` |

---

## 9. Project Structure

```
RETAILX/
├── backend/
│   ├── src/main/java/com/retailx/inventory/
│   │   ├── config/          # DatabaseConfig, DataInitializer, SwaggerConfig
│   │   ├── controller/      # ProductController, StockMovementController, etc.
│   │   ├── dto/             # ProductDTO, CreateStockMovementRequest, etc.
│   │   ├── entity/          # Product, StockMovement, ReorderAlert, Enums
│   │   ├── exception/       # GlobalExceptionHandler, Custom Exceptions
│   │   ├── repository/      # Spring Data JPA interfaces
│   │   └── service/         # InventoryService, StockMovementService, etc.
│   ├── src/main/resources/  # application.yml, schema.sql, data.sql
│   └── src/test/java/       # InventoryServiceTest, BackendApiIntegrationTest, BackendWorkflowIntegrationTest
│
├── frontend/
│   ├── src/
│   │   ├── components/      # Header, Navigation, ProductForm, StockMovementForm, AlertTable, Card, Table, etc.
│   │   ├── pages/           # Dashboard, Products, StockMovements, ReorderAlerts, FastMovingReport
│   │   ├── layouts/         # MainLayout (with Navbar and Toasts)
│   │   ├── services/        # api.ts, productService.ts, movementService.ts, alertService.ts, etc.
│   │   ├── types/           # TypeScript interfaces
│   │   ├── utils/           # formatters.ts, validators.ts, constants.ts
│   │   ├── hooks/           # useApi.ts, useForm.ts
│   │   ├── App.tsx          # React Router
│   │   └── index.css        # Tailwind light-theme tokens
│   ├── package.json
│   └── vite.config.ts
│
└── README.md
```

---

## 10. Setup Instructions

### Prerequisites
- **Java JDK**: Version 17 or higher
- **Maven**: Version 3.8+
- **Node.js**: Version 18+ (with npm)
- **MySQL Server** (Optional for production mode; H2 is pre-configured for dev)

### Database Setup (For MySQL Profile)
```sql
CREATE DATABASE inventory_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'inventory_user'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON inventory_db.* TO 'inventory_user'@'localhost';
FLUSH PRIVILEGES;
```

---

## 11. How to Run

### 1. Run Backend Service
```bash
cd backend
# Default dev profile (In-memory H2 with seed data):
mvn spring-boot:run

# Or with MySQL profile:
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```
* Backend starts at: `http://localhost:8080`
* Swagger OpenAPI Docs: `http://localhost:8080/swagger-ui.html`
* H2 Database Console: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:retail_inventory`)

### 2. Run Frontend Web App
```bash
cd frontend
npm install
npm run dev
```
* Frontend starts at: `http://localhost:5173`

---

## 12. How to Test

### Automated Backend Tests
Run the comprehensive suite of 23 unit, service, and integration tests:
```bash
cd backend
mvn clean test
```

### Manual Testing Workflow
1. Open `http://localhost:5173/` in your browser.
2. Go to **Products** $\rightarrow$ Click **Add Product** $\rightarrow$ Create `Coca-Cola 500ml` (`COKE500`, Threshold: 20, Reorder Qty: 100).
3. Go to **Stock Movements** $\rightarrow$ Record `PURCHASE` of 150 units $\rightarrow$ Current stock becomes 150.
4. Record `SALE` of 133 units $\rightarrow$ Stock becomes 17 (below threshold 20) $\rightarrow$ An **OPEN Reorder Alert** is generated automatically.
5. Record another `SALE` of 1 unit $\rightarrow$ Stock becomes 16 $\rightarrow$ Notice **no duplicate alert** is created.
6. Go to **Reorder Alerts** $\rightarrow$ Click **Fulfill** on the alert $\rightarrow$ Status changes to `FULFILLED` with timestamp.
7. Go to **Reports** $\rightarrow$ Select date range $\rightarrow$ Click **Generate Report** $\rightarrow$ Verify sales rankings.

---

## 13. Sample Workflow (Step-by-Step)

| Step | Action | Endpoint | Result |
| :--- | :--- | :--- | :--- |
| **1** | Create Product | `POST /api/products` | Product `COKE-01` created (Threshold: 20) |
| **2** | Inflow Stock | `POST /api/stock-movements` | `PURCHASE 150` $\rightarrow$ Stock = 150 |
| **3** | Outflow Stock | `POST /api/stock-movements` | `SALE 133` $\rightarrow$ Stock = 17 $\rightarrow$ Alert #1 (`OPEN`) triggered |
| **4** | Outflow More | `POST /api/stock-movements` | `SALE 2` $\rightarrow$ Stock = 15 $\rightarrow$ Duplicate alert suppressed |
| **5** | Fulfill Alert | `PUT /api/reorder-alerts/1/fulfill` | Alert #1 marked `FULFILLED` |
| **6** | Replenish & Sell | `POST /api/stock-movements` | `PURCHASE 100` (Stock = 115) then `SALE 100` (Stock = 15) $\rightarrow$ New Alert #2 (`OPEN`) created |

---

## 14. Screenshots Section

| Screen | Description |
| :--- | :--- |
| **Dashboard** | 4 Summary Metric Cards, Open Alerts Table, Top 5 Fast-Moving Products, Recent Movements Log |
| **Products** | Paginated catalog with real-time stock status badges, add/edit modal, details view, and deletion dialog |
| **Stock Movements** | Transaction form with dynamic stock calculation cards and sortable audit history |
| **Reorder Alerts** | Alert management with status filter (All/Open) and instant fulfillment confirmation |
| **Reports** | Custom date range picker with client/server validation and ranked sales velocity table |

---

## 15. Troubleshooting

* **Port 8080 already in use**:
  Configure a different port in `backend/src/main/resources/application.yml` (`server.port: 8081`).
* **Insufficient Stock on Sale**:
  Ensure you have recorded a `PURCHASE` or `RETURN` movement before attempting a `SALE`.
* **Duplicate SKU error**:
  SKU codes must be globally unique across products. Use distinct SKU values.
* **CORS Errors**:
  Controllers are annotated with `@CrossOrigin(origins = "*")` allowing seamless local development on any port.
