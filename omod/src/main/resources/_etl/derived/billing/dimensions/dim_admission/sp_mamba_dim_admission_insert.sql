-- $BEGIN

INSERT INTO mamba_dim_admission (admission_id,
                                 insurance_policy_id,
                                 is_admitted,
                                 admission_date,
                                 discharging_date,
                                 discharged_by,
                                 disease_type,
                                 admission_type,
                                 created_date)
SELECT admission_id,
       insurance_policy_id,
       is_admitted,
       admission_date,
       discharging_date,
       discharged_by,
       disease_type,
       admission_type,
       created_date
FROM moh_bill_admission;

-- $END