// BuildDB.java
// JG Miller (JGM), Portland, OR, jimsemantic@gmail.com
// 3/15/2020
//
// Assumes:  in input file, no missing or empty fields, no deviation from formatting as listed in handout, spellings
// of TITLEs and PROVIDERs are consistent, STBs all have 2-digit ids, REVs are all under 10.00, no times in
// 10:00->12:59, and capitalization matters. Many of the assumptions are to ensure constant field lengths, for ease of coding.


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

class BuildDB {
    String inputFileName;
    Store store;
    Index primaryKeyIndex;
    ArrayList<Index> columnIndices;

    BuildDB() throws IOException {
        inputFileName = "util/input_data";
        System.out.printf("Reading source data from %s\n", inputFileName);
        store = new Store("store.dat");
        columnIndices = new ArrayList<>();
    }

    public static void main(String[] args) {
        System.out.println("Started:  " + LocalDateTime.now() + "\n");

        BufferedReader br = null;
        try {
            BuildDB buildDB = new BuildDB();
            buildDB.primaryKeyIndex = buildDB.createPrimaryKeyIndex("primary_key");
            br = new BufferedReader(new FileReader(buildDB.inputFileName));
            ArrayList<String> fields = new ArrayList<>();
            ArrayList<String> primaryKey;
            String headerLine = br.readLine();
            buildDB.store.addRecordToStore(headerLine);
            ArrayList<String> headers = new ArrayList(Arrays.asList(headerLine.split("\\|")));
            headers.forEach((h) -> {
                try {
                    buildDB.createColumnIndex(h);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            String line;
            Long lineOffset;
            while ((line = br.readLine()) != null) {
                primaryKey = buildDB.getPrimaryKey(buildDB, line, fields);
                lineOffset = buildDB.getLineOffset(buildDB, primaryKey);
                if (lineOffset == null) {
                    lineOffset = buildDB.store.addRecordToStore(line);
                    buildDB.primaryKeyIndex.storeUnique(primaryKey, lineOffset);
                } else
                    buildDB.store.overwriteRecordInStore(line, lineOffset);
            }
            for (int i = 0; i < buildDB.columnIndices.size(); i++) {
                Iterator it = buildDB.primaryKeyIndex.hashMap.entrySet().iterator();
                while (it.hasNext()) {
                    HashMap.Entry pair = (HashMap.Entry) it.next();
                    line = buildDB.store.getRecord((Long) pair.getValue());
                    primaryKey = (ArrayList<String>) pair.getKey();
                    lineOffset = buildDB.getLineOffset(buildDB, primaryKey);
                    fields.clear();
                    fields.addAll(Arrays.asList(line.split("\\|")));git
                    buildDB.columnIndices.get(i).storeMultivalue(fields.get(i), lineOffset);
                }
                buildDB.columnIndices.get(i).serialize();
            }
            buildDB.primaryKeyIndex.serialize();
            buildDB.store.closeStore();
            System.out.printf("File backing store saved to %s\n", buildDB.store.storeFileName);

            System.out.println("\n" + "Finished:  " + LocalDateTime.now());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private ArrayList<String> getPrimaryKey(BuildDB buildDB, String line, ArrayList<String> fields) {
        ArrayList<String> primaryKey = new ArrayList<>();
        fields.clear();
        fields.addAll(Arrays.asList(line.split("\\|")));
        primaryKey.add(fields.get(0));
        primaryKey.add(fields.get(1));
        primaryKey.add(fields.get(3));
        return primaryKey;
    }

    private Long getLineOffset(BuildDB buildDB, ArrayList<String> primaryKey) {
        return (Long) buildDB.primaryKeyIndex.lookup(primaryKey);
    }

    private Index createPrimaryKeyIndex(String h) throws IOException {
        return primaryKeyIndex = new Index<ArrayList<String>, Long>(h + ".idx");
    }

    private void createColumnIndex(String h) throws IOException {
        Index index = new Index<String, ArrayList<Long>>(h + ".idx");
        columnIndices.add(index);
    }
}