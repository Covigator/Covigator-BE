package com.ku.covigator.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class ShortTermWeatherForecastResponse {

    private Response response;

    @Getter
    @NoArgsConstructor
    public static class Response {

        private Body body;

        @Getter
        @NoArgsConstructor
        public static class Body {

            private Items items;

            @Getter
            @NoArgsConstructor
            public static class Items {

                private List<Item> item;

                @Getter
                @NoArgsConstructor
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