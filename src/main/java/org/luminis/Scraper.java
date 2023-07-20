package org.luminis;
import org.json.*;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Scraper {
    URL url;
    String artistName;
    public Scraper() throws Exception {
        String urlString = "https://api.qmusic.nl/2.0/tracks/plays?limit=1";
        url = new URI(urlString).toURL();

        if (!test_connection()) {
            throw new Exception("Test message failed. Something is wrong with the connection.");
        } else
        {
            System.out.println("Connection with QMusic has been made!");
        }

        try {
            System.out.println("Please enter the name of the band that you wish to search for.");
            Scanner scanner = new Scanner(System.in);
            artistName = scanner.nextLine();
            System.out.println("Band name selected!");
        } catch (Exception e) {
            throw new Exception("Something went wrong with loading in the name");
        }
    }

    private boolean test_connection() throws IOException {
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36");
        int responseCode = con.getResponseCode();

        return responseCode == 200;
    }

    public boolean Scrape(DiscordWebhook discordWebhook) throws IOException, JSONException {
        System.out.println("\nChecked Q-MUSIC at " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36");

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        String html = response.toString();

        JSONObject obj = new JSONObject(html);
        JSONArray arr = obj.getJSONArray("played_tracks");

        for (int i = 0; i < arr.length(); i++){
            JSONObject current_obj = arr.getJSONObject(i);
            JSONObject artist_obj = current_obj.getJSONObject("artist");

            String title = current_obj.getString("title");
            String bandName = artist_obj.getString("name");
            String fullName = artist_obj.getString("original_name");

            System.out.println(title);
            System.out.println(fullName);

            if (bandName.toLowerCase().contains(artistName) || fullName.toLowerCase().contains(artistName))
            {
                discordWebhook.setContent("(ALERT) " + artistName + " is now playing on QMusic!");
                discordWebhook.execute();
                return true;
            }
        }
        return false;
    }
}
