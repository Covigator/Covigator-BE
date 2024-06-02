package com.ku.covigator.domain;

import com.ku.covigator.domain.travelstyle.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class MemberTest {

    @DisplayName("멤버는 기본 'ACTIVE' 상태로 생성된다.")
    @Test
    void memberCreatedInActiveStatus() {
        //given
        Member member = Member.builder()
                .name("김코비")
                .email("covi@naver.com")
                .password("covigator123")
                .nickname("covi")
                .imageUrl("www.covi.com")
                .build();

        //when //then
        assertThat(member.getStatus()).isEqualTo(Status.ACTIVE);
    }

    @DisplayName("회원의 여행 스타일을 저장한다.")
    @Test
    void putTravelStyle() {
        //given
        Member member = Member.builder()
                .name("김코비")
                .email("covi@naver.com")
                .password("covigator123")
                .nickname("covi")
                .imageUrl("www.covi.com")
                .build();

        TravelStyle travelStyle = TravelStyle.builder()
                .areaType(AreaType.CITY)
                .planningType(PlanningType.PLANNED)
                .familiarity(Familiarity.FAMILIAR)
                .photoPriority(PhotoPriority.IMPORTANT)
                .popularity(Popularity.WELL_KNOWN)
                .activityType(ActivityType.ACTIVITY)
                .build();

        //when
        member.putTravelStyle(travelStyle);

        //then
        assertThat(member.getTravelStyle()).usingRecursiveComparison().isEqualTo(travelStyle);
    }

    @DisplayName("회원의 여행 스타일을 변경한다.")
    @Test
    void updateTravelStyle() {
        //given
        Member member = Member.builder()
                .name("김코비")
                .email("covi@naver.com")
                .password("covigator123")
                .nickname("covi")
                .imageUrl("www.covi.com")
                .build();

        TravelStyle travelStyle = TravelStyle.builder()
                .areaType(AreaType.CITY)
                .planningType(PlanningType.PLANNED)
                .familiarity(Familiarity.FAMILIAR)
                .photoPriority(PhotoPriority.IMPORTANT)
                .popularity(Popularity.WELL_KNOWN)
                .activityType(ActivityType.ACTIVITY)
                .build();

        member.putTravelStyle(travelStyle);

        TravelStyle newTravelStyle = TravelStyle.builder()
                .photoPriority(PhotoPriority.NOT_IMPORTANT)
                .popularity(Popularity.NOT_WIDELY_KNOWN)
                .build();

        //when
        member.updateTravelStyle(newTravelStyle);

        //then
        assertThat(member.getTravelStyle().getPhotoPriority()).isEqualTo(PhotoPriority.NOT_IMPORTANT);
        assertThat(member.getTravelStyle().getPopularity()).isEqualTo(Popularity.NOT_WIDELY_KNOWN);
        assertThat(member.getTravelStyle().getAreaType()).isEqualTo(AreaType.CITY);
        assertThat(member.getTravelStyle().getPlanningType()).isEqualTo(PlanningType.PLANNED);
        assertThat(member.getTravelStyle().getFamiliarity()).isEqualTo(Familiarity.FAMILIAR);
        assertThat(member.getTravelStyle().getActivityType()).isEqualTo(ActivityType.ACTIVITY);
    }

}