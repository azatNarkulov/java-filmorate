package ru.yandex.practicum.filmorate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@SpringBootApplication
public class FilmorateApplication {
	public static void main(String[] args) throws IOException, InterruptedException {
		SpringApplication.run(FilmorateApplication.class, args);

		/*HttpClient client = HttpClient.newHttpClient();

		*//*HttpRequest requestFriend = HttpRequest.newBuilder()
				.uri(URI.create("http://localhost:8080/users/4/friends/5"))
				.header("Content-Type", "application/json")
				.PUT(HttpRequest.BodyPublishers.noBody())
				.build();

		HttpResponse<String> responseFriend = client.send(requestFriend, HttpResponse.BodyHandlers.ofString());

		System.out.println("Status: " + responseFriend.statusCode());
		System.out.println("Body: " + responseFriend.body());*//*

		String json = "{"
				+ "\"name\": \"Name\","
				+ "\"description\": \"Description\","
				+ "\"releaseDate\": \"1980-03-25\","
				+ "\"duration\": 200"
				+ "\"mpa\": { \"id\": 10}"
				+ "}";

		HttpRequest requestFilm = HttpRequest.newBuilder()
				.uri(URI.create("http://localhost:8080/films"))
				.header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(json))
				.build();

		HttpResponse<String> responseFilm = client.send(requestFilm, HttpResponse.BodyHandlers.ofString());

		System.out.println("Status: " + responseFilm.statusCode());
		System.out.println("Body: " + responseFilm.body());*/
	}
}
