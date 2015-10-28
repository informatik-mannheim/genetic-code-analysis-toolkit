/*
 * Copyright [2014] [Mannheim University of Applied Sciences]
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package bio.gcat.gui.helper;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.RGBImageFilter;
import java.beans.PropertyVetoException;
import java.net.URL;
import java.util.StringTokenizer;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.Scrollable;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import bio.gcat.Utilities;

public class Guitilities {
	public static final Border EMPTY_BORDER = new EmptyBorder(0,0,0,0);
	public static final Border MENU_EMPTY_BORDER = new EmptyBorder(3,5,3,25);
	public static final Border TEXT_EMPTY_BORDER = new EmptyBorder(6,30,6,25);
	public static final Border SMALL_EMPTY_BORDER = new EmptyBorder(6,6,6,6);
	public static final Border MEDIUM_EMPTY_BORDER = new EmptyBorder(10,10,10,10);
	public static final Border LARGE_EMPTY_BORDER = new EmptyBorder(18,18,18,18);
	public static final Border HUGE_EMPTY_BORDER = new EmptyBorder(30,30,30,30);
	
	public static final Border CHISEL_BORDER = new ChiselBorder();
	public static final Border TITLE_BORDER = new CompoundBorder(CHISEL_BORDER,new EmptyBorder(6,8,6,0));
	public static final Border FOOTER_BORDER = new CompoundBorder(CHISEL_BORDER,new EmptyBorder(0,8,6,0));
	public static final Border CATEGORY_BORDER = new CompoundBorder(CHISEL_BORDER,new EmptyBorder(0,0,10,0)); 
	
	public static final String TITLE_FOREGROUND = "titleForegroundColor";
	public static final String TITLE_GRADIENT_COLOR_A = "titleGradientColor1";
	public static final String TITLE_GRADIENT_COLOR_B = "titleGradientColor2";
	public static final String TITLE_FONT = "titleFont";
	
	public static final String FOOTER_GRADIENT_COLOR_A = "footerGradientColor1";
	public static final String FOOTER_GRADIENT_COLOR_B = "footerGradientColor2";
	
	public static final String SUB_PANEL_BACKGROUND = "subPanelBackgroundColor";
	
	public static final String FILECHOOSER_READONLY = "FileChooser.readOnly";

	public static final Color TRANSPARENT = new Color(0,0,0,0);
	
	public static GridBagConstraints pairLeftConstraint = new GridBagConstraints(0,0,1,1,0.1d,0d,GridBagConstraints.LINE_END, GridBagConstraints.NONE,new Insets(0,10,0,10),0,0),
	                                pairRightConstraint = new GridBagConstraints(1,0,1,1,0.9d,0d,GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL,new Insets(0,10,0,10),0,0),
	                           pairAdditionalConstraint = new GridBagConstraints(2,0,1,1,0.3d,0d,GridBagConstraints.LINE_START, GridBagConstraints.NONE,new Insets(0,10,0,10),0,0);
	private static final GridBagConstraints PAIR_LEFT_DEFAULT_CONSTRAINT = (GridBagConstraints)pairLeftConstraint.clone(),
	                                       PAIR_RIGHT_DEFAULT_CONSTRAINT = (GridBagConstraints)pairRightConstraint.clone(),
                                    PAIR_ADDITIONAL_DEFAULT_CONSTRAINT = (GridBagConstraints)pairAdditionalConstraint.clone();

	private static boolean usingNimbusLookAndFeel = false;
	
	public enum Size { REGULAR,MINI,SMALL,LARGE;
		public String toString() { return super.toString().toLowerCase(); };
	}
	public enum Direction { HORRIZONTAL,VERTICAL }
	
	public static void setComponentSize(JComponent component,Size size) {
		component.putClientProperty("JComponent.sizeVariant",size.toString());
	}
	
	public static void initializeUIManager() {
		Color color = UIManager.getColor(Guitilities.usingNimbusLookAndFeel()? "nimbusBase" : "activeCaption"); 
    if(color==null)
    	color = UIManager.getColor("control");
    float hsb[] = Color.RGBtoHSB(color.getRed(),color.getGreen(),color.getBlue(),null);
    UIManager.put(TITLE_GRADIENT_COLOR_A,Color.getHSBColor(hsb[0]-.013f,.15f,.85f));
    UIManager.put(TITLE_GRADIENT_COLOR_B,Color.getHSBColor(hsb[0]-.005f,.24f,.80f));
    UIManager.put(TITLE_FOREGROUND,Color.getHSBColor(hsb[0],.54f,.40f));
    
    color = UIManager.getColor(Guitilities.usingNimbusLookAndFeel()? "nimbusSelectionBackground" : "inactiveCaption");
    if(color==null)
    	color = UIManager.getColor("control");
    UIManager.put(FOOTER_GRADIENT_COLOR_A,Color.getHSBColor(hsb[0]-.013f,.10f,.90f));
    UIManager.put(FOOTER_GRADIENT_COLOR_B,Color.getHSBColor(hsb[0]-.005f,.14f,.85f));
    
    UIManager.put(SUB_PANEL_BACKGROUND,Guitilities.deriveColorHSB(UIManager.getColor("Panel.background"),0,0,-.15f));
    
    Font font = UIManager.getFont("Label.font");
    UIManager.put(TITLE_FONT,font.deriveFont(Font.BOLD,font.getSize()+4f));
    
    UIManager.put(FILECHOOSER_READONLY,Boolean.TRUE);
	}
	
	public static boolean usingNimbusLookAndFeel() { return usingNimbusLookAndFeel; }
	public static boolean setLookAndFeel(String classname,String name) {
		if((classname==null||classname.isEmpty())&&(name==null||name.isEmpty()))
			return false;
		try {
			UIManager.setLookAndFeel(classname);
			usingNimbusLookAndFeel = UIManager.getLookAndFeel().getName().equals("Nimbus");
			return true;
		}	catch(Exception e_a) {
			try {
				for(LookAndFeelInfo look_and_feel:UIManager.getInstalledLookAndFeels())
					if(name.equals(look_and_feel.getName())) {
		        UIManager.setLookAndFeel(look_and_feel.getClassName());
		        usingNimbusLookAndFeel = UIManager.getLookAndFeel().getName().equals("Nimbus");
		        return true;
		      }
			} catch(Exception e_b) {}
		}
		return false;
	}
	
	public static void invokeAppropriate(Runnable runnable) {
		if(!SwingUtilities.isEventDispatchThread())
			SwingUtilities.invokeLater(runnable);
		else runnable.run();
	}

	public static Point addPoints(Point... points) {
		Point result = new Point();
		for(Point point:points)
			if(point!=null)
				result.translate(point.x,point.y);
		return result;
	}
	public static Point invertPoint(Point point) {
		return new Point(-point.x,-point.y);
	}
	public static class RelativeMouseEvent extends MouseEvent {
		private static final long serialVersionUID = 1l;
		public RelativeMouseEvent(MouseEvent event,Point point,Point relative) {
			super(event.getComponent(),event.getID(),event.getWhen(),event.getModifiers(),
				point.x-relative.x,point.y-relative.y,point.x,point.y,
				event.getClickCount(),event.isPopupTrigger(),event.getButton());
		}
	}
	
	public static Image makeColorTransparent(Image image,final Color color) {
		ImageFilter filter = new RGBImageFilter() {
			public int markerRGB = color.getRGB()|0xFF000000;
			public final int filterRGB(int x,int y,int rgb) {
				if((rgb|0xFF000000)==markerRGB)
					return 0x00FFFFFF & rgb;
				else return rgb;
			}
		};
		return Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(image.getSource(),filter));
	}
	
	private static GraphicsConfiguration getDefaultGraphicsConfiguration() { return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration(); }
	public static Dimension getStringBounds(Font font,String text) { return font.getStringBounds(text,new FontRenderContext(getDefaultGraphicsConfiguration().getDefaultTransform(),true,false)).getBounds().getSize(); }
	public static LineMetrics getLineMetrics(Font font,String text) { return font.getLineMetrics(text,new FontRenderContext(getDefaultGraphicsConfiguration().getDefaultTransform(),true,false)); }
	public static BufferedImage createCompatibleImage(int width,int height) { return getDefaultGraphicsConfiguration().createCompatibleImage(width,height); }
  public static BufferedImage createTranslucentImage(int width,int height) { return getDefaultGraphicsConfiguration().createCompatibleImage(width,height,Transparency.TRANSLUCENT); }
  public static BufferedImage createWhiteImage(int width,int height) {
  	BufferedImage image = createCompatibleImage(width,height);
  	Graphics graphics = image.getGraphics();
  	graphics.setColor(Color.WHITE);
  	graphics.fillRect(0,0,width,height);
  	return image;
  }
  public static BufferedImage createGradientImage(int width,int height,Color gradientA,Color gradientB) {
		BufferedImage image = createCompatibleImage(width,height);
		GradientPaint gradient = new GradientPaint(0,0,gradientA,0,height,gradientB,false);
		Graphics2D graphics = (Graphics2D) image.getGraphics();
		graphics.setPaint(gradient);
		graphics.fillRect(0,0,width,height);
		graphics.dispose();	
		return image;
  }
	
	public static JLabel createLabel(String text) {
  	JLabel label = new JLabel();
  	label.setFocusable(false);
  	label.setText(new StringBuilder("<html>").append(text.replaceAll("\n","<br>")).append("</html>").toString());
  	return label;
  }
	public static void setFittingLabelSize(JLabel label,int width) {
		int wordWidth,lines=1,size=0;
		if(width<=0||label.getText().isEmpty())
			return;
		StringTokenizer words = new StringTokenizer(label.getText());
		FontMetrics metrics = label.getFontMetrics(label.getFont());
		while(words.hasMoreTokens())
			if((size+=(wordWidth=metrics.stringWidth(words.nextToken())))>width) {
				size = wordWidth;
				lines++;
			}
		label.setPreferredSize(new Dimension(width,lines*metrics.getHeight()));
		label.revalidate();
	}
	
	public static JMenuItem getMenuItem(JMenuBar menubar, String action) {
		JMenuItem item = null;
		for(int index=0;index<menubar.getMenuCount();index++)
			if((item=getMenuItem(menubar.getMenu(index), action))!=null)
				return item;
		return null;
	}
	public static JMenuItem getMenuItem(JMenu menu, String action) {
		JMenuItem item;
		for(int position=0;position<menu.getItemCount();position++)
			if((item=menu.getItem(position))!=null&&action.equals(item.getActionCommand()))
				return item;
		return null;
	}
	
	public static JButton getToolbarButton(JToolBar[] toolbars, String action) {
		JButton button;
		for(JToolBar toolbar:toolbars)
			if((button=getToolbarButton(toolbar,action))!=null)
				return button;
		return null;
	}
	public static JButton getToolbarButton(JToolBar toolbar, String action) {
		JButton button;
		for(Component component:toolbar.getComponents())
			if(component instanceof JButton&&action.equals((button=(JButton)component).getActionCommand()))
				return button;
		return null;
	}
	
	public static JMenuItem createMenuItem(String text,String actionCommand,ActionListener listener) { return createMenuItem(text,(String)null,actionCommand,listener); }
	public static JMenuItem createMenuItem(String text,KeyStroke keyStroke,String actionCommand,ActionListener listener) { return createMenuItem(text,null,keyStroke,actionCommand,listener); }
	public static JMenuItem createMenuItem(String text,String icon,String actionCommand,ActionListener listener) { return createMenuItem(text,icon,null,actionCommand,listener); }
	public static JMenuItem createMenuItem(String text,String icon,KeyStroke keyStroke,String actionCommand,ActionListener listener) {
		JMenuItem item = new JMenuItem(text);
		item.setActionCommand(actionCommand);
		if(keyStroke!=null) {
			item.setMnemonic(keyStroke.getKeyChar());
			item.setAccelerator(keyStroke);
		}
		item.setIcon(getImage(icon!=null?icon:"dummy"));
		item.addActionListener(listener);
		JComponent component = (listener instanceof JComponent)?(JComponent)listener:(listener instanceof JFrame)?((JFrame)listener).getRootPane():null;
		if(component!=null)
			registerKeyStroke(component,keyStroke,actionCommand,listener);
		return item;
	}
	
	public static JMenu createSubmenu(String text) { return createSubmenu(text,null); }
	public static JMenu createSubmenu(String text,String icon) {
		JMenu menu = new JMenu(text);
		menu.setIcon(getImage(icon!=null?icon:"dummy"));
		return menu;
	}
	
	public static JMenuItem seperateMenuItem(JMenuItem item) {
		Insets insets = item.getBorder().getBorderInsets(null); insets.top = 8;
		item.setBorder(new EmptyBorder(insets));
		return item;
	}
	
	public static JComponent createMenuText(String text) {
		JLabel label = new JLabel(text);
		label.setBorder(TEXT_EMPTY_BORDER);
		label.setEnabled(false);
		return label;
	}
	
	public static void fireKeyStroke(JMenuBar menubar, KeyStroke keyStroke) {
		for(int menu=0;menu<menubar.getMenuCount();menu++)
			for(Component component:menubar.getMenu(menu).getMenuComponents())
				if(component instanceof JMenuItem)
					for(KeyStroke compareKeyStroke:((JMenuItem)component).getRegisteredKeyStrokes())
						if(keyStroke.equals(compareKeyStroke))
							for(ActionListener listener:((JMenuItem)component).getActionListeners())
								listener.actionPerformed(new ActionEvent(keyStroke, 0, ((JMenuItem)component).getActionCommand()));
	}
	public static void registerKeyStroke(JComponent component, KeyStroke keyStroke, final String actionCommand, final ActionListener listener) {
		if(keyStroke==null||actionCommand==null)
			return;
		component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(keyStroke, actionCommand);
		component.getActionMap().put(actionCommand, new AbstractAction() {
			private static final long serialVersionUID = 1L;
			@Override public void actionPerformed(ActionEvent event) {
				listener.actionPerformed(new ActionEvent(event.getSource(),event.getID(),actionCommand));
			}
		});
	}
	
  public static Color deriveColorAlpha(Color color,int alpha) { return new Color(color.getRed(),color.getGreen(),color.getBlue(),alpha); }  
  public static Color deriveColorHSB(Color color,float dH,float dS,float dB) {
      float hsb[] = Color.RGBtoHSB(color.getRed(),color.getGreen(),color.getBlue(),null);
      hsb[0] += dH; hsb[1] += dS; hsb[2] += dB;
      return Color.getHSBColor(hsb[0] < 0 ? 0 : (hsb[0] > 1 ? 1 : hsb[0]),
                               hsb[1] < 0 ? 0 : (hsb[1] > 1 ? 1 : hsb[1]),
                               hsb[2] < 0 ? 0 : (hsb[2] > 1 ? 1 : hsb[2]));
  }

  public static Point getAbsoluteLocation(Component component) {
  	if(component==null||component instanceof Frame)
  		return new Point();
  	return addPoints(component.getLocation(),getAbsoluteLocation(component.getParent()));
  }
  public static Dimension getActualSize(Frame frame) {
  	try {
  		int extendedState = frame.getExtendedState();
  		java.awt.Rectangle bounds = frame.getMaximizedBounds(),systemBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
  		return new Dimension((extendedState&Frame.MAXIMIZED_HORIZ)==Frame.MAXIMIZED_HORIZ?(bounds!=null&&bounds.width !=Integer.MAX_VALUE?bounds.width :systemBounds.width ):frame.getWidth(),
  				                 (extendedState&Frame.MAXIMIZED_VERT) ==Frame.MAXIMIZED_VERT ?(bounds!=null&&bounds.height!=Integer.MAX_VALUE?bounds.height:systemBounds.height):frame.getHeight());
  	} catch(HeadlessException e) { return frame.getSize(); }
  }
  
  public static JScrollPane createScrollPane(Component component) { return createScrollPane(component,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); }
  public static JScrollPane createScrollPane(Component component,int vsbPolicy,int hsbPolicy) { return new JScrollPane(component,vsbPolicy,hsbPolicy); }
  public static JScrollPane createScrollPane(Component component,int vsbPolicy,int hsbPolicy,int vsbUnitIncrement,int hsbUnitIncrement) {
  	JScrollPane scroll = createScrollPane(component,vsbPolicy,hsbPolicy);
  	scroll.getVerticalScrollBar().setUnitIncrement(vsbUnitIncrement);
  	scroll.getHorizontalScrollBar().setUnitIncrement(hsbUnitIncrement);
  	return scroll;
  }
  
  public static JScrollPane createExtendedScrollPane(Component component) { return createExtendedScrollPane(component,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); }
  public static JScrollPane createExtendedScrollPane(Component component,int vsbPolicy,int hsbPolicy) {
  	JScrollPane scroll = new JScrollPane(vsbPolicy,hsbPolicy);
  	final JViewport viewport = new JViewport() {
			private static final long serialVersionUID = 1l;
			@Override	public Dimension getViewSize() {
        Component component = getView();
        if(component==null)
        	return new Dimension(0,0);
        if(isViewSizeSet)
        	return component.getSize();
        else if(component instanceof Scrollable)
        	return ((Scrollable) component).getPreferredScrollableViewportSize();
        else return component.getPreferredSize();
			}
		};
		viewport.addChangeListener(new ChangeListener() {
			private Dimension size;
			@Override	public void stateChanged(ChangeEvent event) {
				Dimension size = viewport.getViewSize();
				if(this.size!=null&&!this.size.equals(size)) {
					viewport.setViewPosition(new Point());
					this.size = size;
				} else this.size = size;				
			}
		});
  	scroll.setViewport(viewport);
  	scroll.setViewportView(component);
  	return scroll;
  }
  
  public static String showPasswordDialog(Component component,String text) { return showPasswordDialog(component,text,null); }
  public static String showPasswordDialog(Component component,String text,String title) {
  	JPasswordField field = new JPasswordField(); field.setEchoChar('\u25CF');
  	JPanel panel = new JPanel();
  	panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
  	panel.add(createLabel(text));
  	panel.add(field);
  	if(JOptionPane.showConfirmDialog(component,panel,title,JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION)
  		return new String(field.getPassword());
  	else return null;
	}

  public static GroupLayout setGroupLayout(Container container) {
		GroupLayout layout = new GroupLayout(container);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		container.setLayout(layout);
		return layout;
  }
  public static <T extends Container> T setBoxLayout(T container, int align) {
		BoxLayout layout = new BoxLayout(container, align);
		container.setLayout(layout);
		return container;
  }
  
	public static ImageIcon getImage(String name) { return getImage(name,null); }
	public static ImageIcon getImage(String name, ImageIcon fallback) {
		URL resource = Utilities.getResource(name=name+".png");
		if(resource==null) {
			if(fallback==null) {
				System.err.println(String.format("Image %s not found!", name));
				return null;
			} else return fallback;
		} else return new ImageIcon(resource); 
	}
	
	
	public static class BoundaryPoints {
		public Point minimum,maximum;
		public Dimension dimension;
		public BoundaryPoints(Point point,Dimension dimension) {
			minimum = new Point(point); maximum = new Point(point);
			maximum.translate(dimension.width,dimension.height);
			this.dimension = dimension;
		}
		public void checkBoundary(Point point,Dimension dimension) {
			minimum.x = Math.min(minimum.x,point.x);
			minimum.y = Math.min(minimum.y,point.y);
			maximum.x = Math.max(maximum.x,point.x+dimension.width);
			maximum.y = Math.max(maximum.y,point.y+dimension.height);
			this.dimension.setSize(maximum.x-minimum.x,maximum.y-minimum.y);
		}
	}
	
	/*public static class Rectangle {
		public Point point_ul,point_lr;
		public Dimension size;
		public Rectangle(Point2D point_a,Point2D point_b) { this(new Point((int)point_a.getX(),(int)point_a.getY()),
				                                                     new Point((int)point_b.getX(),(int)point_b.getY())); }
		public Rectangle(Point point,Dimension dimension) { this(point,new Point(point.x+dimension.width,point.y+dimension.height)); }
		public Rectangle(Point point_a,Point point_b) {
			this.point_ul = new Point(point_a);
			this.point_lr = new Point(point_b);
			if(point_a.x>point_b.x) {
				int x = point_b.x;
				this.point_lr.x = point_a.x;
				this.point_ul.x = x;
			}
			if(point_a.y>point_b.y) {
				int y = point_b.y;
				this.point_lr.y = point_a.y;
				this.point_ul.y = y;
			}
			this.size = new Dimension(this.point_lr.x-this.point_ul.x,
					                      this.point_lr.y-this.point_ul.y);
		}
		public boolean contains(Point point) { return contains(point,new Dimension()); }
		public boolean contains(Point point,Dimension dimension) {
  		return point_ul.x<=point.x&& point.x+dimension.width <=point_lr.x
  	      && point_ul.y<=point.y&& point.y+dimension.height<=point_lr.y;
		}
		public long getArea() { return size.width*size.height; }
		
		public java.awt.Rectangle toRectangle() { return new java.awt.Rectangle(point_ul,size);	}
		public Area toArea() { return new Area(toRectangle()); }
	}*/
	
  private static class ChiselBorder implements Border {
  	private Insets insets = new Insets(1, 0, 1, 0);
  	public Insets getBorderInsets(java.awt.Component component) { return insets; }
    public boolean isBorderOpaque() { return true; }
    public void paintBorder(java.awt.Component component, Graphics g, int x, int y, int width, int height) {
			Color color = component.getBackground();
			g.setColor(Guitilities.deriveColorHSB(color, 0, 0, .2f));
			g.drawLine(x, y, x + width, y);
			g.setColor(Guitilities.deriveColorHSB(color, 0, 0, -.2f));
			g.drawLine(x, y + height - 1, x + width, y + height - 1);
    }
  }

	public static TitledBorder createTitledBorder(String title) {
		return BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),title);
	}
  
  public static JSeparator createSeparator() {
  	JSeparator separator = new JSeparator();
  	separator.setPreferredSize(new Dimension(separator.getPreferredSize().width,6));
  	return separator;
  }
	public static JSplitPane createSplitPane(int orientation, boolean continuous, boolean expandable, double loactionAndResizeWeight, Component componentA, Component componentB) {
		JSplitPane split = createSplitPane(orientation,continuous,expandable,componentA,componentB);
		split.setDividerLocation(loactionAndResizeWeight);
		split.setResizeWeight(loactionAndResizeWeight);
		return split;
	}
	public static JSplitPane createSplitPane(int orientation, boolean continuous, boolean expandable, int location, double resizeWeight, Component componentA, Component componentB) {
		JSplitPane split = createSplitPane(orientation,continuous,expandable,componentA,componentB);
		split.setDividerLocation(location);
		split.setResizeWeight(resizeWeight);
		return split;
	}
	public static JSplitPane createSplitPane(int orientation, boolean continuous, boolean expandable, Component componentA, Component componentB) {
		JSplitPane split = new JSplitPane(orientation,continuous,componentA,componentB);
		split.setOneTouchExpandable(expandable);
		return split;
	}
	
	public static JButton createToolbarButton(String text, String icon, String action, ActionListener listener) {
		JButton button = new JButton(getImage(icon));
		button.setToolTipText(text);
		button.setBorderPainted(false);
		button.addMouseListener(new MouseAdapter() {
			@Override public void mouseEntered(MouseEvent e) { button.setBorderPainted(true); }
			@Override public void mouseExited(MouseEvent e) { button.setBorderPainted(false); }
		});
		button.setActionCommand(action);
		button.addActionListener(listener);		
		return button;
	}
	public static JButton createToolbarMenuButton(String text, String icon, JMenuItem[] items) {
		JButton button = new JButton("\u25BE", getImage(icon));
		button.setToolTipText(text);
		JPopupMenu menu = new JPopupMenu();
		for(JMenuItem item:items) menu.add(item);
		button.setBorderPainted(false);
		button.addMouseListener(new MouseAdapter() {
			@Override public void mouseEntered(MouseEvent e) { if(!menu.isShowing()) button.setBorderPainted(true); }
			@Override public void mouseExited(MouseEvent e) { button.setBorderPainted(false); }
		});
		button.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent event) {
				button.setBorderPainted(false);
				menu.show(button.getParent(), button.getX(), button.getY()+button.getHeight()); }
		});
		return button;
	}
	
	public static <Type extends Component> Type addGridPairLine(Container panel,int gridy,Component left,Type right) { return addGridPairLine(panel,gridy,left,right,null); }
	public static <Type extends Component> Type addGridPairLine(Container panel,int gridy,Component left,Type right,Component additional) {
		pairLeftConstraint.gridy = pairRightConstraint.gridy = pairAdditionalConstraint.gridy = gridy;
		if(additional!=null)
			pairRightConstraint.weightx = 0.6d;
		
		panel.add(left,pairLeftConstraint);
		panel.add(right,pairRightConstraint);
		if(additional!=null)
			panel.add(additional,pairAdditionalConstraint);
		
		if(!pairLeftConstraint.equals(PAIR_LEFT_DEFAULT_CONSTRAINT))
			pairLeftConstraint = (GridBagConstraints)PAIR_LEFT_DEFAULT_CONSTRAINT.clone();
		if(!pairRightConstraint.equals(PAIR_RIGHT_DEFAULT_CONSTRAINT))
			pairRightConstraint = (GridBagConstraints)PAIR_RIGHT_DEFAULT_CONSTRAINT.clone();
		if(!pairAdditionalConstraint.equals(PAIR_ADDITIONAL_DEFAULT_CONSTRAINT))
			pairAdditionalConstraint = (GridBagConstraints)PAIR_ADDITIONAL_DEFAULT_CONSTRAINT.clone();
		
		return right;
	}

	public static JButton createButton(String text,ActionListener listener) {
		JButton button = new JButton(text);
		button.addActionListener(listener);
		return button;
	}
	public static ButtonGroup createButtonGroup(AbstractButton... buttons) {
		ButtonGroup group = new ButtonGroup();
		for(AbstractButton button:buttons)
			group.add(button);
		return group;
	}
	
	public static int centerText(Direction direction,Graphics graphics,Dimension size,String text) { return centerText(direction,graphics,new java.awt.Rectangle(size),text); }
	public static int centerText(Direction direction,Graphics graphics,java.awt.Rectangle bounds,String text) {
		FontMetrics metrics = graphics.getFontMetrics();
		switch(direction) {
		case HORRIZONTAL: return bounds.x+(int)(bounds.width/2-metrics.stringWidth(text)/2);
		case VERTICAL: return bounds.y+(int)((bounds.height-metrics.getStringBounds(text,graphics).getHeight())/2+metrics.getAscent()); 
		default: return 0; }
	}

	public static void selectFrame(final JInternalFrame frame) {
		SwingUtilities.invokeLater(new Runnable(){
			@Override	public void run() {
				frame.requestFocusInWindow();
				try { frame.setSelected(true); }	catch (PropertyVetoException e) {}
				frame.toFront();
			}
		});
	}	
	
  public static Window getWindowForComponent(Component component) {
  	if(component==null) return null;
  	else if(component instanceof Frame||component instanceof Dialog)
         return (Window)component;
  	else return getWindowForComponent(component.getParent());
  }
  
  public static JDialog createDialog(Component parent, String title) throws HeadlessException {
	  final JDialog dialog;
	
	  Window window = getWindowForComponent(parent);
	  if(window instanceof Dialog)
	       dialog = new JDialog((Dialog)window, title, true);
	  else dialog = new JDialog((Frame)window, title, true);
	  
	  dialog.setLocationRelativeTo(parent);
	  dialog.setResizable(false);
	  dialog.pack();
	          
	  return dialog;
	}
  
	public static void makeGrid(Container parent,
			int rows,int cols,
			int initialX,int initialY,
			int xPad,int yPad) {
		SpringLayout layout;
		try {
			layout = (SpringLayout)parent.getLayout();
		}
		catch(ClassCastException exc) {
			System.err.println("The first argument to makeGrid must use SpringLayout.");
			return;
		}

		Spring xPadSpring = Spring.constant(xPad);
		Spring yPadSpring = Spring.constant(yPad);
		Spring initialXSpring = Spring.constant(initialX);
		Spring initialYSpring = Spring.constant(initialY);
		int max = rows*cols;

		// Calculate Springs that are the max of the width/height so that all
		// cells have the same size.
		Spring maxWidthSpring = layout.getConstraints(parent.getComponent(0)).
				getWidth();
		Spring maxHeightSpring = layout.getConstraints(parent.getComponent(0)).
				getHeight();
		for(int i = 1;i<max;i++) {
			SpringLayout.Constraints cons = layout.getConstraints(
					parent.getComponent(i));

			maxWidthSpring = Spring.max(maxWidthSpring,cons.getWidth());
			maxHeightSpring = Spring.max(maxHeightSpring,cons.getHeight());
		}

		// Apply the new width/height Spring. This forces all the
		// components to have the same size.
		for(int i = 0;i<max;i++) {
			SpringLayout.Constraints cons = layout.getConstraints(
					parent.getComponent(i));

			cons.setWidth(maxWidthSpring);
			cons.setHeight(maxHeightSpring);
		}

		// Then adjust the x/y constraints of all the cells so that they
		// are aligned in a grid.
		SpringLayout.Constraints lastCons = null;
		SpringLayout.Constraints lastRowCons = null;
		for(int i = 0;i<max;i++) {
			SpringLayout.Constraints cons = layout.getConstraints(
					parent.getComponent(i));
			if(i%cols==0) { // start of new row
				lastRowCons = lastCons;
				cons.setX(initialXSpring);
			} else { // x position depends on previous component
				cons.setX(Spring.sum(lastCons.getConstraint(SpringLayout.EAST),
						xPadSpring));
			}

			if(i/cols==0) { // first row
				cons.setY(initialYSpring);
			} else { // y position depends on previous row
				cons.setY(Spring.sum(lastRowCons.getConstraint(SpringLayout.SOUTH),
						yPadSpring));
			}
			lastCons = cons;
		}

		// Set the parent's size.
		SpringLayout.Constraints pCons = layout.getConstraints(parent);
		pCons.setConstraint(SpringLayout.SOUTH,
				Spring.sum(
						Spring.constant(yPad),
						lastCons.getConstraint(SpringLayout.SOUTH)));
		pCons.setConstraint(SpringLayout.EAST,
				Spring.sum(
						Spring.constant(xPad),
						lastCons.getConstraint(SpringLayout.EAST)));
	}

	/* Used by makeCompactGrid. */
	private static SpringLayout.Constraints getConstraintsForCell(
			int row,int col,
			Container parent,
			int cols) {
		SpringLayout layout = (SpringLayout)parent.getLayout();
		Component c = parent.getComponent(row*cols+col);
		return layout.getConstraints(c);
	}

	public static void makeCompactGrid(Container parent,
			int rows,int cols,
			int initialX,int initialY,
			int xPad,int yPad) {
		SpringLayout layout;
		try {
			layout = (SpringLayout)parent.getLayout();
		}
		catch(ClassCastException exc) {
			System.err.println("The first argument to makeCompactGrid must use SpringLayout.");
			return;
		}

		// Align all cells in each column and make them the same width.
		Spring x = Spring.constant(initialX);
		for(int c = 0;c<cols;c++) {
			Spring width = Spring.constant(0);
			for(int r = 0;r<rows;r++) {
				width = Spring.max(width,
						getConstraintsForCell(r,c,parent,cols).
								getWidth());
			}
			for(int r = 0;r<rows;r++) {
				SpringLayout.Constraints constraints =
						getConstraintsForCell(r,c,parent,cols);
				constraints.setX(x);
				constraints.setWidth(width);
			}
			x = Spring.sum(x,Spring.sum(width,Spring.constant(xPad)));
		}

		// Align all cells in each row and make them the same height.
		Spring y = Spring.constant(initialY);
		for(int r = 0;r<rows;r++) {
			Spring height = Spring.constant(0);
			for(int c = 0;c<cols;c++) {
				height = Spring.max(height,
						getConstraintsForCell(r,c,parent,cols).
								getHeight());
			}
			for(int c = 0;c<cols;c++) {
				SpringLayout.Constraints constraints =
						getConstraintsForCell(r,c,parent,cols);
				constraints.setY(y);
				constraints.setHeight(height);
			}
			y = Spring.sum(y,Spring.sum(height,Spring.constant(yPad)));
		}

		// Set the parent's size.
		SpringLayout.Constraints pCons = layout.getConstraints(parent);
		pCons.setConstraint(SpringLayout.SOUTH,y);
		pCons.setConstraint(SpringLayout.EAST,x);
	}
	
	public static void prepareLookAndFeel() {
		UIManager.put("FileChooser.readOnly", Boolean.TRUE);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}	catch(Exception e) { e.printStackTrace(); };
	}
}