import Layout from "../../components/Layout";
import PageHeader from "../../components/PageHeader";
import DataTable from "../../components/DataTable";
import { useState, useEffect } from "react";
import { adminApi } from "../../services/api";

function AuditLogPage() {
  const [rows, setRows] = useState([]);

  useEffect(() => {
    async function loadData() {
      try {
        const data = await adminApi.getAuditLogs();
        setRows(data);
      } catch (e) {
        console.error(e);
      }
    }
    loadData();
  }, []);

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
            rows={rows}
          />
        </div>
      </div>
    </Layout>
  );
}

export default AuditLogPage;
