USE fm;


INSERT INTO system_type(id, name) VALUES (1, '5G_ACCESS'), (2, '5G_CORE'), (3, 'EMS'), (4, 'OCS');


INSERT INTO ne_site(id, system_type_id, name, request_limit, time_limit, time_unit)
VALUES
    (1, 1, '5GA', 200, 1, 'MINUTES'),
    (2, 2, '5GC', 200, 1, 'MINUTES'),
    (3, 3, 'EMS', 200, 1, 'MINUTES'),
    (4, 4, 'OCS', 200, 1, 'MINUTES');


INSERT INTO user (id, full_name, email, phone_number)
VALUES
    ('binhdt8', 'Đặng Thanh Bình', 'binhdt8@vttek.vn', '0982088987'),
    ('diennd2', 'Nguyễn Đức Diễn', 'diennd2@vttek.vn', '84979178623'),
    ('duclm22', 'Lê Minh Đức', 'duclm22@vttek.vn', '0365819777'),
    ('ducnt48', 'Nguyễn Trọng Đức', 'ducnt48@vttek.vn', '0963671495'),
    ('dungpka', 'Phạm Kim Anh Dũng', 'dungpka@vttek.vn', '0987070066'),
    ('dungtx', 'Trần Xuân Dũng', 'dungtx@vttek.vn', '84976319888'),
    ('gianglt', 'Lê Trường Giang', 'gianglt@vttek.vn', '84979799566'),
    ('giangtm1', 'Trần Minh Giang', 'giangtm1@vttek.vn', '84829921995'),
    ('haolx', 'Lương Xuân Hào', 'haolx@vttek.vn', '0982999388'),
    ('hieppq2', 'Phạm Quang Hiệp', 'hieppq2@vttek.vn', '0966951986'),
    ('hungbv9', 'Bùi Việt Hùng', 'hungbv9@vttek.vn', '0986899177'),
    ('linhnc', 'Nguyễn Chí Linh', 'linhnc@vttek.vn', '84988006696'),
    ('longvt', 'Vũ Trường Long', 'longvt@vttek.vn', '0963630088'),
    ('lucnt14', 'Nguyễn Tiến Lực', 'lucnt14@vttek.vn', '84349632926'),
    ('namnd27', 'Nguyễn Đình Nam ', 'namnd27@viettel.com.vn', '84398553820'),
    ('quandv', 'Đinh Viết Quân', 'quandv@viettel.com.vn', '0389828181'),
    ('thangnx6', 'Nguyễn Xuân Thắng', 'thangnx6@vttek.vn', '84975797642'),
    ('tiennt12', 'Nguyễn Trung Tiến', 'tiennt12@vttek.vn', '84975542578'),
    ('trunglq7', 'Lê Quốc Trung', 'trunglq7@vttek.vn', '84384834194'),
    ('truyenhdh', 'Hoàng Đinh Hải Truyền', 'truyenhdh@vttek.vn', '84983989294'),
    ('tuandn3', 'Đỗ Ngọc Tuấn', 'tuandn3@vttek.vn', '84962219966'),
    ('tungtv8', 'Trần Văn Tùng', 'tungtv8@vttek.vn', '84976409191'),
    ('tuyennt11', 'Nguyễn Thị Tuyền', 'tuyennt11@vttek.vn', '84979568128');

INSERT INTO event_category (site_id, code, name, timeout, is_fault)
VALUES
    (1, '1002', 'CELL SETUP FAILURE', 0, TRUE),
    (1, '1003', 'INVALID CONFIGURATION', 0, TRUE),
    (1, '1004', 'CELL START FAILURE', 0, TRUE),
    (1, '1006', 'X2AP CONNECTION FAILURE', 0, TRUE),
    (1, '1007', 'SERVICE PROCESS DOWN', 0, TRUE),
    (1, '1009', 'START PROCESS FAILURE', 0, TRUE),
    (1, '1010', 'OAM PROCESS DOWN', 0, TRUE),
    (1, '2007', 'PA TEMPERATURE HIGH', 0, TRUE),
    (1, '2008', 'RRU POWER TEMPERATURE HIGH', 0, TRUE),
    (1, '2009', 'RRU POWER TEMPERATURE LOW', 0, TRUE),
    (1, '2010', 'RRU TRX BOARD TEMPERATURE HIGH', 0, TRUE),
    (1, '2011', 'RRU TRX BOARD TEMPERATURE LOW', 0, TRUE),
    (1, '2012', 'RRU CPU TEMPERATURE HIGH', 0, TRUE),
    (1, '2013', 'RRU CPU TEMPERATURE LOW', 0, TRUE),
    (1, '2021', 'RRU SFP NOT PRESENT', 0, TRUE),
    (1, '2033', 'RRU 12V VOLTAGE HIGH', 0, TRUE),
    (1, '2034', 'PA BIAS EXEC FAIL', 0, TRUE),
    (1, '2035', 'PA POWER IN LOW', 0, TRUE),
    (1, '2036', 'PA POWER IN HIGH', 0, TRUE),
    (1, '2037', 'PA POWER GAIN LOW', 0, TRUE),
    (1, '2038', 'PA POWER GAIN HIGH', 0, TRUE),
    (1, '2039', 'PA CURRENT HIGH', 0, TRUE),
    (1, '5001', 'L1 L2 OUT OF SYNC', 0, TRUE),
    (1, '10000', 'HEARTBEAT FAILURE', 0, TRUE),
    (1, '10002', 'EMAIL_PROBLEMS', 0, TRUE),
    (1, '10005', 'RRU CPU TEMPERATURE LOW', 0, TRUE),
    (1, '20000', 'THRESHOLD KPI COUNTER', 0, TRUE);


INSERT INTO event_category (site_id, code, name, timeout, is_fault)
VALUES
    (1, '2048', 'GNODEB DOWN', 0, FALSE),
    (1, '16100', 'NE STARTUP', 0, FALSE),
    (1, '16101', 'NE RESTART', 0, FALSE),
    (1, '9100', 'CELL STARTED', 0, FALSE),
    (1, '9101', 'CELL STOPPED', 0, FALSE),
    (1, '9102', 'CELL DELETED', 0, FALSE);


INSERT INTO event_category (site_id, code, name, timeout, is_fault)
VALUES
    (1, 'TA00000', 'ALARM! YOU DON\'T KNOW ME', 0, TRUE),
    (1, 'TA00001', 'ALARM! HANDLE ME', 0, TRUE),
    (1, 'TA00002', 'ALARM! NOTIFY ABOUT ME', 0, TRUE),
    (1, 'TA00003', 'ALARM! ISOLATE ME', 0, TRUE);


INSERT INTO event_category (site_id, code, name, timeout, is_fault)
VALUES
    (1, 'TE00000', 'EVENT! YOU DON\'T KNOW ME', 0, FALSE),
    (1, 'TE00001', 'EVENT! HANDLE ME', 0, FALSE),
    (1, 'TE00002', 'EVENT! NOTIFY ABOUT ME', 0, FALSE),
    (1, 'TE00003', 'EVENT! ISOLATE ME', 0, FALSE);


INSERT INTO event_rule(id, name, type, enabled, effective_time, `condition`)
VALUES
    (1, 'Work hour notification', 'notification', TRUE, NULL, ''),
    (2, 'Home notification', 'notification', TRUE, NULL, '');


INSERT INTO user_notification (rule_id, user_id, active, method)
VALUES
    (1, 'duclm22', TRUE, 'SMS'),
    (2, 'duclm22', TRUE, 'MAIL');


INSERT INTO ne (name, site_id, ip)
VALUES
    ('local', 1, '127.0.0.1'),
    ('test', 1, '192.168.8.101'),
    ('gHI04305', 1, '10.171.135.137'),
    ('gHI04305B', 1, '10.171.134.25');


INSERT INTO param_code(`key`, value, description)
VALUES
('fm.default.message', '[{ne}] {name} ({id}) Location: {interface} Problem: {specific_problem}. Time: {trigger_instant}. Message: {severity}', 'Default format for sending users notification messages.'),
('fm.threshold.system.value', '{"value": 20000, "time_limit": 1, "time_unit": "MINUTES"}', 'Threshold value for the whole system'),

('fm.threshold.system.warning', '{"percentage": 90, "alarm_id": 50}', 'Warning threshold by system'),
('fm.threshold.system.minor', '{"percentage": 95, "alarm_id": 50}', 'Minor threshold by system'),
('fm.threshold.system.major', '{"percentage": 97.5, "alarm_id": 50}', 'Major threshold by system'),
('fm.threshold.system.critical', '{"percentage": 100, "alarm_id": 50}', 'Critical threshold by system'),

('fm.threshold.ne.warning', '{"percentage": 90, "alarm_id": 50}', 'Warning threshold by NE'),
('fm.threshold.ne.minor', '{"percentage": 95, "alarm_id": 50}', 'Minor threshold by NE'),
('fm.threshold.ne.major', '{"percentage": 97.5, "alarm_id": 50}', 'Major threshold by NE'),
('fm.threshold.ne.critical', '{"percentage": 100, "alarm_id": 50}', 'Critical threshold by NE');


INSERT INTO fault (ne_id, event_code, severity, ems_severity, sequence, interface, source_type, specific_problem, isolated, isolation_history, event_id, event_name, event_type, internal_header_fields, nfc_naming_code, nf_naming_code, nf_vendor_name, priority, reporting_entity_id, reporting_entity_name, source_id, source_name, stnd_defined_namespace, version, ves_event_listener_version, alarm_condition, event_category, fault_fields_version, vf_status, additional_info)
VALUES
    # (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    (1, 'TA00001', 'MAJOR', NULL, 12, 'interface', 'source_type', 'specific_problem', TRUE, 'isolation_history', 35, 'event_name', 'event_type', 'internal_header_fields', 'nfc_naming_code', 'nf_naming_code', 'nf_vendor_name', 'HIGH', 'reporting_entity_id', 'reporting_entity_name', 'source_id', 'source_name', 'stnd_defined_namespace', '1.0', '7.2', 'alarm_condition', 'event_category', '1.0', 'vf_status', 'additional_field'),
    (1, 'TA00001', 'MAJOR', NULL, 12, 'interface', 'source_type', 'specific_problem', TRUE, 'isolation_history', 36, 'event_name', 'event_type', 'internal_header_fields', 'nfc_naming_code', 'nf_naming_code', 'nf_vendor_name', 'HIGH', 'reporting_entity_id', 'reporting_entity_name', 'source_id', 'source_name', 'stnd_defined_namespace', '1.0', '7.2', 'alarm_condition', 'event_category', '1.0', 'vf_status', 'additional_field');

INSERT INTO event_rule (id, name, type, enabled, effective_time, `condition`)
VALUES
(1, 'Rule 1', 'notification', 1, NULL, '[{"start":["event_code","in",["1002","1003","1004","1006"]]}]'),
(2, 'Rule 2', 'notification', 1, NULL, '[{"start":["event_code","in",["1007","1010"]]}]'),
(3, 'Rule 3', 'notification', 1, NULL, '[{"start":["event_code","in",["2007","2008","2009","2010","2011","2012","2013","2021","2033","2034","2035","2036","2037","2038","2039","2048"]]}]'),
(4, 'Rule 4', 'notification', 1, NULL, '[{"start":["event_code","in",["5001","10000"]]}]');

INSERT INTO event_rule(id, name, type, enabled, effective_time, `condition`)
VALUES
    (5, 'Rule test isolation', 'isolation', TRUE, NULL, '[{"start":["event_code","in",["TA00003", "TE00003"]]}]');

INSERT INTO user_notification (rule_id, user_id, active, method)
VALUES
    (1, 'binhdt8', 1, 'SMS'),
    (1, 'duclm22', 1, 'ALL'),
    (1, 'ducnt48', 1, 'SMS'),
    (1, 'dungpka', 1, 'SMS'),
    (1, 'dungtx', 1, 'SMS'),
    (1, 'gianglt', 1, 'SMS'),
    (1, 'giangtm1', 1, 'SMS'),
    (1, 'haolx', 1, 'SMS'),
    (1, 'hieppq2', 1, 'SMS'),
    (1, 'hungbv9', 1, 'SMS'),
    (1, 'linhnc', 1, 'SMS'),
    (1, 'longvt', 1, 'SMS'),
    (1, 'lucnt14', 1, 'SMS'),
    (1, 'thangnx6', 1, 'SMS'),
    (1, 'tiennt12', 1, 'SMS'),
    (1, 'truyenhdh', 1, 'SMS'),
    (1, 'tuandn3', 1, 'SMS'),
    (1, 'tungtv8', 1, 'SMS'),
    (2, 'binhdt8', 1, 'SMS'),
    (2, 'duclm22', 1, 'ALL'),
    (2, 'ducnt48', 1, 'SMS'),
    (2, 'dungpka', 1, 'SMS'),
    (2, 'dungtx', 1, 'SMS'),
    (2, 'gianglt', 1, 'SMS'),
    (2, 'giangtm1', 1, 'SMS'),
    (2, 'haolx', 1, 'SMS'),
    (2, 'hieppq2', 1, 'SMS'),
    (2, 'hungbv9', 1, 'SMS'),
    (2, 'linhnc', 1, 'SMS'),
    (2, 'longvt', 1, 'SMS'),
    (2, 'lucnt14', 1, 'SMS'),
    (2, 'thangnx6', 1, 'SMS'),
    (2, 'tiennt12', 1, 'SMS'),
    (2, 'truyenhdh', 1, 'SMS'),
    (2, 'tuandn3', 1, 'SMS'),
    (2, 'tungtv8', 1, 'SMS'),
    (3, 'binhdt8', 1, 'SMS'),
    (3, 'duclm22', 1, 'ALL'),
    (3, 'giangtm1', 1, 'SMS'),
    (3, 'hieppq2', 1, 'SMS'),
    (3, 'longvt', 1, 'SMS'),
    (3, 'thangnx6', 1, 'SMS'),
    (4, 'binhdt8', 1, 'SMS'),
    (4, 'duclm22', 1, 'ALL'),
    (4, 'ducnt48', 1, 'SMS'),
    (4, 'dungpka', 1, 'SMS'),
    (4, 'dungtx', 1, 'SMS'),
    (4, 'gianglt', 1, 'SMS'),
    (4, 'giangtm1', 1, 'SMS'),
    (4, 'haolx', 1, 'SMS'),
    (4, 'hieppq2', 1, 'SMS'),
    (4, 'hungbv9', 1, 'SMS'),
    (4, 'linhnc', 1, 'SMS'),
    (4, 'longvt', 1, 'SMS'),
    (4, 'lucnt14', 1, 'SMS'),
    (4, 'thangnx6', 1, 'SMS'),
    (4, 'tiennt12', 1, 'SMS'),
    (4, 'truyenhdh', 1, 'SMS'),
    (4, 'tuandn3', 1, 'SMS'),
    (4, 'tungtv8', 1, 'SMS');

