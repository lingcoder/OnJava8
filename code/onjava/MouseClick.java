// onjava/MouseClick.java
// (c)2020 MindView LLC: see Copyright.txt
// We make no guarantees that this code is fit for any purpose.
// Visit http://OnJava8.com for more book information.
// Helper interface to allow lambda expressions
package onjava;
import java.awt.event.*;

// Default everything except mouseClicked():
public interface MouseClick extends MouseListener {
    @Override
    default void mouseEntered(MouseEvent e) {}
    @Override
    default void mouseExited(MouseEvent e) {}
    @Override
    default void mousePressed(MouseEvent e) {}
    @Override
    default void mouseReleased(MouseEvent e) {}
}
