package ca.ulaval.trotti_ul.api.technician.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateTechnicianRequest(
        @NotBlank String idul,
        @NotBlank @Size(max = 100) String name,
        @NotBlank @Email String email,
        @NotBlank @Pattern(regexp = "MALE|FEMALE|OTHER", message = "gender must be MALE, FEMALE or OTHER") String gender,
        @NotBlank String dateOfBirth,
        @NotBlank @Size(min = 10, max = 100) String password
) {}
