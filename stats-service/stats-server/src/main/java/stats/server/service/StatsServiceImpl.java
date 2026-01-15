package stats.server.service;

import stats.dto.StatsHitDto;
import stats.dto.StatsViewDto;
import lombok.RequiredArgsConstructor;
import stats.server.mapper.HitMapper;
import org.springframework.stereotype.Service;
import stats.server.repository.HitRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final HitRepository repository;
    private final HitMapper mapper;

    @Override
    public void saveHit(StatsHitDto dto) {
        repository.save(mapper.toEntity(dto));
    }

    @Override
    public List<StatsViewDto> getStats(
            LocalDateTime start,
            LocalDateTime end,
            List<String> uris,
            boolean unique) {

        if (unique) {
            return repository.findUniqueStats(start, end, uris);
        }
        return repository.findAllStats(start, end, uris);
    }
}
