package com.irum.come2us.domain.store.domain.repository;

import com.irum.come2us.domain.member.domain.entity.Member;
import com.irum.come2us.domain.store.domain.entity.Store;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, UUID> {

    boolean existsByMember(Member member);

    boolean existsByBusinessRegistrationNumber(String businessRegistrationNumber);

    boolean existsByTelemarketingRegistrationNumber(String telemarketingRegistrationNumber);
}
