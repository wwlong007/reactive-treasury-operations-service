package com.acme.treasury.application;
public final class DuplicateBusinessReference extends RuntimeException { public DuplicateBusinessReference(String reference) { super("business reference already exists: " + reference); } }

