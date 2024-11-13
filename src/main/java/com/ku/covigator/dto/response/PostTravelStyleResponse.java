package com.ku.covigator.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.ku.covigator.domain.member.Gender;
import com.ku.covigator.domain.member.Generation;
import com.ku.covigator.domain.travelstyle.*;
import lombok.Builder;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record PostTravelStyleResponse(ActivityType activityType, AreaType areaType, Familiarity familiarity, PhotoPriority photoPriority,
                                      PlanningType planningType, Popularity popularity, Gender gender, Generation generation) {

    public static PostTravelStyleResponse from(TravelStyle travelStyle, Gender gender, Generation generation) {
        return PostTravelStyleResponse.builder()
                .activityType(travelStyle.getActivityType())
                .areaType(travelStyle.getAreaType())
                .familiarity(travelStyle.getFamiliarity())
                .photoPriority(travelStyle.getPhotoPriority())
                .planningType(travelStyle.getPlanningType())
                .popularity(travelStyle.getPopularity())
                .gender(gender)
                .generation(generation)
                .build();
    }
}
