alter table ratings modify column rating float;
alter table ratings modify column numVotes int(11);

delete from stars_in_movies where movieId > "tt0499470";
delete from genres_in_movies where genreId > 23;
delete from ratings where movieId > "tt0499470";
delete from movies where id > "tt0499470";
delete from stars where id > "nm9423080";
delete from genres where id > 23;


drop procedure if exists add_movie;

delimiter $$

create procedure add_movie(

in title varchar(100),
in year int(11),
in director varchar(100),
in starName varchar(100),
in genreName varchar(32),

out movieMessage varchar(100),
out starMessage varchar(100),
out genreMessage varchar(100)

)

begin

declare movieId varchar(10);
declare starId varchar(10);
declare genreId int(11);

declare maxMovieId varchar(10);
declare maxStarId varchar(10);
declare maxGenreId int(11);

select id into movieId from movies 
where movies.title = title and movies.year = year and movies.director = director 
limit 1;

select id into starId from stars  
where stars.name = starName
limit 1;

select id into genreId from genres 
where genres.name = genreName
limit 1;

-- movie exists

if movieId is not null then

	set movieMessage = "Movie already exists, ";
	set starMessage = "no changes to the ";
	set genreMessage = "database are made.";

else

	-- get or set movieId, starId, genreId

	set maxMovieId = (select max(id) from movies);
	set movieId = (select left(maxMovieId, 2));
	set maxMovieId = (select substring(maxMovieId, 3));
	set maxMovieId = maxMovieId + 1;
	set maxMovieId = (select lpad(maxMovieId, 7, 0));
	set movieId = concat(movieId, maxMovieId);

	if starId is null then

		set maxStarId = (select max(id) from stars);
		set starId = (select left(maxStarId, 2));
		set maxStarId = (select substring(maxStarId, 3));
		set maxStarId = maxStarId + 0 + 1;
		set starId = concat(starId, maxStarId);

		insert into stars(id, name, birthYear) 
		values(starId, starName, null);

		set starMessage = concat("new star ", starName, " added, star ID = ", starId);

	else

		set starMessage = concat("link to star ", starName, " successful, star ID = ", starId);

	end if;

	if genreId is null then

		set maxGenreId = (select max(id) from genres);
		set genreId = maxGenreId + 1;

		insert into genres(id, name)
		values(genreId, genreName);

		set genreMessage = concat("new genre ", genreName, " added, genre ID = ", genreId);

	else

		set genreMessage = concat("link to genre ", genreName, " successful, genre ID = ", genreId);

	end if;

	-- insert data into moviedb

	insert into movies(id, title, year, director) 
	values(movieId, title, year, director);

	insert into stars_in_movies(starId, movieId) 
	values(starId, movieId);

	insert into genres_in_movies(genreId, movieId)
	values(genreId, movieId);

	insert into ratings(movieId, rating, numVotes)
	values(movieId, null, null);

	-- done

	set movieMessage = concat("new movie ", title, " added, movie ID = ", movieId);

end if;


end$$
delimiter ;

-- insert into stars(id, name, birthYear) values("nm9423081", "Star1", null);

-- call add_movie("Movie1", 2020, "Director1", "Star2", "Genre1", @movieMessage, @starMessage, @genreMessage);
-- select @movieMessage, @starMessage, @genreMessage;
--
-- call add_movie("Movie2", 2020, "Director1", "Star1", "Genre1", @movieMessage, @starMessage, @genreMessage);
-- select @movieMessage, @starMessage, @genreMessage;
--
-- call add_movie("Movie1", 2020, "Director1", "Star2", "Genre2", @movieMessage, @starMessage, @genreMessage);
-- select @movieMessage, @starMessage, @genreMessage;
