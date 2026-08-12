CREATE TABLE tenant_registry (
  id uuid PRIMARY KEY,
  legal_name varchar(180) NOT NULL,
  status varchar(20) NOT NULL CHECK (status IN ('ACTIVE','SUSPENDED')),
  created_at timestamptz NOT NULL DEFAULT clock_timestamp()
);

CREATE TABLE cash_account (
  id uuid PRIMARY KEY, tenant_id uuid NOT NULL REFERENCES tenant_registry(id),
  account_reference varchar(80) NOT NULL, display_name varchar(140) NOT NULL,
  currency char(3) NOT NULL, available_balance numeric(19,2) NOT NULL CHECK (available_balance >= 0),
  reserved_balance numeric(19,2) NOT NULL CHECK (reserved_balance >= 0),
  status varchar(20) NOT NULL CHECK (status IN ('ACTIVE','SUSPENDED','CLOSED')),
  version bigint NOT NULL DEFAULT 0, created_at timestamptz NOT NULL, updated_at timestamptz NOT NULL,
  UNIQUE (tenant_id, account_reference), UNIQUE (tenant_id, id)
);

CREATE TABLE payment_instruction (
  id uuid PRIMARY KEY, tenant_id uuid NOT NULL REFERENCES tenant_registry(id),
  client_reference varchar(80) NOT NULL, debit_account_id uuid NOT NULL,
  beneficiary_name varchar(140) NOT NULL, beneficiary_account varchar(80) NOT NULL,
  amount numeric(19,2) NOT NULL CHECK (amount > 0), currency char(3) NOT NULL,
  status varchar(30) NOT NULL CHECK (status IN ('PENDING_RISK','AWAITING_APPROVAL','APPROVED','REJECTED','CANCELLED')),
  required_approvals integer NOT NULL CHECK (required_approvals > 0), approval_count integer NOT NULL DEFAULT 0,
  created_by varchar(160) NOT NULL, created_at timestamptz NOT NULL, updated_at timestamptz NOT NULL, version bigint NOT NULL DEFAULT 0,
  UNIQUE (tenant_id, client_reference), UNIQUE (tenant_id, id),
  FOREIGN KEY (tenant_id, debit_account_id) REFERENCES cash_account(tenant_id, id)
);

CREATE TABLE approval_record (
  id uuid PRIMARY KEY, tenant_id uuid NOT NULL REFERENCES tenant_registry(id), payment_id uuid NOT NULL,
  approver varchar(160) NOT NULL, decision varchar(20) NOT NULL CHECK (decision IN ('APPROVE','REJECT')),
  comment varchar(500) NOT NULL DEFAULT '', decided_at timestamptz NOT NULL,
  UNIQUE (tenant_id, payment_id, approver), FOREIGN KEY (tenant_id, payment_id) REFERENCES payment_instruction(tenant_id, id)
);

CREATE TABLE compliance_audit (
  id uuid PRIMARY KEY, tenant_id uuid NOT NULL REFERENCES tenant_registry(id), event_type varchar(80) NOT NULL,
  aggregate_type varchar(80) NOT NULL, aggregate_id uuid NOT NULL, actor varchar(160) NOT NULL,
  attributes jsonb NOT NULL DEFAULT '{}'::jsonb, occurred_at timestamptz NOT NULL
);

CREATE INDEX payment_instruction_tenant_created_idx ON payment_instruction(tenant_id, created_at DESC, id);
CREATE INDEX compliance_audit_tenant_occurred_idx ON compliance_audit(tenant_id, occurred_at DESC, id);
CREATE INDEX approval_record_tenant_payment_idx ON approval_record(tenant_id, payment_id, decided_at);

GRANT USAGE ON SCHEMA public TO treasury_app;
GRANT SELECT, INSERT, UPDATE, DELETE ON cash_account, payment_instruction, approval_record, compliance_audit TO treasury_app;
GRANT SELECT ON tenant_registry TO treasury_app;
