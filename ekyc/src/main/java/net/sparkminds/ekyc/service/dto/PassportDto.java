package net.sparkminds.ekyc.service.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PassportDto {
    private String passportNo;
    private String citizenIdNo;
    private String sureName;
    private String givenNames;
    private String nationality;
    private LocalDate dateOfBirth;
    private String placeOfBirth;
    private String gender;
    private String dateOfIssue;
    private String image;
}
