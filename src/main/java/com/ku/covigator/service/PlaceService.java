package com.ku.covigator.service;

import com.ku.covigator.domain.Place;
import com.ku.covigator.exception.notfound.NotFoundPlaceException;
import com.ku.covigator.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceRepository placeRepository;

    public Place getPlaceInfo(String name, String address) {
        return placeRepository.findByNameAndAddress(name, address)
                .orElseThrow(NotFoundPlaceException::new);
    }

}
