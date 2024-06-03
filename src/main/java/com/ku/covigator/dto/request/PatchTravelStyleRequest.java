package com.ku.covigator.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.ku.covigator.domain.travelstyle.*;
import lombok.Builder;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record PatchTravelStyleRequest(AreaType areaType, Familiarity familiarity, ActivityType activityType,
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
