import { NativeModules } from 'react-native';
const { P2PBLEService } = NativeModules

interface IP2PWrapper {
  startService: (callback: (result: boolean) => void) => void;
  stopService: (callback: (result: boolean) => void) => void;
  getIfSupport: () => boolean;
  getObject: (callback: (result: string) => void) => void;
  setValue: (item: string) => void;
}

export default P2PBLEService as IP2PWrapper;
