package com.CardMaster.service.bsp;

import com.CardMaster.Enum.bsp.StatementStatus;
import com.CardMaster.Enum.tap.TransactionStatus;
import com.CardMaster.dao.bsp.StatementRepository;
import com.CardMaster.dao.cias.CardAccountRepository;
import com.CardMaster.dao.tap.TransactionRepository;
import com.CardMaster.exceptions.bsp.StatementNotFoundException;
import com.CardMaster.model.bsp.Statement;
import com.CardMaster.model.cias.CardAccount;
import com.CardMaster.model.tap.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatementService {

    private final StatementRepository statementRepository;
    private final CardAccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public Statement generateStatement(Statement statement) {
        Long accountId = statement.getAccount().getAccountId();
        CardAccount account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + accountId));

        statementRepository.findFirstByAccount_AccountIdAndStatusOrderByGeneratedDateDesc(accountId, StatementStatus.OPEN)
                .ifPresent(existing -> {
                    throw new IllegalStateException("Close the existing OPEN statement before generating a new one");
                });

        LocalDate periodStart = statement.getPeriodStart();
        LocalDate periodEnd = statement.getPeriodEnd();
        if (periodStart == null || periodEnd == null || periodEnd.isBefore(periodStart)) {
            throw new IllegalArgumentException("Valid statement period is required");
        }

        List<Transaction> transactions = transactionRepository.findByAccount_AccountIdAndTransactionDateBetween(
                accountId,
                periodStart.atStartOfDay(),
                periodEnd.atTime(23, 59, 59)
        );

        double totalDue = transactions.stream()
                .filter(tx -> tx.getStatus() == TransactionStatus.POSTED)
                .mapToDouble(Transaction::getAmount)
                .sum();

        statement.setAccount(account);
        statement.setGeneratedDate(LocalDate.now());
        statement.setTotalDue(totalDue);
        statement.setMinimumDue(totalDue == 0 ? 0.0 : Math.min(totalDue, Math.max(100.0, totalDue * 0.1)));
        statement.setStatus(StatementStatus.OPEN);

        return statementRepository.save(statement);
    }

    @Transactional
    public Statement closeStatement(Long statementId) {
        Statement statement = getById(statementId);
        statement.setStatus(StatementStatus.CLOSED);
        if (statement.getTotalDue() < 0) {
            statement.setTotalDue(0.0);
        }
        if (statement.getMinimumDue() < 0) {
            statement.setMinimumDue(0.0);
        }
        return statementRepository.save(statement);
    }

    @Transactional(readOnly = true)
    public Statement getById(Long id) {
        return statementRepository.findById(id)
                .orElseThrow(() -> new StatementNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<Statement> listAll() {
        return statementRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Statement> listByEmail(String email, LocalDate fromDate, LocalDate toDate) {
        return accountRepository.findByCardCustomerContactInfoEmail(email)
                .map(account -> {
                    if (fromDate != null && toDate != null) {
                        return statementRepository.findByAccount_AccountIdAndGeneratedDateBetweenOrderByGeneratedDateDesc(account.getAccountId(), fromDate, toDate);
                    }
                    return statementRepository.findByAccount_AccountIdOrderByGeneratedDateDesc(account.getAccountId());
                })
                .orElse(List.of());
    }
}
