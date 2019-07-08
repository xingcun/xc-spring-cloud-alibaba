import request from '@/utils/request';



export async function queryQuartzList(params: any) {
  return request(`/api/admin/quartz/page`, {
    method: 'POST',
    data: {
      ...params,
    }
  });
}


export async function saveQuartz(params: any) {
  return request(`/api/admin/quartz/save`, {
    method: 'POST',
    data: {
      ...params,
    }
  });
}

export async function deleteQuartz(params: any) {
  return request(`/api/admin/quartz/delete`, {
    method: 'POST',
    data: {
      ...params,
    },
    requestType: 'form'
  });
}
