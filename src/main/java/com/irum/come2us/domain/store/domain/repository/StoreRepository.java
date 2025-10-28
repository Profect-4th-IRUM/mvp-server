package com.irum.come2us.domain.store.domain.repository;

import com.irum.come2us.domain.member.domain.entity.Member;
import com.irum.come2us.domain.store.domain.entity.Store;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StoreRepository extends JpaRepository<Store, UUID>, StoreRepositoryCustom {

    boolean existsByMember(Member member);

    boolean existsByBusinessRegistrationNumber(String businessRegistrationNumber);

    boolean existsByTelemarketingRegistrationNumber(String telemarketingRegistrationNumber);

    Optional<Store> findByMember(Member member);

    @Query("SELECT s FROM Store s JOIN FETCH s.deliveryPolicy WHERE s.id = :id")
    Optional<Store> findByIdWithDeliveryPolicy(@Param("id") UUID id);
}
