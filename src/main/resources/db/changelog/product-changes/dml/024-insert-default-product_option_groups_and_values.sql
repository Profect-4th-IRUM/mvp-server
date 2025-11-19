-- changeset willjsw:insert-data-4-product-options
-- comment: 각 상품마다 1개의 옵션 그룹 & 옵션 값 생성

INSERT INTO p_product_option_group (
    option_group_id,
    product_id,
    name,
    created_at,
    created_by,
    updated_at,
    updated_by
)
SELECT
    (
        '00000005-0000-7000-8000-' ||
        LPAD(i::text, 12, '0')
        )::uuid AS option_group_id,
        product_id,
    '사이즈' AS name,
    CURRENT_TIMESTAMP,
    '100000',
    CURRENT_TIMESTAMP,
    '100000'
FROM (
         SELECT
             product_id,
             ROW_NUMBER() OVER (ORDER BY product_id) AS i
         FROM p_product
         WHERE product_id ::text LIKE '00000004-0000-7000-8000-%'
     ) AS products_with_index;


INSERT INTO p_product_option_value (
    option_value_id,
    option_group_id,
    name,
    stock_quantity,
    extra_price,
    created_at,
    created_by,
    updated_at,
    updated_by
)
SELECT
    (
        '00000006-0000-7000-8000-' ||
        LPAD(i::text, 12, '0')
        )::uuid AS option_value_id,
        option_group_id,
    'M' AS name,
    100 AS stock_quantity,
    0 AS extra_price,
    CURRENT_TIMESTAMP,
    '100000',
    CURRENT_TIMESTAMP,
    '100000'
FROM (
         SELECT
             option_group_id,
             ROW_NUMBER() OVER (ORDER BY option_group_id) AS i
         FROM p_product_option_group
         WHERE option_group_id ::text LIKE '00000005-0000-7000-8000-%'
     ) AS option_groups_with_index;

-- rollback DELETE FROM p_product_option_value WHERE option_value_id::text LIKE '00000006-0000-7000-8000-%';DELETE FROM p_product_option_group WHERE option_group_id::text LIKE '00000005-0000-7000-8000-%';