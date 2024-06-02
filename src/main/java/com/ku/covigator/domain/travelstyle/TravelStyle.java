package com.ku.covigator.domain.travelstyle;

import com.ku.covigator.domain.BaseTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TravelStyle extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "area_type")
    @Enumerated(value = EnumType.STRING)
    private AreaType areaType;

    @Column(name = "familiarity")
    @Enumerated(value = EnumType.STRING)
    private Familiarity familiarity;

    @Column(name = "activity_type")
    @Enumerated(value = EnumType.STRING)
    private ActivityType activityType;

    @Column(name = "popularity")
    @Enumerated(value = EnumType.STRING)
    private Popularity popularity;

    @Column(name = "planning_type")
    @Enumerated(value = EnumType.STRING)
    private PlanningType planningType;

    @Column(name = "photo_priority")
    @Enumerated(value = EnumType.STRING)
    private PhotoPriority photoPriority;

    @Builder
    public TravelStyle(AreaType areaType, Familiarity familiarity, ActivityType activityType, Popularity popularity,
                       PlanningType planningType, PhotoPriority photoPriority) {
        this.areaType = areaType;
        this.familiarity = familiarity;
        this.activityType = activityType;
        this.popularity = popularity;
        this.planningType = planningType;
        this.photoPriority = photoPriority;
    }

    public void patchTravelStyle(TravelStyle newTravelStyle) {
        this.areaType = newTravelStyle.areaType != null ? newTravelStyle.areaType : this.areaType;
        this.familiarity = newTravelStyle.familiarity != null ? newTravelStyle.familiarity : this.familiarity;
        this.activityType = newTravelStyle.activityType != null ? newTravelStyle.activityType : this.activityType;
        this.popularity = newTravelStyle.popularity != null ? newTravelStyle.popularity : this.popularity;
        this.planningType = newTravelStyle.planningType != null ? newTravelStyle.planningType : this.planningType;
        this.photoPriority = newTravelStyle.photoPriority != null ? newTravelStyle.photoPriority : this.photoPriority;
    }

}
