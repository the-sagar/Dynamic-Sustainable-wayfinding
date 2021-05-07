import React, { useEffect, useLayoutEffect, useState } from 'react';
import { compose, bindActionCreators, Dispatch } from 'redux';
import { connect } from 'react-redux';
import { injectReducer, injectSaga } from 'redux-injectors';
import { StackNavigationProp } from '@react-navigation/stack';
import { AppScreens } from 'navigators/ScreenDefs';
import { Platform, View } from 'react-native';
import tailwind from 'tailwind-rn';
import Text from 'components/Text';
import { TextInput, IconButton, Button, Modal, Portal, ActivityIndicator } from 'react-native-paper';
import { RootState } from 'store/types';
import {
  mapSlice,
  rootMapSaga,
  selectOverlays,
  RouteAction,
  SearchAction,
  selectSearches,
  SearchExactAction,
  selectEndPoint,
  selectExactResult,
} from 'services/map/slice';
import { FontAwesome5 } from '@expo/vector-icons';
import { useFormField } from 'utils/FormFields';
import { useTheme } from 'styled-components/native';
import SearchSuggestor, { Item } from 'components/SearchSuggestor';
import StartPoint from 'components/MapComponents/StartPoint';
import EndPoint from 'components/MapComponents/EndPoint';
import * as Location from 'expo-location';
import CurrentPoint from 'components/MapComponents/CurrentPoint';
import SearchPoint from 'components/MapComponents/SearchPoint';

const MapboxGL = Platform.select({
  web: () => null,
  android: () => require("@react-native-mapbox-gl/maps").default,
  ios: () => require("@react-native-mapbox-gl/maps").default
})!();
// import MapboxGL from '@react-native-mapbox-gl/maps';

MapboxGL?.setAccessToken("pk.eyJ1IjoiY29td29yZCIsImEiOiJja2ttNWphNjYxZ3d0MnZ0ZGU0MzZxamFlIn0.Ohtn69cPPJYfwtL5VWdC1g");
// MapboxGL?.setAccessToken("pk.notoken");

type ComponentProps = {
  navigation: StackNavigationProp<any, AppScreens.Login>
};

type Props = ComponentProps & ReturnType<typeof mapStateToProps> &
ReturnType<typeof mapDispatchToProps>

const MapScreen: React.FC<Props> = ({navigation, overlays, searches, endPoint, exactResult,
  RouteAction, SearchAction, SearchExactAction}) => {
  MapboxGL?.setAccessToken("pk.eyJ1IjoiY29td29yZCIsImEiOiJja2ttNWphNjYxZ3d0MnZ0ZGU0MzZxamFlIn0.Ohtn69cPPJYfwtL5VWdC1g");
  // MapboxGL?.setAccessToken("pk.notoken");
  if(Platform.OS === "android")
    MapboxGL?.setConnected(true);

  const theme = useTheme();
  const [permGranted, setPermGranted] = useState(false);
  const [searchSuggestions, setSearchSuggestions] = useState<Item[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [curLocation, setCurLocation] = useState<Location.LocationObject>();
  const fromField = useFormField("53.3524, -6.25866");
  const toField = useFormField("53.36135, -6.23813");

  const requestPermission = async () => {
    const isGranted = await MapboxGL?.requestAndroidLocationPermissions();
    setPermGranted(isGranted);
  }

  useEffect(()=>{
    requestPermission();
  }, []);

  useEffect(()=>{
    setIsLoading(false);
  }, [overlays]);

  useEffect(()=>{
    let res : Item[] = [];
    searches.map(text => res.push({
      id: text,
      name: text,
      details: "",
    }));
    setSearchSuggestions(res);
  }, [searches]);

  useEffect(() => {
    const t = Date.now()
    const i = setInterval(() => {
      (async () => {
        const location = (await Location.getCurrentPositionAsync({})) as Location.LocationObject;
        setCurLocation(location);
      })
      }, 1000);
    return () => clearInterval(i)
  },[])

  useLayoutEffect( () => {
    navigation.setOptions({
      headerRight: () => (
        <View style={tailwind('flex flex-row')}>
          <IconButton icon="account-settings" onPress={()=>navigation.navigate(AppScreens.Preference)}></IconButton>
          <IconButton icon="bluetooth-settings" onPress={()=>navigation.navigate(AppScreens.P2POptIn)}></IconButton>
          <IconButton icon={({ size, color }) => (
            <FontAwesome5 name="envira" size={size} color={color} />
          )} onPress={()=>navigation.navigate(AppScreens.Sustainable)}></IconButton>
        </View>
      )
    });
  });

  const handleRoute = () => {
    let [ latStr, lonStr ] = fromField.value.split(",");
    const from = {
      Lat: parseFloat(latStr),
      Lon: parseFloat(lonStr)
    };
    [ latStr, lonStr ] = toField.value.split(",");
    const to = {
      Lat: parseFloat(latStr),
      Lon: parseFloat(lonStr)
    };
    setIsLoading(true);
    RouteAction({from, to});
  };

  const handleSearch = (search: string) => {
    if(search.length !== 0)
      SearchAction(search);
  };

  return (
    <>
    <SearchSuggestor onIconPress={handleSearch} suggestions={searchSuggestions}
      onFocus={()=>setSearchSuggestions([])} fetchSuggestions={search => SearchAction(search)}
      onSelect={select => {SearchExactAction(select); setSearchSuggestions([]);}}/>
    <Portal>
      <Modal visible={isLoading} dismissable={false}>
        <ActivityIndicator animating={true} size="large" color={theme.colors.primary}/>
      </Modal>
    </Portal>
    <View style={tailwind('flex-row justify-between items-center')}>
      <TextInput style={tailwind('w-5/12')} testID="startLoc"
        label="Start" {...fromField}/>
      <TextInput style={tailwind('w-5/12')} testID="endLoc"
        label="End" {...toField}/>
      <IconButton icon="navigation" onPress={handleRoute} testID="route"/>
    </View>
    <View style={tailwind("h-full flex")}>
      {Platform.OS != "web" &&
        <View style={tailwind("h-full flex")}>
          {!permGranted &&
            <View>
              <Text style={tailwind('text-center text-2xl my-8')}>
                We need location permission, please grant it.
              </Text>
              <Button mode="contained" onPress={()=>requestPermission()}>Request again</Button>
            </View>
            }
          {permGranted &&
            <MapboxGL.MapView style={tailwind('flex-1')}
              // styleURL={"asset://map_style.json"}
              attributionEnabled={false} logoEnabled={false}>
              <MapboxGL.Camera
                zoomLevel={14}
                centerCoordinate={[-6.2381319, 53.361363100000005]}
                animationMode={'flyTo'}
                animationDuration={0} />
              {/* <MapboxGL.VectorSource id="osm" tms tileUrlTemplates={["http://localhost:23897/maps/osm/{z}/{x}/{y}.vector.pbf"]} /> */}
              <StartPoint id="start" coordinate={fromField.value.split(",").map(n => parseFloat(n)).reverse()}/>
              {endPoint && endPoint.length !== 0 &&
                <EndPoint id="end" coordinate={endPoint.split(",").map(n => parseFloat(n)).reverse()}/>
              }
              { curLocation && <CurrentPoint id="current" coordinate={[curLocation?.coords.longitude, curLocation?.coords.latitude ]} /> }
              {exactResult && exactResult.length > 0 && exactResult.map((s, i)=>(
                <SearchPoint
                  id={`search-${i}`}
                  key={`search-${i}`}
                  coordinate={s.split(",").map(n => parseFloat(n)).reverse()}
                  onSelected={(coor) => toField.onChangeText(`${coor[1]}, ${coor[0]}`)} />
              ))}
              {overlays.length>0 && <MapboxGL.ShapeSource id={`overlayRoute`} shape={overlays.length>0 ? JSON.parse(overlays[overlays.length-1]) : {type: "FeatureCollection", features:[]}}>
                <MapboxGL.LineLayer id={`overlayLine`} style={{lineWidth: 3, lineJoin: 'bevel', lineColor: "red"}} />
              </MapboxGL.ShapeSource>}
            </MapboxGL.MapView>
            }
        </View>
        }
      {Platform.OS === "web" &&
        <Text>Not implemented for web</Text>
        }
    </View>
    </>
  );
};


const mapStateToProps = (state: RootState) => {
  return {
    overlays: selectOverlays(state),
    searches: selectSearches(state),
    endPoint: selectEndPoint(state),
    exactResult: selectExactResult(state),
  }
}

function mapDispatchToProps(dispatch: Dispatch) {
  return bindActionCreators(
    {
      RouteAction,
      SearchAction,
      SearchExactAction
    },
    dispatch
  );
}

const withConnect = connect(mapStateToProps, mapDispatchToProps);
const withReducer = injectReducer({ key: mapSlice.name, reducer: mapSlice.reducer });
const withSaga = injectSaga({key: mapSlice.name, saga: rootMapSaga })

export default compose(withConnect, withReducer, withSaga)(MapScreen) as React.ComponentType<ComponentProps>;

