package edu.stanford.bmir.protegex.chao.util.interval;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.bmir.protegex.chao.change.api.Change;
import edu.stanford.bmir.protegex.chao.change.api.impl.DefaultChange;
import edu.stanford.bmir.protegex.chao.ontologycomp.api.impl.DefaultTimestamp;
import edu.stanford.smi.protege.model.DefaultSimpleInstance;
import edu.stanford.smi.protege.model.FrameID;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.framestore.MergingNarrowFrameStore;
import edu.stanford.smi.protege.model.framestore.NarrowFrameStore;
import edu.stanford.smi.protege.storage.database.AbstractDatabaseFrameDb;
import edu.stanford.smi.protege.storage.database.DatabaseKnowledgeBaseFactory;
import edu.stanford.smi.protege.storage.database.RobustConnection;
import edu.stanford.smi.protege.util.Log;

/**
 * Retrieves the top level changes by accessing direclty the ChAO database, and not through the ChAO API.
 *
 * @author ttania
 *
 */
public class GetDirectDbTopLevelChanges {

    private static transient Logger log = Log.getLogger(GetDirectDbTopLevelChanges.class);

    private final static String CHANGE_ID = "ChangeId";
    private final static String TIMESTAMP_ID = "TimestampId";
    private final static String TIMESTAMP_DATE = "TimestampDate";
    private final static String TIMESTAMP_SEQ = "TimestampSeq";

    private KnowledgeBase chaoKb;

    public GetDirectDbTopLevelChanges(KnowledgeBase chaoKb) {
        this.chaoKb = chaoKb;
    }

    /**
     * Returns a map with the top level changes sorted by the timestamp.
     * @return - the map (or empty map), if successful; null if error.
     *
     */
    public TreeMap<SimpleTime, Change> fillChanges()  {
        log.info("...Using direct database mode to fill in the top level changes cache" ); //TODO: make it fine level

        RobustConnection rconnection;
        try {
            rconnection = getRobustConnection();
        } catch (SQLException e) {
            log.log(Level.WARNING, "Could not connect to the ChAO database table to create the top level changes cache.", e);
            return null;
        }

        synchronized (chaoKb) {
            try {
                return buildTopLevelChangesMap(rconnection);
            } catch (Exception e) {
                log.log(Level.WARNING, "Error at creating the top level changes cache (db mode).", e);
                return null;

            } finally {
                rconnection.setIdle(true);
            }
        }
    }

    private TreeMap<SimpleTime, Change> buildTopLevelChangesMap(RobustConnection rconnection) throws SQLException {
        TreeMap<SimpleTime, Change> changeMap = new TreeMap<SimpleTime, Change>();

        String table = DatabaseKnowledgeBaseFactory.getTableName(chaoKb.getProject().getSources());
        if (table == null) {
            return null;
        }

        String topLevelChangesQuery =
            "SELECT allchanges.frame AS " + CHANGE_ID +", timestamp.short_value AS " + TIMESTAMP_ID + ", " +
                "timestampdate.short_value AS " + TIMESTAMP_DATE + ", timestampseq.short_value AS " + TIMESTAMP_SEQ +" FROM " + table +" AS allchanges " +
                     "JOIN " + table +" AS timestamp ON timestamp.slot='timestamp' AND timestamp.frame = allchanges.frame " +
                     "JOIN " + table +" AS timestampdate ON timestampdate.slot='date' AND timestampdate.frame = timestamp.short_value " +
                     "JOIN " + table +" AS timestampseq on timestampseq.slot='sequence' AND timestampseq.frame = timestamp.short_value " +
                          "WHERE allchanges.slot='context' " +
                              "AND NOT EXISTS (SELECT * FROM " + table +" AS subchanges " +
                                  "WHERE subchanges.slot='partOfCompositeChange' AND allchanges.frame = subchanges.frame LIMIT 1)";

        PreparedStatement stmt = rconnection.getPreparedStatement(topLevelChangesQuery);
        ResultSet rs = stmt.executeQuery();
        try {
            while (rs.next()) {
               String changeid = getValue(rs, CHANGE_ID);
               String timestampid = getValue(rs, TIMESTAMP_ID);
               String timestampDate = getValue(rs, TIMESTAMP_DATE);
               String timestampSeq = getValue(rs, TIMESTAMP_SEQ);

               Date date = DefaultTimestamp.getDateParsed(timestampDate);
               int seq = timestampSeq == null ? 0 : Integer.parseInt(timestampSeq);

               if (changeid == null || timestampid == null || date == null) {
                   log.warning("Skipping top level cache entry: " + changeid + ", " + timestampid + ", " + timestampDate + ", " + timestampSeq + " Result set: " + rs );
               } else {
                   SimpleTime time = new SimpleTime(date, seq);
                   Instance changeInst = new DefaultSimpleInstance(chaoKb, new FrameID(changeid));
                   Change change = new DefaultChange(changeInst);
                   changeMap.put(time, change);
               }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (rs != null) {
                rs.close();
            }
        }

        return changeMap;
    }

    private String getValue(ResultSet rs, String col) {
        String val = null;
        try {
            val = rs.getString(col);
        } catch (SQLException e) { //maybe just one value fails..
            log.log(Level.WARNING, "Error at retrieving top changes cache value: " + rs, e);
        }
        if (val == null || val.equals("")) {
            return null;
        }
        return val;
    }

    private RobustConnection getRobustConnection() throws SQLException {
        chaoKb.getFrameStoreManager().getTerminalFrameStore();
        MergingNarrowFrameStore mnfs = MergingNarrowFrameStore.get(chaoKb);
        NarrowFrameStore nfs = mnfs.getActiveFrameStore();
        AbstractDatabaseFrameDb dbFrameStore = null;
        do {
            if (nfs instanceof AbstractDatabaseFrameDb) {
                dbFrameStore = (AbstractDatabaseFrameDb) nfs;
            }
            nfs = nfs.getDelegate();
        }
        while (nfs != null);
        return dbFrameStore != null ? dbFrameStore.getCurrentConnection() : null;
    }

}
