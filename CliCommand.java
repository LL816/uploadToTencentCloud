package com.cherylcai.homework;

import org.apache.commons.cli.*;
import java.io.File;

/**
 * Created by cheryl.cai on 4/1/2018.
 */
public class CliCommand {
    private Options options=new Options();
    private CommandLineParser parser = new DefaultParser();
    private HelpFormatter formatter = new HelpFormatter();
    private UploadToCloud uploadToCloud = new UploadToCloud();
    private String uploadTo = "/test/";

    public void createOptions(String[] args){

        Option help = new Option("h","help",false,"print this help message");
        this.options.addOption(help);

        Option file = new Option("f","file",true,"upload one or more files");
        this.options.addOption(file);

        Option address = new Option("l", "location",true,"upload files to this location, default value is 'test' ");
        this.options.addOption(address);

        Option force = new Option("F","force",false,"force replace existing files, default setting is false");
        this.options.addOption(force);

        try {
            CommandLine commandLine = parser.parse(this.options,args);

            if(commandLine.hasOption("h")||commandLine.hasOption("help")) {
                formatter.printHelp("upload [-hF] [-f file] [-l location]...", options);
                return;
            }

            if(commandLine.hasOption("F")||commandLine.hasOption("force")) {
                uploadToCloud.forceReplace=true;
            }

            if(commandLine.hasOption("l")||commandLine.hasOption("location")){
                uploadTo = commandLine.getOptionValue("l");
            }

            if(commandLine.hasOption("f")||commandLine.hasOption("file")) {
                String[] filenames=commandLine.getOptionValue("f").split(",");
                for(String name:filenames){
                    File uploadFile=new File(name.trim());
                    String target=uploadTo;
                    uploadToCloud.uploadFile(uploadFile,uploadTo);
                }
            }else{
                System.out.println("Please define which file to upload!");
                formatter.printHelp("upload [-hF] [-f file] [-l location]...", options);
            }

        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("upload [-hF] [-f file] [-l location]...", options);

        }
    }


    public static void main(String[] args){
        CliCommand cli = new CliCommand();
        cli.createOptions(args);
        System.out.println("completed!");
    }
}
