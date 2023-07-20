-- $BEGIN
CREATE TABLE mamba_fact_insurance
(
    id                              INT            NOT NULL AUTO_INCREMENT,
    admission_date                  DATE           NULL,
    closing_date                    DATE           NULL,
    beneficiary_name                TEXT           NULL,
    household_head_name             VARCHAR(255)   NULL,
    family_code                     VARCHAR(255)   NULL,
    beneficiary_level               INT            NULL,
    card_number                     VARCHAR(255)   NULL,
    company_name                    VARCHAR(255)   NULL,
    age                             INT            NULL,
    birth_date                      DATE           NULL,
    gender                          CHAR(1)        NULL,
    doctor_name                     VARCHAR(255)   NULL,
    insurance_name                  VARCHAR(255)   NULL,
    insurance_id                    INT            NOT NULL,
    current_insurance_rate          FLOAT          NOT NULL,
    current_insurance_rate_flat_fee DECIMAL(20, 2) NULL,
    hop_service_id                  INT            NULL,
    service_bill_quantity           DECIMAL(20, 2) NULL,
    service_bill_unit_price         DECIMAL(20, 2) NOT NULL,

    PRIMARY KEY (id)
)
    CHARSET = UTF8MB4;

CREATE INDEX mamba_fact_insurance_insurance_id_index
    ON mamba_fact_insurance (insurance_id);

CREATE INDEX mamba_fact_insurance_hop_service_id_index
    ON mamba_fact_insurance (hop_service_id);

CREATE INDEX mamba_fact_insurance_closing_date_index
    ON mamba_fact_insurance (closing_date);

CREATE INDEX mamba_fact_insurance_admission_date_index
    ON mamba_fact_insurance (admission_date);

-- $END

