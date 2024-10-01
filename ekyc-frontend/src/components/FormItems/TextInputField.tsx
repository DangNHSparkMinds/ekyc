import { BaseInputFieldProps } from '@/models/common';
import { Input } from 'antd';
import React from 'react';
import FormItem from './FormItem';

const TextInputField: React.FunctionComponent<BaseInputFieldProps> = (
  props
) => {
  const {
    placeholder,
    disabled,
    value,
    suffix,
    maxLength,
    readOnly,
    uppercase,
    ...rest
  } = props;

  return (
    <FormItem {...rest}>
      <Input
        placeholder={placeholder}
        disabled={disabled}
        value={value as string}
        suffix={suffix}
        maxLength={maxLength}
        readOnly={readOnly}
        style={uppercase ? { textTransform: 'uppercase' } : undefined}
      />
    </FormItem>
  );
};

export default TextInputField;
