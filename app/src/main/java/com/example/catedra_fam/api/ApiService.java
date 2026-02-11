package com.example.catedra_fam.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    /**
     * Login endpoint - NO requiere Authorization header
     * Solo Content-Type: application/json
     */
    @POST("movil/auth/login/movil")
    Call<LoginResponse> loginMovil(@Body LoginRequest request);
}
