import React from 'react';
import { Provider } from 'react-redux'
import {AppearanceProvider} from 'react-native-appearance';
import RootNavigator from 'navigators/Root';
import { store, persister } from 'store/configureStore';
import ThemeProvider from 'styles/theme/ThemeProvider';
import { PersistGate } from 'redux-persist/integration/react';

export default function App() {
  return (
    <Provider store={store}>
      <PersistGate loading={null} persistor={persister}>
        <AppearanceProvider>
          <ThemeProvider>
            <RootNavigator />
          </ThemeProvider>
        </AppearanceProvider>
      </PersistGate>
    </Provider>
  );
};
