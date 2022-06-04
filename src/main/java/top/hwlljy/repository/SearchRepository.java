package top.hwlljy.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import top.hwlljy.model.pojo.UserSearch;

import java.util.List;

public interface SearchRepository extends JpaRepository<UserSearch, String> {

    @Query(value = "select t1.word from (" +
            "select word,count(*) cnt from weibo_user_search where create_time > :minTime) t1 order by t1.cnt desc ", nativeQuery = true)
    List<UserSearch> getSearchList(Pageable pageable);

}
