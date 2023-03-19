package go.kb.searchserver.service;

import go.kb.searchserver.domain.Keyword;
import go.kb.searchserver.repository.KeywordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TempService {
    @Autowired
    private KeywordRepository keywordRepository;

    public void saveAndIncrement(String keyword) {
        Keyword keywordEntity = keywordRepository.findByKeyword(keyword).orElse(new Keyword(keyword, 0));
        keywordEntity.increment();
        keywordRepository.save(keywordEntity);
    }
}
