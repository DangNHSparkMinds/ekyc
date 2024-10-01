import { Action, configureStore, ThunkAction } from '@reduxjs/toolkit';
import authReducer from './slice/authSlice';
import commonReducer from './slice/commonSlice';
import formReducer from './slice/formSlice';
import kycReducer from './slice/kycSlide';

export const store = configureStore({
  reducer: {
    auth: authReducer,
    common: commonReducer,
    form: formReducer,
    kyc: kycReducer
  },
});

export type AppDispatch = typeof store.dispatch;
export type RootState = ReturnType<typeof store.getState>;
export type AppThunk<ReturnType = void> = ThunkAction<
  ReturnType,
  RootState,
  unknown,
  Action<string>
>;