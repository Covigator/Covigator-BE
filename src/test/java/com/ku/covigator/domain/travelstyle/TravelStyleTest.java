package com.ku.covigator.domain.travelstyle;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class TravelStyleTest {

    @DisplayName("여행 스타일을 부분만 변경한다.")
    @Test
    void patchTravelStyle() {
        //given
        TravelStyle travelStyle = TravelStyle.builder()
                .areaType(AreaType.CITY)
                .planningType(PlanningType.PLANNED)
                .familiarity(Familiarity.FAMILIAR)
                .photoPriority(PhotoPriority.IMPORTANT)
                .popularity(Popularity.WELL_KNOWN)
                .activityType(ActivityType.ACTIVITY)
                .build();

        TravelStyle newTravelStyle = TravelStyle.builder()
                .photoPriority(PhotoPriority.NOT_IMPORTANT)
                .popularity(Popularity.NOT_WIDELY_KNOWN)
                .build();

        //when
        travelStyle.patchTravelStyle(newTravelStyle);

        //then
        assertThat(travelStyle.getPhotoPriority()).isEqualTo(PhotoPriority.NOT_IMPORTANT);
        assertThat(travelStyle.getPopularity()).isEqualTo(Popularity.NOT_WIDELY_KNOWN);
        assertThat(travelStyle.getAreaType()).isEqualTo(AreaType.CITY);
        assertThat(travelStyle.getPlanningType()).isEqualTo(PlanningType.PLANNED);
        assertThat(travelStyle.getFamiliarity()).isEqualTo(Familiarity.FAMILIAR);
        assertThat(travelStyle.getActivityType()).isEqualTo(ActivityType.ACTIVITY);
    }

}