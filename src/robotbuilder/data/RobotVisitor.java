/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotbuilder.data;

/**
 *
 * @author alex
 */
public interface RobotVisitor<T> {
    public T visit(RobotComponent self, Object...extra);
}
