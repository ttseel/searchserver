package go.kb.searchserver.controller;

import go.kb.searchserver.dto.SearchResponse;
import go.kb.searchserver.dto.Top10Response;
import go.kb.searchserver.repository.KeywordRepository;
import go.kb.searchserver.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/search")
public class SearchController {

    @Qualifier("SearchServiceImpl")
    @Autowired
    private SearchService searchService;
    @Autowired
    KeywordRepository keywordRepository;

    @GetMapping("/blog-posting")
    private SearchResponse searchBlogPosting(@RequestParam("query") String query,
                                             @RequestParam(required = false, defaultValue = "accuracy") String sort,
                                             @RequestParam(required = false, defaultValue = "1") String page,
                                             @RequestParam(required = false, defaultValue = "10") String size) {
        SearchResponse searchResponse = searchService.searchBlog(query, sort, page, size);
        return searchResponse;
    }


    @GetMapping("/top10")
    private Top10Response searchTop10() {
        Top10Response top10Response = searchService.searchTop10();
        return top10Response;
    }
}
