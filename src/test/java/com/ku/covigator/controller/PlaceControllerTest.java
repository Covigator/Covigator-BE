package com.ku.covigator.controller;

import com.ku.covigator.domain.Place;
import com.ku.covigator.security.jwt.JwtAuthArgumentResolver;
import com.ku.covigator.security.jwt.JwtAuthInterceptor;
import com.ku.covigator.service.PlaceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PlaceController.class)
class PlaceControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PlaceService placeService;
    @MockBean
    JwtAuthInterceptor jwtAuthInterceptor;
    @MockBean
    JwtAuthArgumentResolver jwtAuthArgumentResolver;

    @DisplayName("장소 세부 정보를 조회한다.")
    @Test
    void test() throws Exception {
        //given
        String name = "코비식당";
        String address = "서울시 광진구";

        Place place = Place.builder()
                .name("코비식당")
                .address("서울시 광진구")
                .floor("1")
                .imageUrl("")
                .buildingName("건국대학교")
                .dongName("자양동")
                .coordinate(createPoint(1.0, 2.0))
                .category("식당")
                .build();

        BDDMockito.given(placeService.getPlaceInfo(name, address)).willReturn(place);

        //when //then
        mockMvc.perform(MockMvcRequestBuilders.get("/places")
                        .param("name", name)
                        .param("address", address)
                ).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("코비식당"))
                .andExpect(jsonPath("$.address").value("서울시 광진구"))
                .andExpect(jsonPath("$.floor").value("1"))
                .andExpect(jsonPath("$.building_name").value("건국대학교"))
                .andExpect(jsonPath("$.dong_name").value("자양동"))
                .andExpect(jsonPath("$.latitude").value("2.0"))
                .andExpect(jsonPath("$.longitude").value("1.0"))
                .andExpect(jsonPath("$.category").value("식당"));
    }

    private Point createPoint(double x, double y) {
        GeometryFactory geometryFactory = new GeometryFactory();
        return geometryFactory.createPoint(new Coordinate(x, y));
    }
}