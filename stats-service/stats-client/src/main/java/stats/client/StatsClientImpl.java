package stats.client;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import stats.dto.StatsHitDto;
import stats.dto.StatsViewDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class StatsClientImpl implements StatsClient {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final RestTemplate restTemplate;
    private final String serverUrl;

    public StatsClientImpl(String serverUrl) {
        this.serverUrl = serverUrl;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public void hit(StatsHitDto hitDto) {
        restTemplate.postForEntity(
                serverUrl + "/hit",
                hitDto,
                Void.class
        );
    }

    @Override
    public List<StatsViewDto> getStats(
            LocalDateTime start,
            LocalDateTime end,
            List<String> uris,
            boolean unique) {

        UriComponentsBuilder builder =
                UriComponentsBuilder.fromHttpUrl(serverUrl + "/stats")
                        .queryParam("start", start.format(FORMATTER))
                        .queryParam("end", end.format(FORMATTER))
                        .queryParam("unique", unique);

        if (uris != null && !uris.isEmpty()) {
            uris.forEach(uri -> builder.queryParam("uris", uri));
        }

        ResponseEntity<StatsViewDto[]> response =
                restTemplate.getForEntity(
                        builder.toUriString(),
                        StatsViewDto[].class
                );

        return Arrays.asList(response.getBody());
    }
}