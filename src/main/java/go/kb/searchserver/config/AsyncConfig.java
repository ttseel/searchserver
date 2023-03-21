package go.kb.searchserver.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig {
    @Getter
    private static final ThreadPoolTaskExecutor searchExecutor = new ThreadPoolTaskExecutor();

    // TODO Async로 멀티 쓰레딩을 사용할 것 같진 않다.
    // Request 요청을 멀티 쓰레딩으로 처리해서 답변을 줄 수 있지 않을까 했는데 그건 WebFlux인 듯..
    // kakao에 요청하고 응답받는걸 멀티 쓰레딩으로 처리한다 해도, Controller가 reponse를 주는게 싱글 쓰레드여서
    // 결국 순차적으로 Response를 하게 된다
    @Bean(name = "SearchProcessExecutor")
    public Executor searchProcessExecutor() {
        int numberOfProcessor = Runtime.getRuntime().availableProcessors();

        log.info("Set Async Core pool size to number of CPUs: {}", numberOfProcessor);
        searchExecutor.setCorePoolSize(10); // 사용 가능한 Thread 수
//        searchExecutor.setQueueCapacity(5); // CorePoolSize 만큼 Thread가 사용될 경우 Queueing 시킬 Task의 갯수
//        searchExecutor.setMaxPoolSize(10); // QueueCapacity 만큼 Task가 꽉찰 경우 새롭게 생성할 Thread의 최대 갯수
        searchExecutor.setThreadNamePrefix("search-executor-thread-");
        searchExecutor.initialize();

        return searchExecutor;
    }
}
