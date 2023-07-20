DELIMITER //

DROP PROCEDURE IF EXISTS sp_mamba_fact_insurance_query;
CREATE PROCEDURE sp_mamba_fact_insurance_query(
    IN START_DATE DATETIME,
    IN END_DATE DATETIME,
    IN INSURANCE_ID INT)

BEGIN
    SELECT patient_service_bill_id,
           consommation_id,
           billable_service_id,
           service_id,
           service_date,
           unit_price,
           quantity,
           paid_quantity,
           service_other,
           service_other_description,
           is_paid,
           drug_frequency,
           item_type,
           voided,
           global_bill_id,
           department_id,
           beneficiary_id,
           patient_bill_id,
           insurance_bill_id,
           third_party_bill_id,
           admission_id,
           insurance_id,
           bill_identifier,
           global_amount,
           closing_date,
           closed,
           closed_by,
           closed_reason,
           edited_by,
           edit_reason,
           global_bill_creation_date,
           department_name,
           facility_service_price_id,
           service_category_id,
           maxima_to_pay,
           start_date,
           end_date,
           beneficary_patient_id,
           insurance_policy_id,
           policy_id_number,
           creator,
           owner_name,
           owner_code,
           level,
           company,
           patient_bill_amount,
           is_patient_bill_paid,
           status,
           insurance_bill_amount,
           third_party_bill_amount,
           third_party_id,
           insurance_card_no,
           insurance_policy_owner,
           coverage_start_date,
           expiration_date,
           insurance_company_concept,
           insurance_category,
           insurance_company_name,
           insurance_company_address,
           insurance_company_phone,
           owner_patient_id,
           third_party_name,
           third_party_rate,
           service_category_name,
           service_category_price,
           facility_location_id,
           facility_concept_id,
           facility_name,
           facility_full_price,
           beneficiary_patient_id,
           beneficiary_family_name,
           beneficiary_middle_name,
           beneficiary_given_name,
           beneficiary_birth_date,
           beneficiary_birth_date_estimated,
           beneficiary_gender,
           TIMESTAMPDIFF(YEAR, beneficiary_birth_date, CURRENT_DATE) AS age

    FROM mamba_fact_insurance i

    WHERE i.insurance_id = INSURANCE_ID
      AND i.global_bill_creation_date BETWEEN START_DATE AND END_DATE;

END //

DELIMITER ;




