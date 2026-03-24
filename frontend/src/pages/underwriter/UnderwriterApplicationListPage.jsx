import Layout from "../../components/Layout";
import PageHeader from "../../components/PageHeader";
import DataTable from "../../components/DataTable";
import { useState, useEffect } from "react";
import { underwriterApi } from "../../services/api";

import { Link } from "react-router-dom";

function UnderwriterApplicationListPage() {
  const [rows, setRows] = useState([]);

  useEffect(() => {
    underwriterApi.getApplications().then(setRows).catch(console.error);
  }, []);

  return (
    <Layout section="underwriter" title="Underwriter Dashboard">
      <PageHeader
        title="Application List"
        subtitle="Review all applications waiting for underwriting action."
      />

      <div className="card border-0 shadow-sm col-12">
        <div className="card-body">
          <DataTable
            columns={[
              { key: "applicationId", label: "App ID" },
              { key: "customerName", label: "Customer Name" },
              { key: "productName", label: "Product" },
              { key: "requestedLimit", label: "Requested Limit" },
              { key: "applicationDate", label: "Date" },
              { key: "status", label: "Status", type: "status" },
              {
                key: "action",
                label: "Action",
                render: (row) => (
                  <Link
                    to={`/underwriter/decisions?appId=${row.applicationId}`}
                    className="btn btn-sm btn-primary"
                  >
                    Review
                  </Link>
                )
              }
            ]}
            rows={rows}
          />
        </div>
      </div>
    </Layout>
  );
}

export default UnderwriterApplicationListPage;
