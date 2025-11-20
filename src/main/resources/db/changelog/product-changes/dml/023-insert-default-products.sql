-- changeset willjsw:insert-data-3-products
-- comment: 테스트 상품 데이터 init

INSERT INTO p_product (
    product_id,
    store_id,
    category_id,
    name,
    description,
    detail_description,
    is_public,
    avg_rating,
    review_count,
    price,
    created_at,
    created_by,
    updated_at,
    updated_by
)
SELECT
    (
        '00000004-0000-7000-8000-' ||
        LPAD(i::text, 12, '0')
        )::uuid AS product_id,
        stores.store_id,
    categories.category_id,
    CONCAT('랜덤 상품 ', i),
    '가볍고 편한 기본 반팔',
    '코튼 100%, 데일리 착용',
    TRUE,
    0,
    0,
    12900,
    CURRENT_TIMESTAMP,
    '100000',
    CURRENT_TIMESTAMP,
    '100000'
FROM generate_series(1, 100000) AS i
         CROSS JOIN LATERAL (
    SELECT store_id
    FROM p_store
             OFFSET (i - 1) % (SELECT COUNT(*) FROM p_store)
    LIMIT 1
    ) AS stores
    CROSS JOIN LATERAL (
SELECT category_id
FROM p_category
WHERE depth = 3
OFFSET (i - 1) % (SELECT COUNT(*) FROM p_category WHERE depth = 3)
    LIMIT 1
    ) AS categories;

-- rollback DELETE FROM p_product;
