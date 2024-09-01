package com.ku.covigator.service;

import com.ku.covigator.domain.member.Member;
import com.ku.covigator.domain.member.Platform;
import com.ku.covigator.domain.travelstyle.*;
import com.ku.covigator.exception.notfound.NotFoundMemberException;
import com.ku.covigator.repository.MemberRepository;
import com.ku.covigator.repository.TravelStyleRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class TravelStyleServiceTest {

    @Autowired
    TravelStyleService travelStyleService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    TravelStyleRepository travelStyleRepository;

    @BeforeEach
    void tearDown() {
        memberRepository.deleteAllInBatch();
    }

    @DisplayName("회원의 여행 스타일을 저장한다.")
    @Test
    void saveTravelStyle() {
        //given
        Member member = createMember();
        TravelStyle travelStyle = createTravelStyle();

        memberRepository.save(member);

        //when
        travelStyleService.saveTravelStyle(member.getId(), travelStyle);
        List<TravelStyle> travelStyles = travelStyleRepository.findAll();
        TravelStyle savedTravelStyle = travelStyles.get(0);
        Member savedMember = memberRepository.findById(member.getId()).get();

        //then
        assertAll(
                () -> assertThat(savedTravelStyle.getId()).isEqualTo(travelStyle.getId()),
                () -> assertThat(savedMember.getTravelStyle()).usingRecursiveComparison().isEqualTo(travelStyle)
        );
    }

    @DisplayName("존재하지 않는 회원에 대한 여행 스타일 저장 시 예외가 발생한다.")
    @Test
    void saveTravelStyleFailsWhenNoMemberExists() {
        //given
        TravelStyle travelStyle = createTravelStyle();

        //when //then
        assertThatThrownBy(() -> travelStyleService.saveTravelStyle(1L, travelStyle))
                .isInstanceOf(NotFoundMemberException.class);
    }

    @DisplayName("회원의 여행 스타일 정보를 수정한다.")
    @Test
    void updateTravelStyle() {
        //given
        Member member = createMember();
        TravelStyle travelStyle = createTravelStyle();

        member.putTravelStyle(travelStyle);
        Long savedMemberId = memberRepository.save(member).getId();
        Long savedTravelStyleId = travelStyleRepository.save(travelStyle).getId();

        TravelStyle updatedTravelStyle = TravelStyle.builder()
                .areaType(AreaType.NATURE)
                .build();

        //when
        travelStyleService.updateTravelStyle(savedMemberId, updatedTravelStyle);
        TravelStyle savedTravelStyle = travelStyleRepository.findById(savedTravelStyleId).get();

        //then
        assertAll(
                () -> assertThat(savedTravelStyle.getAreaType()).isEqualTo(AreaType.NATURE),
                () -> assertThat(savedTravelStyle.getPlanningType()).isEqualTo(PlanningType.PLANNED),
                () -> assertThat(savedTravelStyle.getFamiliarity()).isEqualTo(Familiarity.FAMILIAR),
                () -> assertThat(savedTravelStyle.getPhotoPriority()).isEqualTo(PhotoPriority.IMPORTANT),
                () -> assertThat(savedTravelStyle.getPopularity()).isEqualTo(Popularity.WELL_KNOWN),
                () -> assertThat(savedTravelStyle.getActivityType()).isEqualTo(ActivityType.ACTIVITY)
        );
    }

    @DisplayName("회원의 여행 스타일 정보를 수정 요청 시 저장되어 있는 여행 스타일 정보가 없다면 새로 등록한다.")
    @Test
    void saveTravelStyleWhenNoTravelStyleExistsDespiteOfUpdateRequest() {
        //given
        Member member = createMember();
        Long savedMemberId = memberRepository.save(member).getId();

        TravelStyle updatedTravelStyle = TravelStyle.builder()
                .areaType(AreaType.NATURE)
                .build();

        //when
        travelStyleService.updateTravelStyle(savedMemberId, updatedTravelStyle);
        List<TravelStyle> travelStyles = travelStyleRepository.findAll();
        Long travelStyleId = travelStyles.get(0).getId();
        TravelStyle savedTravelStyle = travelStyleRepository.findById(travelStyleId).get();

        //then
        Assertions.assertAll(
                () -> assertThat(travelStyleId).isEqualTo(savedTravelStyle.getId()),
                () -> assertThat(savedTravelStyle.getAreaType()).isEqualTo(AreaType.NATURE)
        );
    }

    @DisplayName("존재하지 않는 회원에 대한 여행 스타일 정보 수정 시 예외가 발생한다.")
    @Test
    void updateTravelStyleFailsWhenNoMemberExists() {
        //given
        TravelStyle travelStyle = createTravelStyle();

        //when //then
        assertThatThrownBy(() -> travelStyleService.updateTravelStyle(1L, travelStyle))
                .isInstanceOf(NotFoundMemberException.class);
    }

    private Member createMember() {
        return Member.builder()
                .name("김코비")
                .email("covi@naver.com")
                .password("covigator123")
                .nickname("covi")
                .imageUrl("www.covi.com")
                .platform(Platform.LOCAL)
                .build();
    }

    private TravelStyle createTravelStyle() {
        return TravelStyle.builder()
                .areaType(AreaType.CITY)
                .planningType(PlanningType.PLANNED)
                .familiarity(Familiarity.FAMILIAR)
                .photoPriority(PhotoPriority.IMPORTANT)
                .popularity(Popularity.WELL_KNOWN)
                .activityType(ActivityType.ACTIVITY)
                .build();
    }

}