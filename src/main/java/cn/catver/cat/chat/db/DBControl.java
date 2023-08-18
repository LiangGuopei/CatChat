package cn.catver.cat.chat.db;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;

public class DBControl {
    public static String workdir = "./";
    static boolean ready = false;
    static Logger log = Logger.getLogger("DBControl");

    public static String FromTokenGetName(String token){
        for (Map.Entry<String, String> entry : tokenlist.entrySet()) {
            if(entry.getValue().equals(token)){
                return entry.getKey();
            }
        }
        return null;
    }
    static Map<String,String> tokenlist;

    public static void initDB() throws IOException {
        log.info("try to init db");
        tokenlist = new HashMap<>();
        if(new File(workdir+"hasDB.btu").exists()){
            log.info("init success");
            ready = true;
            return;
        }
        new File(workdir+"hasDB.btu").createNewFile();
        new File(workdir+"user").mkdir();
        new File(workdir+"chat").mkdir();
        new File(workdir+"msgcache").mkdir();

        log.info("init success : create 4 dir");
        ready = true;
    }

    static boolean Ready(){
        return !ready;
    }

    static boolean WriteJsonToFile(String url,JSONObject object) {
        try{
            BufferedWriter bw = new BufferedWriter(new FileWriter(url));

            bw.write(object.toString());

            bw.close();
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    static class readjsonret{
        public readjsonret(boolean a, String t){readed = a; text = t;}
        public readjsonret(boolean a){readed = a; text = null;}
        public boolean readed;
        public String text;
    }
    static readjsonret ReadJsonFromFile(String url){
        if(!new File(url).exists()) return new readjsonret(false);
        try{
            BufferedReader br = new BufferedReader(new FileReader(url));
            String line;
            String t = "";
            while((line = br.readLine()) != null){
                t += line;
            }
            br.close();
            return new readjsonret(true,t);
        }catch (Exception e){
            e.printStackTrace();
            return new readjsonret(false);
        }
    }

    public static ErrorClass CreateUser(String username,String password){
        if(Ready())return ErrorClass.not_ready;

        if(new File(workdir+"user/"+username+".json").exists()){
            return ErrorClass.user_exist;
        }

        JSONObject user = new JSONObject();
        user.put("username",username);
        user.put("password", DigestUtils.md5(password));

        if(!WriteJsonToFile(workdir+"user/"+username+".json",user)) return ErrorClass.file_error;

        return ErrorClass.no_problem;
    }

    static class logindata{
        public logindata(ErrorClass e,String t){ec = e; token = t;}
        public logindata(ErrorClass e){ec = e; token = "";}
        public ErrorClass ec;
        public String token;
    }
    public static logindata LoginUser(String username,String password){
        if(!new File(workdir+"user/"+username+".json").exists()) return  new logindata(ErrorClass.user_not_found);
        String userstr;
        String md5pass = Arrays.toString(DigestUtils.md5(password));
        if((userstr = ReadJsonFromFile(workdir+"user/"+username+".json").text) == null)
            return new logindata(ErrorClass.file_error);
        JSONObject user = JSONObject.parseObject(userstr);
        if(!user.getString("password").equals(md5pass)) return new logindata(ErrorClass.user_password_wrong);
        for (Map.Entry<String, String> entry : tokenlist.entrySet()) {
            if(entry.getKey().equals(username)){
                tokenlist.remove(username);
                break;
            }
        }
        String tokenret = UUID.randomUUID().toString();
        tokenlist.put(username, tokenret);
        return new logindata(ErrorClass.no_problem,tokenret);
    }


    public static boolean CreateChat(String username){
        JSONObject chat = new JSONObject();
        JSONArray people = new JSONArray();
        people.add(username);
        chat.put("type","server");
        chat.put("owner",username);
        chat.put("people",people);
        return WriteJsonToFile(workdir+"chat/"+ UUID.randomUUID()+".json",chat);
    }
    public static boolean CreateChat(ChatType ct,String username,String friendname){
        JSONObject chat = new JSONObject();
        JSONArray people = new JSONArray();
        people.add(username);
        people.add(friendname);
        chat.put("type","prichat");
        chat.put("people",people);
        return WriteJsonToFile(workdir+"chat/"+ UUID.randomUUID()+".json",chat);
    }
}
