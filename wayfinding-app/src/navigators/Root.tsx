/**
 * Used to navigating without the navigation prop
 * @see https://reactnavigation.org/docs/navigating-without-navigation-prop/
 *
 * You can add other navigation functions that you need and export them
 */
import React, { Suspense, useContext, useEffect } from 'react'
import { CommonActions, NavigationContainer, NavigationContainerRef, ParamListBase, StackActions } from '@react-navigation/native'
import AppNavigator from './AppNavigator';
import { ThemeContext } from 'styled-components/native';
import { LoadingIndicator } from 'components/LoadingIndicator';
import { Platform } from 'react-native';
import { compose, bindActionCreators, Dispatch } from 'redux';
import { connect } from 'react-redux';
import { injectReducer } from 'redux-injectors';
import { P2PSlice, StartServiceAction } from 'services/p2pNetwork';

export const navigatorRef = React.createRef<NavigationContainerRef>();
let stack: Array<object> = [];

export const RouterActions = {
  push: (screen: string, props: object = {}): void => {
    stack.push({
      name: screen,
      params: props,
    });

    navigatorRef.current?.dispatch(
      CommonActions.navigate({
        params: props,
        name: screen,
      })
    );
  },
  replace: (screen: string, props: object = {}): void => {
    stack = [
      {
        name: screen,
        params: props,
      },
    ];
    const replaceAction = StackActions.replace(screen, props);
    navigatorRef.current?.dispatch(replaceAction);
  },
  pop: (): void => {
    stack.pop();

    const backAction = CommonActions.goBack();
    navigatorRef.current?.dispatch(backAction);
  },
  currentState: (): object => stack[stack.length - 1],
  navigate: (name: string, params: ParamListBase): void => {
    navigatorRef.current?.navigate(name, params)
  },
  navigateAndReset: (routes = [], index = 0): void => {
    navigatorRef.current?.dispatch(
      CommonActions.reset({
        index,
        routes,
      })
    )
  }
};

type Props = ReturnType<typeof mapDispatchToProps>;

const RootNavigator: React.FC<Props> = ({ StartServiceAction }) => {
  const themeContext = useContext(ThemeContext);

  useEffect(()=>{
    if(Platform.OS === "android") {
      StartServiceAction();
    }
  }, []);

  return (
    <Suspense fallback={<LoadingIndicator/>}>
      <NavigationContainer ref={navigatorRef} theme={themeContext}>
        <AppNavigator />
      </NavigationContainer>
    </Suspense>
  );
};

function mapDispatchToProps(dispatch: Dispatch) {
  return bindActionCreators(
    {
      StartServiceAction,
    },
    dispatch
  );
}

const withConnect = connect(null, mapDispatchToProps);
const withReducer = injectReducer({ key: P2PSlice.name, reducer: P2PSlice.reducer });

export default compose(withConnect, withReducer)(RootNavigator);
