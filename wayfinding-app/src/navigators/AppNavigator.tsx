import React, { useContext } from 'react';
import { compose, bindActionCreators, Dispatch } from 'redux';
import { connect } from 'react-redux';
import { injectReducer, injectSaga } from 'redux-injectors';
import { createStackNavigator, TransitionPresets } from '@react-navigation/stack';
import { AppScreens } from './ScreenDefs';
import { ThemeContext } from 'styled-components/native';
import WelcomeScreen from 'containers/WelcomeScreen';
import RegisterScreen from 'containers/RegisterScreen';
import MapScreen from 'containers/MapScreen';
import LoginScreen from 'containers/LoginScreen';
import PreferenceScreen from 'containers/UserPreference';
import SustainScreen from 'containers/SustainScreen';
import P2POptInScreen from 'containers/P2POptInScreen';
import { RootState } from 'store/types';

import { authSlice, rootAuthSaga, selectError, selectLoginStruct } from 'store/auth';

const TransitionScreenOptions = {
  ...TransitionPresets.SlideFromRightIOS
};

type Props = {} & ReturnType<typeof mapStateToProps> &
ReturnType<typeof mapDispatchToProps>

const AppNavigator: React.FC<Props> = ({emailId, lastError}) => {
  const themeContext = useContext(ThemeContext);
  const Stack = createStackNavigator();
  const navOptions = {
    headerStyle: {
      backgroundColor: themeContext.colors.primary,
    }
  };

  return (
    <Stack.Navigator screenOptions={TransitionScreenOptions}>
      {!emailId && <>
        <Stack.Screen name={AppScreens.Welcome} options={{headerShown: false, ...navOptions}} component={WelcomeScreen}/>
        <Stack.Screen name={AppScreens.Register} options={navOptions} component={RegisterScreen} />
        <Stack.Screen name={AppScreens.Login} options={navOptions} component={LoginScreen} />
      </>}
      {emailId && <>
        <Stack.Screen name={AppScreens.Map} options={navOptions} component={MapScreen} />
        <Stack.Screen name={AppScreens.Preference} options={navOptions} component={PreferenceScreen} />
        <Stack.Screen name={AppScreens.P2POptIn} options={navOptions} component={P2POptInScreen} />
        <Stack.Screen name={AppScreens.Sustainable} options={navOptions} component={SustainScreen} />
      </>}
    </Stack.Navigator>
  );
};

const mapStateToProps = (state: RootState) => {
  return {
    emailId: selectLoginStruct(state)?.emailId,
    lastError: selectError(state)
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
const withReducer = injectReducer({ key: authSlice.name, reducer: authSlice.reducer });
const withSaga = injectSaga({key: authSlice.name, saga: rootAuthSaga });

export default compose(withConnect, withReducer, withSaga)(AppNavigator) as React.ComponentType<{}>;
