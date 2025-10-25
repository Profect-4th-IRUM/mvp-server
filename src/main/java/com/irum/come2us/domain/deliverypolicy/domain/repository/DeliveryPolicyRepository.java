package com.irum.come2us.domain.deliverypolicy.domain.repository;

import com.irum.come2us.domain.deliverypolicy.domain.entity.DeliveryPolicy;
import com.irum.come2us.domain.store.domain.entity.Store;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryPolicyRepository extends JpaRepository<DeliveryPolicy, UUID> {

    boolean existsByStore(Store store);
}
