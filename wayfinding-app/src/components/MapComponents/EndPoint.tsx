import React from 'react';
import { View, Text } from 'react-native';
import MapPoint from './PointBase';

type ComponentProps = {
  id: string,
  coordinate: GeoJSON.Position
};

const EndPoint: React.FC<ComponentProps> = ({id, coordinate}) => {
  return (
    <MapPoint id={id} coordinate={coordinate}>
      <View style={{
        height: 25,
        width: 25,
        backgroundColor: '#0070cc',
        borderRadius: 25,
        borderColor: '#fff',
        borderWidth: 3
      }} >
        <Text style={{color: "#fff", paddingLeft: 5}}>E</Text>
      </View>
    </MapPoint>
  );
};

export default EndPoint;
