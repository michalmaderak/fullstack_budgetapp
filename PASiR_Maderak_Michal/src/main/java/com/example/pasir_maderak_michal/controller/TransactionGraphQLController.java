package com.example.pasir_maderak_michal.controller;

import com.example.pasir_maderak_michal.dto.BalanceDto;
import com.example.pasir_maderak_michal.dto.TransactionDTO;
import com.example.pasir_maderak_michal.model.Transaction;
import com.example.pasir_maderak_michal.model.User;
import com.example.pasir_maderak_michal.service.TransactionService;
import com.example.pasir_maderak_michal.service.UserService;
import jakarta.validation.Valid;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;


import java.util.List;

@Controller
public class TransactionGraphQLController {

    private final TransactionService transactionService;
    private final UserService userService;

    public TransactionGraphQLController(TransactionService transactionService, UserService userService) {
        this.transactionService = transactionService;
        this.userService = userService;
    }

    @QueryMapping
    public List<Transaction> transactions() {
        return transactionService.getAllTransactions();
    }

    @MutationMapping
    public Transaction addTransaction(@Valid @Argument TransactionDTO transactionDTO) {
        return transactionService.createTransaction(transactionDTO);
    }

    @MutationMapping
    public Transaction updateTransaction(
            @Argument Long id,
            @Valid @Argument TransactionDTO transactionDTO
    ) {
        return transactionService.updateTransaction(id, transactionDTO);
    }
    @MutationMapping
    public Transaction deleteTransaction(@Argument Long id) {
        return transactionService.deleteTransaction(id);
    }
    @QueryMapping
    public BalanceDto userBalance(@Argument Float days) {
        User user = transactionService.getCurrentUser();
        return transactionService.getUserBalance(user, days); // Pass `days` to service
    }
}