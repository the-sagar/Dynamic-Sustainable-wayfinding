import {rpcRouteServiceClient} from "./routeClient";
import {GPSLocation,
  TypeNaviRoute,
  TypeNaviNode,
  NaviType,
  NodeContent,
  ViaContent
} from './types';

import {
  ObjectResolveRequest,
  ReturnedObject,
  RoutingDecision,
  RoutingDecisionReq,
  RoutingDecisionReqLocation,
  RoutingDecisionResp
} from "../gen-proto/def_pb";

class RouteGenerator {
  constructor() {
  }

  async getRoute(from: GPSLocation, to: GPSLocation, addInfo?: Map<string, string>): Promise<TypeNaviRoute> {
    let routingDecision = new RoutingDecisionReq;

    let fromr = new RoutingDecisionReqLocation();
    fromr.setLat(from.Lat)
    fromr.setLon(from.Lon)
    routingDecision.setFrom(fromr)

    let tor = new RoutingDecisionReqLocation();
    tor.setLat(to.Lat)
    tor.setLon(to.Lon)
    routingDecision.setTo(tor)

    if(addInfo)
      addInfo.forEach((value: string, key: string) => routingDecision.getAdditionalinfoMap().set(key, value));

    // addInfo.set("can_publictrans", "false");
    // addInfo.set("can_walkLong", "false");
    // addInfo.set("can_bike", "false");
    // addInfo.set("can_drive", "true");

    let resp = (await rpcRouteServiceClient.route!(routingDecision)) as RoutingDecisionResp;

    let hops: Array<RoutingDecision> = resp.getHopsList();

    let ret = {Nodes: Array<TypeNaviNode>()} as TypeNaviRoute;

    for (let v of hops) {
      let fromw = (await ResolveObject(v.getFrom())) as NodeContent;

      if (v.getVia() == "") {
        ret.Nodes.push({
          NaviType: NaviType.StartPoint,
          NaviNode: fromw,
          NaviNodeLocation: {Lat: fromw.lat, Lon: fromw.lon},
          NaviVia: undefined,
          NaviViaLocations: undefined
        })
      } else {
        let viaw = (await ResolveObject(v.getVia())) as ViaContent;
        // let GetKnownPoints = viaw.tags["X-osmRoute-KnownPoints"];
        // let knownpoints = JSON.parse(GetKnownPoints)
        ret.Nodes.push({
          NaviType: NaviType.Via,
          NaviNode: fromw,
          NaviNodeLocation: {Lat: fromw.lat, Lon: fromw.lon},
          NaviVia: viaw,
          // NaviViaLocations: knownpoints
        })
      }
    }

    ret.Nodes.push({
      NaviType: NaviType.EndPoint,
      NaviNode: undefined,
      NaviNodeLocation: undefined,
      NaviVia: undefined,
      NaviViaLocations: undefined
    })

    return ret
  }
}

async function ResolveObject(name: string): Promise<NodeContent|ViaContent> {
  let reso = new ObjectResolveRequest();
  reso.setFeatureid(name);
  let nodev: ReturnedObject = (await rpcRouteServiceClient.resolve!(reso)) as ReturnedObject;
  var string = atob(nodev.getObjectcontent_asB64());
  return JSON.parse(string);
}

export {RouteGenerator}
