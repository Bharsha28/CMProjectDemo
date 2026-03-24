import Layout from "../../components/Layout";
import PageHeader from "../../components/PageHeader";
import StatCard from "../../components/StatCard";
import DataTable from "../../components/DataTable";
import { cards, payments, statements, transactions } from "../../data/mockData";

function OperationsDashboardHome() {
  const stats = [
    { title: "Cards Issued", value: cards.length, icon: "bi-credit-card", accent: "primary" },
    { title: "Posted Transactions", value: transactions.filter((item) => item.status === "Posted").length, icon: "bi-arrow-left-right", accent: "success" },
    { title: "Open Statements", value: statements.filter((item) => item.status === "Open").length, icon: "bi-receipt", accent: "warning" },
    { title: "Payments Received", value: payments.length, icon: "bi-cash-stack", accent: "info" }
  ];

  return (
    <Layout section="operations" title="Operations Dashboard">
      <PageHeader
        title="Operations Dashboard Page"
        subtitle="Track issuance, accounts, statements, payments and transaction operations."
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
          <h5 className="mb-3">Latest Card Activity</h5>
          <DataTable
            columns={[
              { key: "customerName", label: "Customer" },
              { key: "customerEmail", label: "Customer Email" },
              { key: "productName", label: "Product" },
              { key: "maskedCardNumber", label: "Card Number" },
              { key: "status", label: "Status", type: "status" }
            ]}
            rows={cards}
          />
        </div>
      </div>
    </Layout>
  );
}

export default OperationsDashboardHome;
