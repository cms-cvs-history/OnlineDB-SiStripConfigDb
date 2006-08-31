package detidGenerator;

import db.*;
import java.io.*;
import java.util.Vector;

/**
 * <p>Get the DetIDs for both TEC and TOB and export them to the online DB</p>
 * @author G. Baulieu
 * @version 1.0
**/

/*
  $Date: 2006/08/30 15:21:12 $
  
  $Log: DetIDGenerator.java,v $
  Revision 1.3  2006/08/30 15:21:12  gbaulieu
  Add the TOB analyzer

  Revision 1.2  2006/06/29 09:19:22  gbaulieu
  The database to which we export the data is no more hard coded. The informations are taken from the $CONFDB variable.

  Revision 1.1  2006/06/28 11:42:24  gbaulieu
  First import of the sources

  Revision 1.15  2006/06/07 12:40:42  baulieu
  Add a - verbose option
  Add a serialVersionUID to the ClassNotSupportedException class to avoid a warning

  Revision 1.14  2006/05/29 13:05:35  baulieu
  Add the TOB infos for the magnet test.
  The way it's done is perfectly ugly : everything is hard coded in the common class detIDGenerator

  Revision 1.13  2006/05/18 08:12:03  baulieu
  Set 1 as fiber length when the length is unknown

  Revision 1.12  2006/05/12 13:49:38  baulieu
  Connection to Oracle 10

  Revision 1.11  2006/05/10 07:47:51  baulieu
  Correct a bug in the det_id when there are several sectors with petal on a disk.
  Change the way the length of the fibers are found.
  Allow to print the result on the screen instead of exporting to a db.
  3 possibles parameters : -mtcc, -export, -help.
  Some documentation.

  Revision 1.10  2006/03/27 08:12:13  baulieu
  Add an option for the MTCC detectors

  Revision 1.9  2006/03/21 17:04:13  baulieu
  New version of the TEC det_id (no fw/bw modules)

  Revision 1.8  2006/02/22 08:40:21  baulieu
  Invert the DCU_IDs

  Revision 1.7  2006/02/10 09:22:13  baulieu
  Retrieve the fibers length

  Revision 1.6  2006/02/08 15:03:00  baulieu
  Add the convertion to 32 bits for the TOB

  Revision 1.5  2006/02/08 14:39:59  baulieu
  Converts the Det_id into 32 bits numbers

  Revision 1.4  2006/02/02 17:17:00  baulieu
  Some modifications for JDK 1.5
  Call a PL/SQL function to export the parameters


*/

/**
   Class used to retrieve informations about the detectors.
*/
public class DetIDGenerator
{
    private String query;
    private CDBConnection c;
    public static boolean mtcc=true;
    public static boolean export=false;
    public static boolean verbose=false;

    private String[][] TOBMTCC = {
	{"5373171","436306180","1","4"},
	{"6026951","436306184","1","4"},
	{"11795783","436306188","1","4"},
	{"16382707","436306192","1","4"},
	{"14284538","436306196","1","4"},
	{"8253946","436306200","1","4"},
	{"6157063","436371972","1","6"},
	{"16514803","436371976","1","6"},
	{"8060666","436371980","1","6"},
	{"12320499","436371984","1","6"},
	{"5896689","436371988","1","6"},
	{"3535858","436371992","1","6"},
	{"14155519","436371716","1","6"},
	{"5371595","436371720","1","6"},
	{"15988183","436371724","1","6"},
	{"12320215","436371728","1","6"},
	{"3536895","436371732","1","6"},
	{"14349055","436371736","1","6"},
	{"4062215","436306436","1","4"},
	{"6025971","436306440","1","4"},
	{"14022387","436306444","1","4"},
	{"5894415","436306448","1","4"},
	{"3274227","436306452","1","4"},
	{"13762291","436306456","1","4"}
    };

    /**
       Default Constructor
    **/
    public DetIDGenerator(){
	query = new String();
	try{

	    c = CDBConnection.getConnection();

	    if(DetIDGenerator.export){
		/*
		  Just to check that we can connect to the export database
		  Better to see it now rather than after all the computing...
		*/
		configureExportDatabaseConnection();
		c.connect();
		c.disconnect();
		
		//Ok it's working, let's go!
	    }

	    c.setUser("prod_consult");
	    c.setUrl("jdbc:oracle:thin:@(DESCRIPTION = (ADDRESS = (PROTOCOL = TCP)(HOST = ccdbcl01.in2p3.fr)(PORT = 1521))(ADDRESS = (PROTOCOL = TCP)(HOST = ccdbcl02.in2p3.fr)(PORT = 1521))(LOAD_BALANCE = yes)(CONNECT_DATA = (SERVER = DEDICATED)(SERVICE_NAME = cccmstrktaf.in2p3.fr)(FAILOVER_MODE =(TYPE = SELECT)(METHOD = BASIC)(RETRIES = 180)(DELAY = 5))))");
	    c.setPassword("am8bilo8gy");
	    
	    c.connect();
	}
	catch(java.sql.SQLException e){
	    Error("SQL Error : \n"+query+"\n"+e.getMessage());
	}
	catch(java.lang.ClassNotFoundException e){
	    Error("Can not find Oracle driver");
	}
    }
	
    private void Error(String message){
	System.out.println(message);
    }

    /**
       Perform the treatment :
       <ul>
       <li> Get the Det IDs for TEC and TOB
       <li> Get the DCU IDs associated to the module IDs
       <li> Get the fiber's length
       <li> Get the number of APVs
       <li> Export the data to a DB or print them on the screen
       </ul>
    **/
    public void go(){
	try{
	    Vector<Vector<String>> list = new Vector<Vector<String>>();
	    IDetIdGenerator tec = new TECAnalyzer();
	    IDetIdGenerator tob = new TOBAnalyzer();
	    
	    list.addAll(tec.getDetIds());
	    list.addAll(tob.getDetIds());

	    if(DetIDGenerator.verbose)
		System.out.println(list.size()+" modules found");

	    if(DetIDGenerator.verbose)
		System.out.println("Retrieving the fibers length...");
	    getFiberLength(list);

	    if(DetIDGenerator.verbose)
		System.out.println("Converting DetIds to 32 bits...");
	    compactDetIds(list);
	    
	    if(DetIDGenerator.verbose)
		System.out.println("Retrieving the number of APVs...");
	    getApvNumber(list);

	    if(DetIDGenerator.verbose)
		System.out.println("Searching the DCU ids...");
	    getDCU(list);

	    if(DetIDGenerator.verbose)
		System.out.println("Reversing the DCU ids...");
	    reverseDcuIds(list);

	    if(DetIDGenerator.verbose)
		System.out.println("Exporting...");
	    exportData(list);

	    c.disconnect();
	    
	}
	catch(java.sql.SQLException e){
	    Error("SQL Error : \n"+query+"\n"+e.getMessage());
	}
	catch(ClassNotSupportedException e){
	    Error("ClassNotSupportedException :\n"+e.getMessage());
	}
	catch(java.lang.ClassNotFoundException e){
	    Error("Can not find Oracle driver");
	}
	catch(Exception e){
	    Error("Error : \n"+e.getMessage());
	}
    }

    /**
       Format the 1.6.x.x.x... string into a 32 bits word
    */
    private void compactDetIds(Vector<Vector<String>> list) throws Exception{
	for(int i=0;i<list.size();i++){
	    Vector<String> v = list.get(i);
	    
	    DetIdConverter d = null;
	    if((v.get(1)).startsWith("1.6.")){
		d = new TECDetIdConverter(v.get(1));
	    }
	    else{
		if((v.get(1)).startsWith("1.5."))
		    d = new TOBDetIdConverter(v.get(1));
		else
		    throw new Exception("The Det_ID should start with 1.6 (TEC) or 1.5 (TOB)");
	    }
	    
	    v.setElementAt(d.compact()+"", 1);
	}
    }

    private void getFiberLength(Vector<Vector<String>> list) throws java.sql.SQLException{
/*
	for(int i=0;i<list.size();i++){
	    Vector<String> v = list.get(i);
	    v.add("1");
	}
*/
	for(int i=0;i<list.size();i++){
	    try{
		Vector<String> v = list.get(i);

		String cDetID = new String(v.get(1));

		String[] det_id = cDetID.split("\\.");
		int tec = Integer.parseInt(det_id[2]);
		int disk = Integer.parseInt(det_id[3]);
		int front = Integer.parseInt(det_id[4]);
		int sector = Integer.parseInt(det_id[5]);
		//System.out.println(tec+" "+disk+" "+front+" "+sector);

		query = "select OA2.object_id, OA2.number_in_container from cmstrkdb.object_assembly OA, cmstrkdb.object_assembly OA2 WHERE OA2.object='AOH' AND OA2.number_in_container=OA.number_in_container AND OA2.container_id=OA.container_id AND OA.object_id="+v.get(0)+" ";
		Vector<Vector<String>> res = c.selectQuery(query);
		if(res.size()>0){
		    String aoh_id = (res.get(0)).get(0);
		    int aoh_position = Integer.parseInt((res.get(0)).get(1));
		    
		    query = "select G.fiber_length from cmstrkdb.aohgeneral_1_aoh_ G, cmstrkdb.aohcomp_1_aoh_ C where C.aohgeneral_1_aoh_=G.test_id AND C.object_id="+aoh_id;
		    Vector<Vector<String>> lengthRes = c.selectQuery(query);
		    if(lengthRes.size()>0){
			int aoh_length = Integer.parseInt((lengthRes.get(0)).get(0));
			
			query = "select distinct D.fanout_length from cmstrkdb.manufacture_1_optfanout_ M, cmstrkdb.diamond_1_optfanout_ D, cmstrkdb.object_assembly FANOUT, cmstrkdb.object_assembly AOH, cmstrkdb.object_assembly LASTRANS, cmstrkdb.link L WHERE M.diamond_1_optfanout_=D.test_id AND M.object_id=FANOUT.object_id AND L.object_id_b=FANOUT.object_id AND L.object_id_a=LASTRANS.object_id AND LASTRANS.container_id=AOH.object_id AND AOH.object_id="+aoh_id;
			Vector<Vector<String>> lengthRes2 = c.selectQuery(query);
			if(lengthRes2.size()==1){
			    int total_length = aoh_length+Integer.parseInt((lengthRes2.get(0)).get(0));
			    v.add(total_length+".0");
			}
			else{
			    v.add("1");
			    if(lengthRes2.size()>1)
				throw new Exception("Can not find unique length for the aoh "+ aoh_id);
			    else
				throw new Exception("Data are missing for this AOH : "+ aoh_id);
			}
		    }
		    else{
			throw new Exception("Can not find the length of the aoh "+ aoh_id);
		    }
		}
		else{
		    throw new Exception("Can not find the AOH corresponding to module "+v.get(0));
		}
		if(DetIDGenerator.verbose)
		System.out.print(((i*100)/list.size())+" %\r");

	    }
	    catch(java.sql.SQLException e){
		throw e;
	    }
	    catch(Exception e){
		Error(e.getMessage());
	    }
	}
    }

    private void getApvNumber(Vector<Vector<String>> list) throws java.sql.SQLException{
	for(int i=0;i<list.size();i++){
	    Vector<String> v = list.get(i);
	    query = "select OD.type_description from cmstrkdb.object_assembly OA, cmstrkdb.object_description OD WHERE"+
		" OA.object=OD.object AND OA.type=OD.type AND OA.version=OD.version AND OA.object='HYB' AND "+
		"OA.container_id="+v.get(0);
	    Vector<Vector<String>> res = c.selectQuery(query);
	    if(res.size()>0){
		String type = (res.get(0)).get(0);
		type = type.substring(type.indexOf('.')+1, type.length()-1);
		v.add(type);
	    }
	    else{
		Error("Type of hybrid contained in module "+v.get(0)+" unknown!!");
	    }
	    if(DetIDGenerator.verbose)
		System.out.print(((i*100)/list.size())+" %\r");
	}
    }

    private void getDCU(Vector<Vector<String>> list) throws java.sql.SQLException{
	for(int i=0;i<list.size();i++){
	    Vector<String> v = list.get(i);

	    query = "select MB.dcuid from cmstrkdb.modvalidation_2_mod_ MV, cmstrkdb.modulbasic_2_mod_ MB "+
		"where MB.test_id=MV.modulbasic_2_mod_ AND MV.status='reference' AND MV.object_id="+v.get(0);

	    Vector<Vector<String>> res = c.selectQuery(query);
	    if(res.size()==1 && (res.get(0)).get(0)!=null && !(res.get(0)).get(0).equals("0")){
		Vector<String> detail = res.get(0);
		v.setElementAt(detail.get(0), 0);
	    }
	    else{
		query = "select distinct FP.dcu_id from cmstrkdb.hybproducer_1_hyb_ HP, cmstrkdb.object_assembly OA, "+
		    "cmstrkdb.fhitproduction_1_hyb_ FP where HP.fhitproduction_1_hyb_=FP.test_id AND "+
		    "OA.object_id=HP.object_id AND OA.object='HYB' AND OA.container_id="+v.get(0);
		res = c.selectQuery(query);
		if(res.size()==1 && (res.get(0)).get(0)!=null && !(res.get(0)).get(0).equals("0")){
		    Vector<String> detail = res.get(0);
		    v.setElementAt(detail.get(0), 0);
		}
		else{
		    query = "select distinct FR.dcu_id from cmstrkdb.hybmeasurements_2_hyb_ HM, "+
			"cmstrkdb.object_assembly OA, "+
			"cmstrkdb.fhitreception_1_hyb_ FR where HM.fhitreception_1_hyb_=FR.test_id AND "+
			"OA.object_id=HM.object_id AND OA.object='HYB' AND OA.container_id="+v.get(0);
		    res = c.selectQuery(query);
		    if(res.size()==1 && (res.get(0)).get(0)!=null && !(res.get(0)).get(0).equals("0")){
			Vector<String> detail = res.get(0);
			v.setElementAt(detail.get(0), 0);
		    }
		    else{
			query = "select distinct FP.dcu_id from cmstrkdb.object_assembly OA, "+
			    "cmstrkdb.fhitproduction_1_hyb_ FP where "+
			    "OA.object_id=FP.object_id AND OA.object='HYB' AND OA.container_id="+v.get(0);
			res = c.selectQuery(query);
			if(res.size()==1 && (res.get(0)).get(0)!=null && !(res.get(0)).get(0).equals("0")){
			    Vector<String> detail = res.get(0);
			    v.setElementAt(detail.get(0), 0);
			}
			else{
			    Error("DCU_ID of module "+v.get(0)+" unknown!!");
			}
		    }
		}
	    }
	    if(DetIDGenerator.verbose)
		System.out.print(((i*100)/list.size())+" %\r");
	}
    }

    private int reverseDcuId(int dbDcuId){
	int firstMask = 0xFF0000;
	int secondMask = 0x00FF00;
	int thirdMask = 0x0000FF;

	int result = 0;

	int dbFirst = (dbDcuId&firstMask)>>16;
	int dbSecond = (dbDcuId&secondMask)>>8;
	int dbThird = (dbDcuId&thirdMask);

	result = result|(dbThird<<16)|(dbSecond<<8)|dbFirst;

	//System.out.println(dbDcuId+"->"+dbFirst+" "+dbSecond+" "+dbThird+"->"+result);
	return result;
    }

    private void reverseDcuIds(Vector<Vector<String>> list) throws Exception{
	for(int i=0;i<list.size();i++){
	    Vector<String> v = list.get(i);
	    int reversedDcuId = reverseDcuId(Integer.parseInt(v.get(0)));
	    v.setElementAt(reversedDcuId+"", 0);
	}	
    }

    private void configureExportDatabaseConnection() throws java.sql.SQLException{
	String dbString = System.getProperty("CONFDB");
	if(dbString==null || dbString.equals("") || dbString.indexOf('/')==-1 || dbString.indexOf('@')==-1)
	    throw new java.sql.SQLException("No valid $CONFDB variable found : can not connect!");
	
	String user = dbString.substring(0,dbString.indexOf('/'));
	String password = dbString.substring(dbString.indexOf('/')+1, dbString.indexOf('@'));
	String url = dbString.substring(dbString.indexOf('@')+1, dbString.length());
	url = "jdbc:oracle:thin:@"+url;
	c.setUser(user);
	c.setUrl(url);
	c.setPassword(password);
    }

    private void exportData(Vector<Vector<String>> list) throws java.sql.SQLException, ClassNotSupportedException{
	c.disconnect();

	if(DetIDGenerator.mtcc){
	    for(String[] s : TOBMTCC){
		Vector<String> n = new Vector<String>();
		n.add(s[0]);
		n.add(s[1]);
		n.add(s[2]);
		n.add(s[3]);
		list.add(n);
	    }
	}

	if(DetIDGenerator.export){
	    configureExportDatabaseConnection();
	    c.connect();
	}
	
	for(int i=0;i<list.size();i++){
	    Vector<String> v = list.get(i);
	    int dcuID = Integer.parseInt(v.get(0));
	    int detID = Integer.parseInt(v.get(1));
	    float length = new java.lang.Float(v.get(2));
	    int apvNumber = Integer.parseInt(v.get(3));
	    if(DetIDGenerator.export){
		int res=c.callFunction("PkgDcuInfo.setValues", dcuID, detID, length, apvNumber);
	    }
	    else{
		try{
		    DetIdConverter det = new DetIdConverter(detID);
		    if(det.getSubDetector()==6){
			TECDetIdConverter d = new TECDetIdConverter(detID);
			d.compact();
			System.out.println(dcuID+","+detID+","+d);
		    }
		    if(det.getSubDetector()==5){
			TOBDetIdConverter d = new TOBDetIdConverter(detID);
			d.compact();
			System.out.println(dcuID+","+detID+","+d);
		    }
		}
		catch(Exception e){
		    
		}
	    }
	    if(DetIDGenerator.verbose)
		System.out.print(((i*100)/list.size())+" %\r");
	}

    }

    /**
       Retrieve the det_id from a dcuHardId
       @param dcuId The dcuHard Id
       @return The det_id corresponding to the dcu_id
    */
    public int getDetId(int dcuId) throws java.sql.SQLException{
	Vector<Vector<String>> res = c.selectQuery("select detid from dcuInfo where dcuhardid="+dcuId);
	if(res.size()==0)
	    throw new java.sql.SQLException("No detId found for DcuId "+dcuId);
	else
	    return Integer.parseInt((res.get(0)).get(0));
    }

    /**
       Load the dcu ids from a file into a vector
       @param name The name of the file
       @return The output vector
    **/

    private static Vector<Integer> loadFile(String name) throws java.io.FileNotFoundException, java.io.IOException
    {
	File inputFile;
        FileReader in;
	int i;
	String s="";
	int c;
	boolean stop;
	Vector<String> lines = new Vector<String>();
	Vector<Integer> v = new Vector<Integer>();
        	
	in = new FileReader(name);
	stop = false;
	c=in.read();
	while(c!=-1){
	    if(c!='\n'){
		s = s+(char)c;
	    }
	    else{
		if(!s.equals(""))
		    lines.add(s);
		s="";
	    }
	    c=in.read();
	}
	in.close();

	for(String line : lines){
	    String[] result = line.split(" ");
	    v.add(Integer.parseInt(result[5]));
	}

	return v;
    }

    /**
       Main method used for test.<br>
       Load a file (argument) with a list of dcu_id and check if we have the corresponding det_id.
    */
    public static void main(String args[])
    {
	try{
	    DetIDGenerator d = new DetIDGenerator();
	    d.exportData(new Vector<Vector<String>>());// only to connect to the configuration DB...
	    Vector<Integer> dcuIds = loadFile(args[0]);
	    System.out.println(dcuIds.size()+" modules : ");
	    for(Integer dcuId : dcuIds){
		try{
		    System.out.println(dcuId+" -> "+d.getDetId(dcuId));
		}
		catch(java.sql.SQLException e){
		    System.out.print(e.getMessage());
		}
	    }
	}
	catch(java.io.FileNotFoundException e2){
	    System.out.println(e2.getMessage());
	}
	catch(java.io.IOException e){
	    System.out.println("Error while reading the file: \n"+e.getMessage());
	}
	catch(java.sql.SQLException e){
	    System.out.print(e.getMessage());
	}
    }
}
