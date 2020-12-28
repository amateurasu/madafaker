SELECT
    ne.id,
    ne.name,
    ne.ip,
    ne.site_id,
    ns.id,
    ns.name,
    ns.system_type_id,
    ns.request_limit,
    ns.time_limit,
    ns.time_unit,
    st.id,
    st.name
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

SELECT * FROM fault WHERE isolated <> TRUE OR isolated IS NULL;

SELECT `key`, `value` FROM param_code WHERE `key` IN (:keys);

SELECT * FROM ne WHERE id = ?;

SELECT * FROM user_notification;

SELECT * FROM event_rule RIGHT JOIN user_notification un ON event_rule.id = un.rule_id;

SELECT * FROM user_notification WHERE rule_id in (:list);

SELECT id, rule_id, name, ne_list, device_type, stop_on_error, commands FROM command_set cs WHERE
    rule_id IN (:list);

SELECT sc.id as id, cron_exp, command_set, rule_id, name, ne_list, device_type, stop_on_error, commands FROM schedule_config sc JOIN command_set cs ON sc.command_set = cs.id;
