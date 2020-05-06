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
public class Word implements Serializable, Comparable<Word>  {
    private Long wordId;
    private String value;
    private Long wordFrequency;
    private Long maxTermFrequency;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "word_id", nullable = false)
    public Long getWordId() {
        return wordId;
    }

    public void setWordId(Long vocabId) {
        this.wordId = vocabId;
    }

    @Basic
    @Column(name = "value", nullable = true, length = -1)
    public String getValue() {
        return value;
    }

    public void setValue(String word) {
        this.value = word;
    }

    @Basic
    @Column(name = "word_frequency", nullable = true, precision = 0)
    public Long getWordFrequency() {
        return wordFrequency;
    }

    public void setWordFrequency(Long reverseFrequency) {
        this.wordFrequency = reverseFrequency;
    }

    @Basic
    @Column(name = "max_term_frequency", nullable = true)
    public Long getMaxTermFrequency() {
        return maxTermFrequency;
    }

    public void setMaxTermFrequency(Long maxTermFrequency) {
        this.maxTermFrequency = maxTermFrequency;
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

    public void addToFrequency() {
        wordFrequency++;
    }

    @Override
    public int compareTo(Word o) {
        return this.wordFrequency.compareTo(o.getWordFrequency());
    }
}
