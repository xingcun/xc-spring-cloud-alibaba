import {getLoginUser ,xcGetCode} from '@/services/user';
import { Effect } from 'dva';
import { Reducer } from 'redux';

export interface CurrentUser {
  avatar?: string ;
  userName?: string;
  mobile?: string;
  nickName?: string;
  [key: string]: any;
}

export interface UserModelState {
  currentUser?: CurrentUser;
}

export interface UserModelType {
  namespace: 'user';
  state: UserModelState;
  effects: {
    fetch: Effect;
    fetchCurrent: Effect;
  };
  reducers: {
    saveCurrentUser: Reducer<UserModelState>;
    changeNotifyCount: Reducer<UserModelState>;
  };
}

const UserModel: UserModelType = {
  namespace: 'user',

  state: {
    currentUser: {},
  },

  effects: {
    *getLoginUser(_, { call, put }) {
      const response = yield call(getLoginUser);
      if(response.code==1 && response.result.obj) {
        yield put({
          type: 'saveXcUser',
          payload: response,
        });
      }

    },
    *getSmsCode({ payload }, { call }) {
      const response = yield call(xcGetCode, payload.data);
      if(payload.resolve){
        payload.resolve(response);
      }

    },
  },

  reducers: {
    saveXcUser(state,action) {
      action.payload.result.obj.avatar = 'https://gw.alipayobjects.com/zos/antfincdn/XAosXuNZyF/BiazfanxmamNRoxxVxka.png';
      return {
        ...state,
        currentUser: action.payload.result.obj,
      };
    },




  },
};

export default UserModel;
