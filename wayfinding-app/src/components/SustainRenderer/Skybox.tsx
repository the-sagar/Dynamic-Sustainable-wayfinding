import { useThree } from "react-three-fiber";
import { CubeTextureLoader } from "three";
import { Asset } from "expo-asset";
import { useEffect, useState } from "react";

// Loads the skybox texture and applies it to the scene.
const SkyBox: React.FC = () => {
  const { scene } = useThree();
  const loader = new CubeTextureLoader();
  const [spaceTexture, setSpaceTexture] = useState('');

  const loadSpace = async () => {
    const asset = Asset.fromModule(require("./assets/space.jpg"));
    await asset.downloadAsync();
    setSpaceTexture(asset.uri);
  };

  useEffect(() => {
    loadSpace();
  }, []);

  // The CubeTextureLoader load method takes an array of urls representing all 6 sides of the cube.
  const texture = loader.load([
    spaceTexture,
    spaceTexture,
    spaceTexture,
    spaceTexture,
    spaceTexture,
    spaceTexture,
  ]);

  // Set the scene background property to the resulting texture.
  scene.background = texture;
  return null;
};

export default SkyBox;
