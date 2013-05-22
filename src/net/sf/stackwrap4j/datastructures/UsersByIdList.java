package net.sf.stackwrap4j.datastructures;

import java.io.IOException;

import net.sf.stackwrap4j.StackWrapper;
import net.sf.stackwrap4j.entities.User;
import net.sf.stackwrap4j.exceptions.ParameterNotSetException;
import net.sf.stackwrap4j.json.JSONException;
import net.sf.stackwrap4j.query.UserQuery;

public class UsersByIdList extends AutoFetchList<User> {

    public UsersByIdList(StackWrapper sw, UserQuery query) {
        super(sw, query);
    }

    @Override
    public MetadataList<User> fetchMoreData() throws IOException, JSONException,
            ParameterNotSetException {
        MetadataList<User> ret = (MetadataList<User>) sw.getUsersById((UserQuery) query);
        if (!query.isAutoIncrement())
            query.incrementPage();
        return ret;
    }
}
