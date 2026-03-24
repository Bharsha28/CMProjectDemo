import Layout from "../../components/Layout";
import PageHeader from "../../components/PageHeader";
import DataTable from "../../components/DataTable";
import { applications } from "../../data/mockData";

function UnderwriterApplicationListPage() {
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
            rows={applications}
          />
        </div>
      </div>
    </Layout>
  );
}

export default UnderwriterApplicationListPage;
