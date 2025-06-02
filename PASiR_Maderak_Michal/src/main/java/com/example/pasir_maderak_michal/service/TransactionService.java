package com.example.pasir_maderak_michal.service;

import com.example.pasir_maderak_michal.dto.TransactionDTO;
import com.example.pasir_maderak_michal.dto.BalanceDto;
import com.example.pasir_maderak_michal.model.Transaction;
import com.example.pasir_maderak_michal.model.TransactionType;
import com.example.pasir_maderak_michal.model.User;
import com.example.pasir_maderak_michal.repository.TransactionRepository;
import com.example.pasir_maderak_michal.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private  UserRepository userRepository;

    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    public List<Transaction> getAllTransactions() {
        User user = getCurrentUser();
        return transactionRepository.findAllByUser(user);
    }

    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono transakcji o ID " + id));
    }

    public Transaction updateTransaction(Long id, TransactionDTO transactionDTO) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono transakcji o ID " + id));
        if (!transaction.getUser().getEmail().equals(getCurrentUser().getEmail())) {
            throw new SecurityException("Brak dostępu do edycji tej transakcji");
        }
        transaction.setAmount(transactionDTO.getAmount());
        transaction.setType(TransactionType.valueOf(transactionDTO.getType()));
        transaction.setTags(transactionDTO.getTags());
        transaction.setNotes(transactionDTO.getNotes());

        return transactionRepository.save(transaction);
    }
    public Transaction createTransaction(@Valid TransactionDTO transactionDTO) {
        Transaction transaction = new Transaction();
        transaction.setAmount(transactionDTO.getAmount());
        transaction.setType(TransactionType.valueOf(transactionDTO.getType()));
        transaction.setTags(transactionDTO.getTags());
        transaction.setNotes(transactionDTO.getNotes());
        transaction.setUser(getCurrentUser());
        transaction.setTimestamp(LocalDateTime.now());

        return transactionRepository.save(transaction);
    }
    public Transaction deleteTransaction(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new EntityNotFoundException("Transaction not found with ID: " + id);
        }
        transactionRepository.deleteById(id);
        return null;
    }
    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }
    public BalanceDto getUserBalance(User user, Float days) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = (days != null)
                ? now.minusDays(days.longValue())
                : null; // If `days` is null, return all transactio ns

        List<Transaction> userTransactions = (startDate != null)
                ? transactionRepository.findAllByUserAndTimestampAfter(user, startDate)
                : transactionRepository.findAllByUser(user);

        double income = userTransactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .mapToDouble(Transaction::getAmount)
                .sum();

        double expense = userTransactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .mapToDouble(Transaction::getAmount)
                .map(Math::abs)
                .sum();

        return new BalanceDto(income, expense, income - expense);
    }

}