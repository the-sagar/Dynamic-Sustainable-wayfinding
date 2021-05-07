import React, { useRef, useEffect } from 'react';
import moment from 'moment';
import { Text } from 'components/SustainRenderer/Text';

type ComponentProps = {
  doomsday: string,
  seconds: number
};

const Clock: React.FC<ComponentProps> = ({doomsday, seconds}) => {
  const textYMD = useRef<Text>();
  const textHMS = useRef<Text>();
  const momentDoomsday = moment(doomsday, 'DD-MM-yyyy').add(seconds, 'seconds');

  useEffect(() => {
    const id = setInterval(() => {
      const duration = moment.duration(momentDoomsday.diff(moment()));
      if(textYMD.current)
        textYMD.current.text = `${duration.years()} years, ${duration.months()} months, ${duration.days()} days`;
      if(textHMS.current)
        textHMS.current.text = moment.utc(duration.asMilliseconds()).format("HH:mm:ss");
    }, 1000);
    return () => clearInterval(id);
  }, []);

  return (
    <>
      <Text ref={textYMD}
        fontSize={50}
        position={[0, 650, 300]}>
      </Text>
      <Text ref={textHMS}
        fontSize={50}
        position={[0, 600, 300]}>
      </Text>
    </>
  );
};

export default Clock;
