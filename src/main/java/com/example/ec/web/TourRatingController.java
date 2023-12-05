package com.example.ec.web;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.ec.domain.Tour;
import com.example.ec.domain.TourRating;
import com.example.ec.domain.TourRatingPk;
import com.example.ec.repo.TourRatingRepository;
import com.example.ec.repo.TourRepository;

@RestController
@RequestMapping(path = "/tours/{tourId}/ratings")
public class TourRatingController {
    TourRatingRepository tourRatingRepository;
    TourRepository tourRepository;

    /*
    The @Autowired annotation is a Spring annotation that is used for automatic dependency injection.
    It allows the Spring container to provide an instance of a required dependency when a bean is created.
    This annotation can be used on fields, constructors, and methods to have Spring provide the dependencies automatically.
     */
    //Better to inject a service but for simplicity we have injected the repositories.
    @Autowired
    public TourRatingController(TourRatingRepository tourRatingRepository, TourRepository tourRepository) {
        this.tourRatingRepository = tourRatingRepository;
        this.tourRepository = tourRepository;
    }

    protected TourRatingController() {
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createTourRating(@PathVariable(value = "tourId") int tourId,
                                 @RequestBody @Validated RatingDto ratingDto) {
        Tour tour = verifyTour(tourId);
        tourRatingRepository.save(new TourRating(new TourRatingPk(tour, ratingDto.getCustomerId()),
                ratingDto.getScore(),
                ratingDto.getComment())
        );
    }

    //Return RatingDto list as response to customer.
    //not paginated
    /*@GetMapping
    public List<RatingDto> getAllRatingsForTour(@PathVariable(value = "tourId") int tourId) {
        verifyTour(tourId);
        return tourRatingRepository.findByTourRatingPkTourId(tourId)
                .stream()
                .map(RatingDto::new).collect(Collectors.toList());
    }*/

    //Return RatingDto list as response to customer.
    //Paginated
    //here paginated response is collected from main repo and then further mapped to
    //paged DTO(here, RatingDto) before returning to the customer.
    @GetMapping
    public Page<RatingDto> getAllRatingsForTour(@PathVariable(value = "tourId") int tourId, Pageable pageable) {
        verifyTour(tourId);
        Page<TourRating> tourRating = tourRatingRepository.findByTourRatingPkTourId(tourId, pageable);
        return new PageImpl<>(
                tourRating.stream().map(RatingDto::new).collect(Collectors.toList()),
                pageable,
                tourRating.getTotalElements()); //total elements in the system, not just the page.
    }

    //Return average rating for a tour
    @GetMapping(path = "/average")
    public Map<String, Double> getAverage(@PathVariable(value = "tourId") int tourId) {
        verifyTour(tourId);
        return Map.of("average", tourRatingRepository.findByTourRatingPkTourId(tourId)
                .stream()
                .mapToInt(TourRating::getScore).average()
                .orElseThrow(() -> new NoSuchElementException("Tour has no ratings.")));
    }

    /* The task of put and patch are almost same.
    Respecting the semantics of the Rest API, put expects the entire request body and hence
    updates the whole record with incoming request body.
    Whereas for patch, we check what specific update attributes the customer has sent and
    just update those.
     */

    @PutMapping
    public RatingDto updateWithPut(@PathVariable(value = "tourId") Integer tourId,
                                   @RequestBody @Validated RatingDto ratingDto) {
        TourRating tourRating = verifyTourRating(tourId, ratingDto.getCustomerId());
        tourRating.setScore(ratingDto.getScore());
        tourRating.setComment(ratingDto.getComment());
        return new RatingDto(tourRatingRepository.save(tourRating));
    }

    @PatchMapping
    public RatingDto updateWithPatch(@PathVariable(value = "tourId") Integer tourId,
                                     @RequestBody @Validated RatingDto ratingDto) {
        TourRating tourRating = verifyTourRating(tourId, ratingDto.getCustomerId());
        if(ratingDto.getScore() != null)
            tourRating.setScore(ratingDto.getScore());

        if(ratingDto.getComment() != null)
            tourRating.setComment(ratingDto.getComment());

        return new RatingDto(tourRatingRepository.save(tourRating));
    }

    @DeleteMapping(path = "/{customerId}")
    public void deleteTourRating(@PathVariable(value = "tourId") Integer tourId,
                                 @PathVariable(value = "customerId") Integer customerId) {
        TourRating tourRating = verifyTourRating(tourId, customerId);
        tourRatingRepository.delete(tourRating);
    }



    /**
     * Verify and return the Tour given a tourId.
     *
     * @param tourId tour identifier
     * @return the found Tour
     * @throws NoSuchElementException if no Tour found.
     */
    private Tour verifyTour(int tourId) throws NoSuchElementException {
        return tourRepository.findById(tourId).orElseThrow(() ->
            new NoSuchElementException("Tour does not exist " + tourId));
    }

    //Verify tour rating for a given tourId and customerId
    private TourRating verifyTourRating(int tourId, int customerId) throws NoSuchElementException {
        return tourRatingRepository.findByTourRatingPkTourIdAndTourRatingPkCustomerId(tourId, customerId)
                .orElseThrow(() ->
                        new NoSuchElementException("No such tour rating found with tour-id " +tourId
                        + " customer-id " +customerId));
    }

    /**
     * Exception handler if NoSuchElementException is thrown in this Controller
     *
     * @param ex exception
     * @return Error message String.
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoSuchElementException.class)
    public String return400(NoSuchElementException ex) {
        return ex.getMessage();
    }
}
