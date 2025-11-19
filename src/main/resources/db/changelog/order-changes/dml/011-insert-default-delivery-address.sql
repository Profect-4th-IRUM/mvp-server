-- changeset willjsw:insert-data-1-delivery_address
-- comment: 기본 배송지 주소 데이터 삽입 (member_id 1~5000)

INSERT INTO p_delivery_address (
    delivery_address_id,
    member_id,
    postal_code,
    city,
    sigungu,
    road_name,
    address_detail,
    recipient_name,
    recipient_contact,
    is_default,
    created_at,
    updated_at,
    deleted_at,
    created_by,
    updated_by
)
SELECT
    (
        '00000007-0000-7000-8000-' ||
        LPAD(i::text, 12, '0')
        )::uuid AS delivery_address_id,
        i AS member_id,
    '12345' AS postal_code,
    '서울특별시' AS city,
    '강남구' AS sigungu,
    '테헤란로145' AS road_name,
    '13층' AS address_detail,
    '홍길동' AS recipient_name,
    '010-1234-5678' AS recipient_contact,
    TRUE AS is_default,
    CURRENT_TIMESTAMP AS created_at,
    CURRENT_TIMESTAMP AS updated_at,
    NULL AS deleted_at,
    i AS created_by,
    i AS updated_by
FROM generate_series(1, 5000) AS i;

-- rollback DELETE FROM p_delivery_address WHERE delivery_address_id::text LIKE '00000007-0000-7000-8000-%';