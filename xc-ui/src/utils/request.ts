/**
 * request 网络请求工具
 * 更详细的 api 文档: https://github.com/umijs/umi-request
 */
import { extend } from 'umi-request';
import {Modal, notification} from 'antd';
import {routerRedux} from "dva/router";
import {stringify} from "qs";

interface ResponseError<D = any> extends Error {
  name: string;
  data: D;
  response: Response;
}

const codeMessage = {
  200: '服务器成功返回请求的数据。',
  201: '新建或修改数据成功。',
  202: '一个请求已经进入后台排队（异步任务）。',
  204: '删除数据成功。',
  400: '发出的请求有错误，服务器没有进行新建或修改数据的操作。',
  401: '用户没有权限（令牌、用户名、密码错误）。',
  403: '用户得到授权，但是访问是被禁止的。',
  404: '发出的请求针对的是不存在的记录，服务器没有进行操作。',
  406: '请求的格式不可得。',
  410: '请求的资源被永久删除，且不会再得到的。',
  422: '当创建一个对象时，发生一个验证错误。',
  500: '服务器发生错误，请检查服务器。',
  502: '网关错误。',
  503: '服务不可用，服务器暂时过载或维护。',
  504: '网关超时。',
};

/**
 * 异常处理程序
 */
const errorHandler = (error: ResponseError) => {
  const {data={}, response = {} as Response } = error;
//  const data = error.data;
  const { status, url } = response;
  let errortext = codeMessage[response.status] || response.statusText;

  if(data){
    if(data.code==99){
      goToLogin();
      return data;
    }
    if(data.message){
      errortext = data.message;
    }

  }



  notification.error({
    message: `请求错误 ${status}: ${url}`,
    description: errortext,
  });
  return data;
};
let isShowLogin = false;
function goToLogin() {
  if(isShowLogin) {
    return;
  }
  isShowLogin = true;
  let secondsToGo = 3;
  const modal = Modal.error({
    title: '用户未登录',
    content: ` ${secondsToGo}秒后自动跳转登录页,或点击确认跳转`,
    onOk: ()=>{
      showLogin();
      },
  });
  const timer = setInterval(() => {
    secondsToGo -= 1;
    modal.update({
      content: `${secondsToGo}秒后自动跳转登录页,或点击确认跳转`,
    });
  }, 1000);
  setTimeout(() => {
    showLogin();
  }, secondsToGo * 1000);

  function showLogin(){
    isShowLogin = false;
    clearInterval(timer);
    modal.destroy();
  //  window.location.href = '/user/login';
    window.location.href = '/user/login?redirect='+ window.location.href;



  }
}

/**
 * 配置request请求时的默认参数
 */
let request = extend({
  errorHandler, // 默认错误处理
  credentials: 'include', // 默认请求是否带上cookie
});

request.interceptors.request.use( (url,options) => {
    const xcToken = sessionStorage.getItem('XC-TOKEN');
    if(xcToken){
      options.headers['xc-token']=xcToken;
    }

    return url;
}

);

request.interceptors.response.use( async (response,options) => {
    const data =await response.clone().json();
    if(data && data.code==99){
      goToLogin();
    }
    return response;
  }
);


export function setRequestToken (token: string):void {
 sessionStorage.setItem("XC-TOKEN",token);
}

export default request;
