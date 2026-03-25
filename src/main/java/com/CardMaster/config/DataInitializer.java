package com.CardMaster.config;

import com.CardMaster.Enum.cpl.CardCategory;
import com.CardMaster.Enum.cpl.ProductStatus;
import com.CardMaster.Enum.iam.UserEnum;
import com.CardMaster.dao.cpl.CardProductRepository;
import com.CardMaster.dao.iam.AuditLogRepository;
import com.CardMaster.dao.iam.UserRepository;
import com.CardMaster.model.cpl.CardProduct;
import com.CardMaster.model.iam.AuditLog;
import com.CardMaster.model.iam.User;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CardProductRepository cardProductRepository;
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;

    @Override
    public void run(String... args) {
        if (cardProductRepository.count() == 0) {
            cardProductRepository.save(CardProduct.builder()
                    .name("Classic Visa")
                    .category(CardCategory.Standard)
                    .interestRate(18.0)
                    .annualFee(500.0)
                    .status(ProductStatus.ACTIVE)
                    .maxCreditLimit(100000.0)
                    .build());

            cardProductRepository.save(CardProduct.builder()
                    .name("Gold MasterCard")
                    .category(CardCategory.Gold)
                    .interestRate(22.0)
                    .annualFee(1500.0)
                    .status(ProductStatus.ACTIVE)
                    .maxCreditLimit(250000.0)
                    .build());

            cardProductRepository.save(CardProduct.builder()
                    .name("Platinum Rewards")
                    .category(CardCategory.Platinum)
                    .interestRate(28.0)
                    .annualFee(3000.0)
                    .status(ProductStatus.ACTIVE)
                    .maxCreditLimit(500000.0)
                    .build());

            System.out.println("[DataInitializer] Seeded 3 default card products.");
        }

        if (auditLogRepository.count() == 0) {
            User admin = userRepository.findByEmail("admin@new.com").orElseGet(() -> {
                User u = new User();
                u.setEmail("admin@new.com");
                u.setName("Admin User");
                u.setPassword("admin123"); // Should be encoded in a real app
                u.setRole(UserEnum.ADMIN);
                return userRepository.save(u);
            });

            auditLogRepository.save(AuditLog.builder()
                    .user(admin)
                    .action("LOGIN_SUCCESS")
                    .resource("AUTH")
                    .metadata("Login from localhost")
                    .build());

            auditLogRepository.save(AuditLog.builder()
                    .user(admin)
                    .action("CREATE_PRODUCT")
                    .resource("CARD_PRODUCT")
                    .metadata("Added Platinum Card")
                    .build());

            auditLogRepository.save(AuditLog.builder()
                    .user(admin)
                    .action("UPDATE_FEE")
                    .resource("FEE_CONFIG")
                    .metadata("Changed Annual Fee for Gold")
                    .build());

            System.out.println("[DataInitializer] Seeded 3 audit logs.");
        }

        seedSampleBillingData();
    }

    private final com.CardMaster.dao.cias.CardAccountRepository cardAccountRepository;
    private final com.CardMaster.dao.tap.TransactionRepository transactionRepository;
    private final com.CardMaster.dao.bsp.StatementRepository statementRepository;
    private final com.CardMaster.dao.bsp.PaymentRepository paymentRepository;

    private void seedSampleBillingData() {
        if (transactionRepository.count() > 0)
            return;

        userRepository.findByEmail("customer1@gmail.com").ifPresent(user -> {
            cardAccountRepository.findByCardCustomerContactInfoEmail(user.getEmail()).ifPresent(account -> {
                // 1. Create Sample Transactions
                com.CardMaster.model.tap.Transaction t1 = new com.CardMaster.model.tap.Transaction();
                t1.setAccount(account);
                t1.setAmount(120.0);
                t1.setCurrency("USD");
                t1.setMerchant("Amazon.com");
                t1.setChannel(com.CardMaster.Enum.tap.TransactionChannel.ONLINE);
                t1.setTransactionDate(java.time.LocalDateTime.now().minusDays(5));
                t1.setStatus(com.CardMaster.Enum.tap.TransactionStatus.POSTED);
                transactionRepository.save(t1);

                com.CardMaster.model.tap.Transaction t2 = new com.CardMaster.model.tap.Transaction();
                t2.setAccount(account);
                t2.setAmount(50.0);
                t2.setCurrency("USD");
                t2.setMerchant("Starbucks Coffee");
                t2.setChannel(com.CardMaster.Enum.tap.TransactionChannel.POS);
                t2.setTransactionDate(java.time.LocalDateTime.now().minusDays(3));
                t2.setStatus(com.CardMaster.Enum.tap.TransactionStatus.POSTED);
                transactionRepository.save(t2);

                com.CardMaster.model.tap.Transaction t3 = new com.CardMaster.model.tap.Transaction();
                t3.setAccount(account);
                t3.setAmount(30.0);
                t3.setCurrency("USD");
                t3.setMerchant("Uber Trip");
                t3.setChannel(com.CardMaster.Enum.tap.TransactionChannel.ONLINE);
                t3.setTransactionDate(java.time.LocalDateTime.now().minusMinutes(30));
                t3.setStatus(com.CardMaster.Enum.tap.TransactionStatus.AUTHORIZED);
                transactionRepository.save(t3);

                // 2. Create Sample Statement
                com.CardMaster.model.bsp.Statement st = new com.CardMaster.model.bsp.Statement();
                st.setAccount(account);
                st.setPeriodStart(java.time.LocalDate.now().minusMonths(1));
                st.setPeriodEnd(java.time.LocalDate.now());
                st.setTotalDue(170.0);
                st.setMinimumDue(25.0);
                st.setGeneratedDate(java.time.LocalDate.now());
                st.setStatus(com.CardMaster.Enum.bsp.StatementStatus.OPEN);
                com.CardMaster.model.bsp.Statement savedStatement = statementRepository.save(st);

                // 3. Create Sample Payment
                com.CardMaster.model.bsp.Payment p = new com.CardMaster.model.bsp.Payment();
                p.setAccount(account);
                p.setStatement(savedStatement);
                p.setAmount(50.0);
                p.setPaymentDate(java.time.LocalDateTime.now().minusDays(1));
                p.setMethod(com.CardMaster.Enum.bsp.PaymentMethod.NETBANKING);
                p.setStatus(com.CardMaster.Enum.bsp.PaymentStatus.COMPLETED);
                paymentRepository.save(p);

                // 4. Update Account Balance (availableLimit = creditLimit - posted - authorized
                // + payments)
                // 10000 - 120 - 50 - 30 + 50 = 9850
                account.setAvailableLimit(account.getCreditLimit() - 170.0 - 30.0 + 50.0);
                cardAccountRepository.save(account);

                System.out.println("[DataInitializer] Seeded sample billing data for customer1@gmail.com");
            });
        });
    }
}
