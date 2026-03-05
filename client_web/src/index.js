import React from "react";
import ReactDOM from "react-dom/client";
import "./index.css";
import App from "./App";
import { StatsProvider } from "./providers/StatsProvider";
import reportWebVitals from "./reportWebVitals";

const root = ReactDOM.createRoot(document.getElementById("root"));
root.render(
  <React.StrictMode>
    <StatsProvider refreshMs={5000}>
      <App />
    </StatsProvider>
  </React.StrictMode>
);

reportWebVitals();
