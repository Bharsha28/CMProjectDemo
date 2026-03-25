import { useEffect, useMemo, useState } from "react";
import Layout from "../../components/Layout";
import PageHeader from "../../components/PageHeader";
import StatCard from "../../components/StatCard";
import DataTable from "../../components/DataTable";
import { customerApi } from "../../services/api";

function CustomerDashboardHome() {
  const [customer, setCustomer] = useState(null);
  const [cardRows, setCardRows] = useState([]);
  const [account, setAccount] = useState(null);
  const [transactionRows, setTransactionRows] = useState([]);
  const [statementRows, setStatementRows] = useState([]);
  const [paymentRows, setPaymentRows] = useState([]);
  const [hubTab, setHubTab] = useState("transactions");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function loadDashboard() {
      setLoading(true);
      try {
        const myCustomer = await customerApi.getMyCustomer();
        setCustomer(myCustomer);
        
        const [myCards, myAccount, myTransactions, myStatements, myPayments] = await Promise.all([
          customerApi.getMyCards(),
          customerApi.getMyAccount(),
          customerApi.getMyTransactions(),
          customerApi.getMyStatements(),
          customerApi.getMyPayments()
        ]);
        
        setCardRows(Array.isArray(myCards) ? myCards : []);
        setAccount(myAccount || null);
        setTransactionRows(Array.isArray(myTransactions) ? myTransactions : []);
        setStatementRows(Array.isArray(myStatements) ? myStatements : []);
        setPaymentRows(Array.isArray(myPayments) ? myPayments : []);
      } catch (error) {
        // Fallback partially handled in API service
      } finally {
        setLoading(false);
      }
    }
    loadDashboard();
  }, []);

  const openStatement = useMemo(
    () => statementRows.find((item) => String(item.status).toUpperCase() === "OPEN"),
    [statementRows]
  );
  const latestPayment = useMemo(() => paymentRows[0], [paymentRows]);
  const usedBalance = useMemo(
    () => Math.max(0, Number(account?.creditLimit || 0) - Number(account?.availableLimit || 0)),
    [account]
  );
  
  const stats = useMemo(
    () => [
      { label: "My Cards", value: String(cardRows.length).padStart(2, "0"), icon: "bi-credit-card-2-front" },
      { label: "Used Balance", value: usedBalance.toLocaleString(), icon: "bi-wallet2" },
      { label: "Current Due", value: Number(openStatement?.totalDue || 0).toLocaleString(), icon: "bi-receipt" },
      { label: "Last Payment", value: Number(latestPayment?.amount || 0).toLocaleString(), icon: "bi-check2-circle" }
    ],
    [cardRows.length, openStatement?.totalDue, usedBalance, latestPayment?.amount]
  );

  return (
    <Layout section="customer" title="Customer Dashboard">
      <PageHeader
        title={customer?.name ? `Welcome, ${customer.name}` : "Welcome Back"}
        subtitle="Manage your cards and track your finances from your personal hub."
      />

      <div className="row g-3 mb-4">
        {stats.map((item, index) => (
          <StatCard
            key={item.label}
            title={item.label}
            value={item.value}
            icon={item.icon}
            accent={["primary", "warning", "success", "info"][index]}
          />
        ))}
      </div>

      <div className="row g-4 mb-4">
        <div className="col-12">
          <div className="card border-0 shadow-sm overflow-hidden mb-4">
            <div className="card-header bg-white border-0 p-0">
              <div className="nav nav-tabs nav-fill">
                <button className={`nav-link border-0 py-3 ${hubTab === 'transactions' ? 'active fw-bold border-bottom border-primary' : 'text-muted'}`} onClick={() => setHubTab('transactions')}>
                  <i className="bi bi-arrow-left-right me-2"></i> Transactions
                </button>
                <button className={`nav-link border-0 py-3 ${hubTab === 'statements' ? 'active fw-bold border-bottom border-primary' : 'text-muted'}`} onClick={() => setHubTab('statements')}>
                  <i className="bi bi-file-earmark-text me-2"></i> Statements
                </button>
                <button className={`nav-link border-0 py-3 ${hubTab === 'payments' ? 'active fw-bold border-bottom border-primary' : 'text-muted'}`} onClick={() => setHubTab('payments')}>
                  <i className="bi bi-wallet2 me-2"></i> Payments
                </button>
              </div>
            </div>
            <div className="card-body p-0">
               {hubTab === 'transactions' && (
                 <div className="p-3">
                   <DataTable 
                    columns={[
                      { key: "transactionDate", label: "Date" },
                      { key: "merchant", label: "Merchant" },
                      { key: "amount", label: "Amount" },
                      { key: "currency", label: "Curr" },
                      { key: "status", label: "Status", type: "status" }
                    ]} 
                    rows={transactionRows} 
                    emptyMessage="No transactions found."
                  />
                 </div>
               )}
               {hubTab === 'statements' && (
                 <div className="p-3">
                   <DataTable 
                    columns={[
                      { key: "periodEnd", label: "Date" },
                      { key: "totalDue", label: "Due", render: (row) => `$${Number(row.totalDue || 0).toLocaleString()}` },
                      { key: "generatedDate", label: "Until" },
                      { key: "status", label: "Status", type: "status" }
                    ]} 
                    rows={statementRows} 
                    emptyMessage="No statements found."
                  />
                 </div>
               )}
               {hubTab === 'payments' && (
                 <div className="p-3">
                   <DataTable 
                    columns={[
                      { key: "paymentDate", label: "Date" },
                      { key: "amount", label: "Amount" },
                      { key: "status", label: "Status", type: "status" }
                    ]} 
                    rows={paymentRows} 
                    emptyMessage="No payments found."
                  />
                 </div>
               )}
            </div>
          </div>
        </div>

        <div className="col-12">
          <div className="card border-0 shadow-sm mb-4">
            <div className="card-body p-4">
              <h5 className="mb-4 d-flex justify-content-between align-items-center">
                Profile Summary
                <i className="bi bi-person-badge text-primary"></i>
              </h5>
              {customer ? (
                <div className="profile-details small">
                  <div className="mb-3">
                    <div className="text-muted">Status</div>
                    <div className="fw-bold"><span className="badge bg-success-subtle text-success">{customer.status}</span></div>
                  </div>
                  <div className="mb-3">
                    <div className="text-muted">Income</div>
                    <div className="fw-bold">${Number(customer.income).toLocaleString()} / year</div>
                  </div>
                  <div className="mb-3">
                    <div className="text-muted">Employment</div>
                    <div className="fw-bold">{customer.employmentType}</div>
                  </div>
                  <div className="mb-3">
                    <div className="text-muted">Contact Email</div>
                    <div className="fw-bold">{customer.contactInfo?.email}</div>
                  </div>
                  <div className="mb-0">
                    <div className="text-muted">Address</div>
                    <div className="fw-bold">{customer.contactInfo?.address}</div>
                  </div>
                </div>
              ) : (
                <div className="text-center py-4">
                  <p className="text-muted">No profile found. Please complete your request wizard.</p>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>

      <div className="row g-4">
        <div className="col-12">
          <div className="card border-0 shadow-sm">
            <div className="card-body p-4">
              <h5 className="mb-3">Current Active Account</h5>
              <div className="row g-3">
                <div className="col-md-3">
                  <div className="border rounded p-3 bg-light">
                    <div className="text-muted small">Account ID</div>
                    <div className="fw-semibold">{account?.accountId || "-"}</div>
                  </div>
                </div>
                <div className="col-md-3">
                  <div className="border rounded p-3 bg-light">
                    <div className="text-muted small">Available Limit</div>
                    <div className="fw-semibold text-success">${Number(account?.availableLimit || 0).toLocaleString()}</div>
                  </div>
                </div>
                <div className="col-md-3">
                  <div className="border rounded p-3 bg-light">
                    <div className="text-muted small">Total Credit Limit</div>
                    <div className="fw-semibold">${Number(account?.creditLimit || 0).toLocaleString()}</div>
                  </div>
                </div>
                <div className="col-md-3">
                  <div className="border rounded p-3 bg-light">
                    <div className="text-muted small">Account Status</div>
                    <div className="fw-semibold text-primary">{account?.status || "Active"}</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </Layout>
  );
}

export default CustomerDashboardHome;
