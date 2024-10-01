import { createSlice } from '@reduxjs/toolkit';

const loadingSlice = createSlice({
  name: 'loading',
  initialState: { count: 0 },
  reducers: {
    addLoading: (state) => {
      state.count += 1;
    },
    removeLoading: (state) => {
      state.count -= 1;
    },
  },
});

export const { addLoading, removeLoading } = loadingSlice.actions;

const loadingReducer = loadingSlice.reducer;
export default loadingReducer;
