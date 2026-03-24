import Layout from "../../components/Layout";
import PageHeader from "../../components/PageHeader";
import DataTable from "../../components/DataTable";
import { useState, useEffect } from "react";
import { underwriterApi } from "../../services/api";

function CreditScorePage() {
  const [rows, setRows] = useState([]);
  const [apps, setApps] = useState([]);
  const [formData, setFormData] = useState({ applicationId: "", bureauScore: "" });
  const [loading, setLoading] = useState(false);
  const [calculationResult, setCalculationResult] = useState(null);

  useEffect(() => {
    underwriterApi.getCreditScores().then(setRows).catch(console.error);
    underwriterApi.getApplications().then(setApps).catch(console.error);
  }, []);

  const handleCalculate = async (e) => {
    e.preventDefault();
    setLoading(true);
    setCalculationResult(null);
    const app = apps.find(a => String(a.applicationId) === formData.applicationId);
    if (!app) return;

    const bureau = Number(formData.bureauScore);
    const internal = Math.round(bureau * 0.95);
    
    // Auto-analysis
    let riskBand = "MEDIUM";
    if (bureau >= 750) riskBand = "LOW";
    if (bureau < 600) riskBand = "HIGH";

    try {
      await underwriterApi.createCreditScore({
        applicationId: app.applicationId,
        bureauScore: bureau,
        internalScore: internal,
        riskBand: riskBand
      });
      // Set result for display
      setCalculationResult({
        customerName: app.customerName || "Customer",
        bureauScore: bureau,
        internalScore: internal
      });

      // Refresh table (backend handles 5 records limit, but we also slice for safety)
      const updatedScores = await underwriterApi.getCreditScores();
      setRows(updatedScores);
      setFormData({ applicationId: "", bureauScore: "" });
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Layout section="underwriter" title="Underwriter Dashboard">
      <PageHeader
        title="Credit Score"
        subtitle="Generate internal scores and suggested limits based on bureau data."
      />

      <div className="row g-4">
        {/* Input Form */}
        <div className="col-12">
          <div className="card border-0 shadow-sm mb-4">
            <div className="card-body">
              <h5 className="mb-3">Calculate New Score</h5>
              <form onSubmit={handleCalculate} className="row g-3 align-items-end">
                <div className="col-md-5">
                  <label className="form-label">Application</label>
                  <select 
                    className="form-select" 
                    value={formData.applicationId}
                    onChange={e => setFormData({...formData, applicationId: e.target.value})}
                    required
                  >
                    <option value="">Select Application...</option>
                    {apps.map(a => (
                      <option key={a.applicationId} value={a.applicationId}>
                        {a.applicationId} - {a.customerName || "Customer"} (Req: {a.requestedLimit})
                      </option>
                    ))}
                  </select>
                </div>
                <div className="col-md-4">
                  <label className="form-label">Bureau Score</label>
                  <input 
                    type="number" 
                    className="form-control" 
                    placeholder="Enter Score (300-900)"
                    value={formData.bureauScore}
                    onChange={e => setFormData({...formData, bureauScore: e.target.value})}
                    min="300" max="900" 
                    required 
                  />
                </div>
                <div className="col-md-3">
                  <button className="btn btn-primary w-100" disabled={loading}>
                    {loading ? "Generating..." : "Generate"}
                  </button>
                </div>
              </form>

              {/* Generated Result */}
              {calculationResult && (
                <div className="mt-4 p-3 bg-success bg-opacity-10 rounded border border-success border-opacity-25">
                  <h6 className="text-success mb-2">Calculation Result:</h6>
                  <div className="row">
                    <div className="col-md-4">
                      <strong>Customer:</strong> {calculationResult.customerName}
                    </div>
                    <div className="col-md-4">
                      <strong>Bureau Score:</strong> {calculationResult.bureauScore}
                    </div>
                    <div className="col-md-4">
                      <strong>Internal Score:</strong> {calculationResult.internalScore}
                    </div>
                  </div>
                </div>
              )}
            </div>
          </div>
        </div>

        {/* Scores Table */}
        <div className="col-12">
          <div className="card border-0 shadow-sm">
            <div className="card-body">
              <h5 className="mb-3">Recent Scores</h5>
              <DataTable
                columns={[
                  { key: "customerName", label: "Customer" },
                  { key: "bureauScore", label: "Bureau Score" },
                  { key: "internalScore", label: "Internal Score" }
                ]}
                rows={rows.slice(0, 5)}
              />
            </div>
          </div>
        </div>
      </div>
    </Layout>
  );
}

export default CreditScorePage;
