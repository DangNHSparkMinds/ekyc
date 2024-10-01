import axios, { AxiosResponse } from 'axios';
import AxiosResponseData from '@/models/axios';
import { HttpResponse } from '@/models/http';
import env from '@/app/env';

const baseURL = env.baseGatewayUrl;

const axiosClient = axios.create({
  baseURL,
  headers: {
    'Content-Type': 'application/json'
  },
  // withCredentials: true,
});

axiosClient.interceptors.request.use(
  (config) => config,
  (error) => Promise.reject(error)
);

axiosClient.interceptors.request.use(request => {
  const accessToken = localStorage.getItem('ewallet-access-token');
  if (accessToken) {
    request.headers['Authorization'] = `Bearer ${accessToken}`;
  }
  return request;
}, error => {
  return Promise.reject(error);
});

axiosClient.interceptors.response.use(
  // @ts-expect-error: we want to return the different data type
  (response: AxiosResponse<AxiosResponseData>) => {
    const { status, data: responseData, headers } = response;
    const data: HttpResponse<object> = {
      status,
      ok: true,
      body: responseData,
    };

    if (headers.link) {
      data.pagination = {
        paging: 0,
        total: Number(headers['x-total-count']),
      };
    }

    return data;
  },
  async (error) => {
    const { response } = error as { response: AxiosResponse<AxiosResponseData>; config: any };
    const { status, data } = response;

    const httpError: HttpResponse = {
      status,
      ok: false,
      error: {
        unauthorized: status === 401,
        badRequest: status === 400,
        notFound: status === 404,
        clientError: status >= 400 && status <= 499,
        serverError: status >= 500 && status <= 599,
        message: data.messageCode || data.data.messageCode,
        // title: `${data.messageCode}-title`,
        errors: data.errors,
        detail: data.detail,
        data: data.data,
      },
    };

    return Promise.reject(httpError);
  }
);

const handleRequest = (promise: Promise<HttpResponse>) =>
  promise.then((res) => res).catch((err) => err as HttpResponse<any>);

export default axiosClient;

export { handleRequest };
