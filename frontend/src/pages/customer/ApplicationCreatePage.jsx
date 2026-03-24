import { useEffect, useMemo, useState } from "react";
import Layout from "../../components/Layout";
import PageHeader from "../../components/PageHeader";
import { customerApi } from "../../services/api";

const initialForm = {
  customerEmail: "",
  productId: "",
  requestedLimit: "",
  documentType: "IdentityProof",
  fileName: ""
};

function ApplicationCreatePage() {
  const [customer, setCustomer] = useState(null);
  const [products, setProducts] = useState([]);
  const [formData, setFormData] = useState(initialForm);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [pageLoading, setPageLoading] = useState(true);

  useEffect(() => {
    async function loadPageData() {
      setPageLoading(true);
      setError("");

      try {
        const [myCustomer, productResponse] = await Promise.all([
          customerApi.getMyCustomer(),
          customerApi.getProductsStrict()
        ]);

        const normalizedCustomer = {
          customerId: myCustomer.customerId,
          name: myCustomer.name,
          email: myCustomer.email || myCustomer.contactInfo?.email || "",
          phone: myCustomer.phone || myCustomer.contactInfo?.phone || "",
          address: myCustomer.address || myCustomer.contactInfo?.address || ""
        };

        const normalizedProducts = productResponse.map((product) => ({
          id: product.productId,
          name: product.name,
          category: product.category,
          status: product.status
        }));

        if (!normalizedCustomer.email || !normalizedCustomer.customerId) {
          throw new Error("Customer profile is incomplete. Please create your customer profile first.");
        }

        if (normalizedProducts.length === 0) {
          throw new Error("No card products are available in the backend. Please contact the admin team.");
        }

        setCustomer(normalizedCustomer);
        setProducts(normalizedProducts);
        setFormData((current) => ({
          ...current,
          customerEmail: normalizedCustomer.email,
          productId: current.productId || String(normalizedProducts[0].id)
        }));
      } catch (loadError) {
        setCustomer(null);
        setProducts([]);
        setError(loadError.message || "Unable to load customer profile and card products.");
      } finally {
        setPageLoading(false);
      }
    }

    loadPageData();
  }, []);

  const selectedProduct = useMemo(
    () => products.find((product) => String(product.id) === formData.productId),
    [products, formData.productId]
  );

  function handleChange(event) {
    const { name, value } = event.target;
    setFormData((current) => ({ ...current, [name]: value }));
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setLoading(true);
    setError("");
    setMessage("");

    const payload = {
      customerId: customer?.customerId,
      productId: Number(formData.productId),
      requestedLimit: Number(formData.requestedLimit),
      applicationDate: new Date().toISOString().slice(0, 10),
      status: "Submitted"
    };

    if (!customer?.customerId) {
      setError("Customer profile is missing. Please create your profile first.");
      setLoading(false);
      return;
    }

    if (!formData.productId) {
      setError("No backend product is available for application creation.");
      setLoading(false);
      return;
    }

    try {
      const applicationResponse = await customerApi.createApplication(payload);
      const createdApplication = applicationResponse?.data || applicationResponse;

      await customerApi.uploadDocument({
        applicationId: createdApplication.applicationId,
        documentType: formData.documentType,
        fileURI: formData.fileName,
        status: "Submitted"
      });

      setMessage(
        `Application and KYC document saved through the backend for ${formData.customerEmail}.`
      );
      setFormData({
        ...initialForm,
        customerEmail: customer.email,
        productId: products[0] ? String(products[0].id) : ""
      });
    } catch (submitError) {
      setError(submitError.message || "Application creation failed.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <Layout section="customer" title="Customer Dashboard">
      <PageHeader
        title="Application Create Page"
        subtitle="Submit a new card application and upload basic KYC document details."
      />

      <div className="card border-0 shadow-sm">
        <div className="card-body p-4">
          {pageLoading ? <div className="alert alert-info">Loading customer profile and backend products...</div> : null}
          <form onSubmit={handleSubmit} className="row g-3">
            <div className="col-md-6">
              <label className="form-label">Customer Email</label>
              <input
                className="form-control"
                name="customerEmail"
                value={formData.customerEmail}
                readOnly
                disabled
              />
            </div>
            <div className="col-md-6">
              <label className="form-label">Card Product</label>
              <select
                className="form-select"
                name="productId"
                value={formData.productId}
                onChange={handleChange}
                required
                disabled={pageLoading || products.length === 0}
              >
                {products.map((product) => (
                  <option key={product.id} value={product.id}>
                    {product.name} - {product.category}
                  </option>
                ))}
              </select>
              {selectedProduct ? (
                <div className="form-text">
                  Selected product status: {selectedProduct.status}
                </div>
              ) : null}
            </div>
            <div className="col-md-6">
              <label className="form-label">Requested Limit</label>
              <input type="number" className="form-control" name="requestedLimit" value={formData.requestedLimit} onChange={handleChange} required disabled={pageLoading || !customer || products.length === 0} />
            </div>
            <div className="col-md-3">
              <label className="form-label">KYC Document</label>
              <select className="form-select" name="documentType" value={formData.documentType} onChange={handleChange} disabled={pageLoading || !customer || products.length === 0}>
                <option>IdentityProof</option>
                <option>AddressProof</option>
                <option>IncomeProof</option>
                <option>EmploymentProof</option>
                <option>Other</option>
              </select>
            </div>
            <div className="col-md-3">
              <label className="form-label">File Name</label>
              <input className="form-control" name="fileName" value={formData.fileName} onChange={handleChange} placeholder="example.pdf" required disabled={pageLoading || !customer || products.length === 0} />
            </div>
            <div className="col-12">
              <button className="btn btn-primary" disabled={loading || pageLoading || !customer || products.length === 0}>
                {loading ? "Saving..." : "Submit Application"}
              </button>
            </div>
          </form>

          {message ? <div className="alert alert-success mt-4 mb-0">{message}</div> : null}
          {error ? <div className="alert alert-danger mt-4 mb-0">{error}</div> : null}
        </div>
      </div>
    </Layout>
  );
}

export default ApplicationCreatePage;
