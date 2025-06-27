import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

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
}
