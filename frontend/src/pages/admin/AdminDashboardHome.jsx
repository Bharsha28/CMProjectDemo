import Layout from "../../components/Layout";
import PageHeader from "../../components/PageHeader";
import StatCard from "../../components/StatCard";
import DataTable from "../../components/DataTable";
import { useState, useEffect } from "react";
import { adminApi, customerApi, operationsApi } from "../../services/api";
function AdminDashboardHome() {
  const [data, setData] = useState({ users: [], applications: [], statements: [], payments: [] });

  useEffect(() => {
    async function fetchDashboard() {
      try {
        const [u, a, s, p] = await Promise.all([
          adminApi.getUsers().catch(() => []),
          customerApi.getApplications().catch(() => []),
          operationsApi.getStatements().catch(() => []),
          operationsApi.getPayments().catch(() => [])
        ]);
        setData({ users: u, applications: a, statements: s, payments: p });
      } catch (e) { console.error(e); }
    }
    fetchDashboard();
  }, []);

  const stats = [
    { title: "Users", value: data.users.length, icon: "bi-people", accent: "primary" },
    { title: "Applications", value: data.applications.length, icon: "bi-files", accent: "warning" },
    { title: "Statements", value: data.statements.length, icon: "bi-journal-text", accent: "success" },
    { title: "Payments", value: data.payments.length, icon: "bi-wallet2", accent: "info" }
  ];

  return (
    <Layout section="admin" title="Admin Dashboard">
      <PageHeader
        title="Admin Dashboard Home"
        subtitle="Manage users, products, fee setup and view system-wide information."
      />

      <div className="row g-3 mb-4">
        {stats.map((item) => (
          <StatCard
            key={item.title}
            title={item.title}
            value={item.value}
            icon={item.icon}
            accent={item.accent}
          />
        ))}
      </div>

      <div className="card border-0 shadow-sm">
        <div className="card-body">
          <h5 className="mb-3">All Applications Snapshot</h5>
          <DataTable
            columns={[
              { key: "customerName", label: "Customer" },
              { key: "customerEmail", label: "Customer Email" },
              { key: "productName", label: "Product" },
              { key: "requestedLimit", label: "Requested Limit" },
              { key: "status", label: "Status", type: "status" }
            ]}
            rows={data.applications}
          />
        </div>
      </div>
    </Layout>
  );
}

export default AdminDashboardHome;
