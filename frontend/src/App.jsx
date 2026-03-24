import { Navigate, Route, Routes } from "react-router-dom";
import HomePage from "./pages/HomePage";
import LoginPage from "./pages/auth/LoginPage";
import RegisterPage from "./pages/auth/RegisterPage";
import LogoutPage from "./pages/auth/LogoutPage";
import CustomerDashboardHome from "./pages/customer/CustomerDashboardHome";
import CustomerWizardPage from "./pages/customer/CustomerWizardPage";
import CustomerCardsPage from "./pages/customer/CustomerCardsPage";
import CustomerStatementsPage from "./pages/customer/CustomerStatementsPage";
import CustomerTransactionsPage from "./pages/customer/CustomerTransactionsPage";
import CustomerPaymentsPage from "./pages/customer/CustomerPaymentsPage";
import UnderwriterDashboardHome from "./pages/underwriter/UnderwriterDashboardHome";
import UnderwriterApplicationListPage from "./pages/underwriter/UnderwriterApplicationListPage";
import CreditScoreViewPage from "./pages/underwriter/CreditScoreViewPage";
import UnderwritingDecisionPage from "./pages/underwriter/UnderwritingDecisionPage";
import OperationsDashboardHome from "./pages/operations/OperationsDashboardHome";
import CardIssuancePage from "./pages/operations/CardIssuancePage";
import CardAccountPage from "./pages/operations/CardAccountPage";
import TransactionsListPage from "./pages/operations/TransactionsListPage";
import StatementsListPage from "./pages/operations/StatementsListPage";
import PaymentsListPage from "./pages/operations/PaymentsListPage";
import AdminDashboardHome from "./pages/admin/AdminDashboardHome";
import UserManagementPage from "./pages/admin/UserManagementPage";
import CardProductPage from "./pages/admin/CardProductPage";
import FeeConfigPage from "./pages/admin/FeeConfigPage";
import AuditLogPage from "./pages/admin/AuditLogPage";
import { getDefaultDashboardPath, getStoredSession, isAuthenticated } from "./utils/auth";

function RequireAuth({ children, allowedRoles }) {
  const session = getStoredSession();

  if (!isAuthenticated()) {
    return <Navigate to="/login" replace />;
  }

  if (allowedRoles && !allowedRoles.includes(session?.role)) {
    return <Navigate to={getDefaultDashboardPath(session?.role)} replace />;
  }

  return children;
}

function RootRedirect() {
  const session = getStoredSession();

  if (!isAuthenticated()) {
    return <Navigate to="/login" replace />;
  }

  return <Navigate to={getDefaultDashboardPath(session?.role)} replace />;
}

function App() {
  return (
    <Routes>
      <Route path="/" element={<RootRedirect />} />
      <Route path="/home" element={<HomePage />} />
      <Route path="/login" element={isAuthenticated() ? <RootRedirect /> : <LoginPage />} />
      <Route path="/register" element={isAuthenticated() ? <RootRedirect /> : <RegisterPage />} />
      <Route path="/logout" element={<LogoutPage />} />

      <Route
        path="/customer"
        element={
          <RequireAuth allowedRoles={["CUSTOMER"]}>
            <CustomerDashboardHome />
          </RequireAuth>
        }
      />
      <Route
        path="/customer/wizard"
        element={
          <RequireAuth allowedRoles={["CUSTOMER"]}>
            <CustomerWizardPage />
          </RequireAuth>
        }
      />
      <Route
        path="/customer/cards"
        element={
          <RequireAuth allowedRoles={["CUSTOMER"]}>
            <CustomerCardsPage />
          </RequireAuth>
        }
      />
      <Route
        path="/customer/statements"
        element={
          <RequireAuth allowedRoles={["CUSTOMER"]}>
            <CustomerStatementsPage />
          </RequireAuth>
        }
      />
      <Route
        path="/customer/transactions"
        element={
          <RequireAuth allowedRoles={["CUSTOMER"]}>
            <CustomerTransactionsPage />
          </RequireAuth>
        }
      />
      <Route
        path="/customer/payments"
        element={
          <RequireAuth allowedRoles={["CUSTOMER"]}>
            <CustomerPaymentsPage />
          </RequireAuth>
        }
      />

      <Route
        path="/underwriter"
        element={
          <RequireAuth allowedRoles={["UNDERWRITER"]}>
            <UnderwriterDashboardHome />
          </RequireAuth>
        }
      />
      <Route
        path="/underwriter/applications"
        element={
          <RequireAuth allowedRoles={["UNDERWRITER"]}>
            <UnderwriterApplicationListPage />
          </RequireAuth>
        }
      />
      <Route
        path="/underwriter/credit-scores"
        element={
          <RequireAuth allowedRoles={["UNDERWRITER"]}>
            <CreditScoreViewPage />
          </RequireAuth>
        }
      />
      <Route
        path="/underwriter/decisions"
        element={
          <RequireAuth allowedRoles={["UNDERWRITER"]}>
            <UnderwritingDecisionPage />
          </RequireAuth>
        }
      />

      <Route
        path="/operations"
        element={
          <RequireAuth allowedRoles={["OFFICER", "RISK"]}>
            <OperationsDashboardHome />
          </RequireAuth>
        }
      />
      <Route
        path="/operations/card-issuance"
        element={
          <RequireAuth allowedRoles={["OFFICER", "RISK"]}>
            <CardIssuancePage />
          </RequireAuth>
        }
      />
      <Route
        path="/operations/card-accounts"
        element={
          <RequireAuth allowedRoles={["OFFICER", "RISK"]}>
            <CardAccountPage />
          </RequireAuth>
        }
      />
      <Route
        path="/operations/transactions"
        element={
          <RequireAuth allowedRoles={["OFFICER", "RISK"]}>
            <TransactionsListPage />
          </RequireAuth>
        }
      />
      <Route
        path="/operations/statements"
        element={
          <RequireAuth allowedRoles={["OFFICER", "RISK"]}>
            <StatementsListPage />
          </RequireAuth>
        }
      />
      <Route
        path="/operations/payments"
        element={
          <RequireAuth allowedRoles={["OFFICER", "RISK"]}>
            <PaymentsListPage />
          </RequireAuth>
        }
      />

      <Route
        path="/admin"
        element={
          <RequireAuth allowedRoles={["ADMIN"]}>
            <AdminDashboardHome />
          </RequireAuth>
        }
      />
      <Route
        path="/admin/users"
        element={
          <RequireAuth allowedRoles={["ADMIN"]}>
            <UserManagementPage />
          </RequireAuth>
        }
      />
      <Route
        path="/admin/products"
        element={
          <RequireAuth allowedRoles={["ADMIN"]}>
            <CardProductPage />
          </RequireAuth>
        }
      />
      <Route
        path="/admin/fees"
        element={
          <RequireAuth allowedRoles={["ADMIN"]}>
            <FeeConfigPage />
          </RequireAuth>
        }
      />
      <Route
        path="/admin/audit-logs"
        element={
          <RequireAuth allowedRoles={["ADMIN"]}>
            <AuditLogPage />
          </RequireAuth>
        }
      />

      <Route path="*" element={<RootRedirect />} />
    </Routes>
  );
}

export default App;
