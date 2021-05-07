import React, { useState, useEffect } from 'react'
import { View, Animated } from 'react-native'
import PropTypes from 'prop-types'
import tailwind from 'tailwind-rn'

const regexArr = [/[a-z]/, /[A-Z]/, /[0-9]/, /[^A-Za-z0-9]/]

export const MAX_LEN = 24,
  MIN_LEN = 8,
  PASS_LABELS = ["Too Short", "Weak", "Normal", "Strong", "Secure"];

const PassMeter = props => {
    const
        [passStat, setPassStat] = useState('Weak'),
        [animateVal, setAnimateVal] = useState(new Animated.Value(0)),
        [animateColor, setAnimateColor] = useState(new Animated.Value(0)),
        [barWidth, setBarWidth] = useState(1)

    useEffect(() => {
        Animated.spring(animateVal, { useNativeDriver:false, bounciness: 15, toValue: barWidth * (props.password.length / props.maxLength) }).start()
        let passPoint = 0

        if (props.password.length > 0 && props.password.length < props.minLength)
            setPassStat(props.labels[0])
        else {
            regexArr.forEach(rgx => rgx.test(props.password) ? passPoint += 1 : null)
            setPassStat(props.labels[passPoint])
        }
        Animated.timing(animateColor, { useNativeDriver:false, toValue: passPoint, duration: 300 }).start()

    }, [props.password])

    const interpolateColor = animateColor.interpolate({
        inputRange: [0, 4],
        outputRange: ['rgb(255,0,0)', 'rgb(0, 255, 0)']
    })

    return (
        <View style={tailwind('flex -m-2')}>
            <View style={styles.backBar} onLayout={(event) => {setBarWidth(event.nativeEvent.layout.width)}}/>
            <Animated.View style={[styles.mainBar, { backgroundColor: interpolateColor, width: animateVal }]} />
            {
                props.showLabels ?
                    props.password.length != 0 ?
                        <Animated.Text style={{ margin: 1, marginTop: 1, color: interpolateColor }}>{passStat}</Animated.Text>
                        : null
                    : null
            }
        </View>
    )
}

const styles = {
    backBar: {
        backgroundColor: 'gray',
        height: 5,
        borderRadius: 25,
    },
    mainBar: {
        ...tailwind('absolute'),
        backgroundColor: 'blue',
        height: 5,
        borderRadius: 25
    }
}

PassMeter.propTypes = {
    minLength: PropTypes.number,
    showLabels: PropTypes.bool,
    maxLength: PropTypes.number,
    labels: PropTypes.array.isRequired,
    password: PropTypes.string.isRequired
}

PassMeter.defaultProps = {
    minLength: MIN_LEN,
    maxLength: MAX_LEN,
    showLabels: true
}

export default PassMeter
