// StockComponent.js
import React, { useEffect, useState } from "react";

const StockComponent = () => {
  const [stockData, setStockData] = useState([]);

  useEffect(() => {
    const fetchStockData = async () => {
      const response = await fetch("http://localhost:8080/stock"); // Adjust URL as per your API
      const data = await response.json();
      setStockData(data);
    };
    
    fetchStockData();
  }, []);

  const sortedStockData = [...stockData].sort((a, b) => new Date(a.entry_date) - new Date(b.entry_date));

  return (
    <div>
      <h2>Stock Inventory</h2>
      <table>
        <thead>
          <tr>
            <th>Batch ID</th>
            <th>Quantity</th>
            <th>Entry Date</th>
            <th>Sale Price</th>
          </tr>
        </thead>
        <tbody>
          {sortedStockData.map((item) => (
            <tr key={item.batch_id}>
              <td>{item.batch_id}</td>
              <td>{item.quantity}</td>
              <td>{new Date(item.entry_date).toLocaleString()}</td>
              <td>{item.sale_price}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default StockComponent;
