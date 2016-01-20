package bio.gcat.operation.analysis;

import static bio.gcat.Help.ANALYSES;
import static bio.gcat.Help.OPERATIONS;

import java.util.Collection;

import bio.gcat.Documented;
import bio.gcat.Parameter;
import bio.gcat.nucleic.Tuple;
import bio.gcat.operation.Cataloged;
import bio.gcat.operation.Named;

@Named(name="add comment", icon="comment_add") @Cataloged(group="Analyse Sequence")
@Parameter.Annotation(key = "comment", label = "Comment", type = Parameter.Type.TEXT)
@Documented(title="Comment", category={OPERATIONS,ANALYSES}, resource="help/operation/analysis/comment.html")
public class Comment implements Analysis {
	@Override public Result analyse(Collection<Tuple> tuples, Object... values) {
		return new SimpleResult(this,values[0].toString()); }
}
