package com.sheelu.spring.auth.controllers.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TimezoneResponseDTO {
    private String userName;
    private String timezoneId;
    private String name;
    private String city;
    private String tzPrettyString;
    private String currentTimeLocal;
    private String currentTimeGMT;
    private TimeDiff diffWithGMT;

    @Data
    public static class TimeDiff {
        private Integer hours;
        private Integer minutes;
        private Boolean isAhead;

        public String prettyString() {
            String diff = String.format("%02d", this.hours) + ":" + String.format("%02d", this.minutes);
            if (this.isAhead) {
                return "GMT+" + diff;
            } else {
                return "GMT-" + diff;
            }
        }
    }
}
