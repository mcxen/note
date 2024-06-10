package template;


import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

/**
 * 广搜的一些板子
 */
public class BFS {

    /**
     * BFS + 状态压缩
     * lc 847 访问所有节点的最短路径
     */
    class State {
        int INF = 0x3f3f3f3f;
        public int shortestPathLength(int[][] graph) {
            int n = graph.length;
            int mask = 1<< n;
            int [][] dist = new int[mask][n];
            Deque<int[]> deque = new ArrayDeque<>();
            for (int i = 0; i < dist.length; i++) {
                Arrays.fill(dist[i], INF);
            }
            for (int i = 0; i < n; i++) {
                dist[1 << i][i] = 0;
                deque.addLast(new int[]{1 << i, i});
            }
            while (!deque.isEmpty()) {
                int [] poll = deque.pollFirst();
                int state = poll[0];
                int i = poll[1];
                if (state == mask - 1) {
                    return dist[state][i];
                }
                for (int j = 0; j < graph[i].length; j++) {
                    int u = graph[i][j];
                    if (dist[state | (1 << u)][u] == INF) {
                        dist[state | (1 << u)][u] = dist[state][i] + 1;
                        deque.addLast(new int[]{state | (1 << u), u});
                    }
                }

            }
            return -1;
        }
    }

}
