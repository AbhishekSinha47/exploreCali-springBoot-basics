package com.example.ec.domain;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import java.util.Objects;

@Entity
public class TourRating {
    @EmbeddedId
    private TourRatingPk tourRatingPk;

    @Column(nullable = false)
    private Integer score;

    @Column
    private String comment;

    public TourRating() {
    }

    public TourRating(TourRatingPk tourRatingPk, Integer score, String comment) {
        this.tourRatingPk = tourRatingPk;
        this.score = score;
        this.comment = comment;
    }

    public TourRatingPk getTourRatingPk() {
        return tourRatingPk;
    }

    public void setTourRatingPk(TourRatingPk tourRatingPk) {
        this.tourRatingPk = tourRatingPk;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TourRating)) return false;
        TourRating that = (TourRating) o;
        return Objects.equals(getTourRatingPk(), that.getTourRatingPk()) && Objects.equals(getScore(), that.getScore()) && Objects.equals(getComment(), that.getComment());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTourRatingPk(), getScore(), getComment());
    }

    @Override
    public String toString() {
        return "TourRating{" +
                "tourRatingPk=" + tourRatingPk +
                ", score=" + score +
                ", comment='" + comment + '\'' +
                '}';
    }
}
