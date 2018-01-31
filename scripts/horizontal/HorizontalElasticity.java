//import com.vmware.vim25.*;
import com.vmware.vim25.mo.*;
import java.util.ArrayList;

//import java.lang.*; // for sleep

public class HorizontalElasticity {

	public static void main(String args[]) throws Exception {
		
		Vcenter2  vcenter_ins = new Vcenter2();
		ServiceInstance  conn = vcenter_ins.connect_vcenter();
		ArrayList<String> ips = new ArrayList<String>();
		int node_count = 2;
		ZabbixMonitoring zabbix_obj = new ZabbixMonitoring();
		String zabi = zabbix_obj.connect();
		String hostgroup = "Scalair scaling group";

		ProxyUpdate  proxy_ins = new ProxyUpdate(); 
		System.out.println("started startded started started");
		try {
			Thread.sleep(120000);
			//Thread.sleep(84000);
			//Thread.sleep(1000);
		} catch (InterruptedException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		while (true) {
			if (zabbix_obj.item_cpu_idle(zabi, hostgroup) > 70) {
				String vm_name = "flask"+node_count;
				String ip = vcenter_ins.clone_VM(conn, "flasktemplate", "Production", vm_name);
				// add host to Zabbix
				proxy_ins.proxy_add_route(ip);
				zabbix_obj.host_create(zabi, vm_name, ip, 10050, hostgroup, "Scalair Template OS Linux");
				// update proxy
				//proxy_ins.proxy_add_route(ip);
				ips.add(ip);
				node_count +=1;
				System.out.println("wait scaling up before taking another decision");
				Thread.sleep(60000);
			}
			if (zabbix_obj.item_cpu_idle(zabi, hostgroup) < 7) {
				//if (ips.size() != 0) {
				if (ips != null && !ips.isEmpty()) {
					String ipp = ips.get(ips.size() - 1);
					ips.remove(ips.size() - 1); // after you get the last element, remove it form the list
                    // delete the route from haproxy
					proxy_ins.proxy_del_route(ipp);
					Thread.sleep(10000);
					// delete the host from zabbix
					zabbix_obj.host_delete(zabi, ipp);
					//destroy the VM from Vcentor inventory
					vcenter_ins.destroy_VM(conn, ipp);
					System.out.println("wait after scaling down before  taking another decision");
            		Thread.sleep(60000);
				} else {
					System.out.println("no nodes in the scaling group to remove");
					Thread.sleep(10000);
				} 
			}
		System.out.println("wait before another loop");
		Thread.sleep(5000);
		
		}
	}

}
