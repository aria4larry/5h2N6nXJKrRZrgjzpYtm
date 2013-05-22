package net.sf.stackwrap4j.datastructures;

import java.io.IOException;

import net.sf.stackwrap4j.StackWrapper;
import net.sf.stackwrap4j.entities.Answer;
import net.sf.stackwrap4j.exceptions.ParameterNotSetException;
import net.sf.stackwrap4j.json.JSONException;
import net.sf.stackwrap4j.query.AnswerQuery;

public class AnswerFromQuestionList extends AutoFetchList<Answer> {

    public AnswerFromQuestionList(StackWrapper sw, AnswerQuery query) {
        super(sw, query);
    }

    @Override
    public MetadataList<Answer> fetchMoreData() throws IOException, JSONException,
            ParameterNotSetException {
        MetadataList<Answer> ret = (MetadataList<Answer>) sw
                .getAnswersByQuestionId((AnswerQuery) query);
        if (!query.isAutoIncrement())
            query.incrementPage();
        return ret;
    }
}
