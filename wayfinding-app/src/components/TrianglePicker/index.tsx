import Text from "components/Text";
import React, { useEffect, useRef, useState } from "react";
import {
  View,
  StyleSheet,
  InteractionManager,
  ViewProps,
  I18nManager,
  LayoutChangeEvent,
  LayoutRectangle,
} from "react-native";
import { createPanResponder } from "./utils";
import barycentricSolver from 'barycentric';

export type Barycentric = { a: number; b: number; c: number };

export type IPickerProps = {
  color?: string;
  oldSelection?: Barycentric;
  labels?: [top: string, bottomLeft: string, bottomRight: string];
  onSelected?: (selected: Barycentric) => void;
  offset?: {x: number, y: number};
} & ViewProps;

function getPickerProperties(pickerSize: number) {
  const triangleSize = pickerSize;
  const triangleRadius = triangleSize / 2;
  const triangleHeight = (triangleRadius * 3) / 2;
  const triangleWidth = 2 * triangleRadius * Math.sqrt(3 / 4); // pythagorean theorem

  return {
    triangleSize,
    triangleRadius,
    triangleHeight,
    triangleWidth,
  };
}

const makeComputedStyles = ({
  indicatorColor,
  pickerSize,
  selected,
  isRTL,
}) => {
  const {
    triangleHeight,
    triangleWidth,
  } = getPickerProperties(pickerSize);

  const markerColor = "rgba(0,0,0,0.8)";
  const {a, b, c} = selected;
  const padTop = (pickerSize - triangleHeight) / 2;
  const svIndicatorSize = 18;
  const svIndicatorMarginLeft = (pickerSize - triangleWidth) / 2
  const svIndicatorMarginTop = (pickerSize - (4 * triangleHeight) / 3) / 2

  const svIndicatorPoint = {
    x: svIndicatorMarginLeft + a*(triangleWidth / 2) + c*triangleWidth,
    y: svIndicatorMarginTop + a*padTop + b*(triangleHeight + padTop) + c*(triangleHeight + padTop),
  }

  return {
    picker: {
      width: pickerSize,
      height: pickerSize,
    },
    svIndicator: {
      top: svIndicatorPoint.y - svIndicatorSize / 2,
      [isRTL ? "right" : "left"]: svIndicatorPoint.x - svIndicatorSize / 2,
      width: svIndicatorSize,
      height: svIndicatorSize,
      borderRadius: svIndicatorSize / 2,
      borderColor: markerColor,
    },
    triangleContainer: {
      width: triangleWidth,
      height: triangleHeight,
    },
    triangleUnderlayingColor: {
      borderLeftWidth: triangleWidth / 2,
      borderRightWidth: triangleWidth / 2,
      borderBottomWidth: triangleHeight,
      borderBottomColor: indicatorColor,
    },
    labelUno: {
      width: "100%",
    },
    labelDuo: {
      bottom: 5,
    },
    labelTri: {
      bottom: 5,
      right: 0
    },
  };
};

const TrianglePicker: React.FC<IPickerProps> = ({
  color,
  labels,
  style,
  onSelected,
  oldSelection,
  offset,
  ...rest
}) => {
  const [pickerSize, setPickerSize] = useState(0);
  const [panXY, setPanXY] = useState<{ x: number; y: number }>();
  const [pageXY, setPageXY] = useState<{ x: number; y: number }>({x:0,y:0});
  const [_layout, setLayout] = useState<LayoutRectangle>({ width: 0, height: 0, x: 0, y: 0 });
  const [computed, setComputed] = useState<
    ReturnType<typeof makeComputedStyles>
  >();
  const [selected, setSelected] = useState<Barycentric>(oldSelection? oldSelection: {a: 0.334, b: 0.333, c:0.333})

  const refPickerContainer = useRef<View>(null);

  const _handleChange = ({ x, y }: { x: number; y: number }) => {
    const { triangleHeight, triangleWidth } = getPickerProperties(pickerSize);

    const left = pickerSize / 2 - triangleWidth / 2;
    const top = pickerSize / 2 - (2 * triangleHeight) / 3;

    const marginLeft = (_layout.width - pickerSize) / 2;
    const marginTop = (_layout.height - pickerSize) / 2;
    const relativeX = x - (pageXY.x + marginLeft + left - (offset ? offset.x : 0));
    const relativeY = y - (pageXY.y + marginTop + top - (offset ? offset.y : 0));
    const padTop = (pickerSize - triangleHeight) / 2;
    let [a, b, c] = barycentricSolver([
      [triangleWidth / 2, padTop],
      [0, triangleHeight + padTop],
      [triangleWidth, triangleHeight + padTop]],
      [relativeX, relativeY]);
    a = Math.min(Math.max(0, a), 1)
    b = Math.min(Math.max(0, b), 1)
    c = Math.min(Math.max(0, c), 1)
    if (a+b+c === 1) {
      onSelected? onSelected({a: a, b: b, c: c}): null;
      setSelected({a: a, b: b, c: c});
    }
  };

  const panResponder = useRef(
    createPanResponder({
      onStart: ({ x, y }) => {
        setPanXY({x, y});
        return true
      },
      onMove: ({ x, y }) => {
        setPanXY({x, y});
        return true
      },
    })
  ).current;

  const _onLayout = (l: LayoutChangeEvent) => {
    setLayout(l.nativeEvent.layout);
    const { width, height } = l.nativeEvent.layout;
    const newPickerSize = Math.min(width, height);
    if (pickerSize !== newPickerSize)
      setPickerSize(newPickerSize);
    InteractionManager.runAfterInteractions(() => {
      // measure only after (possible) animation ended
      refPickerContainer?.current?.measure(
        (x, y, width, height, pageX, pageY) =>
          setPageXY({x: pageX, y: pageY})
      );
    });
  };

  useEffect(() => {
    if (pickerSize) {
      setComputed(
        makeComputedStyles({
          pickerSize,
          selected: selected,
          indicatorColor: color,
          isRTL: I18nManager.isRTL,
        })
      );
    }
  }, [_layout, pickerSize, selected]);

  useEffect(() => {
    panXY && _handleChange(panXY);
  }, [panXY]);

  return (
    <View style={style} {...rest}>
      <View
        onLayout={_onLayout}
        ref={refPickerContainer}
        style={styles.pickerContainer}
      >
        {!pickerSize ? null : (
          <>
            <View
              // key={rotationHack}
              style={[styles.triangleContainer, computed?.triangleContainer]}
            >
              <View
                style={[
                  styles.triangleUnderlayingColor,
                  computed?.triangleUnderlayingColor,
                ]}
              />
            </View>
            <View
              {...panResponder.panHandlers}
              style={[styles.picker, computed?.picker]}
              collapsable={false}
            >
              {labels &&
              <>
                <Text style={[styles.labels, computed?.labelUno]}>{labels[0]}</Text>
                <Text style={[styles.labels, computed?.labelDuo]}>{labels[1]}</Text>
                <Text style={[styles.labels, computed?.labelTri]}>{labels[2]}</Text>
              </>
              }
              <View style={[styles.svIndicator, computed?.svIndicator]} />
            </View>
          </>
        )}
      </View>
      <View style={{paddingBottom:pickerSize/5}}></View>
    </View>
  );
};

const styles = StyleSheet.create({
  pickerContainer: {
    flex: 1,
    alignItems: "center",
    justifyContent: "center",
  },
  pickerIndicator: {
    position: "absolute",
    alignItems: "center",
    justifyContent: "center",
  },
  triangleContainer: {
    position: "relative",
    alignItems: "center",
  },
  triangleUnderlayingColor: {
    position: "absolute",
    top: 0,
    width: 0,
    height: 0,
    backgroundColor: "transparent",
    borderStyle: "solid",
    borderLeftColor: "transparent",
    borderRightColor: "transparent",
  },
  pickerAlignment: {
    alignItems: "center",
  },
  svIndicator: {
    position: "absolute",
    borderWidth: 4,
  },
  picker: {
    position: "absolute",
  },
  labels: {
    position: "absolute",
    textAlign: "center",
  }
});

export default TrianglePicker;
