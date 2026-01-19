package stats.client;

import stats.dto.StatsViewDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsClient {

    void hit(String app, String uri, String ip);

    List<StatsViewDto> getStats(
            LocalDateTime start,
            LocalDateTime end,
            List<String> uris,
            boolean unique
    );
}