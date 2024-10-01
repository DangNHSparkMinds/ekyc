import { HttpResponse } from '@/models/http';
import axiosClient, { handleRequest } from './axiosClient';

const kycApi = {
  verifyDocument: (body: any): Promise<HttpResponse<any>> => {
    const headers = {
      'Content-Type': 'multipart/form-data',
    }
    const url = `/api/public/merchant/kyc/document`;
    return handleRequest(axiosClient.post(url, body, {headers}));
  },
  verifyFace: (body: any): Promise<HttpResponse<any>> => {
    const headers = {
      'Content-Type': 'multipart/form-data',
    }
    const url = `/api/public/merchant/kyc/face`;
    return handleRequest(axiosClient.post(url, body, {headers}));
  }
};

export default kycApi;
