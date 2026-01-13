package stats.server.repository;

import stats.dto.StatsViewDto;
import stats.server.model.Hit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface HitRepository extends JpaRepository<Hit, Long> {

    @Query("""
        SELECT new stats.dto.StatsViewDto(
            h.app,
            h.uri,
            COUNT(DISTINCT h.ip)
        )
        FROM Hit h
        WHERE h.timestamp BETWEEN :start AND :end
        AND (:uris IS NULL OR h.uri IN :uris)
        GROUP BY h.app, h.uri
    """)
    List<StatsViewDto> findUniqueStats(
            LocalDateTime start,
            LocalDateTime end,
            List<String> uris
    );

    @Query("""
        SELECT new stats.dto.StatsViewDto(
            h.app,
            h.uri,
            COUNT(h.ip)
        )
        FROM Hit h
        WHERE h.timestamp BETWEEN :start AND :end
        AND (:uris IS NULL OR h.uri IN :uris)
        GROUP BY h.app, h.uri
    """)
    List<StatsViewDto> findAllStats(
            LocalDateTime start,
            LocalDateTime end,
            List<String> uris
    );
}
