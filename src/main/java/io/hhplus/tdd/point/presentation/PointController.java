package io.hhplus.tdd.point.presentation;

import io.hhplus.tdd.point.application.service.PointService;
import io.hhplus.tdd.point.domain.model.entity.PointHistory;
import io.hhplus.tdd.point.domain.model.entity.UserPoint;
import io.hhplus.tdd.point.presentation.dto.ChargeUserPointRequest;
import io.hhplus.tdd.point.presentation.dto.PointHistoryResponse;
import io.hhplus.tdd.point.presentation.dto.UseUserPointRequest;
import io.hhplus.tdd.point.presentation.dto.UserPointResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RequiredArgsConstructor
@RestController
@RequestMapping("/point")
public class PointController {

    private final PointService pointService;


    @GetMapping("{id}")
    public ResponseEntity<UserPointResponse> point(
            @PathVariable("id") long id
    ) {
        final UserPoint userPoint = pointService.getUserPointByUserId(id);
        return ok(UserPointResponse.of(userPoint));
    }


    @GetMapping("{id}/histories")
    public ResponseEntity<List<PointHistoryResponse>> history(
            @PathVariable("id") long id
    ) {

        final List<PointHistory> pointHistories = pointService.getHistoriesByUserId(id);
        return ok(pointHistories.stream()
                .map(PointHistoryResponse::of).toList());
    }


    @PatchMapping("{id}/charge")
    public ResponseEntity<UserPointResponse> charge(
            @PathVariable("id") long id,
            @RequestBody ChargeUserPointRequest request
    ) {
        final UserPoint userPoint = pointService.charge(request.toCommand(id));
        return ok(UserPointResponse.of(userPoint));
    }


    @PatchMapping("{id}/use")
    public ResponseEntity<UserPointResponse> use(
            @PathVariable("id") long id,
            @RequestBody UseUserPointRequest request
    ) {
        final UserPoint userPoint = pointService.use(request.toCommand(id));
        return ok(UserPointResponse.of(userPoint));
    }
}
