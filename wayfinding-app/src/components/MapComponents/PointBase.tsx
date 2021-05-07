import React from 'react';
import { Platform } from "react-native";

// const MapboxGL = Platform.select({
//   web: () => null,
//   android: () => require("@react-native-mapbox-gl/maps").default,
//   ios: () => require("@react-native-mapbox-gl/maps").default
// })!();

import MapboxGL, { Point } from '@react-native-mapbox-gl/maps';

type ComponentProps = {
  id: string,
  coordinate: GeoJSON.Position,
  anchor?: Point,
  onSelected?: (coordinate: GeoJSON.Position) => void,
};

const MapPoint: React.FC<ComponentProps> = ({id, coordinate, anchor, onSelected, children}) => {
  return (
    <MapboxGL.PointAnnotation onSelected={() => onSelected? onSelected(coordinate): null}
      key={id} id={id} coordinate={coordinate} anchor={anchor}>
      {children}
    </MapboxGL.PointAnnotation>
  );
};

export default MapPoint;
