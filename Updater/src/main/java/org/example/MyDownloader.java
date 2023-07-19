package org.example;

import netscape.javascript.JSObject;
import org.apache.commons.io.FilenameUtils;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class MyDownloader {
    public static void downloadToFile(String downloadURL, String Path) throws IOException
    {
        URL website = new URL(downloadURL);
        String fileName = getFileName(downloadURL);

        try (InputStream inputStream = website.openStream())
        {
           // Files.copy(inputStream, Paths.get(Path+fileName), StandardCopyOption.REPLACE_EXISTING);
        }
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
