import {useCallback} from 'react';
import {useAppDispatch} from './common';
import { setIsSubmitting } from '@/redux/slice/formSlice';

const useSubmitForm = (callbackFn: any) => {
  const dispatch = useAppDispatch();

  return useCallback(
    async (...args: any) => {
      dispatch(setIsSubmitting(true));
      await callbackFn(...args);
      dispatch(setIsSubmitting(false));
    },
    [dispatch, callbackFn]
  );
};

export default useSubmitForm;
