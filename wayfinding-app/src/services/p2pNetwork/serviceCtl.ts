import { createAction } from "@reduxjs/toolkit";
import { SagaIterator } from "redux-saga";
import { cps, put } from "redux-saga/effects";
import P2PBLEService from 'services/p2pNetwork/wrapper';
import { P2PSlice } from "./slice";

export const StartServiceAction = createAction('p2p/startService');
export const StopServiceAction = createAction('p2p/stopService');

export function* sagaStartService(): SagaIterator {
  const result = yield cps(cb => P2PBLEService.startService(res=>cb(null, res)));
  console.log("Start P2P network service: ", result);
  if(result)
    yield put(P2PSlice.actions.setStatus("RUNNING"));
  else
    yield put(P2PSlice.actions.setStatus("STOPPED"));
};

export function* sagaStopService(): SagaIterator {
  const result = yield cps(cb => P2PBLEService.stopService(res=>cb(null, res)));
  console.log("Stop P2P network service: ", result);
  if(result)
    yield put(P2PSlice.actions.setStatus("STOPPED"));
  else
    yield put(P2PSlice.actions.setStatus("RUNNING"));
};
