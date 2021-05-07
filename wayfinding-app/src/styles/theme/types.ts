export type ThemeKeyType = 'light' | 'dark' | 'system';

export interface ThemeState {
  selected: ThemeKeyType;
  display: 'light' | 'dark';
}
