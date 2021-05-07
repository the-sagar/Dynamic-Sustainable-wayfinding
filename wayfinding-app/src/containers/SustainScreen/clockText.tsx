import React, { useState, useEffect } from 'react';
import {View} from 'react-native';
import moment from 'moment';
import Text from 'components/Text';
import tailwind from 'tailwind-rn';

type ComponentProps = {
  doomsday: string,
  seconds: number
};

const Clock: React.FC<ComponentProps> = ({doomsday, seconds}) => {
  const [textYMD, setTextYMD] = useState("");
  const [textHMS, setTextHMS] = useState("");
  const momentDoomsday = moment(doomsday, 'DD-MM-yyyy').add(seconds, 'seconds');

  useEffect(() => {
    const id = setInterval(() => {
      const duration = moment.duration(momentDoomsday.diff(moment()));
      setTextYMD(`${duration.years()} years, ${duration.months()} months, ${duration.days()} days`);
      setTextHMS(moment.utc(duration.asMilliseconds()).format("HH:mm:ss"));
    }, 1000);
    return () => clearInterval(id);
  }, []);

  return (
    <View style={tailwind('flex absolute justify-center w-full')}>
      <Text style={tailwind("text-2xl text-white text-center")}>
        {textYMD}
      </Text>
      <Text style={tailwind("text-3xl text-white text-center")}>
        {textHMS}
      </Text>
    </View>
  );
};

export default Clock;
