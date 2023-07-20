-- $BEGIN

INSERT INTO mamba_dim_billable_service (billable_service_id,
                                        insurance_id,
                                        facility_service_price_id,
                                        service_category_id,
                                        maxima_to_pay,
                                        start_date,
                                        end_date,
                                        created_date)
SELECT billable_service_id,
       insurance_id,
       facility_service_price_id,
       service_category_id,
       maxima_to_pay,
       start_date,
       end_date,
       created_date
FROM moh_bill_billable_service;

-- $END