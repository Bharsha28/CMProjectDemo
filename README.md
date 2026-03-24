# CardMaster

CardMaster is a credit card issuance, transactions, billing, and risk management system with:

- Spring Boot backend at the project root
- React + Vite frontend inside `frontend/`

## Run The Project

Use these steps to run the application locally.

### Backend

1. Make sure MySQL is running on `127.0.0.1:3306`
2. Make sure the database in `src/main/resources/application.yml` exists
3. Start the backend:

```bash
mvn spring-boot:run
```

The backend is configured for port `8082`.

### Frontend

1. Move to the frontend folder:

```bash
cd frontend
```

2. Install dependencies if needed:

```bash
npm install
```

3. Start the frontend:

```bash
npm run dev
```

The frontend runs on Vite's local port such as `5173` or `5174`.

## How To Run The Full Application

1. Start MySQL first
2. Start the backend from the project root:

```bash
mvn spring-boot:run
```

3. Open a new terminal and start the frontend:

```bash
cd frontend
npm run dev
```

4. Open the frontend in your browser:

- `http://127.0.0.1:5173/`
- or the Vite port shown in the terminal, such as `http://127.0.0.1:5174/`

5. Login and open the dashboard for your role

## If Backend Is Not Running

- Dashboard viewing can still work with mock login fallback
- Registration, customer creation, application creation, and other backend save actions will not work
- If MySQL is down, Spring Boot will fail to start on `8082`

## Mock Dashboard Access

When the backend login is unavailable, the frontend supports mock login fallback for dashboard viewing.

These mock credentials are not shown in the UI anymore, but they still work in the login form:

- Customer: `customer@mock.com` / `Customer@123`
- Underwriter: `underwriter@mock.com` / `Underwriter@123`
- Operations Officer: `officer@mock.com` / `Officer@123`
- Risk Analyst: `risk@mock.com` / `Risk@1234`
- Admin: `admin@mock.com` / `Admin@123`

Important:

- Mock login is only for dashboard access
- Registration is still a real backend API flow
- If backend is down, registration will not work

## How To Remove Mock Login Later

Follow these steps when you want only real backend authentication:

1. Open `frontend/src/services/api.js`
2. Delete the `mockRoleCredentials` export
3. In `customerApi`, remove the mock-session fallback branches used in:
   - `getMyCustomer`
   - `createApplication`
   - `uploadDocument`
   - `getProductsStrict`
4. Open `frontend/src/utils/auth.js`
5. Delete the `isMockSession` helper
6. In `authApi.login`, remove the `catch` block that matches email and password against `mockRoleCredentials`
7. Keep only the real `/users/login` backend call inside `authApi.login`
8. Save the files
9. Rebuild the frontend:

```bash
cd frontend
npm run build
```

After that, only real backend login will work.

## Notes

- Customer dashboard pages now include customer-only cards, account summary, transactions, statements, and payments
- Frontend calls backend on `http://localhost:8082`
- If backend is down, some screens use fallback demo data for viewing
