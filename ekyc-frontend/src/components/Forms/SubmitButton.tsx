import { selectForm } from '@/redux/slice/formSlice';
import { Button, Spin } from 'antd';
import { ButtonType } from 'antd/lib/button';
import FormItem from 'antd/lib/form/FormItem';
import React, { CSSProperties, ReactNode } from 'react';
import { useSelector } from 'react-redux';

interface SubmitButtonProps {
  name: string | ReactNode;
  type?: ButtonType;
  className?: string;
  formFieldStyle?: CSSProperties;
  buttonStyle?: CSSProperties;
  buttonClassName?: string;
  isBlock?: boolean;
  disabled?: boolean;
  ref?: any;
  onClick?: () => void;
}

const SubmitButton: React.FunctionComponent<SubmitButtonProps> =
  React.forwardRef(
    (
      {
        name,
        type,
        className,
        formFieldStyle,
        buttonStyle,
        isBlock,
        buttonClassName,
        disabled,
        onClick,
      },
      ref: any
    ) => {
      const { isSubmitting } = useSelector(selectForm);

      return (
        <FormItem shouldUpdate className={className} style={formFieldStyle}>
          <Button
            type={type}
            htmlType='submit'
            disabled={isSubmitting || disabled}
            style={buttonStyle}
            block={isBlock}
            className={buttonClassName}
            ref={ref}
            onClick={onClick}
          >
            {!isSubmitting ? name : <Spin />}
          </Button>
        </FormItem>
      );
    }
  );

SubmitButton.displayName = 'SubmitButton';
export default SubmitButton;
