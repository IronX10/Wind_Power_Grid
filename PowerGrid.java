//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileReader;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//
//class PowerLine {
//
//    String cityA;
//    String cityB;
//
//    public PowerLine(String cityA, String cityB) {
//        this.cityA = cityA;
//        this.cityB = cityB;
//    }
//}
//
//// Students can define new classes here
//public class PowerGrid {
//
//    int numCities;
//    int numLines;
//    String[] cityNames;
//    PowerLine[] powerLines;
//    ArrayList<ArrayList<Integer>> adjList = new ArrayList<>(numCities);
//    HashMap<String, Integer> cityToIndex = new HashMap<>();
//    ArrayList<ArrayList<Integer>> bridgeTree = new ArrayList<>();
//    int[][] up;
//    int[] depth;
//    int LOG = 20;
//    int[] Parent;
//
//    // Students can define private variables here
//    public PowerGrid(String filename) throws Exception {
//        File file = new File(filename);
//        BufferedReader br = new BufferedReader(new FileReader(file));
//        numCities = Integer.parseInt(br.readLine().trim());
//        numLines = Integer.parseInt(br.readLine().trim());
//        cityNames = new String[numCities];
//        for (int i = 0; i < numCities; i++) {
//            cityNames[i] = br.readLine().trim();
//            cityToIndex.put(cityNames[i], i);
//        }
//        powerLines = new PowerLine[numLines];
//        for (int i = 0; i < numLines; i++) {
//            String[] line = br.readLine().split(" ");
//            powerLines[i] = new PowerLine(line[0], line[1]);
//        }
//
//        // TO be completed by students
//        for (int i = 0; i < numCities; i++) {
//            adjList.add(new ArrayList<>());
//        }
//
//        // for (int i = 0; i < numCities; i++) {
//        //     cityToIndex.put(cityNames[i], i);
//        // }
//        for (int i = 0; i < numLines; i++) {
//            adjList.get(cityToIndex.get(powerLines[i].cityA)).add(cityToIndex.get(powerLines[i].cityB));
//            adjList.get(cityToIndex.get(powerLines[i].cityB)).add(cityToIndex.get(powerLines[i].cityA));
//        }
//
//        for (int i = 0; i < numCities; i++) {
//            bridgeTree.add(new ArrayList<>());
//        }
//
//    }
//
//    public ArrayList<PowerLine> criticalLines() {
//        /*
//         * Implement an efficient algorithm to compute the critical transmission lines
//         * in the power grid.
//
//         * Expected running time: O(m + n), where n is the number of cities and m is the
//         * number of transmission lines.
//         */
//        ArrayList<PowerLine> ans = new ArrayList<>();
//
//        int[] time = new int[numCities];
//        Arrays.fill(time, Integer.MAX_VALUE);
//        int[] low = new int[numCities];
//        Arrays.fill(low, Integer.MAX_VALUE);
//
//        boolean[] visited = new boolean[numCities];
//        int[] parent = new int[numCities];
//        Arrays.fill(parent, -1);
//        int[] timeCounter = {0};
//
//        for (int i = 0; i < numCities; i++) {
//            if (!visited[i]) {
//                bridge_Check_Using_DFS(i, time, low, visited, parent, timeCounter, ans);
//            }
//        }
//
//        return ans;
//    }
//
//    private void bridge_Check_Using_DFS(int u, int[] time, int[] low, boolean[] visited, int[] parent,
//                                        int[] timeCounter, ArrayList<PowerLine> ans) {
//
//        visited[u] = true;
//        time[u] = low[u] = timeCounter[0]++;
//
//        for (int v : adjList.get(u)) {
//            if (v == parent[u]) {
//                continue;
//            } else if (visited[v]) {
//                low[u] = Math.min(low[u], time[v]);
//            } else {
//                parent[v] = u;
//                bridge_Check_Using_DFS(v, time, low, visited, parent, timeCounter, ans);
//
//                low[u] = Math.min(low[u], low[v]);
//
//                if (low[v] > time[u]) {
//                    ans.add(new PowerLine(cityNames[u], cityNames[v]));
//                }
//            }
//        }
//    }
//
//
//    public void preprocessImportantLines() {
//        /*
//         * Implement an efficient algorithm to preprocess the power grid and build
//         * required data structures which you will use for the numImportantLines()
//         * method. This function is called once before calling the numImportantLines()
//         * method. You might want to define new classes and data structures for this
//         * method.
//
//         * Expected running time: O(n * logn), where n is the number of cities.
//         */
//
//        ArrayList<PowerLine> bridges = criticalLines();
//
//        for (PowerLine pl : bridges) {
//            int u = cityToIndex.get(pl.cityA);
//            int v = cityToIndex.get(pl.cityB);
//            bridgeTree.get(u).add(v);
//            bridgeTree.get(v).add(u);
//        }
//
//        // LCA prep
//        up = new int[numCities][LOG];
//        depth = new int[numCities];
//        Parent = new int[numCities];
//
//        dfsLCA(0, 0);
//
//    }
//
//    public int numImportantLines(String cityA, String cityB) {
//        /*
//         * Implement an efficient algorithm to compute the number of important
//         * transmission lines between two cities. Calls to numImportantLines will be
//         * made only after calling the preprocessImportantLines() method once.
//
//         * Expected running time: O(logn), where n is the number of cities.
//         */
//
//        int u = cityToIndex.get(cityA);
//        int v = cityToIndex.get(cityB);
//        int lcaNode = lca(u, v);
//        return depth[u] + depth[v] - 2 * depth[lcaNode];
//    }
//
//    private void dfsLCA(int node, int par) {
//        Parent[node] = par;
//        up[node][0] = par;
//
//        for (int i = 1; i < LOG; i++) {
//            up[node][i] = up[up[node][i - 1]][i - 1];
//        }
//
//        for (int nei : bridgeTree.get(node)) {
//            if (nei != par) {
//                depth[nei] = depth[node] + 1;
//                dfsLCA(nei, node);
//            }
//        }
//    }
//
//    private int lca(int u, int v) {
//        if (depth[u] < depth[v]) {
//            int temp = u;
//            u = v;
//            v = temp;
//        }
//
//        for (int i = LOG - 1; i >= 0; i--) {
//            if (depth[u] - (1 << i) >= depth[v]) {
//                u = up[u][i];
//            }
//        }
//
//        if (u == v) {
//            return u;
//        }
//
//        for (int i = LOG - 1; i >= 0; i--) {
//            if (up[u][i] != up[v][i]) {
//                u = up[u][i];
//                v = up[v][i];
//            }
//        }
//
//        return up[u][0];
//    }
//
//    public static void main(String[] args) {
//        try {
//            PowerGrid pg = new PowerGrid("input.txt");
//            ArrayList<PowerLine> critical = pg.criticalLines();
//            System.out.println("Critical Lines:");
//            for (PowerLine pl : critical) {
//                System.out.println(pl.cityA + " - " + pl.cityB);
//            }
//
//            pg.preprocessImportantLines(); // must be called before numImportantLines()
//
//            System.out.println("Important Lines between Delhi and Chennai: " + pg.numImportantLines("Delhi", "Chennai"));
//            System.out.println("Important Lines between Mumbai and Kolkata: " + pg.numImportantLines("Mumbai", "Kolkata"));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//}

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

class PowerLine {
    String cityA;
    String cityB;

    public PowerLine(String cityA, String cityB) {
        this.cityA = cityA;
        this.cityB = cityB;
    }
}

public class PowerGrid {

    int numCities;
    int numLines;
    String[] cityNames;
    PowerLine[] powerLines;
    ArrayList<ArrayList<Integer>> adjList = new ArrayList<>();
    HashMap<String, Integer> cityToIndex = new HashMap<>();
    ArrayList<ArrayList<Integer>> bridgeTree = new ArrayList<>();
    int[][] up;
    int[] depth;
    int LOG = 20;

    // Additional structures
    int[] component; // maps city -> component
    int componentCount;

    public PowerGrid(String filename) throws Exception {
        File file = new File(filename);
        BufferedReader br = new BufferedReader(new FileReader(file));
        numCities = Integer.parseInt(br.readLine().trim());
        numLines = Integer.parseInt(br.readLine().trim());
        cityNames = new String[numCities];
        for (int i = 0; i < numCities; i++) {
            cityNames[i] = br.readLine().trim();
            cityToIndex.put(cityNames[i], i);
            adjList.add(new ArrayList<>());
        }

        powerLines = new PowerLine[numLines];
        for (int i = 0; i < numLines; i++) {
            String[] line = br.readLine().split(" ");
            int u = cityToIndex.get(line[0]);
            int v = cityToIndex.get(line[1]);
            powerLines[i] = new PowerLine(line[0], line[1]);
            adjList.get(u).add(v);
            adjList.get(v).add(u);
        }
    }

    // ----------- Bridge Finding -----------
    ArrayList<int[]> bridges;

    public ArrayList<PowerLine> criticalLines() {
        bridges = new ArrayList<>();
        int[] time = new int[numCities];
        Arrays.fill(time, -1);
        int[] low = new int[numCities];
        Arrays.fill(low, -1);
        boolean[] visited = new boolean[numCities];
        int[] parent = new int[numCities];
        Arrays.fill(parent, -1);
        int[] timer = {0};

        for (int i = 0; i < numCities; i++) {
            if (!visited[i]) {
                dfsBridges(i, time, low, visited, parent, timer);
            }
        }

        ArrayList<PowerLine> ans = new ArrayList<>();
        for (int[] bridge : bridges) {
            ans.add(new PowerLine(cityNames[bridge[0]], cityNames[bridge[1]]));
        }
        return ans;
    }

    private void dfsBridges(int u, int[] time, int[] low, boolean[] visited, int[] parent, int[] timer) {
        visited[u] = true;
        time[u] = low[u] = timer[0]++;

        for (int v : adjList.get(u)) {
            if (v == parent[u]) continue;
            if (visited[v]) {
                low[u] = Math.min(low[u], time[v]);
            } else {
                parent[v] = u;
                dfsBridges(v, time, low, visited, parent, timer);
                low[u] = Math.min(low[u], low[v]);

                if (low[v] > time[u]) {
                    bridges.add(new int[]{u, v});
                }
            }
        }
    }

    // ----------- DSU for Component Grouping -----------
    class DSU {
        int[] parent;
        public DSU(int n) {
            parent = new int[n];
            for (int i = 0; i < n; i++) parent[i] = i;
        }

        int find(int x) {
            if (parent[x] != x) parent[x] = find(parent[x]);
            return parent[x];
        }

        void union(int x, int y) {
            int px = find(x);
            int py = find(y);
            if (px != py) parent[py] = px;
        }
    }

    public void preprocessImportantLines() {
        // Step 1: Find bridges
        ArrayList<int[]> bridgeEdges = new ArrayList<>();
        Set<Long> bridgeSet = new HashSet<>();

        for (int[] b : bridges) {
            bridgeSet.add(1L * b[0] * numCities + b[1]);
            bridgeSet.add(1L * b[1] * numCities + b[0]);
        }

        // Step 2: DSU merge all non-bridge edges
        DSU dsu = new DSU(numCities);
        for (int u = 0; u < numCities; u++) {
            for (int v : adjList.get(u)) {
                if (!bridgeSet.contains(1L * u * numCities + v)) {
                    dsu.union(u, v);
                }
            }
        }

        // Step 3: Assign component IDs
        component = new int[numCities];
        Map<Integer, Integer> compMap = new HashMap<>();
        componentCount = 0;
        for (int i = 0; i < numCities; i++) {
            int root = dsu.find(i);
            if (!compMap.containsKey(root)) {
                compMap.put(root, componentCount++);
            }
            component[i] = compMap.get(root);
        }

        // Step 4: Build Bridge Tree
        bridgeTree = new ArrayList<>();
        for (int i = 0; i < componentCount; i++) {
            bridgeTree.add(new ArrayList<>());
        }

        for (int[] b : bridges) {
            int uComp = component[b[0]];
            int vComp = component[b[1]];
            bridgeTree.get(uComp).add(vComp);
            bridgeTree.get(vComp).add(uComp);
        }

        // Step 5: LCA Preprocessing on bridge tree
        depth = new int[componentCount];
        up = new int[componentCount][LOG];
        dfsLCA(0, 0);
    }

    private void dfsLCA(int u, int parent) {
        up[u][0] = parent;
        for (int i = 1; i < LOG; i++) {
            up[u][i] = up[up[u][i - 1]][i - 1];
        }

        for (int v : bridgeTree.get(u)) {
            if (v != parent) {
                depth[v] = depth[u] + 1;
                dfsLCA(v, u);
            }
        }
    }

    private int lca(int u, int v) {
        if (depth[u] < depth[v]) {
            int temp = u;
            u = v;
            v = temp;
        }

        for (int i = LOG - 1; i >= 0; i--) {
            if (depth[u] - (1 << i) >= depth[v]) {
                u = up[u][i];
            }
        }

        if (u == v) return u;

        for (int i = LOG - 1; i >= 0; i--) {
            if (up[u][i] != up[v][i]) {
                u = up[u][i];
                v = up[v][i];
            }
        }

        return up[u][0];
    }

    public int numImportantLines(String cityA, String cityB) {
        int u = cityToIndex.get(cityA);
        int v = cityToIndex.get(cityB);
        int cu = component[u];
        int cv = component[v];
        int lcaNode = lca(cu, cv);
        return depth[cu] + depth[cv] - 2 * depth[lcaNode];
    }

    // ----------- Main -----------
    public static void main(String[] args) {
        try {
            PowerGrid pg = new PowerGrid("input2.txt");
            ArrayList<PowerLine> critical = pg.criticalLines();
            System.out.println("Critical Lines:");
            for (PowerLine pl : critical) {
                System.out.println(pl.cityA + " - " + pl.cityB);
            }

            pg.preprocessImportantLines(); // must be called before numImportantLines()


            System.out.println("Important Lines between D and E: " + pg.numImportantLines("D", "E"));
            System.out.println("Important Lines between K and N: " + pg.numImportantLines("K", "N"));
            System.out.println("Important Lines between H and O: " + pg.numImportantLines("H", "O"));
            System.out.println("Important Lines between G and J: " + pg.numImportantLines("G", "J"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
