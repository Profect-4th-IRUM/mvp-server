package com.irum.come2us.domain.deliveryaddress.domain.repository;

import com.irum.come2us.domain.deliveryaddress.domain.entity.DeliveryAddress;
import com.irum.come2us.domain.member.domain.entity.Member;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryAddressRepository
        extends JpaRepository<DeliveryAddress, UUID>, DeliveryAddressRepositoryCustom {
    Optional<Member> findByMember(Member member);
}
