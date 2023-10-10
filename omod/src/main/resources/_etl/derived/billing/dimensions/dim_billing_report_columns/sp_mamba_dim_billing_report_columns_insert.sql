-- $BEGIN

INSERT INTO mamba_dim_billing_report_columns (report_type,
                                              hop_service_id,
                                              column_name)
SELECT "INSURANCE",
       service_id,
       name
FROM mamba_dim_hop_service h
WHERE h.service_id in (11, 21, 6, 8, 5, 23, 2, 14, 4, 16, 19, 1, 9, 17, 12, 7)
ORDER BY FIND_IN_SET(h.service_id, '11,21,6,8,5,23,2,14,4,16,19,1,9,17,12,7');
-- heps preserve order of insertion (use on small datasets)

-- insert another type here

-- $END