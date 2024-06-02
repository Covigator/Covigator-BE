package com.ku.covigator.dto.request;

import com.ku.covigator.domain.travelstyle.*;
import lombok.Builder;

@Builder
public record PostTravelStyleRequest(AreaType areaType, Familiarity familiarity, ActivityType activityType,
                                     PlanningType planningType, PhotoPriority photoPriority, Popularity popularity) {

    public TravelStyle toEntity() {
        return TravelStyle.builder()
                .areaType(areaType)
                .familiarity(familiarity)
                .activityType(activityType)
                .planningType(planningType)
                .photoPriority(photoPriority)
                .popularity(popularity)
                .build();
    }

}
