import { useState, useEffect } from "react";
import Layout from "../../components/Layout";
import PageHeader from "../../components/PageHeader";
import DataTable from "../../components/DataTable";
import { underwriterApi, adminApi } from "../../services/api";
import { useSearchParams } from "react-router-dom";

const initialForm = {
  applicationId: "",
  decision: "APPROVE",
  approvedLimit: ""
};

function UnderwritingDecisionPage() {
  const [searchParams] = useSearchParams();
  const [formData, setFormData] = useState(initialForm);
  const [history, setHistory] = useState([]);
  const [applicationList, setApplicationList] = useState([]);
  const [scores, setScores] = useState([]);
  const [products, setProducts] = useState([]);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    async function loadData() {
      try {
        const [hData, aData, sData, pData] = await Promise.all([
          underwriterApi.getUnderwritingHistory().catch(() => []),
          underwriterApi.getApplications().catch(() => []),
          underwriterApi.getCreditScores().catch(() => []),
          adminApi.getProducts().catch(() => [])
        ]);
        setHistory(hData || []);
        setApplicationList(aData || []);
        setScores(sData || []);
        setProducts(pData?.data || []); // unwrapResponse handle for adminApi might differ or be raw

        const queryAppId = searchParams.get("appId");
        if (queryAppId) {
          setFormData(f => ({ ...f, applicationId: queryAppId }));
          autoCalculate(queryAppId, "APPROVE", aData, sData, pData?.data || []);
        } else if (aData.length > 0) {
          const firstId = String(aData[0].applicationId);
          setFormData(f => ({ ...f, applicationId: firstId }));
          autoCalculate(firstId, "APPROVE", aData, sData, pData?.data || []);
        }
      } catch (e) { console.error(e); }
    }
    loadData();
  }, [searchParams]);

  function autoCalculate(appId, decisionType, aList, sList, pList) {
    const app = aList.find(a => String(a.applicationId) === appId);
    const score = sList.find(s => String(s.applicationId) === appId);
    const product = pList.find(p => p.name === app?.productName);
    
    if (!app) return;

    let limit = 0;
    let finalDecision = decisionType;

    if (score && product) {
      const scoreFactor = score.bureauScore / 900;
      const maxProductLimit = product.maxCreditLimit || 100000;
      const calculated = Math.round(maxProductLimit * scoreFactor);
      limit = Math.min(app.requestedLimit, calculated);

      // System suggestion if decisionType not explicitly clicked
      if (!decisionType) {
        if (score.bureauScore >= 750) finalDecision = "APPROVE";
        else if (score.bureauScore >= 650) finalDecision = "CONDITIONAL";
        else finalDecision = "REJECT";
      }
    } else {
      limit = app.requestedLimit;
    }

    if (finalDecision === "REJECT") limit = 0;
    if (finalDecision === "CONDITIONAL") limit = Math.round(limit / 2);

    setFormData(f => ({ 
      ...f, 
      applicationId: appId,
      decision: finalDecision, 
      approvedLimit: String(limit) 
    }));
  }

  async function handleSubmit(event) {
    if (event) event.preventDefault();
    setLoading(true);
    setError("");
    setMessage("");

    const payload = {
      decision: formData.decision,
      approvedLimit: Number(formData.approvedLimit)
    };

    try {
      await underwriterApi.createDecision(formData.applicationId, payload);
      const hData = await underwriterApi.getUnderwritingHistory();
      setHistory(hData || []);
      setMessage("Decision saved successfully.");
    } catch (submitError) {
      setError(submitError.message || "Unable to save the decision.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <Layout section="underwriter" title="Underwriter Dashboard">
      <PageHeader
        title="Underwriting Decision"
        subtitle="Finalize credit decisions and set credit limits."
      />

      <div className="row g-4">
        {/* Form Column */}
        <div className="col-12">
          <div className="card border-0 shadow-sm">
            <div className="card-body p-4">
              <div className="row g-3">
                <div className="col-md-6">
                  <label className="form-label">Select Application</label>
                  <select 
                    className="form-select" 
                    value={formData.applicationId} 
                    onChange={(e) => autoCalculate(e.target.value, "APPROVE", applicationList, scores, products)}
                  >
                    {applicationList.map((app) => (
                      <option key={app.applicationId} value={app.applicationId}>
                        {app.applicationId} - {app.customerName || "Customer"} (Requested: {app.requestedLimit})
                      </option>
                    ))}
                  </select>
                </div>

                <div className="col-md-6">
                  <label className="form-label">Approved Limit</label>
                  <input 
                    type="number" 
                    className="form-control" 
                    value={formData.approvedLimit} 
                    onChange={(e) => setFormData({...formData, approvedLimit: e.target.value})}
                  />
                </div>

                <div className="col-12">
                  <label className="form-label d-block">Set Decision</label>
                  <div className="btn-group w-100" role="group">
                    <button 
                      type="button" 
                      className={`btn ${formData.decision === 'APPROVE' ? 'btn-success' : 'btn-outline-success'}`}
                      onClick={() => autoCalculate(formData.applicationId, "APPROVE", applicationList, scores, products)}
                    >
                      Approve
                    </button>
                    <button 
                      type="button" 
                      className={`btn ${formData.decision === 'CONDITIONAL' ? 'btn-info text-white' : 'btn-outline-info'}`}
                      onClick={() => autoCalculate(formData.applicationId, "CONDITIONAL", applicationList, scores, products)}
                    >
                      Conditional
                    </button>
                    <button 
                      type="button" 
                      className={`btn ${formData.decision === 'REJECT' ? 'btn-danger' : 'btn-outline-danger'}`}
                      onClick={() => autoCalculate(formData.applicationId, "REJECT", applicationList, scores, products)}
                    >
                      Reject
                    </button>
                  </div>
                </div>

                <div className="col-12 d-flex justify-content-end mt-4">
                  <button className="btn btn-primary px-5" onClick={handleSubmit} disabled={loading}>
                    {loading ? "Saving..." : "Save"}
                  </button>
                </div>
              </div>
              {message && <div className="alert alert-success mt-3">{message}</div>}
              {error && <div className="alert alert-danger mt-3">{error}</div>}
            </div>
          </div>
        </div>

        {/* History Column */}
        <div className="col-12">
          <div className="card border-0 shadow-sm">
            <div className="card-body">
              <h5 className="mb-3">Decision History</h5>
              <DataTable
                columns={[
                  { key: "customerName", label: "Customer" },
                  { key: "decision", label: "Decision", type: "status" },
                  { key: "approvedLimit", label: "Limit" },
                  { key: "decisionDate", label: "Date" }
                ]}
                rows={history.slice(0, 5)}
              />
            </div>
          </div>
        </div>
      </div>
    </Layout>
  );
}

export default UnderwritingDecisionPage;
