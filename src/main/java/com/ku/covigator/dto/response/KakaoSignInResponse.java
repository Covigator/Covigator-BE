package com.ku.covigator.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.ku.covigator.domain.member.Gender;
import com.ku.covigator.domain.member.Generation;
import com.ku.covigator.domain.travelstyle.*;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record KakaoSignInResponse(String accessToken, String isNew, String refreshToken, String nickname, String email, String imageUrl, Gender gender, Generation generation, TravelStyleDto travelStyle) {

    public static KakaoSignInResponse fromNewMember(String accessToken, String refreshToken, String nickname, String email, String imageUrl) {
        return new KakaoSignInResponse(accessToken, "True", refreshToken, nickname, email, imageUrl, null, null, null);
    }

    public static KakaoSignInResponse fromOldMember(String accessToken, String refreshToken, String nickname, String email, String imageUrl, Gender gender, Generation generation, TravelStyleDto travelStyle) {
        return new KakaoSignInResponse(accessToken, "False", refreshToken, nickname, email, imageUrl, gender, generation, travelStyle);
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
