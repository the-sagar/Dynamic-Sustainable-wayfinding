import { createAction, createSelector, createSlice, PayloadAction } from "@reduxjs/toolkit";
import { SagaIterator } from "redux-saga";
import { all, put, call, takeLatest, select } from "redux-saga/effects";
import { GPSLocation, TypeNaviRoute, ViaContent } from "services/rpc/types";
import { RootState } from "store/types";
import { RouteGenerator } from "services/rpc/utils";
import { genRouteGeoJSON } from "./geojson";
import { rpcRouteServiceClient } from "services/rpc/routeClient";
import { NameList, NameSearch, ObjectList, ObjectResolveRequest, ReturnedObject } from "services/gen-proto/def_pb";
import { selectPreference, UserPreferenceState } from "store/preferences";

export interface MapState {
  overlay: Array<string>,
  endPoint?: string,
  searches: Array<string>
  exactResult: Array<string>,
};

const initialState: MapState = {
  overlay: [] as Array<string>,
  searches: [] as Array<string>,
  exactResult: [] as Array<string>,
};

export const mapSlice = createSlice({
  name: 'map',
  initialState,
  reducers: {
    addOverlay(state, action: PayloadAction<string>) {
      state.overlay.push(action.payload);
    },
    setSearches(state, action: PayloadAction<string[]>) {
      state.searches = action.payload;
    },
    setExactResult(state, action: PayloadAction<string[]>) {
      state.exactResult = action.payload;
    },
    setEndPoint(state, action: PayloadAction<string>) {
      state.endPoint = action.payload;
    },
  },
});

export const selectOverlays = createSelector(
  [(state: RootState) => state.map || initialState],
  map => {
    return map.overlay;
  },
);

export const selectSearches = createSelector(
  [(state: RootState) => state.map || initialState],
  map => {
    return map.searches;
  },
);

export const selectEndPoint = createSelector(
  [(state: RootState) => state.map || initialState],
  map => {
    return map.endPoint;
  },
);

export const selectExactResult = createSelector(
  [(state: RootState) => state.map || initialState],
  map => {
    return map.exactResult;
  },
);

export interface routeRequest {
  from: GPSLocation,
  to: GPSLocation
};

export const RouteAction = createAction<routeRequest>('map/route');

function PrefStateToReq(state: UserPreferenceState): Map<string, string> {
  return new Map([
    ["objective_time", state.objective.time.toString()],
    ["objective_cost", state.objective.cost.toString()],
    ["objective_sustainable", state.objective.sustainable.toString()],
    ["can_publictrans", state.can.publicTrans.toString()],
    ["can_walkLong", state.can.walkLong.toString()],
    ["can_drive", state.can.drive.toString()],
    ["can_bike", state.can.bike.toString()],
  ]);
}

function* sagaRoute(action: ReturnType<typeof RouteAction>): SagaIterator {
  try {
    const pref = (yield select(selectPreference)) as UserPreferenceState;
    const routeCls = new RouteGenerator();
    const req = action.payload;
    const routeResult = (yield call(routeCls.getRoute, req.from, req.to, PrefStateToReq(pref))) as TypeNaviRoute;
    const result = (yield call(genRouteGeoJSON, routeResult)) as GeoJSON.FeatureCollection<GeoJSON.LineString>;
    const resJson = JSON.stringify(result);
    const endPoint = result.features[0].geometry.coordinates[result.features[0].geometry.coordinates.length-1]
    yield put(mapSlice.actions.setEndPoint(`${endPoint[1]}, ${endPoint[0]}`));
    yield put(mapSlice.actions.addOverlay(resJson));
  } catch(err) {
    console.log(err);
  }
}

export const SearchAction = createAction<string>('map/search');

function* sagaSearch(action: ReturnType<typeof SearchAction>): SagaIterator {
  try {
    const req = action.payload;
    const param = new NameSearch();
    param.setKeyword(req);
    const searchRes = (yield call([rpcRouteServiceClient, rpcRouteServiceClient.searchByNamePrefix!], param)) as NameList;
    yield put(mapSlice.actions.setSearches(searchRes.getObjectnameList()));
  } catch(err) {
    console.log(err);
  }
}

export const SearchExactAction = createAction<string>('map/searchExact');

function* sagaSearchExact(action: ReturnType<typeof SearchExactAction>): SagaIterator {
  try {
    yield put(mapSlice.actions.setExactResult([]));
    const req = action.payload;
    const param = new NameSearch();
    param.setKeyword(req);
    const searchRes = (yield call([rpcRouteServiceClient, rpcRouteServiceClient.searchByNameExact!], param)) as ObjectList;
    const featureList = searchRes.getFeatureidList();
    let resultList: string[] = [];
    for (let name of featureList) {
      const paramResolve = new ObjectResolveRequest();
      paramResolve.setFeatureid(name);
      paramResolve.setSkipifnotcached(false);
      const resolveRes = (yield call([rpcRouteServiceClient, rpcRouteServiceClient.resolve!], paramResolve)) as ReturnedObject;
      let jsonL1 = JSON.parse(atob(resolveRes.getObjectcontent_asB64())) as ViaContent;
      if('tags' in jsonL1 && 'X-osmRoute-KnownPoints' in jsonL1.tags) {
        let jsonL2 = (JSON.parse(jsonL1.tags["X-osmRoute-KnownPoints"])) as Array<GPSLocation>;
        let {Lat, Lon} = jsonL2.reduce((p, c) => ({Lat: p.Lat + c.Lat, Lon: p.Lon + c.Lon}));
        Lat = Lat / jsonL2.length;
        Lon = Lon / jsonL2.length;
        resultList.push(`${Lat}, ${Lon}`);
      }
    }
    console.log(resultList);
    yield put(mapSlice.actions.setExactResult(resultList));
    // yield put(mapSlice.actions.setExactResult(searchRes.getObjectnameList()));
  } catch(err) {
    console.log(err);
  }
}

export function* rootMapSaga(): SagaIterator {
  yield all([
    takeLatest(RouteAction, sagaRoute),
    takeLatest(SearchAction, sagaSearch),
    takeLatest(SearchExactAction, sagaSearchExact),
  ]);
}
