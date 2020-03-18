// Store.java
// JG Miller (JGM), Portland, OR, jimsemantic@gmail.com
// 3/17/2020

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;

class Store {
    String storeFileName;
    RandomAccessFile storeRAFile;

    Store() {}

    Store(String name) throws IOException {
        storeFileName = name;
        File storeFile = new File(storeFileName);
        Files.deleteIfExists(storeFile.toPath());
        storeRAFile = new RandomAccessFile(storeFile, "rw");
    }

    void openStore(String name) throws IOException {
        storeFileName = name;
        File storeFile = new File(storeFileName);
        storeRAFile = new RandomAccessFile(storeFile, "rw");
    }

    String getHeaderLine() throws IOException {
        storeRAFile.seek(0);
        return storeRAFile.readUTF();
    }

    Long addRecordToStore(String line) throws IOException {
        Long offset = storeRAFile.length();
        storeRAFile.seek(offset);
        storeRAFile.writeUTF(line);
        return offset;
    }

    void overwriteRecordInStore(String line, Long offset) throws IOException {
        storeRAFile.seek(offset);
        storeRAFile.writeUTF(line);
    }

    void printStore() throws IOException {
        String storeLine;
        storeRAFile.seek(0);
        while (storeRAFile.getFilePointer() != storeRAFile.length()) {
            storeLine = storeRAFile.readUTF();
            System.out.println(storeLine);
        }
    }

    void closeStore() throws IOException {
        storeRAFile.close();
    }
}
