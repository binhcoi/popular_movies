package com.nanodegree.bpham.popularmovies.tmdbAPI;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by binh on 8/17/15.
 *
 */
public interface TMDBService {
    @GET("/discover/movie")
    Discovery discoverMovies(@Query("api_key") String api_key, @Query("sort_by") String sortBy);

    @GET("/movie/{id}/videos")
    Trailers getTrailers(@Path("id") int id, @Query("api_key") String api_key);

    @GET("/movie/{id}/reviews")
    Reviews getReviews(@Path("id") int id, @Query("api_key") String api_key);

}
