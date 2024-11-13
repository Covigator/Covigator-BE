package com.ku.covigator.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.ku.covigator.domain.member.Gender;
import com.ku.covigator.domain.member.Generation;
import com.ku.covigator.domain.travelstyle.*;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record LocalSignInResponse(String accessToken, String refreshToken, String nickname, String email, String imageUrl,
                                  Gender gender, Generation generation, TravelStyleDto travelStyle) {

    public static LocalSignInResponse from(final String accessToken, final String refreshToken, final String nickname,
                                           final String email, final String imageUrl, final Gender gender,
                                           final Generation generation, final TravelStyleDto travelStyle) {
        return new LocalSignInResponse(accessToken, refreshToken, nickname, email, imageUrl, gender, generation, travelStyle);
    }

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record TravelStyleDto(ActivityType activityType, AreaType areaType, Familiarity familiarity, PhotoPriority photoPriority,
                                 PlanningType planningType, Popularity popularity) {

        public static TravelStyleDto from(TravelStyle travelStyle) {
            return travelStyle == null ? null : new TravelStyleDto(
                    travelStyle.getActivityType(),
                    travelStyle.getAreaType(),
                    travelStyle.getFamiliarity(),
                    travelStyle.getPhotoPriority(),
                    travelStyle.getPlanningType(),
                    travelStyle.getPopularity()
            );
        }
    }

}
