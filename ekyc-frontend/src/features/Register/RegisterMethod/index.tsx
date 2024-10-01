import { Icon } from "@iconify/react/dist/iconify.js";
import { Button } from "antd";
import { useNavigate } from "react-router-dom";

const RegisterMethod: React.FunctionComponent = () => {
  const navigate = useNavigate();

  return (
    <div className="text-center">
      <p>Choosing the register method</p>
      <div>
        <Button className="method-btn">
          <span>Phone Number</span>
          <Icon icon="formkit:arrowright" />
        </Button>
        <Button className="method-btn" onClick={() => navigate("/register/contact-info")}>
          <span>Email</span>
          <Icon icon="formkit:arrowright" />
        </Button>
      </div>
    </div>
  );
}

export default RegisterMethod;