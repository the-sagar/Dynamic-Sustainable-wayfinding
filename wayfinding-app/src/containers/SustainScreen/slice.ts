import { createAction, createSelector, createSlice, PayloadAction } from "@reduxjs/toolkit";
import { SagaIterator } from "redux-saga";
import { takeLatest, all, call, put } from "redux-saga/effects";
import { RootState } from "store/types";

export interface SustainState {
  doomsday: string,
  seconds: number,
  carbonEmission: number
}

const initialState: SustainState = {
  doomsday: "22-02-2222", //DD-MM-yyyy
  seconds: 0,
  carbonEmission: 0
};

export const sustainSlice = createSlice({
  name: 'sustain',
  initialState,
  reducers: {
    setSeconds(state, action: PayloadAction<number>) {
      state.seconds = action.payload;
    },
    setCarbonEmission(state, action: PayloadAction<number>) {
      state.carbonEmission = action.payload;
    },
  },
});

export const selectSustain = createSelector(
  [(state: RootState) => state.sustain || initialState],
  pref => {
    return pref;
  },
);

export const FetchAction = createAction('sustain/fetch');

function* sagaFetch(): SagaIterator {
  try {

  } catch(err) {
    console.log(err);
  }
}

export function* rootSustainSaga(): SagaIterator {
  yield all([
    takeLatest(FetchAction, sagaFetch),
  ]);
}
