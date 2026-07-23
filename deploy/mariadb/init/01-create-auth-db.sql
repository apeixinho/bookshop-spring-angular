CREATE DATABASE IF NOT EXISTS bookshop_auth;
GRANT ALL PRIVILEGES ON bookshop_auth.* TO 'bookshop_user'@'%';
FLUSH PRIVILEGES;
