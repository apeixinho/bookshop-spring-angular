ALTER TABLE "bookshop_db"."orders" ADD COLUMN IF NOT EXISTS "payment_session_id" varchar(64) DEFAULT NULL;
ALTER TABLE "bookshop_db"."orders" ADD COLUMN IF NOT EXISTS "payment_url" varchar(512) DEFAULT NULL;
ALTER TABLE "bookshop_db"."orders" ADD CONSTRAINT IF NOT EXISTS "UK_order_payment_session" UNIQUE ("payment_session_id");
