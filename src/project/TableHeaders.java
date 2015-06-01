package project;
/* (swing1.1beta3)
 *example from 
 http://www.crionics.com/products/opensource/faq/swing_ex/SwingExamples.html 
 *
 */

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class TableHeaders extends JTableHeader {
	private static final long serialVersionUID = 1L;
	
	//private static final String uiClassID = "GroupableTableHeaderUI";
	  protected Vector<ColumnGroup> columnGroups = null;
	    
	  public TableHeaders(TableColumnModel model) {
	    super(model);
	    setUI(new GroupableTableHeaderUI());
	    setReorderingAllowed(false);
	  }
	  public void updateUI(){
	   setUI(new GroupableTableHeaderUI());
	  }
	  
	  public void setReorderingAllowed(boolean b) {
	    reorderingAllowed = false;
	  }
	    
	  public void addColumnGroup(ColumnGroup g) {
	    if (columnGroups == null) {
	      columnGroups = new Vector<ColumnGroup>();
	    }
	    columnGroups.addElement(g);
	  }

	  public Enumeration<ColumnGroup> getColumnGroups(TableColumn col) {
	    if (columnGroups == null) return null;
	    Enumeration<ColumnGroup> e = columnGroups.elements();
	    while (e.hasMoreElements()) {
	      ColumnGroup cGroup = (ColumnGroup)e.nextElement();
	      Vector<ColumnGroup> v_ret = (Vector<ColumnGroup>)cGroup.getColumnGroups(col,new Vector<ColumnGroup>());
	      if (v_ret != null) { 
	  return v_ret.elements();
	      }
	    }
	    return null;
	  }
	  
	  public void setColumnMargin() {
	    if (columnGroups == null) return;
	    int columnMargin = 0;
	    Enumeration<ColumnGroup> e = columnGroups.elements();
	    while (e.hasMoreElements()) {
	      ColumnGroup cGroup = (ColumnGroup)e.nextElement();
	      cGroup.setColumnMargin(columnMargin);
	    }
	  }
	  
	}

class GroupableTableHeaderUI extends BasicTableHeaderUI {
	  
	 public void paint(Graphics g, JComponent c) {
		    Rectangle clipBounds = g.getClipBounds();
		    if (header.getColumnModel() == null) return;
		    ((TableHeaders)header).setColumnMargin();
		    int column = 0;
		    Dimension size = header.getSize();
		    Rectangle cellRect  = new Rectangle(0, 0, size.width, size.height);
		    Hashtable<ColumnGroup, Rectangle> h = new Hashtable<ColumnGroup, Rectangle>();
		    //int columnMargin = header.getColumnModel().getColumnMargin();

		    Enumeration<?> enumeration = header.getColumnModel().getColumns();
		    while (enumeration.hasMoreElements()) {
		      cellRect.height = size.height;
		      cellRect.y      = 0;
		      TableColumn aColumn = (TableColumn)enumeration.nextElement();
		      Enumeration<?> cGroups = ((TableHeaders)header).getColumnGroups(aColumn);
		      if (cGroups != null) {
		        int groupHeight = 0;
		        while (cGroups.hasMoreElements()) {
		          ColumnGroup cGroup = (ColumnGroup)cGroups.nextElement();
		          Rectangle groupRect = (Rectangle)h.get(cGroup);
		          if (groupRect == null) {
		            groupRect = new Rectangle(cellRect);
		            Dimension d = cGroup.getSize(header.getTable());
		            groupRect.width  = d.width;
		            groupRect.height = d.height;
		            h.put(cGroup, groupRect);
		          }
		          paintCell(g, groupRect, cGroup);
		          groupHeight += groupRect.height;
		          cellRect.height = size.height - groupHeight;
		          cellRect.y      = groupHeight;
		        }
		      }
		      cellRect.width = aColumn.getWidth();
		      if (cellRect.intersects(clipBounds)) {
		        paintCell(g, cellRect, column);
		      }
		      cellRect.x += cellRect.width;
		      column++;
		    }
		  }

		  private void paintCell(Graphics g, Rectangle cellRect, int columnIndex) {
		    TableColumn aColumn = header.getColumnModel().getColumn(columnIndex);
		    TableCellRenderer renderer = getRenderer(columnIndex);
		    Component component = renderer.getTableCellRendererComponent(
		      header.getTable(), aColumn.getHeaderValue(),false, false, -1, columnIndex);
		    rendererPane.add(component);
		    rendererPane.paintComponent(g, component, header, cellRect.x, cellRect.y,
						cellRect.width, cellRect.height, true);
		  }

		  private void paintCell(Graphics g, Rectangle cellRect,ColumnGroup cGroup) {
		    TableCellRenderer renderer = cGroup.getHeaderRenderer();
		    Component component = renderer.getTableCellRendererComponent(
		      header.getTable(), cGroup.getHeaderValue(),false, false, -1, -1);
		    rendererPane.add(component);
		    rendererPane.paintComponent(g, component, header, cellRect.x, cellRect.y,
						cellRect.width, cellRect.height, true);
		  }

		  private int getHeaderHeight() {
		    int height = 0;
		    TableColumnModel columnModel = header.getColumnModel();
		    for(int column = 0; column < columnModel.getColumnCount(); column++) {
		      TableColumn aColumn = columnModel.getColumn(column);
		      TableCellRenderer renderer = getRenderer(column);
		      Component comp = renderer.getTableCellRendererComponent(
		        header.getTable(), aColumn.getHeaderValue(), false, false,-1, column);
		      int cHeight = comp.getPreferredSize().height;
		      Enumeration<?> en = ((TableHeaders)header).getColumnGroups(aColumn);
		      if (en != null) {
		        while (en.hasMoreElements()) {
		          ColumnGroup cGroup = (ColumnGroup)en.nextElement();
		          cHeight += cGroup.getSize(header.getTable()).height;
		        }
		      }
		      height = Math.max(height, cHeight);
		    }
		    return height;
		  }

		  private TableCellRenderer getRenderer(int column){
			  TableColumnModel columnModel = header.getColumnModel();
			  TableCellRenderer renderer = null;
		      if(column<0 && column<columnModel.getColumnCount()) {
		    	  renderer = columnModel.getColumn(column).getHeaderRenderer();
		      }
		      if(renderer==null){
		    	  renderer = header.getDefaultRenderer();
		      }
		      return renderer;
		  }

		  private Dimension createHeaderSize(long width) {
		    TableColumnModel columnModel = header.getColumnModel();
		    width += columnModel.getColumnMargin() * columnModel.getColumnCount();
		    if (width > Integer.MAX_VALUE) {
		      width = Integer.MAX_VALUE;
		    }
		    return new Dimension((int)width, getHeaderHeight());
		  }

		  public Dimension getPreferredSize(JComponent c) {
		    long width = 0;
		    Enumeration<?> enumeration = header.getColumnModel().getColumns();
		    while (enumeration.hasMoreElements()) {
		      TableColumn aColumn = (TableColumn)enumeration.nextElement();
		      width = width + aColumn.getPreferredWidth();
		    }
		    return createHeaderSize(width);
		  }

	}
class ColumnGroup {
	  protected TableCellRenderer renderer;
	  protected Vector<Object> v;
	  protected String text;
	  protected int margin=0;

	  public ColumnGroup(String text) {
	    this(null,text);
	  }

	  public ColumnGroup(TableCellRenderer renderer,String text) {
	    if (renderer == null) {
	      this.renderer = new DefaultTableCellRenderer() {
			private static final long serialVersionUID = 1L;

	public Component getTableCellRendererComponent(JTable table, Object value,
	                         boolean isSelected, boolean hasFocus, int row, int column) {
	    JTableHeader header = table.getTableHeader();
	    if (header != null) {
	      setForeground(header.getForeground());
	      setBackground(header.getBackground());
	      setFont(header.getFont());
	    }
	          setHorizontalAlignment(JLabel.CENTER);
	          setText((value == null) ? "" : value.toString());
	    setBorder(UIManager.getBorder("TableHeader.cellBorder"));
	    return this;
	        }
	      };
	    } else {
	      this.renderer = renderer;
	    }
	    this.text = text;
	    v = new Vector<Object>();
	  }

	  
	  /**
	   * @param obj    TableColumn or ColumnGroup
	   */
	  public void add(Object obj) {
	    if (obj == null) { return; }
	    v.addElement(obj);
	  }

	  
	  /**
	   * @param c    TableColumn
	   * @param v    ColumnGroups
	   */
	  public Vector<ColumnGroup> getColumnGroups(TableColumn c, Vector<ColumnGroup> g) {
	    g.addElement(this);
	    if (v.contains(c)) return g;    
	    Enumeration<Object> e = v.elements();
	    while (e.hasMoreElements()) {
	      Object obj = e.nextElement();
	      if (obj instanceof ColumnGroup) {
	        @SuppressWarnings("unchecked")
			Vector<ColumnGroup> groups = 
	          (Vector<ColumnGroup>)((ColumnGroup)obj).getColumnGroups(c,(Vector<ColumnGroup>)g.clone());
	        if (groups != null) return groups;
	      }
	    }
	    return null;
	  }
	    
	  public TableCellRenderer getHeaderRenderer() {
	    return renderer;
	  }
	    
	  public void setHeaderRenderer(TableCellRenderer renderer) {
	    if (renderer != null) {
	      this.renderer = renderer;
	    }
	  }
	    
	  public Object getHeaderValue() {
	    return text;
	  }
	  
	  public Dimension getSize(JTable table) {
	    Component comp = renderer.getTableCellRendererComponent(
	        table, getHeaderValue(), false, false,-1, -1);
	    int height = comp.getPreferredSize().height; 
	    int width  = 0;
	    Enumeration<Object> e = v.elements();
	    while (e.hasMoreElements()) {
	      Object obj = e.nextElement();
	      if (obj instanceof TableColumn) {
	        TableColumn aColumn = (TableColumn)obj;
	        width += aColumn.getWidth();
	        width += margin;
	      } else {
	        width += ((ColumnGroup)obj).getSize(table).width;
	      }
	    }
	    return new Dimension(width, height);
	  }

	  public void setColumnMargin(int margin) {
	    this.margin = margin;
	    Enumeration<Object> e = v.elements();
	    while (e.hasMoreElements()) {
	      Object obj = e.nextElement();
	      if (obj instanceof ColumnGroup) {
	        ((ColumnGroup)obj).setColumnMargin(margin);
	      }
	    }
	  }
	}