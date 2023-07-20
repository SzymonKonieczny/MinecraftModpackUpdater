package org.example;

import org.json.JSONObject;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class MainGUI {

    class responceObj
    {
        public responceObj()
        {
            modList = new ArrayList<>();
            resourcepackList = new ArrayList<>();
            configJson = new JSONObject();

        }
        List<String> modList;
        List<String> resourcepackList;
        JSONObject configJson;

    }
    class ModsCheckInfo
    {
        public ModsCheckInfo() {
            Redundant = new ArrayList<>();
            Missing = new ArrayList<>();

        }

        public List<String> Redundant;
        public List<String> Missing;
    }
    class ConfigCheckInfo
    {
        public ConfigCheckInfo() {
            Redundant = new ArrayList<>();
            Missing = new ArrayList<>();
            Outdated = new ArrayList<>();

        }

        public List<String> Outdated;
        public List<String> Redundant;
        public List<String> Missing;
    }
    List<String> checkResourcepacks(List<String> list) throws IOException
    {
        List<String> Mismatches = new ArrayList<>();
        Path ResourcepacksPath = Path.of(enteredPath.getText()+"/resourcepacks");
        File resourcepackDir = new File(ResourcepacksPath.toString());
        Set<String> resourcepackSet = new HashSet<>();
        for (File fileEntry : resourcepackDir.listFiles()) {
            if (!fileEntry.isDirectory()) {
              resourcepackSet.add(fileEntry.getName());
            }
        }

        list.forEach(resourcepack -> {
            if(!resourcepackSet.contains(resourcepack))
                Mismatches.add(resourcepack);
        });





        Path testLogs = Path.of(enteredPath.getText()+"/ResourcepackMismatchs.txt");
        if(Files.exists(testLogs)) {
            Files.deleteIfExists(testLogs);
        }
        FileWriter writer = new FileWriter(Files.createFile(testLogs).toString());
        writer.write("Missing: \n");
        Mismatches.forEach(s -> {
            try {
                writer.write(s + "\n");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        writer.close();
        return Mismatches;
    }

    ModsCheckInfo checkMods(List<String> list)  throws IOException
    {
        ModsCheckInfo Mismatches = new ModsCheckInfo();
        Path ModsPath = Path.of(enteredPath.getText()+"/mods");
        File modsDir = new File(ModsPath.toString());
        Set<String> modsSet = new HashSet<>();
        for (File fileEntry : modsDir.listFiles()) {
            if (!fileEntry.isDirectory()) {
                modsSet.add(fileEntry.getName());
            }
        }

        list.forEach(mod -> {
            if(!modsSet.contains(mod))
                Mismatches.Missing.add(mod);
        });

        modsSet.forEach(mod -> {
            if(!list.contains(mod))
                Mismatches.Redundant.add(mod);
        } );




        Path testLogs = Path.of(enteredPath.getText()+"/ModsMismatchs.txt");
        if(Files.exists(testLogs)) {
            Files.deleteIfExists(testLogs);
        }
        FileWriter writer = new FileWriter(Files.createFile(testLogs).toString());
        writer.write("Missing: \n");
        Mismatches.Missing.forEach(s -> {
            try {
                writer.write(s + "\n");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        writer.write("Redundant: \n");
        Mismatches.Redundant.forEach(s -> {
            try {
                writer.write(s + "\n");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        writer.close();
        return Mismatches;
    }
    ConfigCheckInfo checkConfigs(JSONObject serverResponse,JSONObject LocalJson )
    {
        ConfigCheckInfo Mismatches = new ConfigCheckInfo();
        Map<String, Object> ResponceMap= serverResponse.toMap();
        Map<String, Object> LocalMap= LocalJson.toMap();
        if(!LocalMap.equals(ResponceMap))
        {
            ResponceMap.forEach((key,val ) ->{
                if(LocalMap.containsKey(key))
                {
                    if(!LocalMap.get(key).equals(val))
                        Mismatches.Outdated.add(key);
                }
                else
                {
                    Mismatches.Missing.add(key);
                }

            } );
        }


        return Mismatches;
    }
    JSONObject getLocalConfigTimestamps(Path path) throws IOException
    {
        if(!Files.exists(path))
        {
            FileWriter writer = new FileWriter(Files.createFile(path).toString());
            writer.write("{}");
            writer.close();
        }
        String LocalConfigs = Files.readString(path);

        return  new JSONObject(LocalConfigs);
    }
    void unpackResponceJson(JSONObject json,responceObj result)
    {
        String str;
        String modsListStr = json.get("mods").toString().replace("\"","");
        modsListStr = modsListStr.substring(1,modsListStr.length()-1);
        Scanner modsScanner = new Scanner(modsListStr)
                .useDelimiter(",");
             while (modsScanner.hasNext()) {
                 str = modsScanner.next();

                 result.modList.add(str);
             }


        String configStr = json.get("config").toString().replace('[','{').replace(']','}');

        result.configJson = new JSONObject(configStr);


        if(false)
        {
            String resourceListStr = json.get("config").toString();
            Scanner resourceScanner = new Scanner(resourceListStr)
                    .useDelimiter(",");

            while (resourceScanner.hasNext()) {
                str = modsScanner.next();

                result.resourcepackList.add(str);
            }
        }

    }
    void OutputTestFile(ConfigCheckInfo MismatchesInConfigs) throws IOException
    {
       // Path path = Paths.get(System.getProperty("user.dir")+ "/TestConfigChange.txt").toAbsolutePath();
        Path path = Path.of(enteredPath.getText()+"/TestConfigChange.txt");
        if(Files.exists(path)) {
            Files.deleteIfExists(path);
        }
        FileWriter writer = new FileWriter(Files.createFile(path).toString());
        writer.write("Missing: \n");
        MismatchesInConfigs.Missing.forEach(s -> {
            try {
                writer.write(s + "\n");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        writer.write("Outdated: \n");
        MismatchesInConfigs.Outdated.forEach(s -> {
            try {
                writer.write(s + "\n");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        writer.close();
    }

    JSONObject currentTimestampJson = new JSONObject();
    MainGUI()
    {
        System.out.println("Starting gui");
        Path timestampPath = Paths.get(System.getProperty("user.dir")+ "/ConfigTimestamps.json").toAbsolutePath();
      CheckUpdate.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            if(e.getActionCommand().equals("Check for  Updates"))
            {
                responceObj responceObj = new responceObj();

                try{
                    JSONObject responseJson = MyDownloader.downloadToJson(
                            "https://script.google.com/macros/s/AKfycbw8y6pCLYiOr-qdxyY7-cG-7EdDrPq6kynCnaKZgf745-H3p35CPJEqcX9ua6YinLQH/exec?download=false");

                    unpackResponceJson(responseJson,responceObj);


                   MismatchesInMods =          checkMods(responceObj.modList);
                   MismatchesInResourcePacks = checkResourcepacks(responceObj.resourcepackList);

                   JSONObject localData = getLocalConfigTimestamps(timestampPath);
                   MismatchesInConfigs =        checkConfigs(responceObj.configJson, localData);

                   currentTimestampJson = responceObj.configJson;
                    Label.setText("");
                    if(MismatchesInMods.Missing.isEmpty()&& MismatchesInMods.Redundant.isEmpty())
                        Label.setText(Label.getText()+ "Mods Compatible");

                    else Label.setText(Label.getText()+ "Mods Incompatible");


                    Label.setText(Label.getText()+ "       |        ");

                    if(MismatchesInConfigs.Outdated.isEmpty()&&MismatchesInConfigs.Missing.isEmpty())
                        Label.setText(Label.getText()+ "Configs Compatible");

                    else
                        Label.setText(Label.getText()+ "Configs Incompatible");
                    OutputTestFile(MismatchesInConfigs);



                }
                catch (Exception a)
                {

                    Label.setText("An error occured.\n Perhaps the ConfigTimestamps.json file is corrupted? " +
                            "\n Try deleting it. (It will reset all your configs).");
                    JOptionPane.showMessageDialog(null, a.getMessage());
                }
            }

          }
      });


        Update.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getActionCommand().equals("Update"))
                {
                    JOptionPane.showMessageDialog(null,"This may take a while since we use only free resources and we are being rate limited a lot, dont close the program until its done please");
                    Path currentRelativePath = Paths.get("");
                    String s = currentRelativePath.toAbsolutePath().toString();
                    responceObj  responceObj = new responceObj();
                    try{

                    JSONObject responseJson = MyDownloader.downloadToJson(
                            "https://script.google.com/macros/s/AKfycbw8y6pCLYiOr-qdxyY7-cG-7EdDrPq6kynCnaKZgf745-H3p35CPJEqcX9ua6YinLQH/exec?download=true");

                    unpackResponceJson(responseJson,responceObj);
                                                    //---------------------------------------CONFIGS--------------------------------------
                         MismatchesInConfigs.Missing.forEach(config -> {
                             try {
                                 Label.setText("Downloading config : " + config);
                                 Path path = Path.of(enteredPath.getText()+"/config");
                                 MyDownloader.downloadToFile(responceObj.configJson.get(config).toString(),path,config );
                             } catch (IOException ex) {
                                 System.out.println(ex.getCause());
                                 System.out.println(Thread.currentThread().getStackTrace().toString());

                                 throw new RuntimeException(ex);
                             }
                         });
                        MismatchesInConfigs.Outdated.forEach(config -> {
                            try {
                                Label.setText("Downloading config : " + config);
                                Path path = Path.of(enteredPath.getText()+"/config");
                                MyDownloader.downloadToFile(responceObj.configJson.get(config).toString(),path,config );
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                        });
                        if(Files.exists(timestampPath)) {
                            Files.deleteIfExists(timestampPath);
                        }
                        FileWriter writer = new FileWriter(Files.createFile(timestampPath).toString());
                        writer.write(currentTimestampJson.toString());
                        writer.close();

                        //---------------------------------------MODS--------------------------------------
                        JSONObject modLinksJson = new JSONObject(responseJson.get("mods").toString().replace("\n",""));

                        MismatchesInMods.Redundant.forEach(modName -> {
                            Path path = Path.of(enteredPath.getText()+"/mods/" + modName);
                            try {
                                Files.deleteIfExists(path);
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                        });

                        MismatchesInMods.Missing.forEach(modName -> {
                            Path path = Path.of(enteredPath.getText()+"/mods/");
                            try {
                                Label.setText("Downloading config : " + modName);
                                MyDownloader.downloadToFile(modLinksJson.get(modName).toString(),path,modName );
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                        });
//
                        Label.setText("Juz, mozna zamknac program");

                    }
                    catch (IOException a)
                    {
                        Label.setText(a.getMessage());
                    }
                }
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Rafalpack installer :3 meow");
        frame.setContentPane(new MainGUI().MainPanel);
      //  frame.pack();
        frame.setSize(400,400);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private JLabel Label;
    private JPanel MainPanel;
    private JButton CheckUpdate;
    private JButton Update;
    private JTextField enteredPath;
    private JRadioButton radioButton1;

    ModsCheckInfo MismatchesInMods ;
    List<String> MismatchesInResourcePacks;
    ConfigCheckInfo MismatchesInConfigs ;
}
