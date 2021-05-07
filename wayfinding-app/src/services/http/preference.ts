import { UserPreferenceState } from 'store/preferences';
import apiClient from './client'
import { MessageResponseType } from './response';

export type PreferenceType = {
  firstName: string,
  lastName: string,
  birthYear: number,
  birthMon: number,
  birthDay: number,
  objectiveTime: number,
  objectiveCost: number,
  objectiveSustainable: number,
  canWalkLong: boolean,
  canDrive: boolean,
  canBike: boolean,
  canPublicTrans: boolean
};

function PrefStateToReq(state: UserPreferenceState): PreferenceType {
  const birth = state.birthDay.split("-");
  return {
    firstName: state.firstName,
    lastName: state.lastName,
    birthYear: parseInt(birth[2]),
    birthMon: parseInt(birth[1]),
    birthDay: parseInt(birth[0]),
    objectiveTime: state.objective.time,
    objectiveCost: state.objective.cost,
    objectiveSustainable: state.objective.sustainable,
    canWalkLong: state.can.walkLong,
    canDrive: state.can.drive,
    canBike: state.can.bike,
    canPublicTrans: state.can.publicTrans,
  }
};

function RespToPrefState(resp: PreferenceType): UserPreferenceState {
  return {
    firstName: resp.firstName,
    lastName: resp.lastName,
    birthDay: `${resp.birthDay}-${resp.birthMon}-${resp.birthYear}`, //DD-MM-yyyy
    objective: {
      time: resp.objectiveTime,
      cost: resp.objectiveCost,
      sustainable: resp.objectiveSustainable
    },
    can: {
      walkLong: resp.canWalkLong,
      drive: resp.canDrive,
      bike: resp.canBike,
      publicTrans: resp.canPublicTrans
    }
  };
};

export const uploadPreferenceRequest = async (req: UserPreferenceState) => {
  try {
    const response = await apiClient.post('api/UserPreference', PrefStateToReq(req));
    return response?.data as MessageResponseType;
  } catch (error) {
    return error?.data;
  }
};

export const fetchPreferenceRequest = async () => {
  try {
    const response = await apiClient.get('api/UserPreference');
    const data = response?.data as PreferenceType;
    return RespToPrefState(data);
  } catch (error) {
    return error?.data;
  }
};
