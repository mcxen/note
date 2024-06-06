package template;

import java.util.Arrays;
import java.util.Scanner;

/**
 * Dijkstra算法
 * 还可以用优先队列做，即退化版的A*算法
 */
public class Dijkstra {

    static int MAXV = 999;					//最大顶点数，由具体题目决定
    static int M = 999999;					//不能用Integer.MAX_VALUE,否则做加法后可能会溢出

    static int n,m,s;  						//n为顶点数，m为边数，s为起始顶点编号
    static int[][] G=new int[MAXV][MAXV];  	//一个有向图的邻接矩阵，
//与《算法笔记》中另用数组d[]表示起点到各点的最短路径不同，
//本模板直接在原来的邻接矩阵中修改G[s][i]，执行完dijkstra算法后，G[s][i]即为起点到各点的最短路径
    static int[] v=new int[MAXV];    		//标记数组，v[i]=1表示i点已访问，初值均为0
    static String[] path=new String[MAXV]; 	//存放从start到其他各点的最短路径的字符串表示
    //除了距离之外的第二标尺
//	static int cost[][]=new int[MAXV][MAXV]; cost代表新增的边权
//	static int c[]=new int[MAXV];            c代表从起点到各点的花费

//	static int resource[]=new int[MAXV];     resource代表新增的点权
//	static int r[]=new int[MAXV];		     r代表从起点到各点获得的资源

//	static int num[]=new int[MAXV];          num代表到各点的最短路径条数。

    public static void main(String[] args) {
        Scanner sc=new Scanner(System.in);
        n=sc.nextInt();
        m=sc.nextInt();
        s=sc.nextInt();

        for(int i=0;i<n;i++) {
            Arrays.fill(G[i], M);
            G[i][i]=0;
            path[i]=s+"";
        }

//      Arrays.fill(c, M);
//      c[s]=0;
//      r[s]=resource[s];
//      num[s]=1;

        for(int i=0;i<m;i++) {
            int v1=sc.nextInt();
            int v2=sc.nextInt();
            int weight=sc.nextInt();
//			int co=sc.nextInt();
            G[v1][v2]=weight;
//			G[v2][v1]=weight;  若为无向图，则加上这一句
//			cost[v1][v2]=co;
//			cost[v2][v1]=co;
        }

        dijkstra(s);

        for(int i=0;i<n;i++) {
            System.out.println("从"+s+"出发到"+i+"的最短路径为："+path[i]);
        }

        for(int i=0;i<n;i++) {
            System.out.print(G[s][i]+" ");
        }

        sc.close();
    }


    //dijkstra算法
    public static void dijkstra(int s){
        //接受一个起点编号s（从0开始编号）
        for(int i=0;i<n;i++)  //要加入n个顶点
        {
            //1、选出一个距离初始顶点s最近的未标记顶点  u、从s到u的距离min
            int u=-1;
            int min=M;
            for(int j=0;j<n;j++) {
                if(v[j]==0&&G[s][j]<min) {
                    min=G[s][j];
                    u=j;
                }
            }
            //2、选好u之后，看看u是否存在，若存在则更新状态
            //若剩下的顶点都和起点s不连通，则u=-1，返回
            if(u==-1) return ;
            //将第i个选出的顶点u标记为已求出最短路径
            v[u]=1;
            //3、以u为中间点，修正从【起点s】到未访问各点的距离
            for(int j=0;j<n;j++) {
                if(v[j]==0) {
                    if(G[s][u]+G[u][j]<=G[s][j]) {	//要求path时，必须写成“<=”，否则可以只写“<”
                        G[s][j]=G[s][u]+G[u][j];
                        //这里进行其他某些操作
                        path[j]=path[u]+" "+j;
//						c[j]=c[u]+cost[u][v];
//						r[j]=r[u]+resource[j];
//						num[j]=num[u];
                    }
//若上个if条件中符号为"<",且存在第二标尺，那么还要另外考虑第一标尺相同时的情况
//					else if(G[s][u]+G[u][j]==G[s][j]) {
//					    if(当前的第二标尺优于之前){
//						path[j]=path[u]+" "+j;
//				}
//						c[j]=Math.min(c[j],c[u]+cost[u][v]);
//						r[j]=Math.max(r[j],r[u]+resource[j]);
//						num[j]+=num[u];
//					}
                }
            }
        }
    }
}
