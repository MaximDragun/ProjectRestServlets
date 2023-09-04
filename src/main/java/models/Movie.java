package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;
import java.util.Objects;

public class Movie {
    @JsonProperty("id")
    private Long movieId;
    private Long directorId;
    private String name;
    @JsonProperty("year")
    private int yearOfProduction;
    private List<Actor> actorList;

    public Movie() {
    }

    public Movie(Long directorId, String name, int yearOfProduction) {
        this.directorId = directorId;
        this.name = name;
        this.yearOfProduction = yearOfProduction;
    }

    public Movie(Long movieId, Long directorId, String name, int yearOfProduction) {
        this.movieId = movieId;
        this.directorId = directorId;
        this.name = name;
        this.yearOfProduction = yearOfProduction;
    }

    public Long getMovieId() {
        return movieId;
    }

    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }

    public Long getDirectorId() {
        return directorId;
    }

    public void setDirectorId(Long directorId) {
        this.directorId = directorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getYearOfProduction() {
        return yearOfProduction;
    }

    public void setYearOfProduction(int yearOfProduction) {
        this.yearOfProduction = yearOfProduction;
    }

    public List<Actor> getActorList() {
        return actorList;
    }

    public void setActorList(List<Actor> actorList) {
        this.actorList = actorList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return yearOfProduction == movie.yearOfProduction && Objects.equals(movieId, movie.movieId) && Objects.equals(directorId, movie.directorId) && Objects.equals(name, movie.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(movieId, directorId, name, yearOfProduction);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
