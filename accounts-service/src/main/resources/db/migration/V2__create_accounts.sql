INSERT INTO users (login, first_name, last_name, birthday) VALUES
    ('ivanov', 'Ivan', 'Ivanov', TO_DATE('2000-01-01','YYYY-MM-DD')),
    ('petrov', 'Petr', 'Petrov', TO_DATE('2000-02-02','YYYY-MM-DD')),
    ('fedorov', 'Fedor', 'Fedorov', TO_DATE('2000-03-03','YYYY-MM-DD'));

INSERT INTO accounts (user_id, balance) VALUES
    ((SELECT id from users where login='ivanov'), 100.0),
    ((SELECT id from users where login='petrov'), 200.0),
    ((SELECT id from users where login='fedorov'), 300.0);