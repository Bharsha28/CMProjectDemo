import Layout from "../../components/Layout";
import PageHeader from "../../components/PageHeader";
import DataTable from "../../components/DataTable";
import { creditScores } from "../../data/mockData";

function CreditScoreViewPage() {
  return (
    <Layout section="underwriter" title="Underwriter Dashboard">
      <PageHeader
        title="Credit Score View Page"
        subtitle="Compare bureau score and internal score before making a decision."
      />

      <div className="row g-4">
        <div className="col-lg-8">
          <div className="card border-0 shadow-sm">
            <div className="card-body">
              <DataTable
                columns={[
                  { key: "customerName", label: "Customer" },
                  { key: "customerEmail", label: "Customer Email" },
                  { key: "bureauScore", label: "Bureau Score" },
                  { key: "internalScore", label: "Internal Score" },
                  { key: "riskBand", label: "Risk Band" },
                  { key: "generatedDate", label: "Generated Date" }
                ]}
                rows={creditScores}
              />
            </div>
          </div>
        </div>

        <div className="col-lg-4">
          <div className="card border-0 shadow-sm h-100">
            <div className="card-body">
              <h5 className="mb-3">Simple Reading Guide</h5>
              <ul className="text-muted ps-3 mb-0">
                <li>750+ bureau score suggests strong repayment behavior.</li>
                <li>Internal score helps the bank include its own rules.</li>
                <li>Risk band can guide approve, reject or conditional review.</li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </Layout>
  );
}

export default CreditScoreViewPage;
