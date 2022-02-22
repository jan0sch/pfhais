CREATE TABLE "products" (
  "id" UUID NOT NULL,
  CONSTRAINT "products_pk" PRIMARY KEY ("id")
);

COMMENT ON TABLE "products" IS 'This table holds all product keys.';
COMMENT ON COLUMN "products"."id" IS 'The unique product ID.';

CREATE TABLE "names" (
  "product_id" UUID       NOT NULL,
  "lang_code"  VARCHAR(2) NOT NULL,
  "name"       TEXT       NOT NULL,
  CONSTRAINT "names_pk" PRIMARY KEY ("product_id", "lang_code"),
  CONSTRAINT "names_product_id_fk" FOREIGN KEY ("product_id") REFERENCES "products" ("id") ON DELETE CASCADE ON UPDATE CASCADE
);

COMMENT ON TABLE "names" IS 'This table holds all translations for all products. Deletions and updates of products are cascaded.';
COMMENT ON COLUMN "names"."product_id" IS 'A foreign key on the product id.';
COMMENT ON COLUMN "names"."lang_code" IS 'A two letter ISO-639-1 language code.';
COMMENT ON COLUMN "names"."name" IS 'A localized product name.';

