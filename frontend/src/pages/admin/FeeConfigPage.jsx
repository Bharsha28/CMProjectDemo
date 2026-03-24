import { useState } from "react";
import Layout from "../../components/Layout";
import PageHeader from "../../components/PageHeader";
import DataTable from "../../components/DataTable";
import { useEffect } from "react";
import { adminApi } from "../../services/api";

const initialForm = {
  productId: "",
  feeType: "ISSUANCE",
  amount: ""
};

function FeeConfigPage() {
  const [formData, setFormData] = useState(initialForm);
  const [products, setProducts] = useState([]);
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function loadData() {
      try {
        const [loadedProducts, loadedFees] = await Promise.all([
          adminApi.getProducts(),
          adminApi.getFees()
        ]);
        setProducts(loadedProducts);
        setRows(loadedFees);
        // Sync productId to the first real product's actual DB id
        if (loadedProducts.length > 0) {
          setFormData(f => ({ ...f, productId: String(loadedProducts[0].productId) }));
        }
      } catch (e) {
        console.error("Failed to fetch fee data");
      } finally {
        setLoading(false);
      }
    }
    loadData();
  }, []);

  function handleChange(event) {
    const { name, value } = event.target;
    setFormData((current) => ({ ...current, [name]: value }));
  }

  async function handleSubmit(event) {
    event.preventDefault();
    try {
      const payload = {
          productId: Number(formData.productId),
          feeType: formData.feeType,
          amount: Number(formData.amount)
      };
      const response = await adminApi.createFee(payload);
      // Refresh the fee list from backend after saving
      const refreshed = await adminApi.getFees();
      setRows(refreshed);
      setFormData(f => ({ ...initialForm, productId: formData.productId, feeType: formData.feeType }));
    } catch (e) {
      console.error("Save failed:", e);
      alert("Failed to save fee: " + (e.message || "Unknown error"));
    }
  }

  return (
    <Layout section="admin" title="Admin Dashboard">
      <PageHeader
        title="Fee Config Page"
        subtitle="Configure annual, late and overlimit charges for each card product."
      />

      <div className="row g-4">
        <div className="col-12">
          <div className="card border-0 shadow-sm">
            <div className="card-body p-4">
              <form onSubmit={handleSubmit} className="row g-3">
                <div className="col-md-6">
                  <label className="form-label">Card Product</label>
                  <select className="form-select" name="productId" value={formData.productId} onChange={handleChange}>
                    {products.map((product) => (
                      <option key={product.productId} value={product.productId}>
                        {product.name}
                      </option>
                    ))}
                  </select>
                </div>
                <div className="col-md-6">
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
                <div className="col-12 d-flex justify-content-end">
                  <button className="btn btn-primary px-4">Save</button>
                </div>
              </form>
            </div>
          </div>
        </div>

        <div className="col-12">
          <div className="card border-0 shadow-sm">
            <div className="card-body">
              <h5 className="mb-3">Retrieved Fee Configurations</h5>
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
