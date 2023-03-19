package go.kb.searchserver.controller;

import go.kb.searchserver.domain.Keyword;
import go.kb.searchserver.dto.SearchResponse;
import go.kb.searchserver.dto.Top10Response;
import go.kb.searchserver.repository.KeywordRepository;
import go.kb.searchserver.service.SearchService;
import go.kb.searchserver.service.TempService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.Random;

@Slf4j
@RestController
@RequestMapping("/search")
public class SearchController {

    @Qualifier("SearchServiceV1")
    @Autowired
    private SearchService searchService;
    @Autowired
    private TempService tempService;

    @GetMapping("/blog-posting")
    private SearchResponse searchBlogPosting(@RequestParam("keyword") String keyword,
                                             @RequestParam(required = false, defaultValue = "accuracy") String sort,
                                             @RequestParam(required = false, defaultValue = "1") int page,
                                             @RequestParam(required = false, defaultValue = "10") int size) {

        SearchResponse searchResponse = searchService.searchBlog(keyword, sort, page, size);
        tempService.saveAndIncrement(keyword);
        return searchResponse;
    }


    @GetMapping("/top10")
    private Top10Response searchTop10() {
        Top10Response top10Response = searchService.searchTop10();
        return top10Response;
    }

    @Autowired
    KeywordRepository keywordRepository; // TODO 테스트. 삭제해야함

    // TODO 테스트. 삭제해야함
    @PostConstruct
    private void createSampleData() {
        Random random = new Random();
        for (int i = 0; i < 10000; i++) {
            Keyword keyword = new Keyword("keyword" + i, random.nextInt(Integer.MAX_VALUE));
            keywordRepository.save(keyword);
        }
    }
}
