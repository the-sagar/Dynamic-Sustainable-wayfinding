import React from 'react';
import { ThemeProvider } from 'styles/theme/MockTheme';
import { render, fireEvent, RenderAPI } from '@testing-library/react-native';
import { Provider } from 'react-redux';
import { store } from 'store/configureStore';
import PreferenceScreen from './index';

const createTestProps = (props: Object) => ({
  navigation: {
    navigate: jest.fn(),
    addListener: jest.fn()
  },
  ...props
});

describe('main test spec for user preference screen', () => {
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

  it('have user infos',() => {
    const firstNameInput = wrapper.getByTestId("firstName");
    const lastNameInput = wrapper.getByTestId("lastName");
    const birthdayInput = wrapper.getByTestId("birthday");
  });

  it('have user preferences',() => {
    const triangleView = wrapper.getByTestId("triangle");
    const walkSwitch = wrapper.getByTestId("walkSwitch");
    const driveSwitch = wrapper.getByTestId("driveSwitch");
    const bikeSwitch = wrapper.getByTestId("bikeSwitch");
  });

});


