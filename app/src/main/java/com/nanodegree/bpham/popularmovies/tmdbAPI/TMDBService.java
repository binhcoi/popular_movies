package com.nanodegree.bpham.popularmovies.tmdbAPI;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by binh on 8/17/15.
 *
 */
public interface TMDBService {
    @GET("/discover/movie")
    void discoverMovies(@Query("api_key") String api_key, @Query("sort_by") String sortBy, Callback<Discovery> callback);

    @GET("/movie/{id}/videos")
    void getTrailers(@Path("id") int id, @Query("api_key") String api_key, Callback<Trailers> callback);

    @GET("/movie/{id}/reviews")
    void getReviews(@Path("id") int id, @Query("api_key") String api_key, Callback<Reviews> callback);
}
