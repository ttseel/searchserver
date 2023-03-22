package go.kb.searchserver.repository;


import go.kb.searchserver.domain.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    Optional<Keyword> findByKeyword(String keyword);

    List<Keyword> findTop10ByOrderByReadCountDesc();

    @Modifying(clearAutomatically = true)
    @Query(value = "update Keyword k set k.readCount = k.readCount + :increment where k.keyword in (:keywords)")
    void bulkIncrementCount(@Param("increment") int increment, @Param("keywords") List<String> keywords);
}
