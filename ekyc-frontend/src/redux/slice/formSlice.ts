import { BadRequestFieldError } from '@/models/http';
import {createSlice, PayloadAction} from '@reduxjs/toolkit';

interface InitialState {
  isSubmitting: boolean;
  fieldErrors: BadRequestFieldError;
}

const initialState: InitialState = {
  isSubmitting: false,
  fieldErrors: {},
};

const formSlice = createSlice({
  name: 'form',
  initialState,
  reducers: {
    setIsSubmitting: (state, action: PayloadAction<boolean>) => {
      state.isSubmitting = action.payload;
    },
    setFieldErrors: (state, action: PayloadAction<BadRequestFieldError>) => {
      state.fieldErrors = action.payload;
    },
    removeFieldError: (state, action: PayloadAction<string>) => {
      if (state.fieldErrors && state.fieldErrors[action.payload]) {
        delete state.fieldErrors[action.payload];
      }
    },
    resetFormState: () => initialState,
  },
});

export const {
  setIsSubmitting,
  setFieldErrors,
  removeFieldError,
  resetFormState,
} = formSlice.actions;

const formReducer = formSlice.reducer;
export default formReducer;
export const selectForm = (state: any) => state.form;
