import { createAction, createSelector, createSlice, PayloadAction } from "@reduxjs/toolkit";
import { SagaIterator } from "redux-saga";
import { takeLatest, all, call, put, select, delay } from "redux-saga/effects";
import { fetchPreferenceRequest, uploadPreferenceRequest } from "services/http/preference";
import { MessageResponseType } from "services/http/response";
import { RootState } from "store/types";
import { authSlice } from "./auth";

export interface UserPreferenceState {
  firstName: string,
  lastName: string,
  birthDay: string,
  objective: {
    time: number,
    cost: number,
    sustainable: number
  },
  can: {
    walkLong: boolean,
    drive: boolean,
    bike: boolean,
    publicTrans: boolean
  }
}

const initialState: UserPreferenceState = {
  firstName: "",
  lastName: "",
  birthDay: "01-01-1970", //DD-MM-yyyy
  objective: {
    time: 0.33,
    cost: 0.33,
    sustainable: 0.33
  },
  can: {
    walkLong: true,
    drive: true,
    bike: true,
    publicTrans: true
  }
};

export const preferenceSlice = createSlice({
  name: 'pref',
  initialState,
  reducers: {
    setFirstName(state, action: PayloadAction<string>) {
      state.firstName = action.payload;
    },
    setLastName(state, action: PayloadAction<string>) {
      state.lastName = action.payload;
    },
    setBirthday(state, action: PayloadAction<string>) {
      state.birthDay = action.payload;
    },
    setObjective(state, action: PayloadAction<{sustainable: number, cost: number, time: number}>) {
      state.objective.time = action.payload.time;
      state.objective.cost = action.payload.cost;
      state.objective.sustainable = action.payload.sustainable;
    },
    setCan(state, action: PayloadAction<{cate: string, can: boolean}>) {
      if(action.payload.cate in state.can) {
        //@ts-ignore
        state.can[action.payload.cate] = action.payload.can;
      }
    },
    setState(state, action: PayloadAction<UserPreferenceState>) {
      return action.payload;
    }
  }
});

export const selectPreference = createSelector(
  [(state: RootState) => state.pref || initialState],
  pref => {
    return pref;
  },
);

export const SavePrefAction = createAction('pref/save');

function* sagaSavePreference(): SagaIterator {
  try {
    let isFinish = false;
    do {
      const pref = (yield select(selectPreference)) as UserPreferenceState;
      const rsp = (yield call(uploadPreferenceRequest, pref)) as MessageResponseType;
      if (rsp == null)
        console.log("Network error while save user preference");
      else if ("error" in rsp) {
        console.log("Server returns error: " + rsp["error"]);
        isFinish = true;
      } else if ("responseCode" in rsp) {
        if (rsp.responseCode == 0)
          console.log("Saved preference: " + rsp.message);
        else
          console.log("Server returns: " + rsp.message);
        isFinish = true;
      }
      yield delay(30000) // retry in 0.5 min
      } while(!isFinish);
  } catch(err) {
    console.log(err);
  }
}

export const FetchPrefAction = createAction('pref/fetch');

function* sagaFetchPreference(): SagaIterator {
  try {
    const rsp = (yield call(fetchPreferenceRequest)) as UserPreferenceState;
    if (rsp == null) {
      console.log("Network error while fetch user preference");
      return;
    }
    yield put(preferenceSlice.actions.setState(rsp));
  } catch(err) {
    console.log(err);
  }
}

export function* rootPrefSaga(): SagaIterator {
  yield all([
    takeLatest(SavePrefAction, sagaSavePreference),
    takeLatest(FetchPrefAction, sagaFetchPreference),
    takeLatest(authSlice.actions.setLoginResponse, sagaFetchPreference),
  ]);
}

export const reducer = preferenceSlice.reducer;
export const preferenceSliceKey = preferenceSlice.name;
