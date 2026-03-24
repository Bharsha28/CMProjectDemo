function StatCard({ title, value, icon, accent = "primary" }) {
  return (
    <div className="col-md-6 col-xl-3">
      <div className={`card border-0 shadow-sm stat-card stat-card-${accent}`}>
        <div className="card-body">
          <div className="d-flex justify-content-between align-items-start">
            <div>
              <p className="text-muted mb-2">{title}</p>
              <h3 className="mb-0">{value}</h3>
            </div>
            <div className="stat-icon">
              <i className={`bi ${icon}`}></i>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default StatCard;
