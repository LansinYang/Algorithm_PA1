import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class  Driver{
    public static String filename;
    public static boolean testGS_c;
    public static boolean testGS_i;
    public static boolean testBF;
    public static void main(String[] args) throws Exception {
        parseArgs(args);

        if (testBF) {
            Program1 program = new Program1();
            Matching problem = parseMatchingProblemWithExample(filename);
            boolean isStable = program.isStableMatching(problem);
            if (isStable)
                System.out.println("Matching provided is stable");
            else
                System.out.println("Matching provided is not stable");
            testRun(problem);
        } else {
            Matching problem = parseMatchingProblem(filename);
            testRun(problem);
        }
    }

    private static void usage() {
        System.err.println("usage: java Driver [-gc] [-gi] [-bf] <filename>");
        System.err.println("\t-gc\tTest Gale-Shapley university optimal implementation");
        System.err.println("\t-gi\tTest Gale-Shapley student optimal implementation");
        System.err.println("\t-bf\tCheck if input matching is stable");
        System.exit(1);
    }

    public static void parseArgs(String[] args) {
        if (args.length == 0) {
            usage();
        }

        filename = "";
        testGS_c = false;
        testGS_i = false;
        testBF = false;
        boolean flagsPresent = false;

        for (String s : args) {
            if (s.equals("-gc")) {
                flagsPresent = true;
                testGS_c = true;
            } else if (s.equals("-gi")) {
                flagsPresent = true;
                testGS_i = true;
            } else if (s.equals("-bf")) {
                flagsPresent = true;
                testBF = true;
            } else if (!s.startsWith("-")) {
                filename = s;
            } else {
                System.err.printf("Unknown option: %s\n", s);
                usage();
            }
        }
        if (!flagsPresent) {
            testGS_c = true;
            testGS_i = true;
        }
    }

    public static Matching parseMatchingProblemWithExample(String inputFile) throws Exception {
        int m = 0;
        int n = 0;
        ArrayList<ArrayList<Integer>> universityPrefs, studentPrefs;
        ArrayList<Integer> universityPositions;
        ArrayList<Integer> exampleMatching;
        Scanner sc = new Scanner(new File(inputFile));
        String[] inputSizes = sc.nextLine().split(" ");

        m = Integer.parseInt(inputSizes[0]);
        n = Integer.parseInt(inputSizes[1]);
        universityPositions = readPositionsList(sc, m);
        universityPrefs = readPreferenceLists(sc, m);
        studentPrefs = readPreferenceLists(sc, n);

        Matching problem = new Matching(m, n, universityPrefs, studentPrefs, universityPositions);
        exampleMatching = readPositionsList(sc, n);
        problem.setStudentMatching(exampleMatching);

        return problem;
    }

    public static Matching parseMatchingProblem(String inputFile) throws Exception {
        int m = 0;
        int n = 0;
        ArrayList<ArrayList<Integer>> universityPrefs, studentPrefs;
        ArrayList<Integer> universityPositions;

        Scanner sc = new Scanner(new File(inputFile));
        String[] inputSizes = sc.nextLine().split(" ");

        m = Integer.parseInt(inputSizes[0]);
        n = Integer.parseInt(inputSizes[1]);
        universityPositions = readPositionsList(sc, m);
        universityPrefs = readPreferenceLists(sc, m);
        studentPrefs = readPreferenceLists(sc, n);

        Matching problem = new Matching(m, n, universityPrefs, studentPrefs, universityPositions);

        return problem;
    }

    private static ArrayList<Integer> readPositionsList(Scanner sc, int m) {
        ArrayList<Integer> universityPositions = new ArrayList<Integer>(0);

        String[] positions = sc.nextLine().split(" ");
        for (int i = 0; i < m; i++) {
            universityPositions.add(Integer.parseInt(positions[i]));
        }

        return universityPositions;
    }

    private static ArrayList<ArrayList<Integer>> readPreferenceLists(Scanner sc, int m) {
        ArrayList<ArrayList<Integer>> preferenceLists;
        preferenceLists = new ArrayList<ArrayList<Integer>>(0);

        for (int i = 0; i < m; i++) {
            String line = sc.nextLine();
            String[] preferences = line.split(" ");
            ArrayList<Integer> preferenceList = new ArrayList<Integer>(0);
            for (Integer j = 0; j < preferences.length; j++) {
                preferenceList.add(Integer.parseInt(preferences[j]));
            }
            preferenceLists.add(preferenceList);
        }

        return preferenceLists;
    }

    public static void testRun(Matching problem) {
        Program1 program = new Program1();
        boolean isStable;

        if (testGS_c) {
            Matching GSMatching = program.stableMatchingGaleShapley_universityoptimal(problem);
            System.out.println(GSMatching);
            isStable = program.isStableMatching(GSMatching);
            System.out.printf("%s: stable? %s\n", "Gale-Shapley University Optimal", isStable);
            System.out.println();
        }

        if (testGS_i) {
            Matching GSMatching = program.stableMatchingGaleShapley_studentoptimal(problem);
            System.out.println(GSMatching);
            isStable = program.isStableMatching(GSMatching);
            System.out.printf("%s: stable? %s\n", "Gale-Shapley Student Optimal", isStable);
            System.out.println();
        }
    }
}