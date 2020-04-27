package com.thebadtouch.searchengine.entities;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Post {
    private Integer postId;
    private Vocabulary vocabularyByVocabId;
    private Document documentByDocId;

    @Id
    @Column(name = "post_id", nullable = false)
    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return Objects.equals(postId, post.postId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(postId);
    }

    @ManyToOne
    @JoinColumn(name = "vocab_id", referencedColumnName = "vocab_id")
    public Vocabulary getVocabularyByVocabId() {
        return vocabularyByVocabId;
    }

    public void setVocabularyByVocabId(Vocabulary vocabularyByVocabId) {
        this.vocabularyByVocabId = vocabularyByVocabId;
    }

    @ManyToOne
    @JoinColumn(name = "doc_id", referencedColumnName = "doc_id")
    public Document getDocumentByDocId() {
        return documentByDocId;
    }

    public void setDocumentByDocId(Document documentByDocId) {
        this.documentByDocId = documentByDocId;
    }
}
