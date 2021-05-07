import { combineReducers } from 'redux';
import { reducer as TegolaReducer } from 'services/tegolamap/slice';
import { reducer as PreferenceReducer } from './preferences';
import { reducer as AuthReducer } from './auth';

import { InjectedReducersType } from './injector-typings';

export default function createReducer(injectedReducers: InjectedReducersType = {}) {
  return combineReducers({
    auth: AuthReducer,
    tegola: TegolaReducer,
    pref: PreferenceReducer,
    ...injectedReducers,
  });
}
