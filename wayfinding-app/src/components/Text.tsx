import React from 'react';
import { Text as RNText, TextProps } from 'react-native';
import styled from 'styled-components/native';

const Text: React.FC<TextProps> = ({ children, ...rest }) => {
  return (
    <StyledText {...rest}>
      {children}
    </StyledText>
  )
};

const StyledText = styled(RNText)`
  color: ${p => p.theme.colors.text};
`;

export default Text;
