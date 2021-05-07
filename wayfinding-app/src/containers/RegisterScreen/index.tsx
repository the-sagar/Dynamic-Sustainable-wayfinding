import React, { useEffect, useState } from 'react';
import { compose, bindActionCreators, Dispatch } from 'redux';
import { connect } from 'react-redux';
import { injectReducer, injectSaga } from 'redux-injectors';
import { View, ScrollView } from 'react-native';
import { StackNavigationProp } from '@react-navigation/stack';
import { useTheme } from 'styled-components/native';
import { Button, ProgressBar, TextInput } from 'react-native-paper';
import { useFormField } from 'utils/FormFields';
import tailwind from 'tailwind-rn';

import Text from 'components/Text';
import { AppScreens } from 'navigators/ScreenDefs';
import PassMeter, {PASS_LABELS, MAX_LEN, MIN_LEN} from 'components/PassMeter';
import InfoBanner, { getInfoLevel } from 'components/InfoBanner';
import { authSlice, RegisterAction, rootAuthSaga, selectError } from 'store/auth';
import { RootState } from 'store/types';

type ComponentProps = {
  navigation: StackNavigationProp<any, AppScreens.Register>
};

type Props = ComponentProps & ReturnType<typeof mapStateToProps> &
ReturnType<typeof mapDispatchToProps>

const RegisterScreen: React.FC<Props> = ({navigation, lastError, RegisterAction, setError}) => {
  const emailRegexp = new RegExp(/^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/);
  const theme = useTheme();
  const emailField = useFormField();
  const passwordField = useFormField();
  const confirmPasswordField = useFormField();

  const [pwdNotMatch, setPwdNotMatch] = useState(false);
  const [emailErr, setEmailErr] = useState("");
  const [passwordErr, setPasswordErr] = useState("");
  const [isLoading, setIsLoading] = useState(false);

  useEffect(()=>{
    if(passwordField.value !== confirmPasswordField.value)
      setPwdNotMatch(true)
    else
      setPwdNotMatch(false);
  });

  useEffect(()=>{
    const successWelcome = async () => {
      await new Promise(r => setTimeout(r, 1500));
      // setError({...lastError!, show: false});
      navigation.goBack();
    }
    setIsLoading(false);
    if(lastError.code==0) { // success
      successWelcome();
    }
  }, [lastError]);

  const onSignUpClick = () => {
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
    } else if (passwordField.value.length < MIN_LEN) {
      setPasswordErr("Password is too short, please try again");
      return;
    } else if (pwdNotMatch) {
      return;
    }
    setPasswordErr("");
    setError({...lastError!, show: false});
    setIsLoading(true);
    RegisterAction({
      email: emailField.value,
      password: passwordField.value
    });
  };

  return (
    <View>
      {isLoading && <ProgressBar indeterminate color={theme.colors.accent}/>}
      <InfoBanner msg={lastError.msg} show={lastError.show}
        level={getInfoLevel(lastError?.code)} onDismiss={()=>{setError({...lastError!, show: false})}}/>
      <ScrollView>
        <Text style={tailwind('text-center text-3xl mt-8')}>Sustainable Wayfinding</Text>
        <Text style={tailwind('text-center text-2xl font-bold my-4')}>Register</Text>
        <View style={tailwind('px-8')}>
          <TextInput
            mode='outlined'
            style={tailwind('my-2')}
            label="Email"
            autoCompleteType="email"
            keyboardType="email-address"
            accessibilityLabel="email"
            {...emailField}
          />
          {emailErr !== "" && <Text style={{color: theme.colors.error}}>{emailErr}</Text>}
          <TextInput
            mode='outlined'
            style={tailwind('my-2')}
            label="Password"
            maxLength={MAX_LEN}
            secureTextEntry
            {...passwordField}
          />
          {passwordField.value.length!=0 && <View style={tailwind('px-2')}>
              <PassMeter
              showLabels
              password={passwordField.value}
              labels={PASS_LABELS}
              />
            </View>}
          {passwordErr !== "" && <Text style={{color: theme.colors.error}}>{passwordErr}</Text>}
          {/*
          // @ts-ignore */}
          <TextInput
            mode='outlined'
            style={tailwind('my-2')}
            label="Confirm password"
            maxLength={MAX_LEN}
            secureTextEntry
            {...confirmPasswordField}
          />
          {confirmPasswordField.value.length!=0 && pwdNotMatch &&
            <Text style={tailwind('text-red-600')}>Password is not matching</Text>}
          <View style={tailwind('flex-row justify-around my-4')}>
            <Button mode="outlined" onPress={()=>{navigation.goBack()}}>Back</Button>
            <Button mode="contained" onPress={onSignUpClick} testID="Register" >Sign up</Button>
          </View>
        </View>
      </ScrollView>
    </View>
  );
};

const mapStateToProps = (state: RootState) => {
  return {
    lastError: selectError(state)
  }
}

function mapDispatchToProps(dispatch: Dispatch) {
  return bindActionCreators(
    {
      RegisterAction,
      ...authSlice.actions
    },
    dispatch
  );
}

const withConnect = connect(mapStateToProps, mapDispatchToProps);
const withReducer = injectReducer({ key: authSlice.name, reducer: authSlice.reducer });
const withSaga = injectSaga({key: authSlice.name, saga: rootAuthSaga });

export default compose(withConnect, withReducer, withSaga)(RegisterScreen) as React.ComponentType<ComponentProps>;
