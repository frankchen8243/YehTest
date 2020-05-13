import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.text.*;
import java.util.*;


public class netcdf2txt{
    private String[] FileList = null;
    public final String nonValue = new String("-9999");
    private double LatMin = -90.0;
    private double LatMax = 90.0;
    private double LonMin = -180.0;
    private double LonMax = 360.0;
    private int DateMin = 0;
    private int DateMax = 0;
    private String OutputFile = null;

    public static void main(String[] cmd){
            try{
                double latmin = 5.0;//-90.0;
                double latmax = 40.0;//90.0;
                double lonmin = 100.0;//-360.0;
                double lonmax = 140.0;//360.0;
                int datemin = 0;
                int datemax = 99999999;
                String outFile = null;

                for(int i=0;i<cmd.length;i++){
                    if(cmd[i].toLowerCase().startsWith("-latmin")){
                        latmin = Double.parseDouble(cmd[i].toLowerCase().replace("-latmin", ""));
                    }
                    if(cmd[i].toLowerCase().startsWith("-latmax")){
                        latmax = Double.parseDouble(cmd[i].toLowerCase().replace("-latmax", ""));
                    }
                    if(cmd[i].toLowerCase().startsWith("-lonmin")){
                        lonmin = Double.parseDouble(cmd[i].toLowerCase().replace("-lonmin", ""));
                    }
                    if(cmd[i].toLowerCase().startsWith("-lonmax")){
                        lonmax = Double.parseDouble(cmd[i].toLowerCase().replace("-lonmax", ""));
                    }
                    if(cmd[i].toLowerCase().startsWith("-datemin")){
                        datemin = Integer.parseInt(cmd[i].toLowerCase().replace("-datemin", ""));
                    }
                    if(cmd[i].toLowerCase().startsWith("-datemax")){
                        datemax = Integer.parseInt(cmd[i].toLowerCase().replace("-datemax", ""));
                    }
                    if(cmd[i].toLowerCase().startsWith("-output:")){
                        outFile = cmd[i].toLowerCase().replace("-output:", "");
                        File outF = new File(outFile);
                        if(outF.exists() == false){outF.createNewFile();}
                        outF = null;
                    }
                }
                netcdf2txt nc2t = new netcdf2txt(latmin,latmax,lonmin,lonmax,datemin,datemax,outFile);
                nc2t.run();
            }
            catch(Exception e){e.printStackTrace();}
    }//end of main()

    public netcdf2txt(){
        
    }

    public netcdf2txt(double latmin , double latmax , double lonmin , double lonmax,int datemin , int datemax , String outFile){
        this.LatMin = latmin;
        this.LatMax = latmax;
        this.LonMin = lonmin;
        this.LonMax = lonmax;
        this.DateMin = datemin;
        this.DateMax = datemax;
        this.OutputFile = outFile;
    }

    public void run(){
        try{
            FileList = this.getFileList(".cdf");
            for(int i=0;i < FileList.length;i++){
                this.singleJob(FileList[i]);
            }
        }
        catch(Exception e){e.printStackTrace();}
    }

    public void singleJob(String FileName){
        String DataString = null;
        String[] DataRows = null;
        String[] DataNames = null;
        String[][] DataArray = null;
        double[][] DoubleArray = null;
        try{
            /*
            ucar.nc2.NetcdfFile ncf = ucar.nc2.NetcdfFile.open(FileName);
            java.util.List SectionList = ncf.readArrays(ncf.getVariables());
            for(int i=0;i < SectionList.size();i++){
                java.util.Arrays curArray = (java.util.Arrays)SectionList.get(i);
            }
            */
            String FileData = this.ReadFileAsString(FileName);
            //System.out.println(FileData);
            FileData = FileData.substring(0,FileData.lastIndexOf("}")).substring(FileData.indexOf("{") + 1);
            //分隔線
            //System.out.println("get\t"+ FileData.lastIndexOf("{")+"\t"+FileData.indexOf("}"));
            //System.out.println(FileData+"null");
            //以上測試
            DataString = this.getSectionData(FileData, "data");
            System.out.println("Write DataString to " + FileName + ".txt");
            //new BufferedWriter(new FileWriter("./" + FileName + ".txt")).write(DataString);
            //System.out.println(DataString);
            if(DataString.endsWith(";")){
                DataString = DataString.substring(0,(DataString.length() - 1));
            }
            if(DataString.endsWith("\n")){
                DataString = DataString.substring(0,(DataString.length() - 1));
            }
            DataRows = DataString.split(";");
            DataArray = new String[DataRows.length][];
            DataNames = new String[DataRows.length];
            for(int i=0;i<DataRows.length;i++){
                if(DataRows[i] != null && DataRows[i].indexOf("=") != -1){
                    String[] curRow = DataRows[i].split("=");
                    if(curRow.length == 2){
                        DataNames[i] = curRow[0].trim();
                        DataArray[i] = curRow[1].replace("_", this.nonValue).split(",");
                        System.out.println(i + " " + DataNames[i] + " , " + DataArray[i].length);
                    }else{
                        System.out.println("DataRows Parse Fail!\n" + DataRows[i]);
                    }
                }else{
                    System.out.println("Error Found in DataRow " + i + " , " + DataRows[i]);
                }
            }

            DoubleArray = new double[DataArray.length][];
            for(int i=0;i<DataArray.length;i++){
                DoubleArray[i] = new double[DataArray[i].length];
                for(int j=0;j<DataArray[i].length;j++){
                    try{
                        DoubleArray[i][j] = Double.parseDouble(DataArray[i][j]);
                    }catch(Exception e){
                        DoubleArray[i][j] = -9998.0;
                        e.printStackTrace();
                    }
                }
            }

            for(int i=0;i<DoubleArray[0].length;i++){
                DoubleArray[1][i] = DoubleArray[1][i] * 0.00001;
                DoubleArray[2][i] = DoubleArray[2][i] * 0.00001;
                if(DoubleArray[4][i] > -9997.0){DoubleArray[4][i] = DoubleArray[4][i] * 0.01;}
                if(DoubleArray[5][i] > -9997.0){DoubleArray[5][i] = DoubleArray[5][i] * 0.1;}
                if(DoubleArray[9][i] > -9997.0){DoubleArray[9][i] = DoubleArray[9][i] * 0.01;}
                if(DoubleArray[10][i] > -9997.0){ DoubleArray[10][i] = DoubleArray[10][i] * 0.1; }
            }
            BufferedWriter bw = null;
            if(this.OutputFile == null){
                bw = new BufferedWriter(new FileWriter("./" + FileName.replace(".cdf", ".txt")));
            }else{
                bw = new BufferedWriter(new FileWriter(this.OutputFile,true));
            }
            DecimalFormat df = new DecimalFormat("#######0.######");
            for(int c=0;c<DoubleArray[0].length;c++){
                    Calendar cal = Calendar.getInstance();
                    cal.set(1990, 1, 1, 0, 0, 0);
                    cal.add(Calendar.SECOND,(int)DoubleArray[0][c]);
                    DecimalFormat dtf = new DecimalFormat("####0000");
                    int dateNum = cal.get(Calendar.YEAR) * 10000 + cal.get(Calendar.MONTH) * 100 + cal.get(Calendar.DAY_OF_MONTH);
                    int timeNum = cal.get(Calendar.HOUR_OF_DAY) * 100 + cal.get(Calendar.MINUTE) ;
                if(DoubleArray[1][c] > this.LatMin && DoubleArray[1][c] < this.LatMax && DoubleArray[2][c] > this.LonMin && DoubleArray[2][c] < this.LonMax && dateNum > this.DateMin && dateNum < this.DateMax){
                    bw.write(dtf.format( dateNum) + dtf.format( timeNum) + " ");
                    for(int r=1;r<DoubleArray.length;r++){
                        bw.write(df.format( DoubleArray[r][c]) );
                        if(r<DoubleArray.length - 1){bw.write(" ");}
                    }
                    bw.newLine();
                }
                bw.flush();
            }
            bw.close();
            bw = null;
        }
        catch(Exception e){e.printStackTrace();}
    }

    public String getSectionData(String RawData , String SectionName){
        String result = new String("");
        try{
            result = RawData.substring(RawData.indexOf(SectionName + ":") + new String(SectionName + ":").length());
            if(result.indexOf(":") != -1 ){
                //result = result.substring(0,result.indexOf(":"));
            }
            //result = result.substring(0,result.lastIndexOf(";"));
            return result;
        }
        catch(Exception e){e.printStackTrace();}
        return null;
    }

    public String ReadFileAsString(String FileName){
        try{
            BufferedReader br = new BufferedReader(new FileReader(FileName));
            String curLine = null;
            StringBuffer strbuf = new StringBuffer("");
            while((curLine = br.readLine()) != null){
                strbuf.append(curLine.trim().replace(", " , ","));
            }
            return strbuf.toString().replace(" ;", ";").replace(" =", "=");
        }
        catch(Exception e){e.printStackTrace();}
        return null;
    }

    public String[] getFileList(String FileType){
        FileType = FileType.toLowerCase();
        String result[] = null;
        try{
            String[] tempList = new File("./").list();
            java.util.ArrayList al = new ArrayList();
            if(FileType == null){
                return tempList;
            }else{
                int count = 0;
                for(int i=0;i<tempList.length;i++){
                    if(tempList[i].toLowerCase().endsWith(FileType)){
                        al.add(tempList[i]);
                    }
                }
                result = new String[al.size()];
                for(int i=0;i<al.size();i++){
                    result[i] = (String)al.get(i);
                }
                return result;
            }
        }
        catch(Exception e){e.printStackTrace();}
        return result;
    }
}//end of netcdf2txt