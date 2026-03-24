import Layout from "../../components/Layout";
import PageHeader from "../../components/PageHeader";
import DataTable from "../../components/DataTable";
import { auditLogs } from "../../data/mockData";

function AuditLogPage() {
  return (
    <Layout section="admin" title="Admin Dashboard">
      <PageHeader
        title="Audit Log Page"
        subtitle="Review actions performed across user, application and operations modules."
      />

      <div className="card border-0 shadow-sm">
        <div className="card-body">
          <DataTable
            columns={[
              { key: "userEmail", label: "User Email" },
              { key: "action", label: "Action" },
              { key: "resource", label: "Resource" },
              { key: "timestamp", label: "Timestamp" }
            ]}
            rows={auditLogs}
          />
        </div>
      </div>
    </Layout>
  );
}

export default AuditLogPage;
