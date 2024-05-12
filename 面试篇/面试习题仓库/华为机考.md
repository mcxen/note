

### 1.



AK

```java
public void Solution (String[] args) {
    // please define the JAVA input here. For example: Scanner s = new Scanner(System.in);
    // please finish the function body here.
    // please define the JAVA output here. For example: System.out.println(s.nextInt());
    Scanner in = new Scanner(System.in);
    LinkedList<Integer> list = new LinkedList<>();
    while(in.hasNextInt()){
        list.add(in.nextInt());
        //System.out.println(list.toString());
    }
    int n = list.size()-1;
    int target = list.get(list.size()-1);
    //System.out.println(n);
    int[] nums = new int[n];
    for(int i = 0;i<n;i++){
        nums[i] = list.get(i);
    }
    Arrays.sort(nums);
    //System.out.println(Arrays.toString(nums));
    StringBuffer sb = new StringBuffer();
    sb.append('S');
    int l = 0,r = n-1;
    while(l<r){
        int m = (l+r)/2;
        if(nums[m]==target){
            sb.append('Y');
            break;
        }else if(nums[m]>target){
            r = m-1;
            sb.append('L');
        }else{
            l = m+1;
            sb.append('R');
        }
        //System.out.println(nums[m]);
    }
    if(nums[l]==target){
        sb.append('Y');
    }
    else if(l>=r){
        sb.append('N');
    }
    System.out.println(sb.toString());
}
```



### 2.

AK

```java
class Player{
    public int no;
    public String ball;
    public int nums;
    public int length;
    List<Integer> error;

    public Player(int no, String ball, int nums, int length, List<Integer> error) {
        this.no = no;
        this.ball = ball;
        this.nums = nums;
        this.length = length;
        this.error = error;
    }
}
void second(){
    Scanner sc = new Scanner(System.in);
    String[] line = sc.nextLine().split(" ");
    int n = Integer.parseInt(line[0]);
    int m = Integer.parseInt(line[1]);
    line = sc.nextLine().split(" ");
    ArrayList<Player> players = new ArrayList<>();
    for (int i = 0; i < line.length; i++) {
        int no = i+1;
        String ball = line[i];
        int num = 0;
        ArrayList<Integer> error = new ArrayList<>();
        for (int j = 0; j < m; j++) {
            char c = ball.charAt(j);
            if (c=='1') num++;
            if (c=='0'){
                error.add(j);
            }
        }
        int index = 0;
        int length = 0;
        while (index<m){
            while (index<m&&ball.charAt(index)=='0'){
                index++;
            }
            int cur = 0;
            while (index<m&&ball.charAt(index)=='1'){
                cur++;
                index++;
            }
            length = Math.max(length,cur);
        }
        players.add(new Player(no,ball,num,length,error));
    }
    players.sort(new Comparator<Player>() {
        @Override
        public int compare(Player o1, Player o2) {
            if (o1.nums!=o2.nums){
                return o2.nums-o1.nums;
            }
            if (o1.length!=o2.length) return o2.length-o1.length;
            int len = o1.error.size();
            for (int i = 0; i < len; i++) {
                Integer i1 = o1.error.get(i);
                Integer i2 = o2.error.get(i);
                if (i1!=i2) return i2-i1;
            }
            return o1.no-o2.no;
        }
    });
    for (int i = 0; i < players.size(); i++) {
        System.out.print(players.get(i).no+" ");
    }
}
```



### 3.



90%

```java
static class Cycle{
    public List<Integer> cycle;
    public int v;
    public int max;
    public int minIndex;
    public Cycle(List<Integer> cycle){
        this.cycle = cycle;
    };
}
public static void main(String[] args) {
    // please define the JAVA input here. For example: Scanner s = new Scanner(System.in);
    // please finish the function body here.
    // please define the JAVA output here. For example: System.out.println(s.nextInt());
    Scanner in = new Scanner(System.in);
    int n = in.nextInt();
    int[] edges = new int[n];
    for(int i = 0;i<n;i++){
        edges[i] = in.nextInt();
        //System.out.println(Arrays.toString(match[i]));
    }
    //System.out.println(Arrays.toString(edges));
    List<Cycle> cycles = new ArrayList<>();
    Stack<Integer> stack = new Stack<>();
    int[] visited = new int[n];
    Arrays.fill(visited,-1);
    for(int node = 0;node<n;node++){
        if(visited[node]!=-1){
            continue;
        }
        stack.push(node);
        visited[node] = -2;
        int cur = edges[node];
        while(true){
            if(visited[cur]==-2) break;
            if(visited[cur]!=-1 &&visited[cur]!=-2) {
                cycles.get(visited[cur]).v+=stack.size();
                while(!stack.isEmpty()){
                    visited[stack.peek()] = visited[cur];
                    stack.pop();
                }
                break;
            }
            stack.push(cur);
            visited[cur]=-2;
            cur = edges[cur];
        }
        if(stack.isEmpty()) continue;
        List<Integer> cyclelist = new ArrayList<>();
        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;
        int minIndex = -1;
        while(!stack.isEmpty()&&stack.peek()!=cur){
            visited[stack.peek()] = cycles.size();
            max = Math.max(max,stack.peek());
            if(stack.peek()<min){
                min = stack.peek();
                minIndex = cyclelist.size();
            }
            cyclelist.add(stack.pop());
        }
        if(!stack.isEmpty()){
            visited[stack.peek()] = cycles.size();
            max = Math.max(max,stack.peek());
            if(stack.peek()<min){
                min = stack.peek();
                minIndex = cyclelist.size();
            }
            cyclelist.add(stack.pop());
        }
        Cycle cyc = new Cycle(cyclelist);
        cyc.v = stack.size();
        //stack.clear();
        cyc.max = max;
        cyc.minIndex = minIndex;
        cycles.add(cyc);
    }
    cycles.sort(new Comparator<Cycle>(){
        @Override
        public int compare(Cycle o1,Cycle o2){
            int h1 = o1.cycle.size()-o1.v;
            int h2= o2.cycle.size()-o2.v;
            if(h1!=h2) return h2-h1;
            return o2.max-o1.max;
        }
    });
    Cycle cycle = cycles.get(0);
    int i = cycle.minIndex;
    StringBuilder sb = new StringBuilder();
    while(i>=0){
        sb.append(cycle.cycle.get(i--)+" ");
        // System.out.print(cycle.cycle.get(i--)+" ");
    }
    i = cycle.cycle.size()-1;
    while(i>cycle.minIndex){
        sb.append(cycle.cycle.get(i--)+" ");
        //System.out.print(cycle.cycle.get(i--)+" ");
    }
    String ans = sb.toString();
    ans = ans.substring(0,ans.length()-1);
    System.out.println(ans);

}
```