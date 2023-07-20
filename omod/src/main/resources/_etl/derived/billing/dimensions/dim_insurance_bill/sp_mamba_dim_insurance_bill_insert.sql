-- $BEGIN

INSERT INTO mamba_dim_insurance_bill (insurance_bill_id,
                                      amount,
                                      created_date)
SELECT insurance_bill_id,
       amount,
       created_date
FROM moh_bill_insurance_bill;

-- $END