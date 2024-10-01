export interface AuthResponse {
    jwt: string,
    jwtRefreshToken: string
}

export interface SubmitEmailRequest {
    email: string;
}

export interface VerifyOtpRequest {
    phoneNumber?: string;
    email?: string | null;
    otpCode?: string;
}

export interface SubmitPasswordRequest {
    password: string;
}
