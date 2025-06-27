import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        try {
            PowerGrid pg = new PowerGrid("input1.txt");
            ArrayList<PowerLine> critical = pg.criticalLines();
            System.out.println("Critical Lines:");
            for (PowerLine pl : critical) {
                System.out.println(pl.cityA + " - " + pl.cityB);
            }

            pg.preprocessImportantLines(); // must be called before numImportantLines()

            for (int i = 0; i < pg.numCities; i++) {
                for (int j = 0; j < pg.numCities && i != j; j++)
                    System.out.println("Important Lines between " + pg.cityNames[i] + " and " + pg.cityNames[j] + " : " + pg.numImportantLines(pg.cityNames[i], pg.cityNames[j]));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
