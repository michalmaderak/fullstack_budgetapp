package com.example.pasir_maderak_michal.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DebtDto {
    private Long debtorId;
    private Long creditorId;
    private Long groupId;
    private Double amount;
    private String title;
}
