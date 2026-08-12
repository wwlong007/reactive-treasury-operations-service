package com.acme.treasury.application;
public final class AccountNotFound extends RuntimeException { public AccountNotFound(String reference) { super("cash account not found: " + reference); } }

