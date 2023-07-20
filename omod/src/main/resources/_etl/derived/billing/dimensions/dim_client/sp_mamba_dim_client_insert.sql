-- $BEGIN
INSERT INTO dim_client
    (
        client_id,
        birthdate,
        age,
        sex,
        county,
        sub_county,
        ward
    )
    SELECT
        c.person_id,
        birthdate,
        fn_mamba_age_calculator(birthdate,death_date) AS age,
        CASE gender
            WHEN 'M' THEN 'Male'
            WHEN 'F' THEN 'Female'
            ELSE '_'
        END AS sex ,
        pa.county_district,
        pa.city_village,
        pa.address1
    FROM
        mamba_dim_person c
    LEFT JOIN
        mamba_dim_person_name cd
            ON c.person_id = cd.person_id
    LEFT JOIN
        mamba_dim_person_address pa on cd.person_id = pa.person_id;
-- $END
