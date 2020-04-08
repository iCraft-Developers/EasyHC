package icraft.easyhc.sql;

public class SQLQuery {
    private String query;

    public String getQuery() {
        return query;
    }

    public enum Command {
        INSERT;
    }



    public SQLQuery(Command command, String tableName, Object...values) throws Exception {
        switch(command){
            case INSERT:
                query = "insert into " + tableName + " values(";
                for(int i = 0;i < values.length;i++){
                    Object object = values[i];
                    //TODO: Sprawdzic jakie jeszcze obiekty moga byc
                    if(object.getClass() == String.class){
                        query += "'" + object + "'";
                    } else if(object.getClass() == Integer.class) {
                        query += object;
                    } else {
                        throw new Exception("Nieprawidlowy typ danych: " + object.getClass());
                    }
                    if(i < values.length - 1) {
                        query += ", ";
                    }
                }
                query += ");";
                //" VALUES ('" + tag + "', '" + name + "', '" + p.getUniqueId().toString() + "', '" + p.getLocation().getChunk().getX() + "', '" + p.getLocation().getChunk().getZ() + "', '0')";
                break;
        }
    }



}
