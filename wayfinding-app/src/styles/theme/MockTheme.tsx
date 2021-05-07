import React from 'react';
import { ThemeProvider as SCThemeProvider } from "styled-components/native";
import { Provider as PaperProvider } from 'react-native-paper';
import { themes } from './themes';

export const ThemeProvider = (props: { children: React.ReactChild }) => {
  const theme = themes.light;
  return (
    <PaperProvider theme={theme}>
      <SCThemeProvider theme={theme}>
        {React.Children.only(props.children)}
      </SCThemeProvider>
    </PaperProvider>
  )
}
