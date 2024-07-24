package com.example.forumproject.repositories;

import com.example.forumproject.exceptions.EntityDuplicateException;
import com.example.forumproject.exceptions.EntityNotFoundException;
import com.example.forumproject.models.Like;
import com.example.forumproject.models.Post;
import com.example.forumproject.models.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PostRepositoryImpl implements PostRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public PostRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Post> getPosts() {
        return List.of();
    }

    @Override
    public Post getById(int id) {
        try (Session session = sessionFactory.openSession()) {
            Post post = session.get(Post.class, id);
            if (post == null) {
                throw new EntityNotFoundException("Post", id);
            }

            return post;
        }
    }

    @Override
    public Post getByTitle(String title) {
        try (Session session = sessionFactory.openSession()) {
            Query<Post> query = session.createQuery("from Post where title = :title", Post.class);
            query.setParameter("title", title);
            List<Post> posts = query.list();
            if (posts.isEmpty()) {
                throw new EntityNotFoundException("Post", "title", title);
            }

            return posts.get(0);
        }
    }

    @Override
    public void create(Post post) {
        try (Session session = sessionFactory.openSession()) {
            session.persist(post);
        }
    }

    @Override
    public void update(Post post) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.merge(post);
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(int id) {
        Post postToDelete = getById(id);
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.remove(postToDelete);
            session.getTransaction().commit();
        }
    }

    @Override
    public Post addLike(Post post, User user) {
        try (Session session = sessionFactory.openSession()) {
            Query<Like> query = session.createQuery("from Like where post =: postId and user = :userId", Like.class);
            query.setParameter("postId", post.getId());
            query.setParameter("userId", user.getId());

            List<Like> result = query.list();
            if (!result.isEmpty()) {
                throw new EntityDuplicateException("Like", "ID", " ");
            }
            Like like = new Like();
            like.setPost(post);
            like.setUser(user);
            session.beginTransaction();
            session.persist(like);
            session.getTransaction().commit();
            return post;
        }
    }

    @Override
    public List<Post> getTopCommentedPosts() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                            "select p from Post p left join p.comments c group by p.id order by count(c) desc", Post.class)
                    .setMaxResults(10)
                    .list();
        }
    }

    @Override
    public List<Post> getRecentPosts() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("from Post p order by p.creationDate desc", Post.class)
                    .setMaxResults(10)
                    .list();
        }
    }
}
