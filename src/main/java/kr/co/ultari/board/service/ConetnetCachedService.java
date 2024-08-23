package kr.co.ultari.board.service;

import kr.co.ultari.board.entities.Content;
import kr.co.ultari.board.repository.ContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

@Service
public class ConetnetCachedService {
    @Autowired
    ContentRepository contentRepository;

    @Cacheable(value="CONTENT", key="#contentId", unless="#result=null or #result.isEmpty()")
    public List<Content> findByBoardIdOrderByRegistDateDesc(String contentId, Pageable pageable){
        return contentRepository.findByBoardIdOrderByRegistDateDesc(contentId, pageable);
    }
}
