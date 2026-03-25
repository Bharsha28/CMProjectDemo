import { useEffect, useState } from "react";
import Layout from "../../components/Layout";
import PageHeader from "../../components/PageHeader";
import DataTable from "../../components/DataTable";
import { customerApi } from "../../services/api";

const initialForm = {
  amount: "",
  currency: "USD",
  merchant: "",
  channel: "ONLINE"
};

function CustomerTransactionsPage() {
  const [rows, setRows] = useState([]);
  const [account, setAccount] = useState(null);
  const [formData, setFormData] = useState(initialForm);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    async function loadTransactions() {
      try {
        const [myAccount, tData] = await Promise.all([
           customerApi.getMyAccount(),
           customerApi.getMyTransactions()
        ]);
        setAccount(myAccount);
        setRows(Array.isArray(tData) ? tData : []);
      } catch (e) {
          console.error(e);
      }
    }

    loadTransactions();
  }, []);

  function handleChange(event) {
    const { name, value } = event.target;
    setFormData((current) => ({ ...current, [name]: value }));
  }

  async function handleAuthorize(event) {
    event.preventDefault();
    if (!account?.accountId) {
      setError("No active account found to make a transaction.");
      return;
    }

    setLoading(true);
    setError("");
    setMessage("");

    try {
      const created = await customerApi.authorizeTransaction({
        accountId: Number(account.accountId),
        amount: Number(formData.amount),
        currency: formData.currency,
        merchant: formData.merchant,
        channel: formData.channel
      });
      setRows((current) => [created, ...current]);
      setMessage("Transaction authorized successfully.");
      setFormData(initialForm);
    } catch (submitError) {
      setError(submitError.message || "Transaction authorization failed.");
    } finally {
      setLoading(false);
    }
  }

  async function handlePost(transactionId) {
    setLoading(true);
    setError("");
    setMessage("");

    try {
      const updated = await customerApi.postTransaction(transactionId);
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
      const updated = await customerApi.reverseTransaction(transactionId);
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
    <Layout section="customer" title="My Transactions">
      <PageHeader
        title="Simulate a Transaction"
        subtitle="Authorize, post, and reverse test transactions on your active card account."
      />

      <div className="card border-0 shadow-sm mb-4">
        <div className="card-body p-4">
          <form onSubmit={handleAuthorize} className="row g-4">
            <div className="col-md-6">
              <label className="form-label">Merchant Name</label>
              <input className="form-control" name="merchant" value={formData.merchant} onChange={handleChange} required placeholder="e.g. Amazon, Starbucks" />
            </div>
            <div className="col-md-6">
              <label className="form-label">Amount ($)</label>
              <input type="number" className="form-control" name="amount" value={formData.amount} onChange={handleChange} required placeholder="0.00" />
            </div>
            <div className="col-md-6">
              <label className="form-label">Currency</label>
              <input className="form-control" name="currency" value={formData.currency} onChange={handleChange} required />
            </div>
            <div className="col-md-6">
              <label className="form-label">Channel</label>
              <select className="form-select" name="channel" value={formData.channel} onChange={handleChange}>
                <option value="ONLINE">Online</option>
                <option value="POS">POS (In-Store)</option>
                <option value="ATM">ATM</option>
              </select>
            </div>
            <div className="col-12 text-end pt-2">
              <button className="btn btn-primary px-5" disabled={loading || !account}>
                {loading ? "Processing..." : "Authorize Transaction"}
              </button>
            </div>
          </form>
          {message && <div className="alert alert-success mt-4 mb-0">{message}</div>}
          {error && <div className="alert alert-danger mt-4 mb-0">{error}</div>}
        </div>
      </div>

      <div className="card border-0 shadow-sm">
        <div className="card-body">
          <DataTable
            columns={[
              { key: "merchant", label: "Merchant" },
              { key: "amount", label: "Amount", render: (row) => `${row.currency} ${Number(row.amount || 0).toLocaleString()}` },
              { key: "transactionDate", label: "Date" },
              { key: "channel", label: "Channel" },
              { key: "status", label: "Status", type: "status" },
              {
                key: "actions",
                label: "Simulate Merchant Actions",
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
            emptyMessage="No transactions found."
          />
        </div>
      </div>
      
      {!loading && (
        <div className="mt-4 p-3 bg-white shadow-sm rounded border-start border-primary border-4">
          <h6 className="fw-bold mb-1">Authorization vs Posting</h6>
          <p className="small text-muted mb-0">
            'Authorized' transactions are pending and deduct from your available limit. 'Post' them to finalize the transaction into your statement balance.
          </p>
        </div>
      )}
    </Layout>
  );
}

export default CustomerTransactionsPage;
