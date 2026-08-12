CREATE FUNCTION app_current_tenant() RETURNS uuid LANGUAGE sql STABLE PARALLEL SAFE AS $$
  SELECT NULLIF(current_setting('app.tenant_id', true), '')::uuid
$$;
REVOKE ALL ON FUNCTION app_current_tenant() FROM PUBLIC;
GRANT EXECUTE ON FUNCTION app_current_tenant() TO treasury_app;

ALTER TABLE cash_account ENABLE ROW LEVEL SECURITY;
ALTER TABLE cash_account FORCE ROW LEVEL SECURITY;
CREATE POLICY cash_account_tenant_policy ON cash_account FOR ALL TO treasury_app
  USING (tenant_id = app_current_tenant()) WITH CHECK (tenant_id = app_current_tenant());

ALTER TABLE payment_instruction ENABLE ROW LEVEL SECURITY;
ALTER TABLE payment_instruction FORCE ROW LEVEL SECURITY;
CREATE POLICY payment_instruction_tenant_policy ON payment_instruction FOR ALL TO treasury_app
  USING (tenant_id = app_current_tenant()) WITH CHECK (tenant_id = app_current_tenant());

ALTER TABLE approval_record ENABLE ROW LEVEL SECURITY;
ALTER TABLE approval_record FORCE ROW LEVEL SECURITY;
CREATE POLICY approval_record_tenant_policy ON approval_record FOR ALL TO treasury_app
  USING (tenant_id = app_current_tenant()) WITH CHECK (tenant_id = app_current_tenant());

ALTER TABLE compliance_audit ENABLE ROW LEVEL SECURITY;
ALTER TABLE compliance_audit FORCE ROW LEVEL SECURITY;
CREATE POLICY compliance_audit_tenant_policy ON compliance_audit FOR ALL TO treasury_app
  USING (tenant_id = app_current_tenant()) WITH CHECK (tenant_id = app_current_tenant());

