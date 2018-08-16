package com.sequenceiq.cloudbreak.orchestrator.salt.test;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.sequenceiq.cloudbreak.concurrent.MDCCleanerTaskDecorator;
import com.sequenceiq.cloudbreak.orchestrator.executor.ParallelOrchestratorComponentRunner;
import com.sequenceiq.cloudbreak.orchestrator.salt.SaltOrchestrator;
import com.sequenceiq.cloudbreak.orchestrator.state.ExitCriteria;
import com.sequenceiq.cloudbreak.orchestrator.state.ExitCriteriaModel;

@Configuration
@ComponentScan("com.sequenceiq.cloudbreak.orchestrator.salt")
public class Config {

    public static final ExitCriteria EXIT_CRITERIA = new ExitCriteria() {
        @Override
        public boolean isExitNeeded(ExitCriteriaModel exitCriteriaModel) {
            return false;
        }

        @Override
        public String exitMessage() {
            return "No more questions";
        }
    };

    //@Value("${cb.container.threadpool.core.size:}")
    private int containerCorePoolSize = 2;

    //@Value("${cb.container.threadpool.capacity.size:}")
    private int containerteQueueCapacity = 2;

    @Bean
    public SaltOrchestrator saltOrchestrator() {
        SaltOrchestrator saltOrchestrator = new SaltOrchestrator();
        saltOrchestrator.init(simpleParallelContainerRunnerExecutor(), clusterDeletionBasedExitCriteria());
        return saltOrchestrator;
    }

    @Bean
    public ParallelOrchestratorComponentRunner simpleParallelContainerRunnerExecutor() {
        return new TestOrchestratorComponentRunner(containerBootstrapBuilderExecutor());
    }

    @Bean
    public ExitCriteria clusterDeletionBasedExitCriteria() {
        return EXIT_CRITERIA;
    }

    @Bean
    public AsyncTaskExecutor containerBootstrapBuilderExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(containerCorePoolSize);
        executor.setQueueCapacity(containerteQueueCapacity);
        executor.setThreadNamePrefix("containerBootstrapBuilderExecutor-");
        executor.setTaskDecorator(new MDCCleanerTaskDecorator());
        executor.initialize();
        return executor;
    }

    static class TestOrchestratorComponentRunner implements ParallelOrchestratorComponentRunner {

        private final AsyncTaskExecutor asyncTaskExecutor;

        TestOrchestratorComponentRunner(AsyncTaskExecutor asyncTaskExecutor) {
            this.asyncTaskExecutor = asyncTaskExecutor;
        }

        @Override
        public Future<Boolean> submit(Callable<Boolean> callable) {
            return asyncTaskExecutor.submit(callable);
        }
    }
}
