package org.example;

import netscape.javascript.JSObject;
import org.apache.commons.io.FilenameUtils;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Random;

public class MyDownloader {
    public static void downloadToFile(String downloadURL, Path path, String filename) throws IOException
    {
        int i = 0;
        int maximum_backoff = 32000;
        do{
            int random_number_milliseconds = (int)(Math.random()*1000);
            URL website = new URL(downloadURL);
            int waitTime = (int)Math.min(((Math.pow(2,i))+random_number_milliseconds), maximum_backoff);
            System.out.println("Downloading : " + filename + " with backoff of " +waitTime + " via " + downloadURL);
            try (InputStream inputStream = website.openStream())
            {
                Thread.sleep(waitTime);
                Files.copy(inputStream, Path.of( path.toString()+"/"+filename), StandardCopyOption.REPLACE_EXISTING);

                return;
            }
            catch (Exception ex)
            {
                i++;
                System.out.println(i);


            }



        }while ((Math.pow(2,i)) < maximum_backoff);

        //If we get here, download failed
        return;

    }
    public static JSONObject downloadToJson(String downloadURL) throws IOException
    {
        URL website = new URL(downloadURL);
        String fileName = getFileName(downloadURL);

        try (InputStream inputStream = website.openStream())
        {
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder str = new StringBuilder();
            int c;
            while ((c = reader.read()) != -1) {
                str.append((char) c);
            }
            //System.out.println("ResponseJson : "+str);
            return new JSONObject(str.toString());

        }
    }
    public static String getFileName(String downloadURL) throws UnsupportedEncodingException
    {
        String baseName = FilenameUtils.getBaseName(downloadURL);
        String extension = FilenameUtils.getExtension(downloadURL);
        String fileName = baseName + "." + extension;

        int questionMarkIndex = fileName.indexOf("?");
        if (questionMarkIndex != -1)
        {
            fileName = fileName.substring(0, questionMarkIndex);
        }

        fileName = fileName.replaceAll("-", "");
        return URLDecoder.decode(fileName, "UTF-8");
    }

}
