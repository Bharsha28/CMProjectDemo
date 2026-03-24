import { useState } from "react";
import Layout from "../../components/Layout";
import PageHeader from "../../components/PageHeader";
import DataTable from "../../components/DataTable";
import { cardProducts } from "../../data/mockData";

const initialForm = {
  name: "",
  category: "Standard",
  interestRate: "",
  annualFee: "",
  status: "ACTIVE"
};

function CardProductPage() {
  const [formData, setFormData] = useState(initialForm);
  const [rows, setRows] = useState(cardProducts);

  function handleChange(event) {
    const { name, value } = event.target;
    setFormData((current) => ({ ...current, [name]: value }));
  }

  function handleSubmit(event) {
    event.preventDefault();
    setRows((current) => [
      {
        productId: Date.now(),
        ...formData
      },
      ...current
    ]);
    setFormData(initialForm);
  }

  return (
    <Layout section="admin" title="Admin Dashboard">
      <PageHeader
        title="Card Product Page"
        subtitle="Create card products and define category, interest rate and annual fee."
      />

      <div className="row g-4">
        <div className="col-lg-5">
          <div className="card border-0 shadow-sm">
            <div className="card-body p-4">
              <form onSubmit={handleSubmit} className="row g-3">
                <div className="col-12">
                  <label className="form-label">Product Name</label>
                  <input className="form-control" name="name" value={formData.name} onChange={handleChange} required />
                </div>
                <div className="col-md-6">
                  <label className="form-label">Category</label>
                  <select className="form-select" name="category" value={formData.category} onChange={handleChange}>
                    <option>Standard</option>
                    <option>Gold</option>
                    <option>Platinum</option>
                  </select>
                </div>
                <div className="col-md-6">
                  <label className="form-label">Status</label>
                  <select className="form-select" name="status" value={formData.status} onChange={handleChange}>
                    <option>ISSUE</option>
                    <option>ACTIVE</option>
                    <option>INACTIVE</option>
                    <option>BLOCKED</option>
                    <option>DISCONTINUED</option>
                  </select>
                </div>
                <div className="col-md-6">
                  <label className="form-label">Interest Rate</label>
                  <input type="number" step="0.01" className="form-control" name="interestRate" value={formData.interestRate} onChange={handleChange} required />
                </div>
                <div className="col-md-6">
                  <label className="form-label">Annual Fee</label>
                  <input type="number" className="form-control" name="annualFee" value={formData.annualFee} onChange={handleChange} required />
                </div>
                <div className="col-12">
                  <button className="btn btn-primary">Create Product</button>
                </div>
              </form>
            </div>
          </div>
        </div>

        <div className="col-lg-7">
          <div className="card border-0 shadow-sm">
            <div className="card-body">
              <DataTable
                columns={[
                  { key: "name", label: "Product Name" },
                  { key: "category", label: "Category" },
                  { key: "interestRate", label: "Interest Rate" },
                  { key: "annualFee", label: "Annual Fee" },
                  { key: "status", label: "Status", type: "status" }
                ]}
                rows={rows}
              />
            </div>
          </div>
        </div>
      </div>
    </Layout>
  );
}

export default CardProductPage;
