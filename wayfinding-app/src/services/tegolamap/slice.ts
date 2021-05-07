import { createSelector, createSlice, PayloadAction } from '@reduxjs/toolkit';
import { EmitterSubscription, NativeEventEmitter, NativeModule, Platform } from 'react-native';
import { RootState } from 'store/types';
import TegolaWrapper from './wrapper';

export interface TegolaState {
  lastError: string | undefined;
  pid: number | undefined;
  port: number | undefined;
  state: string;
};

const initialState: TegolaState = {
  lastError: undefined,
  pid: undefined,
  port: undefined,
  state: "STOPPED",
};

const tegolaSlice = createSlice({
  name: 'tegola',
  initialState,
  reducers: {
    setError(state, action: PayloadAction<string>) {
      state.lastError = action.payload;
    },
    setPid(state, action: PayloadAction<number>) {
      state.pid = action.payload;
      state.state = "RUNNING";
    },
    setPort(state, action: PayloadAction<number>) {
      state.port = action.payload;
      state.state = "LISTENING";
    },
    setStatus(state, action: PayloadAction<string>) {
      state.state = action.payload;
    },
  },
});

export const selectStatus = createSelector(
  [(state: RootState) => state.tegola || initialState],
  tegola => {
    return tegola.state
  },
);

let eventListeners : EmitterSubscription[] = [];

export const initTegola = () => {
  if(Platform.OS==="android") {
    const eventEmitter = new NativeEventEmitter(TegolaWrapper as unknown as NativeModule)
    eventListeners.push(
      eventEmitter.addListener('OnMVTServerStartFailed', (event) => tegolaSlice.actions.setError(event)),
      eventEmitter.addListener('OnMVTServerRunning', (event) => tegolaSlice.actions.setPid(event)),
      eventEmitter.addListener('OnMVTServerListening', (event) => tegolaSlice.actions.setPort(event)),
      eventEmitter.addListener('OnMVTServerStopping', () => tegolaSlice.actions.setStatus("STOPPING")),
      eventEmitter.addListener('OnMVTServerStopped', () => tegolaSlice.actions.setStatus("STOPPED")),
      eventEmitter.addListener('OnMVTServerOutputStdErr', (event) => console.error(event)),
      eventEmitter.addListener('OnMVTServerOutputStdOut', (event) => console.log(event)));
    TegolaWrapper.writeConfig("ireland/ireland.gpkg");
    TegolaWrapper.initClient();
  }
};

export const deinitTegola = () => {
  if(Platform.OS==="android") {
    TegolaWrapper.stopMVTServer();
    eventListeners.forEach((listener) => listener.remove())
  }
}

export const reducer = tegolaSlice.reducer;
export const tegolaSliceKey = tegolaSlice.name;
