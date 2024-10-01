import { BaseInputFieldProps } from '@/models/common';
import { Input } from 'antd';
import { NamePath } from 'antd/lib/form/interface';
import React from 'react';
import FormItem from './FormItem';
import { EyeInvisibleOutlined, EyeOutlined } from '@ant-design/icons';

interface PasswordInputFieldProps extends BaseInputFieldProps {
  hasIcon?: boolean;
  dependencies?: NamePath[];
}

const PasswordInputField: React.FunctionComponent<PasswordInputFieldProps> = (
  props
) => {
  const { placeholder, value, defaultValue, onChange, disabled, maxLength, hasIcon } =
    props;

  return (
    <FormItem {...props}>
      <Input.Password
        placeholder={placeholder}
        value={value as string}
        defaultValue={defaultValue}
        onChange={onChange}
        disabled={disabled}
        maxLength={maxLength}
        iconRender={visible => (hasIcon !== false ? (visible ? <EyeOutlined /> : <EyeInvisibleOutlined />) : null)}
      />
    </FormItem>
  );
};

export default PasswordInputField;
