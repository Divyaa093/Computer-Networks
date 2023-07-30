import java.util.*; //Map,Set, HashMap and HashSet
import java.io.*; // Reading files and Exception handling
import java.net.*; //Sockets
public class ComputeDVR
{
public Router current_Node;
public int round;
public static long frequency = 10000;
/*
ComputeDVR constructor.
current_Node - Node for the Router
*/
public ComputeDVR(Router current_Node)
{
this.current_Node = current_Node;
this.round = 1;
}
//MAIN
public static void main(String[] args) throws Exception
{
/* Checks whether input file is passed as an arguement */
if (args.length != 1)
{
System.out.println("You need to have the router connectivity file as an argument");
return;
}

//Get the Router name from the .dat file
String filename = args[0];
String myRouter = filename.split(".dat")[0];

9
Router current_Node = new Router(myRouter);
ComputeDVR vector = new ComputeDVR(current_Node);
long currTime = System.currentTimeMillis();
try
{
int Port = 9000 + (int) myRouter.charAt(0);
DatagramSocket Client = new DatagramSocket(Port);
InetAddress IP = InetAddress.getByName("localhost");
while(true)
{
if(vector.current_Node.routeChange)
{
try
{
BufferedReader reader = new BufferedReader(new

FileReader(filename));

// the first line is the number of neighbors
String page = reader.readLine();
Map<String, Double> nextNode = new HashMap<>();
//this is the neighbors.
if (getNeigbhors(reader, nextNode))
{
return;
}
reader.close();

Set<String> addedLink = new HashSet<>();
addedLink.addAll(nextNode.keySet());
addedLink.addAll(vector.current_Node.nextNode.keySet());
for(String nodeDetail : addedLink)
{
Double distance = nextNode.get(nodeDetail);
if(distance == null)
{
distance = Double.POSITIVE_INFINITY;
}
vector.current_Node.newNode(nodeDetail, distance,

nodeDetail, vector.round == 1);

}
}

10

catch(FileNotFoundException e)
{
System.err.println("File does not exist");
return;
}
catch(NumberFormatException e)
{
System.err.println("Data is invalid");
return;
}
catch(IOException e)
{
System.err.println("No valid data!");
return;
}
System.out.println("Take " + vector.round++);
System.out.println(vector.current_Node+"\n");
for(String closeLink : vector.current_Node.nextNode.keySet())
{
if(vector.current_Node.nextNode.get(closeLink) ==

Double.POSITIVE_INFINITY)

{
continue;
}
String linkCost = current_Node.updateLinkData(closeLink);
byte[] data = linkCost.getBytes();
int ClientPort = 9000 + (int) closeLink.charAt(0);
DatagramPacket dataPacket = new DatagramPacket(data,

data.length, IP, ClientPort);

Client.send(dataPacket);
}
vector.current_Node.broadcast();
}

currTime = getCurrTime(vector, currTime, Client);
}
}
catch (SocketException e)
{

System.err.println("Socket Exception!");
e.printStackTrace();
}
catch (UnknownHostException e)
{

11
System.err.println("Unknown Host Exception!");

e.printStackTrace();
}
}
private static boolean getNeigbhors(BufferedReader reader, Map<String, Double> nextNode)throws
IOException
{
String page;
while((page = reader.readLine()) != null)
{
StringTokenizer token = new StringTokenizer(page);
if(token.countTokens() != 2)
{
System.err.println("Please provide valid data");
reader.close();
return true;
}
String nodeDetail = token.nextToken().trim();
Double distance = Double.parseDouble(token.nextToken().trim());
nextNode.put(nodeDetail, distance);
}
return false;
}
/**
*Method used to calulate the delta between two sends.
vector - Send the compute vector.
currTime - Send the current time
client - Send the socket.
*/
private static long getCurrTime(ComputeDVR vector, long currTime, DatagramSocket client)
throws IOException
{
try
{
long TimeOut = frequency - (System.currentTimeMillis() - currTime);
if (TimeOut < 0)
{
throw new SocketTimeoutException();
}
byte[] DataPack = new byte[1024];

12

DatagramPacket PacketData = new DatagramPacket(DataPack, DataPack.length);
client.setSoTimeout((int) TimeOut);
client.receive(PacketData);
byte[] nodeData = PacketData.getData();
Router routerData = new Router(nodeData);
for(String node : routerData.linkCost.keySet())
{
costCal linkValue = vector.current_Node.linkCost.get(node);
Double CostOfLink = Double.POSITIVE_INFINITY;
String count = null;
if(linkValue != null)
{
CostOfLink = linkValue.cost;
count = linkValue.cal;
}
Double DVR= routerData.linkCost.get(node).cost +
vector.current_Node.nextNode.get(routerData.node);

if(CostOfLink > DVR)
{
vector.current_Node.updateLinkCost(node, DVR, routerData.node);
}
else if(count != null && count.equals(routerData.node) && !CostOfLink.equals(DVR))
{
vector.current_Node.updateLinkCost(node, DVR, routerData.node);
}
}
}
catch(SocketTimeoutException e)
{
vector.current_Node.routeChange = true;
currTime = System.currentTimeMillis();
}
return currTime;
}
}