import mockAsyncStorage from '@react-native-async-storage/async-storage/jest/async-storage-mock'
import 'react-native-gesture-handler/jestSetup';

jest.mock('@react-native-async-storage/async-storage', () => mockAsyncStorage)

jest.mock('react-native-reanimated', () => {
  const Reanimated = require('react-native-reanimated/mock');

  // The mock for `call` immediately calls the callback which is incorrect
  // So we override it with a no-op
  Reanimated.default.call = () => {};
  return Reanimated;
});

jest.mock('@react-navigation/native');

jest.mock('react-native-vector-icons/MaterialIcons', () => 'Icon')

jest.mock('expo-crypto', () => ({
  digestStringAsync: jest.fn((algorithm, data, options) => {
    const crypto = require('node-expo-crypto/dist/index');
    return crypto.digestStringAsync(algorithm, data, options);
  })
}));

jest.setTimeout(30000);
