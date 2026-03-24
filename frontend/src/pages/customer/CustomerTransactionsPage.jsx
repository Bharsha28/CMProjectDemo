import { useEffect, useState } from "react";
import Layout from "../../components/Layout";
import PageHeader from "../../components/PageHeader";
import DataTable from "../../components/DataTable";
import { transactions } from "../../data/mockData";
import { customerApi } from "../../services/api";

function CustomerTransactionsPage() {
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    async function loadTransactions() {
      setLoading(true);
      try {
        const response = await customerApi.getMyTransactions();
        setRows(Array.isArray(response) ? response : []);
      } catch (err) {
        setError("Failed to load your transaction history.");
      } finally {
        setLoading(false);
      }
    }

    loadTransactions();
  }, []);

  return (
    <Layout section="customer" title="My Transactions">
      <PageHeader
        title="Transaction History"
        subtitle="Monitor your spends, authorizations, and refunds across all your cards."
      />

      <div className="card border-0 shadow-sm overflow-hidden">
        <div className="card-body p-0">
          <DataTable
            columns={[
              { key: "transactionId", label: "Transaction ID" },
              { key: "merchant", label: "Merchant" },
              { key: "amount", label: "Amount", render: (val) => `$${Number(val).toLocaleString()}` },
              { key: "currency", label: "Currency" },
              { key: "channel", label: "Channel" },
              { key: "transactionDate", label: "Date" },
              { key: "status", label: "Status", type: "status" }
            ]}
            rows={rows}
            emptyMessage="No transactions found."
          />
        </div>
      </div>
      
      {error && <div className="alert alert-danger mt-4 mx-3">{error}</div>}
      
      {!loading && (
        <div className="mt-4 p-3 bg-white shadow-sm rounded border-start border-primary border-4">
          <h6 className="fw-bold mb-1">Authorization vs Posting</h6>
          <p className="small text-muted mb-0">
            'Authorized' transactions are pending and may take 2-3 days to 'Post' to your final balance.
          </p>
        </div>
      )}
    </Layout>
  );
}

export default CustomerTransactionsPage;
