package com.thebadtouch.searchengine.entities;

import lombok.*;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Word implements Serializable, Comparable<Word> {
    @Id
    @Column(name = "word_id", nullable = false)
    private Long wordId;
    @Basic
    @Column(name = "value", nullable = true, length = -1)
    private String value;
    @Basic
    @Column(name = "word_frequency", nullable = true, precision = 0)
    private Long wordFrequency;
    @Basic
    @Column(name = "max_term_frequency", nullable = true)
    private Long maxTermFrequency;

    public void addToFrequency() {
        wordFrequency++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Word that = (Word) o;
        return Objects.equals(wordId, that.wordId) &&
                Objects.equals(value, that.value) &&
                Objects.equals(wordFrequency, that.wordFrequency) &&
                Objects.equals(maxTermFrequency, that.maxTermFrequency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(wordId, value, wordFrequency, maxTermFrequency);
    }

    @Override
    public int compareTo(Word o) {
        return this.wordFrequency.compareTo(o.getWordFrequency());
    }
}
