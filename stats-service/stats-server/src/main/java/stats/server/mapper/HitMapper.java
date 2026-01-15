package stats.server.mapper;

import stats.dto.StatsHitDto;
import stats.server.model.Hit;
import org.springframework.stereotype.Component;

@Component
public class HitMapper {

    public Hit toEntity(StatsHitDto dto) {
        Hit hit = new Hit();
        hit.setApp(dto.getApp());
        hit.setUri(dto.getUri());
        hit.setIp(dto.getIp());
        hit.setTimestamp(dto.getTimestamp());
        return hit;
    }
}