package vn.khanhduc.courseservice.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import vn.khanhduc.courseservice.entity.Token;

@Repository
public interface RedisTokenRepository extends CrudRepository<Token,String> {
}
