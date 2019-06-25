import request from '@/utils/request';
import { TableListParams } from './data';

export async function queryRule(params: TableListParams) {
  return request(`/api/xc/rule`, {
    params,
  });
}

export async function removeRule(params: TableListParams) {
  return request('/api/xc/rule', {
    method: 'POST',
    data: {
      ...params,
      method: 'delete',
    },
  });
}

export async function addRule(params: TableListParams) {
  return request('/api/xc/rule', {
    method: 'POST',
    data: {
      ...params,
      method: 'post',
    },
  });
}

export async function updateRule(params: TableListParams) {
  return request('/api/xc/rule', {
    method: 'POST',
    data: {
      ...params,
      method: 'update',
    },
  });
}


export async function queryUserList(params: any) {
  return request(`/api/admin/user/page`, {
    method: 'POST',
    data: {
      ...params,
    }
  });
}


export async function saveUser(params: any) {
  return request(`/api/admin/user/saveUser`, {
    method: 'POST',
    data: {
      ...params,
    }
  });
}
