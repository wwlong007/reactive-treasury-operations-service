package com.acme.treasury.web.security;
public final class MissingTenantException extends RuntimeException { public MissingTenantException() { super("authenticated tenant context is required"); } }

