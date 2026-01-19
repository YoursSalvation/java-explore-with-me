package stats.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import stats.dto.StatsHitDto;
import stats.dto.StatsViewDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class StatsClientImpl implements StatsClient {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final RestTemplate restTemplate;
    private final String serverUrl;

    public StatsClientImpl(
            @Value("${stats-server.url}") String serverUrl,
            RestTemplateBuilder builder
    ) {
        this.serverUrl = serverUrl;
        this.restTemplate = builder.build();
    }

    @Override
    public void hit(String app, String uri, String ip) {
        StatsHitDto dto = StatsHitDto.builder()
                .app(app)
                .uri(uri)
                .ip(ip)
                .timestamp(LocalDateTime.now())
                .build();

        restTemplate.postForEntity(
                serverUrl + "/hit",
                dto,
                Void.class
        );
    }

    @Override
    public List<StatsViewDto> getStats(
            LocalDateTime start,
            LocalDateTime end,
            List<String> uris,
            boolean unique
    ) {
        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(serverUrl + "/stats")
                        .queryParam("start", start.format(FORMATTER))
                        .queryParam("end", end.format(FORMATTER))
                        .queryParam("unique", unique);

        if (uris != null) {
            uris.forEach(uri -> builder.queryParam("uris", uri));
        }

        StatsViewDto[] response = restTemplate.getForObject(
                builder.toUriString(),
                StatsViewDto[].class
        );

        return response == null ? List.of() : List.of(response);
    }
}