package com.caerus.identity.service;

import com.caerus.identity.payload.request.AuthenticationRequest;
import com.caerus.identity.payload.request.RegisterRequest;
import com.caerus.identity.payload.response.AuthenticationResponse;

public interface AuthenticationService {
    AuthenticationResponse register(RegisterRequest request);
    AuthenticationResponse authenticate(AuthenticationRequest request);
}
