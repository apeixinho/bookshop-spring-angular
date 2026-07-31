BEGIN;

USE "bookshop_db" ;

SET REFERENTIAL_INTEGRITY FALSE;
DROP TABLE IF EXISTS `order_item`;
DROP TABLE IF EXISTS `orders`;
DROP TABLE IF EXISTS `customer`;
DROP TABLE IF EXISTS `address`;
SET REFERENTIAL_INTEGRITY TRUE;

CREATE TABLE IF NOT EXISTS `address` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `city` varchar(255) NOT NULL,
  `country` varchar(16) NOT NULL,
  `state` varchar(64) NOT NULL,
  `street` varchar(255) NOT NULL,
  `zip_code` varchar(32) NOT NULL,
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `customer` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `first_name` varchar(255) NOT NULL,
  `last_name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `oauth_sub` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `UK_customer_email` UNIQUE (`email`),
  CONSTRAINT `UK_customer_oauth_sub` UNIQUE (`oauth_sub`)
);

CREATE TABLE IF NOT EXISTS `orders` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_tracking_number` varchar(255) NOT NULL,
  `total_price` decimal(19,2) NOT NULL,
  `total_quantity` int NOT NULL,
  `currency_code` varchar(8) NOT NULL,
  `fx_rate` decimal(19,6) NOT NULL,
  `idempotency_key` varchar(64) NOT NULL,
  `billing_address_id` bigint NOT NULL,
  `customer_id` bigint NOT NULL,
  `shipping_address_id` bigint NOT NULL,
  `status` varchar(32) NOT NULL,
  `date_created` datetime(6) DEFAULT NULL,
  `last_updated` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `UK_order_tracking_number` UNIQUE (`order_tracking_number`),
  CONSTRAINT `UK_order_idempotency` UNIQUE (`customer_id`, `idempotency_key`),
  CONSTRAINT `UK_billing_address_id` UNIQUE (`billing_address_id`),
  CONSTRAINT `UK_shipping_address_id` UNIQUE (`shipping_address_id`),
  CONSTRAINT `FK_customer_id` FOREIGN KEY (`customer_id`) REFERENCES `customer` (`id`),
  CONSTRAINT `FK_billing_address_id` FOREIGN KEY (`billing_address_id`) REFERENCES `address` (`id`),
  CONSTRAINT `FK_shipping_address_id` FOREIGN KEY (`shipping_address_id`) REFERENCES `address` (`id`),
  CONSTRAINT `CHK_order_qty` CHECK (`total_quantity` > 0),
  CONSTRAINT `CHK_order_price` CHECK (`total_price` >= 0)
);

CREATE TABLE IF NOT EXISTS `order_item` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `image_url` varchar(255) DEFAULT NULL,
  `quantity` int NOT NULL,
  `unit_price` decimal(19,2) NOT NULL,
  `order_id` bigint NOT NULL,
  `product_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FK_order_id` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`),
  CONSTRAINT `FK_product_id` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`),
  CONSTRAINT `CHK_item_qty` CHECK (`quantity` > 0),
  CONSTRAINT `CHK_item_price` CHECK (`unit_price` >= 0)
);

COMMIT;
