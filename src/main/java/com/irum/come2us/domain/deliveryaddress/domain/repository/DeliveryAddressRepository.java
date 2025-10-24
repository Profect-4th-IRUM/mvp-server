package com.irum.come2us.domain.deliveryaddress.domain.repository;

import com.irum.come2us.domain.deliveryaddress.domain.entity.DeliveryAddress;
import com.irum.come2us.domain.member.domain.entity.Member;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DeliveryAddressRepository
        extends JpaRepository<DeliveryAddress, UUID>, DeliveryAddressRepositoryCustom {

    boolean existsByMember(Member member);

    @Query("SELECT d FROM DeliveryAddress d WHERE d.member = :member AND d.isDefault = true")
    Optional<DeliveryAddress> findDefaultAddressByMember(@Param("member") Member member);

    Optional<DeliveryAddress> findTopByMemberOrderByCreatedAtDesc(Member member);
}
