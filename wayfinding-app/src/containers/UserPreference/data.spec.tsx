import React from 'react';

import { ThemeProvider } from 'styles/theme/MockTheme';
import { render, fireEvent, RenderAPI } from '@testing-library/react-native';
import { Provider } from 'react-redux';
import { store } from 'store/configureStore';
import PreferenceScreen from './index';

const createTestProps = (props: Object) => ({
  navigation: {
    navigate: jest.fn()
  },
  ...props
});

describe('data test spec for user preference screen', () => {
  let props;
  let wrapper: RenderAPI;

  beforeEach(() => {
    props = createTestProps({});
    wrapper = render(
      <Provider store={store}>
        <ThemeProvider>
          <PreferenceScreen {...props} />
        </ThemeProvider>
      </Provider>
    );
  });

});


