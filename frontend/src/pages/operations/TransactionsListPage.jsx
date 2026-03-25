import { useEffect, useState } from "react";
import Layout from "../../components/Layout";
import PageHeader from "../../components/PageHeader";
import DataTable from "../../components/DataTable";
import { operationsApi } from "../../services/api";

function TransactionsListPage() {
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");

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

  async function handlePost(transactionId) {
    setLoading(true);
    setError("");
    setMessage("");

    try {
      const updated = await operationsApi.postTransaction(transactionId);
      setRows((current) =>
        current.map((row) => (row.transactionId === transactionId ? { ...row, ...updated } : row))
      );
      setMessage(`Transaction ${transactionId} posted successfully.`);
    } catch (submitError) {
      setError(submitError.message || "Unable to post the transaction.");
    } finally {
      setLoading(false);
    }
  }

  async function handleReverse(transactionId) {
    setLoading(true);
    setError("");
    setMessage("");

    try {
      const updated = await operationsApi.reverseTransaction(transactionId);
      setRows((current) =>
        current.map((row) => (row.transactionId === transactionId ? { ...row, ...updated } : row))
      );
      setMessage(`Transaction ${transactionId} reversed successfully.`);
    } catch (submitError) {
      setError(submitError.message || "Unable to reverse the transaction.");
    } finally {
      setLoading(false);
    }
  }

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
              { key: "status", label: "Status", type: "status" },
              {
                key: "actions",
                label: "Actions",
                render: (row) => (
                  <div className="d-flex gap-2">
                    <button
                      className="btn btn-sm btn-outline-success"
                      disabled={loading || row.status !== "AUTHORIZED"}
                      onClick={() => handlePost(row.transactionId)}
                    >
                      Post
                    </button>
                    <button
                      className="btn btn-sm btn-outline-danger"
                      disabled={loading || !["AUTHORIZED", "POSTED"].includes(row.status)}
                      onClick={() => handleReverse(row.transactionId)}
                    >
                      Reverse
                    </button>
                  </div>
                )
              }
            ]}
            rows={rows}
            emptyMessage="No transactions found in the system."
          />
        </div>
      </div>
      {message && <div className="alert alert-success mt-4 mx-3">{message}</div>}
      {error && <div className="alert alert-danger mt-4 mx-3">{error}</div>}
    </Layout>
  );
}

export default TransactionsListPage;
