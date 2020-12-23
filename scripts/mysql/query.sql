SELECT
    ne.id, ne.name, ne.ip, ne.site_id, ns.id, ns.name, ns.system_type_id, ns.request_limit, ns.time_limit, ns.time_unit,
    st.id, st.name
FROM ne
         JOIN ne_site ns ON ns.id = ne.site_id
         JOIN system_type st ON st.id = ns.system_type_id
WHERE ip = ?;

SELECT * FROM fault WHERE (TIME(initial_instant) >= '15:40:49.308425') AND cleared_by IS NULL LIMIT 10 OFFSET 0;

SELECT *
FROM param_code
WHERE
        `key` IN ('fm.threshold.system.value', 'fm.threshold.system.warning', 'fm.threshold.system.minor',
                  'fm.threshold.system.major', 'fm.threshold.system.critical');


SELECT ne_id, event_id, cleared_by
FROM fault
WHERE
      ne_id = ?
  AND event_id = ?
  AND trigger_instant =
      (SELECT MAX(trigger_instant) FROM fault f1 WHERE ne_id = ? AND event_id = ? GROUP BY f1.ne_id, f1.event_id);

SELECT * FROM fault WHERE cleared_by IS NULL AND event_id IS NOT NULL AND TRUE LIMIT 10 OFFSET 0;

SELECT INSTR('xbar', 'foobar');

