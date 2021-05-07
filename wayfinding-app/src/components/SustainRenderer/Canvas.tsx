import React, { Suspense } from "react";
import { Canvas as RTFCanvas } from "react-three-fiber";
import { ContainerProps } from "react-three-fiber/targets/shared/web/ResizeContainer";

interface ICanvas extends ContainerProps {
  bgColor?: [r: number, g: number, b: number];
}

const Canvas: React.FC<ICanvas> = ({
  children,
  bgColor = [0, 0, 0],
  ...rest
}) => {
  return (
    <RTFCanvas shadowMap {...rest}>
      <Suspense fallback={null}>
        <color attach={"background"} args={bgColor} />
        <directionalLight intensity={1} />
        <ambientLight intensity={0.6} />
        {children}
      </Suspense>
    </RTFCanvas>
  );
};

export { Canvas };
