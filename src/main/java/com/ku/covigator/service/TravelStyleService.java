package com.ku.covigator.service;

import com.ku.covigator.domain.member.Member;
import com.ku.covigator.domain.travelstyle.TravelStyle;
import com.ku.covigator.exception.notfound.NotFoundMemberException;
import com.ku.covigator.repository.MemberRepository;
import com.ku.covigator.repository.TravelStyleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class TravelStyleService {

    private final TravelStyleRepository travelStyleRepository;
    private final MemberRepository memberRepository;

    public void saveTravelStyle(Long memberId, TravelStyle travelStyle) {
        TravelStyle savedTravelStyle = travelStyleRepository.save(travelStyle);
        Member savedMember = memberRepository.findById(memberId).orElseThrow(NotFoundMemberException::new);
        savedMember.putTravelStyle(savedTravelStyle);
    }

    public void updateTravelStyle(Long memberId, TravelStyle travelStyle) {
        Member savedMember = memberRepository.findById(memberId).orElseThrow(NotFoundMemberException::new);
        // 여행 스타일 정보가 없으면 새로 저장한다.
        if(savedMember.getTravelStyle() == null) {
            saveTravelStyle(memberId, travelStyle);
        }
        // 여행 스타일 정보가 저장되어 있으면 수정한다.
        savedMember.updateTravelStyle(travelStyle);
    }

}
