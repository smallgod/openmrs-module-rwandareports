DELIMITER //

DROP PROCEDURE IF EXISTS sp_mamba_view_fact_paymentrefunds_report;

CREATE PROCEDURE sp_mamba_view_fact_paymentrefunds_report()
BEGIN

    SET SESSION group_concat_max_len = 20000; -- Retained, though not directly used by this view's SELECT part

    -- Drop the existing table if it exists (changed from DROP VIEW)
    DROP TABLE IF EXISTS mamba_view_fact_paymentrefunds_report;
    
    -- Create a temporary table to store the data with row numbers
    DROP TEMPORARY TABLE IF EXISTS temp_payment_refunds;
    
    CREATE TEMPORARY TABLE temp_payment_refunds AS
    SELECT
        mdpr.refund_id,
        mdpr.bill_payment_id AS payment_id,
        CONCAT(mdpn.given_name, ' ', mdpn.family_name) AS cashier_name,
        mdpr.created_date AS submitted_on,
        CONCAT(mdpn1.given_name, ' ', mdpn1.family_name) AS approvedby,
        CONCAT(mdpn.given_name, ' ', mdpn.family_name) AS confirmed_by,
        mdfsp.name AS service_name,
        mdpsb.paid_quantity AS qty_paid,
        mdpsbr.refund_quantity AS refund_qty,
        mdps.unit_price,
        COALESCE(mdpsbr.refund_reason, 'No Reason Provided') AS refund_reason,
        @row_number := IF(@current_refund = mdpr.refund_id, @row_number + 1, 1) AS rn,
        @current_refund := mdpr.refund_id
    FROM 
        mamba_dim_payment_refund mdpr
        LEFT JOIN mamba_dim_person_name mdpn ON mdpn.person_id = mdpr.refunded_by
        LEFT JOIN mamba_dim_paid_service_bill_refund mdpsbr ON mdpsbr.refund_id = mdpr.refund_id
        LEFT JOIN mamba_dim_person_name mdpn1 ON mdpn1.person_id = mdpsbr.approved_by
        LEFT JOIN mamba_dim_paid_service_bill mdpsb ON mdpsb.paid_service_bill_id = mdpsbr.paid_item_id
        LEFT JOIN mamba_dim_patient_service_bill mdps ON mdps.patient_service_bill_id = mdpsb.patient_service_bill_id
        LEFT JOIN mamba_dim_billable_service mdbs ON mdbs.billable_service_id = mdps.billable_service_id
        LEFT JOIN mamba_dim_facility_service_price mdfsp ON mdfsp.facility_service_price_id = mdbs.facility_service_price_id,
        (SELECT @row_number := 0, @current_refund := 0) AS t -- Initialize user variables
    ORDER BY 
        mdpr.refund_id, 
        mdpsbr.refund_quantity DESC;
    
    -- Create a new table with the data from the temporary table (changed from CREATE VIEW)
    -- This acts as a materialized view.
    CREATE TABLE mamba_view_fact_paymentrefunds_report AS
    SELECT
        refund_id,
        payment_id,
        cashier_name,
        submitted_on,
        approvedby,
        confirmed_by,
        service_name,
        qty_paid,
        refund_qty,
        unit_price,
        refund_reason,
        rn
    FROM temp_payment_refunds;

END //

DELIMITER ;