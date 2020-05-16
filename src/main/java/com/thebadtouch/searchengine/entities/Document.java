package com.thebadtouch.searchengine.entities;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Document implements Serializable, Comparable<Document> {
    @Id
    @Column(name = "doc_id", nullable = false)
    private Long docId;
    @Basic
    @Column(name = "name", nullable = true, length = -1)
    private String name;
    @Transient
    private Double weight = 0D;

    public void addWeight(Double newWeight) {
        weight += newWeight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Document document = (Document) o;
        return Objects.equals(docId, document.docId) &&
                Objects.equals(name, document.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(docId, name);
    }

    @Override
    public int compareTo(Document o) {
        return o.getWeight().compareTo(this.weight);
    }
}
