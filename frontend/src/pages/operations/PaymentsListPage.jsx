import { useEffect, useState } from "react";
import Layout from "../../components/Layout";
import PageHeader from "../../components/PageHeader";
import DataTable from "../../components/DataTable";
import { operationsApi } from "../../services/api";

const initialForm = {
  accountId: "",
  statementId: "",
  amount: "",
  paymentDate: "",
  method: "UPI",
  status: "COMPLETED"
};

function PaymentsListPage() {
  const [rows, setRows] = useState([]);
  const [accountList, setAccountList] = useState([]);
  const [statementList, setStatementList] = useState([]);
  const [formData, setFormData] = useState(initialForm);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    async function loadPayments() {
      try {
        const [pData, aData, sData] = await Promise.all([
          operationsApi.getPayments().catch(() => []),
          operationsApi.getAccounts().catch(() => []),
          operationsApi.getStatements().catch(() => [])
        ]);
        setRows(pData);
        setAccountList(aData);
        setStatementList(sData);
        if (aData.length > 0) {
           setFormData(f => ({...f, accountId: String(aData[0].accountId)}));
        }
      } catch (e) {
        console.error(e);
      }
    }

    loadPayments();
  }, []);

  function handleChange(event) {
    const { name, value } = event.target;
    setFormData((current) => ({ ...current, [name]: value }));
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setLoading(true);
    setError("");
    setMessage("");

    try {
      const created = await operationsApi.createPayment({
        accountId: Number(formData.accountId),
        statementId: formData.statementId ? Number(formData.statementId) : null,
        amount: Number(formData.amount),
        paymentDate: formData.paymentDate ? `${formData.paymentDate}T00:00:00` : null,
        method: formData.method,
        status: formData.status
      });

      setRows((current) => [created, ...current]);
      setMessage("Payment captured through the backend.");
      setFormData(initialForm);
    } catch (submitError) {
      setError(submitError.message || "Payment capture failed.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <Layout section="operations" title="Operations Dashboard">
      <PageHeader
        title="Payments List Page"
        subtitle="Track payments received and their latest processing status."
      />

      <div className="card border-0 shadow-sm mb-4">
        <div className="card-body p-4">
          <form onSubmit={handleSubmit} className="row g-3">
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
            <div className="col-md-3">
              <label className="form-label">Open Statement</label>
              <select className="form-select" name="statementId" value={formData.statementId} onChange={handleChange}>
                <option value="">Auto-pick latest open statement</option>
                {statementList
                  .filter((statement) => statement.status === "OPEN")
                  .map((statement) => (
                    <option key={statement.statementId} value={statement.statementId}>
                      {statement.statementId} - Account {statement.accountId}
                    </option>
                  ))}
              </select>
            </div>
            <div className="col-md-2">
              <label className="form-label">Amount</label>
              <input type="number" className="form-control" name="amount" value={formData.amount} onChange={handleChange} required />
            </div>
            <div className="col-md-2">
              <label className="form-label">Date</label>
              <input type="date" className="form-control" name="paymentDate" value={formData.paymentDate} onChange={handleChange} />
            </div>
            <div className="col-md-2">
              <label className="form-label">Method</label>
              <select className="form-select" name="method" value={formData.method} onChange={handleChange}>
                <option>UPI</option>
                <option>NETBANKING</option>
                <option>CASH</option>
                <option>CHEQUE</option>
              </select>
            </div>
            <div className="col-md-3">
              <label className="form-label">Status</label>
              <select className="form-select" name="status" value={formData.status} onChange={handleChange}>
                <option>COMPLETED</option>
                <option>PENDING</option>
                <option>FAILED</option>
              </select>
            </div>
            <div className="col-md-9 d-flex align-items-end">
              <button className="btn btn-primary" disabled={loading}>
                {loading ? "Saving..." : "Capture Payment"}
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
              { key: "paymentId", label: "Payment ID" },
              { key: "accountId", label: "Account ID" },
              { key: "customerName", label: "Customer" },
              { key: "customerEmail", label: "Customer Email" },
              { key: "amount", label: "Amount" },
              { key: "method", label: "Method" },
              { key: "paymentDate", label: "Date" },
              { key: "status", label: "Status", type: "status" }
            ]}
            rows={rows}
          />
        </div>
      </div>
    </Layout>
  );
}

export default PaymentsListPage;
