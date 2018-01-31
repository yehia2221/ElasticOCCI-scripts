//package horizontalElasticity;
//import java.text.DateFormat;
import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Date;


import java.util.Scanner;
import java.util.Timer;
//import com.vmware.vim25.mo.*;
import java.util.ArrayList;

import com.vmware.vim25.*;
import com.vmware.vim25.mo.*;

//import java.net.MalformedURLException;
import java.net.URL;
//import java.rmi.RemoteException;
import java.rmi.RemoteException;

// to bypass ssl certificate
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


class VerticalElasticity {
	
	int uthreshold=70;
	int lthreshold=4;
	
	int breathUp = 60000;
	int breathDown = 1000;
	
	int pollTime = 5000;
	
	public enum mode {
		manuel,
		dynamic,
		scheduled;
	}
	public enum Direction {
		up,
		down;
	}
	
	public void VerticalElasticity() {	
		
	}
	public void VerticalElasticity(int uthreshold) {
		this.uthreshold = uthreshold;
	}
	public void VerticalElasticity(int uthreshold, int lthreshold) {
		this.uthreshold = uthreshold;
		this.lthreshold = lthreshold;
	}
	public void VerticalElasticity(int uthreshold, int lthreshold, int breathUp) {
		this.uthreshold = uthreshold;
		this.lthreshold = lthreshold;
		this.breathUp = breathUp;
	}
	public void VerticalElasticity(int uthreshold, int lthreshold, int breathUp, int breathDown) {
		this.uthreshold = uthreshold;
		this.lthreshold = lthreshold;
		this.breathUp = breathUp;
		this.breathDown = breathDown;
	}
	
	public boolean createPolicy ( double metricUtilization, String op, int threshold) {
		boolean testoutput = false;
		if (op.equals("GreaterThan")){
			if  (metricUtilization > threshold){
				testoutput =  true;	
			}
		} else if (op.equals("greater than or equal")){
			if  (metricUtilization >= threshold){
				testoutput =  true;
			}
		} else if (op.equals("less than")){
			if  (metricUtilization < threshold){
				testoutput =  true;
			}
		} else if (op.equals("less than or equal")){
			if  (metricUtilization <= threshold){
				testoutput =  true;
			}
		}			
		
	return testoutput;	
	}
}

public class ElasticCPU extends VerticalElasticity {
	
	private int lowerLimit;
	private int upperLimit;
	private int stepCPUIncrease;
	private int stepCPUDcrease;
	
	private ElasticCPU () {
		
	}
	
	
	public static class Builder {
		private int lowerLimit = 4;
		private int upperLimit = 3;
		private int stepCPUIncrease = 1;
		private int stepCPUDcrease = 1;
		
	    //public Builder() {

	    //}
		public Builder lowerLimit(int value) {
			lowerLimit = value;
	        return this;
	    }
		public Builder upperLimit(int value) {
			upperLimit = value;
	        return this;
	    }
		
		public Builder stepCPUIncrease(int value) {
			stepCPUIncrease = value;
	        return this;
	    }
		
		public Builder stepCPUDcrease(int value) {
			stepCPUDcrease = value;
	        return this;
	    }
		 
	    public ElasticCPU build() {
	    	ElasticCPU inst = new ElasticCPU();
	    	inst.lowerLimit = this.lowerLimit;
	    	inst.upperLimit = this.upperLimit;
	    	inst.stepCPUIncrease = this.stepCPUIncrease;
	    	inst.stepCPUDcrease = this.stepCPUDcrease;
	    	//inst.lowerLimit=this.lowerLimit!=null?this.lowerLimit:0;
	    	return inst;
	    }
	    	    
	}
	
	/////////////////////////////////////////////////////////////
	private void manuel(Direction dir, String vmname) throws Exception {
		
		Vcenter vmconnector = new Vcenter();
		ServiceInstance  conn = vmconnector.connect_vcenter();
		//String  conn = vmconnector.connect_vcenter();
		switch (dir) {
        case up:
        	//int cpus = vmconnector.getCPUs(vmname);
			int cpus = vmconnector.getvCPUS(conn, vmname);

        	System.out.println("current vCPUs are: " + cpus);
        	cpus = cpus + stepCPUIncrease;
        	if (cpus <= upperLimit) {
        		//vmconnector.addCPU(vmname, cpus);
				vmconnector.reconfigure_VM(conn, vmname, cpus);
        		System.out.println("you have increased your VCPUs by " + stepCPUIncrease);
        	} else {
        		System.out.println("You can't add more cpus to this VM, you have arrived the maximum limit");
        	}
            break;
        case down:
        	System.out.println("Sorry, you can't scale down in VMWare technology, please wait until we add more conncetors in OCCIware such as KVM");
            break;
        default: 
        	System.out.println("Enter correct direction, we have only up and down");
        break;
		}
    	
    }
	
	private void dynamic(String vmname) throws Exception {
		ZabbixMonitoring2 zabbix_obj = new ZabbixMonitoring2();
		String zabi = zabbix_obj.connect();
		int hostid = zabbix_obj.getHostByName(zabi, vmname);
		//System.out.print("hi, cpus used is " + cpuUsed);
		//Vertical vmconnector = new Vertical();
		Vcenter vmconnector = new Vcenter();
		ServiceInstance  conn = vmconnector.connect_vcenter();
		System.out.println("started startded started started");
           try {
               Thread.sleep(61000);
           } catch (InterruptedException e2) {
           // TODO Auto-generated catch block
               e2.printStackTrace();
           }
		while (true) {
			Double cpuUsed = zabbix_obj.item_cpu_idle(zabi, hostid);
        	//int cpus = vmconnector.getCPUs(vmname);
			///int cpus = vmconnector.getvCPUS(conn, vmname);
        	///System.out.println("current vCPUs are: " + cpus);
        	///int size = cpus + stepCPUIncrease;
        	//if (cpus <= upperLimit) {
			///if (cpus <= upperLimit) {
            	if (createPolicy(cpuUsed, "GreaterThan", uthreshold)) {
            		int cpus = vmconnector.getvCPUS(conn, vmname);
            		System.out.println("current vCPUs are: " + cpus);
            		int size = cpus + stepCPUIncrease;
					vmconnector.reconfigure_VM(conn, vmname, size);
            		//vmconnector.addCPU(vmname, size);
            		System.out.println("you have increased your VCPUs by "); // + stepCPUIncrease);
                    System.out.println("wait scaling up before taking another decision");
                    //Thread.sleep(breathUp);
					Thread.sleep(60000);
            	}
        	///} else {
        	///	System.out.println("You can't add more cpus to this VM, you have arrived the maximum limit");
        	///}
       // 	if ((createPolicy(cpuUsed, "less than or equal", lthreshold)) && (cpus > lowerLimit)) {
        //		System.out.println("Sorry, you can't scale down in VMWare technology, please wait until we add more conncetors in OCCIware such as KVM");
        //		Thread.sleep(breathDown);
        //	}
		System.out.println("iterations between loops");
        //Thread.sleep(pollTime);	
		Thread.sleep(5000);
		}
	}

	public static void main(String[] args) throws  Exception {
		ElasticCPU inst = new ElasticCPU.Builder().stepCPUIncrease(1).build();
		//ElasticCPU inst = new ElasticCPU.Builder().lowerLimit(50).upperLimit(100).build();
		inst.VerticalElasticity(70,6, 60000);
		System.out.print("uthreshold " + inst.uthreshold + " lthreshold " + inst.lthreshold + " breath up " + inst.breathUp + " breath down " + inst.breathDown);
		
		System.out.print(" " + inst.lowerLimit + " " + inst.upperLimit + '\n');
		
		mode m1 = mode.dynamic;
		//Scanner user_input = new Scanner( System.in );
		//System.out.println("choose elasticity mode (manuel, dynamic, schudeled):");
		//String mode = user_input.next( );
		switch (m1) {
        case manuel:
        	//String dire = "up";
        	Direction dir = Direction.down;
        	System.out.println(m1);
        	inst.manuel(dir, "flask1");
            break;
        case dynamic:
        	//boolean x = inst.createPolicy(60 , "less than or equal", 60);
        	//System.out.println(x);
        	try {
				inst.dynamic("flask1");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            break;
        default: 
        	System.out.println("The keywords entered are not correct, the modes supported are: manuel, dynamic and schudeled ");
        break;
		}
	}

}

