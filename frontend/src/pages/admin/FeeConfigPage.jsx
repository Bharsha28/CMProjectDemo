import { useState } from "react";
import Layout from "../../components/Layout";
import PageHeader from "../../components/PageHeader";
import DataTable from "../../components/DataTable";
import { cardProducts, feeConfigs } from "../../data/mockData";

const initialForm = {
  productId: "1",
  feeType: "Late",
  amount: ""
};

function FeeConfigPage() {
  const [formData, setFormData] = useState(initialForm);
  const [rows, setRows] = useState(feeConfigs);

  function handleChange(event) {
    const { name, value } = event.target;
    setFormData((current) => ({ ...current, [name]: value }));
  }

  function handleSubmit(event) {
    event.preventDefault();
    const product = cardProducts.find((item) => String(item.productId) === formData.productId);

    setRows((current) => [
      {
        feeId: Date.now(),
        productId: Number(formData.productId),
        productName: product?.name || "Card Product",
        feeType: formData.feeType,
        amount: Number(formData.amount)
      },
      ...current
    ]);

    setFormData(initialForm);
  }

  return (
    <Layout section="admin" title="Admin Dashboard">
      <PageHeader
        title="Fee Config Page"
        subtitle="Configure annual, late and overlimit charges for each card product."
      />

      <div className="row g-4">
        <div className="col-lg-5">
          <div className="card border-0 shadow-sm">
            <div className="card-body p-4">
              <form onSubmit={handleSubmit} className="row g-3">
                <div className="col-12">
                  <label className="form-label">Card Product</label>
                  <select className="form-select" name="productId" value={formData.productId} onChange={handleChange}>
                    {cardProducts.map((product) => (
                      <option key={product.productId} value={product.productId}>
                        {product.name}
                      </option>
                    ))}
                  </select>
                </div>
                <div className="col-12">
                  <label className="form-label">Fee Type</label>
                  <select className="form-select" name="feeType" value={formData.feeType} onChange={handleChange}>
                    <option>ISSUANCE</option>
                    <option>ANNUAL</option>
                    <option>LATE_PAYMENT</option>
                    <option>OVERLIMIT</option>
                    <option>FX_MARKUP</option>
                  </select>
                </div>
                <div className="col-12">
                  <label className="form-label">Amount</label>
                  <input type="number" className="form-control" name="amount" value={formData.amount} onChange={handleChange} required />
                </div>
                <div className="col-12">
                  <button className="btn btn-primary">Save Fee</button>
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
                  { key: "productName", label: "Product" },
                  { key: "feeType", label: "Fee Type" },
                  { key: "amount", label: "Amount" }
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

export default FeeConfigPage;
