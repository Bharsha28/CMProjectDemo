import { useEffect, useState } from "react";
import Layout from "../../components/Layout";
import PageHeader from "../../components/PageHeader";
import DataTable from "../../components/DataTable";
import { customerApi } from "../../services/api";

function CustomerPaymentsPage() {
  const [rows, setRows] = useState([]);
  const [account, setAccount] = useState(null);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");

  const [paymentForm, setPaymentForm] = useState({
    amount: "",
    paymentMethod: "NETBANKING", // Changed from BankTransfer
    reference: ""
  });

  useEffect(() => {
    async function loadData() {
      setLoading(true);
      try {
        const [myAccount, myPayments] = await Promise.all([
          customerApi.getMyAccount(),
          customerApi.getMyPayments()
        ]);
        setAccount(myAccount);
        setRows(Array.isArray(myPayments) ? myPayments : []);
      } catch (err) {
        setError("Failed to load payment data.");
      } finally {
        setLoading(false);
      }
    }
    loadData();
  }, []);

  async function handlePayment(e) {
    e.preventDefault();
    if (!account?.accountId) {
        setError("No active account found to make a payment.");
        return;
    }

    setSubmitting(true);
    setError("");
    setMessage("");

    try {
        const payload = {
            accountId: account.accountId,
            amount: Number(paymentForm.amount),
            paymentDate: new Date().toISOString(),
            method: paymentForm.paymentMethod,
            status: "COMPLETED"
        };
        const response = await customerApi.capturePayment(payload);
        setRows(curr => [response?.data || response, ...curr]);
        setMessage("Payment captured successfully! Your available limit has been updated.");
        setPaymentForm({ amount: "", paymentMethod: "NETBANKING", reference: "" });
        // Re-load account data to show new balance
        const updatedAccount = await customerApi.getMyAccount();
        setAccount(updatedAccount);
    } catch (err) {
        setError(err.message || "Payment processing failed.");
    } finally {
        setSubmitting(false);
    }
  }

  return (
    <Layout section="customer" title="Payments">
      <PageHeader
        title="Make a Payment"
        subtitle="Settle your outstanding balance or pay your minimum due amounts."
      />

      <div className="card border-0 shadow-sm mb-4">
        <div className="card-body p-4">
          <h5 className="mb-4">Quick Pay</h5>
          <form onSubmit={handlePayment} className="row g-3">
            <div className="col-md-6">
              <label className="form-label">Payment Amount ($)</label>
              <input
                type="number"
                className="form-control"
                value={paymentForm.amount}
                onChange={(e) => setPaymentForm({ ...paymentForm, amount: e.target.value })}
                placeholder="Enter amount (e.g. 10)"
                required
              />
            </div>
            <div className="col-md-6">
              <label className="form-label">Payment Method</label>
              <select
                className="form-select"
                value={paymentForm.paymentMethod}
                onChange={(e) => setPaymentForm({ ...paymentForm, paymentMethod: e.target.value })}
              >
                <option value="NETBANKING">Net Banking</option>
                <option value="UPI">UPI</option>
                <option value="CASH">Cash</option>
                <option value="CHEQUE">Cheque</option>
              </select>
            </div>
            <div className="col-12">
              <label className="form-label">Account Balance Info</label>
              <div className="p-3 bg-light rounded shadow-sm">
                <div className="row text-center">
                  <div className="col-6 border-end">
                    <div className="small text-muted mb-1">Total Credit Limit</div>
                    <div className="h5 mb-0 fw-bold text-dark">${account?.creditLimit || 0}</div>
                  </div>
                  <div className="col-6">
                    <div className="small text-muted mb-1">Available Balance</div>
                    <div className="h5 mb-0 fw-bold text-success">${account?.availableLimit || 0}</div>
                  </div>
                </div>
              </div>
            </div>
            <div className="col-12 text-end pt-2">
              <button className="btn btn-primary px-5" disabled={submitting || loading}>
                {submitting ? "Processing..." : "Pay Now"}
              </button>
            </div>
          </form>
          {message && <div className="alert alert-success mt-4 small">{message}</div>}
          {error && <div className="alert alert-danger mt-4 small">{error}</div>}
        </div>
      </div>

      <div className="card border-0 shadow-sm overflow-hidden">
        <div className="card-header bg-white border-0 py-3">
          <h6 className="mb-0 fw-bold">Recent Payments</h6>
        </div>
        <div className="card-body p-0">
          <DataTable
            columns={[
              { key: "paymentId", label: "Payment ID" },
              { key: "amount", label: "Amount", render: (row) => `$${Number(row.amount || 0).toLocaleString()}` },
              { key: "paymentDate", label: "Date" },
              { key: "status", label: "Status", type: "status" }
            ]}
            rows={rows}
            emptyMessage="No payment history found."
          />
        </div>
      </div>
    </Layout>
  );
}

export default CustomerPaymentsPage;
