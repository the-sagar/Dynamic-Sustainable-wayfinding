// ref: https://github.com/expo/expo-three/issues/159
// fix 3d model loading on Android
import AsyncStorage from '@react-native-async-storage/async-storage';
import { TextureLoader, THREE } from 'expo-three';
import AssetUtils from 'expo-asset-utils';
import { Platform } from '@unimodules/core';

export default class ExpoTextureLoader extends TextureLoader {
  load(asset, onLoad, onProgress, onError) {
    if (!asset) {
      throw new Error('ExpoTHREE.TextureLoader.load(): Cannot parse a null asset');
    }

    let texture = new THREE.Texture();

    const loader = new THREE.ImageLoader(this.manager);
    loader.setCrossOrigin(this.crossOrigin);
    loader.setPath(this.path);

    (async () => {
      const cached = JSON.parse(await AsyncStorage.getItem(asset));
      // const cached = false;

      const nativeAsset = cached
        ? await AssetUtils.resolveAsync(cached)
        : await AssetUtils.resolveAsync(asset);

      // TODO: add hash control to update image cache
      if (!cached) {
        console.log('caching asset', asset);
        await AsyncStorage.setItem(asset, JSON.stringify(nativeAsset));
      }

      function parseAsset(image) {
        texture.image = image;

        // texture.format = THREE.RGBAFormat;
        texture.needsUpdate = true;

        if (onLoad !== undefined) {
          onLoad(texture);
        }
      }

      if (Platform.OS === 'web') {
        loader.load(
          nativeAsset.localUri,
          (image) => {
            parseAsset(image);
          },
          onProgress,
          onError,
        );
      } else {
        texture.isDataTexture = true; // Forces passing to `gl.texImage2D(...)` verbatim
        texture.minFilter = THREE.LinearFilter; // Pass-through non-power-of-two

        parseAsset({
          data: nativeAsset,
          width: nativeAsset.width,
          height: nativeAsset.height,
        });
      }
    })();

    return texture;
  }
}
