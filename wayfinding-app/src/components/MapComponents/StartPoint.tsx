import React from 'react';
import { Text, View } from 'react-native';
import tailwind from 'tailwind-rn';
import MapPoint from './PointBase';

type ComponentProps = {
  id: string,
  coordinate: GeoJSON.Position
};

const StartPoint: React.FC<ComponentProps> = ({id, coordinate}) => {
  return (
    <MapPoint id={id} coordinate={coordinate}>
      <View style={{
        height: 25,
        width: 25,
        backgroundColor: '#00cc22',
        borderRadius: 25,
        borderColor: '#fff',
        borderWidth: 3
      }} >
        <Text style={{color: "#000", paddingLeft: 5}}>S</Text>
      </View>
    </MapPoint>
  );
};

export default StartPoint;
