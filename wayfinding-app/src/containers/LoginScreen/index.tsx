import React, { useEffect, useState } from 'react';
import { compose, bindActionCreators, Dispatch } from 'redux';
import { ScrollView, View } from 'react-native';
import { StackNavigationProp } from '@react-navigation/stack';
import { Button, ProgressBar, TextInput } from 'react-native-paper';
import { useFormField } from 'utils/FormFields';
import tailwind from 'tailwind-rn';
import { MAX_LEN } from "components/PassMeter";

import Text from 'components/Text';
import { AppScreens } from 'navigators/ScreenDefs';
import { connect } from 'react-redux';
import { authSlice, LoginAction, rootAuthSaga, selectError, selectLoginStruct } from 'store/auth';
import { injectReducer, injectSaga } from 'redux-injectors';
import { useTheme } from 'styled-components/native';
import InfoBanner, { getInfoLevel } from 'components/InfoBanner';

type ComponentProps = {
  navigation: StackNavigationProp<any, AppScreens.Login>
};

type Props = ComponentProps & ReturnType<typeof mapStateToProps> &
ReturnType<typeof mapDispatchToProps>

const LoginScreen: React.FC<Props> = ({navigation, emailId, lastError, LoginAction, setError}) => {
  const emailRegexp = new RegExp(/^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/);
  const theme = useTheme();
  const emailField = useFormField();
  const passwordField = useFormField();
  const [emailErr, setEmailErr] = useState("");
  const [passwordErr, setPasswordErr] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  useEffect(()=>{
    setIsLoading(false);
    if(emailId) {
      navigation.navigate(AppScreens.Map);
    }
  }, [emailId, lastError])

  const onLoginClick = () => {
    // validate email address
    if(emailField.value.length === 0) {
      setEmailErr("Please input your email address");
      return;
    } else if(!emailRegexp.test(String(emailField.value).toLowerCase())) {
      setEmailErr("Wrong email format, please try again");
      return;
    }
    setEmailErr("");
    // validate password
    if(passwordField.value.length === 0) {
      setPasswordErr("Please input your password");
      return;
    }
    setPasswordErr("");
    setError({...lastError!, show: false});
    setIsLoading(true);
    LoginAction({
      email: emailField.value,
      password: passwordField.value
    })
  };

  return (
    <View>
      {isLoading && <ProgressBar indeterminate color={theme.colors.accent}/>}
      <InfoBanner msg={lastError.msg} show={lastError.show}
        level={getInfoLevel(lastError?.code)} onDismiss={()=>{setError({...lastError!, show: false})}}/>
      <ScrollView>
        <Text style={tailwind('text-center text-3xl mt-8')}>Sustainable Wayfinding</Text>
        <Text style={tailwind('text-center text-2xl font-bold my-4')}>Login</Text>
        <View style={tailwind('flex flex-col justify-center px-8')}>
          <TextInput
            mode='outlined'
            style={tailwind('my-2')}
            label="Email"
            autoCompleteType="email"
            keyboardType="email-address"
            testID="email"
            {...emailField}
          />
          {emailErr !== "" && <Text style={{color: theme.colors.error}}>{emailErr}</Text>}
          <TextInput
          mode='outlined'
          style={tailwind('my-2')}
          label="Password"
          maxLength={MAX_LEN}
          secureTextEntry
          testID="password"
          {...passwordField}
          />
          {passwordErr !== "" && <Text style={{color: theme.colors.error}}>{passwordErr}</Text>}
          <View style={tailwind('flex-row justify-around my-4')}>
            <Button mode="outlined" onPress={()=>{navigation.goBack()}}>Back</Button>
            <Button mode="contained" onPress={onLoginClick} testID="Login">Login</Button>
          </View>
        </View>
      </ScrollView>
    </View>
  );
};

const mapStateToProps = (state: any) => {
  return {
    emailId: selectLoginStruct(state)?.emailId,
    lastError: selectError(state)
  }
}

function mapDispatchToProps(dispatch: Dispatch) {
  return bindActionCreators(
    {
      LoginAction,
      ...authSlice.actions
    },
    dispatch
  );
}

const withConnect = connect(mapStateToProps, mapDispatchToProps);
const withReducer = injectReducer({ key: authSlice.name, reducer: authSlice.reducer });
const withSaga = injectSaga({key: authSlice.name, saga: rootAuthSaga });

export default compose(withConnect, withReducer, withSaga)(LoginScreen) as React.ComponentType<ComponentProps>;
