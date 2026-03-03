package za.jvexpress.utils;


 

public  class Log{

public static enum LogLevel{
        VERBOSE,
        INFO,
        OK,
        WARNING,
        ERROR
};
     public void route_print( String data){
        System.out.println("\033[91m [ROUTE] %S \033[00m".formatted(data));
    }
    public void print( String data){
        System.out.println(data);
    }
    public void eprint(Exception ex){
        System.err.println(ex);
    }
    public void libprint(String data,LogLevel lvl){
        if (lvl == LogLevel.ERROR){
            System.out.printf("\033[91m[x]\033[00m %s\n",data);
        }
        if (lvl == LogLevel.OK){
            System.out.printf("\033[92m[x]\033[00m %s\n",data);
        }
        System.out.println(data);
    }
}