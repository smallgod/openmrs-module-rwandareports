DELIMITER //

DROP PROCEDURE IF EXISTS sp_mamba_view_fact_thirdparty_report;

CREATE PROCEDURE sp_mamba_view_fact_thirdparty_report()
BEGIN
    -- Drop the view if it exists
    DROP VIEW IF EXISTS mamba_view_fact_thirdparty_report;
    
    -- Create a temporary table to calculate row numbers
    DROP TEMPORARY TABLE IF EXISTS temp_thirdparty_report;
    
    CREATE TEMPORARY TABLE temp_thirdparty_report AS
    SELECT 
        t.*,
        @row_number:=IF(@current_third_party = t.third_party_id, @row_number + 1, 1) AS rn,
        @current_third_party:=t.third_party_id
    FROM (
        SELECT
            dtp.third_party_id,
            dtp.name AS third_party_name,
            dtp.rate AS third_party_rate,
            dtp.created_date AS third_party_created_date,
            dtpb.third_party_bill_id,
            dtpb.amount AS billed_amount,
            dtpb.created_date AS bill_created_date
        FROM mamba_dim_third_party dtp
        LEFT JOIN mamba_dim_third_party_bill dtpb ON dtp.third_party_id = dtpb.third_party_bill_id
        ORDER BY dtp.third_party_id, dtpb.created_date DESC
    ) t,
    (SELECT @row_number:=0, @current_third_party:=0) AS vars;
    
    -- Now create the view based on the temporary table
    CREATE VIEW mamba_view_fact_thirdparty_report AS
    SELECT 
        third_party_id,
        third_party_name,
        third_party_rate,
        third_party_created_date,
        third_party_bill_id,
        billed_amount,
        bill_created_date,
        rn
    FROM temp_thirdparty_report;

END //

DELIMITER ;
