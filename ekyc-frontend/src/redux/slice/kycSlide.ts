import { createSlice } from '@reduxjs/toolkit';

interface InitialState {
  documentType?: string;
  documentData?: any;
  kycDocumentStatus: string;
  kycFaceStatus: string;
}

const initialState: InitialState = {
  kycDocumentStatus: 'PENDING',
  kycFaceStatus: 'PENDING'
};

const kycSlice = createSlice({
  name: 'kyc',
  initialState,
  reducers: {
    setKycDocumentStatus: (state: any, action: any) => {
      state.kycDocumentStatus = action;
    },
    setKycFaceStatus: (state: any, action: any) => {
      state.kycFaceStatus = action;
    },
    setKycDocumentData: (state: any, action: any) => {
      state.documentData = action.payload;
    }
  },
});
export const { setKycDocumentStatus, setKycFaceStatus, setKycDocumentData } = kycSlice.actions;

const kycReducer = kycSlice.reducer;
export default kycReducer;
export const selectKyc = (state: any) => state.kyc;
