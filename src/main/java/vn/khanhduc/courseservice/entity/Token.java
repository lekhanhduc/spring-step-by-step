package vn.khanhduc.courseservice.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RedisHash("RedisToken")
public class Token {

    @Id
    private String tokenId;

    @TimeToLive(unit = TimeUnit.MINUTES)
    private Long expiredTime;
}
