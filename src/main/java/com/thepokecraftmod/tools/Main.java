package com.thepokecraftmod.tools;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.tukaani.xz.LZMA2Options;
import org.tukaani.xz.XZOutputStream;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    private static final LZMA2Options OPTIONS = new LZMA2Options();
    private static final Path ROOT_REPO_DIR = Paths.get("repositories");
    private static final List<Path> REPOSITORIES = List.of(
            ROOT_REPO_DIR.resolve("pokemon/models")
    );

    public static void main(String[] args) throws IOException {
        for (var repository : REPOSITORIES) {
            System.out.println("Exporting " + ROOT_REPO_DIR.relativize(repository));

            try (var stream = Files.walk(repository).filter(Files::isRegularFile)) {
                List<Path> files = stream.toList();
                writeRepository(files, repository, Paths.get("assets/pokecraft/repositories/" + ROOT_REPO_DIR.relativize(repository) + ".fsrepo"));
            }
        }

        System.out.println("Exported all " + REPOSITORIES.size() + " repositories");
    }

    public static void writeRepository(List<Path> files, Path relativePath, Path output) throws IOException {
        Files.createDirectories(output.getParent());

        try (var xzWriter = new XZOutputStream(Files.newOutputStream(output), OPTIONS)) {
            try (var tarWriter = new TarArchiveOutputStream(xzWriter)) {
                for (var file : files) {
                    var entry = new TarArchiveEntry(file, relativePath.relativize(file).toString());
                    tarWriter.putArchiveEntry(entry);
                    if (Files.isRegularFile(file)) try (var is = new BufferedInputStream(Files.newInputStream(file))) {
                        IOUtils.copy(is, tarWriter);
                    }
                    tarWriter.closeArchiveEntry();
                }
            }
        }
    }
}