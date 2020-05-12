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
public class Post implements Serializable, Comparable<Post> {
    private Long postId;
    private Word wordByWordId;
    private Document documentByDocId;
    private Long termFrequency;

    @Id
    @Column(name = "post_id", nullable = false)
    public Long getPostId() {
        return postId;
    }

    public void setPostId(Long postId) {
        this.postId = postId;
    }

    @Column(name = "term_frequency", nullable = false)
    public Long getTermFrequency() {
        return termFrequency;
    }

    public void setTermFrequency(Long termFrequency) {
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
    @JoinColumn(name = "word_id", referencedColumnName = "word_id")
    public Word getWordByWordId() {
        return wordByWordId;
    }

    public void setWordByWordId(Word wordByVocabId) {
        this.wordByWordId = wordByVocabId;
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


    @Override
    public int compareTo(Post o) {
        return o.getTermFrequency().compareTo(this.termFrequency);
    }
}
