package org.luminis;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        Scraper scraper = startScraper();
        DiscordWebhook discordWebhook = startWebhook();

        if (scraper != null && discordWebhook != null) {
            startServiceTimer(scraper, discordWebhook);
        }
    }

    public static Scraper startScraper() {
        Scraper scraper;

        try {
            scraper = new Scraper();
        } catch (Exception e) {
            System.out.println("Scraper crashed on initialization phase.");
            return null;
        }

        return scraper;
    }

    public static DiscordWebhook startWebhook() {
        String url = "";

        try {
            System.out.println("Please enter the discord webhook url.");
            Scanner scanner = new Scanner(System.in);
            url = scanner.nextLine();
            System.out.println("Discord webhook connection successful!");
        } catch (Exception e) {
            System.out.println("Something went wrong while reading in the discord URL.");
            return null;
        }

        try {
            DiscordWebhook discordWebhook = new DiscordWebhook(url);
            discordWebhook.setContent("De Q-Scraper is nu online!");
            discordWebhook.execute();
            return discordWebhook;
        } catch (IOException e)
        {
            System.out.println("Faulty discord URL.");
            return null;
        }
    }

    public static void startServiceTimer(Scraper scraper, DiscordWebhook discordWebhook) {
        new Timer().scheduleAtFixedRate(
                new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            if (scraper.Scrape(discordWebhook)) {
                                TimeUnit.MINUTES.sleep(10);
                            }
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                0, 15000
        );
    }

}