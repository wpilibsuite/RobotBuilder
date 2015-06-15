
package robotbuilder.data;

/**
 *
 * @author alex
 */
public interface RobotVisitor<T> {

    public T visit(RobotComponent self, Object... extra);
}
