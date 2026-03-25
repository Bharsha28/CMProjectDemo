import StatusBadge from "./StatusBadge";

function DataTable({ columns, rows, emptyMessage = "No records found." }) {
  return (
    <div className="table-responsive">
      <table className="table align-middle table-hover mb-0">
        <thead className="bg-white">
          <tr>
            {columns.map((column) => (
              <th key={column.key}>{column.label}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {rows.length > 0 ? (
            rows.map((row, rowIndex) => (
              <tr key={row.id || row.applicationId || row.userId || rowIndex}>
                {columns.map((column) => (
                  <td key={column.key}>
                    {column.type === "status" ? (
                      <StatusBadge value={row[column.key]} />
                    ) : column.render ? (
                      column.render(row)
                    ) : (
                      row[column.key]
                    )}
                  </td>
                ))}
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan={columns.length} className="text-center py-4 text-muted">
                {emptyMessage}
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
}

export default DataTable;
