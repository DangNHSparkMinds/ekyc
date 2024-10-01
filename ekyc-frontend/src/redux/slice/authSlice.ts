import { createSlice } from '@reduxjs/toolkit';

interface InitialState {
  isLoggedIn?: boolean,
}

const initialState: InitialState = {
  isLoggedIn: !!localStorage.getItem('ewallet-access-token'),
};

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    resetAuthReducer: () => initialState,
    resetIsLoggedIn: (state: any) => {
      state.isLoggedIn = false;
    },
    setIsLoggedIn: (state: any) => {
      state.isLoggedIn = true;
    }
  },
});
export const { resetAuthReducer, resetIsLoggedIn, setIsLoggedIn } = authSlice.actions;

const authReducer = authSlice.reducer;
export default authReducer;
export const selectAuth = (state: any) => state.auth;
