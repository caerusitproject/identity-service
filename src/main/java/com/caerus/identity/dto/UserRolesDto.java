package com.caerus.identity.dto;

import java.util.Set;

public record UserRolesDto(String email, Set<String> roles) {}
