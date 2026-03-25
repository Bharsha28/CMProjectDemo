import { useEffect, useState } from "react";
import Layout from "../../components/Layout";
import PageHeader from "../../components/PageHeader";
import DataTable from "../../components/DataTable";
import { operationsApi } from "../../services/api";

function TransactionsListPage() {
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    async function loadTransactions() {
      try {
        setLoading(true);
        const tData = await operationsApi.getTransactions();
        setRows(tData || []);
      } catch (e) {
        setError("Failed to load operations transactions.");
        console.error(e);
      } finally {
        setLoading(false);
      }
    }

    loadTransactions();
  }, []);

  return (
    <Layout section="operations" title="Operations Dashboard">
      <PageHeader
        title="All System Transactions"
        subtitle="Monitor all transaction activities across the bank's card portfolio."
      />

      <div className="card border-0 shadow-sm">
        <div className="card-body">
          <DataTable
            columns={[
              { key: "transactionId", label: "Transaction ID" },
              { key: "accountId", label: "Account ID" },
              { key: "merchant", label: "Merchant" },
              { key: "amount", label: "Amount", render: (row) => `${row.currency} ${Number(row.amount || 0).toLocaleString()}` },
              { key: "channel", label: "Channel" },
              { key: "transactionDate", label: "Date" },
              { key: "status", label: "Status", type: "status" }
            ]}
            rows={rows}
            emptyMessage="No transactions found in the system."
          />
        </div>
      </div>
      {error && <div className="alert alert-danger mt-4 mx-3">{error}</div>}
    </Layout>
  );
}

export default TransactionsListPage;
