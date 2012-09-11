/******************************************************************************
 * Copyright (c) 2012 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ling Hao - initial implementation and ongoing maintenance
 ******************************************************************************/
package org.eclipse.sapphire.ui.swt.gef.layout;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.GridData;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LayoutManager;
import org.eclipse.draw2d.OrderedLayout;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;

/**
 * Arranges figures in a single row or column. Orientation can be set to produce
 * either a row or column layout. This layout tries to fit all children within
 * the parent's client area. To do this, it compresses the children by some
 * amount, but will not compress them smaller than their minimum size. If a
 * child's preferred size is smaller than the row's or column's minor dimension,
 * the layout can be configured to stretch the child.
 * 
 * @author <a href="mailto:ling.hao@oracle.com">Ling Hao</a>
 *  
 */
public class SapphireSequenceLayout extends OrderedLayout {

	/** The layout contraints */
	private Map constraints = new HashMap();

	/**
	 * Sets whether children should "stretch" with their container
	 * 
	 * @deprecated Use {@link OrderedLayout#setStretchMinorAxis(boolean)} and
	 *             {@link OrderedLayout#isStretchMinorAxis()} instead.
	 * */
	protected boolean matchWidth;

	/**
	 * Space in pixels between Figures
	 * 
	 */
	private int spacing;
	
	private int margin = 0;

	/**
	 * Constructs a vertically oriented ToolbarLayout with child spacing of 0
	 * pixels, {@link #setStretchMinorAxis(boolean)} <code>true</code>, and
	 * {@link #ALIGN_TOPLEFT} minor alignment.
	 * 
	 * @since 2.0
	 */
	public SapphireSequenceLayout() {
		setStretchMinorAxis(true);
		setSpacing(0);
	}

	/**
	 * Constructs a ToolbarLayout with a specified orientation. Default values
	 * are: child spacing 0 pixels, {@link #setStretchMinorAxis(boolean)}
	 * <code>false</code>, and {@link #ALIGN_TOPLEFT} alignment.
	 * 
	 * @param isHorizontal
	 *            whether the children are oriented horizontally
	 * @since 2.0
	 */
	public SapphireSequenceLayout(boolean isHorizontal) {
		setHorizontal(isHorizontal);
		setStretchMinorAxis(false);
		setSpacing(0);
	}

	private Dimension calculateChildrenSize(List children, int wHint,
			int hHint, boolean preferred) {
		Dimension childSize;
		IFigure child;
		int height = 0, width = 0;
		for (int i = 0; i < children.size(); i++) {
			child = (IFigure) children.get(i);
			childSize = transposer.t(preferred ? getChildPreferredSize(child,
					wHint, hHint) : getChildMinimumSize(child, wHint, hHint));
			height += childSize.height;
			width = Math.max(width, childSize.width);

			SapphireSequenceLayoutConstraint constraint = (SapphireSequenceLayoutConstraint)getConstraint(child);
			if (constraint != null) {
				Insets inset = constraint.getMarginInset();
				width += inset.left + inset.right;
				height += inset.top + inset.bottom;
			}
		}
		return new Dimension(width, height);
	}

	/**
	 * Calculates the minimum size of the container based on the given hints. If
	 * this is a vertically-oriented Toolbar Layout, then only the widthHint is
	 * respected (which means that the children can be as tall as they desire).
	 * In this case, the minimum width is that of the widest child, and the
	 * minimum height is the sum of the minimum heights of all children, plus
	 * the spacing between them. The border and insets of the container figure
	 * are also accounted for.
	 * 
	 * @param container
	 *            the figure whose minimum size has to be calculated
	 * @param wHint
	 *            the width hint (the desired width of the container)
	 * @param hHint
	 *            the height hint (the desired height of the container)
	 * @return the minimum size of the container
	 * @see #getMinimumSize(IFigure, int, int)
	 * @since 2.1
	 */
	protected Dimension calculateMinimumSize(IFigure container, int wHint,
			int hHint) {
		Insets insets = container.getInsets();
		if (isHorizontal()) {
			wHint = -1;
			if (hHint >= 0)
				hHint = Math.max(0, hHint - insets.getHeight());
		} else {
			hHint = -1;
			if (wHint >= 0)
				wHint = Math.max(0, wHint - insets.getWidth());
		}

		List children = container.getChildren();
		Dimension minSize = calculateChildrenSize(children, wHint, hHint, false);
		// Do a second pass, if necessary
		if (wHint >= 0 && minSize.width > wHint) {
			minSize = calculateChildrenSize(children, minSize.width, hHint,
					false);
		} else if (hHint >= 0 && minSize.width > hHint) {
			minSize = calculateChildrenSize(children, wHint, minSize.width,
					false);
		}

		minSize.height += Math.max(0, children.size() - 1) * spacing;
		return transposer.t(minSize)
				.expand(insets.getWidth(), insets.getHeight())
				.union(getBorderPreferredSize(container));
	}

	/**
	 * Calculates the preferred size of the container based on the given hints.
	 * If this is a vertically-oriented Toolbar Layout, then only the widthHint
	 * is respected (which means that the children can be as tall as they
	 * desire). In this case, the preferred width is that of the widest child,
	 * and the preferred height is the sum of the preferred heights of all
	 * children, plus the spacing between them. The border and insets of the
	 * container figure are also accounted for.
	 * 
	 * @param container
	 *            the figure whose preferred size has to be calculated
	 * @param wHint
	 *            the width hint (the desired width of the container)
	 * @param hHint
	 *            the height hint (the desired height of the container)
	 * @return the preferred size of the container
	 * @see #getPreferredSize(IFigure, int, int)
	 * @since 2.0
	 */
	protected Dimension calculatePreferredSize(IFigure container, int wHint,
			int hHint) {
		Insets insets = container.getInsets();
		if (isHorizontal()) {
			wHint = -1;
			if (hHint >= 0)
				hHint = Math.max(0, hHint - insets.getHeight());
		} else {
			hHint = -1;
			if (wHint >= 0)
				wHint = Math.max(0, wHint - insets.getWidth());
		}

		List children = container.getChildren();
		Dimension prefSize = calculateChildrenSize(children, wHint, hHint, true);
		// Do a second pass, if necessary
		if (wHint >= 0 && prefSize.width > wHint) {
			prefSize = calculateChildrenSize(children, prefSize.width, hHint,
					true);
		} else if (hHint >= 0 && prefSize.width > hHint) {
			prefSize = calculateChildrenSize(children, wHint, prefSize.width,
					true);
		}

		prefSize.height += Math.max(0, children.size() - 1) * spacing;
		return transposer.t(prefSize)
				.expand(insets.getWidth(), insets.getHeight())
				.union(getBorderPreferredSize(container));
	}

	/**
	 * @param child
	 *            the figure whose minimum size is to be determined
	 * @param wHint
	 *            the width hint
	 * @param hHint
	 *            the height hint
	 * @return the given figure's minimum size
	 * @since 3.3
	 */
	protected Dimension getChildMinimumSize(IFigure child, int wHint, int hHint) {
		return child.getMinimumSize(wHint, hHint);
	}

	/**
	 * @param child
	 *            the figure whose preferred size is to be determined
	 * @param wHint
	 *            the width hint
	 * @param hHint
	 *            the height hint
	 * @return given figure's preferred size
	 * @since 3.3
	 */
	protected Dimension getChildPreferredSize(IFigure child, int wHint,	int hHint) {
		return child.getPreferredSize(wHint, hHint);
	}

	/**
	 * Returns {@link PositionConstants#VERTICAL} by default.
	 * 
	 * @see org.eclipse.draw2d.OrderedLayout#getDefaultOrientation()
	 */
	protected int getDefaultOrientation() {
		return PositionConstants.VERTICAL;
	}

	/**
	 * @return the spacing between children
	 */
	public int getSpacing() {
		return spacing;
	}

	/**
	 * @see org.eclipse.draw2d.AbstractHintLayout#isSensitiveHorizontally(IFigure)
	 */
	protected boolean isSensitiveHorizontally(IFigure parent) {
		return !isHorizontal();
	}

	/**
	 * @see org.eclipse.draw2d.AbstractHintLayout#isSensitiveVertically(IFigure)
	 */
	protected boolean isSensitiveVertically(IFigure parent) {
		return isHorizontal();
	}

	/**
	 * Returns <code>true</code> if stretch minor axis has been enabled. The
	 * default value is false.
	 * 
	 * @return <code>true</code> if stretch minor axis is enabled
	 * @deprecated Use {@link #isStretchMinorAxis()} instead.
	 */
	public boolean getStretchMinorAxis() {
		return isStretchMinorAxis();
	}

	/**
	 * Overwritten to guarantee backwards compatibility with {@link #matchWidth}
	 * field.
	 * 
	 * @see org.eclipse.draw2d.OrderedLayout#isStretchMinorAxis()
	 */
	public boolean isStretchMinorAxis() {
		return matchWidth;
	}

	/**
	 * @see org.eclipse.draw2d.LayoutManager#layout(IFigure)
	 */
	public void layout(IFigure parent) {
		List children = parent.getChildren();
		int numChildren = children.size();
		Rectangle clientArea = transposer.t(parent.getClientArea());
		System.out.println("clientArea: " + clientArea);
		int x = clientArea.x;
		int y = clientArea.y;
		int availableHeight = clientArea.height;

		Dimension prefSizes[] = new Dimension[numChildren];
		Dimension minSizes[] = new Dimension[numChildren];
		SapphireSequenceLayoutConstraint constraints[] = new SapphireSequenceLayoutConstraint[numChildren];
		Insets marginInsets[] = new Insets[numChildren];

		// Calculate the width and height hints. If it's a vertical
		// ToolBarLayout,
		// then ignore the height hint (set it to -1); otherwise, ignore the
		// width hint. These hints will be passed to the children of the parent
		// figure when getting their preferred size.
		int wHint = -1;
		int hHint = -1;
		if (isHorizontal()) {
			hHint = parent.getClientArea(Rectangle.SINGLETON).height;
		} else {
			wHint = parent.getClientArea(Rectangle.SINGLETON).width;
		}

		/*
		 * Calculate sum of preferred heights of all children(totalHeight).
		 * Calculate sum of minimum heights of all children(minHeight). Cache
		 * Preferred Sizes and Minimum Sizes of all children.
		 * 
		 * totalHeight is the sum of the preferred heights of all children
		 * totalMinHeight is the sum of the minimum heights of all children
		 * prefMinSumHeight is the sum of the difference between all children's
		 * preferred heights and minimum heights. (This is used as a ratio to
		 * calculate how much each child will shrink).
		 */
		IFigure child;
		int totalHeight = 0;
		//int totalMinHeight = 0;
		//int prefMinSumHeight = 0;
		int totalMargin = 0;
		int expandCount = 0;

		for (int i = 0; i < numChildren; i++) {
			child = (IFigure) children.get(i);
			
			SapphireSequenceLayoutConstraint constraint = (SapphireSequenceLayoutConstraint)getConstraint(child);
			if (constraint == null)
				setConstraint(child, constraint = new SapphireSequenceLayoutConstraint());
			constraints[i] = constraint;

			prefSizes[i] = transposer.t(getChildPreferredSize(child, wHint, hHint));
			minSizes[i] = transposer.t(getChildMinimumSize(child, wHint, hHint));
			marginInsets[i] = transposer.t(constraint.getMarginInset());
			
			if (isHorizontal() && constraint.expandCellHorizontally || !isHorizontal() && constraint.expandCellVertically) {
				totalHeight += minSizes[i].height;
				expandCount++;
			} else {
				totalHeight += prefSizes[i].height;
			}
			totalMargin += marginInsets[i].top + marginInsets[i].bottom;

			//totalMinHeight += minSizes[i].height;
		}
		totalHeight += (numChildren - 1) * spacing;
		totalHeight += margin + margin;
		//totalMinHeight += (numChildren - 1) * spacing;
		//prefMinSumHeight = totalHeight - totalMinHeight;
		/*
		 * The total amount that the children must be shrunk is the sum of the
		 * preferred Heights of the children minus Max(the available area and
		 * the sum of the minimum heights of the children).
		 * 
		 * amntShrinkHeight is the combined amount that the children must shrink
		 * amntShrinkCurrentHeight is the amount each child will shrink
		 * respectively
		 */
//		int amntShrinkHeight = totalHeight
//				- Math.max(availableHeight, totalMinHeight);
//
//		if (amntShrinkHeight < 0) {
//			amntShrinkHeight = 0;
//		}
		int extraHeight = availableHeight - totalHeight - totalMargin;
		if (extraHeight < 0) {
			extraHeight = 0;
		} else {
			if (expandCount > 0) {
				extraHeight = extraHeight / expandCount;
			} else {
				extraHeight = 0;
			}
		}
		
		y += margin;

		for (int i = 0; i < numChildren; i++) {
//			int amntShrinkCurrentHeight = 0;
			int prefHeight = prefSizes[i].height;
			int minHeight = minSizes[i].height;
			int prefWidth = prefSizes[i].width;
			int minWidth = minSizes[i].width;
			Insets marginInset = marginInsets[i];
			
			Rectangle newBounds;
			SapphireSequenceLayoutConstraint constraint = constraints[i];
			if (isHorizontal() && constraint.expandCellHorizontally || !isHorizontal() && constraint.expandCellVertically) {
				int height = minHeight + extraHeight;
				int offset = 0;
				if (height > prefHeight) {
					// alignment
					switch (getMajorAlignment(constraint)) {
					case SWT.CENTER:
						offset = (height - prefHeight) / 2;
						break;
					case SWT.RIGHT:
					case SWT.BOTTOM:
						offset = height - prefHeight;
						break;
					}
					height = prefHeight;
				}
				newBounds = new Rectangle(x, y + marginInset.top + offset, prefWidth, height);
			} else {
				newBounds = new Rectangle(x, y + marginInset.top, prefWidth, prefHeight);
			}

			child = (IFigure) children.get(i);
//			if (prefMinSumHeight != 0)
//				amntShrinkCurrentHeight = (prefHeight - minHeight)
//						* amntShrinkHeight / (prefMinSumHeight);

			int width = Math.min(prefWidth,
					transposer.t(child.getMaximumSize()).width);
			if (isStretchMinorAxis()/*TODO if fill*/)
				width = transposer.t(child.getMaximumSize()).width;
			width = Math.max(minWidth, Math.min(clientArea.width, width));
			newBounds.width = width;

			int adjust = clientArea.width - width;
			switch (getMinorAlignment(constraint)) {
			case SWT.TOP:
			case SWT.LEFT:
				adjust = 0;
				break;
			case SWT.CENTER:
				adjust /= 2;
				break;
			case ALIGN_BOTTOMRIGHT:
				break;   
			}
			newBounds.x += adjust;
//			newBounds.height -= amntShrinkCurrentHeight;      
			child.setBounds(transposer.t(newBounds));

//			amntShrinkHeight -= amntShrinkCurrentHeight;
//			prefMinSumHeight -= (prefHeight - minHeight);
			y += newBounds.height + spacing + marginInset.bottom;
		}
	}
	
	private int getMajorAlignment(SapphireSequenceLayoutConstraint constraint) {
		return isHorizontal() ? constraint.horizontalAlignment : constraint.verticalAlignment;
	}
	
	private int getMinorAlignment(SapphireSequenceLayoutConstraint constraint) {
		return isHorizontal() ? constraint.verticalAlignment : constraint.horizontalAlignment;
	}

	/**
	 * Sets children's width (if vertically oriented) or height (if horizontally
	 * oriented) to stretch with their container.
	 * 
	 * @deprecated use {@link #setStretchMinorAxis(boolean)}
	 * @param match
	 *            whether to stretch children
	 * @since 2.0
	 */
	public void setMatchWidth(boolean match) {
		matchWidth = match;
	}

	/**
	 * Sets the amount of space between children.
	 * 
	 * @param space
	 *            the amount of space between children
	 * @since 2.0
	 */
	public void setSpacing(int space) {
		spacing = space;
	}
	
	public void setMargin(int margin) {
		this.margin = margin;
	}

	/**
	 * Overwritten to guarantee backwards compatibility with {@link #matchWidth}
	 * field.
	 * 
	 * @see org.eclipse.draw2d.OrderedLayout#setStretchMinorAxis(boolean)
	 */
	public void setStretchMinorAxis(boolean value) {
		matchWidth = value;
	}

	/**
	 * Sets the orientation of the layout
	 * 
	 * @param flag
	 *            whether the orientation should be vertical
	 * @since 2.0
	 * @deprecated Use {@link #setHorizontal(boolean)} with argument
	 *             <code>false</code> instead.
	 */
	public void setVertical(boolean flag) {
		setHorizontal(!flag);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.draw2d.LayoutManager#getConstraint(org.eclipse.draw2d.IFigure
	 * )
	 */
	public Object getConstraint(IFigure child) {
		return constraints.get(child);
	}

	/**
	 * Sets the layout constraint of the given figure. The constraints can only
	 * be of type {@link GridData}.
	 * 
	 * @see LayoutManager#setConstraint(IFigure, Object)
	 */
	public void setConstraint(IFigure figure, Object newConstraint) {
		super.setConstraint(figure, newConstraint);
		if (newConstraint != null) {
			constraints.put(figure, newConstraint);

		}
	}
}