package za.jvexpress.utils;

/**
 * Logging utility class with support for different log levels and colored console output.
 * Provides methods for standard output, error output, and library-specific formatted output.
 */
public  class Log{

    /**
     * Enumeration of supported log levels.
     */
    public static enum LogLevel{
        VERBOSE,
        INFO,
        OK,
        WARNING,
        ERROR
    };
    
    /**
     * Prints a route message with red color highlighting.
     * @param data The route information to print
     */
    public void route_print( String data){
        System.out.println("\033[91m [ROUTE] %s \033[00m".formatted(data));
    }
    
    /**
     * Prints a standard message to console.
     * @param data The message to print
     */
    public void print( String data){
        System.out.println(data);
    }
    
    /**
     * Prints an exception stack trace to standard error.
     * @param ex The exception to print
     */
    public void eprint(Exception ex){
        System.err.println(ex);
    }
    
    /**
     * Prints a library message with color and formatting based on log level.
     * ERROR level prints in red, OK level prints in green.
     * 
     * @param data The message to print
     * @param lvl The log level to determine color/formatting
     */
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