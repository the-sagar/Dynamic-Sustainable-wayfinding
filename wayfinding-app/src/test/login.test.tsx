import React from 'react';
import { ThemeProvider } from 'styles/theme/MockTheme';
import { render, fireEvent, RenderAPI } from '@testing-library/react-native';
import LoginScreen from 'containers/LoginScreen';
import { Provider } from 'react-redux';
import { store } from 'store/configureStore';

const createTestProps = (props: Object) => ({
  navigation: {
    navigate: jest.fn()
  },
  ...props
});

describe('Test case for login screen', () => {
  let props;
  let wrapper: RenderAPI;

  beforeEach(() => {
    props = createTestProps({});
    wrapper = render(
      <Provider store={store}>
        <ThemeProvider>
          <LoginScreen {...props} />
        </ThemeProvider>
      </Provider>
    );
  });

  it('email and password input check',() =>
    {
      const emailInput = wrapper.getByTestId("email");
      fireEvent.changeText(emailInput, 'geto@tcd.ie');
      expect(wrapper.getAllByDisplayValue('geto@tcd.ie')).toHaveLength(1);

      const passwordInput = wrapper.getByTestId("password");
      fireEvent.changeText(passwordInput, 'Password123');
      expect(wrapper.getAllByDisplayValue('Password123')).toHaveLength(1);
    })

    it("should submit the form with username, password", async () =>
    {
      const username = wrapper.getByTestId("email");
      const password = wrapper.getByTestId("password");
      const submit = wrapper.getByTestId("Login");

      fireEvent.changeText(username, "geto@tcd.ie");
      fireEvent.changeText(password, "wrongpassword");
      fireEvent.press(submit);
    });

});


