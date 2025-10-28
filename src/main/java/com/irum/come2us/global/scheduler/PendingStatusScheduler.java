package com.irum.come2us.global.scheduler;

import com.irum.come2us.domain.order.application.service.OrderBatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PendingStatusScheduler {

    private final OrderBatchService orderBatchService;

    /** 1분마다 실행되어 오래된 PENDING 항목을 FAIL로 변경 */
    @Scheduled(fixedRate = 60000) // 1분 마다 실행
    public void updateStalePendingToFail() {
        log.debug("오래된 PENDING 상태 확인 스케줄러 실행...");

        orderBatchService.processStalePendingOrders();
    }
}
