package com.example.ec.web;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.util.Objects;

import com.example.ec.domain.TourRating;

/*
  DTO - Data transfer object
  Basically we don't want the customer to be overwhelmed with unnecessary details in the response body.
  So instead of returning a response of TourRating or TourRatingPk entity type(which has entire Tour object) or
  making a create/update call complex for the customer(as he will have to pass these many params); we introduce an DTO object.
  Here, RatingDto which makes API interactions easier for the customer.
  Just that we need to internally handle the transition from DTO object to actual entity in our app code.
 */
public class RatingDto {
    @Min(0)
    @Max(5)
    private Integer score;

    @Size(max = 255)
    private String comment;

    @NotNull
    private Integer customerId;

    /**
     * Construct a RatingDto from a fully instantiated TourRating.
     * @param tourRating TourRating object.
     */
    public RatingDto(TourRating tourRating) {
        this(tourRating.getScore(), tourRating.getComment(), tourRating.getTourRatingPk().getCustomerId());
    }

    public RatingDto(Integer score, String comment, Integer customerId) {
        this.score = score;
        this.comment = comment;
        this.customerId = customerId;
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

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RatingDto)) return false;
        RatingDto ratingDto = (RatingDto) o;
        return Objects.equals(getScore(), ratingDto.getScore()) && Objects.equals(getComment(), ratingDto.getComment()) && Objects.equals(getCustomerId(), ratingDto.getCustomerId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getScore(), getComment(), getCustomerId());
    }

    @Override
    public String toString() {
        return "RatingDto{" +
                "score=" + score +
                ", comment='" + comment + '\'' +
                ", customerId=" + customerId +
                '}';
    }
}
