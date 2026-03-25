import { useEffect, useState } from "react";
import Layout from "../../components/Layout";
import PageHeader from "../../components/PageHeader";
import DataTable from "../../components/DataTable";
import { operationsApi } from "../../services/api";

function PaymentsListPage() {
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    async function loadPayments() {
      try {
        setLoading(true);
        const pData = await operationsApi.getPayments();
        setRows(pData || []);
      } catch (e) {
        setError("Failed to load payment history.");
        console.error(e);
      } finally {
        setLoading(false);
      }
    }

    loadPayments();
  }, []);

  return (
    <Layout section="operations" title="Operations Dashboard">
      <PageHeader
        title="All System Payments"
        subtitle="Track payments received across all customer accounts."
      />

      <div className="card border-0 shadow-sm">
        <div className="card-body">
          <DataTable
            columns={[
              { key: "paymentId", label: "Payment ID" },
              { key: "accountId", label: "Account" },
              { key: "customerEmail", label: "Customer Email" },
              { key: "amount", label: "Amount", render: (row) => `$${Number(row.amount || 0).toLocaleString()}` },
              { key: "method", label: "Method" },
              { key: "paymentDate", label: "Date" },
              { key: "status", label: "Status", type: "status" }
            ]}
            rows={rows}
            emptyMessage="No payments found in the system."
          />
        </div>
      </div>
      {error && <div className="alert alert-danger mt-4 mx-3">{error}</div>}
    </Layout>
  );
}

export default PaymentsListPage;
