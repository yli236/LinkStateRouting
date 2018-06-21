package HW5;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class LinkStateRouting {
	static HashMap<Integer, Router> routers = new HashMap();
	static HashSet<Integer> idSet = new HashSet<>();
	static HashSet<String> set = new HashSet<>();
	static List<double[]> list = new ArrayList<double[]>();
	static String[] sss = null;
	static double infi1 = 999999999;
	static double infi = Double.MAX_VALUE;
	static boolean flag = true;
	static int total = 0;
	
	
	
	public static class Router {
		private int id;
		private String name;
		private int TTL;
		private boolean status = true;
		private List<Edge> edges = new ArrayList<>();
		private List<Edge> network = new ArrayList<>();
		private List<Edge> Edges = new ArrayList<>();
		
		private List<Integer> dirRouter = new ArrayList<>();
		private List<Integer> information = new ArrayList<>();
		
		private Map<Integer, Integer> id_order = new HashMap<>();
		private Map<Integer, Integer> order_id = new HashMap<>();
		
		public Router(int id, String name) {
			this.id =id;
			this.name = name;
		}
		
		public void addEdges(Edge edge) {
			this.edges.add(edge);
		}
		
		public void addNetwork(List<Edge> network) {
			this.network.addAll(network);
			this.network = new ArrayList<>(new HashSet<>(this.network));
		}
		
		public void getPacket(LinkStatePacket lsp) {
			this.TTL = lsp.TTL --;
			lsp.sequence ++;
			if(lsp.TTL > 0 && this.information.contains(lsp.originalId)) {
				this.information.add(lsp.originalId);
				this.addNetwork(lsp.edges);
				for(int id: dirRouter) {
					lsp.TTL = this.TTL;
					if(routers.get(id).status == true && id!=lsp.originalId&&id!=lsp.lastid) {
						lsp.lastid = this.id;
						routers.get(id).getPacket(lsp);
					}
				}
			}
		}
		
		public void findPacket() {
			if(this.status==true) {
				for(int id: dirRouter) {
					LinkStatePacket lsp = new LinkStatePacket(this.id, this.id, this.edges);
					if(routers.get(id).status==true) {
						routers.get(id).getPacket(lsp);
					}
				}
			}
		}
		
		public void setOrder() {
			int order = 0; 
			this.id_order.clear();
			this.order_id.clear();
			this.Edges.removeAll(this.Edges);
			for(Edge edge: this.network) {
				if(routers.get(edge.startId).status==false || routers.get(edge.endId).status==false) {
					Edge infiEdge = new Edge(edge.startId, edge.endId, infi1);
					this.Edges.add(infiEdge);
				}else {
					this.Edges.add(edge);
				}
				if(!id_order.containsKey(edge.startId)) {
					id_order.put(edge.startId, order);
					order_id.put(order, edge.startId);
					order++;
				}
				if(!id_order.containsKey(edge.endId)) {
					id_order.put(edge.endId, order);
					order_id.put(order,  edge.endId);
					order++;
				}
			}
		}
	}
		
		public static boolean readFile() {
			 try{
		            File file = new File("infile.dat");
		            if(file.isFile() && file.exists()){
		                InputStreamReader read = new InputStreamReader(new FileInputStream(file));
		                BufferedReader br = new BufferedReader(read);
		                String line;
		                ArrayList<String> lines = new ArrayList<String>();
		                while ((line = br.readLine()) != null) {
		                    lines.add(line);
		                }
		                read.close();
		                for (int i = 0; i < lines.size(); i++){
		                    Router router = new Router(0,"0");

		                    if (lines.get(i).charAt(0)!=32 && lines.get(i).charAt(0)!=9) {
		                        total++;
		                        String str = lines.get(i).trim();
		                        sss = str.split("\\s+");
		                        router.id = Integer.parseInt(sss[0]);
		                        router.name = sss[1];
		                        set.add(sss[1]);
		                        routers.put(Integer.parseInt(sss[0]), router);

		                    } else {
		                        String[] str = lines.get(i).trim().split("\\s+");

		                        if(str.length==2){
		                            Edge edge = new Edge(Integer.parseInt(sss[0]), Integer.parseInt(str[0]), Double.parseDouble(str[1]));
		                            routers.get(Integer.parseInt(sss[0])).addEdges(edge);
		                            routers.get(Integer.parseInt(sss[0])).dirRouter.add(Integer.parseInt(str[0]));
		                            routers.get(Integer.parseInt(sss[0])).network.add(edge);
		                        }else{
		                            Edge edge = new Edge(Integer.parseInt(sss[0]), Integer.parseInt(str[0]),1.0);
		                            routers.get(Integer.parseInt(sss[0])).addEdges(edge);
		                            routers.get(Integer.parseInt(sss[0])).dirRouter.add(Integer.parseInt(str[0]));
		                            routers.get(Integer.parseInt(sss[0])).network.add(edge);
		                        }
		                    }
		                }
		            } else {
		                System.out.println("There is no such file");
		                return false;
		            }
		        } catch (Exception e){
		            System.out.println("File formation error");
		            return false;
		        }
		        return true;
		}
		
		public static HashSet<String> read_File(){
			HashSet<String> getSet = new HashSet<>();
	        try {
	            File file = new File("infile.dat");
	            if (file.isFile() && file.exists()) {
	                InputStreamReader read = new InputStreamReader(new FileInputStream(file));
	                BufferedReader br = new BufferedReader(read);
	                String line;
	                ArrayList<String> lines = new ArrayList<>();
	                while ((line = br.readLine()) != null) {
	                    lines.add(line);
	                }
	                read.close();

	                for (int i = 0; i < lines.size(); i++) {
	                    if (lines.get(i).charAt(0)!=32 && lines.get(i).charAt(0)!=9) {
	                        String str = lines.get(i).trim();
	                        String[] strar = str.split("\\s+");
	                        idSet.add(Integer.parseInt(strar[0]));
	                        getSet.add(strar[1]);
	                    }
	                }
	            } else {
	                System.out.println("There is no such file");
	                flag=false;
	            }
	        } catch (Exception e) {
	            System.out.println("File formation error");
	            flag=false;
	        }
	        return getSet;
		}
		
		public static double getCost(int startId, int endId, Router router) {
			if (startId == endId){
	            return 0;
	        }
	        for (int i=0; i<list.size(); i++){
	            double[] cost = list.get(i);
	            double cost1 = cost[0];
	            double cost2 = cost[1];
	            double cost3 = cost[2];
	            if ((startId == cost1 && endId == cost2) || (startId == cost2 && endId == cost1)) {
	                return cost3;
	            }
	        }
	        return infi;
		}
		
		public static int[] Dijkstra(int vs, int[] prev, double[]dist, Router router) {
			boolean[] flag = new boolean[router.id_order.size()];
	        for (int i = 0; i < router.id_order.size(); i++) {
	            flag[i] = false;
	            prev[i] = vs;
	            dist[i] = getCost(vs, i,router);
	        }
	        flag[vs] = true;
	        dist[vs] = 0;
	        int k = 0;
	        for (int i = 1; i < router.id_order.size(); i++) {
	            double min = infi;
	            for (int j = 0; j < router.id_order.size(); j++) {
	                if (flag[j] == false && dist[j] < min) {
	                    min = dist[j];
	                    k = j;
	                }
	            }
	            flag[k] = true;
	            for (int j = 0; j < router.id_order.size(); j++) {
	                double tmp = getCost(k, j,router);
	                tmp = (tmp == infi ? infi : (min + tmp));
	                if (flag[j] == false && (tmp < dist[j])) {
	                    dist[j] = tmp;
	                    prev[j] = k;
	                }
	            }
	        }
	        return prev;
		}
		
		public static void preindex(int vs, int[] prev, double[] dist, Router router) {
			int[] prevv = Dijkstra(vs, prev, dist, router);
	        HashSet<String> getset = new HashSet<>();
	        for (int i = 0; i < router.id_order.size(); i++) {
	            if (prevv[i] == vs) {
	                prevv[i] = i;
	            }
	            while (getCost(vs, prevv[i],router) == infi) {
	                prevv[i] = prevv[prevv[i]];
	            }
	        }

	        System.out.println(routers.get(router.order_id.get(vs)).name + ":");
	        System.out.println(String.format("%-21s", " network")+String.format("%-20s", "cost")+String.format("%-20s", "outgoing link"));
	        getset = read_File();
	        for (int i = 0; i < router.id_order.size(); i++) {
	            if (i == vs) {
	                prevv[i] = i;
	            }
	            if(dist[i]<infi1){
	                System.out.printf(String.format("%-21s", routers.get(router.order_id.get(i)).name)
	                        + String.format("%-21s", dist[i])
	                        + String.format("%-20s", routers.get(router.order_id.get(prev[i])).id));
	                getset.remove(routers.get(router.order_id.get(i)).name);
	                System.out.println();
	            }
	            else if(dist[i]>=infi1){
	                System.out.printf(String.format("%-21s", routers.get(router.order_id.get(i)).name)
	                        + String.format("%-21s", "infinity" )
	                        + String.format("%-20s", routers.get(router.order_id.get(prev[i])).id));
	                getset.remove(routers.get(router.order_id.get(i)).name);
	                System.out.println();
	            }
	        }
		}
		
		public static void main(String[] args) {
			 read_File();
		        if (readFile() == true && flag == true){
		            for(Router router : routers.values()){
		                router.setOrder();
		            }
		            Scanner input = new Scanner(System.in);
		            while (true){
		                System.out.println("please enter the following: (# is router number)");
		                System.out.println("1.Enter C to Continue");
		                System.out.println("2.Enter Q to Quit");
		                System.out.println("3.Enter P# to Print");
		                System.out.println("4.Enter S# to Shut down (Enter C to update)");
		                System.out.println("5.Enter T# to Start up (Enter C to update)");

		                String str = input.nextLine();
		                if (str.equals("C") || str.equals("c")){
		                    for (Router router : routers.values()){
		                        router.findPacket();
		                    }
		                    for (Router router : routers.values()){
		                        router.setOrder();
		                    }

		                }

		                else if (str.equals("Q") || str.equals("q")){
		                    System.out.println("Over");
		                    System.exit(0);
		                }

		                else if ((str.charAt(0) == 'P' || str.charAt(0) == 'p') && str.length() > 1){
		                    str = str.replace("P", "").replace("p","");
		                    if (str.matches("[0-9]*")){
		                        int id = Integer.parseInt(str);

		                        if(!idSet.contains(id)){
		                            System.out.println("There is no such router");
		                        }

		                        if (routers.get(id).status == false){
		                            System.out.println("This router has been shut down");
		                        }

		                        else {
		                            for (Edge edge : routers.get(id).Edges){
		                                double[] a = {(double)routers.get(id).id_order.get(edge.startId), (double)routers.get(id).id_order.get(edge.endId),edge.cost};
		                                list.add(a);
		                            }
		                            if(list.size() == 0){
		                                System.out.println("Network        Cost       Outgoing Link");
		                                System.out.println(routers.get(id).name + "     " + 0 + "     " + routers.get(id).name);
		                                System.out.println();
		                            } else {
		                                int[] prev = new int[routers.get(id).id_order.size()];
		                                double[] dist = new double[routers.get(id).id_order.size()];
		                                preindex(routers.get(id).id_order.get(id), prev, dist, routers.get(id));
		                            }
		                            list.removeAll(list);
		                        }
		                    }else {
		                        System.out.println("Please follow the enter formation");
		                    }
		                }

		                else if ((str.charAt(0) == 'S' || str.charAt(0) == 's') && str.length() > 1){
		                    str = str.replace("S", "").replace("s","");
		                    if (str.matches("[0-9]*")){
		                        int id = Integer.parseInt(str);

		                        if(!idSet.contains(id)){
		                            System.out.println("There is no such router");
		                            continue;
		                        }

		                        routers.get(id).status = false;
		                    }else {
		                        System.out.println("Please follow the enter formation");
		                    }
		                }

		                else if ((str.charAt(0) == 'T' || str.charAt(0) == 't') && str.length() > 1){
		                    str = str.replace("T", "").replace("t","");
		                    if (str.matches("[0-9]*")){
		                        int id = Integer.parseInt(str);

		                        if(!idSet.contains(id)){
		                            System.out.println("There is no such router");
		                            continue;
		                        }

		                        routers.get(id).status = true;
		                    }else {
		                        System.out.println("Please follow the enter formation");
		                    }
		                }

		                else {
		                    System.out.println("Please follow the enter formation");
		                }
		            }
		        }
		}
	
}
