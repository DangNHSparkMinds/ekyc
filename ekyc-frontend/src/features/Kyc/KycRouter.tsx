import { Route, Routes } from "react-router-dom";
import FaceVerification from "./FaceVerification";
import IdentityVerification from "./IdentityVerification";
import KycDetails from "./KycDetails";
import { useEffect } from "react";

const KycRouter: React.FunctionComponent = () => {

  useEffect(() => {
  }, []);

  return (
    <Routes>
      <Route path="/face" element={<FaceVerification />} />
      <Route path="/document" element={<IdentityVerification />} />
      <Route path="/result" element={<KycDetails />} />
    </Routes>
  );
}

export default KycRouter;