import java.io.IOException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.io.*;
import java.util.concurrent.TimeUnit;
import java.lang.*; // for sleep

public class ProxyUpdate {
	String ip;
	//public ProxyUpdate(String name, String ip) { 
		// This constructor has one parameter, name. i
		//System.out.println("Passed Name is :" + name );
		//System.out.println("Passed node ip  is :" + ip );
		//this.ip = ip;
	//}

	public  void proxy_add_route(String ip) {
		Path path = Paths.get("/etc/haproxy/haproxy.cfg");
		try {
			List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

			int position = 75;
			String extraLine = "    server node "+ip+":80 maxconn 1000 check port 80";  

			lines.add(position, extraLine);
			Files.write(path, lines, StandardCharsets.UTF_8);
			Thread.sleep(1000);
	        String cmd = "service haproxy reload";
    	    //Process p = Runtime.getRuntime().exec(cmd);
			Runtime.getRuntime().exec(cmd);
			System.out.println("the proxy is updated, new route is added");
		} catch (IOException e) {
			System.out.println(e);
		} catch(InterruptedException ex) {
		 Thread.currentThread().interrupt();
		}			
	}

public  void notusedproxy_add_route(String ip) {
        Path path = Paths.get("/etc/haproxy/haproxy.cfg");
        try {
            //List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

            //int position = 76;
            //String extraLine = "    server exp1 "+ip+":80 maxconn 1000 check port 80";

            //lines.add(position, extraLine);
            //Files.write(path, lines, StandardCharsets.UTF_8);
            //Thread.sleep(2000);
            String cmd1 = " sudo sed -i  '"+ File.separator + "#insertrouteshere" + File.separator + "a \\    server node "+ ip +":80 maxconn 1000 check port 80' " + path;
			
			System.out.println(cmd1);
			Process p3 = Runtime.getRuntime().exec(cmd1);
			Thread.sleep(1000);
            ////Process p = Runtime.getRuntime().exec(cmd);
			String cmd = "service haproxy restart";
            Runtime.getRuntime().exec(cmd);
            System.out.println("the proxy is updated, new route is added");
        } catch (IOException e) {
            System.out.println(e);
        } catch(InterruptedException ex) {
         Thread.currentThread().interrupt();
        }
    }

 	public  void test(String ip) {
		//Path path = Paths.get("/etc/haproxy/haproxy.cfg");
        Path path = Paths.get("/home/docker/zabbix/java/version1/yehia.cfg");
		try {
		String cmd1 = " sed -i  '"+ File.separator + "#insertrouteshere" + File.separator + "a \\    server node "+ ip +":80 maxconn 1000 check port 80' " +  path;

		ProcessBuilder pb = new ProcessBuilder(cmd1);
		pb.inheritIO();
		pb.start();
		//Runtime.getRuntime().exec(cmd1);
		} catch (IOException e) {
			 System.out.println(e);
		}

    }


	public  void proxy_del_route(String ip) {
		//String cmd = "sed -i  /second/d yehia.txt";  //this is also works 
		Path path = Paths.get("/etc/haproxy/haproxy.cfg");
		String cmd = "sed -i " + File.separator + ip + File.separator + "d " + path; 
		try {
		//Process p = Runtime.getRuntime().exec(cmd);
		Runtime.getRuntime().exec(cmd);
		Runtime.getRuntime().exec("ls");	
		Thread.sleep(2000);
		String cmd2 = "service haproxy restart";
		Process p2 = Runtime.getRuntime().exec(cmd2);
		Runtime.getRuntime().exec(cmd2);
		System.out.println("the proxy is updated, route " + ip + " is removed");
		} catch (IOException e) {
	 		System.out.println(e);
		} catch(InterruptedException ex) {
			Thread.currentThread().interrupt();
        }
	}	

	public static void main(String []args) {
		String ip = "172.16.227.15";
		//ProxyUpdate  proxy = new ProxyUpdate( "tommy", ip );
		ProxyUpdate  proxy = new ProxyUpdate();
		//proxy.test(ip);
		proxy.proxy_add_route("172.16.225.77");
		//proxy.proxy_del_route("172.16.225.77");

	}
}
