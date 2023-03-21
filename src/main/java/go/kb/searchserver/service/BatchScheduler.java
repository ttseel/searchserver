package go.kb.searchserver.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BatchScheduler {
    @Autowired
    private BatchService batchService;

    @Scheduled(fixedDelayString = "${schedule.top10.period}")
    private void keywordUpdateSchedule() {
        batchService.batchUpdateKeyword();
    }
}
