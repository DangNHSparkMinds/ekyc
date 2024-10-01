import authApi from "@/api/authApi";
import commonApi from "@/api/commonApi";
import { useAppDispatch } from "@/hooks/common";
import { SubmitEmailRequest, SubmitPasswordRequest, VerifyOtpRequest } from "@/models/auth";
import { setIsLoggedIn } from "@/redux/slice/authSlice";
import { setFieldErrors, setIsSubmitting } from "@/redux/slice/formSlice";
import { useState } from "react";
import { useNavigate } from "react-router-dom";


const useRegister = () => {
  const navigate = useNavigate();
  const dispatch = useAppDispatch();
  const [message, setMessage] = useState<string | null>(null);

  const register = async (reqBody: SubmitEmailRequest) => {
    dispatch(setIsSubmitting(true));
    try {
      const response = await authApi.register(reqBody);
      if (response.ok) {
        sessionStorage.setItem("register-email", reqBody?.email);
        navigate("/register/otp-verification");
      } else {
        if (response.error?.message) {
          dispatch(setFieldErrors({
            email: ["Email address existed"]
          }));
        }
      }
    } catch (e) {
      console.error("Unexpected error", e);
    } finally {
      dispatch(setIsSubmitting(false));
    }
  }

  const verifyOTP = async (otpCode: string) => {
    dispatch(setIsSubmitting(true));
    try {
      const reqBody: VerifyOtpRequest = {
        email: sessionStorage.getItem("register-email"),
        otpCode,
      };
      const response = await commonApi.verifyOtp(reqBody);
      if (response.ok && response.body) {
        sessionStorage.setItem("register-token", response.body.token);
        navigate("/register/password");
      }
    } catch (e) {
      console.error("Unexpected error", e);
    } finally {
      dispatch(setIsSubmitting(false));
    }
  }

  const submitPassword = async (password: string) => {
    dispatch(setIsSubmitting(true));
    try {
      const reqToken = sessionStorage.getItem('register-token')
      const reqBody: SubmitPasswordRequest = {
        password,
      }
      const response = await authApi.submitPasswordRegister(reqBody, reqToken || "");
      if (response.ok && response.body) {
        localStorage.setItem('ewallet-access-token', response.body.accessToken);
        localStorage.setItem('ewallet-access-token', response.body.refreshToken);
        navigate('/dashboard');
        dispatch(setIsLoggedIn());
      }
    } catch (e) {
      console.error("Unexpected error", e);
    } finally {
      dispatch(setIsSubmitting(false));
    }
  }

  return { register, message, setMessage, verifyOTP, submitPassword }
}

export default useRegister;