import request from '@/utils/request';

export async function testDubbo(params: any) {
  return request(`/api/admin/testDubbo`, {
    params,
  });
}

export async function testCache(params: any) {
  return request(`/api/admin/testCache`, {
    params,
  });
}

export async function testExecutor(params: any) {
  return request(`/api/admin/testExecutor`, {
    params,
  });
}


export async function testFeign(params: any) {
  return request(`/api/admin/testFeign`, {
    params,
  });
}

export async function testFeignFactory(params: any) {
  return request(`/api/admin/testFeignFactory`, {
    params,
  });
}
export async function testLock(params: any) {
  return request(`/api/admin/testLock`, {
    params,
  });
}

export async function testRemoteEvent(params: any) {
  return request(`/api/admin/testRemoteEvent`, {
    params,
  });
}

export async function testLocalEvent(params: any) {
  return request(`/api/admin/testLocalEvent`, {
    params,
  });
}
export async function testMqEvent(params: any) {
  return request(`/api/admin/testMqEvent`, {
    params,
  });
}
