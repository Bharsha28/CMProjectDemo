import Layout from "../../components/Layout";
import PageHeader from "../../components/PageHeader";
import StatCard from "../../components/StatCard";
import DataTable from "../../components/DataTable";
import { useState, useEffect } from "react";
import { underwriterApi } from "../../services/api";
function UnderwriterDashboardHome() {
  const [apps, setApps] = useState([]);
  const [history, setHistory] = useState([]);

  useEffect(() => {
    async function loadDashboard() {
      try {
        const [a, h] = await Promise.all([
          underwriterApi.getApplications().catch(() => []),
          underwriterApi.getUnderwritingHistory().catch(() => [])
        ]);
        setApps(a);
        setHistory(h);
      } catch (e) { console.error(e); }
    }
    loadDashboard();
  }, []);

  const stats = [
    { title: "Waiting Cases", value: apps.filter(a => a.status !== "Approved").length.toString().padStart(2, "0"), icon: "bi-hourglass-split", accent: "warning" },
    { title: "Approved Today", value: history.filter(h => h.decision === "APPROVE").length.toString().padStart(2, "0"), icon: "bi-check-circle", accent: "success" },
    { title: "Conditional", value: history.filter(h => h.decision === "CONDITIONAL").length.toString().padStart(2, "0"), icon: "bi-exclamation-circle", accent: "info" },
    { title: "Rejected", value: history.filter(h => h.decision === "REJECT").length.toString().padStart(2, "0"), icon: "bi-x-circle", accent: "danger" }
  ];

  return (
    <Layout section="underwriter" title="Underwriter Dashboard">
      <PageHeader
        title="Underwriter Dashboard Home"
        subtitle="Monitor application workload, decisions and underwriting performance."
      />

      <div className="row g-3 mb-4">
        {stats.map((item) => (
          <StatCard
            key={item.title}
            title={item.title}
            value={item.value}
            icon={item.icon}
            accent={item.accent}
          />
        ))}
      </div>

      <div className="row g-4">
        <div className="col-12">
          <div className="card border-0 shadow-sm">
            <div className="card-body">
              <h5 className="mb-3">Applications Waiting for Review</h5>
              <DataTable
                columns={[
                  { key: "applicationId", label: "App ID" },
                  { key: "customerName", label: "Customer" },
                  { key: "requestedLimit", label: "Requested Limit" },
                  { key: "status", label: "Status", type: "status" }
                ]}
                rows={apps.filter((item) => item.status !== "Approved")}
              />
            </div>
          </div>
        </div>
      </div>
    </Layout>
  );
}

export default UnderwriterDashboardHome;
