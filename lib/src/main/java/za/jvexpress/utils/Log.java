package za.jvexpress.utils;


 

public  class Log{

public static enum LogLevel{
        VERBOSE,
        INFO,
        WARNING,
        ERROR
};
     public void route_print( String data){
        System.out.println("[ROUTE] %S".formatted(data));
    }
    public void print( String data){
        System.out.println(data);
    }
    public void eprint(Exception ex){
        System.err.println(ex);
    }
    public void libprint(String data,LogLevel lvl){
        
        System.out.println(data);
    }
}