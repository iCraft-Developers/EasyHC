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
        CHANGE_OWNER

    }


    public BufferAction(ActionType action, Object...args) throws Exception {
        this.action = action;
        switch(action){
            case ADD_FACTION:
                SQLQuery sqlquery = new SQLQuery(SQLQuery.Command.INSERT, "factions", args);
                query = sqlquery.getQuery();
                Bukkit.getLogger().info(query);
                break;
            case ADD_MEMBER:
                break;
            case REMOVE_FACTION:
                break;
            case REMOVE_MEMBER:
                break;
            case CHANGE_OWNER:
                break;
        }
    }



}
