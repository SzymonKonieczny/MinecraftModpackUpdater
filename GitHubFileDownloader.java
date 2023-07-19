
import org.apache.commons.io.FilenameUtils;

import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class GitHubFileDownloader {
    public static void download(String downloadURL) throws IOException
    {
        URL website = new URL(downloadURL);
        String fileName = getFileName(downloadURL);

        try (InputStream inputStream = website.openStream())
        {
            Files.copy(inputStream, Paths.get(fileName), StandardCopyOption.REPLACE_EXISTING);
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
