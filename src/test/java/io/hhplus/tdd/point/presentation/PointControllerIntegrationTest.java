package io.hhplus.tdd.point.presentation;

import io.hhplus.tdd.point.domain.model.entity.UserPoint;
import io.hhplus.tdd.point.domain.repository.PointHistoryRepository;
import io.hhplus.tdd.point.domain.repository.UserPointRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PointControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserPointRepository userPointRepository;

    @Autowired
    private PointHistoryRepository pointHistoryRepository;

    @Test
    @DisplayName("사용자 포인트 충전 요청을 처리하고 포인트 히스토리를 저장한다.")
    void charge_shouldChargeUserPointAndSaveHistory() throws Exception {
        // given
        UserPoint initialUserPoint = new UserPoint(1L, 500L, System.currentTimeMillis());
        userPointRepository.save(initialUserPoint);

        // when & then
        mockMvc.perform(patch("/point/{id}/charge", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 10000}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.point").value(10_500L));

        assertEquals(1, pointHistoryRepository.selectAllByUserId(1L).size());
    }

    @Test
    @DisplayName("양수가 아닌 포인트는 충전할 수 없다.")
    void charge_shouldNotChargeNegativePoint() throws Exception {
        // given
        userPointRepository.save(new UserPoint(1L, 500L, System.currentTimeMillis()));

        // when & then
        mockMvc.perform(patch("/point/{id}/charge", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": -100}"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof IllegalArgumentException))
                .andExpect(result -> assertEquals("충전할 포인트는 0보다 커야 합니다.", result.getResolvedException().getMessage()));
    }

    @Test
    @DisplayName("1000원 단위가 아닌 포인트는 충전할 수 없다.")
    void charge_shouldNotChargeNonThousandPoint() throws Exception {
        // given
        userPointRepository.save(new UserPoint(1L, 500L, System.currentTimeMillis()));

        // when & then
        mockMvc.perform(patch("/point/{id}/charge", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 1001}"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof IllegalArgumentException))
                .andExpect(result -> assertEquals("충전할 포인트는 1,000원 단위로 가능합니다.", result.getResolvedException().getMessage()));
    }

    @Test
    @DisplayName("10,000원 미만의 포인트는 충전할 수 없다.")
    void charge_shouldNotChargeLessThanTenThousandPoint() throws Exception {
        // given
        userPointRepository.save(new UserPoint(1L, 500L, System.currentTimeMillis()));

        // when & then
        mockMvc.perform(patch("/point/{id}/charge", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 9000}"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof IllegalArgumentException))
                .andExpect(result -> assertEquals("최소 충전 금액은 10,000원입니다.", result.getResolvedException().getMessage()));
    }

    @Test
    @DisplayName("최대 포인트를 넘는 포인트는 충전할 수 없다.")
    void charge_shouldNotChargeMoreThanMaxPoint() throws Exception {
        // given
        userPointRepository.save(new UserPoint(1L, 980_000L, System.currentTimeMillis()));

        // when & then
        mockMvc.perform(patch("/point/{id}/charge", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": 21000}"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof IllegalArgumentException))
                .andExpect(result -> assertEquals("최대 잔고를 초과할 수 없습니다.", result.getResolvedException().getMessage()));
    }

}
