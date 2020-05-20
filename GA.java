import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class GA {
    public int genom_length;
    public int pop_size;
    public int max_no_of_itterations;
    public int knapsack_size;
    public List<String> population;
    public List<Integer> weights;
    public List<Integer> values;
    public int best_fitness;
    public String best_knapsack;
    public int total_fitness;
    public List<Integer> fitness;

    public void generateInitialPop() {
        best_fitness = -1;

        Random rand = new Random(System.currentTimeMillis());
        population = new ArrayList<String>();
        for (int i = 0; i < pop_size; i++) {
            String temp = "";
            for (int j = 0; j < genom_length; j++) {
                if (ThreadLocalRandom.current().nextDouble(0, 1) <= 0.5)
                    temp += '0';
                else temp += '1';
            }
            population.add(temp);
        }
    }

    public int calculateFitness(int genom_num) {
        int total_value = 0;
        int total_weight = 0;
        for (int i = 0; i < genom_length; i++)
            if (population.get(genom_num).charAt(i) == '1') {
                total_value += values.get(i);
                total_weight += weights.get(i);
            }
        if (total_weight <= knapsack_size) {
            if (total_value > best_fitness) {
                best_fitness = total_value;
                best_knapsack = population.get(genom_num);
            }
            return total_value;
        }
        return 0;
    }

    public String selection(){
        double random_fitness = ThreadLocalRandom.current().nextDouble(0, 1) *  total_fitness;
        int acumulative_fitness = 0;
        List<Integer> temp_fitness = new ArrayList<>();
        for (int i = 0; i < pop_size; i++)
            temp_fitness.add(calculateFitness(i));
        Collections.sort(temp_fitness);

        for(int i = 0; i < pop_size; i++){
            acumulative_fitness += temp_fitness.get(i);
            if(acumulative_fitness >= random_fitness)
                return population.get(fitness.indexOf(temp_fitness.get(i)));
        }
        return null;
    }

    public String[] crossOver(String chromo1,String chromo2,int point){

        String[] newChromos = new String[2];
        newChromos[0] = "";
        newChromos[1] = "";

        // *** newChromo1 Generation ****
        newChromos[0] += chromo1.substring(0,point);
        newChromos[0] += chromo2.substring(point);


        // **** newChromo2 Generation *****
        newChromos[1] += chromo2.substring(0,point);
        newChromos[1] += chromo1.substring(point);


        return newChromos;
    }

    public String mutation(String chromo1, int point){
        if (chromo1.charAt(point-1) == '0')
            chromo1 = chromo1.substring(0,point-1) + '1' + chromo1.substring(point);
        else
            chromo1 = chromo1.substring(0,point-1) + '0' + chromo1.substring(point);

        return chromo1;
    }

    public static void main(String args[]) {
        File file = new File("/Users/tokamagdy/Downloads/input_example.txt");
        try {
            Scanner sc = new Scanner(file);
            int no_of_tests = sc.nextInt();

            for (int j = 0; j < no_of_tests; j++) {
                GA problem = new GA();
                problem.pop_size = 32;
                problem.max_no_of_itterations = 200000;
                problem.genom_length = sc.nextInt();
                problem.knapsack_size = sc.nextInt();
                problem.values = new ArrayList<>();
                problem.weights = new ArrayList<>();

                for (int i = 0; i < problem.genom_length; i++) {
                    problem.weights.add(sc.nextInt());
                    problem.values.add(sc.nextInt());
                }

                problem.generateInitialPop();

                for (int k = 0; k < problem.max_no_of_itterations; k++) {
                    problem.fitness = new ArrayList<>();
                    problem.total_fitness = 0;
                    for (int i = 0; i < problem.pop_size; i++) {
                        int temp = problem.calculateFitness(i);
                        problem.fitness.add(temp);
                        problem.total_fitness += temp;
                    }

                    String first = problem.selection();
                    String second = problem.selection();

                    int first_index = problem.population.indexOf(first);
                    int second_index = problem.population.indexOf(second);

                    String[] arr = problem.crossOver(first, second, problem.genom_length / 2);
                    problem.population.set(first_index, arr[0]);
                    problem.population.set(second_index, arr[1]);

                    for (int i = 0; i < problem.pop_size; i++)
                        problem.population.set(i, problem.mutation(problem.population.get(i), problem.genom_length / 2));
                }

                System.out.println("case " + (j+1) + ':');
                System.out.println("fitness:" + problem.best_fitness);
                //System.out.println("items included:");
                //for (int i = 0; i < problem.genom_length; i++)
                //    if (problem.best_knapsack.charAt(i) == '1')
                //        System.out.println("(" + problem.weights.get(i) + ", " + problem.values.get(i) + ')');
                //System.out.println("-----------------------------------------");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
