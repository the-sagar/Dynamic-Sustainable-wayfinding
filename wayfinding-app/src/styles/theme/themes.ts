import { Colors, DefaultTheme, DarkTheme } from 'react-native-paper';
import {  DarkTheme as NavigationDarkTheme,
          DefaultTheme as NavigationDefaultTheme }
          from '@react-navigation/native';

const lightTheme = {
  ...DefaultTheme,
  ...NavigationDefaultTheme,
  colors: {
    ...DefaultTheme.colors,
    ...NavigationDefaultTheme.colors,
    background: Colors.grey50,
    surface: Colors.white,
    primary: '#00a9ce',
    accent: '#97d700',
    text: Colors.blueGrey900,
    success: "#81c784",
    warning: "#ffb74d",
    error: "#e57373",
    info: "#64b5f6"
  }
};

const darkTheme: Theme = {
  ...DarkTheme,
  ...NavigationDarkTheme,
  colors: {
    ...DarkTheme.colors,
    ...NavigationDarkTheme.colors,
    background: '#1E2225',
    surface: Colors.grey800,
    primary: '#0e73b9',
    accent: '#00b2a9',
    text: Colors.grey50,
    success: "#388e3c",
    warning: "#f57c00",
    error: "#d32f2f",
    info: "#1976d2"
  }
};

export type Theme = typeof lightTheme;

export const themes = {
  light: lightTheme,
  dark: darkTheme,
};
