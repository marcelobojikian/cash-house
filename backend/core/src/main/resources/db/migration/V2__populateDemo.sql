
INSERT INTO FLATMATE (nickname, email, password, roles, enabled, first_step, guest_step) VALUES ('Marcelo (test)', 'marcelo@mail.com', '$2a$10$DobDqO4VhUfNeDlE6wsKy.HK37x2dU9QtAGbmVswdzfQPUv0wI742', 'ADMIN,USER', true, false, false);
INSERT INTO FLATMATE (nickname, email, password, roles, enabled, first_step, guest_step) VALUES ('Biro (test)', 'biro@mail.com', '$2a$10$DobDqO4VhUfNeDlE6wsKy.HK37x2dU9QtAGbmVswdzfQPUv0wI742', 'USER', true, false, true);
INSERT INTO FLATMATE (nickname, email, password, roles, enabled, first_step, guest_step) VALUES ('Eduardo (test)', 'eduardo@mail.com', '$2a$10$DobDqO4VhUfNeDlE6wsKy.HK37x2dU9QtAGbmVswdzfQPUv0wI742', 'USER', true, false, true);
INSERT INTO FLATMATE (nickname, email, password, roles, enabled, first_step, guest_step) VALUES ('Willian (test)', 'will@mail.com', '$2a$10$DobDqO4VhUfNeDlE6wsKy.HK37x2dU9QtAGbmVswdzfQPUv0wI742', 'USER', true, false, true);
INSERT INTO FLATMATE (nickname, email, password, roles, enabled, first_step, guest_step) VALUES ('Gabriela (test)', 'gabi@mail.com', '$2a$10$DobDqO4VhUfNeDlE6wsKy.HK37x2dU9QtAGbmVswdzfQPUv0wI742', 'USER', true, false, true);
INSERT INTO FLATMATE (nickname, email, password, roles, enabled, first_step, guest_step) VALUES ('Carol (test)', 'carol@mail.com', '$2a$10$DobDqO4VhUfNeDlE6wsKy.HK37x2dU9QtAGbmVswdzfQPUv0wI742', 'USER', true, false, true);
INSERT INTO FLATMATE (nickname, email, password, roles, enabled, first_step, guest_step) VALUES ('Raissa (test)', 'rai@mail.com', '$2a$10$DobDqO4VhUfNeDlE6wsKy.HK37x2dU9QtAGbmVswdzfQPUv0wI742', 'USER', true, false, true);
INSERT INTO FLATMATE (nickname, email, password, roles, enabled, first_step, guest_step) VALUES ('Jean (test)', 'jean@mail.com', '$2a$10$DobDqO4VhUfNeDlE6wsKy.HK37x2dU9QtAGbmVswdzfQPUv0wI742', 'USER', true, false, false);
INSERT INTO FLATMATE (nickname, email, password, roles, enabled, first_step, guest_step) VALUES ('Gretchen (test)', 'gretchen@mail.com', '$2a$10$DobDqO4VhUfNeDlE6wsKy.HK37x2dU9QtAGbmVswdzfQPUv0wI742', 'USER', true, false, true);
INSERT INTO FLATMATE (nickname, email, password, roles, enabled, first_step, guest_step) VALUES ('Fernando (test)', 'fernando@mail.com', '$2a$10$DobDqO4VhUfNeDlE6wsKy.HK37x2dU9QtAGbmVswdzfQPUv0wI742', 'USER', true, true, true);

INSERT INTO DASHBOARD (OWNER_ID) VALUES (1);
INSERT INTO DASHBOARD (OWNER_ID) VALUES (8);

INSERT INTO CASHIER (owner_id, balance, name, started) VALUES (1, 32.54, 'Energy & bin', 0);
INSERT INTO CASHIER (owner_id, balance, name, started) VALUES (1, 120, 'Geral', 23);
INSERT INTO CASHIER (owner_id, balance, name, started) VALUES (8, 3.11, 'Rent & Clean', 12.45);
			  
INSERT INTO DASHBOARD_CASHIER (ID_DASHBOARD, ID_CASHIER) VALUES (1, 1);
INSERT INTO DASHBOARD_CASHIER (ID_DASHBOARD, ID_CASHIER) VALUES (1, 2);
INSERT INTO DASHBOARD_CASHIER (ID_DASHBOARD, ID_CASHIER) VALUES (2, 3);
			  
INSERT INTO DASHBOARD_GUEST (ID_DASHBOARD, ID_FLATMATE) VALUES (1, 2);
INSERT INTO DASHBOARD_GUEST (ID_DASHBOARD, ID_FLATMATE) VALUES (1, 3);
INSERT INTO DASHBOARD_GUEST (ID_DASHBOARD, ID_FLATMATE) VALUES (1, 4);
INSERT INTO DASHBOARD_GUEST (ID_DASHBOARD, ID_FLATMATE) VALUES (1, 5);
INSERT INTO DASHBOARD_GUEST (ID_DASHBOARD, ID_FLATMATE) VALUES (1, 6);
INSERT INTO DASHBOARD_GUEST (ID_DASHBOARD, ID_FLATMATE) VALUES (1, 7);
INSERT INTO DASHBOARD_GUEST (ID_DASHBOARD, ID_FLATMATE) VALUES (2, 9);
INSERT INTO DASHBOARD_GUEST (ID_DASHBOARD, ID_FLATMATE) VALUES (2, 10);

-- Usuario Marcelo pertence a dois dashboard
INSERT INTO DASHBOARD_GUEST (ID_DASHBOARD, ID_FLATMATE) VALUES (2, 1);

INSERT INTO TRANSACTION (create_by_id, assigned_id, cashier_id, status, action, value, created_at) VALUES (1, 1, 1, 'CREATED', 'DEPOSIT', 1.99, '2020-01-28 23:59:59');
INSERT INTO TRANSACTION (create_by_id, assigned_id, cashier_id, status, action, value, created_at) VALUES (1, 2, 1, 'FINISHED', 'WITHDRAW', 23, '2020-01-27 23:59:59');
INSERT INTO TRANSACTION (create_by_id, assigned_id, cashier_id, status, action, value, created_at) VALUES (1, 4, 2, 'SENDED', 'WITHDRAW', 5.53, '2020-01-28 23:59:59');
INSERT INTO TRANSACTION (create_by_id, assigned_id, cashier_id, status, action, value, created_at) VALUES (5, 5, 2, 'CREATED', 'DEPOSIT', 12.03, '2020-01-28 23:59:59');
INSERT INTO TRANSACTION (create_by_id, assigned_id, cashier_id, status, action, value, created_at) VALUES (6, 6, 2, 'CANCELED', 'DEPOSIT', 66.11, '2020-01-27 23:59:59');
INSERT INTO TRANSACTION (create_by_id, assigned_id, cashier_id, status, action, value, created_at) VALUES (1, 3, 1, 'DELETED', 'DEPOSIT', 61.30, '2020-01-28 23:59:59');
INSERT INTO TRANSACTION (create_by_id, assigned_id, cashier_id, status, action, value, created_at) VALUES (8, 8, 3, 'SENDED', 'WITHDRAW', 1.50, '2020-01-28 23:59:59');
INSERT INTO TRANSACTION (create_by_id, assigned_id, cashier_id, status, action, value, created_at) VALUES (8, 9, 3, 'CANCELED', 'DEPOSIT', 8.88, '2020-01-28 23:59:59');
INSERT INTO TRANSACTION (create_by_id, assigned_id, cashier_id, status, action, value, created_at) VALUES (10, 10, 3, 'CREATED', 'WITHDRAW', 9.41, '2020-01-28 23:59:59');
INSERT INTO TRANSACTION (create_by_id, assigned_id, cashier_id, status, action, value, created_at) VALUES (9, 9, 3, 'SENDED', 'DEPOSIT', 20, '2020-01-28 23:59:59');

INSERT INTO DASHBOARD_TRANSACTION (ID_DASHBOARD, ID_TRANSACTION) VALUES (1, 1);
INSERT INTO DASHBOARD_TRANSACTION (ID_DASHBOARD, ID_TRANSACTION) VALUES (1, 2);
INSERT INTO DASHBOARD_TRANSACTION (ID_DASHBOARD, ID_TRANSACTION) VALUES (1, 3);
INSERT INTO DASHBOARD_TRANSACTION (ID_DASHBOARD, ID_TRANSACTION) VALUES (1, 4);
INSERT INTO DASHBOARD_TRANSACTION (ID_DASHBOARD, ID_TRANSACTION) VALUES (1, 5);
INSERT INTO DASHBOARD_TRANSACTION (ID_DASHBOARD, ID_TRANSACTION) VALUES (1, 6);
INSERT INTO DASHBOARD_TRANSACTION (ID_DASHBOARD, ID_TRANSACTION) VALUES (2, 7);
INSERT INTO DASHBOARD_TRANSACTION (ID_DASHBOARD, ID_TRANSACTION) VALUES (2, 8);
INSERT INTO DASHBOARD_TRANSACTION (ID_DASHBOARD, ID_TRANSACTION) VALUES (2, 9);
INSERT INTO DASHBOARD_TRANSACTION (ID_DASHBOARD, ID_TRANSACTION) VALUES (2, 10);