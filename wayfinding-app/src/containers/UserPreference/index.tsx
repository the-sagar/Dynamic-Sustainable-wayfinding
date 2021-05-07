import React, { useCallback, useState, useEffect } from 'react';
import { compose, bindActionCreators, Dispatch } from 'redux';
import { connect, useStore } from 'react-redux';
import { injectSaga } from 'redux-injectors';
import { View, ScrollView, StyleSheet, TouchableOpacity, StatusBar, BackHandler, Alert } from 'react-native';
import { StackNavigationProp } from '@react-navigation/stack';
import { useTheme } from 'styled-components/native';
import { AppScreens } from 'navigators/ScreenDefs';
import { RootState } from 'store/types';
import tailwind from 'tailwind-rn';
import Text from 'components/Text';
import { Divider, Portal, TextInput } from 'react-native-paper';
import { DatePickerModalContent } from 'react-native-paper-dates';
import moment from 'moment';
import TrianglePicker from 'components/TrianglePicker';
import LabelledSwitch from 'components/LabelledSwitch';
import { preferenceSlice, preferenceSliceKey, rootPrefSaga, selectPreference, SavePrefAction } from 'store/preferences';
import { CalendarDate } from 'react-native-paper-dates/lib/typescript/src/Date/Calendar';
import { useFocusEffect } from '@react-navigation/native';

type ComponentProps = {
  navigation: StackNavigationProp<any, AppScreens.Preference>
};

type Props = ComponentProps & ReturnType<typeof mapStateToProps> &
ReturnType<typeof mapDispatchToProps>

const PreferenceScreen: React.FC<Props> = ({navigation, prefStore, SavePrefAction,
  setFirstName, setLastName, setCan, setObjective, setBirthday}) => {
  const theme = useTheme();
  const store = useStore();
  const [offset, setOffset] = useState({x:0, y:0});
  const [sustainable, setSustainable] = useState(prefStore.objective.sustainable);
  const [openBirthday, setOpenBirthday] = useState(false);
  const styles = StyleSheet.create({
    sectionText: tailwind('text-sm mt-5 mb-2 px-4'),
    textBox: tailwind('my-2 px-4'),
    textBoxDuo: tailwind('my-2 px-4 w-1/2'),
    triangle: {
      ...tailwind('flex justify-center my-2 px-4'),
      height: "40%",
      flexGrow:1,alignItems:'center',justifyContent:'center',alignSelf:'stretch'
    },
    dateModal: {
      backgroundColor: theme.colors.surface
    }
  });

  const onConfirmBirthday = useCallback(
    (params: {date: CalendarDate}) => {
      setOpenBirthday(false);
      setBirthday(moment(params.date).format('DD-MM-yyyy'));
    },
    [setOpenBirthday]
  );

  const onBack = () => {
    //@ts-ignore
    store.persister.persist();
    //@ts-ignore
    store.persister.flush();
    SavePrefAction();
    return false;
  };

  useFocusEffect(
    useCallback(() => {
      BackHandler.addEventListener('hardwareBackPress', onBack);
      return () =>
        BackHandler.removeEventListener('hardwareBackPress', onBack);
    }, [])
  );

  useEffect(
    () =>
      navigation.addListener('beforeRemove', (e) => {
        if(sustainable < 0.333) {
          // Prompt the user before leaving the screen
          e.preventDefault();
          Alert.alert(
            'Go sustainable?',
            'Your travel plan is not sustainable enough. Are you sure to save it and leave the screen?',
            [
              { text: "Change mind", style: 'cancel', onPress: () => {} },
              {
                text: 'Keep plan',
                style: 'destructive',
                // If the user confirmed, then we dispatch the action we blocked earlier
                // This will continue the action that had triggered the removal of the screen
                onPress: () => {onBack(); navigation.dispatch(e.data.action)},
              },
            ]
          );
        }
        return onBack();
      }),
    [navigation, sustainable]
  );

  return (
    <>
    <ScrollView style={tailwind('flex h-full w-full')} scrollEventThrottle={1}
      onScroll={(event) => setOffset(event.nativeEvent.contentOffset)}>
      <Text style={styles.sectionText}>About you</Text>
      <Divider />
      <View style={tailwind('flex-row justify-between')}>
        <TextInput style={styles.textBoxDuo} testID="firstName"
          label="First name" mode="outlined" textAlign="left"
          value={prefStore.firstName} onChangeText={(t)=>setFirstName(t)} />
        <TextInput style={styles.textBoxDuo} testID="lastName"
          label="Last name" mode="outlined" textAlign="left"
          value={prefStore.lastName}  onChangeText={(t)=>setLastName(t)}/>
      </View>
      <TouchableOpacity onPress={()=>setOpenBirthday(true)}>
        <TextInput style={styles.textBox} testID="birthday" editable={false} textAlign="left"
          label="Birthday" mode="outlined" value={moment(prefStore.birthDay, 'DD-MM-yyyy').format('DD-MMM-YYYY')}/>
      </TouchableOpacity>
      <Text style={styles.sectionText}>Options</Text>
      <Divider />
      <View style={tailwind("pt-4")}>
        <LabelledSwitch label="I can walk" testID="walkSwitch"
          isSwitchOn={prefStore.can.walkLong} onToggleSwitch={(v)=>setCan({cate:"walkLong", can:v})}/>
        <LabelledSwitch label="I can drive car" testID="driveSwitch"
          isSwitchOn={prefStore.can.drive} onToggleSwitch={(v)=>setCan({cate:"drive", can:v})}/>
        <LabelledSwitch label="I can ride bike" testID="bikeSwitch"
          isSwitchOn={prefStore.can.bike} onToggleSwitch={(v)=>setCan({cate:"bike", can:v})}/>
        <LabelledSwitch label="I can take public transports" testID="publicSwitch"
          isSwitchOn={prefStore.can.publicTrans} onToggleSwitch={(v)=>setCan({cate:"publicTrans", can:v})}/>
      </View>

      <Text style={styles.sectionText}>Transport</Text>
      <Divider />
      <View style={styles.triangle}>
        <TrianglePicker
          labels={[ "Sustainable", "Cost", "Time"]}
          color={theme.colors.accent}
          oldSelection={{a: prefStore.objective.sustainable, b: prefStore.objective.cost, c: prefStore.objective.time }}
          onSelected={select => {
            setSustainable(select.a);
            setObjective({sustainable: select.a, cost: select.b, time: select.c});
          }}
          style={tailwind('flex w-full h-full')}
          testID="triangle"
          offset={offset}
        />
      </View>
    </ScrollView>
    <Portal>
      {openBirthday && <View style={[StyleSheet.absoluteFill, styles.dateModal]}>
        <StatusBar
          translucent={true}
          barStyle={theme.dark ? 'dark-content' : 'light-content'}
        />
        <View
          style={[
            {
              height: StatusBar.currentHeight,
              backgroundColor: theme.colors.primary
            },
          ]}
        />
        <DatePickerModalContent
          mode="single"
          onDismiss={()=>setOpenBirthday(false)}
          date={moment(prefStore.birthDay, 'DD-MM-yyyy').toDate()}
          onConfirm={onConfirmBirthday}
          saveLabel="Confirm"
          label="Your birthday"/>
      </View>}
    </Portal>
    </>
  );
};

const mapStateToProps = (state: RootState) => {
  return {
    prefStore: selectPreference(state)
  }
}

function mapDispatchToProps(dispatch: Dispatch) {
  return bindActionCreators(
    {
      ...preferenceSlice.actions,
      SavePrefAction
    },
    dispatch
  );
}

const withConnect = connect(mapStateToProps, mapDispatchToProps);
const withSaga = injectSaga({key: preferenceSliceKey, saga: rootPrefSaga });

export default compose(withConnect, withSaga)(PreferenceScreen) as React.ComponentType<ComponentProps>;
