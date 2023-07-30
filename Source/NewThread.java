import java.util.Random;
import java.util.concurrent.atomic.*;

class NewThread implements Runnable, ChannelConstants
{
String StationNumber;
Thread t;
static int distance, stat=0,frame;
static int ChannelStatus; //Indicates if channel is being used
int FrameNumber,MaxFrameNumber;
private AtomicBoolean CheckIfSuccessfulTransmission;
static int tfr=50; //Transmission time
private int NumberOfAttempts;
NewThread(String threadname, int MaxFrameNumber)
{
StationNumber = threadname;
t = new Thread(this, StationNumber);
FrameNumber = 1;
this.MaxFrameNumber=MaxFrameNumber;
CheckIfSuccessfulTransmission = new AtomicBoolean();
t.start();

}

public void run()
{
Random rand = new Random();
while (!CheckIfSuccessfulTransmission.get())
{



NumberOfAttempts++;
while(FrameNumber <= MaxFrameNumber)
{
if (NumberOfAttempts < 15)
{ //15 is the maximum number of attempts
try
{
if (ChannelStatus == INUSE)
{

System.out.println(StationNumber + " is using Non-Persistent sensing,channel is busy");
try
{
Thread.sleep(rand.nextInt(50)+1000);
}
catch (InterruptedException e)
{
System.out.println(("Interrupt"));
}
}
else
{
System.out.println(StationNumber + " is trying to transmit frame number : "+ FrameNumber);

if (ChannelStatus == FREE && distance == 0)
{//Successful transmission
stat = CheckThreads.checking(Thread.currentThread().getName());
frame = this.FrameNumber;
ChannelStatus = INUSE;//set channel to in use
for (; distance < 9000000; distance++)
for(int i =0;i<1000;i++); //simulate transmission over some distance


System.out.println(StationNumber + " frame " + FrameNumber + " issuccessful");
CheckIfSuccessfulTransmission.set(true);
FrameNumber++;
distance = 0; //reset distance for next frame's transmission
ChannelStatus = FREE;
}
else
{//Collision has occurred
System.out.println("Collision for frame " + FrameNumber + " of " +StationNumber + " and frame " + frame + " of Station " + stat);

System.out.println("Retransmitting Station " + stat + "'s frame " + frame);
CheckIfSuccessfulTransmission.set(false);
ChannelStatus = FREE;

NumberOfAttempts++;

try
{
int R = rand.nextInt((int) (Math.pow(2, NumberOfAttempts - 1)));
int BackOffTime = R * tfr;
Thread.sleep(BackOffTime);

}
catch (InterruptedException e)
{
System.out.println("Interrupted");
}
}
Thread.sleep(1000);



}
}
catch (InterruptedException e)
{
System.out.println(StationNumber + "Main Interrupted");
}
}
else
{
CheckIfSuccessfulTransmission.set(true);
System.out.println("Too many attempts for frame " + FrameNumber+ "of " +StationNumber + ". Transmission stopped");
}

}
}
}
}
