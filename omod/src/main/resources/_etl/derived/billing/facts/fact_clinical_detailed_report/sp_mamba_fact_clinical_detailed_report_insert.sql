-- $BEGIN

INSERT INTO mamba_fact_clinical_detailed_report(patient_id,
                                                given_name,
                                                family_name,
                                                household_name,
                                                gender,
                                                birth_date,
                                                age,
                                                age_at_encounter,
                                                weight,
                                                height,
                                                temperature,
                                                country,
                                                province,
                                                district,
                                                sector,
                                                cell,
                                                umudugudu,
                                                case_status,
                                                catchment_area,
                                                encounter_datetime,
                                                chief_complaint,
                                                treatment,
                                                provider_name,
                                                visit_type,
                                                primary_diagnosis,
                                                secondary_diagnosis,
                                                presumptive_diagnosis,
                                                admission_service,
                                                insurance,
                                                type_of_discharge,
                                                department)

SELECT bps.person_id         AS patient_id,
       bps.person_name_short AS given_name,
       bps.person_name_long  AS family_name,
       ben.owner_name        AS household_name,
       bps.gender            AS gender,
       bps.birthdate         AS birth_date,
       bps.age               AS age,
       NULL                  AS age_at_encounter,      -- Calculate from encounter_datetime and birthdate
       NULL                  AS weight,                -- Get from obs/vitals dimension
       NULL                  AS height,                -- Get from obs/vitals dimension
       NULL                  AS temperature,           -- Get from obs/vitals dimension
       pa.country            AS country,               -- Verify person_address availability
       pa.state_province     AS province,              -- Verify person_address availability
       pa.county_district    AS district,              -- Verify person_address availability
       pa.address3           AS sector,                -- Verify person_address availability
       pa.address1           AS cell,                  -- Verify person_address availability
       pa.address2           AS umudugudu,             -- Verify person_address availability
       NULL                  AS case_status,           -- Identify source table
       NULL                  AS catchment_area,        -- Identify source table
       NULL                  AS encounter_datetime,    -- Join with encounter dimension
       NULL                  AS chief_complaint,       -- Get from obs dimension
       NULL                  AS treatment,             -- Get from obs dimension
       NULL                  AS provider_name,         -- Get from encounter/provider dimension
       NULL                  AS visit_type,            -- Get from visit dimension
       NULL                  AS primary_diagnosis,     -- Get from obs/diagnosis dimension
       NULL                  AS secondary_diagnosis,   -- Get from obs/diagnosis dimension
       NULL                  AS presumptive_diagnosis, -- Get from obs/diagnosis dimension
       adm.admission_service AS admission_service,     -- From dim_admission
       ins.name              AS insurance,             -- Insurance name, not ID
       adm.discharge_type    AS type_of_discharge,     -- From dim_admission
       dept.name             AS department             -- From dim_department

FROM mamba_dim_beneficiary ben
         INNER JOIN mamba_dim_person bps ON bps.person_id = ben.patient_id
         LEFT JOIN mamba_dim_person_address pa ON pa.person_id = bps.person_id AND pa.voided = 0 AND pa.preferred = 1
         INNER JOIN mamba_dim_insurance_policy isp ON ben.insurance_policy_id = isp.insurance_policy_id
         INNER JOIN mamba_dim_insurance ins ON ins.insurance_id = isp.insurance_id
         LEFT JOIN mamba_dim_admission adm ON adm.patient_id = bps.person_id
         LEFT JOIN mamba_dim_department dept ON dept.department_id = adm.department_id
WHERE ben.voided = 0;

-- $END