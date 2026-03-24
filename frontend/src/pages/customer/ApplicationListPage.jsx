import { useEffect, useMemo, useState } from "react";
import Layout from "../../components/Layout";
import PageHeader from "../../components/PageHeader";
import DataTable from "../../components/DataTable";
import { applications } from "../../data/mockData";
import { customerApi } from "../../services/api";

function ApplicationListPage() {
  const [statusFilter, setStatusFilter] = useState("All");
  const [rows, setRows] = useState(applications);

  useEffect(() => {
    async function loadApplications() {
      try {
        const myCustomer = await customerApi.getMyCustomer();
        if (myCustomer?.customerId) {
          const apps = await customerApi.getApplicationsByCustomer(myCustomer.customerId);
          setRows(apps);
        }
      } catch (error) {
        setRows(applications);
      }
    }

    loadApplications();
  }, []);

  const filteredRows = useMemo(() => {
    if (statusFilter === "All") {
      return rows;
    }

    return rows.filter((application) => application.status === statusFilter);
  }, [rows, statusFilter]);

  return (
    <Layout section="customer" title="Customer Dashboard">
      <PageHeader
        title="Application List and View Page"
        subtitle="See all applications and quickly filter by submission status."
        action={
          <select
            className="form-select"
            value={statusFilter}
            onChange={(event) => setStatusFilter(event.target.value)}
          >
            <option>All</option>
            <option>Submitted</option>
            <option>UnderReview</option>
            <option>Approved</option>
            <option>Rejected</option>
          </select>
        }
      />

      <div className="card border-0 shadow-sm">
        <div className="card-body">
          <DataTable
            columns={[
              { key: "applicationId", label: "Application ID" },
              { key: "customerId", label: "Customer ID" },
              { key: "productId", label: "Product ID" },
              { key: "requestedLimit", label: "Requested Limit" },
              { key: "applicationDate", label: "Date" },
              { key: "status", label: "Status", type: "status" }
            ]}
            rows={filteredRows}
          />
        </div>
      </div>
    </Layout>
  );
}

export default ApplicationListPage;
