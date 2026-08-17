ALTER TABLE "catalog_db"."product" ALTER COLUMN "unit_price" SET NOT NULL;
ALTER TABLE "catalog_db"."product" ALTER COLUMN "units_in_stock" SET NOT NULL;
ALTER TABLE "catalog_db"."product" ADD COLUMN IF NOT EXISTS "version" BIGINT NOT NULL DEFAULT 0;
