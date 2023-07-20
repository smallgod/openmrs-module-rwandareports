-- $BEGIN

INSERT INTO mamba_dim_hop_service (service_id,
                                   name,
                                   description,
                                   created_date)
SELECT service_id,
       name,
       description,
       created_date
FROM moh_bill_hop_service;

-- $END