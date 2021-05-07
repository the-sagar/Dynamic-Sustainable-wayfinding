import * as React from 'react';
import { View, ViewProps } from 'react-native';
import { Switch } from 'react-native-paper';
import tailwind from 'tailwind-rn';
import Text from './Text';

type Props = {
  label: string,
  isSwitchOn: boolean,
  onToggleSwitch: (value: boolean) => void
} & ViewProps;

const LabelledSwitch: React.FC<Props> = ({label, isSwitchOn, onToggleSwitch,...rest}) => {

  return (
    <View style={tailwind("flex flex-row justify-between px-4 pt-2")} {...rest}>
      <Text>{label}</Text>
      <Switch value={isSwitchOn} onValueChange={onToggleSwitch} />
    </View>

  );
};

export default LabelledSwitch;
