import kycApi from "@/api/kycApi";
import FileInput from "@/components/Common/FileInput";
import SubmitButton from "@/components/Forms/SubmitButton";
import { useAppDispatch } from "@/hooks/common";
import { setIsSubmitting } from "@/redux/slice/formSlice";
import { Icon } from "@iconify/react/dist/iconify.js";
import { useState } from "react";
import { useNavigate } from "react-router-dom";

const IdentityVerification: React.FunctionComponent = () => {
  const navigate = useNavigate();
  const dispatch = useAppDispatch();
  const [frontImg, setFrontImg] = useState();
  const [backImg, setBackImg] = useState();

  const handleVerifyDocument = async () => {
    const reqBody = {
      frontSideImage: frontImg,
      backSideImage: backImg,
      documentType: 'CITIZEN_ID_CARD'
    }
    dispatch(setIsSubmitting(true))
    try {
      const response = await kycApi.verifyDocument(reqBody);
      if (response.ok && response.body) {
        if (response.body.kycStatus === 'REJECT') {

        } else {
          navigate("/kyc/face");
        }
      }
    } catch (e) {
      console.error('Unexpected error', e);
    } finally {
      dispatch(setIsSubmitting(false));
    }
  }

  return (
    <>
      <div className="kyc-layout-wrapper">
        <div className="kyc-container text-center">
          <h5 className="mt-5">Upload Identity Document</h5>
          <div className="mt-5 d-flex justify-content-evenly">
            <div>
              <FileInput onChange={setFrontImg} >
                <div>
                  <Icon icon="solar:user-id-linear" height={38} />
                  <p>Front side</p>
                </div>
              </FileInput>
            </div>
            <div>
              <FileInput onChange={setBackImg} >
                <div>
                  <Icon icon="solar:card-2-linear" height={38} />
                  <p>Back side</p>
                </div>
              </FileInput>
            </div>
          </div>
          <div className="mt-5">
            <SubmitButton name="Continue" buttonClassName="btn-submit" onClick={handleVerifyDocument} />
          </div>
        </div>
      </div>
    </>
  )
}

export default IdentityVerification;