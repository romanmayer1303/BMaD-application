import org.kramerlab.bmad.algorithms.*;
import org.kramerlab.bmad.general.Tuple;
import org.kramerlab.bmad.matrix.BooleanMatrix;
import weka.core.Instances;

import java.io.File;
import java.util.*;

import static org.kramerlab.bmad.matrix.BooleanMatrix.FALSE;
import static org.kramerlab.bmad.matrix.BooleanMatrix.TRUE;

/**
 * Created by romanmayer on 22/12/16.
 */
public class BMaD_DM {

    private static int n;
    private static HashMap<String, List<String>> adjacencyList;
    private static HashMap<String, Integer> matrixIndices;
    private static byte[][] adjacencyMatrix;

    // read file and
    // create hashmap adjacencyList of all vertices
    // key: vertex v
    // value: [vertices], that v is connected to
    private static void computeAdjacencyList(String file1) {
        try {
            Scanner sc = new Scanner(new File(file1));
            adjacencyList = new HashMap<String, List<String>>();
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] split = line.split(" ");
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
        }
        catch (Exception e) {
            System.out.print(e);
        }
    }

    // matrixIndices saves the position each vertex has in the adjacencyList
    private static void computeMatrixIndices() {
        n = adjacencyList.size();
        String[] keys = new String[n];
        adjacencyList.keySet().toArray(keys);
        matrixIndices = new HashMap<String, Integer>();
        for (int i = 0; i < n; i++) {
            matrixIndices.put(keys[i], i);
        }
    }

    // fill adjacencyMatrix with FALSE; just to be sure
    private static void initializeAdjacencyMatrix() {
        adjacencyMatrix = new byte[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                adjacencyMatrix[i][j] = FALSE;
            }
        }
    }

    // set adjacencyMatrix's elements to TRUE, if vertices are connected
    private static void fillAdjacencyMatrix() {
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
    }

    private static void decomposeAndPrint(double onesWeight, int dimension) {
        // create new BooleanMatrixDecomposition
        BooleanMatrixDecomposition algorithm =
                new BooleanMatrixDecomposition(new AssociationGenerator(0.2),
                        new FastLoc(),
                        new CombinatorPipeline(new DensityGreedyCombinator(),
                                new Iter()),
                        1d);



        // build BooleanMatrix from our adjacencyMatrix (byte[][])
        BooleanMatrix booleanAdjacencyMatrix = new BooleanMatrix(adjacencyMatrix);
        // use algorithm to decompose matrix
        // TODO: make "2", i.e. k a parameter available from main
        Tuple<BooleanMatrix, BooleanMatrix>  t_bool = algorithm.decompose(booleanAdjacencyMatrix, dimension);

        // c*b = original matrix
        BooleanMatrix c = t_bool._1;
        BooleanMatrix b = t_bool._2;

        // to calculate the reconstruction error, we first multiply the matrices:
        BooleanMatrix reconstruction = c.booleanProduct(b);
        // weird error taken from github example
        double reconstructionError =
                booleanAdjacencyMatrix.relativeReconstructionError(reconstruction, onesWeight);

        // get printable instances
        Instances basisRows = b.toInstances(); //t._2;
        Instances learnableRepresentation = c.toInstances(); // t._1;

        //print
        System.out.println("Basis: \n" + basisRows);
        System.out.println("Representation: \n" + learnableRepresentation);
        System.out.println(reconstructionError);
    }


    public static void main(String[] args) {
        computeAdjacencyList("datasets/698.edges");
        computeMatrixIndices();
        initializeAdjacencyMatrix();
        fillAdjacencyMatrix();

        // error weight
        double onesWeight = 1d;
        // dimension of decomposition (k)
        int dimension = 3;
        decomposeAndPrint(onesWeight, dimension);
    }

}
