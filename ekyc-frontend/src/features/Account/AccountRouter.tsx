import { Route, Routes } from "react-router-dom";
import Login from "./Login";

const AccountRouter: React.FunctionComponent = () => {
  return (
    <Routes>
      <Route path="" element={<Login />} />
    </Routes>
  );
}

export default AccountRouter;