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
        long userId = 1L;
        long initialAmount = 500L;
        long chargeAmount = 100L;
        UserPoint initialUserPoint = new UserPoint(userId, initialAmount, System.currentTimeMillis());
        userPointRepository.save(initialUserPoint);

        // when & then
        mockMvc.perform(patch("/point/{id}/charge", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(chargeAmount)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.point").value(initialAmount + chargeAmount));

        assertEquals(1, pointHistoryRepository.selectAllByUserId(userId).size());
    }

    @Test
    @DisplayName("양수가 아닌 포인트는 충전할 수 없다.")
    void charge_shouldNotChargeNegativePoint() throws Exception {
        // given
        long userId = 1L;
        long initialAmount = 500L;
        userPointRepository.save(new UserPoint(userId, initialAmount, System.currentTimeMillis()));

        // when & then
        mockMvc.perform(patch("/point/{id}/charge", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("-100"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof IllegalArgumentException))
                .andExpect(result -> assertEquals("충전할 포인트는 0보다 커야 합니다.", result.getResolvedException().getMessage()));
    }
}