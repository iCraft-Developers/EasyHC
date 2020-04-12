package icraft.easyhc;

import icraft.easyhc.sql.SQLQuery;
import org.bukkit.Bukkit;

public class BufferAction {
    private ActionType action;
    private String query;

    public String getQuery() {
        return query;
    }

    public enum ActionType {
        ADD_FACTION,
        ADD_MEMBER,
        REMOVE_FACTION,
        REMOVE_MEMBER,
        CHANGE_OWNER,
        EXTEND_FACTION;

    }


    public BufferAction(ActionType action, Object...args) throws Exception {
        this.action = action;
        SQLQuery sqlquery;
        switch(action){
            case ADD_FACTION:
                sqlquery = new SQLQuery(SQLQuery.Command.INSERT, "factions", args);
                query = sqlquery.getQuery();
                Bukkit.getLogger().info(query);
                break;
            case ADD_MEMBER:
                break;
            case REMOVE_FACTION:
                sqlquery = new SQLQuery(SQLQuery.Command.DELETE, "factions", args);
                query = sqlquery.getQuery();
                Bukkit.getLogger().info(query);
                break;
            case REMOVE_MEMBER:
                sqlquery = new SQLQuery(SQLQuery.Command.DELETE, "members", args);
                query = sqlquery.getQuery();
                Bukkit.getLogger().info(query);
                break;
            case CHANGE_OWNER:
                break;
            case EXTEND_FACTION:
                sqlquery = new SQLQuery(SQLQuery.Command.UPDATE, "factions", args);
                query = sqlquery.getQuery();
                Bukkit.getLogger().info(query);
                break;
        }
    }



}
