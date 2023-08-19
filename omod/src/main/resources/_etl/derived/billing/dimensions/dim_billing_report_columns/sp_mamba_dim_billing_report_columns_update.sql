-- $BEGIN

-- IMAGING group service name
UPDATE mamba_dim_billing_report_columns
SET group_column_name = 'IMAGING'
WHERE hop_service_id IN (4, 16);

-- PROCEDURES group service name
UPDATE mamba_dim_billing_report_columns
SET group_column_name = 'PROCED.'
WHERE hop_service_id IN (19, 1, 9, 17, 12, 7);

-- $END