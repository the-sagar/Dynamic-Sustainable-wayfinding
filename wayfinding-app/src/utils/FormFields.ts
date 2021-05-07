import React, { useState, useCallback } from 'react';

export const useFormField = (initialValue: string = "") => {
  const [value, setValue] = useState(initialValue);
  const onChangeText = useCallback(
    (e: string) => setValue(e),
    []
  );
  return { value, onChangeText };
};