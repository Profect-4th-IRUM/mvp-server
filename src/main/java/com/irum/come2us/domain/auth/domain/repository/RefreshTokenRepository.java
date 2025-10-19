package com.irum.come2us.domain.auth.domain.repository;

import com.irum.come2us.domain.auth.domain.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {}
