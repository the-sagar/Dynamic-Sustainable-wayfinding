import { createAction, createSelector, createSlice, PayloadAction } from "@reduxjs/toolkit";
import { SagaIterator } from "redux-saga";
import { all, put, call, takeLatest } from "redux-saga/effects";
import { loginRequest, registerRequest } from "services/http/auth";
import { LoginResponseType, MessageResponseType } from "services/http/response";
import { setAuthToken } from "services/http/client";
import { RootState } from "./types";
import { setParameter } from "services/rpc/websocket";
export interface AuthState {
  lastError: MsgFromServer;
  login: LoginResponseType | undefined,
  register: MessageResponseType | undefined
};
export interface RegisterLoginRequest {
  email: string;
  password: string;
};

type MsgFromServer = {
  code: number,
  msg: string,
  show: boolean
};

const initialState: AuthState = {
  lastError: {} as MsgFromServer,
  login: undefined,
  register: undefined
};

export const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    setError(state, action: PayloadAction<MsgFromServer>) {
      state.lastError = action.payload;
    },
    setLoginResponse(state, action: PayloadAction<LoginResponseType>) {
      setAuthToken(action.payload.token);
      setParameter({token: action.payload.token});
      state.login = action.payload;
    },
  },
});

export const selectSessionToken = createSelector(
  [(state: RootState) => state.auth || initialState],
  auth => {
    return auth.login?.token
  },
);

export const selectLoginStruct = createSelector(
  [(state: RootState) => state.auth || initialState],
  auth => {
    return auth.login
  },
);

export const selectError = createSelector(
  [(state: RootState) => state.auth || initialState],
  auth => {
    return auth.lastError
  },
);

//Register Action
export const RegisterAction = createAction<RegisterLoginRequest>('auth/register');

function* sagaRegister(action: ReturnType<typeof RegisterAction>): SagaIterator {
  try {
    const req = action.payload;
    const rsp = (yield call(registerRequest, req.email, req.password)) as MessageResponseType;
    if (rsp == null)
      yield put(authSlice.actions.setError({code: -1, msg: "Network error", show: true}))
    else
      yield put(authSlice.actions.setError({code: rsp.responseCode, msg: rsp.message, show: true}));
  } catch(err) {
    yield put(authSlice.actions.setError({code: err.data.responseCode, msg: err.data.message, show: true }));
  }
}
//Register Listener
function* watchSagaRegister(): SagaIterator {
  yield takeLatest(RegisterAction, sagaRegister);
}
//Login Action
export const LoginAction = createAction<RegisterLoginRequest>('auth/login');

function* sagaLogin(action: ReturnType<typeof LoginAction>): SagaIterator {
  try {
    const req = action.payload;
    const rsp = (yield call(loginRequest, req.email, req.password));
    if (rsp == null)
      yield put(authSlice.actions.setError({code: -1, msg: "Network error", show: true}));
    else if ("token" in rsp)
      yield put(authSlice.actions.setLoginResponse(rsp));
    else if ("error" in rsp)
      yield put(authSlice.actions.setError({code: -2, msg: rsp.error, show: true }));
  } catch(err) {
    yield put(authSlice.actions.setError({code: err.data.responseCode, msg: err.data.message, show: true }));
  }
}

//Login Listener
function* watchSagaLogin(): SagaIterator {
  yield takeLatest(LoginAction, sagaLogin);
}

export function* rootAuthSaga(): SagaIterator {
  yield all([
    call(watchSagaRegister),
    call(watchSagaLogin),
  ])
}

export const reducer = authSlice.reducer;
export const authSliceKey = authSlice.name;
