import React, { useEffect, useState } from 'react';
import { fetchLogs } from '../services/logService';

function LogTable() {
  const [logs, setLogs] = useState([]);

  useEffect(() => {
    fetchLogs().then(setLogs);
  }, []);

  return (
    <div>
      <h2>Logs từ OpenSearch</h2>
      <table border="1">
        <thead>
          <tr>
            <th>Thời gian</th>
            <th>Hành động</th>
            <th>Thông điệp lỗi</th>
            <th>ID người dùng</th>
          </tr>
        </thead>
        <tbody>
          {logs.map((log, idx) => (
            <tr key={idx}>
              <td>{log['@timestamp']}</td>
              <td>{log.action}</td>
              <td>{log['data.error_message'] || '-'}</td>
              <td>{log['data.grantee_id'] || '-'}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default LogTable;
