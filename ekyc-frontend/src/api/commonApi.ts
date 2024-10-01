import { HttpResponse } from '@/models/http';
import axiosClient, { handleRequest } from './axiosClient';
import { VerifyOtpRequest } from '@/models/auth';

const commonApi = {
  getUserDetails: (): Promise<HttpResponse<any>> => {
    const url = `/api/public/user/details`;
    return handleRequest(axiosClient.get(url));
  },
  verifyOtp: (body: VerifyOtpRequest): Promise<HttpResponse<any>> => {
    const url = `/api/auth/register/otp-code`;
    return handleRequest(axiosClient.post(url, body));
  }
};

export default commonApi;
