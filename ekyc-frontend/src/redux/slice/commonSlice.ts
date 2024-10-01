import { createSlice } from '@reduxjs/toolkit';

const commonSlice = createSlice({
  name: 'common',
  initialState: {
    redirectNotFound: false,
    redirectAccessForbidden: false,
    isRedirectLogin: false,
    userDetails: {}
  },
  reducers: {
    redirectNotFound: (state) => {
      state.redirectNotFound = true;
    },
    redirectNotFoundDone: (state) => {
      state.redirectNotFound = true;
    },
    redirectAccessForbidden: (state) => {
      state.redirectAccessForbidden = true;
    },
    resetRedirectAccessForbidden: (state) => {
      state.redirectAccessForbidden = false;
    },
    redirectLogin: (state) => {
      state.isRedirectLogin = true;
    },
    resetReducers: (state) => {
      state.isRedirectLogin = false;
    },
    setUserDetails: (state, action) => {
      state.userDetails = action;
    }
  },
});

export const {
  redirectNotFound,
  redirectNotFoundDone,
  redirectAccessForbidden,
  resetRedirectAccessForbidden,
  redirectLogin,
  resetReducers,
  setUserDetails
} = commonSlice.actions;

const commonReducer = commonSlice.reducer;
export default commonReducer;