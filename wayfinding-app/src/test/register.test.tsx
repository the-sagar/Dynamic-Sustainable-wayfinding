import React from 'react';
import { render, fireEvent, RenderAPI } from '@testing-library/react-native';
import RegisterScreen from 'containers/RegisterScreen';
import { ThemeProvider } from 'styles/theme/MockTheme';
import { Provider } from 'react-redux';
import { store } from 'store/configureStore';

const createTestProps = (props: Object) => ({
  navigation: {
    navigate: jest.fn()
  },
  ...props
});

describe('Test case for register screen', () => {
  let props;
  let wrapper: RenderAPI;

  beforeEach(() => {
    props = createTestProps({});
    wrapper = render(
      <Provider store={store}>
        <ThemeProvider>
          <RegisterScreen {...props} />
        </ThemeProvider>
      </Provider>
    );
  });
  it('email input check',() =>
    {
      const emailInput = wrapper.getAllByA11yLabel("email");
      const submit = wrapper.getByTestId("Register");

      fireEvent.changeText(emailInput[0], 'geto');
      expect(wrapper.getAllByDisplayValue("geto")).toHaveLength(1);
      fireEvent.press(submit);
      expect(wrapper.getAllByText("Wrong email format, please try again")).toHaveLength(1);

      fireEvent.changeText(emailInput[0], 'geto@tcd.ie');
      fireEvent.press(submit);
      expect(wrapper.queryByText("Wrong email format, please try again")).toBeNull();
    })
});
