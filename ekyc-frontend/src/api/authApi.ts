import { HttpResponse } from '@/models/http';
import axiosClient, { handleRequest } from './axiosClient';
import {SubmitEmailRequest, SubmitPasswordRequest, VerifyOtpRequest } from '@/models/auth';

const authApi = {
  register: (body: SubmitEmailRequest): Promise<HttpResponse<any>> => {
    const url = `/api/auth/register/email`;
    return handleRequest(axiosClient.post(url, body));
  },
  verifyOtp: (body: VerifyOtpRequest): Promise<HttpResponse<any>> => {
    const url = `/api/v/email`;
    return handleRequest(axiosClient.post(url, body));
  },
  submitPasswordRegister: (body: SubmitPasswordRequest, token: string): Promise<HttpResponse<any>> => {
    const headers = {
      'X-Registration-Token': `Bearer ${token}`,
    }
    const url = `/api/auth/register/password`;
    return handleRequest(axiosClient.post(url, body, { headers }));
  },
  login: (body: any): Promise<HttpResponse<any>> => {
    const url = `/api/auth/login`;
    return handleRequest(axiosClient.post(url, body));
  },
};

export default authApi;
