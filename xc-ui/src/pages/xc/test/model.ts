import {testRemoteEvent,testLocalEvent, testDubbo,testCache ,testLock,testFeignFactory,testFeign,testExecutor,testMqEvent} from './service';

const Model: any = {
  namespace: 'testModel',

  state: {
    result: {},
  },

  effects: {
    *testDubbo({ payload }, { call, put }) {
      const response = yield call(testDubbo, payload);
      yield put({
        type: 'save',
        payload: response,
      });
    },
    *testCache({ payload }, { call, put }) {
      const response = yield call(testCache, payload);
      yield put({
        type: 'save',
        payload: response,
      });
    },
    *testExecutor({ payload }, { call, put }) {
      const response = yield call(testExecutor, payload);
      yield put({
        type: 'save',
        payload: response,
      });
    },
    *testFeign({ payload }, { call, put }) {
      const response = yield call(testFeign, payload);
      yield put({
        type: 'save',
        payload: response,
      });
    },

    *testFeignFactory({ payload }, { call, put }) {
      const response = yield call(testFeignFactory, payload);
      yield put({
        type: 'save',
        payload: response,
      });
    },
    *testLock({ payload }, { call, put }) {
      const response = yield call(testLock, payload);
      yield put({
        type: 'save',
        payload: response,
      });
    },

    *testRemoteEvent({ payload }, { call, put }) {
      const response = yield call(testRemoteEvent, payload);
      yield put({
        type: 'save',
        payload: response,
      });
    },
    *testLocalEvent({ payload }, { call, put }) {
      const response = yield call(testLocalEvent, payload);
      yield put({
        type: 'save',
        payload: response,
      });
    },
    *testMqEvent({ payload }, { call, put }) {
      const response = yield call(testMqEvent, payload);
      yield put({
        type: 'save',
        payload: response,
      });
    },
  },

  reducers: {
    save(state, action) {
      return {
        ...state,
        result: action.payload,
      };
    },
  },
};

export default Model;
