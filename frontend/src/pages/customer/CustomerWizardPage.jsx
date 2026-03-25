import { useEffect, useState, useMemo } from "react";
import Layout from "../../components/Layout";
import PageHeader from "../../components/PageHeader";
import DataTable from "../../components/DataTable";
import { customerApi } from "../../services/api";
import { getStoredSession } from "../../utils/auth";

const initialCustomerForm = {
  name: "",
  dob: "",
  address: "",
  email: "",
  phone: "",
  income: "",
  employmentType: "Salaried",
  status: "Active"
};

const initialAppForm = {
  productId: "",
  requestedLimit: "",
  documentType: "IdentityProof",
  file: null
};

function CustomerWizardPage() {
  const [step, setStep] = useState(1);
  const [customerForm, setCustomerForm] = useState(initialCustomerForm);
  const [appForm, setAppForm] = useState(initialAppForm);
  const [customer, setCustomer] = useState(null);
  const [products, setProducts] = useState([]);
  const [applications, setApplications] = useState([]);
  const [loading, setLoading] = useState(false);
  const [pageLoading, setPageLoading] = useState(true);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  useEffect(() => {
    const sessionEmail = getStoredSession()?.email || "";
    if (sessionEmail) {
      setCustomerForm((current) => ({ ...current, email: sessionEmail }));
    }

    async function loadInitialData() {
      setPageLoading(true);
      try {
        const [myCustomer, productResponse, myApps] = await Promise.all([
          customerApi.getMyCustomer().catch(() => null),
          customerApi.getProductsStrict().catch(() => []),
          customerApi.getMyApplications().catch(() => [])
        ]);

        if (myCustomer?.customerId) {
          setCustomer(myCustomer);
          setCustomerForm({
            name: myCustomer.name || "",
            dob: myCustomer.dob || "",
            address: myCustomer.contactInfo?.address || "",
            email: myCustomer.contactInfo?.email || sessionEmail,
            phone: myCustomer.contactInfo?.phone || "",
            income: myCustomer.income || "",
            employmentType: myCustomer.employmentType || "Salaried",
            status: myCustomer.status || "Active"
          });
          setStep(2);

          // Fallback: If myApps is empty, retry with customerId
          if (!myApps || myApps.length === 0) {
            try {
              const appsById = await customerApi.getApplicationsByCustomer(myCustomer.customerId);
              if (appsById && appsById.length > 0) {
                setApplications(appsById);
              } else {
                setApplications([]);
              }
            } catch (err) {
              setApplications([]);
            }
          } else {
            setApplications(myApps);
          }
        } else {
          setApplications(myApps || []);
        }

        setProducts(productResponse || []);

        if (productResponse?.length > 0) {
          setAppForm(curr => ({ ...curr, productId: String(productResponse[0].productId) }));
        }
      } catch (err) {
        if (err.message?.includes("400") || err.message?.includes("reflection")) {
          setError("Backend synchronization error: Please RESTART your Spring Boot application to apply latest fixes.");
        } else {
          setError("Failed to synchronize with backend. Please check your connection.");
        }
      } finally {
        setPageLoading(false);
      }
    }
    loadInitialData();
  }, []);

  function handleCustomerChange(e) {
    const { name, value } = e.target;
    setCustomerForm(curr => ({ ...curr, [name]: value }));
  }

  function handleAppChange(e) {
    const { name, value } = e.target;
    setAppForm(curr => ({ ...curr, [name]: value }));
  }

  function handleFileChange(e) {
    const file = e.target.files[0];
    if (!file) return;
    
    // Size check: 10MB = 10 * 1024 * 1024 bytes
    if (file.size > 10 * 1024 * 1024) {
      setError("File size exceeds 10MB limit.");
      e.target.value = ""; // clear input
      return;
    }
    // Type check (already restricted by accept but double check)
    if (file.type !== "application/pdf") {
      setError("Only PDF files are allowed.");
      e.target.value = "";
      return;
    }
    
    setError("");
    setAppForm(curr => ({ ...curr, file }));
  }

  async function handleCustomerSubmit(e) {
    e.preventDefault();
    setLoading(true);
    setError("");
    setMessage("");

    const payload = {
      customerId: customer?.customerId,
      name: customerForm.name,
      dob: customerForm.dob,
      contactInfo: {
        address: customerForm.address,
        email: customerForm.email,
        phone: customerForm.phone
      },
      income: Number(customerForm.income),
      employmentType: customerForm.employmentType,
      status: customerForm.status
    };

    try {
      const response = customer?.customerId 
        ? await customerApi.updateMyCustomer(payload)
        : await customerApi.createCustomer(payload);
        
      const savedCustomer = response?.data || response;
      setCustomer(savedCustomer);
      setMessage(customer?.customerId ? "Profile updated successfully!" : "Profile created! Proceed to card selection.");
      setStep(2);
    } catch (err) {
      setError(err.message || "Failed to save profile. Please check if your phone/email is unique.");
    } finally {
      setLoading(false);
    }
  }

  async function handleAppSubmit(e) {
    e.preventDefault();
    setLoading(true);
    setError("");
    setMessage("");

    const selectedProduct = products.find(p => String(p.productId) === String(appForm.productId));
    const requested = Number(appForm.requestedLimit);
    
    if (requested > 100000) {
      setError("Requested Limit should not be greater than $100,000");
      setLoading(false);
      return;
    }

    if (selectedProduct && requested > selectedProduct.maxCreditLimit) {
      setError(`Requested Limit should not be greater than card limit ($${selectedProduct.maxCreditLimit.toLocaleString()})`);
      setLoading(false);
      return;
    }

    const payload = {
      customerId: customer?.customerId,
      productId: Number(appForm.productId),
      requestedLimit: requested,
      applicationDate: new Date().toISOString().slice(0, 10),
      status: "Submitted"
    };

    try {
      const appResponse = await customerApi.createApplication(payload);
      const createdApp = appResponse?.data || appResponse;
      
      if (appForm.file) {
        await customerApi.uploadDocumentFile(
          createdApp.applicationId,
          appForm.documentType,
          appForm.file
        );
      } else {
        // Fallback for metadata-only if no file selected (though required in UI)
        await customerApi.uploadDocument({
          applicationId: createdApp.applicationId,
          documentType: appForm.documentType,
          fileURI: "no_file_uploaded",
          status: "Submitted"
        });
      }

      setMessage("Card application submitted! We are reviewing your request based on your verified profile.");
      setAppForm(initialAppForm);
      customerApi.getMyApplications().then(setApplications).catch(() => {});
    } catch (err) {
      setError(err.message || "Application submission failed.");
    } finally {
      setLoading(false);
    }
  }

  if (pageLoading) {
    return (
      <Layout section="customer" title="New Request">
        <div className="p-5 text-center">
          <div className="spinner-border text-primary" role="status">
            <span className="visually-hidden">Loading...</span>
          </div>
          <p className="mt-3 text-muted">Synchronizing with backend...</p>
        </div>
      </Layout>
    );
  }

  return (
    <Layout section="customer" title="Request Center & Wizard">
      <PageHeader
        title={customer?.customerId ? "Manage Your Application" : "Start New Request"}
        subtitle="Complete your profile and choose the best card for your needs."
      />

      <div className="row g-4">
        <div className="col-lg-8">
          <div className="card border-0 shadow-sm mb-4">
            <div className="card-body p-4">
              <h5 className="mb-4 d-flex align-items-center">
                <span className={`badge ${step === 1 ? 'bg-primary' : 'bg-success'} me-2`}>{step === 1 ? "1" : "2"}</span>
                {step === 1 ? (customer?.customerId ? "Update Customer Profile" : "Register Customer Profile") : "Choose Card Product"}
              </h5>
              
              <form onSubmit={step === 1 ? handleCustomerSubmit : handleAppSubmit} className="row g-3">
                {step === 1 ? (
                  <>
                    <div className="col-md-6">
                      <label className="form-label">Full Name</label>
                      <input className="form-control" name="name" value={customerForm.name} onChange={handleCustomerChange} required />
                    </div>
                    <div className="col-md-6">
                      <label className="form-label">DOB</label>
                      <input type="date" className="form-control" name="dob" value={customerForm.dob} onChange={handleCustomerChange} required />
                    </div>
                    <div className="col-md-6">
                      <label className="form-label">Email (Account)</label>
                      <input className="form-control bg-light" value={customerForm.email} readOnly />
                    </div>
                    <div className="col-md-6">
                      <label className="form-label">Phone</label>
                      <input className="form-control" name="phone" value={customerForm.phone} onChange={handleCustomerChange} required maxLength="10" />
                    </div>
                    <div className="col-md-6">
                      <label className="form-label">Employment</label>
                      <select className="form-select" name="employmentType" value={customerForm.employmentType} onChange={handleCustomerChange}>
                        <option>Salaried</option>
                        <option>SelfEmployed</option>
                        <option>Student</option>
                        <option>Retired</option>
                        <option>Unemployed</option>
                      </select>
                    </div>
                    <div className="col-md-6">
                      <label className="form-label">Annual Income ($)</label>
                      <input type="number" className="form-control" name="income" value={customerForm.income} onChange={handleCustomerChange} required />
                    </div>
                    <div className="col-12">
                      <label className="form-label">Address</label>
                      <textarea className="form-control" name="address" rows="2" value={customerForm.address} onChange={handleCustomerChange} required />
                    </div>
                    <div className="col-12">
                      <button className="btn btn-primary auth-submit-btn-minimized px-4 w-auto" disabled={loading}>
                        {loading ? "Saving..." : (customer?.customerId ? "Update & Proceed" : "Register & Continue")}
                      </button>
                    </div>
                  </>
                ) : (
                  <>
                    <div className="col-md-6">
                      <label className="form-label">Product</label>
                      <select className="form-select" name="productId" value={appForm.productId} onChange={handleAppChange} required>
                        {products.map(p => (
                          <option key={p.productId} value={p.productId}>{p.name} - {p.category}</option>
                        ))}
                      </select>
                    </div>
                    <div className="col-md-6">
                      <label className="form-label">Requested Limit</label>
                      <input type="number" className="form-control" name="requestedLimit" value={appForm.requestedLimit} onChange={handleAppChange} required />
                    </div>
                    <div className="col-md-6">
                      <label className="form-label">Document</label>
                      <select className="form-select" name="documentType" value={appForm.documentType} onChange={handleAppChange}>
                        <option>IdentityProof</option>
                        <option>AddressProof</option>
                        <option>IncomeProof</option>
                      </select>
                    </div>
                    <div className="col-md-6">
                      <label className="form-label">File attachment (PDF only, max 10MB)</label>
                      <input type="file" className="form-control" name="file" accept=".pdf" onChange={handleFileChange} required />
                    </div>
                    <div className="col-12 mt-3 d-flex justify-content-end align-items-center gap-2">
                      <button type="button" className="btn btn-link link-light me-auto" onClick={() => setStep(1)} disabled={loading}>Edit Profile</button>
                      <button className="btn btn-primary auth-submit-btn-minimized px-4 w-auto" disabled={loading}>
                        {loading ? "Submitting..." : "Submit"}
                      </button>
                    </div>
                  </>
                )}
              </form>
              {message && <div className="alert alert-success mt-4">{message}</div>}
              {error && <div className="alert alert-danger mt-4">{error}</div>}
            </div>
          </div>

          {customer?.customerId && (
            <div className="card border-0 shadow-sm overflow-hidden">
              <div className="card-header bg-white border-0 py-3">
                <h6 className="mb-0 fw-bold">My Application Status</h6>
              </div>
              <div className="card-body p-0">
                <DataTable
                  columns={[
                    { key: "applicationId", label: "Application Id" },
                    { key: "productName", label: "Product" },
                    { key: "requestedLimit", label: "Limit" },
                    { key: "status", label: "Status", type: "status" }
                  ]}
                  rows={applications.slice(0, 5)}
                  emptyMessage="No applications found."
                />
              </div>
            </div>
          )}
        </div>

        <div className="col-lg-4">
          <div className="card bg-dark text-white border-0 shadow-sm mb-4">
            <div className="card-body p-4">
              <h5 className="mb-4">Verification Audit</h5>
              <div className="d-flex gap-3 mb-4">
                <div className="rounded-circle bg-white text-dark d-flex align-items-center justify-content-center fw-bold" style={{ width: 32, height: 32 }}>1</div>
                <div>
                  <h6 className="mb-1">Identity & Income</h6>
                  <p className="small opacity-75">Customer Profile details are saved to the primary database.</p>
                </div>
              </div>
              <div className="d-flex gap-3">
                <div className={`rounded-circle ${step === 2 ? 'bg-primary text-white fw-bold' : 'border border-white text-white'} d-flex align-items-center justify-content-center`} style={{ width: 32, height: 32 }}>2</div>
                <div>
                  <h6 className="mb-1">Card Assignment</h6>
                  <p className="small opacity-75">Specific limits and product types are mapped to your request.</p>
                </div>
              </div>
            </div>
          </div>
          
          {customer?.customerId && (
            <div className="card border-0 shadow-sm">
              <div className="card-body p-4">
                <h6 className="fw-bold mb-3 text-primary">Customer Details</h6>
                <div className="small">
                  <div className="d-flex justify-content-between mb-2">
                    <span className="text-muted">CID:</span>
                    <span className="fw-semibold">#{customer.customerId}</span>
                  </div>
                  <div className="d-flex justify-content-between mb-2">
                    <span className="text-muted">Name:</span>
                    <span className="fw-semibold">{customer.name}</span>
                  </div>
                  <div className="d-flex justify-content-between mb-2">
                    <span className="text-muted">Income:</span>
                    <span className="fw-semibold text-success">${Number(customer.income).toLocaleString()}</span>
                  </div>
                  <div className="d-flex justify-content-between">
                    <span className="text-muted">Status:</span>
                    <span className="badge bg-info-subtle text-info">{customer.status}</span>
                  </div>
                </div>
              </div>
            </div>
          )}
        </div>
      </div>
    </Layout>
  );
}

export default CustomerWizardPage;
