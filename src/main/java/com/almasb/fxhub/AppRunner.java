package com.almasb.fxhub;

// Adapted from https://mkyong.com/java/java-how-to-run-windows-bat-file/

import com.almasb.fxgl.core.util.Platform;
import com.almasb.fxgl.dsl.FXGL;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Paths;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class AppRunner {

    /**
     * Supported extensions: .msi, .bat
     *
     * @param exeFile the executable file to run
     */
    public static void run(File exeFile) {
        String fileName = exeFile.getAbsolutePath();

        ProcessBuilder processBuilder;

        if (fileName.endsWith(".msi")) {
            processBuilder = new ProcessBuilder("msiexec", "/i", fileName);
        } else {
            // TODO: mac hardcoded
            if (FXGL.getSettings().getPlatform() == Platform.MAC) {
                try {
                    new ProcessBuilder("chmod", "+x", fileName).start().waitFor();
                    new ProcessBuilder("chmod", "+x", Paths.get(fileName).resolveSibling("java").toAbsolutePath().toString()).start().waitFor();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // this handles .bat
            processBuilder = new ProcessBuilder(fileName);
        }

        try {
            Process process = processBuilder.start();

            StringBuilder output = new StringBuilder();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }

            int exitVal = process.waitFor();
            if (exitVal == 0) {
                System.out.println(output);

                System.out.println("Normal exit");

            } else {
                System.out.println("Not Normal exit");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}