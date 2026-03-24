package com.CardMaster.Enum.tap;


public enum HoldStatus {
    ACTIVE,       // Hold is currently reserving funds
    RELEASED,     // Hold released without posting
    CAPTURED,     // Hold converted into a posted transaction
    EXPIRED       // Hold expired automatically
}
