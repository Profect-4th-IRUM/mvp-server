package com.irum.come2us.domain.ai.domain.repository;

import com.irum.come2us.domain.ai.domain.entity.Ai;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiRepository extends JpaRepository<Ai, UUID> {
    //    // 추가 메서드는 필요시에만 구현
    //    List<Ai> findByProductProductId(UUID productId);
}
