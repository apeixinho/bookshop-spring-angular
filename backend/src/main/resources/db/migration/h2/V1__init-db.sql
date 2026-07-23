BEGIN;
-- -----------------------------------------------------
-- Schema bookshop_db
-- -----------------------------------------------------
-- DROP SCHEMA IF EXISTS "bookshop_db";
CREATE SCHEMA IF NOT EXISTS "bookshop_db";
USE "bookshop_db";
-- -----------------------------------------------------
-- Table `bookshop_db`.`product_category`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS "bookshop_db"."product_category" (
  "id" BIGINT NOT NULL AUTO_INCREMENT,
  "category_name" CHARACTER VARYING(255),
  PRIMARY KEY ("id")
);
-- -----------------------------------------------------
-- Table `bookshop_db`.`product`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS "bookshop_db"."product" (
  "id" BIGINT NOT NULL AUTO_INCREMENT,
  "sku" CHARACTER VARYING(255),
  "name" CHARACTER VARYING(255),
  "description" CHARACTER VARYING(255),
  "unit_price" NUMERIC(13, 2),
  "image_url" CHARACTER VARYING(255),
  "active" BOOLEAN DEFAULT TRUE,
  "units_in_stock" INTEGER DEFAULT NULL,
  "date_created" TIMESTAMP(6) DEFAULT NULL,
  "last_updated" TIMESTAMP(6) DEFAULT NULL,
  "category_id" BIGINT NOT NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "fk_category" FOREIGN KEY ("category_id") REFERENCES "product_category" ("id")
);

COMMIT;