import React from "react";
import GeoJSON from "geojson";
import { NaviType, TypeNaviRoute } from "services/rpc/types";

class FeatureCollectionImpl implements GeoJSON.FeatureCollection<GeoJSON.LineString>{
  type: "FeatureCollection" = "FeatureCollection";
  features: GeoJSON.Feature<GeoJSON.LineString, GeoJSON.GeoJsonProperties>[] = [];
}

class FeatureImpl implements GeoJSON.Feature<GeoJSON.LineString> {
  type: "Feature" = "Feature";
  geometry: GeoJSON.LineString;
  id?: string | number | undefined;
  properties: GeoJSON.GeoJsonProperties = {};

  constructor(geometry: GeoJSON.LineString) {
    this.geometry = geometry;
  }

  addPosition(pos: GeoJSON.Position) {
    this.geometry.coordinates.push(pos);
  }
}

class LineStringImpl implements GeoJSON.LineString {
  type: "LineString" = "LineString";
  coordinates: GeoJSON.Position[] = [];
}

export const genRouteGeoJSON = (naviRoute: TypeNaviRoute): GeoJSON.GeoJSON => {
  let result = new FeatureCollectionImpl();
  let feature = new FeatureImpl(new LineStringImpl());
  for(let entity of naviRoute.Nodes) {
    switch(entity.NaviType) {
      case NaviType.StartPoint:
        if(entity.NaviNodeLocation)
          feature.addPosition([entity.NaviNodeLocation.Lon, entity.NaviNodeLocation.Lat]);
      break;
      case NaviType.Via:
        if(entity.NaviNodeLocation)
        feature.addPosition([entity.NaviNodeLocation.Lon, entity.NaviNodeLocation.Lat]);
        // if(entity.NaviViaLocations)
        // for(let coord of entity.NaviViaLocations)
        //   feature.addPosition([coord.Lon, coord.Lat]);
      break;
    }
  }
  result.features.push(feature);
  return result;
};
