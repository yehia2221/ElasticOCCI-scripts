import com.vmware.vim25.*;
import com.vmware.vim25.mo.*;

//import java.net.MalformedURLException;
import java.net.URL;
//import java.rmi.RemoteException;

// to bypass ssl certificate
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
//import java.io.OutputStream;

//import java.lang.*; // for sleep

public class Vcenter2 {
	public void  bypass_sslcertificate() {
		TrustManager[] trustAllCerts = new TrustManager[]{
			new X509TrustManager() {
				public java.security.cert.X509Certificate[] getAcceptedIssuers()
				{
					return null;
				}
				public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
				{
					//No need to implement.
				}
				public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
				{
					//No need to implement.
				}
			}
		};
		// Install the all-trusting trust manager
		try
		{
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		}
		catch (Exception e)
		{
			System.out.println(e);
		}
	}

	public ServiceInstance connect_vcenter() {
		ServiceInstance  si = null;
		try {
			bypass_sslcertificate();
			String vSphereUrl = "***********";
			String vSpherePassword = "********";	
			String vSphereUsername  = "**********";
			//String templateVMName = "template";
			//String cloneName = "vm-1";
			//Connect to vSphere server using VIJAVA
			si = new ServiceInstance(new URL(vSphereUrl),vSphereUsername,vSpherePassword);
			System.out.println("host :---"+si.getAboutInfo().getFullName());
			System.out.println(" os type is .. " +si.getAboutInfo().osType);

		} catch (Exception je) {
            //je.printStackTrace();
			System.out.println("connection failed  " + je);
		}

    return si;
	}


	public VirtualMachine get_vcenter_obj(ServiceInstance si, String obj_name) {
		VirtualMachine vm = null;
		try {
			Folder rootFolder = si.getRootFolder();
			vm = (VirtualMachine) new InventoryNavigator(rootFolder).searchManagedEntity("VirtualMachine", obj_name);
			if (vm==null) {
				System.out.println("No VM " + obj_name  + " found");
				//si.getServerConnection().logout();
				//return;
			}		
			System.out.println("template :--- "+ vm.getName());
		} catch (Exception e) {
            //je.printStackTrace();
			System.out.println("error  " + e);
        }

	return vm;
	}

	public String clone_VM(ServiceInstance si, String template, String dcName, String cloneName) {
        VirtualMachine vmTemplate = get_vcenter_obj(si ,template);
		String ipaddress = "";
		 try {	
			VirtualMachine vm1 = get_vcenter_obj(si , cloneName);
			System.out.println("Wait for the machine to be powered on, and get its dhcp ip------ "); 
			Thread.sleep(40000);
			System.out.println("ip address:--- "+vm1.getGuest().getIpAddress());
			ipaddress = vm1.getGuest().getIpAddress();
			while(ipaddress == null || (ipaddress.equals("127.0.0.1"))) {
				Thread.sleep(2000);
				System.out.println("wait ip ip ip");
				ipaddress = vm1.getGuest().getIpAddress();
			}
	
		}  catch (Exception e) {
            //je.printStackTrace();
            System.out.println("can not create clone  " + e);
        }
	return ipaddress;
	}

	public VirtualMachine  findVMbyIp (ServiceInstance si, String ip)  {
		VirtualMachine vm = null;
		try {
		vm = (VirtualMachine) si.getSearchIndex().findByIp(null, ip, true); // null searches in all the inventory, if you specifiy certain datacenter,  this restricts the query to entities in a particular datacenter. true, return vms, false returns hosts
		//System.out.println("what is wrong  " +vm);
		} catch (Exception e) {
			//je.printStackTrace();
			System.out.println("error  " + e);
        }
	return vm;
	}

	public void destroy_VM(ServiceInstance si, String ip)  {
		try { 
			VirtualMachine vm = findVMbyIp(si, ip);
			// power off
			Task taskoff = vm.powerOffVM_Task();
			System.out.println("powering of  VM " +vm.getName());
			taskoff.waitForTask();
			Task destroyTask = vm.destroy_Task();
			System.out.println("deleting  VM " +vm.getName());
			destroyTask.waitForTask();
		} catch (Exception e) {
			//je.printStackTrace();
			System.out.println("error  " + e);
		}
	}

	public static void main(String[] args) {
		Vcenter  vcenter_ins = new Vcenter();
		ServiceInstance  conn = vcenter_ins.connect_vcenter();
		System.out.println("host :--- "+conn.getAboutInfo().getFullName());
		
		//VirtualMachine  template;
		//template = vcenter_ins.get_vcenter_obj(conn ,"template17");
		
		//String ip; 
		//ip = vcenter_ins.clone_VM(conn, "template1", "Production", "yehia1212121");
		//System.out.println("ip in main :--- "+ip);
		//conn.getServerConnection().logout();

		//vcenter_ins.find_VM_byIP(conn);
		vcenter_ins.destroy_VM(conn, "172.16.225.171");
	}
}	
