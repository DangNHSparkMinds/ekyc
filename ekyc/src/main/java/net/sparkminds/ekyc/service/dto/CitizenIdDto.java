package net.sparkminds.ekyc.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
public class CitizenIdDto {

    private String documentType;

    private String number;

    private String fullName;

    private String gender;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate dateOfBirth;

    private String nationality;

    private String placeOfOrigin;

    private String placeOfResidence;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private LocalDate dateOfExpiry;

    private String frontSideImage;

    private String backSideImage;

    @JsonIgnore
    private List<String> unclearProperties;
}
