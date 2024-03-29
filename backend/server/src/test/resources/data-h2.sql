;/* DROP TABLE IF EXISTS FLATMATE;
DROP TABLE IF EXISTS DASHBOARD;
DROP TABLE IF EXISTS CASHIER;
DROP TABLE IF EXISTS TRANSACTION;
DROP TABLE IF EXISTS DASHBOARD_CASHIER;
DROP TABLE IF EXISTS DASHBOARD_GUEST;
DROP TABLE IF EXISTS DASHBOARD_TRANSACTION;

CREATE TABLE IF NOT EXISTS FLATMATE (
	ID BIGINT PRIMARY KEY AUTO_INCREMENT(11,1),
	EMAIL VARCHAR(255) UNIQUE,
	NICKNAME VARCHAR(255),
	PASSWORD VARCHAR(255),
	ROLES VARCHAR(50),
	ENABLED BOOLEAN,
	FIRST_STEP BOOLEAN,
	GUEST_STEP BOOLEAN
);

CREATE TABLE IF NOT EXISTS DASHBOARD (
	ID BIGINT PRIMARY KEY AUTO_INCREMENT(11,1),
	OWNER_ID BIGINT,
	FOREIGN KEY (OWNER_ID) REFERENCES FLATMATE(ID)
);

CREATE TABLE IF NOT EXISTS CASHIER (
	ID BIGINT PRIMARY KEY AUTO_INCREMENT(11,1),
	NAME VARCHAR(200),
	BALANCE DECIMAL(12,2),
	STARTED_VALUE DECIMAL(12,2)
);

CREATE TABLE IF NOT EXISTS TRANSACTION (
	ID BIGINT PRIMARY KEY AUTO_INCREMENT(11,1),
	CREATE_BY_ID BIGINT,
	ASSIGNED_ID BIGINT,
	CASHIER_ID BIGINT,
	STATUS VARCHAR(20),
	ACTION VARCHAR(20),
	VALUE DECIMAL(12,2),
	FOREIGN KEY (CREATE_BY_ID) REFERENCES FLATMATE(ID),
	FOREIGN KEY (ASSIGNED_ID) REFERENCES FLATMATE(ID),
	FOREIGN KEY (CASHIER_ID) REFERENCES CASHIER(ID)
);

CREATE TABLE IF NOT EXISTS DASHBOARD_CASHIER (
	ID_DASHBOARD BIGINT,
	ID_CASHIER BIGINT,
	FOREIGN KEY (ID_DASHBOARD) REFERENCES DASHBOARD(ID),
	FOREIGN KEY (ID_CASHIER) REFERENCES CASHIER(ID),
);

CREATE TABLE IF NOT EXISTS DASHBOARD_GUEST (
	ID_DASHBOARD BIGINT,
	ID_FLATMATE BIGINT,
	FOREIGN KEY (ID_DASHBOARD) REFERENCES DASHBOARD(ID),
	FOREIGN KEY (ID_FLATMATE) REFERENCES FLATMATE(ID),
);

CREATE TABLE IF NOT EXISTS DASHBOARD_TRANSACTION (
	ID_DASHBOARD BIGINT,
	ID_TRANSACTION BIGINT,
	FOREIGN KEY (ID_DASHBOARD) REFERENCES DASHBOARD(ID),
	FOREIGN KEY (ID_TRANSACTION) REFERENCES TRANSACTION(ID),
);

INSERT INTO FLATMATE (id, nickname, email, password, roles, enabled, first_step, guest_step) VALUES (1, 'Marcelo (test)', 'marcelo@mail.com', '$2a$10$DobDqO4VhUfNeDlE6wsKy.HK37x2dU9QtAGbmVswdzfQPUv0wI742', 'ADMIN,USER', true, false, false);
INSERT INTO FLATMATE (id, nickname, email, password, roles, enabled, first_step, guest_step) VALUES (2, 'Biro (test)', 'biro@mail.com', '$2a$10$DobDqO4VhUfNeDlE6wsKy.HK37x2dU9QtAGbmVswdzfQPUv0wI742', 'USER', true, false, false);
INSERT INTO FLATMATE (id, nickname, email, password, roles, enabled, first_step, guest_step) VALUES (3, 'Eduardo (test)', 'eduardo@mail.com', '$2a$10$DobDqO4VhUfNeDlE6wsKy.HK37x2dU9QtAGbmVswdzfQPUv0wI742', 'USER', true, false, false);
INSERT INTO FLATMATE (id, nickname, email, password, roles, enabled, first_step, guest_step) VALUES (4, 'Willian (test)', 'will@mail.com', '$2a$10$DobDqO4VhUfNeDlE6wsKy.HK37x2dU9QtAGbmVswdzfQPUv0wI742', 'USER', true, false, false);
INSERT INTO FLATMATE (id, nickname, email, password, roles, enabled, first_step, guest_step) VALUES (5, 'Gabriela (test)', 'gabi@mail.com', '$2a$10$DobDqO4VhUfNeDlE6wsKy.HK37x2dU9QtAGbmVswdzfQPUv0wI742', 'USER', true, false, false);
INSERT INTO FLATMATE (id, nickname, email, password, roles, enabled, first_step, guest_step) VALUES (6, 'Carol (test)', 'carol@mail.com', '$2a$10$DobDqO4VhUfNeDlE6wsKy.HK37x2dU9QtAGbmVswdzfQPUv0wI742', 'USER', true, false, false);
INSERT INTO FLATMATE (id, nickname, email, password, roles, enabled, first_step, guest_step) VALUES (7, 'Raissa (test)', 'rai@mail.com', '$2a$10$DobDqO4VhUfNeDlE6wsKy.HK37x2dU9QtAGbmVswdzfQPUv0wI742', 'USER', true, false, false);
INSERT INTO FLATMATE (id, nickname, email, password, roles, enabled, first_step, guest_step) VALUES (8, 'Jean (test)', 'jean@mail.com', '$2a$10$DobDqO4VhUfNeDlE6wsKy.HK37x2dU9QtAGbmVswdzfQPUv0wI742', 'USER', true, false, false);
INSERT INTO FLATMATE (id, nickname, email, password, roles, enabled, first_step, guest_step) VALUES (9, 'Gretchen (test)', 'gretchen@mail.com', '$2a$10$DobDqO4VhUfNeDlE6wsKy.HK37x2dU9QtAGbmVswdzfQPUv0wI742', 'USER', true, false, false);
INSERT INTO FLATMATE (id, nickname, email, password, roles, enabled, first_step, guest_step) VALUES (10, 'Fernando (test)', 'fernando@mail.com', '$2a$10$DobDqO4VhUfNeDlE6wsKy.HK37x2dU9QtAGbmVswdzfQPUv0wI742', 'USER', true, false, false);

INSERT INTO DASHBOARD (ID, OWNER_ID) VALUES (1, 1);
INSERT INTO DASHBOARD (ID, OWNER_ID) VALUES (2, 8);

INSERT INTO CASHIER (id, balance, name, started_value) VALUES (1, 32.54, 'Energy & bin', 0);
INSERT INTO CASHIER (id, balance, name, started_value) VALUES (2, 120, 'Geral', 23);
INSERT INTO CASHIER (id, balance, name, started_value) VALUES (3, 3.11, 'Rent & Clean', 12.45);
			  
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

INSERT INTO TRANSACTION (id, create_by_id, assigned_id, cashier_id, status, action, value) VALUES (1, 1, 1, 1, 'CREATED', 'DEPOSIT', 1.99);
INSERT INTO TRANSACTION (id, create_by_id, assigned_id, cashier_id, status, action, value) VALUES (2, 1, 2, 1, 'FINISHED', 'WITHDRAW', 23);
INSERT INTO TRANSACTION (id, create_by_id, assigned_id, cashier_id, status, action, value) VALUES (3, 1, 4, 2, 'SUBMITED', 'WITHDRAW', 5.53);
INSERT INTO TRANSACTION (id, create_by_id, assigned_id, cashier_id, status, action, value) VALUES (4, 5, 5, 2, 'CREATED', 'DEPOSIT', 12.03);
INSERT INTO TRANSACTION (id, create_by_id, assigned_id, cashier_id, status, action, value) VALUES (5, 6, 6, 2, 'CANCELED', 'DEPOSIT', 66.11);
INSERT INTO TRANSACTION (id, create_by_id, assigned_id, cashier_id, status, action, value) VALUES (6, 1, 3, 1, 'DELETED', 'DEPOSIT', 61.30);
INSERT INTO TRANSACTION (id, create_by_id, assigned_id, cashier_id, status, action, value) VALUES (7, 8, 8, 1, 'SUBMITED', 'WITHDRAW', 1.50);
INSERT INTO TRANSACTION (id, create_by_id, assigned_id, cashier_id, status, action, value) VALUES (8, 8, 9, 1, 'CANCELED', 'DEPOSIT', 8.88);
INSERT INTO TRANSACTION (id, create_by_id, assigned_id, cashier_id, status, action, value) VALUES (9, 10, 10, 1, 'SUBMITED', 'WITHDRAW', 9.41);
INSERT INTO TRANSACTION (id, create_by_id, assigned_id, cashier_id, status, action, value) VALUES (10, 9, 9, 1, 'SUBMITED', 'DEPOSIT', 20);

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
*/
