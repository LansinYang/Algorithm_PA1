/*
 * Lanxin Yang
 * EID: <your EID>
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * Your solution goes in this class.
 *
 * Please do not modify the other files we have provided for you, as we will use
 * our own versions of those files when grading your project. You are
 * responsible for ensuring that your solution works with the original version
 * of all the other files we have provided for you.
 *
 * That said, please feel free to add additional files and classes to your
 * solution, as you see fit. We will use ALL of your additional files when
 * grading your solution. However, do not add extra import statements to this file.
 */
public class Program1 extends AbstractProgram1 {
    public int[] uniHighestRank;
    public int[] currStuCount;
    ArrayList<Integer> idelPos;
    int[][] uniPrefRverse;
    int[][] stuPrefRverse;
    ArrayList<ArrayList<Integer>> uniCurrStu;
    int studentCount;
    /**
     * Determines whether a candidate Matching represents a solution to the stable matching problem.
     * Study the description of a Matching in the project documentation to help you with this.
     */
    @Override
    public boolean isStableMatching(Matching problem) {
        ArrayList<Integer> stuResult = problem.getStudentMatching();
        ArrayList<ArrayList<Integer>> uniResult = new ArrayList<ArrayList<Integer>>();
        ArrayList<ArrayList<Integer>> stuPref = problem.getStudentPreference();
        for (int i = 0; i < problem.getUniversityCount(); i++){
            uniResult.add(new ArrayList<Integer>());
        }
        //int[] uniResult = new int[problem.getUniversityCount()];
        int count = 0;
        while(count < problem.getUniversityCount()){
            for(int i = 0; i < stuResult.size(); i ++){
                if(stuResult.get(i) == count){
                    uniResult.get(count).add(i);
                }
            }
            count++;
        }
        //collect admitted students list for each university
        
        for(int i = 0; i < problem.getStudentCount(); i++){
            if(stuResult.get(i) != -1){
                //student i is admitted by some university
                for(int j : stuPref.get(i) ){
                    //list of university that student i prefer than current univerisity 
                    if(j != stuResult.get(i)){
                        //univeristy j prefer than current match
                        int limit = uniHighestRank[j];
                        //the highest rank of univeristy j's admited student
                        if(uniPrefRverse[j][i] < limit){
                            //university j prefer i than the current student
                            System.out.println("university "+j+ " prefer "+i+" than the current student");
                            return false;
                        }
                    }else{
                        break;
                    }
                }
            }else{
                for(int j : stuPref.get(i)){
                    int limit = uniHighestRank[j];
                    //the highest rank of univeristy j's admited student
                    if(uniPrefRverse[j][i] < limit){
                        //university j prefer i than the current student
                        return false;
                    } 
                }
            }
        }
        return true;
    }

    /**
     * Determines a solution to the stable matching problem from the given input set. Study the
     * project description to understand the variables which represent the input to your solution.
     *
     * @return A stable Matching.
     */
    @Override
    public Matching stableMatchingGaleShapley_universityoptimal(Matching problem) {
        studentCount = problem.getStudentCount();
        int[] admit = new int[problem.getStudentCount()];
        Arrays.fill(admit,-1);

        uniHighestRank = new int[problem.getUniversityCount()];
        Arrays.fill(uniHighestRank,-1);

        stuPrefRverse = new int[problem.getStudentCount()][problem.getUniversityCount()];
        for(int i = 0; i < problem.getStudentCount();i++){
            for(int ii = 0; ii < problem.getUniversityCount();ii++){
                stuPrefRverse[i][problem.getStudentPreference().get(i).get(ii)]= ii;
            }
        }

        uniPrefRverse = new int[problem.getUniversityCount()][problem.getStudentCount()];
        for(int i = 0; i < problem.getUniversityCount();i++){
            for(int ii = 0; ii < problem.getStudentCount();ii++){
                uniPrefRverse[i][problem.getUniversityPreference().get(i).get(ii)]= ii;
            }
        }

        uniCurrStu = new ArrayList<ArrayList<Integer>>();
        for(int i = 0; i < problem.getUniversityCount(); i++){
            uniCurrStu.add(new ArrayList<Integer>());
        }
        
        ArrayList<ArrayList<Integer>> test = problem.getUniversityPreference();

        ArrayList<ArrayList<Integer>> uniPref = new ArrayList<>(); 
        for(ArrayList<Integer> i : test){
            ArrayList<Integer> perUni = new ArrayList<Integer>();
            for(int j = 0; j < i.size(); j++){
                perUni.add(i.get(j));
            }
            uniPref.add(perUni);
        }

        idelPos = problem.getUniversityPositions();
        
        int num = 0;
        
        while(num < problem.getUniversityCount()){  
            num = 0;
            for(int i = 0; i < problem.getUniversityCount(); i++){
                if(idelPos.get(i)==0) num++;
                else{    
                    admit = gcApplication(uniPref, admit, i);
                }  
            }
            
        }

        ArrayList<Integer> sol = new ArrayList<Integer>();
        for(int i = 0; i < problem.getStudentCount();i++){
            
            sol.add(admit[i]);
        }
        problem.setStudentMatching(sol);

        return problem;
    }

    public int[] gcApplication(ArrayList<ArrayList<Integer>> uniPref, int[] admit, int i){
        int j = 0;
        if(uniHighestRank[i] > 0){
            j = uniHighestRank[i];
        }
        ArrayList<Integer> currUniPref = uniPref.get(i);
        while(idelPos.get(i)>0 &&j < currUniPref.size()){
            int stu = currUniPref.get(j);
            int stuAdmitted = admit[stu];
            ArrayList<Integer> currStu = uniCurrStu.get(i);
            if(stuAdmitted > -1){
                //student was admit by some univeristy
                int uniAdmitted = stuPrefRverse[stu][stuAdmitted];
                if(stuPrefRverse[stu][i] < stuPrefRverse[stu][stuAdmitted]){
                    admit[stu] = i;
                    currStu.add(stu);
                    uniCurrStu.get(uniAdmitted).remove((Integer)stu);
                    //remove student from the former admitted university
                    idelPos.set(i, idelPos.get(i)-1);
                    idelPos.set(uniAdmitted, idelPos.get(uniAdmitted)+1);
                    uniHighestRank[i] = j;
                }
            }else if(stuAdmitted != i && stuAdmitted == -1){
                admit[stu] = i;
                currStu.add(stu);
                idelPos.set(i, idelPos.get(i)-1);
                uniHighestRank[i] = j;
            }
            j++;
        }
        
        return admit;
    } 

    /**
     * Determines a solution to the stable matching problem from the given input set. Study the
     * project description to understand the variables which represent the input to your solution.
     *
     * @return A stable Matching.
     */
    @Override
    public Matching stableMatchingGaleShapley_studentoptimal(Matching problem) {
        int[] admit = new int[problem.getStudentCount()];
        Arrays.fill(admit,-1);

        uniPrefRverse = new int[problem.getUniversityCount()][problem.getStudentCount()];
        for(int i = 0; i < problem.getUniversityCount();i++){
            for(int ii = 0; ii < problem.getStudentCount();ii++){
                uniPrefRverse[i][problem.getUniversityPreference().get(i).get(ii)]= ii;
            }
        }

        uniHighestRank = new int[problem.getUniversityCount()];
        Arrays.fill(uniHighestRank,-1);

        uniCurrStu = new ArrayList<ArrayList<Integer>>();
        for(int i = 0; i < problem.getUniversityCount(); i++){
            uniCurrStu.add(new ArrayList<Integer>());
        }
        
        ArrayList<ArrayList<Integer>> test = problem.getStudentPreference();

        ArrayList<ArrayList<Integer>> stuPref = new ArrayList<>(); 
        for(ArrayList<Integer> i : test){
            ArrayList<Integer> perStu = new ArrayList<Integer>();
            for(int j = 0; j < i.size(); j++){
                perStu.add(i.get(j));
            }
            stuPref.add(perStu);
        }

        idelPos = problem.getUniversityPositions();

        int num = 0;
        int f = problem.totalUniversityPositions();
        
        while(num < f||!checkAllTries(stuPref, admit)){  
            num = 0;
            for(int i = 0; i < problem.getStudentCount(); i++){
                if(admit[i] > -1) num++;
                else{    
                    admit = application(stuPref, admit, i);
                }  
            }
            
        }
        ArrayList<Integer> sol = new ArrayList<Integer>();
        for(int i = 0; i < problem.getStudentCount();i++){
            sol.add(admit[i]);
        }
        problem.setStudentMatching(sol);

        return problem;
    }
    public int[] application(ArrayList<ArrayList<Integer>> stuPref, int[] admit, int i){
        ArrayList<Integer> currStuPref = stuPref.get(i);
        if(currStuPref.size() != 0){ 
            int uni = currStuPref.get(0);
            int highRank = uniHighestRank[uni];
            int availablePos = idelPos.get(uni);
            ArrayList<Integer> currStu = uniCurrStu.get(uni);
            if(availablePos > 0){
                admit[i] = uni;
                currStu.add(i);
                sortStu(currStu,uni);
                availablePos--;
                idelPos.set(uni, availablePos);
                if(uniPrefRverse[uni][i] > highRank){
                    uniHighestRank[uni] = uniPrefRverse[uni][i];
                }
            }else{
                if(uniPrefRverse[uni][i] < highRank){
                    admit[i] = uni;
                    currStu.add(i);
                    
                    sortStu(uniCurrStu.get(uni),uni);
                    int rej = currStu.remove(currStu.size()-1);
                    stuPref.get(rej).remove(0);
                    admit[rej] = -1;
                    int currLast = currStu.get(currStu.size()-1);
                    uniHighestRank[uni] = uniPrefRverse[uni][currLast];
                }
                else{
                    stuPref.get(i).remove(0);
                }
            }
        }
        
        return admit;
    }
    public void sortStu(ArrayList<Integer> uniCurrStu,int uni){
        if(uniCurrStu.size() > 1){
            int curr = uniCurrStu.size() - 1;
            while(curr > 0 && uniPrefRverse[uni][uniCurrStu.get(curr)] < uniPrefRverse[uni][uniCurrStu.get(curr-1)]){
                int temp = uniCurrStu.get(curr-1);
                uniCurrStu.set(curr-1,uniCurrStu.get(curr));
                uniCurrStu.set(curr, temp);
                curr--;
                
            }
        }else{
            return ; 
        }
        
    }
    public boolean checkAllTries(ArrayList<ArrayList<Integer>> stuPref, int[] admit){
        for(int i = 0; i < stuPref.size();i++){
            if(admit[i] == -1 && stuPref.get(i).size() > 0){
                return false;
            }
        }
        return true;
    }
}