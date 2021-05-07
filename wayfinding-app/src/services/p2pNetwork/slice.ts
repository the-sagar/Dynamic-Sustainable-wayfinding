import { createSelector, createSlice, PayloadAction } from "@reduxjs/toolkit";
import { RootState } from "store/types";

export interface P2PState {
  lastError: string | undefined;
  readInfo: string | undefined;
  state: "STOPPED" | "RUNNING";
};

const initialState: P2PState = {
  lastError: undefined,
  readInfo: undefined,
  state: "STOPPED",
};

export const P2PSlice = createSlice({
  name: 'p2p',
  initialState,
  reducers: {
    setError(state, action: PayloadAction<string>) {
      state.lastError = action.payload;
    },
    setStatus(state, action: PayloadAction<"STOPPED" | "RUNNING">) {
      state.state = action.payload;
    },
    setReadInfo(state, action: PayloadAction<string>) {
      state.readInfo = action.payload;
    }
  },
});

export const selectP2P = createSelector(
  [(state: RootState) => state.p2p || initialState],
  p2p => p2p,
);
