import React from 'react';
import { Dialog, Portal } from 'react-native-paper';

type Props = {
  onDismiss?: () => void,
  visible: boolean
};

const AppDialog: React.FC<Props> = ({onDismiss, visible, children, ...rest}) => {

    return (
      <Portal>
        <Dialog visible={visible} onDismiss={onDismiss} {...rest}>
          {children}
        </Dialog>
      </Portal>
    );
  };

  export default AppDialog;
