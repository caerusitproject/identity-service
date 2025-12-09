package com.caerus.identity.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForgotPasswordEvent {
  private Long userId;
  private String email;
  private String resetLink;
  private String eventType;
  private List<String> channels;
}
