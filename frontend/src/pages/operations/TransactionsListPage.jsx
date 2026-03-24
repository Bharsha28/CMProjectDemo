import { useEffect, useState } from "react";
import Layout from "../../components/Layout";
import PageHeader from "../../components/PageHeader";
import DataTable from "../../components/DataTable";
import { operationsApi } from "../../services/api";

const initialForm = {
  accountId: "",
  amount: "",
  currency: "INR",
  merchant: "",
  channel: "ONLINE"
};

function TransactionsListPage() {
  const [rows, setRows] = useState([]);
  const [accountList, setAccountList] = useState([]);
  const [formData, setFormData] = useState(initialForm);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    async function loadTransactions() {
      try {
        const [tData, aData] = await Promise.all([
           operationsApi.getTransactions().catch(() => []),
           operationsApi.getAccounts().catch(() => [])
        ]);
        setRows(tData);
        setAccountList(aData);
        if (aData.length > 0) {
            setFormData(f => ({...f, accountId: String(aData[0].accountId)}));
        }
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
    setLoading(true);
    setError("");
    setMessage("");

    try {
      const created = await operationsApi.authorizeTransaction({
        accountId: Number(formData.accountId),
        amount: Number(formData.amount),
        currency: formData.currency,
        merchant: formData.merchant,
        channel: formData.channel
      });
      setRows((current) => [created, ...current]);
      setMessage("Transaction authorized through the backend.");
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
      const updated = await operationsApi.postTransaction(transactionId);
      setRows((current) =>
        current.map((row) => (row.transactionId === transactionId ? { ...row, ...updated } : row))
      );
      setMessage(`Transaction ${transactionId} posted through the backend.`);
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
      setMessage(`Transaction ${transactionId} reversed through the backend.`);
    } catch (submitError) {
      setError(submitError.message || "Unable to reverse the transaction.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <Layout section="operations" title="Operations Dashboard">
      <PageHeader
        title="Transactions List Page"
        subtitle="Authorize, post and reverse transactions so backend transaction tables are updated."
      />

      <div className="card border-0 shadow-sm mb-4">
        <div className="card-body p-4">
          <form onSubmit={handleAuthorize} className="row g-3">
            <div className="col-md-3">
              <label className="form-label">Account</label>
              <select className="form-select" name="accountId" value={formData.accountId} onChange={handleChange}>
                {accountList.map((account) => (
                  <option key={account.accountId} value={account.accountId}>
                    {account.customerEmail} - {account.accountId}
                  </option>
                ))}
              </select>
            </div>
            <div className="col-md-2">
              <label className="form-label">Amount</label>
              <input type="number" className="form-control" name="amount" value={formData.amount} onChange={handleChange} required />
            </div>
            <div className="col-md-2">
              <label className="form-label">Currency</label>
              <input className="form-control" name="currency" value={formData.currency} onChange={handleChange} required />
            </div>
            <div className="col-md-3">
              <label className="form-label">Merchant</label>
              <input className="form-control" name="merchant" value={formData.merchant} onChange={handleChange} required />
            </div>
            <div className="col-md-2">
              <label className="form-label">Channel</label>
              <select className="form-select" name="channel" value={formData.channel} onChange={handleChange}>
                <option>ONLINE</option>
                <option>POS</option>
                <option>ATM</option>
              </select>
            </div>
            <div className="col-12">
              <button className="btn btn-primary" disabled={loading}>
                {loading ? "Saving..." : "Authorize Transaction"}
              </button>
            </div>
          </form>
          {message ? <div className="alert alert-success mt-4 mb-0">{message}</div> : null}
          {error ? <div className="alert alert-danger mt-4 mb-0">{error}</div> : null}
        </div>
      </div>

      <div className="card border-0 shadow-sm">
        <div className="card-body">
          <DataTable
            columns={[
              { key: "transactionId", label: "Transaction ID" },
              { key: "accountId", label: "Account ID" },
              { key: "merchant", label: "Merchant" },
              { key: "amount", label: "Amount" },
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
          />
        </div>
      </div>
    </Layout>
  );
}

export default TransactionsListPage;
