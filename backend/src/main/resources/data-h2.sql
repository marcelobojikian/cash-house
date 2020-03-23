
INSERT INTO FLATMATE (id, nickname, email, password, roles, enabled, first_step, guest_step) VALUES (1, 'Marcelo (test)', 'marcelo@mail.com', '$2a$10$DobDqO4VhUfNeDlE6wsKy.HK37x2dU9QtAGbmVswdzfQPUv0wI742', 'ADMIN,USER', true, false, false);
INSERT INTO FLATMATE (id, nickname, email, password, roles, enabled, first_step, guest_step) VALUES (2, 'Biro (test)', 'biro@mail.com', '$2a$10$DobDqO4VhUfNeDlE6wsKy.HK37x2dU9QtAGbmVswdzfQPUv0wI742', 'USER', true, false, true);
INSERT INTO FLATMATE (id, nickname, email, password, roles, enabled, first_step, guest_step) VALUES (3, 'Eduardo (test)', 'eduardo@mail.com', '$2a$10$DobDqO4VhUfNeDlE6wsKy.HK37x2dU9QtAGbmVswdzfQPUv0wI742', 'USER', true, false, true);
INSERT INTO FLATMATE (id, nickname, email, password, roles, enabled, first_step, guest_step) VALUES (4, 'Willian (test)', 'will@mail.com', '$2a$10$DobDqO4VhUfNeDlE6wsKy.HK37x2dU9QtAGbmVswdzfQPUv0wI742', 'USER', true, false, true);
INSERT INTO FLATMATE (id, nickname, email, password, roles, enabled, first_step, guest_step) VALUES (5, 'Gabriela (test)', 'gabi@mail.com', '$2a$10$DobDqO4VhUfNeDlE6wsKy.HK37x2dU9QtAGbmVswdzfQPUv0wI742', 'USER', true, false, true);
INSERT INTO FLATMATE (id, nickname, email, password, roles, enabled, first_step, guest_step) VALUES (6, 'Carol (test)', 'carol@mail.com', '$2a$10$DobDqO4VhUfNeDlE6wsKy.HK37x2dU9QtAGbmVswdzfQPUv0wI742', 'USER', true, false, true);
INSERT INTO FLATMATE (id, nickname, email, password, roles, enabled, first_step, guest_step) VALUES (7, 'Raissa (test)', 'rai@mail.com', '$2a$10$DobDqO4VhUfNeDlE6wsKy.HK37x2dU9QtAGbmVswdzfQPUv0wI742', 'USER', true, false, true);
INSERT INTO FLATMATE (id, nickname, email, password, roles, enabled, first_step, guest_step) VALUES (8, 'Jean (test)', 'jean@mail.com', '$2a$10$DobDqO4VhUfNeDlE6wsKy.HK37x2dU9QtAGbmVswdzfQPUv0wI742', 'USER', true, false, false);
INSERT INTO FLATMATE (id, nickname, email, password, roles, enabled, first_step, guest_step) VALUES (9, 'Gretchen (test)', 'gretchen@mail.com', '$2a$10$DobDqO4VhUfNeDlE6wsKy.HK37x2dU9QtAGbmVswdzfQPUv0wI742', 'USER', true, false, true);
INSERT INTO FLATMATE (id, nickname, email, password, roles, enabled, first_step, guest_step) VALUES (10, 'Fernando (test)', 'fernando@mail.com', '$2a$10$DobDqO4VhUfNeDlE6wsKy.HK37x2dU9QtAGbmVswdzfQPUv0wI742', 'USER', true, true, true);

INSERT INTO DASHBOARD (ID, OWNER_ID) VALUES (1, 1);
INSERT INTO DASHBOARD (ID, OWNER_ID) VALUES (2, 8);

INSERT INTO CASHIER (id, owner_id, balance, name, started) VALUES (1, 1, 32.54, 'Energy & bin', 0);
INSERT INTO CASHIER (id, owner_id, balance, name, started) VALUES (2, 1, 120, 'Geral', 23);
INSERT INTO CASHIER (id, owner_id, balance, name, started) VALUES (3, 8, 3.11, 'Rent & Clean', 12.45);
			  
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

INSERT INTO TRANSACTION (id, create_by_id, assigned_id, cashier_id, status, action, value, created_at) VALUES (1, 1, 1, 1, 'CREATED', 'DEPOSIT', 1.99, '2020-01-28 23:59:59');
INSERT INTO TRANSACTION (id, create_by_id, assigned_id, cashier_id, status, action, value, created_at) VALUES (2, 1, 2, 1, 'FINISHED', 'WITHDRAW', 23, '2020-01-27 23:59:59');
INSERT INTO TRANSACTION (id, create_by_id, assigned_id, cashier_id, status, action, value, created_at) VALUES (3, 1, 4, 2, 'SENDED', 'WITHDRAW', 5.53, '2020-01-28 23:59:59');
INSERT INTO TRANSACTION (id, create_by_id, assigned_id, cashier_id, status, action, value, created_at) VALUES (4, 5, 5, 2, 'CREATED', 'DEPOSIT', 12.03, '2020-01-28 23:59:59');
INSERT INTO TRANSACTION (id, create_by_id, assigned_id, cashier_id, status, action, value, created_at) VALUES (5, 6, 6, 2, 'CANCELED', 'DEPOSIT', 66.11, '2020-01-27 23:59:59');
INSERT INTO TRANSACTION (id, create_by_id, assigned_id, cashier_id, status, action, value, created_at) VALUES (6, 1, 3, 1, 'DELETED', 'DEPOSIT', 61.30, '2020-01-28 23:59:59');
INSERT INTO TRANSACTION (id, create_by_id, assigned_id, cashier_id, status, action, value, created_at) VALUES (7, 8, 8, 3, 'SENDED', 'WITHDRAW', 1.50, '2020-01-28 23:59:59');
INSERT INTO TRANSACTION (id, create_by_id, assigned_id, cashier_id, status, action, value, created_at) VALUES (8, 8, 9, 3, 'CANCELED', 'DEPOSIT', 8.88, '2020-01-28 23:59:59');
INSERT INTO TRANSACTION (id, create_by_id, assigned_id, cashier_id, status, action, value, created_at) VALUES (9, 10, 10, 3, 'CREATED', 'WITHDRAW', 9.41, '2020-01-28 23:59:59');
INSERT INTO TRANSACTION (id, create_by_id, assigned_id, cashier_id, status, action, value, created_at) VALUES (10, 9, 9, 3, 'SENDED', 'DEPOSIT', 20, '2020-01-28 23:59:59');

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

