package io.hhplus.tdd.point.application.service;

import io.hhplus.tdd.point.application.dto.ChargeUserPointCommand;
import io.hhplus.tdd.point.application.dto.UseUserPointCommand;
import io.hhplus.tdd.point.domain.model.entity.PointHistory;
import io.hhplus.tdd.point.domain.model.entity.UserPoint;
import io.hhplus.tdd.point.domain.repository.PointHistoryRepository;
import io.hhplus.tdd.point.domain.repository.UserPointRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import static java.lang.System.currentTimeMillis;
import static org.assertj.core.api.Assertions.assertThat;


@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@SpringBootTest
class PointServiceConcurrencyTest {

    @Autowired
    private PointService pointService;

    @Autowired
    private UserPointRepository userPointRepository;

    @Autowired
    private PointHistoryRepository pointHistoryRepository;


    @Test
    void 동일한_사용자의_포인트를_동시에_10건_충전하면_정상적으로_충전된다() throws ExecutionException, InterruptedException {
        // given
        int numberOfThreads = 10;
        userPointRepository.save(new UserPoint(1L, 1000L, currentTimeMillis()));

        // when
        List<CompletableFuture<Void>> futures = IntStream.range(0, numberOfThreads)
                .mapToObj(i -> CompletableFuture.runAsync(() -> {
                    ChargeUserPointCommand command = ChargeUserPointCommand.of(1L, 10000L);
                    pointService.charge(command);
                })).toList();

        // 모든 작업이 완료될 때까지 대기
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();

        // then
        UserPoint finalPoint = pointService.getUserPointByUserId(1L);
        final List<PointHistory> pointHistories = pointService.getHistoriesByUserId(1L);
        assertThat(finalPoint.point()).isEqualTo(101000L);
        assertThat(pointHistories).hasSize(10);
    }

    @Test
    void 동일한_사용자의_포인트를_동시에_10건_사용하면_정상적으로_사용된다() throws ExecutionException, InterruptedException {
        // given
        int numberOfThreads = 10;
        userPointRepository.save(new UserPoint(1L, 100000L, currentTimeMillis()));

        // when
        List<CompletableFuture<Void>> futures = IntStream.range(0, numberOfThreads)
                .mapToObj(i -> CompletableFuture.runAsync(() -> {
                    UseUserPointCommand command = UseUserPointCommand.of(1L, 10000L);
                    pointService.use(command);
                })).toList();

        // 모든 작업이 완료될 때까지 대기
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();

        // then
        UserPoint finalPoint = pointService.getUserPointByUserId(1L);
        final List<PointHistory> pointHistories = pointService.getHistoriesByUserId(1L);
        assertThat(finalPoint.point()).isEqualTo(0L);
        assertThat(pointHistories).hasSize(10);
    }


    @Test
    void 두명_사용자의_포인트를_동시에_5건씩_충전하면_정상적으로_충전된다() throws ExecutionException, InterruptedException {
        // given
        int numberOfThreads = 10;
        userPointRepository.save(new UserPoint(1L, 1000L, currentTimeMillis()));
        userPointRepository.save(new UserPoint(2L, 5000L, currentTimeMillis()));

        // when
        List<CompletableFuture<Void>> futures = IntStream.range(0, numberOfThreads)
                .mapToObj(i -> CompletableFuture.runAsync(() -> {
                    long userId = i % 2 + 1;
                    ChargeUserPointCommand command = ChargeUserPointCommand.of(userId, 10000L);
                    pointService.charge(command);
                })).toList();

        // 모든 작업이 완료될 때까지 대기
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();


        // then
        UserPoint userPoint1 = pointService.getUserPointByUserId(1L);
        assertThat(userPoint1.point()).isEqualTo(51000L);
        final List<PointHistory> pointHistories1 = pointService.getHistoriesByUserId(1L);
        assertThat(pointHistories1).hasSize(5);


        UserPoint userPoint2 = pointService.getUserPointByUserId(2L);
        assertThat(userPoint2.point()).isEqualTo(55000L);
        final List<PointHistory> pointHistories2 = pointService.getHistoriesByUserId(2L);
        assertThat(pointHistories2).hasSize(5);
    }

    @Test
    void 동일한_사용자의_포인트를_동시에_충전하거나_사용할_수_있다() throws ExecutionException, InterruptedException {
        // Given
        int numberOfThreads = 5;
        userPointRepository.save(new UserPoint(1L, 100_000L, currentTimeMillis()));


        // 포인트 변화를 정의하는 리스트
        List<Long> pointChanges = List.of(10000L, -10000L, 20000L, -15000L, 15000L);

        // When
        List<CompletableFuture<Void>> futures = IntStream.range(0, numberOfThreads)
                .mapToObj(i -> CompletableFuture.runAsync(() -> {
                    long pointChange = pointChanges.get(i);
                    if (pointChange > 0) {
                        ChargeUserPointCommand chargeCommand = ChargeUserPointCommand.of(1L, pointChange);
                        pointService.charge(chargeCommand);
                    } else {
                        UseUserPointCommand useCommand = UseUserPointCommand.of(1L, -pointChange);
                        pointService.use(useCommand);}
                }))
                .toList();

        // 모든 작업이 완료될 때까지 대기
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();

        // Then
        UserPoint finalPoint = pointService.getUserPointByUserId(1L);
        assertThat(finalPoint.point()).isEqualTo(120_000L);

        final List<PointHistory> pointHistories = pointService.getHistoriesByUserId(1L);
        assertThat(pointHistories).hasSize(numberOfThreads);
    }



    @Test
    void 두명의_사용자의_포인트를_동시에_충전하거나_사용할_수_있다() throws ExecutionException, InterruptedException {
        // given
        int numberOfThreads = 10;
        userPointRepository.save(new UserPoint(1L, 100_000L, currentTimeMillis()));
        userPointRepository.save(new UserPoint(2L, 200_000L, currentTimeMillis()));

        // 유저별 포인트 변화를 정의하는 리스트
        List<Long> user1PointChanges = List.of(10000L, -10000L, 20000L, -15000L, 15000L);
        List<Long> user2PointChanges = List.of(20000L, -20000L, 30000L, -25000L, 25000L);

        // when
        List<CompletableFuture<Void>> futures = IntStream.range(0, numberOfThreads)
                .mapToObj(i -> CompletableFuture.runAsync(() -> {
                    long userId = i % 2 + 1;
                    long pointChange = userId == 1 ? user1PointChanges.get(i / 2) : user2PointChanges.get(i / 2);
                    if (pointChange > 0) {
                        ChargeUserPointCommand chargeCommand = ChargeUserPointCommand.of(userId, pointChange);
                        pointService.charge(chargeCommand);
                    } else {
                        UseUserPointCommand useCommand = UseUserPointCommand.of(userId, -pointChange);
                        pointService.use(useCommand);
                    }
                }))
                .toList();

        // 모든 작업이 완료될 때까지 대기
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();

        // then
        UserPoint userPoint1 = pointService.getUserPointByUserId(1L);
        assertThat(userPoint1.point()).isEqualTo(120_000L);
        final List<PointHistory> pointHistories1 = pointService.getHistoriesByUserId(1L);
        assertThat(pointHistories1).hasSize(5);

        UserPoint userPoint2 = pointService.getUserPointByUserId(2L);
        assertThat(userPoint2.point()).isEqualTo(230_000L);
        final List<PointHistory> pointHistories2 = pointService.getHistoriesByUserId(2L);
        assertThat(pointHistories2).hasSize(5);
    }



    @Test
    void 동일한_사용자에_대한_충전_요청이_들어오면_포인트가_정상적으로_충전된다() throws InterruptedException {
        // given
        userPointRepository.save(new UserPoint(1L, 1_000L, currentTimeMillis()));

        // when
        final int threadCount = 30;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        IntStream.range(0, threadCount).forEach(i -> {
            executorService.execute(() -> {
                final ChargeUserPointCommand command = ChargeUserPointCommand.of(1L, 10000L);
                pointService.charge(command);
                countDownLatch.countDown();
            });
        });

        countDownLatch.await();

        // then
        UserPoint userPoint = pointService.getUserPointByUserId(1L);
        final List<PointHistory> pointHistories = pointService.getHistoriesByUserId(1L);

        assertThat(userPoint.point()).isEqualTo(301000L);
        assertThat(pointHistories).hasSize(threadCount);
    }

    @Test
    void 동일한_사용자에_대한_사용_요청이_들어오면_포인트가_정상적으로_사용된다() throws InterruptedException {
        // given
        final ChargeUserPointCommand chargeCommand = ChargeUserPointCommand.of(1L, 1_000_000L);
        pointService.charge(chargeCommand);

        // when
        final int threadCount = 30;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        IntStream.range(0, threadCount).forEach(i -> {
            executorService.execute(() -> {
                final UseUserPointCommand command = UseUserPointCommand.of(1L, 10_000L);
                pointService.use(command);
                latch.countDown();
            });
        });

        latch.await();

        // then
        UserPoint userPoint = pointService.getUserPointByUserId(1L);
        final List<PointHistory> pointHistories = pointService.getHistoriesByUserId(1L);

        assertThat(userPoint.point()).isEqualTo(700_000L);
        assertThat(pointHistories).hasSize(31);
    }
}