import TextInputField from "@/components/FormItems/TextInputField";
import SubmitButton from "@/components/Forms/SubmitButton";
import VerticalForm from "@/components/Forms/VerticalForm";
import CustomModal from "@/components/Modals/CustomModal";
import useRegister from "../useRegister";
import { useForm } from "antd/lib/form/Form";
import { SubmitEmailRequest } from "@/models/auth";

const RegisterContactInfo: React.FunctionComponent = () => {
  const { message, register } = useRegister();
  const [form] = useForm();

  const handleSubmit = (data: SubmitEmailRequest) => {
    register(data);
  };

  return (
    <>
      {
        message && (<CustomModal message={message} />)
      }
      <div className="register-form">
        <VerticalForm name="personal-info" form={form} onFinish={handleSubmit}>
          <TextInputField name="email" placeholder="Email" className="input-password" />
          <SubmitButton buttonClassName="submit-btn" name="Continue" />
        </VerticalForm>
      </div>
    </>
  );
}

export default RegisterContactInfo;