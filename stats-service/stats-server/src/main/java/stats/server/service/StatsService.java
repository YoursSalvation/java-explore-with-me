package stats.server.service;

import stats.dto.StatsHitDto;
import stats.dto.StatsViewDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {

    void saveHit(StatsHitDto hitDto);

    List<StatsViewDto> getStats(
            LocalDateTime start,
            LocalDateTime end,
            List<String> uris,
            boolean unique
    );
}