BEGIN;
USE `bookshop_db` ;

CREATE TABLE IF NOT EXISTS `bookshop_db`.`product_category` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `bookshop_db`.`product_category_translation` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `category_id` BIGINT(20) NOT NULL,
  `locale` VARCHAR(8) NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_category_locale` (`category_id`, `locale`),
  CONSTRAINT `fk_category_translation` FOREIGN KEY (`category_id`) REFERENCES `product_category` (`id`)
);

CREATE TABLE IF NOT EXISTS `bookshop_db`.`product` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `sku` VARCHAR(255) DEFAULT NULL,
  `unit_price` DECIMAL(13, 2) DEFAULT NULL,
  `image_url` VARCHAR(255) DEFAULT NULL,
  `active` BIT DEFAULT 1,
  `units_in_stock` INT(11) DEFAULT NULL,
  `date_created` DATETIME(6) DEFAULT NULL,
  `last_updated` DATETIME(6) DEFAULT NULL,
  `category_id` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_category` (`category_id`),
  CONSTRAINT `fk_category` FOREIGN KEY (`category_id`) REFERENCES `product_category` (`id`)
);

CREATE TABLE IF NOT EXISTS `bookshop_db`.`product_translation` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `product_id` BIGINT(20) NOT NULL,
  `locale` VARCHAR(8) NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  `description` VARCHAR(2000) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_product_locale` (`product_id`, `locale`),
  CONSTRAINT `fk_product_translation` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)
);

COMMIT;
