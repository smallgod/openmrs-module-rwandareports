DELIMITER //

DROP PROCEDURE IF EXISTS sp_mamba_view_fact_thirdparty_report;

CREATE PROCEDURE sp_mamba_view_fact_thirdparty_report()

BEGIN

  SET session group_concat_max_len = 20000;
  SET @thirdparty_report_columns := NULL;
  SET @imaging_report_columns := NULL;
  SET @proced_report_columns := NULL;

  -- Individual ThirdParty Columns (non-aggregated services)
  SELECT GROUP_CONCAT(DISTINCT CONCAT('IFNULL (bill.`', hop_service_id, '`, 0) AS ', '`', column_name, '`') ORDER BY
            id ASC SEPARATOR ', ')
  INTO @thirdparty_report_columns
  FROM mamba_dim_billing_report_columns
  WHERE report_type = 'THIRDPARTY'
   AND group_column_name = 'THIRDPARTY';

  -- Imaging Columns (aggregated into single IMAGING column)
  SELECT (GROUP_CONCAT(DISTINCT CONCAT('IFNULL (bill.`', hop_service_id, '`, 0)') ORDER BY
             id ASC SEPARATOR ' + ')) AS 'group_column_name'
  INTO @imaging_report_columns
  FROM mamba_dim_billing_report_columns
  WHERE report_type = 'THIRDPARTY'
   AND group_column_name = 'IMAGING';

  -- Procedure Columns (aggregated into single PROCED. column)
  SELECT GROUP_CONCAT(DISTINCT CONCAT('IFNULL (bill.`', hop_service_id, '`, 0)') ORDER BY
            id ASC SEPARATOR ' + ')
  INTO @proced_report_columns
  FROM mamba_dim_billing_report_columns
  WHERE report_type = 'THIRDPARTY'
   AND group_column_name = 'PROCED.';

  SET @select_stmt = CONCAT('CREATE OR REPLACE VIEW mamba_view_fact_thirdparty_report AS
      SELECT bill.first_closing_date_id,
          bill.admission_date,
          bill.card_number,
          bill.age,
          bill.gender,
          bill.beneficiary_name,
          bill.company_name, 
          c.rate AS insurace_rate,
          (100 - c.rate) AS patient_rate,',
          COALESCE(@thirdparty_report_columns, 'NULL AS no_thirdparty_data'), ',
          (', COALESCE(@imaging_report_columns, '0'), ') AS IMAGING,
          (', COALESCE(@proced_report_columns, '0'), ') AS PROCED,
          d.amount AS amount_100_percent,
          (d.amount * c.rate) / 100 AS insurance_amount,
          (d.amount * (100 - c.rate)) / 100 AS third_party_amount
      FROM mamba_fact_patient_service_bill_flat bill
      INNER JOIN mamba_dim_consommation b ON  bill.global_bill_id = b.global_bill_id
      INNER JOIN mamba_dim_patient_bill d ON d.patient_bill_id =b.patient_bill_id
      INNER JOIN mamba_dim_insurance_rate c ON  bill.insurance_id = c.insurance_id;'
  );

  PREPARE select_stmt FROM @select_stmt;
  EXECUTE select_stmt;
  DEALLOCATE PREPARE select_stmt;

END //

DELIMITER ;
