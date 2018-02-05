package com.git;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.util.ArrayList;
import java.util.List;


public class GIT {
	
	public static void main(String[] args) throws IOException, InterruptedException
	{
		Path path = FileSystems.getDefault().getPath("D:\\source\\testprojectselenium\\testproject");
		//gitpath(path);
		ProcessBuilder pb = new ProcessBuilder("cmd");
    	pb.directory(new File("D:\\source\\testprojectselenium\\testproject"));
    	Process pp1 = pb.start();
    	File newFile = new File("D:\\source\\testprojectselenium\\testproject", "myNewFile");
    	newFile.createNewFile();
    	gitcheckout(path);
    	gitadd(path);
    	gitCommit(path,"test");
    	gitpath(path);
		//gitlist(path);
		
	}
	
	/*private static List<Path> allFilesInDirectory(Path directory) throws IOException {
        final List<Path> files = new ArrayList<>();
        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                if (dir.endsWith(".git")) {
                    return FileVisitResult.SKIP_SUBTREE;
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (attrs.isRegularFile()) {
                    files.add(file);
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return files;
    }*/

    public static void gitInit(Path directory) throws IOException, InterruptedException {
        runCommand(directory, "git", "init");
    }
    public static void gitcheckout(Path directory) throws IOException, InterruptedException {
        runCommand(directory, "git", "checkout","-b","develop");
    }

    public static void gitadd(Path directory) throws IOException, InterruptedException {
        runCommand(directory, "git", "add", "--all");
    }

    public static void gitCommit(Path directory, String message) throws IOException, InterruptedException {
        runCommand(directory, "git", "commit", "-m", message);
    }

    public static void gitGc(Path directory) throws IOException, InterruptedException {
        runCommand(directory, "git", "gc");
    }
    public static void gitpath(Path directory) throws IOException, InterruptedException {
    	 runCommand(directory, "git", "push");
    }
    public static void gitlist(Path directory) throws IOException, InterruptedException {
        runCommand(directory, "git", "ls-files");
    }

    public static void runCommand(Path directory, String... command) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder()
                .command(command)
                .directory(directory.toFile());
        Process p = pb.start();
        StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream(), "ERROR");
        StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(), "OUTPUT");
        outputGobbler.start();
        errorGobbler.start();
        int exit = p.waitFor();
        errorGobbler.join();
        outputGobbler.join();
        /*if (exit != 0) {
            throw new AssertionError(String.format("runCommand returned %d", exit));
        }*/
    }
   

    private static class StreamGobbler extends Thread {

        InputStream is;
        String type;

        private StreamGobbler(InputStream is, String type) {
            this.is = is;
            this.type = type;
        }

        @Override
        public void run() {
            try {
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line = null;
                while ((line = br.readLine()) != null) {
                    System.out.println(type + "> " + line);
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
}
