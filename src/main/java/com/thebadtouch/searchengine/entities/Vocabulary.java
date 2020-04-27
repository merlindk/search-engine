package com.thebadtouch.searchengine.entities;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Vocabulary {
    private Integer vocabId;
    private String word;
    private Double reverseFrequency;
    private Long maxTermFrequency;

    @Id
    @Column(name = "vocab_id", nullable = false)
    public Integer getVocabId() {
        return vocabId;
    }

    public void setVocabId(Integer vocabId) {
        this.vocabId = vocabId;
    }

    @Basic
    @Column(name = "word", nullable = true, length = -1)
    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    @Basic
    @Column(name = "reverse_frequency", nullable = true, precision = 0)
    public Double getReverseFrequency() {
        return reverseFrequency;
    }

    public void setReverseFrequency(Double reverseFrequency) {
        this.reverseFrequency = reverseFrequency;
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
        Vocabulary that = (Vocabulary) o;
        return Objects.equals(vocabId, that.vocabId) &&
                Objects.equals(word, that.word) &&
                Objects.equals(reverseFrequency, that.reverseFrequency) &&
                Objects.equals(maxTermFrequency, that.maxTermFrequency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vocabId, word, reverseFrequency, maxTermFrequency);
    }
}
