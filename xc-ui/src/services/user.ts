import request from '@/utils/request';

export async function query(): Promise<any> {
  return request('/api/users');
}

export async function queryCurrent(): Promise<any> {
  return request('/api/currentUser');
}

export async function queryNotices(): Promise<any> {
  return request('/api/notices');
}

export async function getLoginUser(): Promise<any> {
  return request('/api/admin/user/getLoginUser');
}

export async function xcGetCode(params: any) {
  return request('/api/admin/user/getCode', {
    method: 'post',
    data: params,
    requestType: "form",
  });
}
