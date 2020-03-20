// Query.java
// JG Miller (JGM), Portland, OR, jimsemantic@gmail.com
// 3/15/2020
//
// Assumes command line args containing whitespace are surrounded with quotes, and TITLE/PROVIDER don't contain commas.


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

class Query {
    Store store;
    HashMap<String, Integer> headerMap;
    HashMap<String, ArrayList<String>> queryArgs;
    ArrayList<ArrayList<String>> resultRecordsList;

    Query() throws IOException {
        store = new Store();
        store.openStore("store.dat");
        makeHeaderMap();
        queryArgs = new HashMap<>();
        resultRecordsList = new ArrayList<>();
    }

    public static void main(String[] args) {
        System.out.println("Started:  " + LocalDateTime.now() + "\n");

        try {
            Query query = new Query();
            query.parseArgs(args);

            // TODO:  Reimplement as a Stream pipeline
            query.filter();
            query.order();
            query.select();

            query.store.closeStore();
            query.printResults();

            System.out.println("\n" + "Finished:  " + LocalDateTime.now());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    void makeHeaderMap() throws IOException {
        String headerLine = store.getHeaderLine();
        ArrayList<String> headers = new ArrayList(Arrays.asList(headerLine.split("\\|")));
        headerMap = new HashMap<>();
        for (int i = 0; i < headers.size(); i++) {
            headerMap.put(headers.get(i), i);
        }
    }

    void parseArgs(String[] args) throws IllegalArgumentException {
        for (int i = 0; i < args.length; i++) {
            if (i == args.length - 1)
                throw new IllegalArgumentException("Query function args must be valid store fields");
            ArrayList<String> functionArgs = new ArrayList<>(Arrays.asList(args[i + 1].split(",")));
            testFunctionArgs(functionArgs);
            switch (args[i]) {
                case "-s":
                    queryArgs.put("SELECT", functionArgs);
                    i++;
                    break;
                case "-o":
                    queryArgs.put("ORDER", functionArgs);
                    i++;
                    break;
                case "-f":
                    queryArgs.put("FILTER", functionArgs);
                    i++;
                    break;
                default:
                    throw new IllegalArgumentException("Query functions must be SELECT, ORDER, or FILTER");
            }
        }
    }

    private void testFunctionArgs(ArrayList<String> functionArgs) throws IllegalArgumentException {
        Integer indexOfEqualsSign;
        for (String fa : functionArgs) {
            indexOfEqualsSign = fa.indexOf('=');
            if (indexOfEqualsSign != -1)
                fa = fa.substring(0, indexOfEqualsSign);
            if (headerMap.get(fa) == null)
                throw new IllegalArgumentException("Query function args must be valid store fields");
        }
    }

    void filter() throws IOException, ClassNotFoundException {
        ArrayList<String> filterArgs = queryArgs.get("FILTER");
        ArrayList<String> storeRecords = new ArrayList<>();
        Index index = new Index();
        if (filterArgs == null || filterArgs.isEmpty())
            storeRecords = store.getAllRecords();
        else {
            ArrayList<String> filterTokens = new ArrayList<>();
            ArrayList<Long> filterTokensLookup = new ArrayList<>();
            HashSet<Long> resultOffsets = new HashSet<>();
            HashSet<Long> filterOffsets = new HashSet<>();
            for (int i = 0; i < filterArgs.size(); i++) {
                filterTokens.clear();
                filterOffsets.clear();
                filterTokens.addAll(new ArrayList(Arrays.asList(filterArgs.get(i).split("="))));
                index.loadIndex(filterTokens.get(0) + ".idx");
                index.deserialize();
                filterTokensLookup = (ArrayList<Long>) index.lookup(filterTokens.get(1));
                if (filterTokensLookup != null)
                    filterOffsets.addAll(filterTokensLookup);
                if (i == 0)
                    resultOffsets.addAll(filterOffsets);
                else
                    resultOffsets.retainAll(filterOffsets);
            }
            for (Long offset : resultOffsets)
                storeRecords.add(store.getRecord(offset));
        }
        storeRecords.forEach((r) -> {
            resultRecordsList.add(new ArrayList(Arrays.asList(r.split("\\|"))));
        });
    }

    void order() {
        ArrayList<String> orderArgs = queryArgs.get("ORDER");
        ArrayList<Integer> orderArgPositions = new ArrayList();
        if (orderArgs == null)
            return;
        for (String orderArg : orderArgs)
            orderArgPositions.add(headerMap.get(orderArg));

        Comparator<ArrayList<String>> listStringComp = (list1, list2) -> {
            int comp = 0;
            for (Integer sortField : orderArgPositions) {
                comp = ((String) list1.get(sortField)).compareTo((String) list2.get(sortField));
                if (comp != 0)
                    break;
            }
            return comp;
        };

        resultRecordsList.sort(listStringComp);
    }

    void select() {
        ArrayList<String> selectArgs = queryArgs.get("SELECT");
        ArrayList<Integer> selectArgPositions = new ArrayList<>();
        if (selectArgs == null)
            return;
        for (String selectArg : selectArgs)
            selectArgPositions.add(headerMap.get(selectArg));
        //Collections.sort(selectArgPositions);
        ArrayList<String> revisedRecord;
        for (int i = 0; i < resultRecordsList.size(); i++) {
            revisedRecord = new ArrayList<>();
            for (Integer selectArg : selectArgPositions)
                revisedRecord.add(resultRecordsList.get(i).get(selectArg));
            resultRecordsList.set(i, revisedRecord);
        }
    }

    void printResults() {
        resultRecordsList.forEach((r) -> {
            System.out.println(String.join(",", r));
        });
    }
}
