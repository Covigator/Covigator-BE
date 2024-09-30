package com.ku.covigator.domain.member;
import com.ku.covigator.domain.travelstyle.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class MemberTest {

    @DisplayName("멤버는 기본 'ACTIVE' 상태로 생성된다.")
    @Test
    void memberCreatedInActiveStatus() {
        //given
        Member member = Member.builder()
                .email("covi@naver.com")
                .password("covigator123")
                .nickname("covi")
                .imageUrl("www.covi.com")
                .platform(Platform.LOCAL)
                .build();

        //when //then
        assertThat(member.getStatus()).isEqualTo(Status.ACTIVE);
    }

    @DisplayName("회원의 여행 스타일을 저장한다.")
    @Test
    void putTravelStyle() {
        //given
        Member member = Member.builder()
                .email("covi@naver.com")
                .password("covigator123")
                .nickname("covi")
                .imageUrl("www.covi.com")
                .platform(Platform.LOCAL)
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
                .email("covi@naver.com")
                .password("covigator123")
                .nickname("covi")
                .imageUrl("www.covi.com")
                .platform(Platform.LOCAL)
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

    @DisplayName("프로필 이미지를 추가한다.")
    @Test
    void addImageUrl() {
        //given
        Member member = Member.builder()
                .email("covi@naver.com")
                .password("covigator123")
                .nickname("covi")
                .platform(Platform.LOCAL)
                .build();

        //when
        member.addImageUrl("www.covi.com");

        //then
        assertThat(member.getImageUrl()).isEqualTo("www.covi.com");
    }

    @DisplayName("회원 정보를 수정한다.")
    @Test
    void updateMemberInfo() {
        //given
        Member member = Member.builder()
                .email("covi@naver.com")
                .password("covigator123")
                .nickname("covi")
                .platform(Platform.LOCAL)
                .build();

        //when
        member.updateMemberInfo("covi2", "covigator123!");

        //then
        assertThat(member.getNickname()).isEqualTo("covi2");
        assertThat(member.getPassword()).isEqualTo("covigator123!");
    }
}