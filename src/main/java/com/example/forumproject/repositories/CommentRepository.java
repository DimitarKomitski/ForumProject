package com.example.forumproject.repositories;

import com.example.forumproject.models.Comment;
import com.example.forumproject.models.CommentDto;
import com.example.forumproject.models.User;

import java.util.List;

public interface CommentRepository {

    List<Comment> getById(int id);

    Comment update(Comment comment);

    Comment delete(Comment comment);

    Comment getCommentById(int id);

    Comment createComment(Comment comment);
}
