import { useAppSelector } from "@/hooks/common";
import { selectKyc } from "@/redux/slice/kycSlide";
import { Icon } from "@iconify/react/dist/iconify.js";
import { Button } from "antd";
import React from "react";

const KycDetails: React.FunctionComponent = () => {
  const { documentData } = useAppSelector(selectKyc);

  return (
    <>
      <div className="kyc-verical-layout-wrapper">
        <span className="kyc-result-header">
          <div>
            <Icon icon="clarity:success-standard-line" />
          </div>
          <p>{documentData?.status}</p>
        </span>
        <div className="mt-5">
          <div className="row">
            {
              documentData?.status === 'APPROVED' && (
                <>
                  <strong className="col-md-4">Document Type</strong>
                  <div className="col-md-8 text-end">{documentData?.metaData.metaData.documentType}</div>
                  <strong className="col-md-4">ID Number</strong>
                  <div className="col-md-8 text-end">{documentData?.metaData.metaData.number}</div>
                  <strong className="col-md-4">Full name</strong>
                  <div className="col-md-8 text-end">{documentData?.metaData.metaData.fullName}</div>
                  <strong className="col-md-4">Gender</strong>
                  <div className="col-md-8 text-end">{documentData?.metaData.metaData.gender}</div>
                  <strong className="col-md-4">Place of birth</strong>
                  <div className="col-md-8 text-end">{documentData?.metaData.metaData.placeOfOrigin}</div>
                  <strong className="col-md-4">Nationality</strong>
                  <div className="col-md-8 text-end">{documentData?.metaData.metaData.nationality}</div>
                </>
              )
            }
            {
              documentData?.status !== 'APPROVED' && (
                <>
                  <strong className="col-md-4">Message</strong>
                  <div className="col-md-8 text-end">{documentData?.metaData.message}</div>
                </>
              )
            }
          </div>
          <div className="text-center">
            <Button className="btn-submit">COMPLETE</Button>
          </div>
        </div>
      </div>
    </>
  );
}
export default KycDetails;