import { Content } from "antd/es/layout/layout";
import React, { useEffect } from "react";
import { Outlet, useNavigate } from "react-router-dom";
import MainHeader from "./components/SideBarMenu";
import commonApi from "@/api/commonApi";
import { useAppDispatch } from "@/hooks/common";
import { setUserDetails } from "@/redux/slice/commonSlice";

interface IMainLayoutProps { }

const MainLayout: React.FunctionComponent<IMainLayoutProps> = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();

  useEffect(() => {
    const fetchUserDetails = async () => {
      const response = await commonApi.getUserDetails();
      if (response.ok && response.body) {
        dispatch(setUserDetails(response.ok))
        if (response.body.kycStatus !== 'APPROVED') {
          navigate("/kyc/document")
        }
      }
    };
    fetchUserDetails();
  }, []);

  return (
    <div className="main-layout-wrapper">
      <MainHeader />
      <div className="sidebar-menu" />
      <div className="main-container">
        <Content>
          <Outlet />
        </Content>
      </div>
    </div>
  );
}

export default MainLayout;