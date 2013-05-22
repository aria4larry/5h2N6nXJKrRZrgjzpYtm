package net.sf.stackwrap4j.datastructures;

import java.io.IOException;

import net.sf.stackwrap4j.StackWrapper;
import net.sf.stackwrap4j.entities.Reputation;
import net.sf.stackwrap4j.exceptions.ParameterNotSetException;
import net.sf.stackwrap4j.json.JSONException;
import net.sf.stackwrap4j.query.ReputationQuery;

public class ReputationByUserList extends AutoFetchList<Reputation> {

	public ReputationByUserList(StackWrapper sw, ReputationQuery query) {
		super(sw, query);
	}

	@Override
	public MetadataList<Reputation> fetchMoreData() throws IOException, JSONException,
	        ParameterNotSetException {
		MetadataList<Reputation> ret = (MetadataList<Reputation>) sw
		        .getReputationByUserId((ReputationQuery) query);
		if (!query.isAutoIncrement())
			query.incrementPage();
		return ret;
	}

}
