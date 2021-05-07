import React, { useContext } from 'react';
import styled, { ThemeContext } from 'styled-components/native';
import tailwind from 'tailwind-rn';
import { Button } from 'react-native-paper';
import { StackNavigationProp } from '@react-navigation/stack';
import { AppScreens } from 'navigators/ScreenDefs';

import { ObjectListWithAssociatedObjects, ScanRegionRequest } from 'services/gen-proto/def_pb';
import { rpcRouteServiceClient } from 'services/rpc/routeClient';
import { NativeModules } from 'react-native';
import TegolaWrapper from 'services/tegolamap/wrapper';

type Props = {
  navigation: StackNavigationProp<any, AppScreens.Welcome>
};

const DebugScreen: React.FC<Props> = ({navigation}) => {
  const themeContext = useContext(ThemeContext);

  const testRPC = async () => {
    const param = new ScanRegionRequest();
    param.setLat(53.3537673);
    param.setLon(-6.2564454);
    const result = (await rpcRouteServiceClient.scanRegion!(param)) as ObjectListWithAssociatedObjects;
    // const objList = result.getFeatureidList();
    // console.log(objList);
    const featureAssoc = result.getFeatureidandassociatedobjectsMap();
    console.log(featureAssoc);
    const locAssoc = result.getLocationassociationList();
    console.log(locAssoc);
  };

  return (
    <Wrapper>
      <Row>
        <Button mode="contained" onPress={()=>{navigation.navigate(AppScreens.Register)}}>Register</Button>
        <Button mode="contained" color={themeContext.colors.accent} onPress={()=>{navigation.navigate(AppScreens.Login)}}>Login</Button>
        <Button mode="outlined" onPress={()=>{navigation.navigate(AppScreens.Map)}}>Map</Button>
        <Button onPress={testRPC}>Test gRPC</Button>
      </Row>
      <Row>
        <Button mode="contained" onPress={()=>{NativeModules.LogcatActivity.startActivity()}}>Logcat</Button>
        <Button mode="contained" onPress={()=>{NativeModules.AboutLibsActivity.startActivity()}}>About</Button>
        <Button onPress={()=>{TegolaWrapper.startMVTServer("ireland", "version.properties")}}>startMVT</Button>
        <Button onPress={()=>{TegolaWrapper.stopMVTServer()}}>StopMVT</Button>
      </Row>
      <Row>
        <Button mode="contained" color={themeContext.colors.accent} onPress={()=>{navigation.navigate(AppScreens.Preference)}}>Preference</Button>
        <Button onPress={()=>{NativeModules.RoutingService.startService()}}>Start Routing</Button>
        <Button onPress={()=>{NativeModules.RoutingService.stopService()}}>Stop Routing</Button>
      </Row>
      <Row>
        <Button mode="contained" onPress={()=>{navigation.navigate(AppScreens.Sustainable)}}>Sustain</Button>
      </Row>
    </Wrapper>
  );
};

const Wrapper = styled.View`
  ${tailwind('flex-1')}
  background-color: ${p => p.theme.colors.background};
`;

const Row = styled.View`
  ${tailwind('flex-row justify-around my-2')}
`;

export default DebugScreen;
