-- Password: password
-- Inserção de um usuário admin
INSERT INTO users (id, username, password, role) VALUES
('123e4567-e89b-12d3-a456-556642440000', 'username', '$2a$10$6LFYgUrSNREwdVdTPhLZT.8.GBAR2TATB8foCjf0vSrpBa1IXdABS', 'ADMIN');

SELECT * FROM users;