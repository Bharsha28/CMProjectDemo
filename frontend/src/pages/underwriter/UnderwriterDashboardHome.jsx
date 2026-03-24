import Layout from "../../components/Layout";
import PageHeader from "../../components/PageHeader";
import StatCard from "../../components/StatCard";
import DataTable from "../../components/DataTable";
import { applications, underwritingHistory } from "../../data/mockData";

function UnderwriterDashboardHome() {
  const stats = [
    { title: "Waiting Cases", value: "08", icon: "bi-hourglass-split", accent: "warning" },
    { title: "Approved Today", value: "03", icon: "bi-check-circle", accent: "success" },
    { title: "Conditional", value: "02", icon: "bi-exclamation-circle", accent: "info" },
    { title: "Rejected", value: "01", icon: "bi-x-circle", accent: "danger" }
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
        <div className="col-lg-7">
          <div className="card border-0 shadow-sm">
            <div className="card-body">
              <h5 className="mb-3">Applications Waiting for Review</h5>
              <DataTable
                columns={[
                  { key: "customerName", label: "Customer" },
                  { key: "customerEmail", label: "Customer Email" },
                  { key: "requestedLimit", label: "Requested Limit" },
                  { key: "status", label: "Status", type: "status" }
                ]}
                rows={applications.filter((item) => item.status !== "Approved")}
              />
            </div>
          </div>
        </div>

        <div className="col-lg-5">
          <div className="card border-0 shadow-sm">
            <div className="card-body">
              <h5 className="mb-3">Recent Underwriting History</h5>
              <DataTable
                columns={[
                  { key: "customerEmail", label: "Customer Email" },
                  { key: "decision", label: "Decision", type: "status" },
                  { key: "approvedLimit", label: "Approved Limit" },
                  { key: "decisionDate", label: "Decision Date" }
                ]}
                rows={underwritingHistory}
              />
            </div>
          </div>
        </div>
      </div>
    </Layout>
  );
}

export default UnderwriterDashboardHome;
