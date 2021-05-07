import { ThemeState } from "styles/theme/types";
import { AuthState } from "./auth";
import { TegolaState } from "services/tegolamap/slice"
import { MapState } from "services/map/slice";
import { SustainState } from "containers/SustainScreen/slice";
import { UserPreferenceState } from "./preferences";
import { P2PState } from "services/p2pNetwork/slice";

export interface RootState {
  theme?: ThemeState;
  auth?: AuthState;
  tegola?: TegolaState;
  map?: MapState;
  pref?: UserPreferenceState;
  sustain?: SustainState;
  p2p? : P2PState;
}
