package com.ku.covigator.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ShortTermWeatherForecastResponse {

    private Response response;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {

        private Body body;

        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Body {

            private Items items;

            @Getter
            @NoArgsConstructor
            @AllArgsConstructor
            public static class Items {

                private List<Item> item;

                @Getter
                @NoArgsConstructor
                @AllArgsConstructor
                public static class Item {

                    private String category;
                    private String fcstDate;
                    private String fcstTime;
                    private String fcstValue;

                }

            }

        }

    }

    public List<Response.Body.Items.Item> getItem() {
        return this.getResponse().getBody().getItems().getItem();
    }
}