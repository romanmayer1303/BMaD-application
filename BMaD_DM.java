import java.io.File;
import java.util.*;

import static java.lang.Boolean.FALSE;
import static org.kramerlab.bmad.matrix.BooleanMatrix.TRUE;

/**
 * Created by romanmayer on 22/12/16.
 */
public class BMaD_DM {

    private static int n;
    private static HashMap<String, List<String>> adjacencyList;
    private static HashMap<String, Integer> matrixIndices;
    private static byte[][] adjacencyMatrix;

    private static void createNetwork(String file1) {
        try {
            Scanner sc = new Scanner(new File(file1));

            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] split = line.split(" ");

                // create hashmap of all vertices
                adjacencyList = new HashMap<String, List<String>>();
                String key = split[0];
                String value = split[1];

                // if vertex exists as key, add value;
                // else, create key and add value;
                if (adjacencyList.containsKey(key)) {
                    adjacencyList.get(key).add(value);
                } else {
                    ArrayList<String> valueList = new ArrayList<String>();
                    valueList.add(value);
                    adjacencyList.put(key, valueList);
                }
            }


            n = adjacencyList.size();
            String[] keys = new String[n];
            adjacencyList.keySet().toArray(keys);
            for (int i = 0; i < n; i++) {
                matrixIndices.put(keys[i], i);
            }

            adjacencyMatrix = new byte[n][n];
            Arrays.fill(adjacencyMatrix, FALSE);

            Iterator entries = adjacencyList.entrySet().iterator();

            while (entries.hasNext()) {
                Map.Entry<String, ArrayList<String>> entry = (Map.Entry) entries.next();
                String key = entry.getKey();
                ArrayList<String> value = entry.getValue();
                Integer row = matrixIndices.get(key);
                for (String vertex : value) {
                    adjacencyMatrix[row][matrixIndices.get(vertex)] = TRUE;
                }
            }


        } catch (Exception e) {

        }
    }

    public static void main(String[] args) {
        createNetwork("datasets/698.edges");
    }

}
