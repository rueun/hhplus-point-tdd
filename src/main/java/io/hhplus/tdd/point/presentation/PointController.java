package io.hhplus.tdd.point.presentation;

import io.hhplus.tdd.point.application.dto.UseUserPointCommand;
import io.hhplus.tdd.point.application.service.PointService;
import io.hhplus.tdd.point.domain.model.entity.PointHistory;
import io.hhplus.tdd.point.domain.model.entity.UserPoint;
import io.hhplus.tdd.point.presentation.dto.ChargeUserPointRequest;
import io.hhplus.tdd.point.presentation.dto.UseUserPointRequest;
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

    private static final Logger log = LoggerFactory.getLogger(PointController.class);
    private final PointService pointService;

    /**
     * TODO - 특정 유저의 포인트를 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}")
    public UserPoint point(
            @PathVariable long id
    ) {
        return new UserPoint(0, 0, 0);
    }

    /**
     * TODO - 특정 유저의 포인트 충전/이용 내역을 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}/histories")
    public List<PointHistory> history(
            @PathVariable long id
    ) {
        return List.of();
    }


    @PatchMapping("{id}/charge")
    public ResponseEntity<UserPoint> charge(
            @PathVariable("id") long id,
            @RequestBody ChargeUserPointRequest request
    ) {
        return ok(pointService.charge(request.toCommand(id)));
    }


    @PatchMapping("{id}/use")
    public ResponseEntity<UserPoint> use(
            @PathVariable("id") long id,
            @RequestBody UseUserPointRequest request
    ) {
        return ok(pointService.use(request.toCommand(id)));
    }
}
