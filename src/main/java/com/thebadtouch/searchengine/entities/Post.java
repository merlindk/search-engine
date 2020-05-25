package com.thebadtouch.searchengine.entities;

import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Post implements Serializable, Comparable<Post> {
    @Id
    @Column(name = "post_id", nullable = false)
    private Long postId;
    @Column(name = "term_frequency", nullable = false)
    private Long termFrequency;
    @ManyToOne
    @JoinColumn(name = "word_id", referencedColumnName = "word_id")
    private Word wordByWordId;
    @ManyToOne
    @JoinColumn(name = "doc_id", referencedColumnName = "doc_id")
    private Document documentByDocId;

    public void incrementTermFrequency() {
        termFrequency++;
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

    @Override
    public int compareTo(Post o) {
        return o.getTermFrequency().compareTo(this.termFrequency);
    }
}
