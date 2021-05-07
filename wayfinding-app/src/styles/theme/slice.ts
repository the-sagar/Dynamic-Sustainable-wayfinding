import { PayloadAction, createSelector, createSlice } from '@reduxjs/toolkit';
import { ThemeState, ThemeKeyType } from './types';
import { themes } from './themes';
import { RootState } from 'store/types';
import { Appearance } from 'react-native-appearance';

export const initialState: ThemeState = {
  selected: 'system',
  display: Appearance.getColorScheme() === 'dark' ? 'dark' : 'light'
};

const themeSlice = createSlice({
  name: 'theme',
  initialState,
  reducers: {
    changeTheme(state, action: PayloadAction<ThemeKeyType>) {
      state.selected = action.payload;
    },
    changeDisplay(state, action: PayloadAction<'light'|'dark'>) {
      state.display = action.payload;
    }
  },
});

export const selectTheme = createSelector(
  [(state: RootState) => state.theme || initialState],
  theme => {
    if (theme.selected === 'system') {
      return Appearance.getColorScheme()==='dark' ? themes.dark : themes.light;
    }
    return themes[theme.selected];
  },
);

export const selectThemeKey = createSelector(
  [(state: RootState) => state.theme || initialState],
  theme => theme.selected,
);

export const selectDisplay = createSelector(
  [(state: RootState) => state.theme || initialState],
  theme => theme.display,
);

export const { changeTheme, changeDisplay } = themeSlice.actions;
export const reducer = themeSlice.reducer;
export const themeSliceKey = themeSlice.name;

