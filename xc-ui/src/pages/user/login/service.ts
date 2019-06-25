import request from '@/utils/request';
import { FromDataType } from './index';

export async function fakeAccountLogin(params: FromDataType) {
  return request('/api/login/account', {
    method: 'POST',
    data: params,
  });
}

export async function getFakeCaptcha(mobile: string) {
  return request(`/api/login/captcha?mobile=${mobile}`);
}


export async function xcLogin(params: FromDataType) {
  return request('/api/admin/user/login', {
    method: 'post',
    data: params,
    requestType: "form",
  });
}



export async function xcLoginMobile(params: FromDataType) {
  return request('/api/admin/user/loginMobile', {
    method: 'post',
    data: params,
    requestType: "form",
  });
}
