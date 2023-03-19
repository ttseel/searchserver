package go.kb.searchserver.repository;


import go.kb.searchserver.domain.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    Optional<Keyword> findByKeyword(String keyword);

    List<Keyword> findTop10ByOrderByReadCountDesc();
}
