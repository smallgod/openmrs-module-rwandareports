DROP PROCEDURE IF EXISTS sp_mamba_fact_patient_service_bill_flat_create;

DELIMITER //

CREATE PROCEDURE sp_mamba_fact_patient_service_bill_flat_create()
BEGIN

    SET session group_concat_max_len = 20000;
    SET @service_columns := NULL;

    SELECT GROUP_CONCAT(DISTINCT CONCAT('`', hop_service_id, '` DECIMAL(20, 2)'))
    INTO @service_columns
    FROM mamba_fact_patient_service_bill;

    IF @service_columns IS NULL THEN
        SET @create_table = CONCAT(
                'CREATE TABLE mamba_fact_patient_service_bill_flat (

                    insurance_id            INT            NOT NULL,
                    global_bill_identifier  VARCHAR(250)    NULL,
                    admission_date          DATETIME       NOT NULL,
                    closing_date            DATETIME       NULL,
                    beneficiary_name        TEXT            NULL,
                    household_head_name     VARCHAR(255)   NULL,
                    family_code             VARCHAR(255)   NULL,
                    beneficiary_level       INT             NULL,
                    card_number             VARCHAR(255)   NULL,
                    company_name            VARCHAR(255)   NULL,
                    age                     INT            NULL,
                    birth_date              DATE           NULL,
                    gender                  CHAR(1)        NULL,
                    doctor_name             VARCHAR(255)   NULL,
                    first_closing_date_id    INT            NOT NULL,
                    global_bill_id          INT            NOT NULL,

                    -- Unique constraints
                    -- constraint first_closing_date_id unique (first_closing_date_id),
                    constraint global_bill_id unique (global_bill_id),

                    -- Indexes
                    INDEX mamba_fact_patient_service_bill_flat_global_bill_index (global_bill_id),
                    INDEX mamba_fact_patient_service_bill_flat_closing_date_index (closing_date),
                    INDEX mamba_fact_patient_service_bill_flat_insurance_id_index (insurance_id),
                    INDEX mamba_fact_patient_service_bill_flat_first_closing_date_id_index (first_closing_date_id))'
            );

    ELSE
        SET @create_table = CONCAT(
                'CREATE TABLE mamba_fact_patient_service_bill_flat (

                    insurance_id            INT            NOT NULL,
                    global_bill_identifier  VARCHAR(250)    NULL,
                    admission_date          DATETIME       NOT NULL,
                    closing_date            DATETIME       NULL,
                    beneficiary_name        TEXT            NULL,
                    household_head_name     VARCHAR(255)   NULL,
                    family_code             VARCHAR(255)   NULL,
                    beneficiary_level       INT             NULL,
                    card_number             VARCHAR(255)   NULL,
                    company_name            VARCHAR(255)   NULL,
                    age                     INT            NULL,
                    birth_date              DATE           NULL,
                    gender                  CHAR(1)        NULL,
                    doctor_name             VARCHAR(255)   NULL,
                    first_closing_date_id    INT            NOT NULL,
                    global_bill_id          INT            NOT NULL,
                    ', @service_columns, ',
                -- full_100%               DECIMAL(12, 2) NULL,
                -- insurance_90%           DECIMAL(12, 2) NULL,
                -- patient_10%             DECIMAL(12, 2) NULL

                -- Unique constraints
                -- constraint first_closing_date_id unique (first_closing_date_id),
                constraint global_bill_id unique (global_bill_id),

                -- Indexes
                INDEX mamba_fact_patient_service_bill_flat_global_bill_index (global_bill_id),
                INDEX mamba_fact_patient_service_bill_flat_closing_date_index (closing_date),
                INDEX mamba_fact_patient_service_bill_flat_insurance_id_index (insurance_id),
                INDEX mamba_fact_patient_service_bill_flat_first_closing_date_id_index (first_closing_date_id))'
            );
    END IF;

    DROP TABLE IF EXISTS `mamba_fact_patient_service_bill_flat`;

    PREPARE createtb FROM @create_table;
    EXECUTE createtb;
    DEALLOCATE PREPARE createtb;

END //

DELIMITER ;