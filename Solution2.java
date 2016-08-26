import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/*******************************************************************************
 * Copyright 2010 Cees De Groot, Alex Boisvert, Jan Kotek
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

public class Solution2
{
	static Scanner sc = null;
	
	static String tableName;
	static String line = null;
	static HashMap<String, Serializable> hp = new HashMap<String, Serializable>();
	static HashMap<String, BPTree<?, ?>> btree = new HashMap<String, BPTree<?, ?>>();	
	
	static HashMap<String, BPTree<?, ?>> btree_attr = new HashMap<String, BPTree<?, ?>>();	
	public static void main(String arg[]) throws IOException
	{
		sc = new Scanner(System.in);
		boolean flag = false;
		sc.useDelimiter(System.getProperty("line.separator"));
		load_all_keys();
		
		//load_all_record_hashmap();
		
		/*
		for (Map.Entry entry : btree.entrySet()) 
		{
		    BPTree t1 = (BPTree) entry.getValue();
		    StringWriter st = new StringWriter();
			t1.printXml(st);
			System.out.println(entry.getKey());
			System.out.println(st);
		   
		}
		
		*/
		System.out.println(hp);
		System.out.println("BTREES : ");
		System.out.println(btree.get("students"));
		
		
		BPTree t = btree.get("students");
		StringWriter st = new StringWriter();
		t.printXml(st);
		System.out.println(st);
		//System.out.println(hp.get("movies").toString().contains("123"));
		System.out.println("Welcome to IMDB database \nEnter your choice please:");
		
		do
		{
			System.out.println("1. Create a table");
			System.out.println("2. Delete a table");
			System.out.println("3. List all tables");
			System.out.println("4. Show any table to browse");
			System.out.println("5. SELECT");
			System.out.println("6. DELETE");
			System.out.println("7. INSERT");
			System.out.println("8. UPDATE");
			
			System.out.println("9.SELECT (INDEX)");
			System.out.println("10. CREATE B+ TREE using ANY attribute");
			
			
			System.out.println("13. Show B+ tree for any particular table");
			System.out.println("99. exit");
			String choice_str = sc.next();
			if(!choice_str.matches("[-+]?\\d*\\.?\\d+"))
	        {
	        	choice_str = "999";
	        }
			if(choice_str.equals(""))
				choice_str = "999";
			int choice = Integer.parseInt(choice_str);
			
			switch(choice)
			{
				case 1: create_table();
						break;
				case 2: delete_table();
						break;
				case 3: list_all_tables();
						break;
				case 4: show_table();
						break;
				case 5: select();
						break;
				case 6: delete_query();
						break;
				case 7: insert();
						break;
				case 8: update();
						break;
				case 9: select_index();
						break;
				case 10: any_attr();
						break;	
				case 13: show_tab_btree();
						break;
				case 99: flag = true;
						System.out.println("Exiting");
						try {
							TimeUnit.SECONDS.sleep(1);
							System.out.println("..........");
							TimeUnit.SECONDS.sleep(1);
							System.out.println("Thanks for visiting our application");
							TimeUnit.SECONDS.sleep(1);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						break;
				default:System.out.println("\nEnter correct choice");
						//clear_screen();
						break;
			}
			//clear_screen();
		}
		while(flag == false);
	}
	
	private static void any_attr() throws IOException 
	{
		// btree_attr
		
		System.out.println("Enter table for index creattion");
		String tab_name = sc.next();
		
		File f= new File(tab_name+".txt");
		if(!f.exists())
		{
			System.out.println("Table does not exists for index creation!");
			return;
		}
		
		System.out.println("Enter attr name");
		String attr = sc.next();
		
		FileInputStream fis = new FileInputStream(f);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		
		String schema = br.readLine();
		String pk = br.readLine();
		
		String[] temp = schema.split(",");
 		//int pk_pos = Integer.parseInt(pk.split(",")[1]);
		
		if(!schema.contains(attr))
		{
			System.out.println("Enter correct attr name!!");
			return;
		}
		int attr_pos=0;
		
		for(String str : temp)
		{
			if(attr.equals(str.split(":")[0]))
				break;
			
			attr_pos++;
		}
		String line;
		int cnt  = 3;
		BPTree bp = new BPTree<>();
		while((line = br.readLine()) != null)
		{
			bp.put(line.split(",")[attr_pos], cnt++);
		}
		
		btree_attr.put(tab_name, bp);
		
		
		StringWriter out = new StringWriter();
		bp.printXml(out);
		System.out.println(out);
		
	}

	private static void update() 
	{
		System.out.println("Enter your query");
		String query = sc.next();
		
		
		LinkedHashMap<String,String> hp=new LinkedHashMap<String,String>();
		ArrayList<String> set_attr=new ArrayList<String>();
		ArrayList<String> set_val=new ArrayList<String>();
		ArrayList<String> where_attr=new ArrayList<String>();
		ArrayList<String> where_val=new ArrayList<String>();
		ArrayList<String> where_cond=new ArrayList<String>();
		// parts[0] - update movie
		// parts[1] - name=nikhil where id=1
		String[] parts = query.split("set");	
		if(parts[0].equals(query))
		{
			System.out.println("There is no SET!");
			return;
		}
		// part1[0] - update
		// part1[1] - movie
		String[] part1=parts[0].split(" ");
		if(part1.length==1){
			System.out.println("Table name not provided!");
			return;
		}
		String tab_name=part1[1].trim();
		System.out.println("tab_name :" + tab_name);
		File fp = new File(tab_name.trim() + ".txt");
		System.out.println("fp update: " + fp);
        if(!fp.exists())
        {
            System.out.println("\nTable does not exist!");
            clear_screen();
            return;
        }
        
        if(parts.length == 1){
        	System.out.println("error!");
        	clear_screen();
        	return;
        }
        if(!parts[1].contains("where"))
        {
        	String attname=null,attval=null;
        	boolean c=false;
        	ArrayList<String> attr=new ArrayList<String>();
        	ArrayList<String> va=new ArrayList<String>();
        	if(!parts[1].contains(","))
        	{
        		parts[1]=parts[1].trim();
        		attname=parts[1].split("=")[0];
        		attval=parts[1].split("=")[1];
        	}
        	else
        	{
        		c=true;
        		String[] v=parts[1].split(",");
        		for(String s:v)
        		{
        			s=s.trim();
        			attr.add(s.split("=")[0]);
        			va.add(s.split("=")[1]);
        		}
        	}
        	FileInputStream fs;
        	try {
    			fs = new FileInputStream(tab_name+".txt");
    			BufferedReader br = new BufferedReader(new InputStreamReader(fs));
    	    	
    			String schema = br.readLine();
    	    	String pk = br.readLine();
    	    	ArrayList<String> type_ls=new ArrayList<String>();
    	    	ArrayList<Integer> whr=new ArrayList<Integer>();
    	    	String[] str=schema.split(",");
    	    	//primary key updation validation for update of a single attribute
    	    	for(String s:str)
    	    	{
    	    		type_ls.add(s.split(":")[1]);
    	    	}
    	    	ArrayList<String> attr_ls = new ArrayList<String>();
    	    	for(String s : str)
    	    	{
    	    		attr_ls.add(s.split(":")[0]);
    	    	}
    	    	
    	    	if(c==false)
    	    	{
    	    	if(pk.split(",")[0].equals(attname))
	        	{
	        		System.out.println("\nSorry..you can not update primary key of the table");
	        		clear_screen();
	        		br.close();
	        		return;
	        	}
    	    	}
    	    	else
    	    	{
    	    		for(String s:attr)
    	    		{
    	    			if(pk.split(",")[0].equals(s))
    		        	{
    		        		System.out.println("\nSorry..you can not update primary key of the table");
    		        		clear_screen();
    		        		br.close();
    		        		return;
    		        	}
    	    		}
    	    	}
    	    	int count=0,ct=0;
    	    	if(c==false)
    	    	{
    	    		for(String st:attr_ls)
    	    		{
    	    			if(st.equals(attname))
    	    				break;
    	    			else 
    	    				count++;
    	    		}
    	    	}
    	    	else
    	    	{
    	    		for(String s:attr)
    	    		{
    	    			ct=0;
    	    			for(String sr:attr_ls)
    	    			{
    	    				if(s.equals(sr))
    	    				{
    	    					whr.add(ct);
    	    					break;
    	    				}
    	    				else
    	    					ct++;
    	    			}
    	    		}
    	    	}
    	    	
    	    	String input = "";
            	input += schema + System.lineSeparator();
            	input += pk + System.lineSeparator();	
    	    	String line=null;	
    	    	int k;
    	    	int j=0;
    	    	while((line=br.readLine())!=null)
    	    	{
    	    		if(!line.equals("#"))
    	    		{
    	    			String[] values=line.split(",");
    	    		
    	    			if(c==false)
    	    			{
    	    				for(int i=0;i<values.length;i++)
    	    				{
    	    				if(i==count)
    	    				{
    	    					values[i]=attval;
    	    					input+=values[i];
    	    					if(i!=values.length-1)
    	    					  input+=",";
    	    				}
    	    				else
    	    				{
    	    					input+=values[i];
    	    					if(i!=values.length-1)
    	    					  input+=",";
    	    				}
    	    			}
    	    			input+=System.lineSeparator();
    	    		}
    	    		else
    	    		{
    	    			k=0;
		    			for(j=0;j<whr.size();j++)
	    				{
		    			while(k<values.length)
		    			{
		    					    			
		    			    		    					
		    					  if(k==whr.get(j))
		    					  {
		    								values[k]=va.get(j);
		    								input+=values[k];
		    								//System.out.println("input "+input);
		    								if(k!=values.length-1)
		    									input+=",";
		    								    //System.out.println("input "+input);
		    								k++;
		    								
		    								break;
		    						}
		    						
		    					
		    					else
		    					{
		    						input+=values[k];
		    						//System.out.println("input "+input);
		    						if(k!=values.length-1)
		    							input+=",";
		    						  //System.out.println("input "+input);
		    						k++;
		    					}
		    				//}
		    			}
		    			}	
		    			    if(j==whr.size()&&k<values.length)
		    			    {
		    			    	while(k<values.length)
		    			    	{
		    			    		input+=values[k];
		    			    		if(k!=values.length-1)
		    							input+=",";
		    			    		k++;
		    			    	}
		    			    }
		    				input+=System.lineSeparator();	
			    			//System.out.println("input "+input);

    	    			}
    	    		}
    	    		else
    	    		{
    	    			input+="#";
    	    			input+=System.lineSeparator();
    	    		}
    	    	}
    	    	// end-while
    	    	
    	    	FileOutputStream os = new FileOutputStream(fp);
    	        os.write(input.getBytes());

    	        br.close();
    	        os.close();
    	        
    	        display_all_main(tab_name);
           }
        	catch (IOException e) 
    		{
    			e.printStackTrace();
    		}
        }
        else{
		// parts[1] -name=nikhil where id=1
        String[] part2=parts[1].trim().split("where");
        // part2[0] - name=nikhil ;
        // part2[1] -  id=1 
        //System.out.println(part2[0]);
        //System.out.println(part2[1]);
        if(part2.length>3){
        	System.out.println("The query you have entered is incorrect!");
        	clear_screen();
        	return;
        }
        //part2[1]- id=1
        String condition=null;
        String split_cond=null;
        boolean andor=false;
        String pr1[] = new String[2];
        if(part2[1].contains("AND")||part2[1].contains("OR"))
        {
        	andor=true;
        	if(part2[1].contains("AND"))
        	{
        	    split_cond="AND";
        		String[] s=part2[1].split("AND");
        	    for(int i=0;i<s.length;i++)
        	    {
        	    	s[i]=s[i].trim();
        	    	if(s[i].contains("="))
        	    	{
        	    		where_attr.add(s[i].split("=")[0]);
        	    		where_val.add(s[i].split("=")[1]);
        	    		where_cond.add("=");
        	    	}
        	    	else if(s[i].contains("<"))
        	    	{
        	    		where_attr.add(s[i].split("<")[0]);
        	    		where_val.add(s[i].split("<")[1]);
        	    		where_cond.add("<");
        	    	}
        	    	else if(s[i].contains(">"))
        	    	{
        	    		where_attr.add(s[i].split(">")[0]);
        	    		where_val.add(s[i].split(">")[1]);
        	    		where_cond.add(">");
        	    	}
        	    }
        	    
        	}
        	
        	if(part2[1].contains("OR"))
        	{
        	    split_cond="OR";
        		String[] s=part2[1].split("OR");
        	    for(int i=0;i<s.length;i++)
        	    {
        	    	s[i]=s[i].trim();
        	    	if(s[i].contains("="))
        	    	{
        	    		where_attr.add(s[i].split("=")[0]);
        	    		where_val.add(s[i].split("=")[1]);
        	    		where_cond.add("=");
        	    	}
        	    	else if(s[i].contains("<"))
        	    	{
        	    		where_attr.add(s[i].split("<")[0]);
        	    		where_val.add(s[i].split("<")[1]);
        	    		where_cond.add("<");
        	    	}
        	    	else if(s[i].contains(">"))
        	    	{
        	    		where_attr.add(s[i].split(">")[0]);
        	    		where_val.add(s[i].split(">")[1]);
        	    		where_cond.add(">");
        	    	}
        	    }
        	}
        }
        else
        {
        if(part2[1].contains("="))
        {
        	System.out.println("part2[1] "+part2[1]);
        	pr1[0]=part2[1].split("=")[0];
        	pr1[1]=part2[1].split("=")[1];
        	condition="=";
        }
        else if(part2[1].contains("<"))
        {
        	pr1[0]=part2[1].split("<")[0];
        	pr1[1]=part2[1].split("<")[1];
        	condition="<";
        }
        else if(part2[1].contains(">"))
        {
        	pr1[0]=part2[1].split(">")[0];
        	pr1[1]=part2[1].split(">")[1];
        	condition=">";
        }
        }
        if(pr1.length>2){
        	System.out.println("The query you have entered is incorrect!");
        	clear_screen();
        	return;
        }
        boolean contains=false;
        System.out.println("pr1[0] "+pr1[0]);
        System.out.println("pr1[1] "+pr1[1]);
        String where_attrname=pr1[0];
		String where_attrvalue=pr1[1];
		String set_attrname=null;
		String set_attrvalue=null;
		//part2[0]- name=nikhil
		//part2[0]- name=nikhil,year=2000
		if(part2[0].contains(",")){
			//pr[0]- name=nikhil
			//pr[1]- year=2000
			contains=true;
			String[] pr=part2[0].split(",");
			System.out.println("pr");
			for(String str : pr)
				System.out.println(" " + str);
			//LinkedHashMap<String,String> hp=new LinkedHashMap<String,String>();
			for(int i=0;i<pr.length;i++){
				pr[i] = pr[i].trim();
				System.out.println("pr[i]"+pr[i]);
				String[] p=pr[i].split("=");
				set_attr.add(p[0]);
				set_val.add(p[1]);
			}
			for(String s:set_val)
			  System.out.println("setval "+s);
			
		}
		else{
		String[] pr2=part2[0].split("=");
        if(pr2.length>2){
        	System.out.println("The query you have entered is incorrect!");
        	clear_screen();
        	return;
        }
        
		set_attrname=pr2[0];
		set_attrvalue=pr2[1];
		}
		FileInputStream fis;
		try {
			fis = new FileInputStream(tab_name+".txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
	    	
			String schema = br.readLine();
	    	String pk = br.readLine();
	    	ArrayList<String> type_ls=new ArrayList<String>();
	    	String[] str=schema.split(",");
	    	//primary key updation validation for update of a single attribute
	    	for(String s:str)
	    	{
	    		type_ls.add(s.split(":")[1]);
	    	}
	    	ArrayList<String> attr_ls = new ArrayList<String>();
	    	for(String s : str)
	    	{
	    		attr_ls.add(s.split(":")[0]);
	    	}
	    	if(contains==false)
	    	{
	    		if(pk.split(",")[0].equals(set_attrname))
	        	{
	        		System.out.println("\nSorry..you can not update primary key of the table");
	        		clear_screen();
	        		br.close();
	        		return;
	        	}
	    	}
	    	//primary key updation validation for update of multiple attributes
	    	else
	    	{
	    		for(String s:set_attr)
	    		{
	    			if(pk.split(",")[0].equals(s))
		        	{
		        		System.out.println("\nSorry..you can not update primary key of the table");
		        		clear_screen();
		        		br.close();
		        		return;
		        	}
	    		}
	    	}
	    	String[] temp = schema.split(",");
	    	
	    	//type checking for update of a single attribute
	    	if(contains==false)
	    	{
	    		boolean attr_flag = false;
	        	int attr_pos = 0;
	        	
	        	String attr_type = null;        	
	        	for(String sch : temp)
	        	{
	        		if(sch.split(":")[0].equals(set_attrname))
	        		{
	        			// when attribute name found, break and search 
	        			// for that attribute name over entered PK
	        			
	        			// attribute type to check for updated value
	        			attr_type =sch.split(":")[1]; 
	        			
	        			attr_flag = true;
	        			break;
	        		}
	        		attr_pos++;
	        	}
	        	if(attr_flag  == false)
	        	{
	        		attr_flag = false;
	        		System.out.println("\nEnter appropriate attribute name");
	        		clear_screen();
	        		br.close();
	        		return;
	        	}
	        	
	        		        	
	        	// To verify type while updating
	        	
	        	// check for correct update value
	        	boolean type_check = false;
	        	if(!set_attrvalue.equals("null"))
	        	{
	        		if(attr_type.equals("string"))
	        		{
	        			if(set_attrvalue.matches(".*\\d+.*"))
	        			{
	        				// contains number
	        				type_check = true;
	        			}
	        		}
	        		/*else if(attr_type.equals("int"))
	        		{
	        			try
	        			{
	        				Integer.parseInt(set_attrvalue);
	        			}	
	        			catch(NumberFormatException e)
	        			{
	        				//not a int
	        				type_check = true;
	        			}
	        		}*/
	        		else if(attr_type.equals("float"))
	        		{
	        			try
	        			{
	        				Double.parseDouble(set_attrvalue);
	        			}
	        			catch(NumberFormatException e)
	        			{
	        				//not a double
	        				type_check = true;
	        			}
	        		}
	        		else if(attr_type.equals("double"))
	        		{
	        			try
	        			{
	        				Float.parseFloat(set_attrvalue);
	        			}
	        			catch(NumberFormatException e)
	        			{
	        				//not a double
	        				type_check = true;
	        			}
	        		}
	        	}
	        	if(type_check == true)
	        	{
	        		System.out.println("Enter correct type for " + set_attrvalue);
	        		br.close();
	        		return;
	        	}
	        	
	    	}
	    	
	    	//type check for update of multiple attributes
	    	else
	    	{
	    		boolean attr_flag = false;
	        	//LinkedHashMap<String,String> h=new LinkedHashMap<String,String>();	        	
	        	ArrayList<String> att_type=new ArrayList<String>();
	    		String attr_type = null;        	
	    		for(String s:set_attr)
        	    {
	    		for(String sch : temp)
	        	{   
	        		      if(sch.split(":")[0].equals(s))
	        		      {
	        			    // when attribute name found, break and search 
	        			    // for that attribute name over entered PK
	        			
	        			    // attribute type to check for updated value
	        			    attr_type =sch.split(":")[1]; 
	        			    att_type.add(attr_type);
	        			    attr_flag = true;
	        		    	break;
	        		      }
	        	    }
	        	}
	        	if(attr_flag  == false)
	        	{
	        		attr_flag = false;
	        		System.out.println("\nEnter appropriate attribute name");
	        		clear_screen();
	        		br.close();
	        		return;
	        	}
	        	
	        		        	
	        	// To verify type while updating
	        		
	        	
	        	// check for correct update value
	        	boolean type_check;
	        	for(int i=0;i<set_val.size();i++){
	        		type_check = false;
	        		if(!set_val.get(i).equals("null"))
	        	    {
	        		  
	        			if(att_type.get(i).equals("string"))
	        		    {
	        			if(set_val.get(i).matches(".*\\d+.*"))
	        			{
	        				// contains number
	        				type_check = true;
	        			}
	        		}
	        		else if(att_type.get(i).equals("int"))
	        		{
	        			try
	        			{
	        				Integer.parseInt(set_val.get(i));
	        			}	
	        			catch(NumberFormatException e)
	        			{
	        				//not a int
	        				type_check = true;
	        			}
	        		}
	        		else if(att_type.get(i).equals("float"))
	        		{
	        			try
	        			{
	        				Double.parseDouble(set_val.get(i));
	        			}
	        			catch(NumberFormatException e)
	        			{
	        				//not a double
	        				type_check = true;
	        			}
	        		}
	        		else if(att_type.get(i).equals("double"))
	        		{
	        			try
	        			{
	        				Float.parseFloat(set_val.get(i));
	        			}
	        			catch(NumberFormatException e)
	        			{
	        				//not a double
	        				type_check = true;
	        			}
	        		}
	        	}
	        		  
	        	  	        	
	        	if(type_check == true)
	        	{
	        		System.out.println("Enter correct type for " + set_val.get(i));
	        		br.close();
	        		return;
	        	}
	        	}
	    	}
	    	int where_attrpos=0,set_attrpos=0,wh_pos=0;
	    	ArrayList<Integer> where_pos=new ArrayList<Integer>();
	    	//System.out.println("attr_lls : "+attr_ls);
	    	System.out.println("where_attrname "+where_attrname);
	    	if(andor==false)
	    	{
	    	for(String attr_name:attr_ls){
	    		
	    		if(!attr_name.equals(where_attrname.trim()))
	    			where_attrpos++;
	    		else
	    			break;
	    	}
	    	}
	    	if(where_attrpos==temp.length){
	    		System.out.println("The attribute was not found");
	    		clear_screen();
	    		return;
	    	}
	    	if(andor==true)
	    	{
	    	if(split_cond.equals("AND")||split_cond.equals("OR"))
	    	{
	    		for(String s:where_attr)
	    		{
	    			wh_pos=0;
	    			for(String st:attr_ls)
	    			{
	    				if(!s.equals(st))
	    					wh_pos++;
	    				else
	    				{
	    					where_pos.add(wh_pos);
	    					break;
	    				}
	    			}
	    		}
	    			
	    	}
	    	}
	    	if(wh_pos==temp.length){
	    		System.out.println("The attribute was not found");
	    		clear_screen();
	    		return;
	    	}
	    	//if query has only single attribute to be updated
	    	if(contains==false){
	    	for(String attr_name:attr_ls){
	    		if(!attr_name.equals(set_attrname.trim()))
	    			set_attrpos++;
	    		else
	    			break;
	    	}
	    	String input = "";
        	input += schema + System.lineSeparator();
        	input += pk + System.lineSeparator();	
	    	String line=null;
	    	boolean found=false;
	    	boolean where=false;
	    	int record_flag,p;
	    	String opr=null,val=null;
	    	while((line=br.readLine())!=null){
	    		String[] values=line.split(",");
	    		//System.out.println(where_attrpos);
	    		record_flag=0;
	    		if(andor==true)
	    		{
	    			System.out.println("where_pos size "+where_pos);
	    				for(int i=0;i<where_pos.size();i++)
	    				{
	    					p=where_pos.get(i);
	    					opr=where_cond.get(i);
	    					//System.out.println("where_pos size "+where_pos.size());
	    					//System.out.println("val "+values[where_pos.get(i)]);
	    					val=values[where_pos.get(i)];
	    					if(!val.equals("null"))
	    	        	    {
	    						if(opr.equals("<"))
	    						{
	    	        	    		if(val.matches(".*\\d+.*"))
	    	        	    		{
	    	        	    		   if(type_ls.get(i).equals("int"))
	    	        	    		   {
	    	        	    				//int
	    	        	    				int val1 =  Integer.parseInt(val);
	    			        	    		int val2 = Integer.parseInt(where_val.get(i)); 
	    			        	    		if(val1 < val2)
	    			        	    		{
	    			        	    			record_flag++;
	    			        	    		}
	    	        	    			}
	    	        	    		   else
	    	        	    			{
	    	        	    				//float
	    	        	    				float val1 = Float.parseFloat(val);
	    	        	    				float val2 = Float.parseFloat(where_val.get(i));
	    	        	    				if(val1 < val2)
	    			        	    		{
	    			        	    			record_flag++;
	    			        	    		}
	    	        	    			} 
	    	        	    		}
	    	        	    		   else
	    	           	    		   {
	    	           	    			System.out.println("\n< or > ops does not work for strings");
	    	           	    			br.close();
	    	           	    			return;
	    	           	    		   }	        	    	
	    	           	    	      }
	    						
	    						else if(opr.equals(">"))
	    						{
	    	        	    		if(val.matches(".*\\d+.*"))
	    	        	    		{
	    	        	    			//good attr for condition 
	    	        	    			try
	    	        	    			{
	    	        	    				//int
	    	        	    				int val1 =  Integer.parseInt(val);
	    			        	    		int val2 = Integer.parseInt(where_val.get(i)); 
	    			        	    		if(val1 > val2)
	    			        	    		{
	    			        	    			record_flag++;
	    			        	    		}
	    	        	    			}
	    	        	    			catch(NumberFormatException e)
	    	        	    			{
	    	        	    				//float
	    	        	    				float val1 = Float.parseFloat(val);
	    	        	    				float val2 = Float.parseFloat(where_val.get(i));
	    	        	    				if(val1 > val2)
	    			        	    		{
	    			        	    			record_flag++;
	    			        	    		}
	    	        	    			}    	    			
	    	        	    		}	
	    							
	    	        	    		else
	    	        	    		{
	    	        	    			System.out.println("\n< or > ops does not work for strings");
	    	        	    			br.close();
	    	        	    			return;
	    	        	    		}
	    	        	    	}
	    	        	    		
	    						else if(opr.equals("="))
	    	        	    	{
	    	        	    		if(val.equals(where_val.get(i)))
	    	        	    		{
	    	        	    			record_flag++;
	    	        	    		}
	    	        	    	}
	    	        	    }
	    							
	    	        	    		
	    				}
	    	        	    		
	    				if(split_cond.equals("AND")&&record_flag==where_cond.size())
	    				{
	    					where=true;
	    	    			for(int i=0;i<values.length;i++){
	    	    				if(i==set_attrpos){
	    	    					values[i]=set_attrvalue.trim();
	    	    					input+=values[i];
	    	    					if(i!=values.length-1)
	    	    					  input+=",";
	    	    					found=true;
	    	    				}
	    	    				else{
	    	    					input+=values[i];
	    	    					if(i!=values.length-1)
	    		    				  input+=",";
	    	    				}
	    	    			}
	    	    			input+=System.lineSeparator();
	    					    
	    				}
	    				if(split_cond.equals("AND")&&record_flag!=where_cond.size())
	    				{
	    	    			for(int i=0;i<values.length;i++){
	    	    				input+=values[i];
	    	    				if(i!=values.length-1)
	    	    					 input+=",";
	    	    			}
	    	    			input+=System.lineSeparator();	
	    	    		}
	    				
	    				if(split_cond.equals("OR")&&record_flag!=0)
	    				{
	    					where=true;
	    	    			for(int i=0;i<values.length;i++){
	    	    				if(i==set_attrpos){
	    	    					values[i]=set_attrvalue.trim();
	    	    					input+=values[i];
	    	    					if(i!=values.length-1)
	    	    					  input+=",";
	    	    					found=true;
	    	    				}
	    	    				else{
	    	    					input+=values[i];
	    	    					if(i!=values.length-1)
	    		    				  input+=",";
	    	    				}
	    	    			}
	    	    			input+=System.lineSeparator();
	    					    
	    				}
	    				
	    				if(split_cond.equals("OR")&&record_flag==0)
	    				{
	    	    			for(int i=0;i<values.length;i++){
	    	    				input+=values[i];
	    	    				if(i!=values.length-1)
	    	    					 input+=",";
	    	    			}
	    	    			input+=System.lineSeparator();	
	    	    		}
	    			
	    		}
	    		
	    		else{
	    		if(condition.equals("="))
	    		{
	    		if(values[where_attrpos].equals(where_attrvalue)){
	    		
	    		    where=true;
	    			for(int i=0;i<values.length;i++){
	    				if(i==set_attrpos){
	    					values[i]=set_attrvalue.trim();
	    					input+=values[i];
	    					if(i!=values.length-1)
	    					  input+=",";
	    					found=true;
	    				}
	    				else{
	    					input+=values[i];
	    					if(i!=values.length-1)
		    				  input+=",";
	    				}
	    			}
	    			input+=System.lineSeparator();
	    		}
	    		else{
	    			for(int i=0;i<values.length;i++){
	    				input+=values[i];
	    				if(i!=values.length-1)
	    					 input+=",";
	    			}
	    			input+=System.lineSeparator();	
	    		}
	    	  }
	    		else if(condition.equals("<"))
	    		{
	    			if(type_ls.get(where_attrpos).equals("int"))
	    			{
	    			    
	    				if(Integer.parseInt(values[where_attrpos])<Integer.parseInt(where_attrvalue)){
	    	    		
		    		    where=true;
		    			for(int i=0;i<values.length;i++){
		    				if(i==set_attrpos){
		    					values[i]=set_attrvalue.trim();
		    					input+=values[i];
		    					if(i!=values.length-1)
		    					  input+=",";
		    					found=true;
		    				}
		    				else{
		    					input+=values[i];
		    					if(i!=values.length-1)
			    				  input+=",";
		    				}
		    			}
		    			input+=System.lineSeparator();
		    		}
		    		else{
		    			for(int i=0;i<values.length;i++){
		    				input+=values[i];
		    				if(i!=values.length-1)
		    					 input+=",";
		    			}
		    			input+=System.lineSeparator();	
		    		}
	    		}
	    			if(type_ls.get(where_attrpos).equals("float"))
                    {
	    			    
	    				if(Float.parseFloat(values[where_attrpos])<Float.parseFloat(where_attrvalue)){
	    	    		
		    		    where=true;
		    			for(int i=0;i<values.length;i++){
		    				if(i==set_attrpos){
		    					values[i]=set_attrvalue.trim();
		    					input+=values[i];
		    					if(i!=values.length-1)
		    					  input+=",";
		    					found=true;
		    				}
		    				else{
		    					input+=values[i];
		    					if(i!=values.length-1)
			    				  input+=",";
		    				}
		    			}
		    			input+=System.lineSeparator();
		    		}
		    		else{
		    			for(int i=0;i<values.length;i++){
		    				input+=values[i];
		    				if(i!=values.length-1)
		    					 input+=",";
		    			}
		    			input+=System.lineSeparator();	
		    		}
	    		}
	    		
	    			if(type_ls.get(where_attrpos).equals("string"))	
	    			{
	    				System.out.println("Wrong type!");
	    				return;
	    			}
	    		
	    		}	
	    		
	    		else if(condition.equals(">"))
	    		{
	    			if(type_ls.get(where_attrpos).equals("int"))
	    			{
	    			    
	    				if(Integer.parseInt(values[where_attrpos])>Integer.parseInt(where_attrvalue)){
	    	    		
		    		    where=true;
		    			for(int i=0;i<values.length;i++){
		    				if(i==set_attrpos){
		    					values[i]=set_attrvalue.trim();
		    					input+=values[i];
		    					if(i!=values.length-1)
		    					  input+=",";
		    					found=true;
		    				}
		    				else{
		    					input+=values[i];
		    					if(i!=values.length-1)
			    				  input+=",";
		    				}
		    			}
		    			input+=System.lineSeparator();
		    		}
		    		else{
		    			for(int i=0;i<values.length;i++){
		    				input+=values[i];
		    				if(i!=values.length-1)
		    					 input+=",";
		    			}
		    			input+=System.lineSeparator();	
		    		}
	    		}
	    			if(type_ls.get(where_attrpos).equals("float"))
                    {
	    			    
	    				if(Float.parseFloat(values[where_attrpos])>Float.parseFloat(where_attrvalue)){
	    	    		
		    		    where=true;
		    			for(int i=0;i<values.length;i++){
		    				if(i==set_attrpos){
		    					values[i]=set_attrvalue.trim();
		    					input+=values[i];
		    					if(i!=values.length-1)
		    					  input+=",";
		    					found=true;
		    				}
		    				else{
		    					input+=values[i];
		    					if(i!=values.length-1)
			    				  input+=",";
		    				}
		    			}
		    			input+=System.lineSeparator();
		    		}
		    		else{
		    			for(int i=0;i<values.length;i++){
		    				input+=values[i];
		    				if(i!=values.length-1)
		    					 input+=",";
		    			}
		    			input+=System.lineSeparator();	
		    		}
	    		}
	    		
	    			if(type_ls.get(where_attrpos).equals("string"))	
	    			{
	    				System.out.println("Wrong type!");
	    				return;
	    			}
	    		
	    		}	
	    		
	    	}	
	    	}
	    	if(where==false)
	    	{
	    		System.out.println("The where attribute was not found");
	    		return;
	    	}
	    	if(found==false){
	    		System.out.println("The specified set attribute's value was not found!");
	    		clear_screen();
	    		return;
	    	}
	    	else{
	    		System.out.println("The record has been updated successfully!");
	    		clear_screen();
	    	}
	    	FileOutputStream os = new FileOutputStream(fp);
	        os.write(input.getBytes());
	        os.close();
	      }
	    	//if query has multiple attributes to be updated
	    	else{
	    		
	    		ArrayList<Integer> att_pos=new ArrayList<Integer>();
	    		//LinkedHashMap<String,Integer> hmap=new LinkedHashMap<String,Integer>();
	    		//System.out.println("hp "+hp);
	    		for (String s: set_attr)
	    		{
	    			
	    			int attr_pos=0;
	    	
	    			for(String attr_name:attr_ls)
	    			{//System.out.println("attrname "+attr_name);
	    			 //System.out.println("key "+key);
	    				if(!attr_name.equals(s))
	    					
	    					attr_pos++;
	    				else{
	    					att_pos.add(attr_pos);
	    					break;
	    				}
	    			}
	    		}
	    		
	    		//System.out.println("hmap"+hmap);
	    		String input = "";
	        	input += schema + System.lineSeparator();
	        	input += pk + System.lineSeparator();	
		    	String line=null;
		    	boolean found=false;
		    	boolean where=false;
		    	int record_flag,p;
		    	String opr=null,val=null;
		    	int k,j;
		    	while((line=br.readLine())!=null)
		    	{
		    		String[] values=line.split(",");
		    		record_flag=0;
		    		//System.out.println(where_attrpos);
		    		if(andor==true)
		    		{
		    			System.out.println("where_pos size "+where_pos);
	    				for(int i=0;i<where_pos.size();i++)
	    				{
	    					p=where_pos.get(i);
	    					opr=where_cond.get(i);
	    					//System.out.println("where_pos size "+where_pos.size());
	    					//System.out.println("val "+values[where_pos.get(i)]);
	    					val=values[where_pos.get(i)];
	    					if(!val.equals("null"))
	    	        	    {
	    						if(opr.equals("<"))
	    						{
	    	        	    		if(val.matches(".*\\d+.*"))
	    	        	    		{
	    	        	    		   if(type_ls.get(i).equals("int"))
	    	        	    		   {
	    	        	    				//int
	    	        	    				int val1 =  Integer.parseInt(val);
	    			        	    		int val2 = Integer.parseInt(where_val.get(i)); 
	    			        	    		if(val1 < val2)
	    			        	    		{
	    			        	    			record_flag++;
	    			        	    		}
	    	        	    			}
	    	        	    		   else
	    	        	    			{
	    	        	    				//float
	    	        	    				float val1 = Float.parseFloat(val);
	    	        	    				float val2 = Float.parseFloat(where_val.get(i));
	    	        	    				if(val1 < val2)
	    			        	    		{
	    			        	    			record_flag++;
	    			        	    		}
	    	        	    			} 
	    	        	    		}
	    	        	    		   else
	    	           	    		   {
	    	           	    			System.out.println("\n< or > ops does not work for strings");
	    	           	    			br.close();
	    	           	    			return;
	    	           	    		   }	        	    	
	    	           	    	      }
	    						
	    						else if(opr.equals(">"))
	    						{
	    	        	    		if(val.matches(".*\\d+.*"))
	    	        	    		{
	    	        	    			//good attr for condition 
	    	        	    			try
	    	        	    			{
	    	        	    				//int
	    	        	    				int val1 =  Integer.parseInt(val);
	    			        	    		int val2 = Integer.parseInt(where_val.get(i)); 
	    			        	    		if(val1 > val2)
	    			        	    		{
	    			        	    			record_flag++;
	    			        	    		}
	    	        	    			}
	    	        	    			catch(NumberFormatException e)
	    	        	    			{
	    	        	    				//float
	    	        	    				float val1 = Float.parseFloat(val);
	    	        	    				float val2 = Float.parseFloat(where_val.get(i));
	    	        	    				if(val1 > val2)
	    			        	    		{
	    			        	    			record_flag++;
	    			        	    		}
	    	        	    			}    	    			
	    	        	    		}	
	    							
	    	        	    		else
	    	        	    		{
	    	        	    			System.out.println("\n< or > ops does not work for strings");
	    	        	    			br.close();
	    	        	    			return;
	    	        	    		}
	    	        	    	}
	    	        	    		
	    						else if(opr.equals("="))
	    	        	    	{
	    	        	    		if(val.equals(where_val.get(i)))
	    	        	    		{
	    	        	    			record_flag++;
	    	        	    		}
	    	        	    	}
	    	        	    }
	    							
	    	        	    		
	    				}
	    	        	    		
	    				if(split_cond.equals("AND")&&record_flag==where_cond.size())
	    				{
	    					where=true;
			    			k=0;
			    			for(j=0;j<att_pos.size();j++)
		    				{
			    			while(k<values.length)
			    			{
			    					    			
			    			    		    					
			    					  if(k==att_pos.get(j))
			    					  {
			    								values[k]=set_val.get(j).trim();
			    								input+=values[k];
			    								//System.out.println("input "+input);
			    								if(k!=values.length-1)
			    									input+=",";
			    								    //System.out.println("input "+input);
			    								k++;
			    								found=true;
			    								break;
			    						}
			    						
			    					
			    					else
			    					{
			    						input+=values[k];
			    						//System.out.println("input "+input);
			    						if(k!=values.length-1)
			    							input+=",";
			    						  //System.out.println("input "+input);
			    						k++;
			    					}
			    				//}
			    			}
			    			}	
			    			    if(j==att_pos.size()&&k<values.length)
			    			    {
			    			    	while(k<values.length)
			    			    	{
			    			    		input+=values[k];
			    			    		if(k!=values.length-1)
			    							input+=",";
			    			    		k++;
			    			    	}
			    			    }
			    				input+=System.lineSeparator();	
				    			//System.out.println("input "+input);

	    					    
	    				}
	    				if(split_cond.equals("AND")&&record_flag!=where_cond.size())
	    				{
	    					for(int i=0;i<values.length;i++){
			    				input+=values[i];
			    				//System.out.println("input "+input);
			    				if(i!=values.length-1)
			    					 input+=",";
			    					 //System.out.println("input "+input);
			    			    
			    			}
			    			input+=System.lineSeparator();
			    			//System.out.println("input "+input);
	    	    		}
	    				
	    				if(split_cond.equals("OR")&&record_flag!=0)
	    				{
	    					where=true;
			    			k=0;
			    			for(j=0;j<att_pos.size();j++)
		    				{
			    			while(k<values.length)
			    			{
			    					    			
			    			    		    					
			    					  if(k==att_pos.get(j))
			    					  {
			    								values[k]=set_val.get(j).trim();
			    								input+=values[k];
			    								//System.out.println("input "+input);
			    								if(k!=values.length-1)
			    									input+=",";
			    								    //System.out.println("input "+input);
			    								k++;
			    								found=true;
			    								break;
			    						}
			    						
			    					
			    					else
			    					{
			    						input+=values[k];
			    						//System.out.println("input "+input);
			    						if(k!=values.length-1)
			    							input+=",";
			    						  //System.out.println("input "+input);
			    						k++;
			    					}
			    				//}
			    			}
			    			}	
			    			    if(j==att_pos.size()&&k<values.length)
			    			    {
			    			    	while(k<values.length)
			    			    	{
			    			    		input+=values[k];
			    			    		if(k!=values.length-1)
			    							input+=",";
			    			    		k++;
			    			    	}
			    			    }
			    				input+=System.lineSeparator();	
				    			//System.out.println("input "+input);

	    					    
	    				}
	    				
	    				if(split_cond.equals("OR")&&record_flag==0)
	    				{
	    					for(int i=0;i<values.length;i++){
			    				input+=values[i];
			    				//System.out.println("input "+input);
			    				if(i!=values.length-1)
			    					 input+=",";
			    					 //System.out.println("input "+input);
			    			    
			    			}
			    			input+=System.lineSeparator();
			    			//System.out.println("input "+input);
	    	    		}
	    			
		    			
		    		}
		    		else
		    		{
		    		if(condition.equals("="))
		    		{
		    		if(values[where_attrpos].equals(where_attrvalue))
		    		{	
		    			where=true;
		    			k=0;
		    			for(j=0;j<att_pos.size();j++)
	    				{
		    			while(k<values.length)
		    			{
		    					    			
		    			    		    					
		    					  if(k==att_pos.get(j))
		    					  {
		    								values[k]=set_val.get(j).trim();
		    								input+=values[k];
		    								//System.out.println("input "+input);
		    								if(k!=values.length-1)
		    									input+=",";
		    								    //System.out.println("input "+input);
		    								k++;
		    								found=true;
		    								break;
		    						}
		    						
		    					
		    					else
		    					{
		    						input+=values[k];
		    						//System.out.println("input "+input);
		    						if(k!=values.length-1)
		    							input+=",";
		    						  //System.out.println("input "+input);
		    						k++;
		    					}
		    				//}
		    			}
		    			}	
		    			    if(j==att_pos.size()&&k<values.length)
		    			    {
		    			    	while(k<values.length)
		    			    	{
		    			    		input+=values[k];
		    			    		if(k!=values.length-1)
		    							input+=",";
		    			    		k++;
		    			    	}
		    			    }
		    				input+=System.lineSeparator();	
			    			//System.out.println("input "+input);

		    			}
		    				
		    			
		    			
		    			//input+=System.lineSeparator();
		    		
	    	
		    		else
		    		{
		    			for(int i=0;i<values.length;i++){
		    				input+=values[i];
		    				//System.out.println("input "+input);
		    				if(i!=values.length-1)
		    					 input+=",";
		    					 //System.out.println("input "+input);
		    			    
		    			}
		    			input+=System.lineSeparator();
		    			//System.out.println("input "+input);
		    		
		    	}
		    	}	
		    	else if(condition.equals("<"))
		    	{
		    		if(type_ls.get(where_attrpos).equals("int"))
	    			{
		    			if(Integer.parseInt(values[where_attrpos])<Integer.parseInt(where_attrvalue))
		    			{
		    				where=true;
		    				k=0;
			    			for(j=0;j<att_pos.size();j++)
			    			{
			    				while(k<values.length)
			    				{
			    					if(k==att_pos.get(j))
			    					  {
			    								values[k]=set_val.get(j).trim();
			    								input+=values[k];
			    								//System.out.println("input "+input);
			    								if(k!=values.length-1)
			    									input+=",";
			    								    //System.out.println("input "+input);
			    								k++;
			    								found=true;
			    								break;
			    						}
			    						
			    					
			    					else
			    					{
			    						input+=values[k];
			    						//System.out.println("input "+input);
			    						if(k!=values.length-1)
			    							input+=",";
			    						  //System.out.println("input "+input);
			    						k++;
			    					}
			    				//}
			    				}
			    			}
			    			if(j==att_pos.size()&&k<values.length)
		    			    {
		    			    	while(k<values.length)
		    			    	{
		    			    		input+=values[k];
		    			    		if(k!=values.length-1)
		    							input+=",";
		    			    		k++;
		    			    	}
		    			    }
		    				input+=System.lineSeparator();	
			    			}
		    			else{
			    			for(int i=0;i<values.length;i++){
			    				input+=values[i];
			    				//System.out.println("input "+input);
			    				if(i!=values.length-1)
			    					 input+=",";
			    					 //System.out.println("input "+input);
			    			    
			    			}
			    			input+=System.lineSeparator();
			    			//System.out.println("input "+input);
			    		
			    	}
		    			}
		    		
		    		if(type_ls.get(where_attrpos).equals("float"))
	    			{
		    			if(Float.parseFloat(values[where_attrpos])<Float.parseFloat(where_attrvalue))
		    			{
		    				where=true;
		    				k=0;
			    			for(j=0;j<att_pos.size();j++)
			    			{
			    				while(k<values.length)
			    				{
			    					if(k==att_pos.get(j))
			    					  {
			    								values[k]=set_val.get(j).trim();
			    								input+=values[k];
			    								//System.out.println("input "+input);
			    								if(k!=values.length-1)
			    									input+=",";
			    								    //System.out.println("input "+input);
			    								k++;
			    								found=true;
			    								break;
			    						}
			    						
			    					
			    					else
			    					{
			    						input+=values[k];
			    						//System.out.println("input "+input);
			    						if(k!=values.length-1)
			    							input+=",";
			    						  //System.out.println("input "+input);
			    						k++;
			    					}
			    				//}
			    				}
			    			}
			    			if(j==att_pos.size()&&k<values.length)
		    			    {
		    			    	while(k<values.length)
		    			    	{
		    			    		input+=values[k];
		    			    		if(k!=values.length-1)
		    							input+=",";
		    			    		k++;
		    			    	}
		    			    }
		    				input+=System.lineSeparator();	
			    			}
		    			else{
			    			for(int i=0;i<values.length;i++){
			    				input+=values[i];
			    				//System.out.println("input "+input);
			    				if(i!=values.length-1)
			    					 input+=",";
			    					 //System.out.println("input "+input);
			    			    
			    			}
			    			input+=System.lineSeparator();
			    			//System.out.println("input "+input);
			    		
			    	}
		    			}
		    		
		    		if(type_ls.get(where_attrpos).equals("string"))	
	    			{
	    				System.out.println("Wrong type!");
	    				return;
	    			}
	    			}
		    		
		    	else if(condition.equals(">"))
		    	{
		    		if(type_ls.get(where_attrpos).equals("int"))
	    			{
		    			if(Integer.parseInt(values[where_attrpos])>Integer.parseInt(where_attrvalue))
		    			{
		    				where=true;
		    				k=0;
			    			for(j=0;j<att_pos.size();j++)
			    			{
			    				while(k<values.length)
			    				{
			    					if(k==att_pos.get(j))
			    					  {
			    								values[k]=set_val.get(j).trim();
			    								input+=values[k];
			    								//System.out.println("input "+input);
			    								if(k!=values.length-1)
			    									input+=",";
			    								    //System.out.println("input "+input);
			    								k++;
			    								found=true;
			    								break;
			    						}
			    						
			    					
			    					else
			    					{
			    						input+=values[k];
			    						//System.out.println("input "+input);
			    						if(k!=values.length-1)
			    							input+=",";
			    						  //System.out.println("input "+input);
			    						k++;
			    					}
			    				//}
			    				}
			    			}
			    			if(j==att_pos.size()&&k<values.length)
		    			    {
		    			    	while(k<values.length)
		    			    	{
		    			    		input+=values[k];
		    			    		if(k!=values.length-1)
		    							input+=",";
		    			    		k++;
		    			    	}
		    			    }
		    				input+=System.lineSeparator();	
			    			}
		    			else{
			    			for(int i=0;i<values.length;i++){
			    				input+=values[i];
			    				//System.out.println("input "+input);
			    				if(i!=values.length-1)
			    					 input+=",";
			    					 //System.out.println("input "+input);
			    			    
			    			}
			    			input+=System.lineSeparator();
			    			//System.out.println("input "+input);
			    		
			    	}
		    			}
		    		
		    		if(type_ls.get(where_attrpos).equals("float"))
	    			{
		    			if(Float.parseFloat(values[where_attrpos])>Float.parseFloat(where_attrvalue))
		    			{
		    				where=true;
		    				k=0;
			    			for(j=0;j<att_pos.size();j++)
			    			{
			    				while(k<values.length)
			    				{
			    					if(k==att_pos.get(j))
			    					  {
			    								values[k]=set_val.get(j).trim();
			    								input+=values[k];
			    								//System.out.println("input "+input);
			    								if(k!=values.length-1)
			    									input+=",";
			    								    //System.out.println("input "+input);
			    								k++;
			    								found=true;
			    								break;
			    						}
			    						
			    					
			    					else
			    					{
			    						input+=values[k];
			    						//System.out.println("input "+input);
			    						if(k!=values.length-1)
			    							input+=",";
			    						  //System.out.println("input "+input);
			    						k++;
			    					}
			    				//}
			    				}
			    			}
			    			if(j==att_pos.size()&&k<values.length)
		    			    {
		    			    	while(k<values.length)
		    			    	{
		    			    		input+=values[k];
		    			    		if(k!=values.length-1)
		    							input+=",";
		    			    		k++;
		    			    	}
		    			    }
		    				input+=System.lineSeparator();	
			    			}
		    			else{
			    			for(int i=0;i<values.length;i++){
			    				input+=values[i];
			    				//System.out.println("input "+input);
			    				if(i!=values.length-1)
			    					 input+=",";
			    					 //System.out.println("input "+input);
			    			    
			    			}
			    			input+=System.lineSeparator();
			    			//System.out.println("input "+input);
			    		
			    	}
		    			}
		    		
		    		if(type_ls.get(where_attrpos).equals("string"))	
	    			{
	    				System.out.println("Wrong type!");
	    				return;
	    			}
	    			}
		    	}
	    	}
		    	if(where==false)
		    	{
		    		System.out.println("The where attribute was not found");
		    		clear_screen();
		    		return;
		    	}
		    	if(found==false){
		    		System.out.println("The specified set attribute's value was not found!");
		    		clear_screen();
		    		return;
		    	}
		    	else{
		    		System.out.println("The record has been updated successfully!");
		    		clear_screen();
		    	}
		    	FileOutputStream os = new FileOutputStream(fp);
		        os.write(input.getBytes());
		        os.close();
	    	}
	        br.close();
	        
	    		
	  }
		catch (IOException e) 
		{
			e.printStackTrace();
		}
        }
	}
	private static void insert() 
	{
		System.out.println("Enter your query");
		String query = sc.next();
		String[] parts = query.split("into");	
		//parts[0]-insert
		//parts[1]- movie values (3,nikhil,2000)
		//OR parts[1]- movie (id,name,year) values (3,nikhil,2000)
		if(parts[0].trim().equals(query))
		{
			System.out.println("There is no INTO!");
			return;
		}
		String[] parts2=parts[1].split(" ");
		if((parts2.length!=4)&&(parts2.length!=5))
		{
		    System.out.println("The query format is incorrect!");
		    return;
		}
		
		//if the query doesn't contain column names
		if(parts2.length==4)
		{
			
			int pos=0;
			for(String s:parts2)
			{
				if((s.equals("values"))||(s.equals("VALUES")))
				  break;
				else
				  pos++;
			}
			if(pos==parts2.length)
			{
				System.out.println("There is no VALUES");
				return;
			}
			if(pos!=2)
			{
				System.out.println("The query format is incorrect!");
				return;
			}
			String tab_name = parts2[1];
			File fp = new File(tab_name + ".txt");
	        
			if(!fp.exists())
	        {
	            System.out.println("\nTable does not exist!");
	            clear_screen();
	            return;
	        }
			String values=parts2[3];
			if(values.equals("") || values.equals(null))
        	{
        		System.out.println("Enter values please");
        		return;
        	}
			if((values.contains("("))&&(values.contains(")")))
			{
				String replaced2 = values.replaceAll("[()]", "");
				String[] val_list=replaced2.split(",");
				try 
	            {
	        	    FileInputStream fis = new FileInputStream(fp);
	        	    BufferedReader br = new BufferedReader(new InputStreamReader(fis));
	        	
	            	System.out.println("\nSchema:");
	        	    System.out.println("----------------");
	        	    String schema = br.readLine();
	        	
	        	// to verify type also while checking
	        	    ArrayList<String> type_ls = new ArrayList<String>();
	        	    ArrayList<String> attr_ls = new ArrayList<String>();
	        	    for(String str : schema.split(","))
	        	    {
	        		    type_ls.add(str.split(":")[1]);
	        		    attr_ls.add(str.split(":")[0]);
	        	    }
	        	    int diff=0;
	        	    boolean miss=false;
	        	    int k;
	        	    String[] val_ls = new String[attr_ls.size()];
	        	   /* if(val_list.length<attr_ls.size())
	        	    {
	        	    	miss=true;
	        	    	diff=attr_ls.size()-val_list.length;
	        	    	for(k=0;k<val_list.length;k++)
	        	    		val_ls[k]=val_list[k];
	        	    	for(int m=0;m<diff;m++)
	        	    	{
	        	    		val_ls[k]="null";
	        	    		k++;
	        	    	}
	        	    }*/
	        	    System.out.println(tab_name + "(" + schema+")");
	        	    String pk = br.readLine();
	        	    
	        	    int tot_count=2;
	        	    while(br.readLine()!=null)
	        	    	tot_count++;
	        	    	
	        	    System.out.println("PK: " + pk);
	        	    String[] pk_split = pk.split(",");
	        	    System.out.println("Primary Key: " + pk_split[0]);
	        	    int pk_pos = Integer.parseInt(pk_split[1]);
	        	    if(val_list[pk_pos].equals("null")) 
	            	{
	            		System.out.println("\nERROR: Primary Key can not be NULL");
	            		br.close();
	            		return;
	            	}
	        	    if(hp.get(tab_name)!= null)
	            	{
	            		if((hp.get(tab_name).toString().contains(val_list[pk_pos])))
	            	
	            		{
	            			System.out.println("\nERROR: Primary Key should be UNIQUE");
	            			br.close();
	            			return;
	            		}
	            	}
	        	    //if(miss==false)
	        	    //{
	        	    int i;
	        	    boolean type_check = false;
	        	    int add = 0;
	        	    for(i=0;i<val_list.length;i++)
	            	{
	            		//System.out.println("processing now.. " + each_val[i]);
	            		// as we allow NULL values, we do not check for data types for null
	        	    	
	        	    	if(i==pk_pos)
	        	    	{
	        	    		add = 1;
	        	    	}
	            		if(!val_list[i].equals("null"))
	            		{
	            			// handle String, int, float, double
	            			if(type_ls.get(i+add).equals("String"))
	            			{
	            				if(val_list[i].matches(".*\\d+.*"))
	            				{
	            					// contains number
	            					type_check = true;
	            					break;
	            				}
	            			}
	            			else if(type_ls.get(i+add).equals("int"))
	            			{
	            				try
	            				{
	            					Integer.parseInt(val_list[i]);
	            				}
	            				catch(NumberFormatException e)
	            				{
	            					//not a int
	            					type_check = true;
	            					break;
	            				}
	            			}
	            			else if(type_ls.get(i+add).equals("double"))
	            			{
	            				try
	            				{
	            					Double.parseDouble(val_list[i]);
	            				}
	            				catch(NumberFormatException e)
	            				{
	            					//not a double
	            					type_check = true;
	            					break;
	            				}
	            			}
	            			else if(type_ls.get(i+add).equals("float"))
	            			{
	            				try
	            				{
	            					Float.parseFloat(val_list[i]);
	            				}
	            				catch(NumberFormatException e)
	            				{
	            					//not a double
	            					type_check = true;
	            					break;
	            				}
	            			}
	            		}
	            	}
	            	
	            	if(type_check == true)
	            	{
	            		type_check = false;
	            		System.out.println("\nType mismatch in " + val_list[i]);
	            		br.close();
	            		return;
	            	}
	            	
	            	
	            	//hp.put(tab_name,val_list[pk_pos]);
	            	br.close();
	            	String output="";
	            	int j=0;
	            	
	            	//System.out.println("tab_name" +tab_name);
	            	System.out.println("pk pos: " + pk_pos);
	            	for(String s:val_list)
	            	{   
	            		if(j==val_list.length-1)
	            		{
	            			if(j==pk_pos)
	            			{
	            				ArrayList<Integer> arr=(ArrayList<Integer>) hp.get(tab_name);
	            				System.out.println("arr size: " + arr.size());
	            				if(!(arr.size() == 0))	
	            				{
	            					BPTree t = (BPTree)btree.get(tab_name);
	            					t.put(String.valueOf(arr.get(arr.size()-1)+1).toString(), tot_count+1);
	            					btree.put(tab_name, t);
	            					
	            					output+=String.valueOf(arr.get(arr.size()-1)+1) + ",";
		            				arr.add(arr.get(arr.size()-1)+1);
	            					hp.put(tab_name, arr);
	            				}
	            				else
	            				{
	            					BPTree t = (BPTree)btree.get(tab_name);
	            					t.put(String.valueOf(1).toString(), tot_count+1);
	            					btree.put(tab_name, t);
	            					
	            					output+=1 + ",";
            						ArrayList temp = new ArrayList<>();
	            					hp.put(tab_name, temp);
	            				}
	            			}
	            			output+=s;
	            			break;
	            		}
	            		if(j==pk_pos)
            			{
            				ArrayList<Integer> arr=(ArrayList<Integer>) hp.get(tab_name);
            				System.out.println("arr: " + arr);
            				System.out.println("arr size "+arr.size());
            				if(!(arr.size() == 0))
            				{

            					int val =  arr.get(arr.size()-1)+1;
            					
            					BPTree t = (BPTree)btree.get(tab_name);
            					t.put(String.valueOf(val).toString(), tot_count+1);
            					btree.put(tab_name, t);
            					
            					output+=String.valueOf(val) + ",";

	            				arr.add(arr.get(arr.size()-1)+1);
            					hp.put(tab_name, arr);
            				}
            					else
            					{
	            					BPTree t = (BPTree)btree.get(tab_name);
	            					t.put(String.valueOf(1).toString(), tot_count+1);
	            					btree.put(tab_name, t);
	            					
            						output+=1+",";
            						ArrayList temp = new ArrayList<>();
            						temp.add(1);
            						hp.put(tab_name, temp);
            					}
            			}
	            		output+=s+",";
	            		j++;
	            	}
	            	System.out.println("hp: " + hp);
	            	BufferedWriter writer = new BufferedWriter(new FileWriter(fp,true));
	            	writer.write(System.getProperty("line.separator"));
	            	writer.write(output);
	            	System.out.println("The record has been inserted successfully!");
	            	writer.close();
	        	   // }
	        	    /*else
	        	    {
	        	    	int i;
		        	    boolean type_check = false;
		        	    
		        	    for(i=0;i<val_ls.length;i++)
		            	{
		            		//System.out.println("processing now.. " + each_val[i]);
		            		// as we allow NULL values, we do not check for data types for null
		        	    	
		            		if(!val_ls[i].equals("null"))
		            		{
		            			// handle String, int, float, double
		            			if(type_ls.get(i).equals("string"))
		            			{
		            				if(val_ls[i].matches(".*\\d+.*"))
		            				{
		            					// contains number
		            					type_check = true;
		            					break;
		            				}
		            			}
		            			else if(type_ls.get(i).equals("int"))
		            			{
		            				try
		            				{
		            					Integer.parseInt(val_ls[i]);
		            				}
		            				catch(NumberFormatException e)
		            				{
		            					//not a int
		            					type_check = true;
		            					break;
		            				}
		            			}
		            			else if(type_ls.get(i).equals("double"))
		            			{
		            				try
		            				{
		            					Double.parseDouble(val_ls[i]);
		            				}
		            				catch(NumberFormatException e)
		            				{
		            					//not a double
		            					type_check = true;
		            					break;
		            				}
		            			}
		            			else if(type_ls.get(i).equals("float"))
		            			{
		            				try
		            				{
		            					Float.parseFloat(val_ls[i]);
		            				}
		            				catch(NumberFormatException e)
		            				{
		            					//not a double
		            					type_check = true;
		            					break;
		            				}
		            			}
		            		}
		            	}
		            	
		            	if(type_check == true)
		            	{
		            		type_check = false;
		            		System.out.println("\nType mismatch in " + val_ls[i]);
		            		br.close();
		            		return;
		            	}
		            	
		            	
		            	hp.put(tab_name,val_ls[pk_pos]);
		            	br.close();
		            	String output="";
		            	int j=0;
		            	for(String s:val_ls)
		            	{   
		            		if(j==val_ls.length-1)
		            		{
		            			output+=s;
		            			break;
		            		}
		            		output+=s+",";
		            		j++;
		            	}
		            	BufferedWriter writer = new BufferedWriter(new FileWriter(fp,true));
		            	writer.write(System.getProperty("line.separator"));
		            	writer.write(output);
		            	System.out.println("The record has been inserted successfully!");
		            	writer.close();
		        	    }*/
	        	    
	            }
			    catch (IOException e) 
		        {
					e.printStackTrace();
				}
			}
		}
		//if the query contains column names as well
		if(parts2.length==5)	
		{    
			int pos=0;
			for(String s:parts2)
			{
				if((s.equals("values"))||(s.equals("VALUES")))
				  break;
				else
				  pos++;
			}
			if(pos==parts2.length)
			{
				System.out.println("There is no VALUES");
				return;
			}
			if(pos!=3)
			{
				System.out.println("The query format is incorrect!");
				return;
			}
			String tab_name = parts2[1];
			File fp = new File(tab_name + ".txt");
	        
			if(!fp.exists())
	        {
	            System.out.println("\nTable does not exist!");
	            clear_screen();
	            return;
	        }
			String cnames=parts2[2];
			if(cnames.equals("") || cnames.equals(null))
        	{
        		System.out.println("Enter columns please");
        		return;
        	}
			if((cnames.contains("("))&&(cnames.contains(")")))
			{
				String replaced1 = cnames.replaceAll("[()]", "");
				String[] attr_list=replaced1.split(",");
				String values=parts2[4];
				if(values.equals("") || values.equals(null))
	        	{
	        		System.out.println("Enter values please");
	        		return;
	        	}
				if((values.contains("("))&&(values.contains(")")))
				{
					String replaced2 = values.replaceAll("[()]", "");
					String[] val_list=replaced2.split(",");
				    if(attr_list.length!=val_list.length)
				    {
				    	System.out.println("The no. of columns and the no. of values do not match!");
				    	return;
				    }
				    try 
		            {
		        	    FileInputStream fis = new FileInputStream(fp);
		        	    BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		        	
		            	System.out.println("\nSchema:");
		        	    System.out.println("----------------");
		        	    String schema = br.readLine();
		        	
		        	// to verify type also while checking
		        	    ArrayList<String> type_ls = new ArrayList<String>();
		        	    ArrayList<String> attr_ls = new ArrayList<String>();
		        	    for(String str : schema.split(","))
		        	    {
		        		    type_ls.add(str.split(":")[1]);
		        		    attr_ls.add(str.split(":")[0]);
		        	    }
		        	    if((attr_list.length>attr_ls.size())||(val_list.length>attr_ls.size()))
		        	    {
		        	    	System.out.println("The number of values is greater than the number of columns!");
		        	    	return;
		        	    }
		        	    String[] val_ls = new String[attr_ls.size()];
		        	    boolean miss=false;
		        	    int diff=0;
		        	    if(attr_list.length<attr_ls.size())
		        	    {
		        	    	miss=true;
		        	    	int m=0,k=0;
		        	    	int i;
		        	    	int l=attr_list.length;
		        	    	int len=attr_ls.size();
		        	    	diff=attr_ls.size()-val_list.length;
		        	    	if(attr_list[l-1].equals(attr_ls.get(l-1)))
		        	    	{
		        	    		for(i=0;i<val_list.length;i++)
		        	    			val_ls[i]=val_list[i];
		        	    		for(int n=0;n<diff;n++)
			        	    	{
			        	    		val_ls[i]="null";
			        	    		i++;
			        	    	}
		        	    	}
		        	    	else{
		        	    	for(i=0;i<attr_ls.size();i++)
		        	    	{
		        	    		if(attr_ls.get(i).equals(attr_list[k]))
		        	    		{
		        	    			val_ls[m]=val_list[k];
		        	    			m++;
		        	    			if(m==attr_ls.size())
		        	    				break;
		        	    			k++;
		        	    		}
		        	    		else
		        	    		{
		        	    			val_ls[m]="null";
		        	    			m++;
		        	    			if(m==attr_ls.size())
		        	    				break;
		        	    		}
		        	    	}
		        	    }
		        	    	
		        	    }	
		        	    /*System.out.println("val_ls");
		        	    for(int u=0;u<val_ls.length;u++)
		        	    	System.out.println(val_ls[u]);*/
		        	    System.out.println(tab_name + "(" + schema+")");
		        	    String pk = br.readLine();
		        	    
		        	    int tot_count = 2;
		        	    while(br.readLine()!=null)
		        	    	tot_count++;
		        	    
		        	    
		        	    
		        	    System.out.println("PK: " + pk);
		        	    String[] pk_split = pk.split(",");
		        	    System.out.println("Primary Key: " + pk_split[0]);
		        	    int pk_pos = Integer.parseInt(pk_split[1]);
		        	    if(val_list[pk_pos].equals("null")) 
		            	{
		            		System.out.println("\nERROR: Primary Key can not be NULL");
		            		br.close();
		            		return;
		            	}
		        	    if(hp.get(tab_name)!= null)
		            	{
		            		if((hp.get(tab_name).toString().contains(val_list[pk_pos])))
		            	
		            		{
		            			System.out.println("\nERROR: Primary Key should be UNIQUE");
		            			br.close();
		            			return;
		            		}
		            	}
		        	    if(miss==false)
		        	    {
		        	    boolean type_check = false;
		            	int i;
		            	for(i=0;i<val_list.length;i++)
		            	{
		            		//System.out.println("processing now.. " + each_val[i]);
		            		// as we allow NULL values, we do not check for data types for null
		            		if(!val_list[i].equals("null"))
		            		{
		            			// handle String, int, float, double
		            			if(type_ls.get(i).equals("String"))
		            			{
		            				if(val_list[i].matches(".*\\d+.*"))
		            				{
		            					// contains number
		            					type_check = true;
		            					break;
		            				}
		            			}
		            			else if(type_ls.get(i).equals("int"))
		            			{
		            				try
		            				{
		            					Integer.parseInt(val_list[i]);
		            				}
		            				catch(NumberFormatException e)
		            				{
		            					//not a int
		            					type_check = true;
		            					break;
		            				}
		            			}
		            			else if(type_ls.get(i).equals("double"))
		            			{
		            				try
		            				{
		            					Double.parseDouble(val_list[i]);
		            				}
		            				catch(NumberFormatException e)
		            				{
		            					//not a double
		            					type_check = true;
		            					break;
		            				}
		            			}
		            			else if(type_ls.get(i).equals("float"))
		            			{
		            				try
		            				{
		            					Float.parseFloat(val_list[i]);
		            				}
		            				catch(NumberFormatException e)
		            				{
		            					//not a double
		            					type_check = true;
		            					break;
		            				}
		            			}
		            		}
		            	}
		            	
		            	if(type_check == true)
		            	{
		            		type_check = false;
		            		System.out.println("\nType mismatch in " + val_list[i]);
		            		br.close();
		            		return;
		            	}
		            	BPTree bppp = (BPTree)btree.get(tab_name);
		            	
		            	// baki hai
		            	bppp.put(val_list[pk_pos], tot_count);
		            	btree.put(tab_name, bppp);
		            	
		            	ArrayList<Integer> als = (ArrayList<Integer>) hp.get(tab_name);
		            	als.add(Integer.parseInt(val_list[pk_pos]));
		            	hp.put(tab_name,als);
		            	br.close();
		            	String output="";
		            	int j=0;
		            	for(String s:val_list)
		            	{   
		            		if(j==val_list.length-1)
		            		{
		            			output+=s;
		            			break;
		            		}
		            		output+=s+",";
		            		j++;
		            	}
		            	BufferedWriter writer = new BufferedWriter(new FileWriter(fp,true));
		            	writer.write(System.getProperty("line.separator"));
		            	writer.write(output);
		            	System.out.println("The record has been inserted successfully!");
		            	writer.close();
		        	    }
		        	    else
		        	    {
			        	    boolean type_check = false;
			            	int i;
			            	for(i=0;i<val_ls.length;i++)
			            	{
			            		//System.out.println("processing now.. " + each_val[i]);
			            		// as we allow NULL values, we do not check for data types for null
			            		if(!val_ls[i].equals("null"))
			            		{
			            			// handle String, int, float, double
			            			if(type_ls.get(i).equals("String"))
			            			{
			            				if(val_ls[i].matches(".*\\d+.*"))
			            				{
			            					// contains number
			            					type_check = true;
			            					break;
			            				}
			            			}
			            			else if(type_ls.get(i).equals("int"))
			            			{
			            				try
			            				{
			            					Integer.parseInt(val_ls[i]);
			            				}
			            				catch(NumberFormatException e)
			            				{
			            					//not a int
			            					type_check = true;
			            					break;
			            				}
			            			}
			            			else if(type_ls.get(i).equals("double"))
			            			{
			            				try
			            				{
			            					Double.parseDouble(val_ls[i]);
			            				}
			            				catch(NumberFormatException e)
			            				{
			            					//not a double
			            					type_check = true;
			            					break;
			            				}
			            			}
			            			else if(type_ls.get(i).equals("float"))
			            			{
			            				try
			            				{
			            					Float.parseFloat(val_ls[i]);
			            				}
			            				catch(NumberFormatException e)
			            				{
			            					//not a double
			            					type_check = true;
			            					break;
			            				}
			            			}
			            		}
			            	}
			            	
			            	if(type_check == true)
			            	{
			            		type_check = false;
			            		System.out.println("\nType mismatch in " + val_ls[i]);
			            		br.close();
			            		return;
			            	}
			            	
			            	
			            	BPTree bpp = (BPTree)btree.get(tab_name);
			            	
			            	//baki hai
			            	BPTree bppp = (BPTree)btree.get(tab_name);
			            	
			            	// baki hai
			            	bppp.put(val_list[pk_pos], tot_count);
			            	btree.put(tab_name, bppp);
			            	
			            	ArrayList<Integer> als = (ArrayList<Integer>) hp.get(tab_name);
			            	als.add(Integer.parseInt(val_list[pk_pos]));
			            	hp.put(tab_name,als);
			            	
			            	hp.put(tab_name,val_ls[pk_pos]);
			            	br.close();
			            	String output="";
			            	int j=0;
			            	for(String s:val_ls)
			            	{   
			            		if(j==val_ls.length-1)
			            		{
			            			output+=s;
			            			break;
			            		}
			            		output+=s+",";
			            		j++;
			            	}
			            	BufferedWriter writer = new BufferedWriter(new FileWriter(fp,true));
			            	writer.write(System.getProperty("line.separator"));
			            	writer.write(output);
			            	System.out.println("The record has been inserted successfully!");
			            	writer.close();
			        	    }
		            }
				    catch (IOException e) 
			        {
						e.printStackTrace();
					}
				}
			}
		}
	}
	private static void delete_query() throws IOException
	{
		System.out.println("Enter your query");
		String query = sc.next();
		
		long startTime = System.currentTimeMillis();
		
		//parts[0]=delete
		//parts[1]= movies where name=''
		//OR parts[1]= movies where name='' and year=''
		String[] parts = query.split("from");	
		if(parts[0].equals(query))
		{
			System.out.println("There is no FROM!");
			return;
		}
		if(!parts[1].contains("where")&&!parts[1].contains("WHERE"))
		{
			System.out.println("WHERE is missing!");
			return;
		}
		String[] parts1=parts[1].split("where");
		String tab_name=parts1[0].trim();
		File fp = new File(tab_name + ".txt");
        boolean found=false;
		if(!fp.exists())
        {
            System.out.println("\nTable does not exist!");
            clear_screen();
            return;
        }
		if(parts1[1].contains("AND")||parts1[1].contains("OR"))
		{
		
	    BufferedReader br = new BufferedReader(new FileReader(fp));
		File tempFile = new File(tab_name + ".tmp");
        PrintWriter pw = new PrintWriter(new FileWriter(tempFile));
        String line = null;
        String schema=null;
        String pk=null;
        int position;
        schema=br.readLine();
        pk=br.readLine();
        
        int pk_pos = Integer.parseInt(pk.split(",")[1]);
        
        
        pw.println(schema);
        pw.println(pk);
        ArrayList<String> attr_ls=new ArrayList<String>();
        ArrayList<String> attr_type=new ArrayList<String>();
        String[] str=schema.split(",");
        for(String s:str)
        {
        	attr_ls.add(s.split(":")[0]);
        	attr_type.add(s.split(":")[1]);
        }
		LinkedHashMap<String, Integer> hp = new LinkedHashMap<String, Integer>();
		String split_cond = null;
		if(parts1[1].contains("AND"))
		{
			split_cond = "AND";
		}
		if(parts1[1].contains("OR"))
		{
			split_cond = "OR";
		}
		for(String tmp : parts1[1].split(split_cond))
		 {
        	position=0;
			String attr_name = null;
        	if(tmp.contains("<"))
        		attr_name = tmp.trim().split("<")[0];
        	else if(tmp.contains(">"))
        		attr_name = tmp.trim().split(">")[0];
        	else if(tmp.contains("="))
        		attr_name = tmp.trim().split("=")[0];
        	
        	//int position = attr_ls.indexOf(attr_name);
        	for(String s:attr_ls)
    	    {
    	    	if(!s.equals(attr_name))
    	    		position++;
    	    	else
    	    		break;
    	    }
        	if(position == -1)
			{
				System.out.println("\nEnter correct attribute name " +attr_name);
				clear_screen();
				br.close();
				return;
			}
        	hp.put(attr_name,position);
        }
		while ((line = br.readLine()) != null) 
        {
    	    System.out.println("line "+ line);
        	if(!line.equals("#"))
        	{
        	int i=0;
        	int record_flag = 0;
        	System.out.println("hp:" + hp);
        	for (Entry<String, Integer> entry : hp.entrySet()) 
        	{
        	    String key = entry.getKey(); // year
        	    Object value = entry.getValue(); // 2
        	    
        	    System.out.println("key " + key + "value " + value);

        	    String val = line.split(",")[(int) value]; // year in the record 
        	    String cond_line = parts1[1].split(split_cond)[i++].trim(); // year>1990
        	   
        	    
        	    // 19966
        	    
        	    // year>1990
        	 
        	    // If val does not contain any value or it is null then 
        	    // do not process that record
        	    //System.out.println("val : " + val);
        	    
        	    //System.out.println("cnd line: " + cond_line + "line : " + line);
        	    if(!val.equals("null"))
        	    {
        	    	if(cond_line.contains("<"))
        	    	{
        	    		if(val.matches(".*\\d+.*"))
        	    		{
        	    			//good attr for condition 
        	    			if(attr_type.get((int)value).equals("int"))
        	    			{
        	    				//int
        	    				int val1 =  Integer.parseInt(val);
		        	    		int val2 = Integer.parseInt(cond_line.split("<")[1]); 
		        	    		if(val1 < val2)
		        	    		{
		        	    			record_flag++;
		        	    		}
        	    			}
        	    			else
        	    			{
        	    				//float
        	    				float val1 = Float.parseFloat(val);
        	    				float val2 = Float.parseFloat(cond_line.split("<")[1]);
        	    				if(val1 < val2)
		        	    		{
		        	    			record_flag++;
		        	    		}
        	    			}    	    			
        	    		}
        	    		else
        	    		{
        	    			System.out.println("\n< or > ops does not work for strings");
        	    			br.close();
        	    			pw.close();
        	    			//tempFile.delete();
        	    			return;
        	    		}	        	    	
        	    	}
        	    	else if(cond_line.contains(">"))
        	    	{
        	    		if(val.matches(".*\\d+.*"))
        	    		{
        	    			//good attr for condition 
        	    			try
        	    			{
        	    				//int
        	    				int val1 =  Integer.parseInt(val);
		        	    		int val2 = Integer.parseInt(cond_line.split(">")[1]); 
		        	    		if(val1 > val2)
		        	    		{
		        	    			record_flag++;
		        	    		}
        	    			}
        	    			catch(NumberFormatException e)
        	    			{
        	    				//float
        	    				float val1 = Float.parseFloat(val);
        	    				float val2 = Float.parseFloat(cond_line.split(">")[1]);
        	    				if(val1 > val2)
		        	    		{
		        	    			record_flag++;
		        	    		}
        	    			}    	    			
        	    		}
        	    		else
        	    		{
        	    			System.out.println("\n< or > ops does not work for strings");
        	    			br.close();
        	    			pw.close();
        	    			//tempFile.delete();
        	    			return;
        	    		}
        	    	}
        	    	else if(cond_line.contains("="))
        	    	{
        	    		if(val.equals(cond_line.split("=")[1]))
        	    		{
        	    			record_flag++;
        	    		}
        	    	}
        	    }
        	    
        	} // end-for
        	// only if all conditions are true then enter that line into tempfile
        	
        	
        	//System.out.println("flag : " +record_flag);
        	System.out.println("record flag "+record_flag);
        	if(split_cond.equals("AND"))
        	{
        		if(record_flag != hp.size())
        		{
        		//System.out.println("comes AND");
        			found=true;
        		    pw.println(line);
        			pw.flush();
        			record_flag = 0;
        		}	
        		else
        		{
        			// removing entry from BPtree when delete operation happens
        			BPTree bp = btree.get(tab_name);
        			bp.remove(line.split(",")[pk_pos]);
        			btree.put(tab_name, bp);
        			pw.println("#");
        			pw.flush();
        			record_flag = 0;
        		}
        	}
        	if(split_cond.equals("OR"))
        	{
        		if(record_flag ==0)
        		{
        			//System.out.println("comes OR");
        			found=true;
        			pw.println(line);
        			pw.flush();
        			record_flag = 0;
        		}
        		else
        		{
        			// removing entry from BPtree when delete operation happens
        			BPTree bp = btree.get(tab_name);
        			bp.remove(line.split(",")[pk_pos]);
        			btree.put(tab_name, bp);
        			pw.println("#");
        			pw.flush();
        			record_flag = 0;
        		}
        	}
        	}
        	else
        	{
        		pw.println("#");
        	}
        }
        
        // end-while
        
        pw.close();
        br.close();
        if (!fp.delete()) {
        	System.out.println("comes here./././.");
            System.out.println("\nCould not delete file"+fp);
            clear_screen();
            tempFile.delete();
            return;
          } 
        if (!tempFile.renameTo(fp))
        {
          System.out.println("\nCould not rename file");
          clear_screen();
          return;
        }
        if(found == true)
        {	
        	// if any record found then and then shows all records after deletion
        	System.out.println("The record has been deleted!");
        	found = false;
        	display_all_main(tab_name+".txt");
        }
        else
        {
        	System.out.println("No records found!");
        	return;
        }
        clear_screen();
	}
		
		
				
		else
		{
			String st=parts1[1].trim();
			String attrname=null;
			String value=null;
			String op=null;
			if(st.contains("="))
			{
				String[] s=st.split("=");
				attrname=s[0];
				value=s[1];
				op="=";
			}
			if(st.contains("<"))
			{
				String[] s=st.split("<");
				attrname=s[0];
				value=s[1];
				op="<";
			}
			if(st.contains(">"))
			{
				String[] s=st.split(">");
				attrname=s[0];
				value=s[1];
				op=">";
			}
			File tempFile = new File(fp.getAbsolutePath() + ".tmp");
	        BufferedReader br = new BufferedReader(new FileReader(fp));
	        PrintWriter pw = new PrintWriter(new FileWriter(tempFile));
	        int attrpos=0;
	        String line = null;
	        
	        //Read from the original file and write to the new
	        String schema = br.readLine();
	        String pk_all = br.readLine();
	        
	        
	        int pk_pos = Integer.parseInt(pk_all.split(",")[1]);
	        pw.println(schema);
	        
    	    ArrayList<String> attr_ls = new ArrayList<String>();
    	    ArrayList<String> attr_type = new ArrayList<String>();
    	    for(String str : schema.split(","))
    	    {
    		    attr_ls.add(str.split(":")[0]);
    		    attr_type.add(str.split(":")[1]);
    	    }
    	    for(String s:attr_ls)
    	    {
    	    	if(!s.equals(attrname))
    	    		attrpos++;
    	    	else
    	    		break;
    	    }
	        pw.println(pk_all);
	        int pos = Integer.parseInt(pk_all.split(",")[1]);
	        boolean flag = false;
	        while ((line = br.readLine()) != null)
	        {
	        	if(!line.equals("#"))
	        	{
	        	if(op.equals("="))
	        	{
	        		if (!line.split(",")[attrpos].equals(value)) 
	        		{
	                    pw.println(line);
	                    pw.flush();
	                  }
	                  else
	                  {
	                	  BPTree bp = btree.get(tab_name);
	          			bp.remove(line.split(",")[pk_pos]);
	          			btree.put(tab_name, bp);
	          			pw.println("#");
	                	  flag = true;
	                  }
	        	}
	        	
	        	if(op.equals("<"))
	        	{
	        		if(attr_type.get(attrpos).equals("int"))
	        		{
	        		if ((Integer.parseInt(line.split(",")[attrpos]))>=(Integer.parseInt(value))) 
	        		{
	                    pw.println(line);
	                    pw.flush();
	                  }
	                  else
	                  {
	                	  BPTree bp = btree.get(tab_name);
	          			bp.remove(line.split(",")[pk_pos]);
	          			btree.put(tab_name, bp);
	          			pw.println("#");
	                	  flag = true;
	                  }
	        		}
	        		if(attr_type.get(attrpos).equals("float"))
	        		{
	        		if ((Float.parseFloat(line.split(",")[attrpos]))>=(Float.parseFloat(value))) 
	        		{
	                    pw.println(line);
	                    pw.flush();
	                  }
	                  else
	                  {
	                	  BPTree bp = btree.get(tab_name);
	          			bp.remove(line.split(",")[pk_pos]);
	          			btree.put(tab_name, bp);
	          			pw.println("#");
	                	  flag = true;
	                  }
	        		}
	        		if(attr_type.get(attrpos).equals("String"))
	        		{
	        		    System.out.println("Wrong type");
	        		    br.close();
	        		    return;
	        		}
	        	}
	        	
	        	if(op.equals(">"))
	        	{
	        		if(attr_type.get(attrpos).equals("int"))
	        		{
	        		if ((Integer.parseInt(line.split(",")[attrpos]))<=(Integer.parseInt(value))) 
	        		{
	                    pw.println(line);
	                    pw.flush();
	                  }
	                  else
	                  {
	                	  BPTree bp = btree.get(tab_name);
	          			bp.remove(line.split(",")[pk_pos]);
	          			btree.put(tab_name, bp);
	          			pw.println("#");
	                	  flag = true;
	                  }
	        		}
	        		if(attr_type.get(attrpos).equals("float"))
	        		{
	        		if ((Float.parseFloat(line.split(",")[attrpos]))<=(Float.parseFloat(value))) 
	        		{
	                    pw.println(line);
	                    pw.flush();
	                  }
	                  else
	                  {
	                	  BPTree bp = btree.get(tab_name);
	          			bp.remove(line.split(",")[pk_pos]);
	          			btree.put(tab_name, bp);
	          			pw.println("#");
	                	  flag = true;
	                  }
	        		}
	        		if(attr_type.get(attrpos).equals("String"))
	        		{
	        		    System.out.println("Wrong type");
	        		    br.close();
	        		    return;
	        		}
	        	}
	        	}
	        	else
	        	{
	        		pw.println("#");
	        	}
	        }
	        if(flag == false)
	        {
	        	System.out.println("\nNo record found!");
	        	clear_screen();
	        }
	        else
	        {
	        	System.out.println("\nRecord deleted");
	        	clear_screen();
	        	
	        }
	        pw.close();
	        br.close();
	        if (!fp.delete()) {
	            System.out.println("\nCould not delete file"+fp);
	            clear_screen();
	            tempFile.delete();
	            return;
	          } 
	        //Rename the new file to the filename the original file had.
	          if (!tempFile.renameTo(fp))
	          {
	            System.out.println("\nCould not rename file");
	            clear_screen();
	            return;
	          }
	          if(flag == true)
	          {	
	          	// if any record found then and then shows all records after deletion
	          	flag = false;
	          	display_all_main(tab_name+".txt");
	          }
	          clear_screen();
		}
		

		long endTime = System.currentTimeMillis();
		System.out.println("Without Index: Delete query took " + (endTime-startTime) + "milli secs");
		clear_screen();
	}
	private static void select_index() throws IOException 
	{
		System.out.println("Enter your query");
		String query = sc.next();
		
		long startTime = System.currentTimeMillis();
		
		String[] parts = query.split("from");	
		if(parts[0].equals(query))
		{
			System.out.println("There is no FROM!");
			return;
		}
		// parts[0] - SELECT *_
		if(parts[0].trim().equals("select"))
		{
			System.out.println("No attribute names or '*' provided after SELECT!");
			return;
		}
		//parts[1] - movie where rating > 7 order by year
		String tab_name;
		
		if(parts.length == 1 )
		{
			System.out.println("Table name not provided!");
			return;
		}
	
		// contains WHERE
		if(parts[1].contains("where"))
		{
			String[] parts_mod = parts[1].split("where");
			long starttime = System.currentTimeMillis();
			
			tab_name = parts_mod[0].trim();	
			System.out.println("tab_name:" + tab_name);
			
			if(tab_name.contains("order by"))
			{
				System.out.println("order by can not come before where!");
				return;
			}
			if(tab_name.contains(","))
			{
					
				String tab1 = tab_name.split(",")[0].trim();
				String tab2 = tab_name.split(",")[1].trim();
				
				// bigger table
				File f1 = new File(tab1+".txt");
				FileInputStream fis1 = new FileInputStream(f1);
				BufferedReader br1 = new BufferedReader(new InputStreamReader(fis1));
				String schema1 = br1.readLine();
				String pk1 = br1.readLine();
				
				String attr1 = parts_mod[1].split("=")[0].trim();
				System.out.println("attr1 " + attr1);
				if(!schema1.contains(attr1))
				{
					System.out.println("Attribute " + attr1 + " not found!!");
					return;
				}
				br1.close();
				
				// smaller table
				File f2 = new File(tab2+".txt");
				FileInputStream fis2 = new FileInputStream(f2);
				BufferedReader br2 = new BufferedReader(new InputStreamReader(fis2));
				String schema2 = br2.readLine();
				String pk2 = br2.readLine();
				
				int pk_pos = Integer.parseInt(pk2.split(",")[1]);
				
				String attr2 = parts_mod[1].split("=")[1].trim();
				System.out.println("attr1 " + attr1);
				if(!schema2.contains(attr2))
				{
					System.out.println("Attribute " + attr2 + " not found!!");
					return;
				}
			
				File tempFile = new File("join.tmp");
				PrintWriter pw = new PrintWriter(new FileWriter(tempFile));
				
				pw.println(schema1 + "," + schema2);
				pw.println(pk1 + "," + pk2);
				
				
				// bigger table
				BPTree bt1 = (BPTree)btree.get(tab1);
				String line = null;
				
				//indexing logic
				while((line = br2.readLine())!= null)
				{
					if(!line.equals("#"))
					{
						String val = line.split(",")[pk_pos];
						if(bt1.get(val) != null)
						{
							int line_num = (int)bt1.get(val);
							String lineval = null;
							try (Stream<String> lines = Files.lines(Paths.get(tab1+".txt"))) {
								lineval = lines.skip(line_num-1).findFirst().get();
							}
						
							pw.println(lineval + "," + line);
						}
					}
				}
				pw.flush();
				pw.close();
				br2.close();
				display_all_main("join.tmp");
				
				
				//start with small tables and find the correspoding line from bigger table using B+ tree
				
				
				
			}
			else
			{
				String attr = null;
				int val = 0;
				String check = null;
				// WITHOUT JOIN
				
				
				if(!parts_mod[1].contains("AND") || !parts_mod[1].contains("OR"))
				{
					if(parts_mod[1].contains("="))
					{
						attr = parts_mod[1].split("=")[0].trim();
						val = Integer.parseInt(parts_mod[1].split("=")[1].trim());
						check = "=";
					}
					else if(parts_mod[1].contains(">"))
					{
						attr = parts_mod[1].split(">")[0].trim();
						val = Integer.parseInt(parts_mod[1].split(">")[1].trim());
						check = ">";
					}
					else if(parts_mod[1].contains("<"))
					{
						attr = parts_mod[1].split("<")[0].trim();
						val = Integer.parseInt(parts_mod[1].split("<")[1].trim());
						check = "<";
					}
					
					
					// B+ tree 
					BPTree bp = (BPTree)btree.get(tab_name);
					
					// getting line number from B+ tree
					int line_num = (int) bp.get(String.valueOf(val));
					
					System.out.println("-----------------------------------");
					if(check.equals("="))
					{
						String line;
						try (Stream<String> lines = Files.lines(Paths.get(tab_name+".txt"))) {
						    line = lines.skip(line_num-1).findFirst().get();
						}
						if(!line.equals("#"))
							System.out.println(line);
						
					}
					else if (check.equals("<"))
					{
						File f = new File(tab_name+".txt");
						FileInputStream fis = new FileInputStream(f);
						BufferedReader br = new BufferedReader(new InputStreamReader(fis));
						
						int count = 2;
						
						
						String line = null;

						File tempFile  = new File(tab_name+".tmp");
						PrintWriter pw = new PrintWriter(new FileWriter(tempFile));
						
						pw.println(br.readLine());
						pw.println(br.readLine());
						while((line=br.readLine())!=null && count < line_num)
						{
							// yello
							System.out.println("line: " + line);
							if(!line.equals("#"))
								pw.println(line);
							count++;
						}
						br.close();
						pw.close();
						display_all_main(tab_name+".tmp");
						
					}
					else if (check.equals(">"))
					{
						Object[] line;
						try (Stream<String> lines = Files.lines(Paths.get(tab_name+".txt"))) {
						    line = lines.skip(line_num).toArray();
						}
						System.out.println();
						for(Object str : line)
						{
							if(!str.equals("#"))
								System.out.println(str);
						}
					}
					
				}
			}
			System.out.println("With index took : " + (System.currentTimeMillis() - startTime) + "milli secs");
		}
		
		
	}
	private static void select() throws IOException 
	{
		
		System.out.println("Enter your query");
		String query = sc.next();
		
		long startTime = System.currentTimeMillis();
		
		String[] parts = query.split("from");	
		if(parts[0].equals(query))
		{
			System.out.println("There is no FROM!");
			return;
		}
		// parts[0] - SELECT *_
		if(parts[0].trim().equals("select"))
		{
			System.out.println("No attribute names or '*' provided after SELECT!");
			return;
		}
		//parts[1] - movie where rating > 7 order by year
		String tab_name;
		
		if(parts.length == 1 )
		{
			System.out.println("Table name not provided!");
			return;
		}
	
		// contains WHERE
		if(parts[1].contains("where"))
		{
			String[] parts_mod = parts[1].split("where");
		
			
			
			tab_name = parts_mod[0].trim();	
			
			if(tab_name.contains("order by"))
			{
				System.out.println("order by can not come before where!");
				return;
			}
			
			// Without JOIN
			if(!parts_mod[0].contains(","))
			{
				File fp = new File(tab_name + ".txt");
		        if(!fp.exists())
		        {
		        	System.out.println("\nTable " + fp + " does not exist!");
		            clear_screen();
		            return;
		        }
		        if(check_for_records(parts_mod[0].trim()))
		        {    	    
		        	// parts_mod[0] - movie
					// parts_mod[1] -  rating > 7 AND year > 2000 order by year
		        	try {
						//SELECT ALL
			    		FileInputStream fis;
						fis = new FileInputStream(tab_name+".txt");
						BufferedReader br = new BufferedReader(new InputStreamReader(fis));
				    	
						String schema = br.readLine();
				    	String pk = br.readLine();
				    	int pk_pos = Integer.parseInt(pk.split(",")[1]);
				    	br.close();
				    	String[] temp = schema.split(",");
				    	
				    	ArrayList<String> attr_ls = new ArrayList<String>();
				    	for(String s : temp)
				    	{
				    		attr_ls.add(s.split(":")[0]);
				    	}
				   
		        	
				    	// ONLY WHERE CONDITIONS 
				    	if(!parts_mod[1].contains("order by"))
				    	{
				    		String conditions = parts_mod[1];
				    		//System.out.println("conditions: " + conditions); 
				    		// can have more than one conditions
				    		if(search_record(tab_name,conditions,attr_ls))
				    		{	
				    			if(parts[0].contains("*"))
				    		
				    			{	
				    				//SELECT ALL
				    				display_all_main(tab_name+".tmp");
				    				clear_screen();
				    			}
				    			else
				    			{
				    				// PROJECTION
				    				String attr = parts[0].trim().split(" ")[1];
				    				if(!projection1(tab_name+".tmp",attr,attr_ls))
				    				{
				    					// error in projection 
				    					// delete .tmp file
				    					File f = new File(tab_name+".tmp");
				    					f.delete();
				    				}
				    			}
				    		}
				    		else
				    		{
				    			// error in search_record
				    			return;
				    		}
				    	}
				    	// WHERE and ORDER BY also
				    	else
				    	{
				    		//System.out.println("Comes here...........");
				    		String conditions = parts_mod[1].split("order by")[0].trim();
				    		
				    		if(!search_record(tab_name,conditions,attr_ls))
				    		{
				    			// error while searching record
				    			return;
				    		}
		        		
				    		String order = null;
				    		String order_attr = null;
				    		String order_type;
		    			
				    		String tail = parts_mod[1].split("order by")[1].trim();
				    		// tail - title desc
		    			
		    			
				    		if(tail.contains("desc"))
				    		{
				    			order = "DESC";
				    			order_attr = tail.split(" desc")[0];
				    		}
				    		else if (tail.contains("asc"))
				    		{
				    			order = "ASC";
				    			order_attr = tail.split(" asc")[0];
				    		}
				    		else
				    		{
				    			order = "ASC";
				    			order_attr = tail.trim();
				    		}
		    			
				    		String attr_type = null;
				    		for(String type : temp)
				    		{
				    			if(type.split(":")[0].equals(order_attr))
				    			{
				    				attr_type = type.split(":")[1];
				    				break;
				    			}
				    		}
				    		System.out.println("tab_name: "+ tab_name + " attr_ls: " + attr_ls + " order_attr: " + order_attr + " attr_type: " + attr_type);
				    		System.out.println("order: " + order);
				    		sort_records_where(tab_name, attr_ls, pk_pos, order_attr, attr_type, order);
				    		if(parts[0].contains("*"))
				    		{
				    			//SELECT ALL
				    			display_all_main(tab_name+"_sort.tmp");
		        			
				    			clear_screen();
				    		}
				    		else
				    		{
				    			// PROJECTION
				    			String attr = parts[0].trim().split(" ")[1];
				    			if(!projection1(tab_name+"_sort.tmp",attr,attr_ls))
				    			{
				    				// error while projection
				    				// delete temp file created
				    				File f = new File(tab_name+"_sort.tmp");
				    				f.delete();
				    			}
				    		}
				    	}	        	
		        	
		        	
		        	// SORT PART NOW
				    /*
		    	    if(parts[0].contains("*"))
		    	    {
		    	    	//SELECT ALL   	
		    	    }
		    	    else
		    	    {
		    	    	// PROJECTION
		    	    }*/
		        }
		    	    catch(IOException e)
					{
						e.printStackTrace();
					}
		        }
		        else
		        {
		        	System.out.println("No records found!");
		        	clear_screen();
		        	return;
		        }
		        
			}
			
			// contains WHERE and
			// JOIN part
			else
			{
				// parts[0] - SELECT *_
				//parts[1] - movies,actors where movie_id=id AND year=a_year AND year>2000 order by title desc

				// parts_mod = parts[1].split("where");			
				// tab_name = parts_mod[0].trim();
				
				String[] tables = tab_name.trim().split(",");
				// movies - 0 , actors - 1
				ArrayList schema = new ArrayList<>();
				ArrayList pk = new ArrayList<>();
				int i=0;
				
				// read schema and pk for each tables
				System.out.println("tab_name: " + tab_name);
				
				for(String temp: tables)
				{
					System.out.println("tab: " + temp);
					File f = new File(temp.trim() + ".txt");
					if(!f.exists())
					{
						System.out.println("Table " + temp.trim() + " does not exist!");
						return;
					}
					try {
						FileInputStream f1 = new FileInputStream(f);
						BufferedReader br = new BufferedReader(new InputStreamReader(f1));
						String s1= br.readLine();
						for(String ff : s1.split(","))
							schema.add(ff.split(":")[0]);
						pk.add(br.readLine());
				    	br.close();
				    	
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				System.out.println("schema: "+ schema);
				System.out.println("pk: " + pk);
				
				// WITHOUT SORT
					// parts_mod[1] = movie_id=id AND year=a_year AND year>2000 order by title desc
				
				String[] parts_order = parts_mod[1].trim().split("order by");
				String conditions = parts_order[0].trim();
					
					
					System.out.println("conditions: "+ conditions);
					System.out.println("tab_names " + tab_name);
					
					String[] conds = conditions.trim().split("AND");
					
					String join_conds ="";
					String normal_conds ="";
					
					int ii=0;
					for(String str:conds)
					{
						if(schema.indexOf(str.trim().split("=")[0])!= -1 &&
								schema.indexOf(str.trim().split("=")[1])!= -1)
						{
							join_conds += str;
						}
						else
						{
							normal_conds += str;
							if(ii < conds.length-1)
							{
								normal_conds += "AND";
							}
						}
						ii++;
					}
					System.out.println("join conds:" + join_conds);
					System.out.println("normal conds: "  + normal_conds);
					
					boolean  flag = false;
 					//conds is all conditions
					// SUPPORTS ONLY ONE JOIN CONDITION OVER PK VALUES
					//if(schema.indexOf(str.trim().split("=")[0])!= -1 &&
					//			schema.indexOf(str.trim().split("=")[1])!= -1)
					if(join_conds != "")
					{
							// both are attributes in the conditions 
							
							//join conditions
							System.out.println("comes here/////////////////");
							if(!join_where_conds(join_conds.trim(),tab_name))
							{
								return;
							}
					}
					if(normal_conds != "")
					{
							File file = new File(tables[0]+"_"+tables[1]+".tmp");
							FileInputStream fis = new FileInputStream(file);
							BufferedReader br = new BufferedReader(new InputStreamReader(fis));
							
							String schema_new = br.readLine();
							ArrayList attr_ls = new ArrayList<>();
							for(String ss : schema_new.split(","))
							{
								attr_ls.add(ss.split(":")[0]);
							}
							br.close();
							if(!search_record_where(tables[0]+"_"+tables[1]+".tmp", normal_conds.trim(), attr_ls))
							{
								br.close();
								return;
							}
							// there was normal where condition other than join ones
							flag = true;
						
					}
					
					if(flag == false && !parts_mod[1].contains("order by"))
					{
						display_all_main(tables[0]+"_"+tables[1]+".tmp");
						File f = new File(tables[0]+"_"+tables[1]+".tmp");
						if(!f.delete())
						{
							System.out.println("Could not delete " + f + "!!!!");
						}
					}
					else
					{

						if(!parts_mod[1].contains("order by"))
						{
							// NO SORT
							display_all_main(tables[0]+"_"+tables[1]+".tmp.tmp");
							System.out.println("comes here././././");
							File f = new File(tables[0]+"_"+tables[1]+".tmp");
							System.out.println(f);
							if(!f.delete())
							{
								System.out.println("Could not delete " + f);
								return;
							}
						}
						else
						{
							String order = null;
							String order_attr = null;
							String order_type = null;
							
							String tail = parts_order[1].trim();
									
							if(tail.contains("DESC"))
							{
								order = "DESC";
								order_attr = tail.split("DESC")[0].trim();
							}
							else if(tail.contains("ASC"))
							{
								order = "ASC";					
								order_attr = tail.split("ASC")[0].trim();
							}
							else
							{
								order = "ASC";
								order_attr = tail;
							}
				    		String attr_type = null;
				    		
				    		File f = new File(tables[0]+"_"+tables[1]+".tmp");
				    		FileInputStream fis = new FileInputStream(f);
				    		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
				    		String sch = br.readLine();
				    		String two_pk = br.readLine();
				    		
				    		System.out.println("SCHEMA:=> " + sch);
				    		
				    		ArrayList attr_ls = new ArrayList<>();
				    		for(String type : sch.split(","))
				    		{
				    			attr_ls.add(type.split(":")[0]);
				    		}
				    		for(String type : sch.split(","))
				    		{
				    			attr_ls.add(type.split(":")[0]);
				    			if(type.split(":")[0].equals(order_attr))
				    			{
				    				attr_type = type.split(":")[1];
				    				break;
				    			}
				    		}
				    		br.close();
				    		System.out.println("attr_ls:" + attr_ls + " order_attr: "  + order_attr + " attr_type: " + attr_type + " order: "+ order);
							sort_records_join(tables[0]+"_"+tables[1]+".tmp", attr_ls, order_attr, attr_type, order);
							
							//tab_name+".tmp"
							//check for projection
							
							if(!parts[0].contains("*"))
							{
								// projection
								System.out.println("comes for projection./././");
								String attr = parts[0].split(" ")[1].trim();
								projection1(tables[0]+"_"+tables[1]+".tmp.tmp", attr, attr_ls);
							}
							else
							{
								File fd= new File(tables[0]+"_"+tables[1]+".tmp");
								fd.delete();
							}
							
							
							
							
							//title desc
							//title
						}
					
					}		
			}				
		}
		
		//does not contain WHERE
		else
		{
			String tail = parts[1].trim();
			//  'movies order by title desc'
			// 'movies order by title'
			String order = null;
			String order_attr = null;
			String order_type;
			
			boolean flag = false; // false means no ordering 
			if(!tail.contains("order by"))
			{
				tab_name = tail;
				flag = false;
			}
			else
			{
				tab_name = tail.split("order by")[0].trim();
				if(tail.split("order by")[1].contains("desc"))
				{
					order = "DESC";
					order_attr = tail.split("order by")[1].trim().split(" desc")[0];
				}
				else if (tail.split("order by")[1].contains("asc"))
				{
					order = "ASC";
					order_attr = tail.split("order by")[1].trim().split(" asc")[0];
				}
				else
				{
					order = "ASC";
					order_attr = tail.split("order by")[1].trim();
				}
				
				flag = true;
			}
			
			File fp = new File(tab_name + ".txt");
	        if(!fp.exists())
	        {
	            System.out.println("\nTable "+ fp + " does not exist!");
	            clear_screen();
	            return;
	        }
			FileInputStream fis;
			try {
				//SELECT ALL
	    		
				fis = new FileInputStream(tab_name+".txt");
				BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		    	
				String schema = br.readLine();
		    	String pk = br.readLine();
		    	int pk_pos = Integer.parseInt(pk.split(",")[1]);
		    	br.close();
		    	String[] temp = schema.split(",");
		    	String attr_type = null;
				for(String type : temp)
				{
					if(type.split(":")[0].equals(order_attr))
					{
						attr_type = type.split(":")[1];
						break;
					}
				}	
		    	ArrayList<String> attr_ls = new ArrayList<String>();
		    	for(String s : temp)
		    	{
		    		attr_ls.add(s.split(":")[0]);
		    	}
		    	
		    	if(parts[0].contains("*") && !parts[0].contains(","))
				{
		    		if(check_for_records(tab_name))
		    		{
		    			if(flag == false)
		    				display_all_main(tab_name+".txt");
		    			else
		    			{
		    				//sort into temp file and then display all 
		    			
		    				sort_records(tab_name, attr_ls, pk_pos, order_attr, attr_type, order);
		    				display_all_main(tab_name+".tmp");
		    				//sort_records(String tab_name, ArrayList attr_ls, 
		    				//int pk_pos,String attr, String attr_type, String order)	
		    			}
		    		}
		    		else
		    		{
		    			System.out.println("No records found!");
		    			clear_screen();
		    			return;
		    		}
		    	}
		    	
				else
				{
					//PROJECTION
					if(check_for_records(tab_name))
					{	if(flag == false)
						{
							String attr = parts[0].trim().split(" ")[1];
							projection1(tab_name+".txt",attr,attr_ls);
						}
						else
						{
							sort_records(tab_name, attr_ls, pk_pos, order_attr, attr_type, order);
							String attr = parts[0].trim().split(" ")[1];
							projection1(tab_name+".tmp",attr,attr_ls);
						}
					}
					else
					{
						System.out.println("No records found!");
						clear_screen();
						return;
					}
				}
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		
		long endTime = System.currentTimeMillis();
		System.out.println("Without Index: Select query took " + (endTime-startTime) + "milli secs");
		clear_screen();
	}
	
	
	private static void sort_records_join(String tab_name, ArrayList attr_ls, String attr,
			String attr_type, String order) 
	{
		try {
			FileReader fileReader = new FileReader(tab_name);
            BufferedReader br = new BufferedReader(fileReader);
            
            File tempFile = new File(tab_name + ".tmp");
            PrintWriter pw = new PrintWriter(new FileWriter(tempFile));
            
            
            //System.out.println("attr_ls: " + attr_ls+ " attr_type: " + attr_type + "order attr: " + attr + "order : " + order);
            
			pw.println(br.readLine());
			pw.println(br.readLine());
			int position = attr_ls.indexOf(attr);
			System.out.println("position: " + position);
			if(position == -1)
			{
				System.out.println("\nEnter correct attribute name");
				br.close();
				pw.close();
				clear_screen();
				return;
			}
			
			LinkedHashMap ls = new LinkedHashMap();
			
			String line = null;
			while((line  = br.readLine()) != null)
			{
				
				System.out.println("line: " + line);
				System.out.println("hp val: " + line.split(",")[position]);
				ls.put(line,line.split(",")[position]);
			}
			//System.out.println("Before sort : ls " + ls);
			
			List<Entry> list = new LinkedList<Entry>(ls.entrySet());

	        // Sorting the list based on values
	        Collections.sort(list, new Comparator<Entry>()
	        {
	            public int compare(Entry o1,Entry o2)
	            {
	                if (order.equals("ASC"))
	                {
	                	if(attr_type.equals("int"))
	                	{
	                		System.out.println(o1.getValue() + " " + o2.getValue());
	                		return Integer.parseInt((String) o1.getValue()) - Integer.parseInt((String) o2.getValue());
	                	}
	                	else if(attr_type.equals("float"))
	                	{
	                		return Float.compare(Float.parseFloat(o1.getValue().toString()), 
	                				Float.parseFloat(o2.getValue().toString()));
	                	}
	                	else if(attr_type.equals("double"))
	                	{
	                		return Double.compare(Double.parseDouble(o1.getValue().toString()), 
	                				Double.parseDouble(o2.getValue().toString()));
	                	}
	                	else if(attr_type.equals("String"))
	                	{
	                		return o1.getValue().toString().compareTo(o2.getValue().toString());
	                	}
	                }
	                else
	                {
	                	if(attr_type.equals("int"))
	                	{
	                		return Integer.parseInt((String) o2.getValue()) - Integer.parseInt((String) o1.getValue());
	                	}
	                	else if(attr_type.equals("float"))
	                	{
	                		return Float.compare(Float.parseFloat(o2.getValue().toString()), 
	                				Float.parseFloat(o1.getValue().toString()));
	                	}
	                	else if(attr_type.equals("double"))
	                	{
	                		return Double.compare(Double.parseDouble(o2.getValue().toString()), 
	                				Double.parseDouble(o1.getValue().toString()));
	                	}
	                	else if(attr_type.equals("String"))
	                	{
	                		return o2.getValue().toString().compareTo(o1.getValue().toString());
	                	}
	                }
					return 0;
	            }
	        });

	        Map sortedMap = new LinkedHashMap();
	        for (Entry entry : list)
	        {
	            sortedMap.put(entry.getKey(), entry.getValue());
	        }
	        
	        //System.out.println("After sort: " + sortedMap);
	        
			br.close();
			
			
			//fileReader = new FileReader(tab_name);
            //br = new BufferedReader(fileReader);
			// this ArrayList contains values (PK) in sorting order;
			// sorts Map by key i.e. position (order) in which record appears
			System.out.println();
			
			//br.readLine();
			//br.readLine();
			List final_ls = new ArrayList<>(sortedMap.keySet());
			
			
			
			System.out.println("FINAL_LS : " + final_ls);
			/*
			TreeMap<Integer, String> hp = new TreeMap<Integer, String>();
			// now read lines and find pk position in arraylist to determine the order
			while((line  = br.readLine()) != null)
			{
				//System.out.println("record: " + line);
				//System.out.println("pk value in record" + line.split(",")[pk_pos]);
				hp.put(final_ls.indexOf(line.split(",")[pk_pos1]),line);
			}
			// Added whole record and position (order) in which it appears
			//System.out.println("treemap " + hp);
			br.close();*/
			
			System.out.println("----------------------------------------");
			for(int i=0;i<final_ls.size();i++)
			{	
				System.out.println(final_ls.get(i));
				pw.print(final_ls.get(i));
				if(i<final_ls.size()-1)
					pw.println();
				
			}
			pw.flush();
			pw.close();
			//br.close();
			
			//display_all_main(tab_name+".tmp");
			
			File f = new File(tab_name);
			f.delete();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}

	}
	private static boolean join_where_conds(String str, String tab_name) throws IOException 
	{
		// str ->    movie_id=id
		String[] tab = tab_name.split(",");
 		File f = new File(tab[0]+"_"+tab[1]+".tmp");
 		
 		File f1 = new File(tab[0]+".txt");
 		File f2 = new File(tab[1]+".txt");
 		FileInputStream fis1 = new FileInputStream(f1);
 		FileInputStream fis2 = new FileInputStream(f2);
 		
 		BufferedReader br1 = new BufferedReader(new InputStreamReader(fis1));
 		BufferedReader br2 = new BufferedReader(new InputStreamReader(fis2));
 		
 		String schema1 = br1.readLine(); 
 		String schema2 = br2.readLine();
 		String pk1 = br1.readLine(); 
 		String pk2 = br2.readLine();
 		// read schema and pk
 	
 		String input = "";
 		input += schema1;
 		input += ","+schema2;
 		input += System.lineSeparator();
 		
 		//int pos1 = Integer.parseInt(pk1.split(",")[1]);
 		//int pos2 = Integer.parseInt(pk2.split(",")[1]);
 		
 		input += pk1;
 		input += "," + pk2;
 		input += System.lineSeparator();
 		
 		HashMap hp1 = new HashMap();
 		
 		for(String tt : schema1.split(","))
 		{
 			hp1.put(tt.split(":")[0], tt.split(":")[1]);
 		}
 		HashMap hp2 = new HashMap();
 		
 		for(String tt : schema2.split(","))
 		{
 			hp2.put(tt.split(":")[0], tt.split(":")[1]);
 		}
 		
 		if(!hp1.get(str.split("=")[0]).equals(hp2.get(str.split("=")[1])))
 		{
 			// if attributes types are not same
 			System.out.println("JOIN can not be applied on attributes with different types");
 			br1.close();
 			br2.close();
 			f.delete();
 			return false;
 		}
 		
 		ArrayList attr_ls1 = new ArrayList<>();
 		for(String mod : schema1.split(","))
 		{
 			attr_ls1.add(mod.split(":")[0]);
 		}
 		
 		ArrayList attr_ls2 = new ArrayList<>();
 		for(String mod : schema2.split(","))
 		{
 			attr_ls2.add(mod.split(":")[0]);
 		}
 		
 		
 		int pos1 = attr_ls1.indexOf(str.split("=")[0].trim());
 		int pos2 = attr_ls2.indexOf(str.split("=")[1].trim());
 		
 		System.out.println("file " + f);
 		
 		String line1 = null;
 		String line2 = null;
 		
 		line1 = br1.readLine();
 		line2 = br2.readLine();
 		
 		while(line1!=null && line2!=null)
 		{
 			if(!line1.equals("#") && !line2.equals("#"))
 			{
 			//System.out.println("line1: " + line1);
 			//System.out.println("line2: " + line2);
 			//System.out.println(Integer.parseInt(line1.split(",")[pos1]));
 			//System.out.println(Integer.parseInt(line2.split(",")[pos2]));
 			if(line1.split(",")[pos1].equals(line2.split(",")[pos2]))
 			{
 				input += line1 + "," + line2;
 				input += System.lineSeparator();
 				line1 = br1.readLine();
 		 		line2 = br2.readLine();
 			}
 			else if(Integer.parseInt(line1.split(",")[pos1]) < 
 					Integer.parseInt((line2.split(",")[pos2])))
 			{
 				line1 = br1.readLine();
 			}
 			else if(Integer.parseInt(line1.split(",")[pos1]) > 
 					Integer.parseInt((line2.split(",")[pos2])))
 			{
 		 		line2 = br2.readLine();
 			}
 			}
 			else
 			{
 				if(line1.equals("#"))
 				{
 					line1= br1.readLine();
 				}
 				if(line2.equals("#"))
 				{
 					line2 = br2.readLine();
 				}
 			}
 		}
 		FileOutputStream os = new FileOutputStream(f);
        os.write(input.getBytes());
        
        os.close();
        br1.close();
        br2.close();
 		
 		return true;
	}
	private static boolean search_record_where(String tab_name, String conditions, ArrayList<String> attr_ls) throws IOException 
	{
		LinkedHashMap<String, Integer> hp = new LinkedHashMap<String, Integer>();
        
		//conditions  = rating>7 AND year>2000
		// conditions = rating>7 AND year>2000 movie_id = 123
		String split_cond = null;
		if(conditions.contains("AND"))
		{
			split_cond = "AND";
		}
		else if(conditions.contains("OR"))
		{
			split_cond = "OR";
		}
		else
		{
			split_cond = ",";
		}
		
		
		System.out.println("cond: "  +conditions);
        for(String tmp : conditions.split(split_cond))
        {
        	String attr_name = null;
        	if(tmp.contains("<"))
        		attr_name = tmp.trim().split("<")[0];
        	else if(tmp.contains(">"))
        		attr_name = tmp.trim().split(">")[0];
        	else if(tmp.contains("="))
        		attr_name = tmp.trim().split("=")[0];
        	
        	int position = attr_ls.indexOf(attr_name);
			if(position == -1)
			{
				System.out.println("\nEnter correct attribute name " +attr_name);
				clear_screen();
				return false;
			}
        	hp.put(attr_name,position);
        }
        
        //System.out.println("hp" + hp);
        
        // write intermediate record into temp file
        
        FileInputStream fis;
		fis = new FileInputStream(tab_name);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        
        
        File tempFile = new File(tab_name + ".tmp");
        PrintWriter pw = new PrintWriter(new FileWriter(tempFile));
        
        pw.println(br.readLine());
        pw.println(br.readLine());
        
        String line = null;
        while ((line = br.readLine()) != null) 
        {
        	if(!line.equals("#"))
        	{
        		//System.out.println("Line : "+ line);
        		int i=0;
        		int record_flag = 0;
        	//System.out.println("hp:" + hp);
        	for (Entry<String, Integer> entry : hp.entrySet()) 
        	{
        	    String key = entry.getKey(); // year
        	    Object value = entry.getValue(); // 2
        	    
        	    String val = line.split(",")[(int) value]; // year in the record 
        	    String cond_line = conditions.split(split_cond)[i++].trim(); // year>1990
        	    
        	    // 1996
        	    // year>1990
        	 
        	    // If val does not contain any value or it is null then 
        	    // do not process that record
        	    //System.out.println("val : " + val);
        	    
        	    //System.out.println("cnd line: " + cond_line + "line : " + line);
        	    if(!val.equals("null"))
        	    {
        	    	if(cond_line.contains("<"))
        	    	{
        	    		if(val.matches(".*\\d+.*"))
        	    		{
        	    			//good attr for condition 
        	    			if(attr_ls.get((int)value).toString().contains("int"))
        	    			{
        	    				//int
        	    				int val1 =  Integer.parseInt(val);
		        	    		int val2 = Integer.parseInt(cond_line.split("<")[1]); 
		        	    		if(val1 < val2)
		        	    		{
		        	    			record_flag++;
		        	    		}
        	    			}
        	    			else
        	    			{
        	    				//float
        	    				float val1 = Float.parseFloat(val);
        	    				float val2 = Float.parseFloat(cond_line.split("<")[1]);
        	    				if(val1 < val2)
		        	    		{
		        	    			record_flag++;
		        	    		}
        	    			}    	    			
        	    		}
        	    		else
        	    		{
        	    			System.out.println("\n< or > ops does not work for strings");
        	    			br.close();
        	    			pw.close();
        	    			//tempFile.delete();
        	    			return false;
        	    		}	        	    	
        	    	}
        	    	else if(cond_line.contains(">"))
        	    	{
        	    		if(val.matches(".*\\d+.*"))
        	    		{
        	    			//good attr for condition 
        	    			try
        	    			{
        	    				//int
        	    				int val1 =  Integer.parseInt(val);
		        	    		int val2 = Integer.parseInt(cond_line.split(">")[1]); 
		        	    		if(val1 > val2)
		        	    		{
		        	    			record_flag++;
		        	    		}
        	    			}
        	    			catch(NumberFormatException e)
        	    			{
        	    				//float
        	    				float val1 = Float.parseFloat(val);
        	    				float val2 = Float.parseFloat(cond_line.split(">")[1]);
        	    				if(val1 > val2)
		        	    		{
		        	    			record_flag++;
		        	    		}
        	    			}    	    			
        	    		}
        	    		else
        	    		{
        	    			System.out.println("\n< or > ops does not work for strings");
        	    			br.close();
        	    			pw.close();
        	    			//tempFile.delete();
        	    			return false;
        	    		}
        	    	}
        	    	else if(cond_line.contains("="))
        	    	{
        	    		if(val.equals(cond_line.split("=")[1]))
        	    		{
        	    			record_flag++;
        	    		}
        	    	}
        	    }
        	    
        	} // end-for
        	// only if all conditions are true then enter that line into tempfile
        	
        	
        	//System.out.println("flag : " +record_flag);
        	if(split_cond.equals("AND") && record_flag == hp.size())
        	{
        		//System.out.println("comes AND");
        			pw.println(line);
        			pw.flush();
        			record_flag = 0;
        	}	
        	else if(split_cond.equals("OR") && record_flag !=0)
        	{
        		//System.out.println("comes OR");
        		pw.println(line);
    			pw.flush();
    			record_flag = 0;
        	}
        	else if(record_flag != 0 && !(split_cond.equals("AND") || split_cond.equals("OR")))
        	{
        		//System.out.println("comes single condition");
        		pw.println(line);
    			pw.flush();
    			record_flag = 0;
        	}
        }
       }
        
        // end-while
        
        pw.close();
        br.close();
        return true;
	}
	private static boolean search_record(String tab_name, String conditions, ArrayList<String> attr_ls) throws IOException 
	{
		LinkedHashMap<String, Integer> hp = new LinkedHashMap<String, Integer>();
        
		//conditions  = rating>7 AND year>2000
		// conditions = rating>7 AND year>2000 movie_id = 123
		String split_cond = null;
		
		if(conditions.contains("AND"))
		{
		String[] conds_mod = conditions.split("AND");
		if(conds_mod.length == 1)
		{
			System.out.println("No condition provided!!!");
			return false;
		}
		}
		if(conditions.contains("AND"))
		{
			split_cond = "AND";
		}
		else if(conditions.contains("OR"))
		{
			split_cond = "OR";
		}
		else
		{
			split_cond = ",";
		}
		
		//System.out.println("cond: "  +split_cond);
        for(String tmp : conditions.split(split_cond))
        {
        	String attr_name = null;
        	
        	
        	
        	if(tmp.contains("<"))
        	{
        		if(tmp.trim().split("<").length == 1)
        		{
        			System.out.println("No value provided!!!");
        			return false;
        		}
        		attr_name = tmp.trim().split("<")[0];
        	}
        	else if(tmp.contains(">"))
        	{
        		if(tmp.trim().split(">").length == 1)
        		{
        			System.out.println("No value provided!!!");
        			return false;
        		}
        		attr_name = tmp.trim().split(">")[0];
        	}
        	else if(tmp.contains("="))
        	{
        		if(tmp.trim().split("=").length == 1)
        		{
        			System.out.println("No value provided!!!");
        			return false;
        		}
        		attr_name = tmp.trim().split("=")[0];
        	}
        	
        	System.out.println("attr ls " + attr_ls);
        	System.out.println("attr name : " + attr_name);
        	int position = attr_ls.indexOf(attr_name);
			if(position == -1)
			{
				System.out.println("\nEnter correct attribute name................ " +attr_name);
				clear_screen();
				return false;
			}
        	hp.put(attr_name,position);
        }
        
        //System.out.println("hp" + hp);
        
        // write intermediate record into temp file
        
        FileInputStream fis;
		fis = new FileInputStream(tab_name+".txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        
        
        File tempFile = new File(tab_name + ".tmp");
        PrintWriter pw = new PrintWriter(new FileWriter(tempFile));
        
        pw.println(br.readLine());
        pw.println(br.readLine());
        
        String line = null;
        while ((line = br.readLine()) != null) 
        {
        	if(!line.equals("#"))
        	{
        		int i=0;
        	
        	int record_flag = 0;
        	//System.out.println("hp:" + hp);
        	for (Entry<String, Integer> entry : hp.entrySet()) 
        	{
        	    String key = entry.getKey(); // year
        	    Object value = entry.getValue(); // 2
        	    
        	    String val = line.split(",")[(int) value]; // year in the record 
        	    String cond_line = conditions.split(split_cond)[i++].trim(); // year>1990
        	    
        	    // 1996
        	    // year>1990
        	 
        	    // If val does not contain any value or it is null then 
        	    // do not process that record
        	    //System.out.println("val : " + val);
        	    
        	    //System.out.println("cnd line: " + cond_line + "line : " + line);
        	    if(!val.equals("null"))
        	    {
        	    	if(cond_line.contains("<"))
        	    	{
        	    		if(val.matches(".*\\d+.*"))
        	    		{
        	    			//good attr for condition 
        	    			if(attr_ls.get((int)value).toString().contains("int"))
        	    			{
        	    				//int
        	    				int val1 =  Integer.parseInt(val);
		        	    		int val2 = Integer.parseInt(cond_line.split("<")[1]); 
		        	    		if(val1 < val2)
		        	    		{
		        	    			record_flag++;
		        	    		}
        	    			}
        	    			else
        	    			{
        	    				//float
        	    				float val1 = Float.parseFloat(val);
        	    				float val2 = Float.parseFloat(cond_line.split("<")[1]);
        	    				if(val1 < val2)
		        	    		{
		        	    			record_flag++;
		        	    		}
        	    			}    	    			
        	    		}
        	    		else
        	    		{
        	    			System.out.println("\n< or > ops does not work for strings");
        	    			br.close();
        	    			pw.close();
        	    			//tempFile.delete();
        	    			return false;
        	    		}	        	    	
        	    	}
        	    	else if(cond_line.contains(">"))
        	    	{
        	    		if(val.matches(".*\\d+.*"))
        	    		{
        	    			//good attr for condition 
        	    			try
        	    			{
        	    				//int
        	    				int val1 =  Integer.parseInt(val);
		        	    		int val2 = Integer.parseInt(cond_line.split(">")[1]); 
		        	    		if(val1 > val2)
		        	    		{
		        	    			record_flag++;
		        	    		}
        	    			}
        	    			catch(NumberFormatException e)
        	    			{
        	    				//float
        	    				float val1 = Float.parseFloat(val);
        	    				float val2 = Float.parseFloat(cond_line.split(">")[1]);
        	    				if(val1 > val2)
		        	    		{
		        	    			record_flag++;
		        	    		}
        	    			}    	    			
        	    		}
        	    		else
        	    		{
        	    			System.out.println("\n< or > ops does not work for strings");
        	    			br.close();
        	    			pw.close();
        	    			//tempFile.delete();
        	    			return false;
        	    		}
        	    	}
        	    	else if(cond_line.contains("="))
        	    	{
        	    		if(val.equals(cond_line.split("=")[1]))
        	    		{
        	    			record_flag++;
        	    		}
        	    	}
        	    }
        	    
        	} // end-for
        	// only if all conditions are true then enter that line into tempfile
        	
        	
        	//System.out.println("flag : " +record_flag);
        	if(split_cond.equals("AND") && record_flag == hp.size())
        	{
        		//System.out.println("comes AND");
        			pw.println(line);
        			pw.flush();
        			record_flag = 0;
        	}	
        	else if(split_cond.equals("OR") && record_flag !=0)
        	{
        		//System.out.println("comes OR");
        		pw.println(line);
    			pw.flush();
    			record_flag = 0;
        	}
        	else if(record_flag != 0 && !(split_cond.equals("AND") || split_cond.equals("OR")))
        	{
        		//System.out.println("comes single condition");
        		pw.println(line);
    			pw.flush();
    			record_flag = 0;
        	}
        }
        }
        
        // end-while
        
        pw.close();
        br.close();
        return true;
	}
	private static boolean projection1(String tab_name, String attr, ArrayList<String> attr_ls) 
	{
		LinkedHashMap<String, Integer> hp = new LinkedHashMap<String, Integer>();
		for(String str : attr.split(","))
		{
			int position = attr_ls.indexOf(str);
			if(position == -1)
			{
				System.out.println("\nEnter correct attribute name " + str);
				clear_screen();
				return false;
			}
			hp.put(str, position);
		}
		
		FileInputStream fis;
		try {
			File f = new File(tab_name);
			fis = new FileInputStream(tab_name);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
	    	
			String schema = br.readLine();
	    	String pk = br.readLine();
	    	
	    	String line = null;
			while ((line = br.readLine()) != null) 
	        {
				if(!line.equals("#"))
				{
					String[] record = line.split(",");
					int i = 1;
					for (Object value : hp.values()) 
					{
						//System.out.println("value: " + value);
						System.out.print(record[(int) value] + " | ");
					}  	 
					System.out.println();
				}
	        }
			br.close();
			if(tab_name.contains(".tmp"))
				f.delete();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		
		return true;
		
	}
	private static boolean check_for_records(String string) 
	{
		int record_count = 0;
        try
        {
        	FileInputStream fis_temp;
			fis_temp = new FileInputStream(string+".txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(fis_temp));
	    	String schema = br.readLine();
	    	String pk = br.readLine();
	    	
	    	while(br.readLine()!=null)
	    	{
	    		record_count++;
	    		
	    		// to skip scanning the whole file
	    		// even single record exists then break it
	    		if(record_count > 0)
	    			break;
	    	}
	    	if(record_count == 0)
	    	{
	    		br.close();
	    		return false;
	    	}
	    	br.close();
        }
        catch(IOException e)
        {
        	e.printStackTrace();
        }
		return true;
	}
	private static void show_tab_btree() 
	{
		System.out.println("Enter table name: ");
		String tab_name = sc.next();
		
		System.out.println();
		if(btree.get(tab_name) != null)
		{
			BPTree st = btree.get(tab_name);
			StringWriter out = new StringWriter();
			st.printXml(out);
			System.out.println(out);
		}
		else
		{
			System.out.println("No such table exists!");
			clear_screen();
		}
	}
	
	private static void clear_screen() 
	{
		System.out.println("\f\f\f\f\f\f\f\f\f\f");
	}
	
	private static void load_all_keys() throws IOException 
	{
		String pathName = System.getProperty("user.dir");
		//System.out.println("pathname is : " + pathName);
		
		File dir = new File(pathName);
        File[] fList = dir.listFiles();		
        
        String name;
        int check = 0;
        for (File file : fList)
        {
      	  name=file.getName();
      	  if(name.contains(".txt"))
      	  {
      		  check = 1;
      		  String fname=name.substring(0, name.lastIndexOf("."));
      		
      		  
      		  // st stores PK value in table with line num
      		BPTree st1 = new BPTree();
        	
      		FileInputStream fis = new FileInputStream(name);
        	BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        	String schema = br.readLine();
        	String pk = br.readLine();
        	String[] pk_val = pk.split(",");
        	//System.out.println(pk);
        	int pk_pos = Integer.parseInt(pk_val[1]); // PK_position
        	
        	//System.out.println("file: "  +  file+"  pk pos: "+  pk_pos);
        	// now reading records line by line
        	String record = br.readLine();
        	ArrayList<Integer> ls;
        	
        	int cntr = 3;
        	if(record == null)
        	{
        		btree.put(fname, st1);
        		ls = new ArrayList<>();
        		hp.put(fname, ls);
        		continue;
        	}
        	else
        	{
        		ls = new ArrayList<Integer>();
        		String line = record;
        		//System.out.println(line);
        		while(line!=null)
        		{
        			if(!line.equals("#"))
        			{	
        				String[] pk_values = line.split(",");
        				//System.out.println(pk_values[pk_pos]);
        				ls.add(Integer.parseInt(pk_values[pk_pos]));
        				st1.put(pk_values[pk_pos], cntr++);
        			}
        			
        			line = br.readLine();
        		}
        		hp.put(fname, ls);
          		btree.put(fname, st1);
        		
        	}
        	//hp.put(fname, ls);
      		//System.out.println(fname);
      		br.close();
      	  }
        }
        
	}
	private static void sort_records_where(String tab_name, ArrayList<String> attr_ls, int pk_pos, String attr,
			String attr_type, String order) 
	{
		try {
			File f = new File(tab_name+".tmp");
			FileReader fileReader = new FileReader(tab_name+".tmp");
            BufferedReader br = new BufferedReader(fileReader);
            
            File tempFile = new File(tab_name +"_sort" + ".tmp");
            PrintWriter pw = new PrintWriter(new FileWriter(tempFile));
            
			pw.println(br.readLine());
			pw.println(br.readLine());
			int position = attr_ls.indexOf(attr);
			if(position == -1)
			{
				System.out.println("\nEnter correct attribute name");
				br.close();
				pw.close();
				clear_screen();
				return;
			}
			
			LinkedHashMap ls = new LinkedHashMap();
			
			String line = null;
			while((line  = br.readLine()) != null)
			{
				//System.out.println("line: " + line);
				// added attributes
				ls.put(line.split(",")[pk_pos],line.split(",")[position]);
			}
			//System.out.println("Before sort : ls " + ls);
			
			List<Entry> list = new LinkedList<Entry>(ls.entrySet());

	        // Sorting the list based on values
	        Collections.sort(list, new Comparator<Entry>()
	        {
	            public int compare(Entry o1,Entry o2)
	            {
	                if (order.equals("ASC"))
	                {
	                	if(attr_type.equals("int"))
	                	{
	                		System.out.println(o1.getValue() + " " + o2.getValue());
	                		return Integer.parseInt((String) o1.getValue()) - Integer.parseInt((String) o2.getValue());
	                	}
	                	else if(attr_type.equals("float"))
	                	{
	                		return Float.compare(Float.parseFloat(o1.getValue().toString()), 
	                				Float.parseFloat(o2.getValue().toString()));
	                	}
	                	else if(attr_type.equals("double"))
	                	{
	                		return Double.compare(Double.parseDouble(o1.getValue().toString()), 
	                				Double.parseDouble(o2.getValue().toString()));
	                	}
	                	else if(attr_type.equals("String"))
	                	{
	                		return o1.getValue().toString().compareTo(o2.getValue().toString());
	                	}
	                }
	                else
	                {
	                	if(attr_type.equals("int"))
	                	{
	                		return Integer.parseInt((String) o2.getValue()) - Integer.parseInt((String) o1.getValue());
	                	}
	                	else if(attr_type.equals("float"))
	                	{
	                		return Float.compare(Float.parseFloat(o2.getValue().toString()), 
	                				Float.parseFloat(o1.getValue().toString()));
	                	}
	                	else if(attr_type.equals("double"))
	                	{
	                		return Double.compare(Double.parseDouble(o2.getValue().toString()), 
	                				Double.parseDouble(o1.getValue().toString()));
	                	}
	                	else if(attr_type.equals("String"))
	                	{
	                		return o2.getValue().toString().compareTo(o1.getValue().toString());
	                	}
	                }
					return 0;
	            }
	        });

	        Map sortedMap = new LinkedHashMap();
	        for (Entry entry : list)
	        {
	            sortedMap.put(entry.getKey(), entry.getValue());
	        }
	        
	        //System.out.println("After sort: " + sortedMap);
	        
			br.close();
			
			fileReader = new FileReader(tab_name+".tmp");
            br = new BufferedReader(fileReader);
			// this ArrayList contains values (PK) in sorting order;
			// sorts Map by key i.e. position (order) in which record appears
			System.out.println();
			
			br.readLine();
			br.readLine();
			List final_ls = new ArrayList<>(sortedMap.keySet());
			
			//System.out.println("final_ls : " + final_ls);
			
			TreeMap<Integer, String> hp = new TreeMap<Integer, String>();
			// now read lines and find pk position in arraylist to determine the order
			while((line  = br.readLine()) != null)
			{
				//System.out.println("record: " + line);
				//System.out.println("pk value in record" + line.split(",")[pk_pos]);
				hp.put(final_ls.indexOf(line.split(",")[pk_pos]),line);
			}
			// Added whole record and position (order) in which it appears
			//System.out.println("treemap " + hp);
			br.close();
			int i=0;
			for(Object value : hp.values())
			{
				/*
				for(String dis : value.toString().split(","))
				{
					System.out.print(dis + " | ");
				}	
				System.out.println();
				*/
				pw.print(value);
				if(i<hp.size()-1)
					pw.println();
				
				i++;
			}
			pw.flush();
			pw.close();
			br.close();
			f.delete();
			
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}

	}
	private static void sort_records(String tab_name, ArrayList attr_ls, 
			int pk_pos,String attr, String attr_type, String order) 
	{
		try {
			FileReader fileReader = new FileReader(tab_name+".txt");
            BufferedReader br = new BufferedReader(fileReader);
            
            File tempFile = new File(tab_name + ".tmp");
            PrintWriter pw = new PrintWriter(new FileWriter(tempFile));
            
			pw.println(br.readLine());
			pw.println(br.readLine());
			int position = attr_ls.indexOf(attr);
			if(position == -1)
			{
				System.out.println("\nEnter correct attribute name");
				br.close();
				pw.close();
				clear_screen();
				return;
			}
			
			LinkedHashMap ls = new LinkedHashMap();
			
			String line = null;
			while((line  = br.readLine()) != null)
			{
				// added attributes
				ls.put(line.split(",")[pk_pos],line.split(",")[position]);
			}
			//System.out.println("Before sort : ls " + ls);
			
			List<Entry> list = new LinkedList<Entry>(ls.entrySet());

	        // Sorting the list based on values
	        Collections.sort(list, new Comparator<Entry>()
	        {
	            public int compare(Entry o1,Entry o2)
	            {
	                if (order.equals("ASC"))
	                {
	                	if(attr_type.equals("int"))
	                	{
	                		System.out.println(o1.getValue() + " " + o2.getValue());
	                		return Integer.parseInt((String) o1.getValue()) - Integer.parseInt((String) o2.getValue());
	                	}
	                	else if(attr_type.equals("float"))
	                	{
	                		return Float.compare(Float.parseFloat(o1.getValue().toString()), 
	                				Float.parseFloat(o2.getValue().toString()));
	                	}
	                	else if(attr_type.equals("double"))
	                	{
	                		return Double.compare(Double.parseDouble(o1.getValue().toString()), 
	                				Double.parseDouble(o2.getValue().toString()));
	                	}
	                	else if(attr_type.equals("String"))
	                	{
	                		return o1.getValue().toString().compareTo(o2.getValue().toString());
	                	}
	                }
	                else
	                {
	                	if(attr_type.equals("int"))
	                	{
	                		return Integer.parseInt((String) o2.getValue()) - Integer.parseInt((String) o1.getValue());
	                	}
	                	else if(attr_type.equals("float"))
	                	{
	                		return Float.compare(Float.parseFloat(o2.getValue().toString()), 
	                				Float.parseFloat(o1.getValue().toString()));
	                	}
	                	else if(attr_type.equals("double"))
	                	{
	                		return Double.compare(Double.parseDouble(o2.getValue().toString()), 
	                				Double.parseDouble(o1.getValue().toString()));
	                	}
	                	else if(attr_type.equals("String"))
	                	{
	                		return o2.getValue().toString().compareTo(o1.getValue().toString());
	                	}
	                }
					return 0;
	            }
	        });

	        Map sortedMap = new LinkedHashMap();
	        for (Entry entry : list)
	        {
	            sortedMap.put(entry.getKey(), entry.getValue());
	        }
	        
	        //System.out.println("After sort: " + sortedMap);
	        
			br.close();
			
			fileReader = new FileReader(tab_name+".txt");
            br = new BufferedReader(fileReader);
			// this ArrayList contains values (PK) in sorting order;
			// sorts Map by key i.e. position (order) in which record appears
			System.out.println();
			
			br.readLine();
			br.readLine();
			List final_ls = new ArrayList<>(sortedMap.keySet());
			
			//System.out.println("final_ls : " + final_ls);
			
			TreeMap<Integer, String> hp = new TreeMap<Integer, String>();
			// now read lines and find pk position in arraylist to determine the order
			while((line  = br.readLine()) != null)
			{
				//System.out.println("record: " + line);
				//System.out.println("pk value in record" + line.split(",")[pk_pos]);
				hp.put(final_ls.indexOf(line.split(",")[pk_pos]),line);
			}
			// Added whole record and position (order) in which it appears
			//System.out.println("treemap " + hp);
			br.close();
			int i=0;
			for(Object value : hp.values())
			{
				/*
				for(String dis : value.toString().split(","))
				{
					System.out.print(dis + " | ");
				}	
				System.out.println();
				*/
				pw.print(value);
				if(i<hp.size()-1)
					pw.println();
				
				i++;
			}
			pw.flush();
			pw.close();
			br.close();
			
			
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	private static void display_all_main(String table_name)
	{
		File fp = new File(table_name);
        if(!fp.exists())
        {
            System.out.println("\nTable "+ fp +" does not exist!");
            clear_screen();
            return;
        }
        System.out.println("tab (display):" + table_name);
        try
        {	
        	FileInputStream fis = new FileInputStream(fp);
        	BufferedReader br = new BufferedReader(new InputStreamReader(fis));
    	
        	String schema = br.readLine();
        	String pk = br.readLine();
        	
        	String record = br.readLine();
        	if(record == null)
        	{
        		System.out.println("No records found!!");
        		br.close();
        		fp.delete();
        		return;
        	}
        	else
        	{
        		for(String tmp: record.split(","))
				{	
					System.out.print(tmp + " | ");
				}
        		
        		if(!record.equals("#"))
        		{
        			String line = record;
        			System.out.println("\n" + schema);
        			for(String var : schema.split(","))
        			{
        				System.out.print(var.split(":")[0] + " | ");
        			}
        			System.out.println("\n----------------------------------------------");
        			while(line!=null)
        			{
        				if(!line.equals("#"))
        				{
        					for(String tmp: line.split(","))
        					{	
        						System.out.print(tmp + " | ");
        					}
        					System.out.println();
        				}
        				line = br.readLine();
        			}
        		}
        		
        	}
        	System.out.println();
        	br.close();
        	if(table_name.contains(".tmp"))
        	{	
        		if(!fp.delete())
        		{
        			System.out.println("Could not delete the file");
        			return;
        		}
        	}
        }
        catch(IOException e)
        {
        	e.printStackTrace();
        }   
	}
	
	private static void show_table() 
	{
		System.out.println("\nEnter table name: ");
		String tab_name = sc.next();
		File fp = new File(tab_name + ".txt");
        if(!fp.exists())
        {
            System.out.println("\nTable " + fp + " does not exist!");
            return;
        }
        try 
        {
        	FileInputStream fis = new FileInputStream(fp);
        	BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        	System.out.println("----------------");
        	System.out.println("\nSchema:");
        	System.out.println(tab_name + "(" + br.readLine()+")");
        	br.close();
		} 
        catch (IOException e) 
        {
			e.printStackTrace();
		}
	}

	private static void list_all_tables() 
	{
		String pathName = System.getProperty("user.dir");
		//System.out.println("pathname is : " + pathName);
		
		File dir = new File(pathName);
        File[] fList = dir.listFiles();		
        
        String name;
        int check = 0;
        for (File file : fList){
      	  name=file.getName();
      	  if(name.contains(".txt"))
      	  {
      		  check = 1;
      		  String fname=name.substring(0, name.lastIndexOf("."));
      		  System.out.println(fname);
      	  }
        }
        if(check == 0)
        	System.out.println("\nNo tables in directory!");
        
        System.out.println();
	}

	private static void delete_table()
	{
		System.out.println("\nEnter the table to be deleted:\n");
        tableName=sc.next();
        File fp = new File(tableName + ".txt");
        //System.out.println(fp.getAbsolutePath());
        //System.out.println(tableName +" " +  fp);
        if(!fp.exists())
        {
   	     System.out.println("\nTable " +tableName+ " does not exist!\n");
        }
        else
        {	
        	if(!fp.delete())
        	{
        		System.out.println("Table coult not be deleted");
        	}
        	else
        		System.out.println("Table " + fp + " has been deleted" );
        }
        System.out.println();
	}

	private static void create_table() throws IOException 
	{
		//Runtime.getRuntime().exec("cls");
		//System.out.print("\033[H\033[2J");
		//System.out.flush();
		
		System.out.println("\nEnter table name (without spaces)");
        tableName=sc.next();
        
        // check if this works
        tableName = tableName.toLowerCase();
        try{
             File fp = new File(tableName + ".txt");
             boolean tab_created = false;
             if(!fp.exists())
             {
	             fp.createNewFile();
	             tab_created = true;
             }
             else
             {
	             System.out.println("\nTable already exists!");
	             return;
             }
             if(tab_created == true)
             {
            	 tab_created = false;
            	 System.out.println("\nEnter no. of attributes: ");
            	 int total = Integer.parseInt(sc.next());
            	 int i=0;
            	 System.out.println("\nEnter attributes with their data types (EACH in separate lines): ");
            	 BufferedWriter writer = new BufferedWriter(new FileWriter(fp));
        	 
            	 while(i<total)
            	 {
            		 String attr = sc.next();
            		 String type = sc.next();
            		 String temp = attr + ":" + type;
            		 writer.write(temp);
            		 i++;
            		 
            		 if(i!=total)
            			 writer.write(",");           		 
            	 }
            	 writer.flush();
            	 //writer.close();
            	 //writer = new BufferedWriter(new FileWriter(fp));
             	
            	 FileInputStream fis = new FileInputStream(fp);
             	 BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            	 
             	 String schema = br.readLine();
            	 String[] attr = schema.split(",");
            	 
            	 
            	 System.out.println("\nEnter Primary Key(PK)");
            	 //String ch = sc.next();
            	 //System.out.println("choice " + ch);
            	 //if(ch.equals("Y") || ch.equals("y"))
            	 //{
            		// System.out.println("Enter attribute name for PK:");
            		 writer.write(System.getProperty("line.separator"));
            		 String pk = sc.next();
            		 writer.write(pk);
            		 int cnt = 0;
            		 for(String tmp : attr)
            		 {
            			 String[] temp = tmp.split(":");
            			 if(temp[0].equals(pk))
            				 writer.write("," + cnt);
            			 
            			 cnt++;
            		 }
            	 //}
                 br.close();
            	 writer.close();
            	 System.out.println("\nTable " + tableName + " has been created");
            	 hp.put(tableName, new ArrayList<>());
            	 
            	 BPTree new_tree = new BPTree<>();
            	 btree.put(tableName, new_tree);
            	 clear_screen();
             }
        }
        catch(FileNotFoundException e) {
       	e.printStackTrace();
           }
        catch(IOException io) {
	   	    io.printStackTrace();
           }
        System.out.println();
	}
}