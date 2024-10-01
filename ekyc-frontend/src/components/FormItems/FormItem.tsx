import { BaseFormItemProps } from '@/models/common';
import { Form } from 'antd';
import React from 'react';
import { useAppSelector, useErrTranslation } from '@/hooks/common';
import { selectForm } from '@/redux/slice/formSlice';

const FormItem: React.FunctionComponent<BaseFormItemProps> = (props) => {
  const { fieldErrors } = useAppSelector(selectForm);
  const { name } = props;
  const et = useErrTranslation();
  const validateStatus = fieldErrors?.[name] ? 'error' : 'success';
  const help = fieldErrors?.[name] ? et(fieldErrors[name][0]) : null;

  return <Form.Item {...props} validateStatus={validateStatus} help={help} />;
};

export default FormItem;
