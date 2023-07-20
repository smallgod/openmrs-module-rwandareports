-- $BEGIN

INSERT INTO mamba_dim_department (department_id,
                                  name,
                                  description,
                                  created_date)
SELECT department_id,
       name,
       description,
       created_date
FROM moh_bill_department;

-- $END