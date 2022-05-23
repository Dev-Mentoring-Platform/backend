package com.project.mentoridge.modules.board.repository;

import com.project.mentoridge.modules.board.controller.response.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Repository
public class ContentSearchRepository {

    public Page<PostResponse> findPostsSearchedByContent() {
        return null;
    }
}
