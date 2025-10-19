package com.irum.come2us.domain.auth.domain.repository;

import com.irum.come2us.domain.auth.domain.entity.BlackListToken;
import org.springframework.data.repository.CrudRepository;

public interface BlackListTokenRepository extends CrudRepository<BlackListToken, Long> {}
