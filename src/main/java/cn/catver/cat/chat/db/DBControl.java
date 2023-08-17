package cn.catver.cat.chat.db;


import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DBControl {
    public static String workdir = "./";
    static boolean ready = false;
    static Logger log = Logger.getLogger("DBControl");

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
        public readjsonret(boolean a){readed = a; text = "";}
        public boolean readed;
        public String text;
    }
    static readjsonret ReadJsonFromFile(String url){
        if(!new File(url).exists()) return new readjsonret(false);

    }

    public static ErrorClass CreateUser(String username,String password,String email){
        if(Ready())return ErrorClass.not_ready;
        if(!username.matches("\\w+")) return ErrorClass.invalid_data;
        if(!password.matches("\\w+")) return ErrorClass.invalid_data;
        if(!email.matches("\\w+@\\w+\\.\\w+")) return ErrorClass.invalid_data;

        if(new File(workdir+"user/"+username+".json").exists()){
            return ErrorClass.user_exist;
        }

        JSONObject user = new JSONObject();
        user.put("username",username);
        user.put("password", DigestUtils.md5(password));
        user.put("email",email);

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
        if(!username.matches("\\w+")) return new logindata(ErrorClass.invalid_data);
        if(!password.matches("\\w+")) return new logindata(ErrorClass.invalid_data);
        if(!new File(workdir+"user/"+username+".json").exists()) return  new logindata(ErrorClass.user_not_found);

    }
}
