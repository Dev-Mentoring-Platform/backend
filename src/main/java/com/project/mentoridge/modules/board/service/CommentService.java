package com.project.mentoridge.modules.board.service;

import com.project.mentoridge.modules.base.AbstractService;
import com.project.mentoridge.modules.board.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class CommentService extends AbstractService {

    private final CommentRepository commentRepository;
}
