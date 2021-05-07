import { NativeModules } from 'react-native';
const { TegolaWrapper } = NativeModules

interface ITegolaWrapper {
  initClient():void;
  writeConfig(gpkg: string): void;
  startMVTServer(gpkg_bundle: string, config: string):void;
  stopMVTServer():void;
  queryState():void;
}

export default TegolaWrapper as ITegolaWrapper;
