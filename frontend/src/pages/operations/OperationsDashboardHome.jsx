import Layout from "../../components/Layout";
import PageHeader from "../../components/PageHeader";
import StatCard from "../../components/StatCard";
import DataTable from "../../components/DataTable";
import { useState, useEffect } from "react";
import { operationsApi } from "../../services/api";
function OperationsDashboardHome() {
  const [data, setData] = useState({ cards: [], transactions: [], statements: [], payments: [] });

  useEffect(() => {
    async function loadDashboard() {
      try {
        const [c, t, s, p] = await Promise.all([
          operationsApi.getCards().catch(() => []),
          operationsApi.getTransactions().catch(() => []),
          operationsApi.getStatements().catch(() => []),
          operationsApi.getPayments().catch(() => [])
        ]);
        setData({ cards: c, transactions: t, statements: s, payments: p });
      } catch (e) { console.error(e); }
    }
    loadDashboard();
  }, []);

  const stats = [
    { title: "Cards Issued", value: data.cards.length, icon: "bi-credit-card", accent: "primary" },
    { title: "Posted Transactions", value: data.transactions.filter((item) => item.status === "Posted").length, icon: "bi-arrow-left-right", accent: "success" },
    { title: "Open Statements", value: data.statements.filter((item) => item.status === "Open").length, icon: "bi-receipt", accent: "warning" },
    { title: "Payments Received", value: data.payments.length, icon: "bi-cash-stack", accent: "info" }
  ];

  return (
    <Layout section="operations" title="Operations Dashboard">
      <div className="mb-4">
        <p className="text-muted">Track issuance, accounts, statements, payments and transaction operations.</p>
      </div>

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

        <div className="col-12">
          <div className="card border-0 shadow-sm">
            <div className="card-body">
              <h5 className="mb-3">Latest Card Activity</h5>
              <DataTable
                columns={[
                  { key: "customerName", label: "Customer" },
                  { key: "productName", label: "Product" },
                  { key: "maskedCardNumber", label: "Card Number" },
                  { key: "status", label: "Status", type: "status" }
                ]}
                rows={data.cards}
              />
            </div>
          </div>
        </div>
    </Layout>
  );
}

export default OperationsDashboardHome;
