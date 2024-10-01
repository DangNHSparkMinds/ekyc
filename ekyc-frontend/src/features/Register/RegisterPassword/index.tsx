import PasswordInputField from "@/components/FormItems/PasswordInputField";
import SubmitButton from "@/components/Forms/SubmitButton";
import VerticalForm from "@/components/Forms/VerticalForm";
import { useAppDispatch } from "@/hooks/common";
import {setFieldErrors } from "@/redux/slice/formSlice";
import { useForm } from "antd/lib/form/Form";
import useRegister from "../useRegister";

const RegisterPassword: React.FunctionComponent = () => {
  const {submitPassword} = useRegister();
  const dispatch = useAppDispatch();
  const [form] = useForm();

  const validatePassword = (value: string) => {
    const hasLetter = /[a-zA-Z]/.test(value); // Kiểm tra có chữ cái không
    const hasNumber = /\d/.test(value); // Kiểm tra có số không
    const hasSymbol = /[^a-zA-Z0-9]/.test(value); // Kiểm tra có ký hiệu không

    if (value.length >= 8 && ((hasLetter && hasNumber) || (hasLetter && hasSymbol) || (hasNumber && hasSymbol))) {
      dispatch(setFieldErrors({}));
    } else {
      dispatch(setFieldErrors({
        password: ["Password must be 8+ characters and include two of: letters, numbers, or symbols."]
      }));
    }
  };

  const handleChangePassword = () => {
    const formdata = form.getFieldsValue();
    validatePassword(formdata.password);
  }

  const handleChangeConfirmPassword = () => {
    const formdata = form.getFieldsValue();
    if (formdata.password !== formdata.confirmPassword) {
      dispatch(setFieldErrors({
        confirmPassword: ['Passwords do not match']
      }))
    }
  }

  const handleSubmitPassword = () => {
    submitPassword(form.getFieldValue('confirmPassword'));
  }

  return (
    <>
      <div className="register-form">
        <VerticalForm name="personal-info" form={form} onFinish={handleSubmitPassword} >
          <PasswordInputField
            name="password"
            placeholder="Password"
            className="input-password"
            onChange={handleChangePassword}
          />
          <PasswordInputField
            name="confirmPassword"
            placeholder="Confirm Password"
            className="input-password"
            onChange={handleChangeConfirmPassword}
          />
          <SubmitButton buttonClassName="submit-btn" name="Continue" />
        </VerticalForm>
      </div>
    </>
  );
}

export default RegisterPassword;