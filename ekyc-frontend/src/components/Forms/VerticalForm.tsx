import { removeFieldError } from '@/redux/slice/formSlice';
import {useAppDispatch} from '../../hooks/common';
import {Form, FormProps} from 'antd';
import React, {ReactNode} from 'react';

const VerticalForm: React.FunctionComponent<FormProps> = (props) => {
  const dispatch = useAppDispatch();
  const {...restProps} = props;
  return (
    <Form
      {...restProps}
      children={props.children as ReactNode}
      layout='vertical'
      autoComplete='off'
      requiredMark={false}
      scrollToFirstError
      onValuesChange={(changedValues: object) => {
        const field = Object.keys(changedValues)[0];
        dispatch(removeFieldError(field));
      }}
    />
  );
};

export default VerticalForm;
