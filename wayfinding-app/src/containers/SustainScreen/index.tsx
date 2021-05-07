import { decode, encode } from "base-64";
import React, { Suspense, useState } from 'react';
import { compose, bindActionCreators, Dispatch } from 'redux';
import { connect } from 'react-redux';
import { injectReducer, injectSaga } from 'redux-injectors';
import { StackNavigationProp } from '@react-navigation/stack';
import { AppScreens } from 'navigators/ScreenDefs';
import { View, Platform } from 'react-native';
// import { Canvas } from 'components/SustainRenderer';
import tailwind from 'tailwind-rn';
// import Earth from 'components/SustainRenderer/Earth';
// import SkyBox from 'components/SustainRenderer/Skybox';
import AppDialog from 'components/AppDialog';
import { Text } from 'react-native';
import { Button, Dialog } from 'react-native-paper';
import { RootState } from 'store/types';
import { selectSustain, sustainSlice, rootSustainSaga } from './slice';
// import Clock3D from './clock';
import ClockText from './clockText';
import usePromise from "react-promise-suspense";
import { Asset } from "expo-asset";

const Video = Platform.select({
  web: () => null,
  android: () => require("react-native-video").default,
  ios: () => require("react-native-video").default
})!();

if (!global.btoa) {
  global.btoa = encode;
}

if (!global.atob) {
  global.atob = decode;
}

type ComponentProps = {
  navigation: StackNavigationProp<any, AppScreens.Sustainable>
};

type Props = ComponentProps & ReturnType<typeof mapStateToProps> &
ReturnType<typeof mapDispatchToProps>

const SustainScreen: React.FC<Props> = ({navigation, sustainStore}) => {
  const [showDialog, setShowDialog] = useState(true);
  const loadVideo = async () => {
    const asset = Asset.fromModule(require("./assets/earth.mp4"));
    await asset.downloadAsync();
    return asset.uri;
  };

  return (
    <View style={tailwind('h-full flex-1')}>
      {/* {Platform.OS === "web" && <Canvas camera={{ position: [0, 0, 1200], up: [0, 0, 0] }}>
        <Suspense fallback="loading">
          <Earth />
        </Suspense>
        <SkyBox />
        <Clock3D doomsday={sustainStore.doomsday} seconds={sustainStore.seconds}/>
      </Canvas>} */}
      {Platform.OS != "web" && <View style={tailwind('flex')}>
        <Suspense fallback="loading">
          <Video
            style={tailwind('flex h-full')}
            source={{uri: usePromise(loadVideo, [])}}
            muted={true}
            repeat={true}
            resizeMode={"cover"}
            rate={1.0}
            ignoreSilentSwitch={"obey"} />
        </Suspense>
        <ClockText doomsday={sustainStore.doomsday} seconds={sustainStore.seconds}/>
      </View>}

      <AppDialog visible={showDialog}>
        <Dialog.Title>Save humanity</Dialog.Title>
        <Dialog.Content>
          <Text style={tailwind("text-justify")}>The time shown here represents the remaining endurance of humanity,
            if we continue in our current course without taking any action. However,
            with the action from every individual, this can be changed. Each of our
            actions will have a tiny but measurable effect on our fate. This will be
            represented with light on the spinning globe below. The globe has no
            borders and no marks, represent every individual work toward the goal of
            extending our shared future.</Text>
        </Dialog.Content>
        <Dialog.Actions>
          <Button onPress={()=>setShowDialog(false)}>Done</Button>
        </Dialog.Actions>
      </AppDialog>
    </View>
  );
};

const mapStateToProps = (state: RootState) => {
  return {
    sustainStore: selectSustain(state)
  }
}

function mapDispatchToProps(dispatch: Dispatch) {
  return bindActionCreators(
    {
      ...sustainSlice.actions
    },
    dispatch
  );
}

const withConnect = connect(mapStateToProps, mapDispatchToProps);
const withReducer = injectReducer({ key: sustainSlice.name, reducer: sustainSlice.reducer });
const withSaga = injectSaga({key: sustainSlice.name, saga: rootSustainSaga })

export default compose(withConnect, withReducer, withSaga)(SustainScreen) as React.ComponentType<ComponentProps>;

