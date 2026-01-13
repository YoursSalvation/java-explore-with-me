package stats.dto;

import lombok.Data;

@Data
public class StatsViewDto {

    private String app;
    private String uri;
    private Long hits;

    public StatsViewDto(String app, String uri, Long hits) {
        this.app = app;
        this.uri = uri;
        this.hits = hits;
    }
}
