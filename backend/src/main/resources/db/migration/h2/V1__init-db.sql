BEGIN;
CREATE SCHEMA IF NOT EXISTS "catalog_db";
USE "catalog_db";

CREATE TABLE IF NOT EXISTS "catalog_db"."product_category" (
  "id" BIGINT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY ("id")
);

CREATE TABLE IF NOT EXISTS "catalog_db"."product_category_translation" (
  "id" BIGINT NOT NULL AUTO_INCREMENT,
  "category_id" BIGINT NOT NULL,
  "locale" CHARACTER VARYING(8) NOT NULL,
  "name" CHARACTER VARYING(255) NOT NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "uk_category_locale" UNIQUE ("category_id", "locale"),
  CONSTRAINT "fk_category_translation" FOREIGN KEY ("category_id") REFERENCES "product_category" ("id")
);

CREATE TABLE IF NOT EXISTS "catalog_db"."product" (
  "id" BIGINT NOT NULL AUTO_INCREMENT,
  "sku" CHARACTER VARYING(255),
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

CREATE TABLE IF NOT EXISTS "catalog_db"."product_translation" (
  "id" BIGINT NOT NULL AUTO_INCREMENT,
  "product_id" BIGINT NOT NULL,
  "locale" CHARACTER VARYING(8) NOT NULL,
  "name" CHARACTER VARYING(255) NOT NULL,
  "description" CHARACTER VARYING(2000) NOT NULL,
  PRIMARY KEY ("id"),
  CONSTRAINT "uk_product_locale" UNIQUE ("product_id", "locale"),
  CONSTRAINT "fk_product_translation" FOREIGN KEY ("product_id") REFERENCES "product" ("id")
);

COMMIT;
