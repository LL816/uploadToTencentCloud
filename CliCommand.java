package com.cherylcai.homework;

import org.apache.commons.cli.*;
import java.util.ArrayList;
import java.util.Arrays;
/**
 * Created by cheryl.cai on 4/1/2018.
 */
public class CliCommand {
    private Options options=new Options();
    private CommandLineParser parser = new DefaultParser();
    private HelpFormatter formatter = new HelpFormatter();
    private UploadToCloud uploadToCloud = new UploadToCloud();

    public void createOptions(String[] args){

        Option help = new Option("h","help",false,"print this help message");
        this.options.addOption(help);

        Option file = new Option("f","file",true,"upload one or more files");
        this.options.addOption(file);

        Option address = new Option("l", "location",true,"upload files to this location, default value is 'test' ");
        this.options.addOption(address);

        Option force = new Option("F","force",false,"force replace existing files, default setting is false");
        this.options.addOption(force);

        Option imgOnly = new Option("i", "imgOnly", false, "only image files under current dir will be uploaded");
        this.options.addOption(imgOnly);

        try {
            CommandLine commandLine = parser.parse(this.options,args);

            if(commandLine.hasOption("h")||commandLine.hasOption("help")) {
                System.out.println("Please use this tool to upload files to your tencent cloud bucket");
                formatter.printHelp("[-hF] [-f file1,file2,dir...] [-l location]...", options);
                System.exit(1);
            }

            if(commandLine.hasOption("F")||commandLine.hasOption("force")) {
                uploadToCloud.forceReplace=true;
            }

            if(commandLine.hasOption("i")||commandLine.hasOption("imgOnly")){
                uploadToCloud.imgOnly=true;
            }

            if(commandLine.hasOption("l")||commandLine.hasOption("location")){
                uploadToCloud.destination = commandLine.getOptionValue("l");
            }

            if(commandLine.hasOption("f")||commandLine.hasOption("file")) {
                uploadToCloud.sourcePaths=new ArrayList<>(Arrays.asList(commandLine.getOptionValue("f").split(",")));
            }

            uploadToCloud.uploadFile();


        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("upload [-hiF] [-f file1,file2,dir...] [-l location]...", options);

        }
    }


    public static void main(String[] args){
        CliCommand cli = new CliCommand();
        cli.createOptions(args);
        System.out.println("completed!");
    }
}
