import React from 'react';
import { compose, bindActionCreators, Dispatch } from 'redux';
import { connect } from 'react-redux';
import { StackNavigationProp } from '@react-navigation/stack';
import { AppScreens } from 'navigators/ScreenDefs';
import { View, Text as RNText, Image, StyleSheet, Dimensions } from 'react-native';
import tailwind from 'tailwind-rn';

import { Entypo } from '@expo/vector-icons';
import { useTheme } from 'styled-components/native';
import Text from 'components/Text';
import { Button } from 'react-native-paper';
import { StatusBar } from 'expo-status-bar';
import { selectDisplay, themeSliceKey, reducer } from 'styles/theme/slice';
import { RootState } from 'store/types';
import { injectReducer } from 'redux-injectors';

type ComponentProps = {
  navigation: StackNavigationProp<any, AppScreens.Welcome>
};

type Props = ComponentProps & ReturnType<typeof mapStateToProps> &
ReturnType<typeof mapDispatchToProps>

const WelcomeScreen: React.FC<Props> = ({navigation, themeDisplay}) => {
  const theme = useTheme();
  const styles = StyleSheet.create({
    container: {
        overflow: "hidden",
        flex: 7,
    },
    castle: tailwind("h-full w-full"),
    footprint: {
      ...tailwind("absolute h-full w-full"),
      transform: [
        { scale: 0.9 },
        { translateX: - Dimensions.get('window').width * 0.33 },
        { translateY: Dimensions.get('window').height * 7/13 * 0.4 },
      ],
    },
    birdOne: {
      ...tailwind("absolute"),
      height: "10%",
      width: "10%",
      transform: [
        { translateX: Dimensions.get('window').width * 0.03 }
      ]
    },
    birdTwo: {
      ...tailwind("absolute"),
      height: "10%",
      width: "10%",
      transform: [
        { translateX: Dimensions.get('window').width * 0.84 },
        { translateY: 20 },
      ]
    }
  });
  return (
    <View style={tailwind("flex h-full w-full")}>
      <StatusBar backgroundColor="transparent" animated translucent
          style={themeDisplay === 'dark' ? 'light' : 'dark'}/>
      <View style={{flex: 6, ...tailwind("pt-12 px-8")}}>
        <Entypo name="map" size={48} color={theme.colors.text} />
        <Text style={tailwind('text-left text-2xl mt-3')}>
          <RNText style={{color: theme.colors.accent}}>GREEN</RNText> Travel
        </Text>
        <Text style={tailwind('text-center text-4xl mt-3')}>
          <RNText style={{color: theme.colors.accent}}>GREEN</RNText> Ireland
        </Text>
        <Text style={tailwind('text-right text-xl mt-3')}>
          <RNText style={{color: theme.colors.accent}}>Save </RNText>
          <RNText style={{color: theme.colors.primary}}>Humanity </RNText>
          Today
        </Text>
        <Button mode="contained" uppercase={false}
          onPress={()=>{navigation.navigate(AppScreens.Register)}}
          color={theme.colors.primary} style={tailwind("mt-3")}>
            <Text style={tailwind('text-lg')}>Sign up</Text>
          </Button>
        <Button mode="outlined" uppercase={false}
          onPress={()=>{navigation.navigate(AppScreens.Login)}}
          style={tailwind("mt-3")}>
          <Text style={tailwind('text-lg')}>Log in</Text>
        </Button>
      </View>
      <View style={styles.container}>
        <Image style={styles.castle} source={require('./assets/castle.png')}/>
        <Image style={styles.footprint} source={require('./assets/footsteps.png')}/>
        <Image style={styles.birdOne} source={require('./assets/bird_one.png')}/>
        <Image style={styles.birdTwo} source={require('./assets/bird_one.png')}/>
      </View>
    </View>
  );
};

const mapStateToProps = (state: RootState) => {
  return {
    themeDisplay: selectDisplay(state),
  }
}

function mapDispatchToProps(dispatch: Dispatch) {
  return bindActionCreators(
    {
    },
    dispatch
  );
}

const withConnect = connect(mapStateToProps, mapDispatchToProps);
const withReducer = injectReducer({ key: themeSliceKey, reducer: reducer });

export default compose(withConnect, withReducer)(WelcomeScreen) as React.ComponentType<ComponentProps>;

