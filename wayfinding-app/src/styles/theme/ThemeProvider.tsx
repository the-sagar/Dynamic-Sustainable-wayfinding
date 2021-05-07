import React, { useEffect } from 'react';
import { ThemeProvider as SCThemeProvider } from "styled-components/native";
import { Appearance } from 'react-native-appearance'
import { injectReducer } from 'redux-injectors';
import { compose, bindActionCreators, Dispatch } from 'redux';
import { connect } from 'react-redux';
import { selectTheme, themeSliceKey, reducer, changeDisplay, selectDisplay, selectThemeKey } from './slice';
import { StatusBar } from 'react-native';
import { Provider as PaperProvider } from 'react-native-paper';
import { RootState } from 'store/types';

type ComponentProps = {

};

type Props = ComponentProps & ReturnType<typeof mapStateToProps> &
ReturnType<typeof mapDispatchToProps>

const ThemeProvider: React.FC<Props> = ({ theme, themeDisplay, changeDisplay, themeKey, children }) => {

  useEffect(() => {
    const subscription = Appearance.addChangeListener(({ colorScheme }) => {
      if(themeKey === 'system')
        changeDisplay(colorScheme === 'dark' ? 'dark' : 'light');
    })
    return () => subscription.remove()
  }, [])

  return (
    <PaperProvider theme={theme}>
      <SCThemeProvider theme={theme}>
        <StatusBar backgroundColor="transparent" animated translucent
          barStyle={themeDisplay === 'dark' ? 'dark-content' : 'light-content'}/>
        {React.Children.only(children)}
      </SCThemeProvider>
    </PaperProvider>
  );
};

const mapStateToProps = (state: RootState) => {
  return {
    themeDisplay: selectDisplay(state),
    theme: selectTheme(state),
    themeKey: selectThemeKey(state)
  }
}

function mapDispatchToProps(dispatch: Dispatch) {
  return bindActionCreators(
    {
      changeDisplay
    },
    dispatch
  );
}

const withConnect = connect(mapStateToProps, mapDispatchToProps);
const withReducer = injectReducer({ key: themeSliceKey, reducer: reducer });

export default compose(withConnect, withReducer)(ThemeProvider) as React.ComponentType<ComponentProps>;
