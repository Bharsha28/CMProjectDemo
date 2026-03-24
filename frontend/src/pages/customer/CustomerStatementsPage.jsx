import { useEffect, useState } from "react";
import Layout from "../../components/Layout";
import PageHeader from "../../components/PageHeader";
import DataTable from "../../components/DataTable";
import { customerApi } from "../../services/api";

function CustomerStatementsPage() {
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [fromDate, setFromDate] = useState("");
  const [toDate, setToDate] = useState("");

  async function loadStatements() {
    setLoading(true);
    try {
      const response = await customerApi.getMyStatements(fromDate, toDate);
      setRows(Array.isArray(response) ? response : []);
    } catch (err) {
      setError("Failed to load your statements from the server.");
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    loadStatements();
  }, []);

  return (
    <Layout section="customer" title="My Statements">
      <PageHeader
        title="Your Billing Statements"
        subtitle="Review your monthly statements, track balance due, and monitor payment status."
      />

      <div className="card border-0 shadow-sm overflow-hidden">
        <div className="card-header bg-white border-bottom py-3">
          <div className="row g-2 align-items-center">
            <div className="col-auto">
              <label className="form-label mb-0 small text-muted">From Date</label>
              <input type="date" className="form-control form-control-sm" value={fromDate} onChange={e => setFromDate(e.target.value)} />
            </div>
            <div className="col-auto">
              <label className="form-label mb-0 small text-muted">To Date</label>
              <input type="date" className="form-control form-control-sm" value={toDate} onChange={e => setToDate(e.target.value)} />
            </div>
            <div className="col-auto align-self-end">
              <button className="btn btn-sm btn-primary" onClick={loadStatements}>
                Filter
              </button>
            </div>
          </div>
        </div>
        <div className="card-body p-0">
          <DataTable
            columns={[
              { key: "statementId", label: "Statement ID" },
              { key: "periodStart", label: "Period Start" },
              { key: "periodEnd", label: "Period End" },
              { key: "totalDue", label: "Total Due", render: (val) => `$${Number(val).toLocaleString()}` },
              { key: "minimumDue", label: "Min Due", render: (val) => `$${Number(val).toLocaleString()}` },
              { key: "dueDate", label: "Due Date" },
              { key: "status", label: "Status", type: "status" },
              {
                key: "actions",
                label: "Actions",
                render: (row) => (
                  <button
                    className="btn btn-sm btn-primary px-3"
                    disabled={row.status !== "OPEN"}
                    onClick={() => {
                        window.location.href = "/customer/payments"; // Mock redirect to payments
                    }}
                  >
                    Pay Now
                  </button>
                )
              }
            ]}
            rows={rows}
            emptyMessage="No statements found for your account."
          />
        </div>
      </div>
      
      {error && <div className="alert alert-danger mt-4 mx-3">{error}</div>}
      
      {!loading && rows.length > 0 && (
        <div className="mt-4 p-3 bg-light rounded text-muted small">
          <i className="bi bi-info-circle me-2"></i>
          Payments made after the statement date will reflect in your next billing cycle.
        </div>
      )}
    </Layout>
  );
}

export default CustomerStatementsPage;
