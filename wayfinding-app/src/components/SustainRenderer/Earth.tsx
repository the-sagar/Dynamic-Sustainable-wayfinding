import React, { useRef } from "react";
import { GLTFLoader } from "utils/GLTFLoader";
import { DRACOLoader } from 'three/examples/jsm/loaders/DRACOLoader';
import { useFrame, useLoader } from "react-three-fiber";
import usePromise from "react-promise-suspense";
import { Asset } from "expo-asset";

const Earth: React.FC = () => {
  const planet = useRef<THREE.Mesh>();
  const loadModel = async () => {
    const asset = Asset.fromModule(require("./assets/earth.glb"));
    await asset.downloadAsync();
    return asset.uri;
  };

  //@ts-ignore
  const { nodes } = useLoader(GLTFLoader, usePromise(loadModel, []), loader => {
    const dracoLoader = new DRACOLoader();
    // dracoLoader.setDecoderPath('/draco-gltf/');
    loader.setDRACOLoader(dracoLoader);
   });

  useFrame(() => {
    if(planet.current)
      planet.current.rotation.y += 0.005;
  });

  return (
    <mesh
      ref={planet}
      visible
      position={[0, 0, 0]}
      geometry={nodes.Cube001?.geometry}
      material={nodes.Cube001?.material}
    />
  );
};

export default Earth;
