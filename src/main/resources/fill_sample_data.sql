INSERT INTO User (ID, HASLOANS, NAME, version)
VALUES (1, false, 'k', 1);

INSERT INTO User (HASLOANS, NAME, version)
VALUES (false, 'l', 1);

INSERT INTO Book (ID, LOAN_ID, version)
VALUES (1, null, 1);

INSERT INTO Book (ID, LOAN_ID, version)
VALUES (2, null, 1);