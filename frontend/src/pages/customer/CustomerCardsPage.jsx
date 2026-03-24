import { useEffect, useState } from "react";
import Layout from "../../components/Layout";
import PageHeader from "../../components/PageHeader";
import DataTable from "../../components/DataTable";
import { accounts, cards, payments, statements } from "../../data/mockData";
import { customerApi } from "../../services/api";

function CustomerCardsPage() {
  const [cardRows, setCardRows] = useState(cards);
  const [account, setAccount] = useState(accounts[0] || null);
  const [statementRows, setStatementRows] = useState(statements);
  const [paymentRows, setPaymentRows] = useState(payments);

  useEffect(() => {
    async function loadCustomerCardData() {
      try {
        const [myCards, myAccount, myStatements, myPayments] = await Promise.all([
          customerApi.getMyCards(),
          customerApi.getMyAccount(),
          customerApi.getMyStatements(),
          customerApi.getMyPayments()
        ]);

        setCardRows(Array.isArray(myCards) ? myCards : cards);
        setAccount(myAccount || accounts[0] || null);
        setStatementRows(Array.isArray(myStatements) ? myStatements : statements);
        setPaymentRows(Array.isArray(myPayments) ? myPayments : payments);
      } catch (error) {
        setCardRows(cards);
        setAccount(accounts[0] || null);
        setStatementRows(statements);
        setPaymentRows(payments);
      }
    }

    loadCustomerCardData();
  }, []);

  const usedLimit = account ? Number(account.creditLimit || 0) - Number(account.availableLimit || 0) : 0;
  const openStatement = statementRows.find((item) => String(item.status).toUpperCase() === "OPEN");
  const latestPayment = paymentRows[0];

  return (
    <Layout section="customer" title="Customer Dashboard">
      <PageHeader
        title="My Cards And Balance"
        subtitle="View your issued cards, credit usage, current due amount and latest payment details."
      />

      <div className="row g-3 mb-4">
        <div className="col-md-3">
          <div className="card border-0 shadow-sm h-100">
            <div className="card-body">
              <div className="text-muted small mb-2">Credit Limit</div>
              <h4 className="mb-0">{account ? Number(account.creditLimit || 0).toLocaleString() : "0"}</h4>
            </div>
          </div>
        </div>
        <div className="col-md-3">
          <div className="card border-0 shadow-sm h-100">
            <div className="card-body">
              <div className="text-muted small mb-2">Available Limit</div>
              <h4 className="mb-0">{account ? Number(account.availableLimit || 0).toLocaleString() : "0"}</h4>
            </div>
          </div>
        </div>
        <div className="col-md-3">
          <div className="card border-0 shadow-sm h-100">
            <div className="card-body">
              <div className="text-muted small mb-2">Used Balance</div>
              <h4 className="mb-0">{usedLimit.toLocaleString()}</h4>
            </div>
          </div>
        </div>
        <div className="col-md-3">
          <div className="card border-0 shadow-sm h-100">
            <div className="card-body">
              <div className="text-muted small mb-2">Current Due</div>
              <h4 className="mb-0">{Number(openStatement?.totalDue || 0).toLocaleString()}</h4>
            </div>
          </div>
        </div>
      </div>

      <div className="card border-0 shadow-sm mb-4">
        <div className="card-body">
          <h5 className="mb-3">My Cards</h5>
          <DataTable
            columns={[
              { key: "cardId", label: "Card ID" },
              { key: "maskedCardNumber", label: "Masked Card Number" },
              { key: "expiryDate", label: "Expiry Date" },
              { key: "status", label: "Status", type: "status" }
            ]}
            rows={cardRows}
            emptyMessage="No cards found for this customer."
          />
        </div>
      </div>

      <div className="card border-0 shadow-sm">
        <div className="card-body">
          <h5 className="mb-3">Account Snapshot</h5>
          <div className="row g-3">
            <div className="col-md-3">
              <div className="border rounded p-3 bg-light">
                <div className="text-muted small">Account ID</div>
                <div className="fw-semibold">{account?.accountId || "-"}</div>
              </div>
            </div>
            <div className="col-md-3">
              <div className="border rounded p-3 bg-light">
                <div className="text-muted small">Status</div>
                <div className="fw-semibold">{account?.status || "-"}</div>
              </div>
            </div>
            <div className="col-md-3">
              <div className="border rounded p-3 bg-light">
                <div className="text-muted small">Minimum Due</div>
                <div className="fw-semibold">{Number(openStatement?.minimumDue || 0).toLocaleString()}</div>
              </div>
            </div>
            <div className="col-md-3">
              <div className="border rounded p-3 bg-light">
                <div className="text-muted small">Last Payment</div>
                <div className="fw-semibold">{Number(latestPayment?.amount || 0).toLocaleString()}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </Layout>
  );
}

export default CustomerCardsPage;
