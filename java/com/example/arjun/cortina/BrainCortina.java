package com.example.arjun.cortina;

/**
 * Created by arjun on 2/14/2017.
 */
import android.os.Environment;
import android.widget.Toast;

import java.io.*;
public class BrainCortina extends CourtingCortina {
    public String recog(String b){
        String gb[] = b.split(" ");
        String g = "none";
        for(int i = 0;i<gb.length;i++){
            if(gb[i].equalsIgnoreCase("cortana")){
                g = "cortana";
            }
            else if(gb[i].equalsIgnoreCase("siri")){
                g = "siri";
            }
        }
        return g;
    }
    public String kickout(String name){
        String hush = "";
        for(int i = 0;i<name.length();i++){
            char ch = name.charAt(i);
            if(Character.isLetterOrDigit(ch) || Character.isWhitespace(ch)){
                hush+=Character.toString(ch);
            }
        }
        return hush;
    }
    public String getterfunc(String name){
        String q = "";
        String s = "court/";
        name = name+".txt";
        s+=name;
        try{
            FileReader fr = new FileReader(Environment.getExternalStoragePublicDirectory(s));
            BufferedReader br = new BufferedReader(fr);
            q = br.readLine();
            //Toast.makeText(Jake_sully.this,q,Toast.LENGTH_SHORT).show();

        }catch(IOException e){
            return "##";

        }
        return q;
    }
    public String answer(String name){
        String hush = "###";
        String ppl = getterfunc("format");
        String tenner[] = ppl.split("##");
        String ana[] = name.split(" ");
        for(int i = 0;i<tenner.length;i++){
            String plz[] = tenner[i].split("#");
            String tree = plz[0];
            String ubc[] = tree.split(",");
            int count = 0;
            for(int j = 0;j<ubc.length;j++){
                String yuk = ubc[j];
                for(int k = 0;k<ana.length;k++){
                    if(yuk.equalsIgnoreCase(ana[k])){
                        count++;
                    }
                }
            }
            if(count==ubc.length){
                hush = plz[1];
            }
        }
        return hush;
    }
}
