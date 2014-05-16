//package cs585;


import java.io.FileNotFoundException;
import java.sql.*;

import oracle.spatial.geometry.JGeometry;
import oracle.sql.STRUCT;



public class HW2 {
	private static Connection conn;
	public void connectDB(){
		  conn=null;
		try{
			//System.out.print("Connecting to DB...");
			conn=DriverManager.getConnection( "jdbc:oracle:thin:@localhost:1521:sysdba", "LINDAN", "Tld19901107");
			//System.out.println("connected !!");
			//conn.close();
			//System.out.println("Close successfully");
		}
		catch(SQLException e){
			System.out.println( "Error while connecting to DB: "+ e.toString() );
			e.printStackTrace();
			return ;
		}
		
	}
	
	public static void doQueryWindow(String objName, String lx,String ly,String rx,String ry){
		try {
			Statement stmt= conn.createStatement();
   String query="";
			if(objName.equals("building"))
       query="SELECT b.b_id FROM building b WHERE SDO_RELATE(b.shape,SDO_GEOMETRY(2003,NULL,NULL,SDO_ELEM_INFO_ARRAY(1,1003,3),SDO_ORDINATE_ARRAY("+lx+","+ly+","+rx+","+ry+")),'mask=inside+coveredby')='TRUE'and b.b_name not in(Select * from firebuilding)";
			else if(objName.equals("firebuilding"))
				{//System.out.println("It is here1");
				query="SELECT b.b_id FROM building b WHERE SDO_RELATE(b.shape,SDO_GEOMETRY(2003,NULL,NULL,SDO_ELEM_INFO_ARRAY(1,1003,3),SDO_ORDINATE_ARRAY("+lx+","+ly+","+rx+","+ry+")),'mask=inside+coveredby')='TRUE'and b.b_name in(Select * from firebuilding)";
				
				}			
				else if(objName.equals("firehydrant"))
					query="SELECT h.h_id FROM hydrant h WHERE SDO_RELATE(h.shape,SDO_GEOMETRY(2003,NULL,NULL,SDO_ELEM_INFO_ARRAY(1,1003,3),SDO_ORDINATE_ARRAY("+lx+","+ly+","+rx+","+ry+")),'mask=inside+coveredby')='TRUE'";
				
			else {System.out.println("Invalid input");return ;}	
				
	        ResultSet rs= stmt.executeQuery(query);
		
			while(rs.next())
				{System.out.println(rs.getString(1));
				//System.out.println("here is !");
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				System.out.println("There is an error");
				e.printStackTrace();
			}
		
	}
	public static void doQueryWithin(String objName,String start,String len) throws SQLException{
		String query="";
		if(objName.equals("building")){
			query="SELECT b.b_id FROM building b,building b1 WHERE SDO_WITHIN_DISTANCE(b.shape,b1.shape,'distance="+len+"')= 'TRUE' and b1.b_name='"+start+"'and b.b_name not in(select * from firebuilding)";
		}
		else if(objName.equals("firebuilding")){
			query="SELECT b.b_id FROM building b,building b1 WHERE SDO_WITHIN_DISTANCE(b.shape,b1.shape,'distance="+len+"')= 'TRUE' and b1.b_name='"+start+"'and b.b_name in(select * from firebuilding)";
		}
		else if(objName.equals("firehydrant")){
			query="SELECT h.h_id FROM hydrant h,building b1 WHERE SDO_WITHIN_DISTANCE(h.shape,b1.shape,'distance="+len+"')= 'TRUE' and b1.b_name='"+start+"'";
		}
		else{
			System.out.println("Invalid input about the object");
			return;
		}
		executeSQL(query);
		
	}
	public static void doQueryNN(String objName,String start,String num) throws SQLException{
		String query="";
		boolean startOnfire=false;
		/*if(objName.equals("building")||objName.equals("firebuilding")){
			String sql="SELECT b.b_id FROM building b WHERE  b.b_name in (select * from firebuilding) and b.b_id='"+start+"'";
			Statement stmt= conn.createStatement();
			ResultSet rs= stmt.executeQuery(sql);
			if(!rs.next()) {startOnfire=false; }
			else{startOnfire=true;}
		}*/
		if(objName.equals("building")){
		
 query="SELECT b.b_id FROM building b, building b1  WHERE b1.b_id='"+start+"' and SDO_NN(b.shape, b1.shape, 'sdo_batch_size=50') = 'TRUE' AND b.b_name not in (select * from firebuilding) AND ROWNUM <="+num+"and  b.b_id<>'"+start+"'";
				
		}
		else if(objName.equals("firebuilding")){
query="SELECT b.b_id FROM building b, building b1  WHERE b1.b_id='"+start+"' and SDO_NN(b.shape, b1.shape, 'sdo_batch_size=50') = 'TRUE' AND b.b_name in (select * from firebuilding) AND ROWNUM <="+num+"and  b.b_id<>'"+start+"'";
		}
		else if(objName.equals("firehydrant")){
query="SELECT h.h_id FROM hydrant h, building b1  WHERE b1.b_id='"+start+"' and SDO_NN(h.shape, b1.shape, 'sdo_batch_size=100') = 'TRUE'  AND ROWNUM <="+num;		
		}
		else{
			System.out.println("Invalid input about the object");
			return;
		}
		
		executeSQL(query);
	}
	public static void doQueryDemo(String demoNum) throws SQLException{
		String query="";
		if(demoNum.equals("1"))
		{query="select b.b_name from building b where substr(b.b_name,1,1)='S' and b.b_name not in (select * from firebuilding)";
		executeSQL(query);
		}
		else if(demoNum.equals("2")){
			String sql="select b.b_name,b.b_id from building b where b.b_name in (select * from firebuilding)";
			Statement stmt= conn.createStatement();
			ResultSet rs= stmt.executeQuery(sql);
			while(rs.next())
			{System.out.println(rs.getString(1));
			query="SELECT h.h_id FROM hydrant h, building b1  WHERE b1.b_id='"+rs.getString(2)+"' and SDO_NN(h.shape, b1.shape, 'sdo_batch_size=100') = 'TRUE'  AND ROWNUM <=5";		
			executeSQL(query);
			//System.out.println("here is !");
			}
			
		}
		else if(demoNum.equals("3")){
			
String query1="select max(cnt) from(select h1.h_id,count(b1.b_id) as cnt from hydrant h1,building b1 where SDO_WITHIN_DISTANCE(b1.shape,h1.shape,'distance=120')= 'TRUE' group by h1.h_id)";			
			
query="select h.h_id from hydrant h,building b where SDO_WITHIN_DISTANCE(b.shape,h.shape,'distance=120')='TRUE' group by h.h_id having count(b.b_id)=("+query1+")";
executeSQL(query);		
		}
		else if(demoNum.equals("4")){
			String query1="create or replace view cnum as select b.b_id,min(SDO_GEOM.SDO_DISTANCE(h.shape,b.shape,0.1)) as minLen from building b,hydrant h group by b.b_id";
			String query2="select * from(select h.h_id,count(h.h_id) as num from building b,hydrant h,cnum where SDO_GEOM.SDO_DISTANCE(h.shape,b.shape,0.1)=cnum.minLen and b.b_id=cnum.b_id group by h.h_id order by num desc) where rownum<=5";
			
			
			
			Statement statement = conn.createStatement();
				
			statement.execute(query1);
			//statement.close();
			
			Statement stmt= conn.createStatement();
			ResultSet rs= stmt.executeQuery(query2);
			while(rs.next())
			{System.out.println(rs.getString(1)+" "+rs.getString(2));
			//System.out.println("here is !");
			}
			String query3="drop view cnum";
			Statement stmt2= conn.createStatement();
			ResultSet rs2= stmt2.executeQuery(query3);
			
			stmt.close();
			statement.close();
		    stmt2.close();
		}
		
		else if(demoNum.equals("5")){
			query="SELECT SDO_AGGR_MBR(b.shape)  FROM building b	where b.b_name like '%HE'"; 
			Statement stmt= conn.createStatement();
			
			ResultSet rs= stmt.executeQuery(query);
			rs.next(); 
			//while(rs.next())
			STRUCT st = (oracle.sql.STRUCT) rs.getObject(1);
			
			JGeometry j_geom = JGeometry.load(st);
			double[] array=j_geom.getMBR();
			System.out.print("lower left x: "+array[0]+" lower left y: "+array[1]);
			System.out.println();
			System.out.print("higher upper x: "+array[2]+" higher upper y: "+array[3]);
			
		}
		else System.out.println("invalid output");
	}
   public static void executeSQL(String query) throws SQLException{
	Statement stmt= conn.createStatement();
	ResultSet rs= stmt.executeQuery(query);
	
	
	while(rs.next())
		{System.out.println(rs.getString(1));
		//System.out.println("here is !");
		}
	
	stmt.close();
	
}

public static void main(String[] args) throws FileNotFoundException, SQLException
{
	HW2 t = new HW2();
	t.connectDB();
	if(args[0].equals("window"))
	t.doQueryWindow(args[1],args[2],args[3],args[4],args[5]);
	
	else if(args[0].equals("within"))
		t.doQueryWithin(args[1],args[2],args[3]);
	else if(args[0].equals("nn"))
		t.doQueryNN(args[1],args[2],args[3]);
	else if(args[0].equals("demo"))
		t.doQueryDemo(args[1]);
	else
		System.out.println("Invalid input query-type");
	conn.close();
	
      
	
	//t.importData(args[0],args[1],args[2],args[3]);
	//t.populateDB();
	//t.findNearestPoint();
}
}