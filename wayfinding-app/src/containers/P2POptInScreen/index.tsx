import React, { useEffect, useState } from "react";
import { ScrollView, View } from "react-native";
import { compose, bindActionCreators, Dispatch } from 'redux';
import { connect } from 'react-redux';
import { injectReducer } from 'redux-injectors';
import { StackNavigationProp } from "@react-navigation/stack";
import { AppScreens } from "navigators/ScreenDefs";
import { useTheme } from "styled-components/native";
import { RootState } from "store/types";
import { FontAwesome } from '@expo/vector-icons';
import Text from 'components/Text';
import tailwind from "tailwind-rn";
import { Button } from "react-native-paper";
import { P2PSlice, selectP2P, StartP2PAction, StopP2PAction } from "services/p2pNetwork";
import * as Notifications from 'expo-notifications';
import * as Permissions from "expo-permissions";

const askPermissions = async () => {
  const { status: existingStatus } = await Permissions.getAsync(Permissions.NOTIFICATIONS);
  let finalStatus = existingStatus;
  if (existingStatus !== "granted") {
    const { status } = await Permissions.askAsync(Permissions.NOTIFICATIONS);
    finalStatus = status;
  }
  if (finalStatus !== "granted") {
    return false;
  }
  return true;
};

type ComponentProps = {
  navigation: StackNavigationProp<any, AppScreens.P2POptIn>
};

type Props = ComponentProps & ReturnType<typeof mapStateToProps> &
ReturnType<typeof mapDispatchToProps>

const P2POptInScreen: React.FC<Props> = ({navigation, readInfo, StartP2PAction, StopP2PAction}) => {
  const theme = useTheme();
  // const [isShow, setIsShow] = useState(false);
  const [isGranted, setIsGranted] = useState(false);

  useEffect(()=>{
    if(readInfo !== undefined && readInfo?.length > 5) {
      if (isGranted) {
        const localNotification = {
          title: 'New Road Impacts',
          body: 'A new message received from Bluetooth.' + readInfo,
        };
        Notifications.setNotificationHandler({
          handleNotification: async () => ({
            shouldShowAlert: true,
            shouldPlaySound: false,
            shouldSetBadge: false,
          }),
        });
        Notifications.scheduleNotificationAsync({
          content: localNotification,
          trigger: null,
        });
      }
    }
  }, [readInfo]);

  useEffect(() => {
    const notifiPermission = async () => {
      setIsGranted(await askPermissions());
    };
    notifiPermission();
  }, [])

  return (
    <ScrollView>
      <View style={tailwind("flex w-full text-center mt-8")}>
        <View style={tailwind("flex flex-row justify-center items-center")} >
          <FontAwesome name="bluetooth" size={50} color={theme.colors.primary} />
          <Text style={tailwind("text-3xl ml-2")}>Bluetooth</Text>
        </View>
        <Text style={tailwind("text-lg mt-2 text-center")}>Offline Peer-to-Peer Network</Text>
        <Text style={tailwind("text-3xl mt-4 text-center")}>Opt-in Consent</Text>
      </View>
      <View style={tailwind("flex p-8")}>
        <Text style={tailwind("text-justify")}>You are about to activate the P2P network function of this program. This feature will use the Bluetooth low energy feature on your device to receive and share some real-time traffic information. This function can obtain real-time traffic information from other users nearby when your device is offline. At the same time, you will also broadcast the traffic information received from our server via Bluetooth to help other offline users nearby.</Text>
        <Text style={tailwind("text-justify")}>Enabling this feature and join the P2P network requires your device to support and enable the Bluetooth low energy function. At the same time, other users may detect your private information wirelessly, including the MAC address of your Bluetooth device, and the physical distance to your device.</Text>
        <Text style={tailwind("text-justify")}>If you agree to join the P2P network, please choose button "I agree" below.</Text>
      </View>
      <View style={tailwind('flex-row justify-around my-1')}>
        <Button mode="outlined" onPress={()=>{navigation.goBack()}} uppercase={false}>I Disagree</Button>
        <Button mode="contained" onPress={()=>StartP2PAction()} testID="Agree" uppercase={false}>I Agree</Button>
      </View>
    </ScrollView>
  );
};

const mapStateToProps = (state: RootState) => {
  return {
    readInfo: selectP2P(state).readInfo
  }
}

function mapDispatchToProps(dispatch: Dispatch) {
  return bindActionCreators(
    {
      StartP2PAction,
      StopP2PAction
    },
    dispatch
  );
}

const withConnect = connect(mapStateToProps, mapDispatchToProps);
const withReducer = injectReducer({ key: P2PSlice.name, reducer: P2PSlice.reducer });

export default compose(withConnect, withReducer)(P2POptInScreen) as React.ComponentType<ComponentProps>;
