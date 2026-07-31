-- Harden catalog money/stock columns for checkout integrity.
ALTER TABLE `product`
  MODIFY COLUMN `unit_price` decimal(13,2) NOT NULL;
ALTER TABLE `product`
  MODIFY COLUMN `units_in_stock` int NOT NULL;
ALTER TABLE `product`
  ADD COLUMN `version` bigint NOT NULL DEFAULT 0;
