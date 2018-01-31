import org.json.JSONObject;
import org.json.JSONException;
import com.goebl.david.Webb;
import org.json.JSONArray;
//import java.lang.*;
import java.util.ArrayList;
 
public class ZabbixMonitoring {

	public String  connect() {
		String str = "";
		try {
			JSONObject mainJObj = new JSONObject();
			JSONObject paramJObj = new JSONObject();
			mainJObj.put("jsonrpc", "2.0");
			mainJObj.put("method", "user.login");
 			
			paramJObj.put("user", "*****");
			paramJObj.put("password", "***********");
 
			mainJObj.put("params", paramJObj);
			mainJObj.put("id", "1");
 
			Webb webb = Webb.create();
 
			//System.out.println("Data to send: " + mainJObj.toString());

			JSONObject result = webb.post("**********")
            	.header("Content-Type", "application/json")
                .useCaches(false)
                .body(mainJObj)
                .ensureSuccess()
                .asJsonObject()
                .getBody();
		//System.out.println("Authentication token: " + result.getString("result"));
		str =  result.getString("result");
		} catch (JSONException je) {
 			System.out.println("Error creating JSON request to Zabbix API..." + je.getMessage());
		}
		return str;
	} 

    public int hostgroups_list(String zabi, String hg_name) { // get host group id by name
        int id = 0;
        try {
            JSONObject mainJObj = new JSONObject();
            JSONObject paramJObj = new JSONObject();
			JSONObject subparamJObj = new JSONObject();

            mainJObj.put("jsonrpc", "2.0");
            mainJObj.put("method", "hostgroup.get");

            paramJObj.put("output", "extend");
			
			//subparamJObj.put("name", new JSONArray(new Object[] { "Linux servers", "Zabbix servers"}));
			subparamJObj.put("name", hg_name); // get hg by name
			paramJObj.put("filter", subparamJObj);

            mainJObj.put("params", paramJObj);
            mainJObj.put("auth", zabi);
			mainJObj.put("id", "1");

            Webb webb = Webb.create();

            System.out.println("Data to send: " + mainJObj.toString());

            JSONObject result = webb.post("http://172.16.225.37/zabbix/api_jsonrpc.php")
                .header("Content-Type", "application/json")
                .useCaches(false)
                .body(mainJObj)
                .ensureSuccess()
                .asJsonObject()
                .getBody();
			
			//String output = result.getString("result");
			JSONArray output = result.getJSONArray("result");
			System.out.println("output" + output);
			for (int i = 0; i < output.length(); ++i) {
            	JSONObject obj = output.getJSONObject(i);
				System.out.println("group name  " + obj);
    			id = obj.getInt("groupid");
    			String name = obj.getString("name");
				System.out.println("group   " + name  + " with id  " + id);
			}

        } catch (JSONException je) {
            System.out.println("Error creating JSON request to Zabbix API..." + je.getMessage());
        }
		return id; 
    }

    public ArrayList<Integer>  hosts_list(String zabi, String hg_name) {
        ArrayList<Integer> arr = new ArrayList<Integer>();
		//int arr[] = new int[2];
        try {
            JSONObject mainJObj = new JSONObject();
            JSONObject paramJObj = new JSONObject();
            //JSONObject subparamJObj = new JSONObject();
			int hg_id = hostgroups_list(zabi, hg_name);	
			System.out.println("hg id " + hg_id);	
            mainJObj.put("jsonrpc", "2.0");
            mainJObj.put("method", "host.get");
			paramJObj.put("groupids", hg_id);
            paramJObj.put("output", new JSONArray(new Object[] { "name", "hostid"}));
            //subparamJObj.put("name", new JSONArray(new Object[] { "Linux servers", "Zabbix servers"}));
            //paramJObj.put("filter", subparamJObj);
            mainJObj.put("params", paramJObj);
            mainJObj.put("auth", zabi);
            mainJObj.put("id", "1");
            Webb webb = Webb.create();
            System.out.println("Data to send: " + mainJObj.toString());

            JSONObject result = webb.post("http://172.16.225.37/zabbix/api_jsonrpc.php")
                .header("Content-Type", "application/json")
                .useCaches(false)
                .body(mainJObj)
                .ensureSuccess()
                .asJsonObject()
                .getBody();

            //String output = result.getString("result");
			//System.out.println("output   " + output) ;
            JSONArray output = result.getJSONArray("result");
            System.out.println("output" + output);
			for (int i = 0; i < output.length(); ++i) {
            	JSONObject obj = output.getJSONObject(i);
            	int id = obj.getInt("hostid");
				//arr[i] = id;
				arr.add(id);
            	String name = obj.getString("name");
            	System.out.println("host   " + name  + " with id  " + id);
            }

        } catch (JSONException je) {
            System.out.println("Error creating JSON request to Zabbix API..." + je.getMessage());
        }
		return arr;
    }

    public int get_template(String zabi, String byname) {
		int template_id = 0;
        try {
            JSONObject mainJObj = new JSONObject();
            JSONObject paramJObj = new JSONObject();
            JSONObject subparamJObj = new JSONObject();

            mainJObj.put("jsonrpc", "2.0");
            mainJObj.put("method", "template.get");
            paramJObj.put("output", new JSONArray(new Object[] { "name", "templateid"}));
            subparamJObj.put("host", byname);
            paramJObj.put("filter", subparamJObj);
            mainJObj.put("params", paramJObj);
            mainJObj.put("auth", zabi);
            mainJObj.put("id", "1");
            Webb webb = Webb.create();
            System.out.println("Data to send: " + mainJObj.toString());

            JSONObject result = webb.post("http://172.16.225.37/zabbix/api_jsonrpc.php")
                .header("Content-Type", "application/json")
                .useCaches(false)
                .body(mainJObj)
                .ensureSuccess()
                .asJsonObject()
                .getBody();
			
            //String output = result.getString("result");
            //System.out.println("output   " + output) ;
            JSONArray output = result.getJSONArray("result");
            //System.out.println("output" + output);
            for (int i = 0; i < output.length(); ++i) {
                JSONObject obj = output.getJSONObject(i);
            	int id11 = obj.getInt("templateid");
           		//String name = obj.getString("name");
				template_id = id11;
            	//System.out.println( name  + " with id  " + id11);
           	}

        } catch (JSONException je) {
            System.out.println("Error creating JSON request to Zabbix API..." + je.getMessage());
        }
		return template_id;
    }


    public Double item_cpu_idle(String zabi, String hg_name) {
		Double cpu_total = 0.0;  // total cpu idle for all hosts in the hostgroup
		Double cpu_average = 0.0; // average cpu idle for all hosts in the hostgroup
		Double cpu_usage = 0.0;  // average cpu usage for all hosts in the hostgroup
		int count = 0;
		ArrayList<Integer> arr = hosts_list(zabi, hg_name);
		for(int n=0;n<arr.size();n++) {//length is the property of array, size of arraylist  
			//System.out.println(arr.get(n));
			int hostidd = arr.get(n);  
        	try {
            	JSONObject mainJObj = new JSONObject();
            	JSONObject paramJObj = new JSONObject();
            	JSONObject subparamJObj = new JSONObject();

            	mainJObj.put("jsonrpc", "2.0");
            	mainJObj.put("method", "item.get");
            	paramJObj.put("output", "extend");
            	paramJObj.put("hostids", hostidd);
				subparamJObj.put("key_", "system.cpu.util[,idle]");
            	paramJObj.put("search", subparamJObj);
				mainJObj.put("params", paramJObj);
            	mainJObj.put("auth", zabi);
            	mainJObj.put("id", "1");
            	Webb webb = Webb.create();
            	//System.out.println("Data to send: " + mainJObj.toString());

            	JSONObject result = webb.post("http://172.16.225.37/zabbix/api_jsonrpc.php")
                	.header("Content-Type", "application/json")
                	.useCaches(false)
                	.body(mainJObj)
                	.ensureSuccess()
                	.asJsonObject()
                	.getBody();

            	////String output = result.getString("result");
            	////System.out.println("output   " + output) ;
            	JSONArray output = result.getJSONArray("result");
            	//System.out.println("output" + output);
            	for (int i = 0; i < output.length(); ++i) {
                	JSONObject obj = output.getJSONObject(i);
                	//String name = obj.getString("name");
            		Double  value = obj.getDouble("lastvalue");
           			//System.out.println( name  + " is  " + value);
					cpu_total +=value;
           		}	

        	} catch (JSONException je) {
            	System.out.println("Error creating JSON request to Zabbix API..." + je.getMessage());
        	}
		count +=1;
		}
		//System.out.println("cpu total    " + cpu_total);
        //System.out.println("count  " + count);	
		cpu_average = cpu_total/count;
		//System.out.println("cpu average    " + cpu_average);	
		cpu_usage = 100 - cpu_average;
		System.out.println("cpu_usage cpu_usage cpu_usage    " + cpu_usage); 
		return cpu_usage;
    }

    public void host_create(String zabi, String host_name, String host_ip, int port_num, String  hgroup, String template) {
        try {
            JSONObject mainJObj = new JSONObject();
            JSONObject paramJObj = new JSONObject();
            JSONObject subparamJObj = new JSONObject();
			JSONObject subparamJObj2 = new JSONObject();
			JSONObject subparamJObj3 = new JSONObject();			
			int template_id = get_template(zabi, template);
			int hg_id = hostgroups_list(zabi, hgroup);
			mainJObj.put("jsonrpc", "2.0");
            mainJObj.put("method", "host.create");
            
			paramJObj.put("host", host_name);
			subparamJObj.put("type", "1");
			subparamJObj.put("main", "1");
			subparamJObj.put("useip", "1");
			subparamJObj.put("ip", host_ip);
			subparamJObj.put("dns", "1");
			subparamJObj.put("port", port_num);
			paramJObj.put("interfaces", new JSONArray(new Object[] { subparamJObj}));

			subparamJObj2.put("groupid", hg_id);
            paramJObj.put("groups", new JSONArray(new Object[] { subparamJObj2 }));

			subparamJObj3.put("templateid", template_id);
			paramJObj.put("templates", new JSONArray(new Object[] { subparamJObj3 }));

            mainJObj.put("params", paramJObj);
            mainJObj.put("auth", zabi);
            mainJObj.put("id", "1");
            Webb webb = Webb.create();
            //System.out.println("Data to send: " + mainJObj.toString());

            JSONObject result = webb.post("http://172.16.225.37/zabbix/api_jsonrpc.php")
                .header("Content-Type", "application/json")
                .useCaches(false)
                .body(mainJObj)
                .ensureSuccess()
                .asJsonObject()
                .getBody();

			//System.out.println("output   " + result) ;

        } catch (JSONException je) {
            System.out.println("Error creating JSON request to Zabbix API..." + je.getMessage());
        }
    }

    public void host_delete(String zabi, String  ip) {
        try {
            JSONObject mainJObj = new JSONObject();
			int host_id = get_host_by_ip(zabi, ip);			

            mainJObj.put("jsonrpc", "2.0");
            mainJObj.put("method", "host.delete");
            mainJObj.put("params", new JSONArray(new Object[] { host_id }));
            mainJObj.put("auth", zabi);
            mainJObj.put("id", "1");
            Webb webb = Webb.create();
            //System.out.println("Data to send: " + mainJObj.toString());

            JSONObject result = webb.post("http://172.16.225.37/zabbix/api_jsonrpc.php")
                .header("Content-Type", "application/json")
                .useCaches(false)
                .body(mainJObj)
                .ensureSuccess()
                .asJsonObject()
                .getBody();

            //System.out.println("output   " + result) ;

        } catch (JSONException je) {
            System.out.println("Error creating JSON request to Zabbix API..." + je.getMessage());
        }
    }

    public int  get_host_by_ip(String zabi, String ip) {
        int id = 0;
		try {
            JSONObject mainJObj = new JSONObject();
            JSONObject paramJObj = new JSONObject();
            //int template_id = get_template(zabi, template);

            mainJObj.put("jsonrpc", "2.0");
            mainJObj.put("method", "hostinterface.get");
			paramJObj.put("output", "extend");
			paramJObj.put("sortfield", "interfaceid");
            mainJObj.put("params", paramJObj);
            mainJObj.put("auth", zabi);
            mainJObj.put("id", "1");
            Webb webb = Webb.create();
            System.out.println("Data to send: " + mainJObj.toString());

            JSONObject result = webb.post("http://172.16.225.37/zabbix/api_jsonrpc.php")
                .header("Content-Type", "application/json")
                .useCaches(false)
                .body(mainJObj)
                .ensureSuccess()
                .asJsonObject()
                .getBody();

            //System.out.println("output   " + result) ;
			JSONArray output = result.getJSONArray("result");
            //System.out.println("output" + output);
            for (int i = 0; i < output.length(); ++i) {
                JSONObject obj = output.getJSONObject(i);
				//System.out.println("input ip" + ip);
                String host_ip  = obj.getString("ip");
				if ((host_ip).equals (ip)) {
					int host_id = obj.getInt("hostid");
					id = host_id;
                	//System.out.println(" the ip of this host is  " + host_id);
				}
            }

        } catch (JSONException je) {
            System.out.println("Error creating JSON request to Zabbix API..." + je.getMessage());
        }
		return id;
    }


	public static void main(String[] args) {
		ZabbixMonitoring zabbix_obj = new ZabbixMonitoring();
		String zabi = zabbix_obj.connect();
		System.out.println(zabi);

		//zabbix_obj.hostgroups_list(zabi);
	    //int hg_id = zabbix_obj.hostgroups_list(zabi, "Scalair scaling group");	
		//System.out.println("hg_id  " + hg_id);

		//zabbix_obj.hosts_list(zabi);
		////int arr[] = zabbix_obj.hosts_list(zabi);
		//ArrayList<Integer> arr = zabbix_obj.hosts_list(zabi, "Scalair scaling group");
		//for(int i=0;i<arr.size();i++) {//length is the property of array, size of arraylist  
		//System.out.println(arr.get(i));  }
		
		//zabbix_obj.item_cpu_idle(zabi);
		Double xy = zabbix_obj.item_cpu_idle(zabi, "Scalair scaling group");
		System.out.println("ouput " + xy);

		//zabbix_obj.host_create(zabi, "node1", "172.16.225.76", 10050, "Scalair scaling group", "Scalair Template OS Linux");
		//int x = zabbix_obj.get_template(zabi, "Scalair Template OS Linux");
		//zabbix_obj.host_delete(zabi, "172.16.225.76");
		//int y = zabbix_obj.get_host_by_ip(zabi, "172.16.225.76");
		//System.out.println(y);
	}	  
}
