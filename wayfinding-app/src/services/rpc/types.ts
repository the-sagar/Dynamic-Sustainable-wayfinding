type TypeNaviRoute = {
  Nodes: Array<TypeNaviNode>;
};

type TypeNaviNode = {
  NaviType: NaviType;
  NaviNode?: NodeContent;
  NaviNodeLocation?: GPSLocation;
  NaviVia?: ViaContent;
  NaviViaLocations?: Array<GPSLocation>;
};

enum NaviType {
  StartPoint = 1,
  EndPoint,
  Via,
};

type GPSLocation = {
  Lat: number;
  Lon: number;
};

type ObjectContent = {
  type: string,
  id: number,
  user: string,
  uid: number,
  visible: boolean,
  version: number,
  changeset: number,
  timestamp: string,
};

type NodeContent = {
  lat: number,
  lon: number,
} & ObjectContent;

type ViaContent = {
  nodes: Array<number>,
  tags: Map<string, string>
} & ObjectContent;

export {GPSLocation, TypeNaviRoute, TypeNaviNode, NaviType, NodeContent, ViaContent}
