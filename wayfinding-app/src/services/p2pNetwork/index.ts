import { SagaIterator } from "redux-saga";
import { all, call, race, take, takeLatest } from "redux-saga/effects";
import { P2PSlice, selectP2P } from "./slice";

import {
  StartP2PAction,
  StopP2PAction,
  sagaReadWorker
} from "./rworker";

import {
  StartServiceAction,
  StopServiceAction,
  sagaStartService,
  sagaStopService
} from "./serviceCtl";

function* rootP2PSaga(): SagaIterator {
  yield all([
    takeLatest(StartServiceAction, sagaStartService),
    takeLatest(StopServiceAction, sagaStopService),
    takeLatest(StartP2PAction, function* () {
      yield race({
        task: call(sagaReadWorker),
        cancel: take(StopP2PAction)
      })
    })
  ]);
};

export { P2PSlice, rootP2PSaga, selectP2P };
export { StartP2PAction, StopP2PAction, StartServiceAction, StopServiceAction};
