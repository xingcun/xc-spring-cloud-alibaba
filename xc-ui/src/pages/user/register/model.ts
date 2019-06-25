import {fakeRegister, xcUserRegister} from './service';
import { Reducer } from 'redux';
import { EffectsCommandMap } from 'dva';
import { AnyAction } from 'redux';

export interface IStateType {
  status?: 'ok' | 'error';
  currentAuthority?: 'user' | 'guest' | 'admin';
}

export type Effect = (
  action: AnyAction,
  effects: EffectsCommandMap & { select: <T>(func: (state: IStateType) => T) => T },
) => void;

export interface ModelType {
  namespace: string;
  state: IStateType;
  effects: {
    submit: Effect;
  };
  reducers: {
    registerHandle: Reducer<IStateType>;
  };
}

const Model: ModelType = {
  namespace: 'userRegister',

  state: {
    status: undefined,
  },

  effects: {
    *submit({ payload }, { call, put }) {
      const response = yield call(fakeRegister, payload);
      yield put({
        type: 'registerHandle',
        payload: response,
      });
    },

    *regist({ payload ,callback}, { call, put }) {
      const response = yield call(xcUserRegister, payload);

      if(callback) {
        callback(response);
      }
    }
  },

  reducers: {
    registerHandle(state, { payload }) {
      return {
        ...state,
        ...payload,
      };
    },
  },
};

export default Model;
