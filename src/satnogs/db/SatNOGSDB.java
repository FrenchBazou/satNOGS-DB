/**
 * this fetches specific data from the satNOGS DB site
 * and produces csv files for each type of data
 *
 * based on the Java_wget project
 *
 * v2 : removed directory structure in the destination
 * V3 : coming up soon
 */
package satnogs.db;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.json.*;
import static satnogs.db.SatNOGSDB.myBufferedWriter;

/**
 *
 * @author frenchbazou
 */
public class SatNOGSDB {

    public static String inputLine, outputLine;
    //
    // this holds the types of data we can fetch from satNOGS db
    //
    public static String[] types = {"modes", "satellites", "transmitters"};
    public static String[] modes = {"name", "id"};
    public static String[] satellites = {"decayed", "image", "names", "name", "norad_cat_id", "status", "telemetries"};
    public static String[] transmitters = {"baud", "downlink_drift", "alive", "uplink_drift", "invert", "citation", "description", "type", "norad_cat_id", "uuid", "mode", "mode_id", "service", "downlink_high", "uplink_high", "downlink_low", "updated", "uplink_low", "uplink_mode", "status"};
    public static JSONArray jsonArr = new JSONArray();

    public static FileWriter myFileWriter;
    public static BufferedWriter myBufferedWriter;
    //static SimpleDateFormat DB_date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    static SimpleDateFormat DB_date_format = new SimpleDateFormat("yyyy-MM-dd");

    public static void main(String[] args) throws Exception {

        // modes types[0] : OK
        // transmitters types[2] : OK !
        // satellites types[1] : OK
        //Get_Listing(types[1]);
        //System.exit(0);

        for (int i = 0; i < types.length; i++) {
            System.out.println("Workign on " + types[i]);
            Get_Listing(types[i]);
        }

    }

    public static void Get_Listing(String myType) throws MalformedURLException, IOException, ParseException {

        // build the filename including the date
        //
        Date now = new Date();

        //String data_file_path = "/Users/Shared/radio amateur/projets/satNOGS/DB from satNOGS/satNOGS " + myType + "-" + DB_date_format.format(now) + ".csv";
        String data_file_path = myType + "-" + DB_date_format.format(now) + ".csv";
        myFileWriter = new FileWriter(data_file_path, false);
        myBufferedWriter = new BufferedWriter(myFileWriter);
        System.out.println("writing to filename = " + data_file_path);

        URL myURL = new URL("https://db.satnogs.org/api/" + myType + "/?format=json");
        System.out.println("myURL = " + myURL);
        BufferedReader myBufferedReader = new BufferedReader(
                new InputStreamReader(myURL.openStream()));

        while ((inputLine = myBufferedReader.readLine()) != null) {
            outputLine = inputLine;
        }

        myBufferedReader.close();

        // build a JSONArray (myJSONArray)
        //
        jsonArr = new JSONArray(outputLine);

        // now that we have the 
        // now print all JSONObjects from this JSONArray
        //
        // First, print the specific heading for this data type
        //
        switch (myType) {
            case "modes":
                // print the JSONObject structure (heading for the csv files)
                System.out.println("---------------------------------------------------------------------------------------------");
                System.out.println("for modes - Heading will be : name,id");
                System.out.println("Fetched " + jsonArr.length() + " records.");
                System.out.println("---------------------------------------------------------------------------------------------");
                Generate_CSV(modes);
                break;
            case "satellites":
                //
                // print the JSONObject structure (heading for the csv files)
                System.out.println("---------------------------------------------------------------------------------------------");
                System.out.println("for satellites - Heading will be : decayed,image,names,name,norad_cat_id,status,telemetries");
                System.out.println("Fetched " + jsonArr.length() + " records.");
                System.out.println("---------------------------------------------------------------------------------------------");
                Generate_CSV(satellites);
                break;
            case "transmitters":
                //
                // print the JSONObject structure (heading for the csv files)
                System.out.println("---------------------------------------------------------------------------------------------");
                System.out.println("for transmitters - Heading will be : baud,downlink_drift,alive,uplink_drift,invert,citation,description,type,norad_cat_id,uuid,mode,mode_id,service,downlink_high,uplink_high,downlink_low,updated,uplink_low,uplink_mode,status ");
                System.out.println("Fetched " + jsonArr.length() + " records.");
                System.out.println("---------------------------------------------------------------------------------------------");
                Generate_CSV(transmitters);
                break;
        }
    }

    /** this method builds the csv files
     * 
     * @param myType
     * @throws IOException 
     */
    public static void Generate_CSV(String[] myType) throws IOException {
        //
        // print the first row, the heading row
        //
        //System.out.print(myType[0]);
        myBufferedWriter.write(myType[0]);
        for (int i = 1; i < myType.length; i++) {
            //System.out.print("," + myType[i]);
            myBufferedWriter.write("," + myType[i]);
        }
        myBufferedWriter.write("\n");
        //System.out.println();
        //
        // now print the data in a csv format
        //
        for (int i = 0; i < jsonArr.length(); i++) {
            //System.out.print(jsonArr.getJSONObject(i).get(myType[0]));
            myBufferedWriter.write(String.valueOf(jsonArr.getJSONObject(i).get(myType[0])));
            for (int j = 1; j < myType.length; j++) {
                //
                // replace any "," with "-"
                //
                String tmp = String.valueOf(jsonArr.getJSONObject(i).get(myType[j]));
                String tmp2 = tmp.replace(",","-");
                //
                // replace any "\r\n," with " - "
                //
                String tmp3 = tmp2.replace("\r\n"," - ");
                //System.out.print("," + tmp3);
                myBufferedWriter.write("," + tmp3);
            }
            // terminate the line
            //System.out.println();
            myBufferedWriter.write("\n");
            myBufferedWriter.flush();
        }
        myBufferedWriter.flush();
    }

}

// modes : name, id
//  transmitters : baud downlink_drift alive uplink_drift invert citation description type norad_cat_id uuid mode mode_id service downlink_high uplink_high downlink_low updated uplink_low uplink_mode status 
// name
// satellites : decayed,image,names,name,norad_cat_id,status,telemetries
