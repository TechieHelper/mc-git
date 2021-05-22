package com.techiehelper.gitmc;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.network.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.WorldSavePath;

import java.io.*;
import java.util.*;

public class Util {
    public static final UUID VALID_UUID = UUID.fromString("2e26f35f-b6a7-4939-9377-76b1149b4b4d");
    public static MinecraftServer server;
    
    public static String runCommand(String command, CommandContext<ServerCommandSource> ctx) throws IOException {
        Runtime rt = Runtime.getRuntime();
        Process proc = rt.exec("cmd /c cd " + ctx.getSource().getMinecraftServer().getSavePath(WorldSavePath.ROOT).toAbsolutePath() + "&" + command);
    
        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(proc.getInputStream()));
    
        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(proc.getErrorStream()));

        StringBuilder s = new StringBuilder();
        String c = "";
        while ((c = stdInput.readLine()) != null) {
            s.append(c);
        }

        while ((c = stdError.readLine()) != null) {
            s.append(c);
        }
        proc.destroy();
        return s.toString();
    }
    
    public static String somethingWentWrongTry(String command, CommandContext<ServerCommandSource> ctx) {
        try {
            return runCommand(command, ctx);
        } catch (IOException e) {
            displayMessage(ctx, "Something went wrong! Here is the error message:\n" + Arrays.toString(e.getStackTrace()), Formatting.RED);
            return null;
        }
    }
    
    public static void displayMessage(String message) {
        displayMessage(server, message, Formatting.WHITE);
    }
    
    public static void displayMessage(String message, Formatting formatting) {
        displayMessage(server, message, formatting);
    }
    
    public static void displayMessage(CommandContext<ServerCommandSource> ctx, String message) {
        displayMessage(ctx.getSource().getMinecraftServer(), message, Formatting.WHITE);
    }
    
    public static void displayMessage(CommandContext<ServerCommandSource> ctx, String message, Formatting formatting) {
        displayMessage(ctx.getSource().getMinecraftServer(), message, formatting);
    }
    
    public static void displayMessage(MinecraftServer server, String message) {
        displayMessage(server, message, Formatting.WHITE);
    }
    
    public static void displayMessage(MinecraftServer server, String message, Formatting formatting) {
        server.getPlayerManager().broadcastChatMessage(new LiteralText(message).formatted(formatting), MessageType.SYSTEM, VALID_UUID);
    }
    
    public static HashMap<String, String> parseFile(File file) {
        HashMap<String, String> output = new HashMap<>();
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String data = scanner.nextLine();
                String[] splitData = data.split("=");
                if (splitData.length == 2) {
                    output.put(splitData[0], splitData[1]);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return output;
    }
    
    public static void writeToFile(File file, HashMap<String, String> writeData) {
        try {
            if (file.createNewFile()) {
                FileWriter writer = new FileWriter(file);
                BufferedWriter out = new BufferedWriter(writer);
                String outputData = "";
                for (Map.Entry<String, String> entry : writeData.entrySet()) {
                    outputData += entry.getKey() + "=" + entry.getValue() + "\n";
                }
                out.write(outputData);
                out.close();
            } else {
                HashMap<String, String> currentData = Util.parseFile(file);
                LinkedList<String> notPresentData = new LinkedList<>();
                currentData.forEach((k, v) -> {
                    System.out.println(k + " " + writeData.get(k));
                    if (writeData.get(k) == null) notPresentData.add(k);
                });
                FileWriter writer = new FileWriter(file);
                BufferedWriter out = new BufferedWriter(writer);
                String outputData = "";
                for (Map.Entry<String, String> entry : writeData.entrySet()) {
                    outputData += entry.getKey() + "=" + entry.getValue() + "\n";
                }
                for (String npd : notPresentData) {
                    outputData += npd + "=" + currentData.get(npd) + "\n";
                }
                out.write(outputData);
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
