import java.util.*;


// Here the Drone class defines a drone with its starting and ending coordinates, start time, and a list of its path coordinates.
class Drone {
    int startRow;
    int startCol;
    int endRow;
    int endCol;
    int startTime;
    List<int[]> path;

    // constructor drone has been called
    Drone(int startRow, int startCol, int endRow, int endCol, int startTime) {
        this.startRow = startRow;
        this.startCol = startCol;
        this.endRow = endRow;
        this.endCol = endCol;
        this.startTime = startTime;
        this.path = new ArrayList<>();
    }
    // here the move method of the Drone class takes in a row and column coordinate and adds it to the path list.
    void move(int row, int col) {
        this.path.add(new int[] {row, col});
    }
}

public class DroneNavigation {

    // here the DIRS array stores the possible directions the drone can move in.
    private static final int[][] DIRS = {{-1, 0}, {0, -1}, {1, 0}, {0, 1}};

    // The main method reads input from the user to create a list of drones,
    // puts them in a timeline map based on their start time, and initializes a grid of -1 values.
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter the number of drones: ");
        int n = sc.nextInt();
        int endRow=0, endCol=0;
        List<Drone> drones = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            System.out.print("Enter the starting row and column for drone " + i + ": ");
            int startRow = sc.nextInt();
            int startCol = sc.nextInt();
            System.out.print("Enter the ending row and column for drone " + i + ": ");
             endRow = sc.nextInt();
             endCol = sc.nextInt();
            System.out.print("Enter the start time for drone " + i + ": ");
            int startTime = sc.nextInt();
            drones.add(new Drone(startRow, startCol, endRow, endCol, startTime));
        }

        Map<Integer, List<Drone>> timeline = new TreeMap<>();
        for (Drone drone : drones) {
            timeline.putIfAbsent(drone.startTime, new ArrayList<>());
            timeline.get(drone.startTime).add(drone);
        }

        int[][] grid = new int[20][20];
        for (int i = 0; i < 20; i++) {
            Arrays.fill(grid[i], -1);
        }

        for (int time : timeline.keySet()) {
            for (Drone drone : timeline.get(time)) {
                //  performs BFS on each drone to get its path.
                bfs(drone, grid);
            }
        }

        for (int i = 0 ; i < drones.size(); i++) {
            // printing the path of the respective drones to the console
            System.out.println("Path for drone " + (i + 1) + ": " + Arrays.deepToString(drones.get(i).path.toArray()));
        }
    }

    // The bfs method takes in a drone and the grid and performs a breadth-first search to find a path from the drone's starting
    // coordinates to its ending coordinates. It uses a queue to store coordinates to visit, a map to store previous coordinates,
    // and the constructPath method to construct the drone's path.
    private static void bfs(Drone drone, int[][] grid) {
        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[]{drone.startRow, drone.startCol});
        Map<String, int[]> prev = new HashMap<>();
        prev.put(drone.startRow + "," + drone.startCol, null);

        while (!queue.isEmpty()) {
            int[] curr = queue.poll();

            if (curr[0] == drone.endRow && curr[1] == drone.endCol) {
                drone.move(curr[0], curr[1]);
                constructPath(prev, drone, curr[0], curr[1]);
                break;
            }

            for (int[] dir : DIRS) {
                int nextRow = curr[0] + dir[0];
                int nextCol = curr[1] + dir[1];

                if (nextRow < 0 || nextRow >= 20 || nextCol < 0 || nextCol >= 20) {
                    continue;
                }

                if (grid[nextRow][nextCol] != -1 && grid[nextRow][nextCol] <= drone.startTime) {
                    continue;
                }

                if (prev.containsKey(nextRow + "," + nextCol)) {
                    continue;
                }

                queue.offer(new int[]{nextRow, nextCol});
                prev.put(nextRow + "," + nextCol, curr);
            }
        }
    }

    // The constructPath method takes in the prev map, the drone, and the final row and column coordinates,
    // and constructs the drone's path using a stack to backtrack from the final coordinates to the starting coordinates.
    private static void constructPath(Map<String, int[]> prev, Drone drone, int row, int col) {
        Stack<int[]> stack = new Stack<>();
        while (row != drone.startRow || col != drone.startCol) {
            stack.push(new int[]{row, col});
            int[] curr = prev.get(row + "," + col);
            row = curr[0];
            col = curr[1];
        }
        stack.push(new int[]{row, col});
        while (!stack.isEmpty()) {
            drone.move(stack.peek()[0], stack.pop()[1]);
        }
    }


}
