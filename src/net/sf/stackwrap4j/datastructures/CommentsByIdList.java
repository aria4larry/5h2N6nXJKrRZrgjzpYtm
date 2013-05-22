package net.sf.stackwrap4j.datastructures;

import java.io.IOException;

import net.sf.stackwrap4j.StackWrapper;
import net.sf.stackwrap4j.entities.Comment;
import net.sf.stackwrap4j.exceptions.ParameterNotSetException;
import net.sf.stackwrap4j.json.JSONException;
import net.sf.stackwrap4j.query.CommentQuery;

public class CommentsByIdList extends AutoFetchList<Comment> {

    public CommentsByIdList(StackWrapper sw, CommentQuery query) {
        super(sw, query);
    }

    @Override
    public MetadataList<Comment> fetchMoreData() throws IOException,
            JSONException, ParameterNotSetException {
        MetadataList<Comment> ret = (MetadataList<Comment>) sw
                .getComments((CommentQuery) query);
        if (!query.isAutoIncrement())
            query.incrementPage();
        return ret;
    }
}
