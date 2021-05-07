import { createAction } from "@reduxjs/toolkit";
import { SagaIterator } from "redux-saga";
import { cancelled, cps, delay, put, select } from "redux-saga/effects";
import { P2PState, selectP2P, P2PSlice } from "./slice";
import P2PBLEService from 'services/p2pNetwork/wrapper';

export const StartP2PAction = createAction('p2p/start');
export const StopP2PAction = createAction('p2p/stop');

export function* sagaReadWorker(): SagaIterator {
  try {
    let state = (yield select(selectP2P)) as P2PState;
    if(state.state === "RUNNING") {
      console.log("Started P2P ReadWorker task");
      do {
        const fromService = yield cps(cb => P2PBLEService.getObject(res=>cb(null, res)));
        let state = (yield select(selectP2P)) as P2PState;
        if(fromService !== state.readInfo) {
          // console.log("Received from BLE: ", fromService);
          yield put(P2PSlice.actions.setReadInfo(fromService));
        }
        yield delay(10000); // wait 10s
      } while (true);
    }
  } finally {
    if (yield cancelled())
      console.log("P2P ReadWorker task cancelled.")
  }
};
