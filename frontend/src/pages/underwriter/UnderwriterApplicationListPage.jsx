import Layout from "../../components/Layout";
import PageHeader from "../../components/PageHeader";
import DataTable from "../../components/DataTable";
import { useState, useEffect } from "react";
import { underwriterApi } from "../../services/api";

function UnderwriterApplicationListPage() {
  const [rows, setRows] = useState([]);

  useEffect(() => {
    underwriterApi.getApplications().then(setRows).catch(console.error);
  }, []);

  return (
    <Layout section="underwriter" title="Underwriter Dashboard">
      <PageHeader
        title="Application List Page"
        subtitle="Review all applications waiting for underwriting action."
      />

      <div className="card border-0 shadow-sm">
        <div className="card-body">
          <DataTable
            columns={[
              { key: "customerName", label: "Customer Name" },
              { key: "customerEmail", label: "Customer Email" },
              { key: "productName", label: "Product" },
              { key: "requestedLimit", label: "Requested Limit" },
              { key: "applicationDate", label: "Date" },
              { key: "status", label: "Status", type: "status" }
            ]}
            rows={rows}
          />
        </div>
      </div>
    </Layout>
  );
}

export default UnderwriterApplicationListPage;
