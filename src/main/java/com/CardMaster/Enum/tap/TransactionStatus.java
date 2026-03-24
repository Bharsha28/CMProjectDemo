package com.CardMaster.Enum.tap;

public enum TransactionStatus {
    AUTHORIZED,   // Transaction authorized, funds reserved
    POSTED,       // Transaction captured/settled
    FAILED,       // Authorization or posting failed
    REVERSED      // Transaction reversed or voided
}
