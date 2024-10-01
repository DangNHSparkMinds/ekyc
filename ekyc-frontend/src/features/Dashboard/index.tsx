import commonApi from "@/api/commonApi";
import { useAppDispatch } from "@/hooks/common";
import { setUserDetails } from "@/redux/slice/commonSlice";
import { Button } from "antd";
import React, { useEffect } from "react";

const Dashboard: React.FunctionComponent = () => {

  const dispatch = useAppDispatch();
  useEffect(() => {
    const fetchUserDetails = async () => {
      const response = await commonApi.getUserDetails();
      if (response.ok && response.body) {
        dispatch(setUserDetails(response.body));
      }
    }
    fetchUserDetails();
  }, []);

  return (
    <div className="row">
      <div className="col-md-8">
        <div className="row gx-5">
          <div className="col-md-7">
            <div className="card-banner">
              <Button className="btn-more">
                More
              </Button>
            </div>
          </div>
        </div>
      </div>
      <div className="col-md-4">
      </div>
    </div>
  )
}

export default Dashboard;
