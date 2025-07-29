package com.michiki.michiki.place.repository;

import com.michiki.michiki.place.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepository extends JpaRepository<Place, Long> {
}
