import { useState } from "react";
import Layout from "../../components/Layout";
import PageHeader from "../../components/PageHeader";
import DataTable from "../../components/DataTable";
import { accounts, cards } from "../../data/mockData";
import { operationsApi } from "../../services/api";

const initialForm = {
  cardId: String(cards[0]?.cardId || "")
};

function CardAccountPage() {
  const [formData, setFormData] = useState(initialForm);
  const [rows, setRows] = useState(accounts);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  function handleChange(event) {
    const { name, value } = event.target;
    setFormData((current) => ({ ...current, [name]: value }));
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setLoading(true);
    setError("");
    setMessage("");
    const selectedCard = cards.find((card) => String(card.cardId) === formData.cardId);

    try {
      const response = await operationsApi.createAccount({
        cardId: Number(formData.cardId)
      });

      const newAccount = {
        accountId: response?.accountId || Date.now(),
        cardId: response?.cardId || Number(formData.cardId),
        customerEmail: selectedCard?.customerEmail || "",
        customerName: selectedCard?.customerName || "",
        creditLimit: response?.creditLimit || 0,
        availableLimit: response?.availableLimit || 0,
        openDate: response?.openDate || new Date().toISOString().slice(0, 10),
        status: response?.status || "ACTIVE"
      };

      setRows((current) => [newAccount, ...current]);
      setMessage("Card account created through the backend.");
      setFormData(initialForm);
    } catch (submitError) {
      setError(submitError.message || "Unable to create the card account.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <Layout section="operations" title="Operations Dashboard">
      <PageHeader
        title="Card Account Page"
        subtitle="Create a card account and track limit usage for issued cards."
      />

      <div className="row g-4">
        <div className="col-lg-5">
          <div className="card border-0 shadow-sm">
            <div className="card-body p-4">
              <form onSubmit={handleSubmit} className="row g-3">
                <div className="col-12">
                  <label className="form-label">Issued Card</label>
                  <select className="form-select" name="cardId" value={formData.cardId} onChange={handleChange} required>
                    {cards.map((card) => (
                      <option key={card.cardId} value={card.cardId}>
                        {card.customerEmail} - {card.maskedCardNumber}
                      </option>
                    ))}
                  </select>
                </div>
                <div className="col-12">
                  <button className="btn btn-primary" disabled={loading}>
                    {loading ? "Saving..." : "Create Card Account"}
                  </button>
                </div>
              </form>
              {message ? <div className="alert alert-success mt-4 mb-0">{message}</div> : null}
              {error ? <div className="alert alert-danger mt-4 mb-0">{error}</div> : null}
            </div>
          </div>
        </div>

        <div className="col-lg-7">
          <div className="card border-0 shadow-sm">
            <div className="card-body">
              <DataTable
                columns={[
                  { key: "customerName", label: "Customer" },
                  { key: "customerEmail", label: "Customer Email" },
                  { key: "openDate", label: "Open Date" },
                  { key: "creditLimit", label: "Credit Limit" },
                  { key: "availableLimit", label: "Available Limit" },
                  { key: "status", label: "Status", type: "status" }
                ]}
                rows={rows}
              />
            </div>
          </div>
        </div>
      </div>
    </Layout>
  );
}

export default CardAccountPage;
