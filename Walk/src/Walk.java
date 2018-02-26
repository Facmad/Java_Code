import com.sun.istack.internal.NotNull;
import exceptions.CreateDirectoriesException;
import exceptions.PathException;


import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Walk {

    private String inputFile;
    private String outputFile;

    public Walk(@NotNull String input,@NotNull String output) {
        inputFile = input;
        outputFile = output;
    }

    public static void main (String[] args) {
        if (args.length == 2) {
            try {
                new Walk(args[0], args[1]).run();
            } catch (IOException | InvalidPathException | NullPointerException ex) {
                System.err.println(ex.getMessage());
            }
        } else {
            System.err.println("Usage: java Walk <input file> <output file>.");
        }

    }

    private void run () throws IOException, PathException {
        try {
            List<String> files = Files.readAllLines(Paths.get(inputFile));
            countHashesAndWrite(files);
        } catch (InvalidPathException ex) {
            throw new PathException("Invalid path", "Error trying to get path");
        }
    }

    private void countHashesAndWrite(List<String> files) throws IOException, PathException {
        Path outputPath = Paths.get(outputFile);
        if (outputPath.getParent() != null) {
            try {
                Files.createDirectories(Paths.get(outputFile).getParent());
            } catch (IOException ex) {
                throw new CreateDirectoriesException("Error trying to create directories, which contain output file");
            }
        }
        try (BufferedWriter output = Files.newBufferedWriter(outputPath)) {
            for (String file : files) {
                output.write(String.format("%08x",getFileHash(file)) + " " + file + "\n");
            }
        } catch (IOException ex) {
            throw new IOException("Error trying to work with output file");
        } catch (InvalidPathException ex) {
            throw new PathException("Invalid path exceptions", "Error trying to get path");
        }
    }

    private int getFileHash (String file) {
        try (ByteChannel input = Files.newByteChannel(Paths.get(file))) {
            ByteBuffer buf = ByteBuffer.allocate(2048);
            int hash = 0x811c9dc5;
            while (input.read(buf) >= 0) {
                hash = FNV(buf, hash);
                buf.flip();
            }
            return hash;
        } catch (IOException | InvalidPathException ex) {
            return 0;
        }
    }

    private int FNV (final ByteBuffer buf, int hash) {
        for (int i = 0; i < buf.position(); ++i) {
            hash *=  0x01000193;
            hash ^= (buf.get(i) & 0xff) ;
        }
        return hash;
    }
}