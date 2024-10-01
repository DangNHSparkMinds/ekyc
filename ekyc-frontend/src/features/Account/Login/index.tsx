import authApi from "@/api/authApi";
import PasswordInputField from "@/components/FormItems/PasswordInputField";
import TextInputField from "@/components/FormItems/TextInputField"; import SubmitButton from "@/components/Forms/SubmitButton";
import VerticalForm from "@/components/Forms/VerticalForm";
import { setIsSubmitting } from "@/redux/slice/formSlice";
import { useForm } from "antd/lib/form/Form";
import React from "react";
import { useDispatch } from "react-redux";
import { Link, useNavigate } from "react-router-dom";

const Login: React.FunctionComponent = () => {
  const dispatch = useDispatch()
  const navigate = useNavigate();
  const [form] = useForm()

  const submitLogin = async () => {
    dispatch(setIsSubmitting(true))
    const reqBody = {
      email: form.getFieldValue('email'),
      password: form.getFieldValue('password'),
    }
    try {
      const response = await authApi.login(reqBody);
      if (response.ok && response.body) {
        localStorage.setItem('ewallet-access-token', response.body.accessToken);
        navigate("/dashboard");
      }
    } catch (err) {
      console.error('Unexpected error', err);
    } finally {
      dispatch(setIsSubmitting(false))
    }
  }

  return (
    <>
      <div className="login-layout-wrapper">
        <div className="login-left">
          <img src="/src/assets/login.jpg" />
        </div>
        <div className="login-form">
          <VerticalForm form={form} onFinish={submitLogin}>
            <div className="login-form-header">EWallet</div>
            <div className='login__form__content'>
              <TextInputField
                className='input-primary'
                name='email'
                placeholder="Email"
                validateStatus="error"
              />
              <PasswordInputField
                className='input-primary'
                name='password'
                placeholder='Password'
              />
            </div>
            <div className="mt-5 text-center">
              <SubmitButton name="Login" buttonClassName="btn-login" />
              <span className="mt-5">
                <Link className="link-login" to={"/register"}>Register</Link>
              </span>
            </div>
          </VerticalForm>
        </div>
      </div>
    </>
  );
}

export default Login;