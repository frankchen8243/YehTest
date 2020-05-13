import java.util.*;
import java.io.*;
import java.lang.reflect.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.net.*;




public class nc2cdf{
public static void main(String cmd[]){
int i0=0,i1=0,i2=0,i3=0;

String[] filelist = null;
File fo = new File("./");
filelist = fo.list();

for(i0=0;i0<filelist.length;i0++){
if(filelist[i0].substring(filelist[i0].length() - 3).equalsIgnoreCase(".nc")){
System.out.println(filelist[i0] + " dump start!");
try{
Runtime rt = Runtime.getRuntime();
String cmd0="cmd /c ncdump.exe " + filelist[i0]  + " > " + filelist[i0].replaceAll("\\.nc","\\.cdf"); //
System.out.println(cmd0);
Process proc = rt.exec (cmd0);
            String line = null;
InputStream stderr = proc.getErrorStream ();
            InputStreamReader esr = new InputStreamReader (stderr);
            BufferedReader ebr = new BufferedReader (esr);
/*            System.out.println ("<error>");
            while ( (line = ebr.readLine ()) != null)
                System.out.println(line);
            System.out.println ("</error>");
*/      
		StringBuffer str=new StringBuffer();
            InputStream stdout = proc.getInputStream ();
            InputStreamReader osr = new InputStreamReader (stdout);
            BufferedReader obr = new BufferedReader (osr);
//            System.out.println ("<output>");
            while ( (line = obr.readLine ()) != null)
				str.append(line+"\n");
//                System.out.println(line);
//            System.out.println ("</output>");

FileWriter fstream = new FileWriter(filelist[i0].replaceAll("\\.nc","\\.cdf"));
  BufferedWriter out = new BufferedWriter(fstream);
  out.write(str.toString());
  //Close the output stream
  out.close();
			
            int exitVal = proc.waitFor ();
            System.out.println ("Process exitValue: " + exitVal);


java.lang.Thread.sleep(1000);
}
catch(Throwable e){e.printStackTrace();}
}
}//end of for

}//end of main


}