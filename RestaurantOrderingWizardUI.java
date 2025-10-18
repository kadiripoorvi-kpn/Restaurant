import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.Arrays;

public class RestaurantOrderingWizardUI {

    static class FoodItem {
        String name;
        double price;
        String imagePath;
        public FoodItem(String name, double price, String imagePath) {
            this.name = name; this.price = price; this.imagePath = imagePath;
        }
    }

    static class OrderItem {
        FoodItem food;
        int qty;
        public OrderItem(FoodItem f, int qty) { this.food = f; this.qty = qty; }
        public double lineTotal() { return food.price * qty; }
    }

    private JFrame mainFrame;
    private JPanel menuPanel;
    private Map<String, OrderItem> currentOrder = new LinkedHashMap<>();
    private List<FoodItem> menuList;
    private JTextField customerNameField;
    private JLabel totalLabel;
    private DefaultTableModel orderTableModel;
    private static int tokenCounter = 1;
    private DecimalFormat moneyFmt = new DecimalFormat("₹0.00");

    // Variables to carry to next blocks
    private String customerName;
    private String orderId;
    private int currentToken;
    private double subtotal, taxAmount, totalWithTax;

    public RestaurantOrderingWizardUI() {
        showWelcomeScreen(); // --- show intro first

        menuList = Arrays.asList(
                new FoodItem("Pramodh's Cheese Burger", 119, "C:\\Users\\kpoor\\Downloads\\cheese burger.jpg"),
                new FoodItem("Bhuvan's Pizza", 249, "C:\\Users\\kpoor\\Downloads\\pizza.jpg"),
                new FoodItem("Chandu's French Fries", 79, "C:\\Users\\kpoor\\Downloads\\french fries.jpg"),
                new FoodItem("Manu's Prime Soda", 69, "C:\\Users\\kpoor\\Downloads\\soda.jpg"),
                new FoodItem("Poorvinath's Sandwich", 139, "C:\\Users\\kpoor\\Downloads\\Sandwich.jpg"),
                new FoodItem("Gowtham's Veg Wrap", 109, "C:\\Users\\kpoor\\Downloads\\veg wrap.jpg"),
                new FoodItem("KGVPP's Hot Dog", 169, "C:\\Users\\kpoor\\Downloads\\hot dog.jpg"),
                new FoodItem("Rishit's Water", 19, "C:\\Users\\kpoor\\Downloads\\water.jpg")
        );
        buildMainMenu();
    }

    // ------------------ WELCOME BLOCK -------------------
    private void showWelcomeScreen() {
        JDialog welcome = new JDialog((Frame) null, "Welcome", true);
        welcome.setSize(600, 400);
        welcome.setLocationRelativeTo(null);
        welcome.setLayout(new BorderLayout());

        // Gradient panel background
        JPanel gradientPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, Color.decode("#FF7E5F"), 0, getHeight(), Color.decode("#FEB47B"));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        gradientPanel.setLayout(new BorderLayout());

        // Title
        JLabel title = new JLabel("CID FOOD PLAZA", SwingConstants.CENTER);
        title.setFont(new Font("Arial Black", Font.BOLD, 30));
        title.setForeground(Color.WHITE);
        title.setBorder(new EmptyBorder(40, 10, 10, 10));
        gradientPanel.add(title, BorderLayout.NORTH);

        // Subtitle
        JLabel tagline = new JLabel("Delicious. Fresh. Fast. 😍", SwingConstants.CENTER);
        tagline.setFont(new Font("SansSerif", Font.ITALIC, 20));
        tagline.setForeground(Color.WHITE);
        gradientPanel.add(tagline, BorderLayout.CENTER);

        // Start Button
        JButton startBtn = createRoundedButton("Start Ordering →", new Color(34, 139, 34));
        startBtn.setPreferredSize(new Dimension(220, 55));
        startBtn.addActionListener(e -> welcome.dispose());
        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.add(startBtn);
        gradientPanel.add(btnPanel, BorderLayout.SOUTH);

        welcome.add(gradientPanel);
        welcome.setVisible(true);
    }


    // ------------------ MAIN MENU -------------------
    private void buildMainMenu() {
        mainFrame = new JFrame("🍔 FOOD PLAZA");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1000, 650);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setLayout(new BorderLayout(12,12));

        mainFrame.getContentPane().setBackground(Color.decode("#F8F8F8"));

        JLabel title = new JLabel("CID FOOD PLAZA", SwingConstants.CENTER);
        title.setFont(new Font("Arial Black", Font.ITALIC, 24));
        title.setForeground(Color.decode("#FF4500"));
        title.setBorder(new EmptyBorder(10,10,10,10));
        mainFrame.add(title, BorderLayout.NORTH);

        JPanel main = new JPanel(new GridLayout(1,2,12,12));
        main.setBorder(new EmptyBorder(10,10,10,10));
        mainFrame.add(main, BorderLayout.CENTER);

        // LEFT: menu panel
        menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(Color.WHITE);
        menuPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.ORANGE), "Menu Items"));
        JScrollPane menuScroll = new JScrollPane(menuPanel);
        main.add(menuScroll);

        for (FoodItem f : menuList) menuPanel.add(createMenuItemRow(f));

        // RIGHT: order summary panel
        JPanel orderPanel = new JPanel(new BorderLayout(8,8));
        orderPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLUE), "Order Summary"));
        orderPanel.setBackground(Color.decode("#FFF8F0"));
        main.add(orderPanel);

        // Customer details (Name + Phone)
        JPanel customerPanel = new JPanel();
        customerPanel.setLayout(new GridLayout(2, 2, 10, 10));
        customerPanel.setBackground(orderPanel.getBackground());

// Name
        customerPanel.add(new JLabel("Customer Name: "));
        customerNameField = new JTextField();
        customerNameField.setFont(new Font("SansSerif", Font.PLAIN, 18)); // larger font
        customerNameField.setPreferredSize(new Dimension(250, 35));       // bigger box
        customerPanel.add(customerNameField);

// Phone
        customerPanel.add(new JLabel("Phone Number: "));
        phoneField = new JTextField();
        phoneField.setFont(new Font("SansSerif", Font.PLAIN, 18));
        phoneField.setPreferredSize(new Dimension(250, 35));
        phoneField.setBorder(BorderFactory.createTitledBorder("Phone Number"));
        customerPanel.add(phoneField);


        orderPanel.add(customerPanel, BorderLayout.NORTH);


        String[] cols = {"Item", "Qty", "Price"};
        orderTableModel = new DefaultTableModel(cols,0) {
            public boolean isCellEditable(int r, int c){ return false; }
        };
        JTable orderTable = new JTable(orderTableModel);
        orderTable.setFillsViewportHeight(true);
        orderTable.setRowHeight(26);
        orderTable.setBackground(Color.decode("#FFF8DC"));
        orderTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        orderTable.getTableHeader().setBackground(Color.ORANGE);
        orderTable.getTableHeader().setForeground(Color.WHITE);
        JScrollPane tableScroll = new JScrollPane(orderTable);
        orderPanel.add(tableScroll, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(8,8));
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        totalPanel.setBackground(orderPanel.getBackground());
        totalLabel = new JLabel("Total: " + moneyFmt.format(0));
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        totalLabel.setForeground(Color.BLUE);
        totalPanel.add(totalLabel);
        bottomPanel.add(totalPanel, BorderLayout.WEST);

        JButton nextBtn = createRoundedButton("Next →", Color.decode("#32CD32"));
        nextBtn.setPreferredSize(new Dimension(180,40));
        nextBtn.addActionListener(e -> proceedToOrderSummary());
        bottomPanel.add(nextBtn,BorderLayout.EAST);

        orderPanel.add(bottomPanel, BorderLayout.SOUTH);

        mainFrame.setVisible(true);
    }

    private JPanel createMenuItemRow(FoodItem f){
        JPanel row = new JPanel(new GridBagLayout());
        row.setBorder(new EmptyBorder(8,8,8,8));
        row.setBackground(Color.WHITE);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(0,6,0,6);
        c.anchor = GridBagConstraints.WEST;

        ImageIcon icon = new ImageIcon(f.imagePath);
        JLabel imgLabel = new JLabel(new ImageIcon(
                icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH)
        ));
        imgLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY,1));
        c.gridx=0; row.add(imgLabel,c);

        JLabel nameLbl = new JLabel(f.name);
        nameLbl.setPreferredSize(new Dimension(200,22));
        c.gridx=1; row.add(nameLbl,c);

        JLabel priceLbl = new JLabel(moneyFmt.format(f.price));
        priceLbl.setPreferredSize(new Dimension(80,22));
        c.gridx=2; row.add(priceLbl,c);

        JButton minus = createRoundedButton("-", Color.RED);
        minus.setPreferredSize(new Dimension(50,28));

        JLabel qtyLbl = new JLabel("0", SwingConstants.CENTER);
        qtyLbl.setPreferredSize(new Dimension(36,26));

        JButton plus = createRoundedButton("+", new Color(50,205,50));
        plus.setPreferredSize(new Dimension(50,28));

        c.gridx=3; row.add(minus,c);
        c.gridx=4; row.add(qtyLbl,c);
        c.gridx=5; row.add(plus,c);

        plus.addActionListener(e -> {
            int q = Integer.parseInt(qtyLbl.getText()) + 1;
            qtyLbl.setText("" + q);
            updateOrder(f,q);
        });
        minus.addActionListener(e -> {
            int q = Integer.parseInt(qtyLbl.getText());
            if(q > 0) q--;
            qtyLbl.setText("" + q);
            updateOrder(f,q);
        });

        return row;
    }

    private void updateOrder(FoodItem f, int qty){
        if(qty<=0) currentOrder.remove(f.name); else currentOrder.put(f.name,new OrderItem(f,qty));
        refreshOrderTable();
    }

    private void refreshOrderTable(){
        orderTableModel.setRowCount(0);
        subtotal = 0;
        for(OrderItem oi: currentOrder.values()){
            orderTableModel.addRow(new Object[]{oi.food.name,oi.qty,moneyFmt.format(oi.lineTotal())});
            subtotal+=oi.lineTotal();
        }

        taxAmount=subtotal*0.05;
        totalWithTax=subtotal+taxAmount;

        orderTableModel.addRow(new Object[]{"Tax (5%)","",moneyFmt.format(taxAmount)});
        totalLabel.setText("Total (incl. Tax): "+moneyFmt.format(totalWithTax));
    }

    // ------------------ BLOCK 2: Order Summary Confirmation -------------------
    private void proceedToOrderSummary(){
        customerName = customerNameField.getText().trim();
        if(customerName.isEmpty()){
            JOptionPane.showMessageDialog(mainFrame,"Please enter customer name before proceeding.","Missing Name",JOptionPane.WARNING_MESSAGE);
            return;
        }
        if(currentOrder.isEmpty()){
            JOptionPane.showMessageDialog(mainFrame,"Cart is empty. Add items before proceeding.","Empty Cart",JOptionPane.WARNING_MESSAGE);
            return;
        }

        orderId="ORD"+System.currentTimeMillis();
        currentToken=tokenCounter++;

        StringBuilder sb=new StringBuilder();
        for(OrderItem oi:currentOrder.values()){
            sb.append(oi.food.name).append(" x").append(oi.qty).append(" -> ").append(moneyFmt.format(oi.lineTotal())).append("\n");
        }
        sb.append("\nSubtotal: ").append(moneyFmt.format(subtotal))
                .append("\nTax (5%): ").append(moneyFmt.format(taxAmount))
                .append("\nTotal: ").append(moneyFmt.format(totalWithTax))
                .append("\nCustomer: ").append(customerName)
                .append("\nOrder ID: ").append(orderId)
                .append("\nToken: ").append(currentToken);

        JDialog orderDlg = new JDialog(mainFrame,"Confirm Order",true);
        orderDlg.setSize(450,500); orderDlg.setLocationRelativeTo(mainFrame);
        orderDlg.setLayout(new BorderLayout(8,8));

        JTextArea details=new JTextArea(sb.toString());
        details.setEditable(false); details.setBackground(orderDlg.getBackground());
        orderDlg.add(details,BorderLayout.CENTER);

        JButton placeOrderBtn=createRoundedButton("Place Order →", new Color(50,205,50));
        placeOrderBtn.addActionListener(e->{ orderDlg.dispose(); showQRPayment(); });
        orderDlg.add(placeOrderBtn,BorderLayout.SOUTH);

        orderDlg.setVisible(true);
    }

    // ------------------ BLOCK 3: QR Payment -------------------
    private void showQRPayment(){
        JDialog qrDlg=new JDialog(mainFrame,"Scan to Pay",true);
        qrDlg.setSize(450,550); qrDlg.setLocationRelativeTo(mainFrame);
        qrDlg.setLayout(new BorderLayout(10,10));

        JLabel top=new JLabel("Scan to Pay",SwingConstants.CENTER);
        top.setFont(new Font("SansSerif",Font.BOLD,18));
        top.setBorder(new EmptyBorder(10,10,0,10));
        qrDlg.add(top,BorderLayout.NORTH);

        QRPanel qr=new QRPanel(12,Objects.hash(currentOrder.hashCode(),System.currentTimeMillis()));
        qr.setPreferredSize(new Dimension(300,300));
        JPanel qrWrap=new JPanel(); qrWrap.add(qr); qrDlg.add(qrWrap,BorderLayout.CENTER);

        JTextArea details=new JTextArea(buildOrderSummaryText());
        details.setEditable(false); details.setBackground(qrDlg.getBackground());
        qrDlg.add(details,BorderLayout.SOUTH);

        JButton doneBtn=createRoundedButton("Confirm Payment →", new Color(50,205,50));
        doneBtn.addActionListener(e->{ qrDlg.dispose(); showPaymentSuccess(); });
        qrDlg.add(doneBtn,BorderLayout.EAST);

        qrDlg.setVisible(true);
    }

    // ------------------ BLOCK 4: Payment Successful -------------------
    private void showPaymentSuccess(){
        JOptionPane.showMessageDialog(mainFrame,"Payment Successful!\nToken Number: "+currentToken,"Success",JOptionPane.INFORMATION_MESSAGE);
        currentOrder.clear();
        resetMenuQuantities();
        refreshOrderTable();
        showThankYouScreen(); // 👈 call end block
    }


    private String buildOrderSummaryText(){
        StringBuilder sb=new StringBuilder();
        for(OrderItem oi:currentOrder.values()){
            sb.append(oi.food.name).append(" x").append(oi.qty).append(" -> ").append(moneyFmt.format(oi.lineTotal())).append("\n");
        }
        sb.append("\nSubtotal: ").append(moneyFmt.format(subtotal))
                .append("\nTax (5%): ").append(moneyFmt.format(taxAmount))
                .append("\nTotal: ").append(moneyFmt.format(totalWithTax))
                .append("\nCustomer: ").append(customerName)
                .append("\nOrder ID: ").append(orderId)
                .append("\nToken: ").append(currentToken);
        return sb.toString();
    }

    private void resetMenuQuantities(){
        Component[] rows=menuPanel.getComponents();
        for(Component comp:rows){
            if(comp instanceof JPanel){
                JPanel row=(JPanel) comp;
                for(Component c:row.getComponents()){
                    if(c instanceof JLabel){
                        JLabel lbl=(JLabel)c;
                        if(lbl.getPreferredSize().width==36 && lbl.getText().matches("\\d+")) lbl.setText("0");
                    }
                }
            }
        }
    }

    // ------------------ QR Panel -------------------
    static class QRPanel extends JPanel{
        private final int cells; private final long seed;
        public QRPanel(int cells,long seed){ this.cells=Math.max(8,cells); this.seed=seed; setPreferredSize(new Dimension(300,300)); setBackground(Color.WHITE);}
        @Override
        protected void paintComponent(Graphics g){
            super.paintComponent(g);
            int w=getWidth(),h=getHeight(),cellSize=Math.min(w,h)/cells,gridW=cellSize*cells,offsetX=(w-gridW)/2,offsetY=(h-gridW)/2;
            Random rnd=new Random(seed^0x9E3779B97F4A7C15L);
            g.setColor(Color.WHITE); g.fillRect(0,0,w,h);
            g.setColor(Color.BLACK); g.drawRect(offsetX-1,offsetY-1,gridW+1,gridW+1);
            for(int r=0;r<cells;r++){for(int c=0;c<cells;c++){
                boolean isFinder=(r<2&&c<2)||(r<2&&c>cells-3)||(r>cells-3&&c<2);
                boolean black=isFinder || rnd.nextDouble()<0.45;
                if(black) g.fillRect(offsetX+c*cellSize,offsetY+r*cellSize,cellSize,cellSize);
            }}
            g.setColor(Color.DARK_GRAY); g.setFont(new Font("SansSerif",Font.PLAIN,12));
            g.drawString("Simulated QR — demo only", offsetX, offsetY+gridW+18);
        }
    }

    // ------------------ Rounded Button Factory -------------------
    private JButton createRoundedButton(String text, Color bgColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                FontMetrics fm = g2.getFontMetrics();
                Rectangle rect = new Rectangle(0, 0, getWidth(), getHeight());
                int x = (rect.width - fm.stringWidth(getText())) / 2;
                int y = (rect.height - fm.getHeight()) / 2 + fm.getAscent();
                g2.setColor(Color.WHITE);
                g2.setFont(getFont().deriveFont(Font.BOLD, 14));
                g2.drawString(getText(), x, y);

                g2.dispose();
            }
            @Override
            protected void paintBorder(Graphics g) {}
        };
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        return btn;
    }
    // ------------------ BLOCK 5: Thank You Screen -------------------
    private JTextField phoneField; // class-level variable
    private void showThankYouScreen() {
        JDialog thankDlg = new JDialog(mainFrame, "Thank You", true);
        thankDlg.setSize(500, 350);
        thankDlg.setLocationRelativeTo(mainFrame);
        thankDlg.setLayout(new BorderLayout());

        JPanel gradientPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, Color.decode("#6A11CB"), 0, getHeight(), Color.decode("#2575FC"));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        gradientPanel.setLayout(new BorderLayout());

        JLabel msg = new JLabel("Thank You, " + customerName + "!", SwingConstants.CENTER);
        msg.setFont(new Font("Arial Black", Font.BOLD, 24));
        msg.setForeground(Color.WHITE);
        msg.setBorder(new EmptyBorder(40, 10, 10, 10));
        gradientPanel.add(msg, BorderLayout.NORTH);

        JLabel subMsg = new JLabel("We hope to serve you again ", SwingConstants.CENTER);
        subMsg.setFont(new Font("SansSerif", Font.PLAIN, 20));
        subMsg.setForeground(Color.WHITE);
        gradientPanel.add(subMsg, BorderLayout.CENTER);

        if (phoneField != null && !phoneField.getText().trim().isEmpty()) {
            JLabel phoneMsg = new JLabel("📞 Contact saved: " + phoneField.getText().trim(), SwingConstants.CENTER);
            phoneMsg.setFont(new Font("SansSerif", Font.ITALIC, 16));
            phoneMsg.setForeground(Color.YELLOW);
            gradientPanel.add(phoneMsg, BorderLayout.SOUTH);
        }

        thankDlg.add(gradientPanel);
        thankDlg.setVisible(true);
    }



    public static void main(String[] args){ SwingUtilities.invokeLater(RestaurantOrderingWizardUI::new);}
}



