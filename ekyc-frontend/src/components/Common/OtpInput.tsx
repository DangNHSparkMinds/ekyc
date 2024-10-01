import React, { useState, useRef } from 'react';

interface IOtpInputProps {
  inputClassName?: string;
  onChangeValue?: (otp: string) => void;
}

const OtpInput: React.FunctionComponent<IOtpInputProps> = (props) => {
  const { inputClassName, onChangeValue } = props;
  const [otp, setOtp] = useState<string[]>(new Array(6).fill(""));
  const inputRefs = useRef<(HTMLInputElement | null)[]>([]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>, index: number) => {
    const value = e.target.value;
    if (/^[0-9]$/.test(value) || value === "") {
      const newOtp = [...otp];
      newOtp[index] = value;
      setOtp(newOtp);

      if (onChangeValue) {
        const otpString = newOtp.join('');
        onChangeValue(otpString);
      }

      if (value !== "" && index < 5) {
        inputRefs.current[index + 1]?.focus();
      }
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>, index: number) => {
    if (e.key === "Backspace" && otp[index] === "" && index > 0) {
      inputRefs.current[index - 1]?.focus();
    }
  };

  const handlePaste = (e: React.ClipboardEvent<HTMLInputElement>) => {
    const pasteData = e.clipboardData.getData("text");
    if (/^\d{6}$/.test(pasteData)) {
      const newOtp = pasteData.split("");
      setOtp(newOtp);
  
      if (onChangeValue) {
        const otpString = newOtp.join('');
        onChangeValue(otpString);
      }
  
      inputRefs.current[5]?.focus();
    }
  };  

  return (
    <div>
      {otp.map((digit, index) => (
        <input
          key={index}
          type="text"
          maxLength={1}
          value={digit}
          onChange={(e) => handleChange(e, index)}
          onKeyDown={(e) => handleKeyDown(e, index)}
          onPaste={handlePaste}
          ref={(el) => (inputRefs.current[index] = el)}
          className={inputClassName}
          style={{ margin: "0.5rem", textAlign: "center" }}
        />
      ))}
    </div>
  );
};

export default OtpInput;
