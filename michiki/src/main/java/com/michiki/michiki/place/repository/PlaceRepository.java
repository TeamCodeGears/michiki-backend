package com.michiki.michiki.place.repository;

import com.michiki.michiki.place.entity.Place;
import com.michiki.michiki.plan.entity.Plan;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

    // place Entity DB 접근 담당
public interface PlaceRepository extends JpaRepository<Place, Long> {

    // 특정 계획 내 특정 장소 ID로 장소 조회
    Optional<Place> findByPlanAndPlaceId(Plan plan, @NotNull Long placeId);

    // 특정 계획/ 특정 일자 해당 장소 조회
    List<Place> findByPlanAndTravelDate(Plan plan, @NotNull LocalDate travelDate);

    // 특정 계획 장소 날짜 오름차순/ 일일 순서 오름차수 정렬 조회
    List<Place> findByPlanOrderByTravelDateAsc(Plan plan);

    // 특정 계획 포함된 모든 장소 목록 조회
    List<Place> findByPlan(Plan plan);
}
