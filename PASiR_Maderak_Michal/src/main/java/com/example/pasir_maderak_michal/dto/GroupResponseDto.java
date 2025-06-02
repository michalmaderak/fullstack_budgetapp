package com.example.pasir_maderak_michal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GroupResponseDto {
    private Long id;
    private String name;
    private Long ownerId;
}