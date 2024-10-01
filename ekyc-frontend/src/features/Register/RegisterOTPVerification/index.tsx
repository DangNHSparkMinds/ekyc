import SubmitButton from "@/components/Forms/SubmitButton";
import useRegister from "../useRegister";
import { useState } from "react";
import OtpInput from "@/components/Common/OtpInput";

const RegisterOTPVerification: React.FunctionComponent = () => {
  const { verifyOTP } = useRegister();
  const [otpCode, setOtpCode] = useState('');

  const handleSubmitOtp = () => {
    verifyOTP(otpCode);
  }

  return (
    <div className="text-center">
      <p>Enter OTP verification code</p>
      <div>
        <OtpInput inputClassName="otp-input" onChangeValue={setOtpCode} />
        <SubmitButton name="Verification" buttonClassName="submit-btn mt-5" onClick={handleSubmitOtp} />
      </div>
    </div>
  )
}

export default RegisterOTPVerification;
