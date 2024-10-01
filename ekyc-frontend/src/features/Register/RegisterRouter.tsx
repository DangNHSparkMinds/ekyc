import { Route, Routes } from "react-router-dom";
import RegisterMethod from "./RegisterMethod";
import RegisterContactInfo from "./RegisterContactInfo";
import RegisterOTPVerification from "./RegisterOTPVerification";
import RegisterPassword from "./RegisterPassword";

const RegisterRouter: React.FunctionComponent = () => {
  return (
    <div className="register-layout-wrapper">
      <div className="register-container">
        <div className="register-header">EWallet</div>
        <Routes>
          <Route path="" element={<RegisterMethod />} />
          <Route path="/contact-info" element={<RegisterContactInfo />} />
          <Route path="/otp-verification" element={<RegisterOTPVerification />} />
          <Route path="/password" element={<RegisterPassword />} />
        </Routes>
      </div>
    </div>
  );
}

export default RegisterRouter;
