import React from 'react';
import { View, Text } from 'react-native';
import MapPoint from './PointBase';
import { MaterialIcons } from '@expo/vector-icons';

type ComponentProps = {
  id: string,
  coordinate: GeoJSON.Position,
  onSelected?: (coordinate: GeoJSON.Position) => void,
};

const SearchPoint: React.FC<ComponentProps> = ({id, coordinate, onSelected}) => {
  return (
    <MapPoint id={id} coordinate={coordinate} anchor={{x: 0.5, y: 1}} onSelected={onSelected}>
      <View style={{
        height: 40,
        width: 40,
      }}>
        <Text style={{color: "black"}}>
          <MaterialIcons name="location-pin" size={40} color="black"/>
        </Text>
      </View>
    </MapPoint>
  );
};

export default SearchPoint;
