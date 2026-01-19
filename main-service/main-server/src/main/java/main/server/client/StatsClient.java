package main.server.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import stats.dto.StatsHitDto;
import stats.dto.StatsViewDto;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StatsClient {

    private final RestTemplate restTemplate;

    @Value("${stats-server.url}")
    private String statsServerUrl;

    public void hit(String app, String uri, String ip) {
        StatsHitDto dto = StatsHitDto.builder()
                .app(app)
                .uri(uri)
                .ip(ip)
                .timestamp(LocalDateTime.now())
                .build();

        restTemplate.postForEntity(
                statsServerUrl + "/hit",
                dto,
                Void.class
        );
    }

    public List<StatsViewDto> getStats(
            String start,
            String end,
            List<String> uris,
            boolean unique
    ) {
        String url = statsServerUrl +
                "/stats?start={start}&end={end}&uris={uris}&unique={unique}";

        StatsViewDto[] response = restTemplate.getForObject(
                url,
                StatsViewDto[].class,
                start,
                end,
                String.join(",", uris),
                unique
        );

        return response == null ? List.of() : List.of(response);
    }
}