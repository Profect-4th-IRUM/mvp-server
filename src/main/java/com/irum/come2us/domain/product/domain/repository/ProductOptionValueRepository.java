package com.irum.come2us.domain.product.domain.repository;

import java.util.Optional;
import java.util.UUID;

import jakarta.persistence.LockModeType;

import org.hibernate.annotations.processing.SQL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.irum.come2us.domain.product.domain.entity.ProductOptionValue;

@Repository
public interface ProductOptionValueRepository extends JpaRepository<ProductOptionValue, UUID> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select pov from ProductOptionValue pov where pov.id = :id")
	Optional<ProductOptionValue> findByIdWithPessimisticLock(UUID id);
}
