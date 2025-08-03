package com.michiki.michiki.place.repository;

import com.michiki.michiki.place.entity.Place;
import com.michiki.michiki.plan.entity.Plan;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place, Long> {
    Optional<Place> findByPlanAndPlaceId(Plan plan, @NotNull Long placeId);

    List<Place> findByPlanAndTravelDate(Plan plan, @NotNull LocalDate travelDate);

    List<Place> findByPlanOrderByTravelDateAscOrderInDayAsc(Plan plan);

    List<Place> findByPlan(Plan plan);
}
