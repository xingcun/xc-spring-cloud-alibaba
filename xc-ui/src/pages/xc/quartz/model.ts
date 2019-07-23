import { queryQuartzList,saveQuartz,deleteQuartz,setQuartzState } from './service';
import { Reducer } from 'redux';
import { EffectsCommandMap } from 'dva';
import { AnyAction } from 'redux';


export type Effect = (
  action: AnyAction,
  effects: EffectsCommandMap & { select: <T>(func: (state: any) => T) => T },
) => void;

export interface ModelType {
  namespace: string;
  state: {};
  effects: {
    fetch: Effect;
    add: Effect;
    remove: Effect;
    update: Effect;
  };
  reducers: {
    save: Reducer<any>;
  };
}

const Model: ModelType = {
  namespace: 'xcQuartz',

  state: {
    data: {},
  },

  effects: {
    *fetch({ payload }, { call, put }) {
      const response = yield call(queryQuartzList, payload);
      if(response && response.code==1) {
        yield put({
          type: 'save',
          payload: response,
        });
      }

    },
    *remove({ payload, callback }, { call, put }) {
      const response = yield call(deleteQuartz, payload);
      yield put({
        type: 'save',
        payload: response,
      });
      if (callback) callback();
    },
    *setQuartzState({ payload, callback }, { call, put }) {
      const response = yield call(setQuartzState, payload);
      yield put({
        type: 'save',
        payload: response,
      });
      if (callback){
        let msg = null;
        if(response.code!=1) {
          msg = response.message;
        }
        callback(msg);
      }
    },
    *update({ payload, callback }, { call, put }) {
      const response = yield call(saveQuartz, payload);
      if (callback){
        let msg = null;
        if(response.code!=1) {
          msg = response.message;
        }
        callback(msg);
      }
    },
  },

  reducers: {
    save(state, action) {
      return {
        ...state,
        data: action.payload,
      };
    },
  },
};

export default Model;
