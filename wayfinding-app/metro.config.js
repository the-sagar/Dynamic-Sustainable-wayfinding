const defaultAssetExts = require("metro-config/src/defaults/defaults").assetExts;

module.exports = {
  transformer: {
    assetPlugins: ['expo-asset/tools/hashAssetFiles'],
  },
  resolver: {
    assetExts: [
      ...defaultAssetExts,
      "glb",
      "gltf",
      "png",
      "mp4"
    ]
  }
};
