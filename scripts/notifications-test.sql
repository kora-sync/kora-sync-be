DELETE FROM notifications;

INSERT INTO notifications (id, company_id, type, title, message, dedupe_key, created_at, updated_at)
SELECT gen_random_uuid(), u.company_id, t.type, t.title, t.message, t.key, now() - t.age, now() - t.age
FROM users u,
     (VALUES ('LOW_STOCK',      'Low stock: Arroz', 'Arroz is at 2.000 KG (minimum 5.000)', 'TEST:1', interval '3 minutes'),
             ('ENTRY_DUE_SOON', 'Entry due soon',   'Invoice #10 is due in 2 days',         'TEST:2', interval '2 minutes'),
             ('ENTRY_OVERDUE',  'Entry overdue',    'Invoice #7 is overdue',                'TEST:3', interval '1 minute')
     ) AS t(type, title, message, key, age)
WHERE u.email = 'admin@tenanta.com';

INSERT INTO notifications (id, company_id, type, title, message, dedupe_key, created_at, updated_at)
SELECT gen_random_uuid(), c.id, 'LOW_STOCK', 'Low stock: Arroz B', 'Tenant B only', 'TEST:B1', now(), now()
FROM companies c
WHERE c.id <> (SELECT company_id FROM users WHERE email = 'admin@tenanta.com');

SELECT c.id, count(*) AS notifications, count(*) FILTER (WHERE n.read_at IS NULL) AS unread
FROM notifications n JOIN companies c ON c.id = n.company_id
GROUP BY c.id ORDER BY notifications DESC;