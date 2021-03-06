/*******************************************************************************
 * Copyright (c) 2017 Pegasystems Inc. All rights reserved.
 *
 * Contributors:
 *     Manu Varghese
 *******************************************************************************/
package com.pega.gcs.fringecommon.guiutilities;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.event.MouseInputListener;

public class FilterTableHeaderResizablePopupMenu extends JPopupMenu implements MouseInputListener {

	private static final long serialVersionUID = -2250952010740118551L;

	private static final int BORDER_DRAG_THICKNESS = 5;

	private static final int CORNER_DRAG_WIDTH = 16;

	private static final int[] cursorMapping = new int[] { Cursor.NW_RESIZE_CURSOR, Cursor.NW_RESIZE_CURSOR,
			Cursor.N_RESIZE_CURSOR, Cursor.NE_RESIZE_CURSOR, Cursor.NE_RESIZE_CURSOR, Cursor.NW_RESIZE_CURSOR, 0, 0, 0,
			Cursor.NE_RESIZE_CURSOR, Cursor.W_RESIZE_CURSOR, 0, 0, 0, Cursor.E_RESIZE_CURSOR, Cursor.SW_RESIZE_CURSOR,
			0, 0, 0, Cursor.SE_RESIZE_CURSOR, Cursor.SW_RESIZE_CURSOR, Cursor.SW_RESIZE_CURSOR, Cursor.S_RESIZE_CURSOR,
			Cursor.SE_RESIZE_CURSOR, Cursor.SE_RESIZE_CURSOR };

	private Cursor lastCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
	/**
	 * Used to determine the corner the resize is occuring from.
	 */
	private int dragCursor;

	/**
	 * X location the mouse went down on for a drag operation.
	 */
	private int dragOffsetX;

	/**
	 * Y location the mouse went down on for a drag operation.
	 */
	private int dragOffsetY;

	/**
	 * Width of the window when the drag started.
	 */
	private int dragWidth;

	/**
	 * Height of the window when the drag started.
	 */
	private int dragHeight;

	public FilterTableHeaderResizablePopupMenu() {

		JLabel testLabel = new JLabel("my name is manu varghese");
		JLabel secondLabel = new JLabel("this is secondLabel");

		add(testLabel);
		addSeparator();
		add(secondLabel);

		addMouseListener(this);
		addMouseMotionListener(this);

		setLightWeightPopupEnabled(false);

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// do nothing
	}

	@Override
	public void mousePressed(MouseEvent e) {

		Point dragWindowOffset = e.getPoint();

		JComponent comp = (JComponent) e.getSource();
		// if (w != null) {
		// w.toFront();
		// }

		if (comp != null) {
			dragOffsetX = dragWindowOffset.x;
			dragOffsetY = dragWindowOffset.y;
			dragWidth = comp.getWidth();
			dragHeight = comp.getHeight();
			dragCursor = getCursor(calculateCorner(comp, dragWindowOffset.x, dragWindowOffset.y));
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (dragCursor != 0) {
			// Some Window systems validate as you resize, others won't,
			// thus the check for validity before repainting.
			// window.validate();
			getRootPane().repaint();
		}

		dragCursor = 0;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		JComponent comp = (JComponent) e.getSource();
		lastCursor = comp.getCursor();
		mouseMoved(e);

	}

	@Override
	public void mouseExited(MouseEvent e) {
		JComponent comp = (JComponent) e.getSource();
		comp.setCursor(lastCursor);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		JComponent comp = (JComponent) e.getSource();
		Point pt = e.getPoint();

		if (dragCursor != 0) {
			Rectangle r = comp.getBounds();
			Rectangle startBounds = new Rectangle(r);
			Dimension min = comp.getMinimumSize();

			switch (dragCursor) {
			case Cursor.E_RESIZE_CURSOR:
				adjust(r, min, 0, 0, pt.x + (dragWidth - dragOffsetX) - r.width, 0);
				break;
			case Cursor.S_RESIZE_CURSOR:
				adjust(r, min, 0, 0, 0, pt.y + (dragHeight - dragOffsetY) - r.height);
				break;
			case Cursor.N_RESIZE_CURSOR:
				adjust(r, min, 0, pt.y - dragOffsetY, 0, -(pt.y - dragOffsetY));
				break;
			case Cursor.W_RESIZE_CURSOR:
				adjust(r, min, pt.x - dragOffsetX, 0, -(pt.x - dragOffsetX), 0);
				break;
			case Cursor.NE_RESIZE_CURSOR:
				adjust(r, min, 0, pt.y - dragOffsetY, pt.x + (dragWidth - dragOffsetX) - r.width,
						-(pt.y - dragOffsetY));
				break;
			case Cursor.SE_RESIZE_CURSOR:
				adjust(r, min, 0, 0, pt.x + (dragWidth - dragOffsetX) - r.width,
						pt.y + (dragHeight - dragOffsetY) - r.height);
				break;
			case Cursor.NW_RESIZE_CURSOR:
				adjust(r, min, pt.x - dragOffsetX, pt.y - dragOffsetY, -(pt.x - dragOffsetX), -(pt.y - dragOffsetY));
				break;
			case Cursor.SW_RESIZE_CURSOR:
				adjust(r, min, pt.x - dragOffsetX, 0, -(pt.x - dragOffsetX),
						pt.y + (dragHeight - dragOffsetY) - r.height);
				break;
			default:
				break;
			}
			if (!r.equals(startBounds)) {
				comp.setBounds(r);

				// Defer repaint/validate on mouseReleased unless dynamic
				// layout is active.
				if (Toolkit.getDefaultToolkit().isDynamicLayoutActive()) {
					comp.validate();
					getRootPane().repaint();
				}

				// ((FilterTableHeaderPopupMenu)comp).pack();

				// java.awt.Window w = SwingUtilities.getWindowAncestor(comp);
				// w.pack();
				// w.setBounds(r);
				// w.validate();
			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {

		JComponent comp = (JComponent) e.getSource();

		// Update the cursor
		int cursor = getCursor(calculateCorner(comp, e.getX(), e.getY()));

		if (cursor != 0) {
			comp.setCursor(Cursor.getPredefinedCursor(cursor));
		} else {
			comp.setCursor(lastCursor);
		}
	}

	private int getCursor(int corner) {
		if (corner == -1) {
			return 0;
		}
		return cursorMapping[corner];
	}

	private int calculateCorner(JComponent comp, int x, int y) {
		Insets insets = comp.getInsets();
		int xPosition = calculatePosition(x - insets.left, comp.getWidth() - insets.left - insets.right);
		int yPosition = calculatePosition(y - insets.top, comp.getHeight() - insets.top - insets.bottom);

		if (xPosition == -1 || yPosition == -1) {
			return -1;
		}
		return yPosition * 5 + xPosition;
	}

	private int calculatePosition(int spot, int width) {
		if (spot < BORDER_DRAG_THICKNESS) {
			return 0;
		}
		if (spot < CORNER_DRAG_WIDTH) {
			return 1;
		}
		if (spot >= (width - BORDER_DRAG_THICKNESS)) {
			return 4;
		}
		if (spot >= (width - CORNER_DRAG_WIDTH)) {
			return 3;
		}
		return 2;
	}

	private void adjust(Rectangle bounds, Dimension min, int deltaX, int deltaY, int deltaWidth, int deltaHeight) {
		bounds.x += deltaX;
		bounds.y += deltaY;
		bounds.width += deltaWidth;
		bounds.height += deltaHeight;
		if (min != null) {
			if (bounds.width < min.width) {
				int correction = min.width - bounds.width;
				if (deltaX != 0) {
					bounds.x -= correction;
				}
				bounds.width = min.width;
			}
			if (bounds.height < min.height) {
				int correction = min.height - bounds.height;
				if (deltaY != 0) {
					bounds.y -= correction;
				}
				bounds.height = min.height;
			}
		}
	}
}
