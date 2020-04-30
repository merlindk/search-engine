package com.thebadtouch.searchengine.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post implements Serializable {
    private Integer postId;
    private Vocabulary vocabularyByVocabId;
    private Document documentByDocId;
    private Integer termFrequency;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id", nullable = false)
    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    @Column(name = "term_frequency", nullable = false)
    public Integer getTermFrequency() {
        return termFrequency;
    }

    public void setTermFrequency(Integer termFrequency) {
        this.termFrequency = termFrequency;
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

    public void incrementTermFrequency(){
        termFrequency++;
    }
}
