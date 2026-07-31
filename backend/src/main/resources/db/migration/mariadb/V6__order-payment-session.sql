ALTER TABLE `orders` ADD COLUMN `payment_session_id` varchar(64) DEFAULT NULL;
ALTER TABLE `orders` ADD COLUMN `payment_url` varchar(512) DEFAULT NULL;
ALTER TABLE `orders` ADD UNIQUE KEY `UK_order_payment_session` (`payment_session_id`);
