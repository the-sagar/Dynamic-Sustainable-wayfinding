const plugins = ['module:react-native-dotenv']

plugins.push([
  'module-resolver',
  {
    root: ['./src'],
    extensions: ['.js', '.ts', '.tsx', '.json']
  },
])

module.exports = function(api) {
  api.cache(true);
  return {
    presets: ['babel-preset-expo', 'module:metro-react-native-babel-preset'],
    plugins: plugins
  };
};
